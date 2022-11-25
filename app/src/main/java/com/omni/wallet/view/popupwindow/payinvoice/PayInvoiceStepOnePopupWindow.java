package com.omni.wallet.view.popupwindow.payinvoice;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.UriUtil;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;
import routerrpc.RouterOuterClass;

/**
 * PayInvoiceStepOne的弹窗
 */
public class PayInvoiceStepOnePopupWindow {
    private static final String TAG = PayInvoiceStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    PayInvoiceStepTwoPopupWindow mPayInvoiceStepTwoPopupWindow;
    String mAddress;
    long mAssetId;
    String toNodeAddress;
    long payAmount;
    String lnInvoice;
    long feeSats = 0;
    private static final int PAYMENT_HASH_BYTE_LENGTH = 32;
    LightningOuterClass.Route route;
    String paymentHash;

    public PayInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String address, long assetId) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_pay_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            mAddress = address;
            mAssetId = assetId;
            showStepOne(rootView);

            /**
             * @备注： 点击cancel 按钮
             * @description: Click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });

            /**
             * @备注： close 按钮
             * @description: Click close button
             */
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    private void showStepOne(View rootView) {
        TextView fromNodeAddressTv = rootView.findViewById(R.id.tv_from_node_address);
        TextView fromNodeNameTv = rootView.findViewById(R.id.tv_from_node_name);
        EditText invoiceEdit = rootView.findViewById(R.id.edit_invoice);
        fromNodeAddressTv.setText(StringUtils.encodePubkey(mAddress));
        /**
         * @备注： 点击back关闭pay invoice 窗口
         * @description: Close pay invoice popup window when click back
         */
        rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasePopWindow.dismiss();
            }
        });
        /**
         * @备注： 点击next显示invoice step two
         * @description: Show pay invoice step two when click next
         */
        rootView.findViewById(R.id.layout_next_to_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = invoiceEdit.getText().toString();
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
                            RouterOuterClass.SendPaymentRequest probeRequest = preparePaymentProbe(resp);
                            Obdmobile.routerSendPaymentV2(probeRequest.toByteArray(), new RecvStream() {
                                @Override
                                public void onError(Exception e) {
                                    if (e.getMessage().equals("EOF")) {
                                        return;
                                    }
                                    LogUtils.e(TAG, "-------------routerSendPaymentV20nError-----------" + e.getMessage());
                                    ToastUtils.showToast(mContext, e.getMessage());
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

                                                        if (route.getTotalFeesMsat() % 1000 == 0) {
                                                            feeSats = route.getTotalFeesMsat() / 1000;
                                                        } else {
                                                            feeSats = (route.getTotalFeesMsat() / 1000) + 1;
                                                        }
                                                        paymentHash = payment.getPaymentHash();
                                                        toNodeAddress = resp.getDestination();
                                                        payAmount = resp.getAmount();
                                                        rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                                                        rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                                                        showStepTwo(rootView);
                                                        deletePaymentProbe(payment.getPaymentHash());
                                                    }
                                                });
                                                break;
                                            case FAILURE_REASON_NO_ROUTE:
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
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
        });
    }

    private void showStepTwo(View rootView) {
        TextView fromNodeAddress1Tv = rootView.findViewById(R.id.tv_from_node_address_1);
        TextView fromNodeName1Tv = rootView.findViewById(R.id.tv_from_node_name_1);
        TextView toNodeAddress1Tv = rootView.findViewById(R.id.tv_to_node_address_1);
        TextView toNodeName1Tv = rootView.findViewById(R.id.tv_to_node_name_1);
        ImageView amountLogoTv = rootView.findViewById(R.id.iv_amount_logo);
        TextView amountPayTv = rootView.findViewById(R.id.tv_amount_pay);
        TextView amountPayExchangeTv = rootView.findViewById(R.id.tv_amount_pay_exchange);
        TextView amountPayFeeTv = rootView.findViewById(R.id.tv_amount_pay_fee);
        if (mAssetId == 0) {
            amountLogoTv.setImageResource(R.mipmap.icon_btc_logo_small);
        } else {
            amountLogoTv.setImageResource(R.mipmap.icon_usdt_logo_small);
        }
        fromNodeAddress1Tv.setText(StringUtils.encodePubkey(mAddress));
        toNodeAddress1Tv.setText(StringUtils.encodePubkey(toNodeAddress));
        amountPayTv.setText(payAmount + "");
        amountPayExchangeTv.setText(payAmount + "");
        amountPayFeeTv.setText(feeSats + "");
        /**
         * @备注： 点击back显示invoice step one
         * @description: Show pay invoice step one when click next
         */
        rootView.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.VISIBLE);
            }
        });
        /**
         * @备注： 点击pay执行支付操作，根据支付结果跳转到succeed或者failed步骤
         * @description: Click pay to execute the payment operation, and jump to the successful or failed step according to the payment result
         */
        rootView.findViewById(R.id.layout_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterOuterClass.SendToRouteRequest sendToRouteRequest = RouterOuterClass.SendToRouteRequest.newBuilder()
                        .setPaymentHash(byteStringFromHex(paymentHash))
                        .setRoute(route)
                        .build();
                Obdmobile.routerSendToRouteV2(sendToRouteRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "Exception while executing SendToRoute.");
                        LogUtils.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        try {
                            LightningOuterClass.HTLCAttempt htlcAttempt = LightningOuterClass.HTLCAttempt.parseFrom(bytes);
                            switch (htlcAttempt.getStatus()) {
                                case SUCCEEDED:
                                    // updated the history, so it is shown the next time the user views it
                                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                    showStepSuccess(rootView);
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
                                            Obdmobile.routerSendPaymentV2(sendPaymentRequest.toByteArray(), new RecvStream() {
                                                @Override
                                                public void onError(Exception e) {
                                                    if (e.getMessage().equals("EOF")) {
                                                        return;
                                                    }
                                                    LogUtils.e(TAG, "------------------sendPaymentOnError------------------" + e.getMessage());
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                            rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                                            rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                            rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                            showStepFailed(rootView, e.getMessage());
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
                                                                LogUtils.e(TAG, "------------------sendPaymentOnResponse-----------------" + resp);
                                                                if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.SUCCEEDED) {
                                                                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                                    rootView.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                                                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                    showStepSuccess(rootView);
                                                                } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                                                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                                    rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                                                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                    String errorMessage;
                                                                    switch (resp.getFailureReason()) {
                                                                        case FAILURE_REASON_TIMEOUT:
                                                                            errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                                                            showStepFailed(rootView, errorMessage);
                                                                            break;
                                                                        case FAILURE_REASON_NO_ROUTE:
                                                                            errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                                                            showStepFailed(rootView, errorMessage);
                                                                            break;
                                                                        case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                                                            errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                                                            showStepFailed(rootView, errorMessage);
                                                                            break;
                                                                        case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                                                            errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                                                            showStepFailed(rootView, errorMessage);
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
            }
        });
    }

    private void showStepSuccess(View rootView) {
        TextView fromNodeAddress2Tv = rootView.findViewById(R.id.tv_from_node_address_2);
        TextView fromNodeName2Tv = rootView.findViewById(R.id.tv_from_node_name_2);
        TextView toNodeAddress2Tv = rootView.findViewById(R.id.tv_to_node_address_2);
        TextView toNodeName2Tv = rootView.findViewById(R.id.tv_to_node_name_2);
        ImageView amountLogo1Tv = rootView.findViewById(R.id.iv_amount_logo_1);
        TextView amountPay1Tv = rootView.findViewById(R.id.tv_amount_pay_1);
        TextView amountPayExchange1Tv = rootView.findViewById(R.id.tv_amount_pay_exchange_1);
        TextView amountPayFee1Tv = rootView.findViewById(R.id.tv_amount_pay_fee_1);
        if (mAssetId == 0) {
            amountLogo1Tv.setImageResource(R.mipmap.icon_btc_logo_small);
        } else {
            amountLogo1Tv.setImageResource(R.mipmap.icon_usdt_logo_small);
        }
        fromNodeAddress2Tv.setText(StringUtils.encodePubkey(mAddress));
        toNodeAddress2Tv.setText(StringUtils.encodePubkey(toNodeAddress));
        amountPay1Tv.setText(payAmount + "");
        amountPayExchange1Tv.setText(payAmount + "");
        amountPayFee1Tv.setText(feeSats + "");
    }

    private void showStepFailed(View rootView, String message) {
        TextView fromNodeAddress3Tv = rootView.findViewById(R.id.tv_from_node_address_3);
        TextView toNodeAddress3Tv = rootView.findViewById(R.id.tv_to_node_address_3);
        ImageView amountLogo2Tv = rootView.findViewById(R.id.iv_amount_logo_2);
        TextView amountUnitTv = rootView.findViewById(R.id.tv_amount_unit);
        TextView amountPay2Tv = rootView.findViewById(R.id.tv_amount_pay_2);
        TextView amountUnit1Tv = rootView.findViewById(R.id.tv_amount_unit_1);
        TextView payTimeTv = rootView.findViewById(R.id.tv_pay_time);
        TextView payTimeUnitTv = rootView.findViewById(R.id.tv_pay_time_unit);
        TextView failedMessageTv = rootView.findViewById(R.id.tv_failed_message);
        if (mAssetId == 0) {
            amountLogo2Tv.setImageResource(R.mipmap.icon_btc_logo_small);
            amountUnitTv.setText("BTC");
            amountUnit1Tv.setText("BTC");
        } else {
            amountLogo2Tv.setImageResource(R.mipmap.icon_usdt_logo_small);
            amountUnitTv.setText("USDT");
            amountUnit1Tv.setText("USDT");
        }
        toNodeAddress3Tv.setText(toNodeAddress);
        amountPay2Tv.setText(payAmount + "");
        failedMessageTv.setText(message);
        fromNodeAddress3Tv.setText(mAddress);

        RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share);
        rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击back后退到第二步
         * @description: Click back button,back to step two
         */
        rootView.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.GONE);
                rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击share to，显示可分享的选项
         * @description: Click share to button,then show the layout for select options
         */
        rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(shareLayout.toString());
                shareLayout.setVisibility(View.VISIBLE);
            }
        });
        /**
         * @备注： 点击face book 图标
         * @description: Click face book icon
         */
        rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasePopWindow.dismiss();
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击twitter 图标
         * @description: Click twitter icon
         */
        rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasePopWindow.dismiss();
                shareLayout.setVisibility(View.GONE);
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
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
