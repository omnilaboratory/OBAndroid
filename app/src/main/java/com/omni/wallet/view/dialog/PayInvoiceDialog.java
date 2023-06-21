package com.omni.wallet.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.entity.AssetEntity;
import com.omni.wallet.entity.InvoiceEntity;
import com.omni.wallet.entity.event.PayInvoiceFailedEvent;
import com.omni.wallet.entity.event.PayInvoiceSuccessEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.ScanActivity;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.ShareUtil;
import com.omni.wallet.utils.UriUtil;

import org.greenrobot.eventbus.EventBus;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;
import routerrpc.RouterOuterClass;

/**
 * 汉: 支付发票的弹窗
 * En: PayInvoiceDialog
 * author: guoyalei
 * date: 2022/12/5
 */
public class PayInvoiceDialog {
    private static final String TAG = PayInvoiceDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    String mAddress;
    long mAssetId;
    int mTag;
    String toNodeAddress;
    long payAmount;
    String lnInvoice;
    long feeSats = 0;
    private static final int PAYMENT_HASH_BYTE_LENGTH = 32;
    LightningOuterClass.Route route;
    String paymentHash;
    String toNodeName;
    LoadingDialog mLoadingDialog;
    private List<InvoiceEntity> list;
    private List<InvoiceEntity> btcList;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    public PayInvoiceDialog(Context context) {
        this.mContext = context;
    }

    public void show(String address, long assetId, String invoiceAddr, int tag) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_popupwindow_pay_invoice_stepone)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        mLoadingDialog = new LoadingDialog(mContext);
        mAddress = address;
        mAssetId = assetId;
        mTag = tag;
        showStepOne(invoiceAddr, tag);
        /**
         * @备注： 点击cancel 按钮
         * @description: Click cancel button
         */
        mAlertDialog.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        /**
         * @备注： close 按钮
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

    private void showStepOne(String invoiceAddr, int tag) {
        TextView fromNodeAddressTv = mAlertDialog.findViewById(R.id.tv_from_node_address);
        TextView fromNodeNameTv = mAlertDialog.findViewById(R.id.tv_from_node_name);
        EditText invoiceEdit = mAlertDialog.findViewById(R.id.edit_invoice);
        if (!StringUtils.isEmpty(invoiceAddr)) {
            invoiceEdit.setText(invoiceAddr);
        }
        fromNodeAddressTv.setText(StringUtils.encodePubkey(mAddress));
        /**
         * @备注： 点击二维码扫描按钮跳转扫码页面
         * @description: Click the two-dimensional code scanning button to jump to the scanning code page
         */
        mAlertDialog.findViewById(R.id.layout_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        mAlertDialog.dismiss();
                        Intent intent = new Intent(mContext, ScanActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(ScanActivity.KEY_SCAN_CODE, tag);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
                    }
                });
            }
        });
        /**
         * @备注： 点击next显示invoice step two
         * @description: Show pay invoice step two when click next
         */
        mAlertDialog.findViewById(R.id.layout_next_to_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = invoiceEdit.getText().toString();
                // Avoid index out of bounds. An Request with less than 11 characters isn't valid.
                if (data.length() < 11) {
                    ToastUtils.showToast(mContext, "Length is greater than 11 characters");
                    return;
                }
                mLoadingDialog.show();
                // TODO: 2023/1/9 待完善
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mLoadingDialog.dismiss();
                    }
                }, 3000);
                // convert to lower case
                lnInvoice = data.toLowerCase();
                // Remove the "lightning:" uri scheme if it is present, LND needs it without uri scheme
                lnInvoice = UriUtil.removeURI(lnInvoice);
                if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                    if (!lnInvoice.contains("obto")) {
                        mLoadingDialog.dismiss();
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_error).setVisibility(View.VISIBLE);
                        mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                        showStepError();
                        return;
                    }
                } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                    if (!lnInvoice.contains("obort")) {
                        mLoadingDialog.dismiss();
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_error).setVisibility(View.VISIBLE);
                        mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                        showStepError();
                        return;
                    }
                } else { //mainnet
                    if (!lnInvoice.contains("obo")) {
                        mLoadingDialog.dismiss();
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_error).setVisibility(View.VISIBLE);
                        mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                        showStepError();
                        return;
                    }
                }
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
                            // To Node
                            toNodeName = resp.getDescription();
                            mAssetId = resp.getAssetId();
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
                                            mLoadingDialog.dismiss();
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
//                                                        if (route.getTotalFeesMsat() % 1000 == 0) {
//                                                            feeSats = route.getTotalFeesMsat() / 1000;
//                                                        } else {
//                                                            feeSats = (route.getTotalFeesMsat() / 1000) + 1;
//                                                        }
                                                        paymentHash = payment.getPaymentHash();
                                                        toNodeAddress = resp.getDestination();
                                                        if (mAssetId == 0) {
                                                            payAmount = resp.getAmtMsat();
                                                        } else {
                                                            payAmount = resp.getAmount();
                                                        }
                                                        feeSats = payAmount / 10000;
                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                                                        mLoadingDialog.dismiss();
                                                        showStepTwo();
                                                        deletePaymentProbe(payment.getPaymentHash());
                                                    }
                                                });
                                                break;
                                            case FAILURE_REASON_NO_ROUTE:
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        paymentHash = payment.getPaymentHash();
                                                        toNodeAddress = resp.getDestination();
                                                        if (mAssetId == 0) {
                                                            payAmount = resp.getAmtMsat();
                                                        } else {
                                                            payAmount = resp.getAmount();
                                                        }
                                                        feeSats = payAmount / 10000;
                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                                                        mLoadingDialog.dismiss();
                                                        showStepTwo();
                                                        deletePaymentProbe(payment.getPaymentHash());
                                                    }
                                                });
                                                break;
                                            default:
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mAlertDialog.dismiss();
                                                        mLoadingDialog.dismiss();
//                                                        ToastUtils.showToast(mContext, payment.getFailureReason().toString());
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
                entity.setExpiry(resp.getExpiry());
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
                entity.setExpiry(resp.getExpiry());
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
                entity.setExpiry(resp.getExpiry());
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
                entity.setExpiry(resp.getExpiry());
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

    private void showStepTwo() {
        TextView fromNodeAddress1Tv = mAlertDialog.findViewById(R.id.tv_from_node_address_1);
        TextView fromNodeName1Tv = mAlertDialog.findViewById(R.id.tv_from_node_name_1);
        TextView toNodeAddress1Tv = mAlertDialog.findViewById(R.id.tv_to_node_address_1);
        TextView toNodeName1Tv = mAlertDialog.findViewById(R.id.tv_to_node_name_1);
        ImageView amountLogoTv = mAlertDialog.findViewById(R.id.iv_amount_logo);
        TextView amountPayTv = mAlertDialog.findViewById(R.id.tv_amount_pay);
        TextView amountPayExchangeTv = mAlertDialog.findViewById(R.id.tv_amount_pay_exchange);
        TextView amountPayFeeTv = mAlertDialog.findViewById(R.id.tv_amount_pay_fee);
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), amountLogoTv);
            }
        }
        fromNodeAddress1Tv.setText(StringUtils.encodePubkey(mAddress));
        toNodeAddress1Tv.setText(StringUtils.encodePubkey(toNodeAddress));
        if (StringUtils.isEmpty(toNodeName)) {
            toNodeName1Tv.setText("None");
        } else {
            toNodeName1Tv.setText(toNodeName);
        }
        DecimalFormat df = new DecimalFormat("0.00######");
        DecimalFormat df1 = new DecimalFormat("0.00");
        if (mAssetId == 0) {
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000));
            amountPayExchangeTv.setText("$" + df1.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            amountPayFeeTv.setText("$" + df1.format(Double.parseDouble(String.valueOf(feeSats / 1000)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
        } else {
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000));
            amountPayExchangeTv.setText("$" + df1.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            amountPayFeeTv.setText("$" + df.format(Double.parseDouble(String.valueOf(feeSats)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
        }
        /**
         * @备注： 点击back显示invoice step one
         * @description: Show pay invoice step one when click next
         */
        mAlertDialog.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.VISIBLE);
            }
        });
        /**
         * @备注： 点击pay执行支付操作，根据支付结果跳转到succeed或者failed步骤
         * @description: Click pay to execute the payment operation, and jump to the successful or failed step according to the payment result
         */
        mAlertDialog.findViewById(R.id.layout_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                    mLoadingDialog.dismiss();
                                    mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                    mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                    mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
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
                                                updateInvoiceList();
                                                PayInvoiceSuccessEvent event = new PayInvoiceSuccessEvent();
                                                event.setTag(mTag);
                                                EventBus.getDefault().post(event);
                                                mLoadingDialog.dismiss();
                                                // updated the history, so it is shown the next time the user views it
                                                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                                mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                showStepSuccess();
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
                                                                mLoadingDialog.dismiss();
                                                                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                                                mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
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
                                                                        updateInvoiceList();
                                                                        PayInvoiceSuccessEvent event = new PayInvoiceSuccessEvent();
                                                                        event.setTag(mTag);
                                                                        EventBus.getDefault().post(event);
                                                                        mLoadingDialog.dismiss();
                                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                                                        mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                                                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                                                        showStepSuccess();
                                                                    } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                                                        EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                                                        mLoadingDialog.dismiss();
                                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                                                        mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                                                        mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
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
                                    if (e.getMessage().contains("invoice is already paid") || e.getMessage().contains("invoice expired")) {
                                        updateInvoiceList();
                                    }
                                    EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                    mLoadingDialog.dismiss();
                                    mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                    mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                    mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
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
                                            updateInvoiceList();
                                            PayInvoiceSuccessEvent event = new PayInvoiceSuccessEvent();
                                            event.setTag(mTag);
                                            EventBus.getDefault().post(event);
                                            mLoadingDialog.dismiss();
                                            mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                            mAlertDialog.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                            mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                            mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                            showStepSuccess();
                                        } else if (resp.getStatus() == LightningOuterClass.Payment.PaymentStatus.FAILED) {
                                            EventBus.getDefault().post(new PayInvoiceFailedEvent());
                                            mLoadingDialog.dismiss();
                                            mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                            mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                                            mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
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
        });
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

    private void showStepSuccess() {
        TextView fromNodeAddress2Tv = mAlertDialog.findViewById(R.id.tv_from_node_address_2);
        TextView fromNodeName2Tv = mAlertDialog.findViewById(R.id.tv_from_node_name_2);
        TextView toNodeAddress2Tv = mAlertDialog.findViewById(R.id.tv_to_node_address_2);
        TextView toNodeName2Tv = mAlertDialog.findViewById(R.id.tv_to_node_name_2);
        ImageView amountLogo1Tv = mAlertDialog.findViewById(R.id.iv_amount_logo_1);
        TextView amountPay1Tv = mAlertDialog.findViewById(R.id.tv_amount_pay_1);
        TextView amountPayExchange1Tv = mAlertDialog.findViewById(R.id.tv_amount_pay_exchange_1);
        TextView amountPayFee1Tv = mAlertDialog.findViewById(R.id.tv_amount_pay_fee_1);
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), amountLogo1Tv);
            }
        }
        fromNodeAddress2Tv.setText(StringUtils.encodePubkey(mAddress));
        toNodeAddress2Tv.setText(StringUtils.encodePubkey(toNodeAddress));
        if (StringUtils.isEmpty(toNodeName)) {
            toNodeName2Tv.setText("None");
        } else {
            toNodeName2Tv.setText(toNodeName);
        }
        DecimalFormat df = new DecimalFormat("0.00######");
        DecimalFormat df1 = new DecimalFormat("0.00");
        if (mAssetId == 0) {
            amountPay1Tv.setText(df.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000));
            amountPayExchange1Tv.setText("$" + df1.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            amountPayFee1Tv.setText("$" + df1.format(Double.parseDouble(String.valueOf(feeSats / 1000)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
        } else {
            amountPay1Tv.setText(df.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000));
            amountPayExchange1Tv.setText("$" + df1.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            amountPayFee1Tv.setText("$" + df.format(Double.parseDouble(String.valueOf(feeSats)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
        }
    }

    private void showStepFailed(String message) {
        TextView fromNodeAddress3Tv = mAlertDialog.findViewById(R.id.tv_from_node_address_3);
        TextView toNodeAddress3Tv = mAlertDialog.findViewById(R.id.tv_to_node_address_3);
        ImageView amountLogo2Tv = mAlertDialog.findViewById(R.id.iv_amount_logo_2);
        TextView amountUnitTv = mAlertDialog.findViewById(R.id.tv_amount_unit);
        TextView amountPay2Tv = mAlertDialog.findViewById(R.id.tv_amount_pay_2);
        TextView amountUnit1Tv = mAlertDialog.findViewById(R.id.tv_amount_unit_1);
        TextView payTimeTv = mAlertDialog.findViewById(R.id.tv_pay_time);
        TextView payTimeUnitTv = mAlertDialog.findViewById(R.id.tv_pay_time_unit);
        TextView failedMessageTv = mAlertDialog.findViewById(R.id.tv_failed_message);
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), amountLogo2Tv);
                amountUnitTv.setText(entity.getName());
                amountUnit1Tv.setText(entity.getName());
            }
        }
        toNodeAddress3Tv.setText(toNodeAddress);
        DecimalFormat df = new DecimalFormat("0.00######");
        if (mAssetId == 0) {
            amountPay2Tv.setText(df.format(Double.parseDouble(String.valueOf(payAmount / 1000)) / 100000000));
        } else {
            amountPay2Tv.setText(df.format(Double.parseDouble(String.valueOf(payAmount)) / 100000000));
        }
        failedMessageTv.setText(message);
        fromNodeAddress3Tv.setText(mAddress);

        RelativeLayout shareLayout = mAlertDialog.findViewById(R.id.layout_share);
        mAlertDialog.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击back后退到第二步
         * @description: Click back button,back to step two
         */
        mAlertDialog.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击share to，显示可分享的选项
         * @description: Click share to button,then show the layout for select options
         */
        mAlertDialog.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
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
        mAlertDialog.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "Not yet open, please wait");
//                mAlertDialog.dismiss();
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击twitter 图标
         * @description: Click twitter icon
         */
        mAlertDialog.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, lnInvoice));
//                mAlertDialog.dismiss();
                shareLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showStepError() {
        TextView fromNodeAddressErrorTv = mAlertDialog.findViewById(R.id.tv_from_node_address_error);
        fromNodeAddressErrorTv.setText(mAddress);

        RelativeLayout shareLayout = mAlertDialog.findViewById(R.id.layout_share_error);
        mAlertDialog.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击back后退到第一步
         * @description: Click back button,back to step one
         */
        mAlertDialog.findViewById(R.id.layout_back_to_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_pay_invoice_step_error).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击share to，显示可分享的选项
         * @description: Click share to button,then show the layout for select options
         */
        mAlertDialog.findViewById(R.id.layout_share_to_error).setOnClickListener(new View.OnClickListener() {
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
        mAlertDialog.findViewById(R.id.iv_facebook_share_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "Not yet open, please wait");
                shareLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @备注： 点击twitter 图标
         * @description: Click twitter icon
         */
        mAlertDialog.findViewById(R.id.iv_twitter_share_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, lnInvoice));
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
