package com.omni.wallet.view.popupwindow.createinvoice;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.client.LuckPkClient;
import com.omni.wallet.entity.InvoiceEntity;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.PayInvoiceFailedEvent;
import com.omni.wallet.entity.event.PayInvoiceSuccessEvent;
import com.omni.wallet.thirdsupport.zxing.util.RedCodeUtils;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.EditInputFilter;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.ShareUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.view.dialog.CreateChannelTipDialog;
import com.omni.wallet.view.dialog.CreateNewChannelTipDialog;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.popupwindow.SelectChannelBalancePopupWindow;
import com.omni.wallet.view.popupwindow.SelectTimePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;

import io.grpc.StatusRuntimeException;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;
import routerrpc.RouterOuterClass;
import toolrpc.LuckPkOuterClass;

/**
 * 汉: 创建红包的弹窗
 * En: CreateLuckyPacketPopupWindow
 * author: guoyalei
 * date: 2022/12/1
 */
public class CreateLuckyPacketPopupWindow {
    private static final String TAG = CreateLuckyPacketPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    TextView assetMaxTv;
    TextView mCanSendTv;
    TextView mCanReceiveTv;
    ProgressBar mProgressBar;
    SelectChannelBalancePopupWindow mSelectChannelBalancePopupWindow;
    SelectTimePopupWindow mSelectTimePopupWindow;
    String mAddress;
    long mAssetId;
    String assetBalanceMax;
    String canReceive;
    String canSend;
    String amountInput;
    String timeInput;
    String timeType;
    String numberInput;
    String qrCodeUrl;
    String qrCodeCotent;
    LoadingDialog mLoadingDialog;
    // pay
    String lnInvoice;
    long payAmount;
    LightningOuterClass.Route route;
    String paymentHash;
    private List<InvoiceEntity> list;
    private List<InvoiceEntity> btcList;
    private static final int PAYMENT_HASH_BYTE_LENGTH = 32;

    public CreateLuckyPacketPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String address, long assetId, long balanceAccount) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_lucky_packet);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            mLoadingDialog = new LoadingDialog(mContext);
            mAddress = address;
            mAssetId = assetId;
            showStepOne(rootView);
            /**
             * @描述： 点击cancel
             * @desc: click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            /**
             * @描述： 点击close
             * @desc: click close button
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
        EditText addressTv = rootView.findViewById(R.id.tv_address);
        InputFilter[] filters = {new EditInputFilter(24)};
        addressTv.setFilters(filters);
        ImageView assetTypeIv = rootView.findViewById(R.id.iv_asset_type);
        TextView assetTypeTv = rootView.findViewById(R.id.tv_asset_type);
        assetMaxTv = rootView.findViewById(R.id.tv_asset_max);
        mCanSendTv = rootView.findViewById(R.id.tv_can_send);
        mCanReceiveTv = rootView.findViewById(R.id.tv_can_receive);
        mProgressBar = rootView.findViewById(R.id.progressbar);
        TextView amountMaxTv = rootView.findViewById(R.id.tv_amount_max);
        EditText amountEdit = rootView.findViewById(R.id.edit_amount);
        TextView amountUnitTv = rootView.findViewById(R.id.tv_amount_unit);
        Button timeButton = rootView.findViewById(R.id.btn_time);
        EditText amountTimeEdit = rootView.findViewById(R.id.edit_time);
        EditText numberEdit = rootView.findViewById(R.id.edit_number);
        if (mAssetId == 0) {
            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeTv.setText("BTC");
            amountUnitTv.setText("BTC");
        } else {
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountUnitTv.setText("dollar");
        }
        getChannelBalance(mAssetId);
        RelativeLayout selectAssetLayout = rootView.findViewById(R.id.layout_select_asset);
        selectAssetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectChannelBalancePopupWindow = new SelectChannelBalancePopupWindow(mContext);
                mSelectChannelBalancePopupWindow.setOnItemClickCallback(new SelectChannelBalancePopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view, ListAssetItemEntity item) {
                        if (item.getPropertyid() == 0) {
                            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                            assetTypeTv.setText("BTC");
                            amountUnitTv.setText("BTC");
                            amountEdit.setText("0");
                        } else {
                            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                            assetTypeTv.setText("dollar");
                            amountUnitTv.setText("dollar");
                            amountEdit.setText("0");
                        }
                        mAssetId = item.getPropertyid();
                        getChannelBalance(mAssetId);
                    }
                });
                mSelectChannelBalancePopupWindow.show(v);
            }
        });
        amountMaxTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEdit.setText(canSend);
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectTimePopupWindow = new SelectTimePopupWindow(mContext);
                mSelectTimePopupWindow.setOnItemClickCallback(new SelectTimePopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_minutes:
                                timeButton.setText(R.string.minutes);
                                break;
                            case R.id.tv_hours:
                                timeButton.setText(R.string.hours);
                                break;
                            case R.id.tv_days:
                                timeButton.setText(R.string.day);
                                break;
                        }
                    }
                });
                mSelectTimePopupWindow.show(v);
            }
        });
        /**
         * @描述： 点击Back
         * @desc: click back button
         */
        rootView.findViewById(R.id.layout_back_to_none).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBasePopWindow.dismiss();
            }
        });
        /**
         * @描述： 点击Next
         * @desc: click next button
         */
        rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountInput = amountEdit.getText().toString();
                timeInput = amountTimeEdit.getText().toString();
                numberInput = numberEdit.getText().toString();
                timeType = timeButton.getText().toString();
                if (StringUtils.isEmpty(amountInput)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.create_invoice_amount));
                    return;
                }
                if (amountInput.equals("0")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.amount_greater_than_0));
                    return;
                }
                // TODO: 2022/11/23 最大值最小值的判断需要完善一下
                if ((Double.parseDouble(amountInput) * 100000000) - (Double.parseDouble(canSend) * 100000000) > 0) {
//                    ToastUtils.showToast(mContext, mContext.getString(R.string.credit_is_running_low));
                    CreateNewChannelTipDialog mCreateNewChannelTipDialog = new CreateNewChannelTipDialog(mContext);
                    mCreateNewChannelTipDialog.setCallback(new CreateNewChannelTipDialog.Callback() {
                        @Override
                        public void onClick() {
                            mBasePopWindow.dismiss();
                        }
                    });
                    mCreateNewChannelTipDialog.show();
                    return;
                }
                if (StringUtils.isEmpty(numberInput)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.enter_the_number));
                    return;
                }
                if (Integer.parseInt(numberInput) > 20) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.num_exceeds));
                    return;
                }
                if (StringUtils.isEmpty(timeInput)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.enter_the_time));
                    return;
                }
                mLoadingDialog.show();
                try {
                    LuckPkClient client = new LuckPkClient(ConstantInOB.usingBTCHostAddress, 38332, mContext.getApplicationContext().getExternalCacheDir() + "/tls.cert", mContext.getApplicationContext().getExternalCacheDir() + "/tls.key.pcks8");
                    try {
                        LuckPkOuterClass.LuckPk payRequest;
                        if (mAssetId == 0) {
                            payRequest = LuckPkOuterClass.LuckPk.newBuilder()
                                    .setAssetId(mAssetId)
                                    .setAmt((long) (Double.parseDouble(amountEdit.getText().toString()) * 100000000))
                                    .setParts(Long.parseLong(numberInput))
                                    .setErrorCreateMsg(addressTv.getText().toString())
                                    .build();
                        } else {
                            payRequest = LuckPkOuterClass.LuckPk.newBuilder()
                                    .setAssetId(mAssetId)
                                    .setAmt((long) (Double.parseDouble(amountEdit.getText().toString()) * 100000000))
                                    .setParts(Long.parseLong(numberInput))
                                    .setErrorCreateMsg(addressTv.getText().toString())
                                    .build();
                        }
                        try {
                            LuckPkOuterClass.LuckPk payResponse = client.blockingStub.createLuckPk(payRequest);
                            LogUtils.e(TAG + "payResponse.getServInvoice()", payResponse.getInvoice());
                            LogUtils.e(TAG + "payResponse.getServInvoice()", payResponse.toString());
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("amt", payResponse.getAmt());
                                jsonObject.put("id", payResponse.getId());
                                jsonObject.put("asstId", payResponse.getAssetId());
                                jsonObject.put("time", payResponse.getCreatedAt());
                                jsonObject.put("totalNum", payResponse.getParts());
                                jsonObject.put("giveNum", payResponse.getGives());
                                jsonObject.put("bestWishes", payResponse.getErrorCreateMsg());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            qrCodeUrl = UriUtil.generateLuckyPacketUri(payResponse.getInvoice());
                            qrCodeCotent = jsonObject.toString();
                            String data = payResponse.getInvoice();
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
                                            mLoadingDialog.dismiss();
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
                                        /**
                                         * @备注： 存储未支付的发票
                                         * @description: Store Unpaid Invoices
                                         */
                                        saveInvoiceList(resp);
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
                                                        if (e.getMessage().contains("self-payments not allowed")) {
                                                            updateInvoiceList();
                                                        }
                                                        EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                                        ToastUtils.showToast(mContext, e.getMessage());
                                                        mLoadingDialog.dismiss();
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
                                                                    showStepPay(rootView);
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
                                                                    showStepPay(rootView);
                                                                    deletePaymentProbe(payment.getPaymentHash());
                                                                }
                                                            });
                                                            break;
                                                        default:
                                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mLoadingDialog.dismiss();
                                                                    deletePaymentProbe(payment.getPaymentHash());
                                                                    CreateChannelTipDialog mCreateChannelTipDialog = new CreateChannelTipDialog(mContext);
                                                                    mCreateChannelTipDialog.show();
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
                        } catch (StatusRuntimeException e) {
                            e.printStackTrace();
                            LogUtils.e(TAG, e.getMessage());
                            return;
                        }
                    } finally {
                        client.shutdown();
                    }
                } catch (SSLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showStepPay(View rootView) {
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
                            ToastUtils.showToast(mContext, e.getMessage());
                            mLoadingDialog.dismiss();
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
                                        updateInvoiceList();
                                        EventBus.getDefault().post(new PayInvoiceSuccessEvent());
                                        mLoadingDialog.dismiss();
                                        // updated the history, so it is shown the next time the user views it
                                        rootView.findViewById(R.id.lv_lucky_packet_step_one).setVisibility(View.GONE);
                                        rootView.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                        rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                        rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                        showStepSuccess(rootView);
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
                                                        if (e.getMessage().contains("invoice is already paid") || e.getMessage().contains("invoice expired")) {
                                                            updateInvoiceList();
                                                        }
                                                        EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                                        ToastUtils.showToast(mContext, e.getMessage());
                                                        mLoadingDialog.dismiss();
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
                                                                updateInvoiceList();
                                                                EventBus.getDefault().post(new PayInvoiceSuccessEvent());
                                                                mLoadingDialog.dismiss();
                                                                rootView.findViewById(R.id.lv_lucky_packet_step_one).setVisibility(View.GONE);
                                                                rootView.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                                                rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                                rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                showStepSuccess(rootView);
                                                            } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                                                EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                                                mLoadingDialog.dismiss();
                                                                String errorMessage;
                                                                switch (resp.getFailureReason()) {
                                                                    case FAILURE_REASON_TIMEOUT:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_NO_ROUTE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                                                        ToastUtils.showToast(mContext, errorMessage);
                                                                        break;
                                                                    case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                                                        errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                                                        ToastUtils.showToast(mContext, errorMessage);
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
                            if (e.getMessage().contains("invoice is already paid") || e.getMessage().contains("invoice expired")) {
                                updateInvoiceList();
                            }
                            EventBus.getDefault().post(new PayInvoiceFailedEvent());
                            ToastUtils.showToast(mContext, e.getMessage());
                            mLoadingDialog.dismiss();
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
                                    updateInvoiceList();
                                    EventBus.getDefault().post(new PayInvoiceSuccessEvent());
                                    mLoadingDialog.dismiss();
                                    rootView.findViewById(R.id.lv_lucky_packet_step_one).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                    showStepSuccess(rootView);
                                } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                    EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                    mLoadingDialog.dismiss();
                                    String errorMessage;
                                    switch (resp.getFailureReason()) {
                                        case FAILURE_REASON_TIMEOUT:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_timeout);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_NO_ROUTE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_no_route);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_INSUFFICIENT_BALANCE:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_insufficient_balance);
                                            ToastUtils.showToast(mContext, errorMessage);
                                            break;
                                        case FAILURE_REASON_INCORRECT_PAYMENT_DETAILS:
                                            errorMessage = mContext.getResources().getString(R.string.error_payment_invalid_details);
                                            ToastUtils.showToast(mContext, errorMessage);
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

    private void showStepSuccess(View rootView) {
        ImageView assetTypeSuccessIv = rootView.findViewById(R.id.iv_asset_type_success);
        TextView assetTypeSuccessTv = rootView.findViewById(R.id.tv_asset_type_success);
        TextView amountSuccessTv = rootView.findViewById(R.id.tv_amount_success);
        TextView amountUnitSuccessTv = rootView.findViewById(R.id.tv_amount_unit_success);
        TextView numberSuccessTv = rootView.findViewById(R.id.tv_number_success);
        TextView timeSuccessTv = rootView.findViewById(R.id.tv_time_success);
        TextView timeUnitSuccessTv = rootView.findViewById(R.id.tv_time_unit_success);
        ImageView qrCodeIv = rootView.findViewById(R.id.iv_success_qrcode);
        TextView paymentSuccessTv = rootView.findViewById(R.id.tv_success_payment);
        ImageView copyIv = rootView.findViewById(R.id.iv_success_copy);
        if (mAssetId == 0) {
            assetTypeSuccessIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeSuccessTv.setText("BTC");
            amountUnitSuccessTv.setText("BTC");
        } else {
            assetTypeSuccessIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeSuccessTv.setText("dollar");
            amountUnitSuccessTv.setText("dollar");
        }
        amountSuccessTv.setText(amountInput);
        numberSuccessTv.setText(numberInput);
        timeSuccessTv.setText(timeInput);
        timeUnitSuccessTv.setText(timeType);
        paymentSuccessTv.setText(qrCodeUrl);
        Bitmap mQRBitmap = RedCodeUtils.createQRCode(qrCodeCotent, DisplayUtil.dp2px(mContext, 100));
        qrCodeIv.setImageBitmap(mQRBitmap);

        copyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //接收需要复制到粘贴板的地址
                //Get the address which will copy to clipboard
                String toCopyAddress = qrCodeUrl;
                //接收需要复制成功的提示语
                //Get the notice when you copy success
                String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                CopyUtil.SelfCopy(mContext, toCopyAddress, toastString);
            }
        });
        /**
         * @描述： 点击Back
         * @desc: click back button
         */
        rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.lv_lucky_packet_step_one).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.GONE);
                rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
                showStepOne(rootView);
            }
        });

        /**
         * for success page
         * 成功页面
         */
        RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share_success);
        rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @描述： 点击成功页 share to
         * @desc: click share to button in success page
         */
        rootView.findViewById(R.id.layout_share_to_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.VISIBLE);
            }
        });
        /**
         * @描述： 点击成功页 facebook
         * @desc: click facebook button in success page
         */
        rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "Not yet open, please wait");
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @描述： 点击成功页 twitter
         * @desc: click twitter button in success page
         */
        rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, qrCodeUrl));
                shareLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * get Channel Balance
     * 查询通道余额
     *
     * @param propertyid
     */
    private void getChannelBalance(long propertyid) {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) propertyid)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                            if (propertyid == 0) {
                                if (resp.getLocalBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canSend = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canSend = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000)) / 100000000);
                                }
                                mCanSendTv.setText(canSend);
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                }
                                mCanReceiveTv.setText(canReceive);
                                if (resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000 + resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000 + resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                }
                                assetMaxTv.setText(assetBalanceMax);
                            } else {
                                if (resp.getLocalBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canSend = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canSend = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat())) / 100000000);
                                }
                                mCanSendTv.setText(canSend);
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
                                }
                                mCanReceiveTv.setText(canReceive);
                                if (resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat())) / 100000000);
                                }
                                assetMaxTv.setText(assetBalanceMax);
                            }
                            /**
                             * @描述： 设置进度条
                             * @desc: set progress bar
                             */
                            long totalBalance = resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat();
                            float barValue = (float) ((double) resp.getLocalBalance().getMsat() / (double) totalBalance);
                            mProgressBar.setProgress((int) (barValue * 100f));
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * @备注： 存储未支付的发票
     * @description: Store Unpaid Invoices
     */
    private void saveInvoiceList(LightningOuterClass.PayReq resp) {
        if (mAssetId == 0) {
            SharedPreferences sp = mContext.getSharedPreferences("SP_BTC_INVOICE_LIST", Activity.MODE_PRIVATE);
            String btcInvoiceListJson = sp.getString("btcInvoiceListKey", "");
            if (StringUtils.isEmpty(btcInvoiceListJson)) {
                btcList = new ArrayList<>();
                InvoiceEntity entity = new InvoiceEntity();
                entity.setAssetId(0);
                entity.setDate(resp.getTimestamp());
                entity.setAmount(resp.getAmtMsat());
                entity.setInvoice(lnInvoice);
                btcList.add(entity);
                removeDuplicateInvoice(btcList);
                Gson gson = new Gson();
                String jsonStr = gson.toJson(btcList);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("btcInvoiceListKey", jsonStr);
                editor.commit();
            } else {
                Gson gson = new Gson();
                btcList = gson.fromJson(btcInvoiceListJson, new TypeToken<List<InvoiceEntity>>() {
                }.getType());
                InvoiceEntity entity = new InvoiceEntity();
                entity.setAssetId(0);
                entity.setDate(resp.getTimestamp());
                entity.setAmount(resp.getAmtMsat());
                entity.setInvoice(lnInvoice);
                btcList.add(entity);
                removeDuplicateInvoice(btcList);
                String jsonStr = gson.toJson(btcList);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("btcInvoiceListKey", jsonStr);
                editor.commit();
            }
        } else {
            SharedPreferences sp = mContext.getSharedPreferences("SP_INVOICE_LIST", Activity.MODE_PRIVATE);
            String invoiceListJson = sp.getString("invoiceListKey", "");
            if (StringUtils.isEmpty(invoiceListJson)) {
                list = new ArrayList<>();
                InvoiceEntity entity = new InvoiceEntity();
                entity.setAssetId(resp.getAssetId());
                entity.setDate(resp.getTimestamp());
                entity.setAmount(resp.getAmount());
                entity.setInvoice(lnInvoice);
                list.add(entity);
                removeDuplicateInvoice(list);
                Gson gson = new Gson();
                String jsonStr = gson.toJson(list);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("invoiceListKey", jsonStr);
                editor.commit();
            } else {
                Gson gson = new Gson();
                list = gson.fromJson(invoiceListJson, new TypeToken<List<InvoiceEntity>>() {
                }.getType());
                InvoiceEntity entity = new InvoiceEntity();
                entity.setAssetId(resp.getAssetId());
                entity.setDate(resp.getTimestamp());
                entity.setAmount(resp.getAmount());
                entity.setInvoice(lnInvoice);
                list.add(entity);
                removeDuplicateInvoice(list);
                String jsonStr = gson.toJson(list);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("invoiceListKey", jsonStr);
                editor.commit();
            }
        }
        EventBus.getDefault().post(new PayInvoiceFailedEvent());
    }

    /**
     * @备注： 循环删除重复数据
     * @description: Circular deletion of duplicate data
     */
    public static void removeDuplicateInvoice(List<InvoiceEntity> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getInvoice().equals(list.get(i).getInvoice())) {
                    list.remove(j);
                }
            }
        }
        System.out.println(list);
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

    /**
     * @备注： 更新未支付的发票
     * @description: Update Unpaid Invoices
     */
    private void updateInvoiceList() {
        if (mAssetId == 0) {
            for (int i = 0; i < btcList.size(); i++) {
                if (btcList.get(i).getInvoice().equals(lnInvoice)) {
                    btcList.remove(i);
                }
            }
            Gson gson = new Gson();
            String jsonStr = gson.toJson(btcList);
            SharedPreferences sp = mContext.getSharedPreferences("SP_BTC_INVOICE_LIST", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("btcInvoiceListKey", jsonStr);
            editor.commit();
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getInvoice().equals(lnInvoice)) {
                    list.remove(i);
                }
            }
            Gson gson = new Gson();
            String jsonStr = gson.toJson(list);
            SharedPreferences sp = mContext.getSharedPreferences("SP_INVOICE_LIST", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("invoiceListKey", jsonStr);
            editor.commit();
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
