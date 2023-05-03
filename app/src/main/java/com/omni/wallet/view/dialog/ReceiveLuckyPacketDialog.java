package com.omni.wallet.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.client.LuckPkClient;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.entity.AssetEntity;
import com.omni.wallet.entity.event.CreateInvoiceEvent;
import com.omni.wallet.framelibrary.entity.User;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
    String besyWishes;
    String canReceive;
    LoadingDialog mLoadingDialog;
    private List<AssetEntity> mAssetData = new ArrayList<>();

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
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(data);
            mAssetId = Long.parseLong(jsonObject.get("asstId").toString());
            id = (int) jsonObject.get("id");
            time = jsonObject.get("time").toString();
            amount = jsonObject.get("amt").toString();
            number = jsonObject.get("totalNum").toString();
            besyWishes = jsonObject.get("bestWishes").toString();
            getChannelBalance(mAssetId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                showStepDecodePay();
            }
        }, 1000);
    }

    private void showStepDecodePay() {
        Random rand = new Random();
        double max = Double.parseDouble(amount) / Double.parseDouble(number);
        double min = (Double.parseDouble(amount) / Double.parseDouble(number)) / 2;
        if (Integer.parseInt(number) == 1) {
            randAmount = Double.parseDouble(amount);
        } else {
            randAmount = rand.nextDouble() * (max - min) + min;
        }
        LogUtils.e("============================", randAmount + "");
        if (randAmount - (Double.parseDouble(canReceive) * 100000000) > 0) {
            CreateNewChannelTipDialog mCreateNewChannelTipDialog = new CreateNewChannelTipDialog(mContext);
            mCreateNewChannelTipDialog.setCallback(new CreateNewChannelTipDialog.Callback() {
                @Override
                public void onClick() {
                    mLoadingDialog.dismiss();
                    mAlertDialog.dismiss();
                }
            });
            mCreateNewChannelTipDialog.show();
            return;
        }
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
                                LuckPkClient client = new LuckPkClient(ConstantWithNetwork.getInstance(ConstantInOB.networkType).getBTC_HOST_ADDRESS(), 38332, mContext.getApplicationContext().getExternalFilesDir(null).toString() + "/ObdMobile/" + ConstantInOB.networkType + "/tls.cert", mContext.getApplicationContext().getExternalFilesDir(null) + "/obd" + "/tls.key.pcks8");
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
        TextView bestWishesTv = mAlertDialog.findViewById(R.id.tv_best_wishes);
        DecimalFormat df = new DecimalFormat("0.00######");
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), assetTypeIv);
                assetTypeTv.setText(entity.getName());
            }
        }
        amountPayTv.setText(df.format(Double.parseDouble(String.valueOf(randAmount)) / 100000000));
        payTimeTv.setText(DateUtils.Hourmin(timestamp));
        payDateTv.setText(DateUtils.MonthDay(timestamp));
        bestWishesTv.setText(besyWishes);
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
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                }
                            } else {
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
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

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
