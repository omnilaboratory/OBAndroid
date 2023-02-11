package com.omni.wallet.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.UriUtil;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;
import routerrpc.RouterOuterClass;

/**
 * 汉: 收红包的弹窗
 * En: ReceiveRedPacketDialog
 * author: guoyalei
 * date: 2023/2/7
 */
public class ReceiveLuckyPacketDialog {
    private static final String TAG = ReceiveLuckyPacketDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    long mAssetId;
    long payAmount;
    String lnInvoice;
    long feeSats = 0;
    private static final int PAYMENT_HASH_BYTE_LENGTH = 32;
    LightningOuterClass.Route route;
    String paymentHash;
    LoadingDialog mLoadingDialog;

    public ReceiveLuckyPacketDialog(Context context) {
        this.mContext = context;
    }

    public void show(String address, long assetId, String invoiceAddr) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_receive_lucky_packet)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        mLoadingDialog = new LoadingDialog(mContext);
        mAssetId = assetId;
        showStepDecodePay(invoiceAddr);
        /**
         * @备注： 关闭按钮
         * @description: Click close button
         */
        mAlertDialog.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    private void showStepDecodePay(String invoiceAddr) {
        String data = invoiceAddr;
        // Avoid index out of bounds. An Request with less than 11 characters isn't valid.
        if (data.length() < 11) {
            ToastUtils.showToast(mContext, "Length is greater than 11 characters");
            return;
        }
        // convert to lower case
        lnInvoice = data.toLowerCase();
        // Remove the "lightning:" uri scheme if it is present, LND needs it without uri scheme
        lnInvoice = UriUtil.removeURI(lnInvoice);
        LightningOuterClass.PayReqString decodePaymentRequest = LightningOuterClass.PayReqString.newBuilder()
                .setPayReq(lnInvoice)
                .build();
        Obdmobile.decodePayReq(decodePaymentRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mContext, e.getMessage());
                    }
                });
                LogUtils.e(TAG, "------------------decodePaymentOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.PayReq resp = LightningOuterClass.PayReq.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + resp);
                    if (resp == null) {
                        ToastUtils.showToast(mContext, "Probe send request was null");
                        return;
                    }
                    RouterOuterClass.SendPaymentRequest probeRequest;
                    if (mAssetId == 0) {
                        probeRequest = prepareBtcPaymentProbe(resp);
                    } else {
                        probeRequest = preparePaymentProbe(resp);
                    }
                    Obdmobile.routerOB_SendPaymentV2(probeRequest.toByteArray(), new RecvStream() {
                        @Override
                        public void onError(Exception e) {
                            if (e.getMessage().equals("EOF")) {
                                return;
                            }
                            LogUtils.e(TAG, "-------------routerSendPaymentV2OnError-----------" + e.getMessage());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(mContext, e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.Payment payment = LightningOuterClass.Payment.parseFrom(bytes);
                                LogUtils.e(TAG, "-------------routerSendPaymentV2OnResponse-----------" + payment.toString());
                                switch (payment.getFailureReason()) {
                                    case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                route = payment.getHtlcs(0).getRoute();
                                                paymentHash = payment.getPaymentHash();
                                                if (mAssetId == 0) {
                                                    payAmount = resp.getAmtMsat();
                                                } else {
                                                    payAmount = resp.getAmount();
                                                }
                                                feeSats = payAmount / 10000;
                                                showStepPay();
                                                deletePaymentProbe(payment.getPaymentHash());
                                            }
                                        });
                                        break;
                                    case FAILURE_REASON_NO_ROUTE:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                paymentHash = payment.getPaymentHash();
                                                if (mAssetId == 0) {
                                                    payAmount = resp.getAmtMsat();
                                                } else {
                                                    payAmount = resp.getAmount();
                                                }
                                                feeSats = payAmount / 10000;
                                                showStepPay();
                                                deletePaymentProbe(payment.getPaymentHash());
                                            }
                                        });
                                        break;
                                    default:
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtils.showToast(mContext, payment.getFailureReason().toString());
                                                deletePaymentProbe(payment.getPaymentHash());
                                            }
                                        });
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showStepPay() {
        mLoadingDialog.show();
        if (route != null) {
            RouterOuterClass.SendToRouteRequest sendToRouteRequest = RouterOuterClass.SendToRouteRequest.newBuilder()
                    .setPaymentHash(byteStringFromHex(paymentHash))
                    .setRoute(route)
                    .build();
            Obdmobile.routerSendToRouteV2(sendToRouteRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.dismiss();
                            mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                            mAlertDialog.findViewById(R.id.lv_lucky_packet_failed).setVisibility(View.VISIBLE);
                            mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                            showStepFailed(e.getMessage());
                        }
                    });
                    LogUtils.e(TAG, "Exception while executing SendToRoute.");
                    LogUtils.e(TAG, e.getMessage());
                }

                @Override
                public void onResponse(byte[] bytes) {
                    try {
                        LightningOuterClass.HTLCAttempt htlcAttempt = LightningOuterClass.HTLCAttempt.parseFrom(bytes);
                        switch (htlcAttempt.getStatus()) {
                            case SUCCEEDED:
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLoadingDialog.dismiss();
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                        showStepSuccess(String.valueOf(htlcAttempt.getAttemptTimeNs()).substring(0, 10));
                                    }
                                });
                                break;
                            case FAILED:
                                switch (htlcAttempt.getFailure().getCode()) {
                                    case INCORRECT_OR_UNKNOWN_PAYMENT_DETAILS:
                                        RouterOuterClass.SendPaymentRequest sendPaymentRequest = RouterOuterClass.SendPaymentRequest.newBuilder()
                                                .setAssetId((int) mAssetId)
                                                .setPaymentRequest(lnInvoice)
                                                .setFeeLimitMsat(calculateAbsoluteFeeLimit(payAmount))
                                                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                                                .setMaxParts(1)
                                                .build();
                                        Obdmobile.routerOB_SendPaymentV2(sendPaymentRequest.toByteArray(), new RecvStream() {
                                            @Override
                                            public void onError(Exception e) {
                                                if (e.getMessage().equals("EOF")) {
                                                    return;
                                                }
                                                LogUtils.e(TAG, "------------------routerOB_SendPaymentV2OnError------------------" + e.getMessage());
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mLoadingDialog.dismiss();
                                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_failed).setVisibility(View.VISIBLE);
                                                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                        showStepFailed(e.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(byte[] bytes) {
                                                if (bytes == null) {
                                                    return;
                                                }
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            LightningOuterClass.Payment resp = LightningOuterClass.Payment.parseFrom(bytes);
                                                            LogUtils.e(TAG, "------------------routerOB_SendPaymentV2OnResponse-----------------" + resp);
                                                            if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.SUCCEEDED) {
                                                                mLoadingDialog.dismiss();
                                                                mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                                                mAlertDialog.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                                                mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                showStepSuccess(String.valueOf(resp.getCreationDate()));
                                                            } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                                                mLoadingDialog.dismiss();
                                                                mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                                                mAlertDialog.findViewById(R.id.lv_lucky_packet_failed).setVisibility(View.VISIBLE);
                                                                mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                String errorMessage;
                                                                switch (resp.getFailureReason()) {
                                                                    case FAILURE_REASON_TIMEOUT:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                                                        showStepFailed(errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_NO_ROUTE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                                                        showStepFailed(errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                                                        showStepFailed(errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                                                        showStepFailed(errorMessage);
                                                                        break;
                                                                }
                                                            }
                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        break;
                                }
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            RouterOuterClass.SendPaymentRequest sendPaymentRequest = RouterOuterClass.SendPaymentRequest.newBuilder()
                    .setAssetId((int) mAssetId)
                    .setPaymentRequest(lnInvoice)
                    .setFeeLimitMsat(calculateAbsoluteFeeLimit(payAmount))
                    .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                    .setMaxParts(RefConstants.LN_MAX_PARTS)
                    .build();
            Obdmobile.routerOB_SendPaymentV2(sendPaymentRequest.toByteArray(), new RecvStream() {
                @Override
                public void onError(Exception e) {
                    if (e.getMessage().equals("EOF")) {
                        return;
                    }
                    LogUtils.e(TAG, "------------------noRouterOB_SendPaymentV2OnError------------------" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.dismiss();
                            mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                            mAlertDialog.findViewById(R.id.lv_lucky_packet_failed).setVisibility(View.VISIBLE);
                            mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                            showStepFailed(e.getMessage());
                        }
                    });
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if (bytes == null) {
                        return;
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LightningOuterClass.Payment resp = LightningOuterClass.Payment.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------noRouterOB_SendPaymentV2OnResponse-----------------" + resp);
                                if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.SUCCEEDED) {
                                    mLoadingDialog.dismiss();
                                    mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                    mAlertDialog.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                    mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                    showStepSuccess(String.valueOf(resp.getCreationDate()));
                                } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                    mLoadingDialog.dismiss();
                                    mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                    mAlertDialog.findViewById(R.id.lv_lucky_packet_failed).setVisibility(View.VISIBLE);
                                    mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                    String errorMessage;
                                    switch (resp.getFailureReason()) {
                                        case FAILURE_REASON_TIMEOUT:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                            showStepFailed(errorMessage);
                                            break;
                                        case FAILURE_REASON_NO_ROUTE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                            showStepFailed(errorMessage);
                                            break;
                                        case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                            showStepFailed(errorMessage);
                                            break;
                                        case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                            showStepFailed(errorMessage);
                                            break;
                                    }
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    private void showStepSuccess(String timestamp) {
        ImageView assetTypeIv = mAlertDialog.findViewById(R.id.iv_asset_type_success);
        TextView assetTypeTv = mAlertDialog.findViewById(R.id.tv_asset_type_success);
        TextView amountPayTv = mAlertDialog.findViewById(R.id.tv_amount_success);
        TextView payTimeTv = mAlertDialog.findViewById(R.id.tv_time_success);
        TextView payDateTv = mAlertDialog.findViewById(R.id.tv_date_success);
        DecimalFormat df = new DecimalFormat("0.00######");
        if (mAssetId == 0) {
            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeTv.setText("BTC");
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000));
        } else {
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000));
        }
        payTimeTv.setText(DateUtils.Hourmin(timestamp));
        payDateTv.setText(DateUtils.MonthDay(timestamp));
    }

    private void showStepFailed(String message) {
        TextView failedMessageTv = mAlertDialog.findViewById(R.id.tv_failed_message);
        failedMessageTv.setText(message);
        /**
         * @备注： 扫描按钮
         * @description: Click scan button
         */
        mAlertDialog.findViewById(R.id.layout_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
    }

    public RouterOuterClass.SendPaymentRequest preparePaymentProbe(LightningOuterClass.PayReq paymentRequest) {
        return preparePaymentProbe(paymentRequest.getDestination(), paymentRequest.getAmount(), paymentRequest.getPaymentAddr(), paymentRequest.getRouteHintsList(), paymentRequest.getFeaturesMap());
    }

    public RouterOuterClass.SendPaymentRequest preparePaymentProbe(String destination, long amountSat, @Nullable ByteString paymentAddress, @Nullable List<LightningOuterClass.RouteHint> routeHints, @Nullable Map<Integer, LightningOuterClass.Feature> destFeatures) {
        // The paymentHash will be replaced with a random hash. This way we can create a fake payment.
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[PAYMENT_HASH_BYTE_LENGTH];
        random.nextBytes(bytes);
        long feeLimit = calculateAbsoluteFeeLimit(amountSat);
        RouterOuterClass.SendPaymentRequest.Builder sprb = RouterOuterClass.SendPaymentRequest.newBuilder()
                .setAssetId((int) mAssetId)
                .setDest(byteStringFromHex(destination))
                .setAssetAmt(amountSat)
                .setFeeLimitMsat(feeLimit)
                .setPaymentHash(ByteString.copyFrom(bytes))
                .setNoInflightUpdates(true)
                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                .setMaxParts(1); // We are looking for a direct path. Probing using MPP isn’t really possible at the moment.
        if (paymentAddress != null) {
            sprb.setPaymentAddr(paymentAddress);
        }
        if (destFeatures != null && !destFeatures.isEmpty()) {
            for (Map.Entry<Integer, LightningOuterClass.Feature> entry : destFeatures.entrySet()) {
                sprb.addDestFeaturesValue(entry.getKey());
            }
        }
        if (routeHints != null && !routeHints.isEmpty()) {
            sprb.addAllRouteHints(routeHints);
        }

        return sprb.build();
    }

    public RouterOuterClass.SendPaymentRequest prepareBtcPaymentProbe(LightningOuterClass.PayReq paymentRequest) {
        return prepareBtcPaymentProbe(paymentRequest.getDestination(), paymentRequest.getAmtMsat(), paymentRequest.getPaymentAddr(), paymentRequest.getRouteHintsList(), paymentRequest.getFeaturesMap());
    }

    public RouterOuterClass.SendPaymentRequest prepareBtcPaymentProbe(String destination, long amountSat, @Nullable ByteString paymentAddress, @Nullable List<LightningOuterClass.RouteHint> routeHints, @Nullable Map<Integer, LightningOuterClass.Feature> destFeatures) {
        // The paymentHash will be replaced with a random hash. This way we can create a fake payment.
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[PAYMENT_HASH_BYTE_LENGTH];
        random.nextBytes(bytes);
        long feeLimit = calculateAbsoluteFeeLimit(amountSat);
        RouterOuterClass.SendPaymentRequest.Builder sprb = RouterOuterClass.SendPaymentRequest.newBuilder()
                .setAssetId((int) mAssetId)
                .setDest(byteStringFromHex(destination))
                .setAmtMsat(amountSat)
                .setFeeLimitMsat(feeLimit)
                .setPaymentHash(ByteString.copyFrom(bytes))
                .setNoInflightUpdates(true)
                .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                .setMaxParts(1); // We are looking for a direct path. Probing using MPP isn’t really possible at the moment.
        if (paymentAddress != null) {
            sprb.setPaymentAddr(paymentAddress);
        }
        if (destFeatures != null && !destFeatures.isEmpty()) {
            for (Map.Entry<Integer, LightningOuterClass.Feature> entry : destFeatures.entrySet()) {
                sprb.addDestFeaturesValue(entry.getKey());
            }
        }
        if (routeHints != null && !routeHints.isEmpty()) {
            sprb.addAllRouteHints(routeHints);
        }

        return sprb.build();
    }

    /**
     * Used to delete a payment probe. We don't need these stored in the database. They just bloat it.
     */
    public static void deletePaymentProbe(String paymentHash) {
        LightningOuterClass.DeletePaymentRequest deletePaymentRequest = LightningOuterClass.DeletePaymentRequest.newBuilder()
                .setPaymentHash(byteStringFromHex(paymentHash))
                .setFailedHtlcsOnly(false)
                .build();
        Obdmobile.deletePayment(deletePaymentRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "Exception while deleting payment probe.");
                LogUtils.e(TAG, e.getMessage());

            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "Payment probe deleted.");
            }
        });
    }

    // ByteString values when using for example "paymentRequest.getDescriptionBytes()" can for some reason not directly be used as they are double in length
    private static ByteString byteStringFromHex(String hexString) {
        byte[] hexBytes = BaseEncoding.base16().decode(hexString.toUpperCase());
        return ByteString.copyFrom(hexBytes);
    }

    public static void sendToRoute(String paymentHash, LightningOuterClass.Route route) {

    }

    public static long calculateAbsoluteFeeLimit(long amountSatToSend) {
        long absFee;
        if (amountSatToSend <= RefConstants.LN_PAYMENT_FEE_THRESHOLD) {
            absFee = (long) (Math.sqrt(amountSatToSend));
        } else {
            absFee = (long) (getRelativeSettingsFeeLimit() * amountSatToSend);
        }
        return Math.max(absFee, 3L);
    }

    public static float getRelativeSettingsFeeLimit() {
        String lightning_feeLimit = "3%";
        String feePercent = lightning_feeLimit.replace("%", "");
        float feeMultiplier = 1f;
        if (!feePercent.equals("None")) {
            feeMultiplier = Integer.parseInt(feePercent) / 100f;
        }
        return feeMultiplier;
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
