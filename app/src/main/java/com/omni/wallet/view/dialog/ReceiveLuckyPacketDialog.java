package com.omni.wallet.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.client.LuckPkClient;
import com.omni.wallet.entity.event.CreateInvoiceEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Random;

import javax.net.ssl.SSLException;

import io.grpc.StatusRuntimeException;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import toolrpc.LuckPkOuterClass;

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
    String time;
    int id;
    double randAmount;
    String amount;
    String number;
    LoadingDialog mLoadingDialog;

    public ReceiveLuckyPacketDialog(Context context) {
        this.mContext = context;
    }

    public void show(String data) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_receive_lucky_packet)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
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
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(data);
                    mAssetId = Long.parseLong(jsonObject.get("asstId").toString());
                    id = (int) jsonObject.get("id");
                    time = jsonObject.get("time").toString();
                    amount = jsonObject.get("amt").toString();
                    number = jsonObject.get("totalNum").toString();
                    showStepDecodePay();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    private void showStepDecodePay() {
        Random rand = new Random();
        randAmount = rand.nextDouble() * (Double.parseDouble(amount) / Double.parseDouble(number));
        LogUtils.e("============================", randAmount + "");
        LightningOuterClass.Invoice asyncInvoiceRequest;
        if (mAssetId == 0) {
            asyncInvoiceRequest = LightningOuterClass.Invoice.newBuilder()
                    .setAssetId((int) mAssetId)
                    .setValueMsat((long) randAmount * 1000)
                    .setMemo("lucky")
                    .setExpiry(Long.parseLong("86400")) // in seconds
                    .setPrivate(false)
                    .build();
        } else {
            asyncInvoiceRequest = LightningOuterClass.Invoice.newBuilder()
                    .setAssetId((int) mAssetId)
                    .setAmount((long) randAmount)
                    .setMemo("lucky")
                    .setExpiry(Long.parseLong("86400")) // in seconds
                    .setPrivate(false)
                    .build();
        }
        Obdmobile.oB_AddInvoice(asyncInvoiceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------addInvoiceOnError------------------" + e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mContext, e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.AddInvoiceResponse resp = LightningOuterClass.AddInvoiceResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------addInvoiceOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LuckPkClient client = new LuckPkClient("43.138.107.248", 38332, mContext.getApplicationContext().getExternalCacheDir() + "/tls.cert", mContext.getApplicationContext().getExternalCacheDir() + "/tls.key.pcks8");
                                try {
                                    LuckPkOuterClass.GiveLuckPkReq payRequest = LuckPkOuterClass.GiveLuckPkReq.newBuilder()
                                            .setId(id)
                                            .setInvoice(resp.getPaymentRequest())
                                            .build();
                                    try {
                                        client.blockingStub.giveLuckPk(payRequest);
                                        EventBus.getDefault().post(new CreateInvoiceEvent());
                                        mLoadingDialog.dismiss();
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_success).setVisibility(View.VISIBLE);
                                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                        showStepSuccess(time);
                                    } catch (StatusRuntimeException e) {
                                        e.printStackTrace();
                                        LogUtils.e(TAG, e.getMessage());
                                        mLoadingDialog.dismiss();
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_waiting).setVisibility(View.GONE);
                                        mAlertDialog.findViewById(R.id.lv_lucky_packet_collected).setVisibility(View.VISIBLE);
                                        mAlertDialog.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                        showStepDesc(e.getMessage());
                                    }
                                } finally {
                                    client.shutdown();
                                }
                            } catch (SSLException |
                                    InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (
                        InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
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
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(randAmount)) / 100000000));
        } else {
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(randAmount)) / 100000000));
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

    private void showStepDesc(String message) {
        TextView descTv = mAlertDialog.findViewById(R.id.tv_desc);
        descTv.setText(message);
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
