package com.omni.wallet.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.entity.AssetEntity;
import com.omni.wallet.entity.event.CloseChannelEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelListItem;
import com.omni.wallet.utils.TimeFormatUtil;
import com.omni.wallet.utils.UtilFunctions;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.view.dialog.CreateChannelDialog;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.dialog.SendFailedDialog;
import com.omni.wallet.view.dialog.SendSuccessDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

/**
 * 汉: 通道详情的弹窗
 * En: ChannelDetailsPopupWindow
 * author: guoyalei
 * date: 2022/10/11
 */
public class ChannelDetailsPopupWindow {
    private static final String TAG = ChannelDetailsPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ImageView mStatusDot;
    private TextView mRemoteName;
    private ImageView mAssetLogo;
    private TextView mAssetUnit;
    private TextView mRemotePubkey;
    private TextView mAssetAmount;
    private ProgressBar mProgressBar;
    private TextView mLocalBalance;
    private TextView mLocalBalanceUnit;
    private TextView mRemoteBalance;
    private TextView mRemoteBalanceUnit;
    private TextView mFundingTransaction;
    private TextView mCreateTime;
    private TextView mNumberOfUpdates;
    private TextView mTimeLock;
    private TextView mActivity;
    private TextView mTotalSent;
    private TextView mTotalReceived;
    private RelativeLayout mCloseChannelParentLayout;
    private LinearLayout mCloseChannelLayout;
    private TextView mCloseChannelButton;
    private TextView mCloseChannelNoButton;
    private ImageView mCloseChannelIv;
    private LinearLayout mAnotherInfo;

    LoadingDialog mLoadingDialog;
    SendSuccessDialog mSendSuccessDialog;
    SendFailedDialog mSendFailedDialog;
    CreateChannelDialog mCreateChannelDialog;

    private String mChannelPoint;
    private List<String> txidList;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    public ChannelDetailsPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, ByteString channelString, int type, long balanceAmount, String walletAddress, String pubKey) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_channel_details);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            mLoadingDialog = new LoadingDialog(mContext);
            mSendSuccessDialog = new SendSuccessDialog(mContext);
            mSendFailedDialog = new SendFailedDialog(mContext);
            mStatusDot = rootView.findViewById(R.id.iv_channel_state);
            mRemoteName = rootView.findViewById(R.id.tv_node_name);
            mAssetLogo = rootView.findViewById(R.id.im_token_type);
            mAssetUnit = rootView.findViewById(R.id.tv_token_type);
            mRemotePubkey = rootView.findViewById(R.id.tv_pubkey_value);
            mAssetAmount = rootView.findViewById(R.id.tv_asset_amount);
            mLocalBalance = rootView.findViewById(R.id.tv_local_amount);
            mLocalBalanceUnit = rootView.findViewById(R.id.tv_local_amount_unit);
            mRemoteBalance = rootView.findViewById(R.id.tv_remote_amount);
            mRemoteBalanceUnit = rootView.findViewById(R.id.tv_remote_amount_unit);
            mFundingTransaction = rootView.findViewById(R.id.tv_funding_transaction);
            mCreateTime = rootView.findViewById(R.id.tv_create_time);
            mNumberOfUpdates = rootView.findViewById(R.id.tv_number_of_updates);
            mTimeLock = rootView.findViewById(R.id.tv_time_lock);
            mActivity = rootView.findViewById(R.id.tv_activity);
            mTotalSent = rootView.findViewById(R.id.tv_total_sent);
            mTotalReceived = rootView.findViewById(R.id.tv_total_received);
            mCloseChannelParentLayout = rootView.findViewById(R.id.layout_close_channel_parent);
            mCloseChannelLayout = rootView.findViewById(R.id.layout_close_channel);
            mCloseChannelButton = rootView.findViewById(R.id.tv_close_channel);
            mCloseChannelNoButton = rootView.findViewById(R.id.tv_close_channel_no);
            mCloseChannelIv = rootView.findViewById(R.id.iv_close_channel);
            mAnotherInfo = rootView.findViewById(R.id.layout_another_info);

            // set progress bar
            // 设置进度条
            mProgressBar = rootView.findViewById(R.id.pv_amount_percent);
            float barValue = (float) ((double) 100 / (double) 700);
            mProgressBar.setProgress((int) (barValue * 100f));

            try {
                switch (type) {
                    case ChannelListItem.TYPE_OPEN_CHANNEL:
                        bindOpenChannel(channelString);
                        break;
                    case ChannelListItem.TYPE_PENDING_OPEN_CHANNEL:
                        bindPendingOpenChannel(channelString);
                        break;
                    case ChannelListItem.TYPE_WAITING_CLOSE_CHANNEL:
                        bindWaitingCloseChannel(channelString);
                        break;
                    case ChannelListItem.TYPE_PENDING_CLOSING_CHANNEL:
                        bindPendingCloseChannel(channelString);
                        break;
                    case ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL:
                        bindForceClosingChannel(channelString);
                        break;
                }
            } catch (InvalidProtocolBufferException | NullPointerException exception) {
                mBasePopWindow.dismiss();
            }

            //click create button
            // 点击create按钮
            rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mCreateChannelDialog = new CreateChannelDialog(mContext);
                    mCreateChannelDialog.show(balanceAmount, walletAddress, "");
//                    mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//                    mCreateChannelStepOnePopupWindow.show(view, balanceAmount, walletAddress, "");
                }
            });
            //click close button at bottom
            // 点击底部close
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

    private void bindOpenChannel(ByteString channelString) throws InvalidProtocolBufferException {
        LightningOuterClass.Channel channel = LightningOuterClass.Channel.parseFrom(channelString);
        mRemoteName.setText(Wallet.getInstance().getNodeAliasFromPubKey(channel.getRemotePubkey(), mContext));
        mRemotePubkey.setText(channel.getRemotePubkey());
        int assetId = channel.getAssetId();
        long mAssetId = assetId & 0xffffffffL;
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), mAssetLogo);
                mAssetUnit.setText(entity.getName());
                mLocalBalanceUnit.setText(entity.getName());
                mRemoteBalanceUnit.setText(entity.getName());
            }
        }
        if (channel.getAssetId() == 0) {
            long availableCapacity = channel.getBtcCapacity() - channel.getCommitFee();
            setBalances(channel.getLocalBalance(), channel.getRemoteBalance(), availableCapacity);
            // activity
            String activity = UtilFunctions.roundDouble(((double) (channel.getTotalSatoshisSent() + channel.getTotalSatoshisReceived()) / channel.getBtcCapacity() * 100), 2) + "%";
            mActivity.setText(activity);
            // local reserve amount
            mTotalSent.setText(channel.getLocalConstraints().getChanReserveSat() + " sat");
            // remote reserve amount
            mTotalReceived.setText(channel.getRemoteConstraints().getChanReserveSat() + " sat");
        } else {
            long availableCapacity = channel.getAssetCapacity() - channel.getCommitFee();
            setBalances(channel.getLocalAssetBalance(), channel.getRemoteAssetBalance(), availableCapacity);
            // activity
            String activity = UtilFunctions.roundDouble(((double) (channel.getTotalSatoshisSent() + channel.getTotalSatoshisReceived()) / channel.getAssetCapacity() * 100), 2) + "%";
            mActivity.setText(activity);
            // local reserve amount
            mTotalSent.setText(channel.getLocalConstraints().getChanReserveSat() + " unit");
            // remote reserve amount
            mTotalReceived.setText(channel.getRemoteConstraints().getChanReserveSat() + " unit");
        }
        mFundingTransaction.setText(channel.getChannelPoint().substring(0, channel.getChannelPoint().indexOf(':')));
        // register for channel close events and keep channel point for later comparison
        mChannelPoint = channel.getChannelPoint();
        showClosingButton(!channel.getActive(), channel.getCsvDelay(), channel.getAssetId());
        if (channel.getActive()) {
            mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_009b19_25);
        } else {
            mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_99000000_25);
        }
        mCreateTime.setText(DateUtils.dateFormat(channel.getCreateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        mAnotherInfo.setVisibility(View.VISIBLE);
        // time lock
        long timeLockInSeconds = channel.getLocalConstraints().getCsvDelay() * 10 * 60;
        String timeLock = String.valueOf(channel.getLocalConstraints().getCsvDelay()) + " (" + TimeFormatUtil.formattedDurationShort(timeLockInSeconds, mContext) + ")";
        mTimeLock.setText(timeLock);
    }

    private void bindPendingOpenChannel(ByteString channelString) throws InvalidProtocolBufferException {
        LightningOuterClass.PendingChannelsResponse.PendingOpenChannel pendingOpenChannel = LightningOuterClass.PendingChannelsResponse.PendingOpenChannel.parseFrom(channelString);
        setBasicInformation(pendingOpenChannel.getChannel().getAssetId(), pendingOpenChannel.getChannel().getRemoteNodePub(),
                pendingOpenChannel.getChannel().getRemoteNodePub(),
                pendingOpenChannel.getChannel().getChannelPoint(),
                pendingOpenChannel.getChannel().getCreateTime());
        mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_ec9a1e_25);
        if (pendingOpenChannel.getChannel().getAssetId() == 0) {
            setBalances(pendingOpenChannel.getChannel().getLocalBalance() / 1000, pendingOpenChannel.getChannel().getRemoteBalance() / 1000, pendingOpenChannel.getChannel().getBtcCapacity());
        } else {
            setBalances(pendingOpenChannel.getChannel().getLocalBalance(), pendingOpenChannel.getChannel().getRemoteBalance(), pendingOpenChannel.getChannel().getAssetCapacity());
        }
        mAnotherInfo.setVisibility(View.GONE);
    }

    private void bindWaitingCloseChannel(ByteString channelString) throws InvalidProtocolBufferException {
        LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel waitingCloseChannel = LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel.parseFrom(channelString);
        setBasicInformation(waitingCloseChannel.getChannel().getAssetId(), waitingCloseChannel.getChannel().getRemoteNodePub(),
                waitingCloseChannel.getChannel().getRemoteNodePub(),
                waitingCloseChannel.getChannel().getChannelPoint(),
                waitingCloseChannel.getChannel().getCreateTime());
        mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_ff0000_25);
        if (waitingCloseChannel.getChannel().getAssetId() == 0) {
            setBalances(waitingCloseChannel.getChannel().getLocalBalance() / 1000, waitingCloseChannel.getChannel().getRemoteBalance() / 1000, waitingCloseChannel.getChannel().getBtcCapacity());
        } else {
            setBalances(waitingCloseChannel.getChannel().getLocalBalance(), waitingCloseChannel.getChannel().getRemoteBalance(), waitingCloseChannel.getChannel().getAssetCapacity());
        }
        mAnotherInfo.setVisibility(View.GONE);
    }

    private void bindPendingCloseChannel(ByteString channelString) throws InvalidProtocolBufferException {
        LightningOuterClass.PendingChannelsResponse.ClosedChannel pendingCloseChannel = LightningOuterClass.PendingChannelsResponse.ClosedChannel.parseFrom(channelString);
        setBasicInformation(pendingCloseChannel.getChannel().getAssetId(), pendingCloseChannel.getChannel().getRemoteNodePub(),
                pendingCloseChannel.getChannel().getRemoteNodePub(),
                pendingCloseChannel.getChannel().getChannelPoint(),
                pendingCloseChannel.getChannel().getCreateTime());
        mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_ff0000_25);
        if (pendingCloseChannel.getChannel().getAssetId() == 0) {
            setBalances(pendingCloseChannel.getChannel().getLocalBalance() / 1000, pendingCloseChannel.getChannel().getRemoteBalance() / 1000, pendingCloseChannel.getChannel().getBtcCapacity());
        } else {
            setBalances(pendingCloseChannel.getChannel().getLocalBalance(), pendingCloseChannel.getChannel().getRemoteBalance(), pendingCloseChannel.getChannel().getAssetCapacity());
        }
        mAnotherInfo.setVisibility(View.GONE);
    }

    private void bindForceClosingChannel(ByteString channelString) throws InvalidProtocolBufferException {
        LightningOuterClass.PendingChannelsResponse.ForceClosedChannel forceClosedChannel = LightningOuterClass.PendingChannelsResponse.ForceClosedChannel.parseFrom(channelString);

        setBasicInformation(forceClosedChannel.getChannel().getAssetId(), forceClosedChannel.getChannel().getRemoteNodePub(),
                forceClosedChannel.getChannel().getRemoteNodePub(),
                forceClosedChannel.getChannel().getChannelPoint(),
                forceClosedChannel.getChannel().getCreateTime());
        mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_ff0000_25);
        if (forceClosedChannel.getChannel().getAssetId() == 0) {
            setBalances(forceClosedChannel.getChannel().getLocalBalance() / 1000, forceClosedChannel.getChannel().getRemoteBalance() / 1000, forceClosedChannel.getChannel().getBtcCapacity());
        } else {
            setBalances(forceClosedChannel.getChannel().getLocalBalance(), forceClosedChannel.getChannel().getRemoteBalance(), forceClosedChannel.getChannel().getAssetCapacity());
        }
        mAnotherInfo.setVisibility(View.GONE);
    }

    private void setBasicInformation(@NonNull int assetId, @NonNull String remoteNodePublicKey, @NonNull String remotePubKey, @NonNull String channelPoint, @NonNull long createTime) {
        mRemoteName.setText(Wallet.getInstance().getNodeAliasFromPubKey(remoteNodePublicKey, mContext));
        mRemotePubkey.setText(remotePubKey);
        long mAssetId = assetId & 0xffffffffL;
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), mAssetLogo);
                mAssetUnit.setText(entity.getName());
                mLocalBalanceUnit.setText(entity.getName());
                mRemoteBalanceUnit.setText(entity.getName());
            }
        }
        mFundingTransaction.setText(channelPoint.substring(0, channelPoint.indexOf(':')));
        mCreateTime.setText(DateUtils.dateFormat(createTime, DateUtils.YYYY_MM_DD_HH_MM_SS));
    }

    private void setBalances(long local, long remote, long capacity) {
        float localBarValue = (float) ((double) local / (double) capacity);
        float remoteBarValue = (float) ((double) remote / (double) capacity);
        mProgressBar.setProgress((int) (localBarValue * 100f));

        if (capacity == 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            mAssetAmount.setText(df.format(Double.parseDouble(String.valueOf(capacity)) / 100000000));
        } else {
            DecimalFormat df = new DecimalFormat("0.00######");
            mAssetAmount.setText(df.format(Double.parseDouble(String.valueOf(capacity)) / 100000000));
        }
        DecimalFormat df = new DecimalFormat("0.00######");
        mLocalBalance.setText(df.format(Double.parseDouble(String.valueOf(local)) / 100000000));
        mRemoteBalance.setText(df.format(Double.parseDouble(String.valueOf(remote)) / 100000000));
    }

    private void showClosingButton(boolean forceClose, int csvDelay, int assetId) {
        mCloseChannelParentLayout.setVisibility(View.VISIBLE);
        mCloseChannelParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCloseChannelIv.setVisibility(View.INVISIBLE);
                mCloseChannelLayout.setVisibility(View.VISIBLE);
            }
        });
        mCloseChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog.show();
                closeChannel(forceClose, csvDelay, assetId);
            }
        });
        mCloseChannelNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCloseChannelIv.setVisibility(View.VISIBLE);
                mCloseChannelLayout.setVisibility(View.GONE);
            }
        });
    }

    public void closeChannel(boolean force, int csvDelay, int assetId) {
        LightningOuterClass.ChannelPoint point = LightningOuterClass.ChannelPoint.newBuilder()
                .setFundingTxidStr(mChannelPoint.substring(0, mChannelPoint.indexOf(':')))
                .setOutputIndex(Character.getNumericValue(mChannelPoint.charAt(mChannelPoint.length() - 1)))
                .build();

        LightningOuterClass.CloseChannelRequest closeChannelRequest = LightningOuterClass.CloseChannelRequest.newBuilder()
                .setChannelPoint(point)
                .build();
        if (assetId == 0) {
            Obdmobile.closeChannel(closeChannelRequest.toByteArray(), new RecvStream() {
                @Override
                public void onError(Exception e) {
                    if (e.getMessage().equals("EOF")) {
                        return;
                    }
                    LogUtils.e(TAG, "------------------closeChannelOnError------------------" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (mBasePopWindow != null) {
                                mBasePopWindow.dismiss();
                            }
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            if (e.getMessage().toLowerCase().contains("offline")) {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close_offline));
                            } else if (e.getMessage().toLowerCase().contains("terminated")) {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close_timeout));
                            } else {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close));
                            }
                        }
                    });
                }

                @Override
                public void onResponse(byte[] bytes) {
                    try {
                        LightningOuterClass.CloseStatusUpdate resp = LightningOuterClass.CloseStatusUpdate.parseFrom(bytes);
                        LogUtils.e(TAG, "------------------closeChannelOnResponse------------------" + resp.getClosePending().getTxid());
                        if (resp.hasClosePending()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (assetId != 0) {
                                        // 存储txid
                                        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
                                        String txidListJson = txidSp.getString("txidListKey", "");
                                        if (StringUtils.isEmpty(txidListJson)) {
                                            txidList = new ArrayList<>();
                                            txidList.add(resp.getClosePending().getTxidStr());
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
                                            }.getType());
                                            txidList.add(resp.getClosePending().getTxidStr());
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        }
                                    }
                                    EventBus.getDefault().post(new CloseChannelEvent());
                                    if (mBasePopWindow != null) {
                                        mBasePopWindow.dismiss();
                                    }
                                    if (mLoadingDialog != null) {
                                        mLoadingDialog.dismiss();
                                    }
                                    mSendSuccessDialog.show(mContext.getString(R.string.channel_close_success));
                                }
                            });
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Obdmobile.ob_SafeBox_CloseChannel(closeChannelRequest.toByteArray(), new RecvStream() {
                @Override
                public void onError(Exception e) {
                    if (e.getMessage().equals("EOF")) {
                        return;
                    }
                    LogUtils.e(TAG, "------------------oBSafeBoxCloseChannelOnError------------------" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (mBasePopWindow != null) {
                                mBasePopWindow.dismiss();
                            }
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            if (e.getMessage().toLowerCase().contains("offline")) {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close_offline));
                            } else if (e.getMessage().toLowerCase().contains("terminated")) {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close_timeout));
                            } else {
                                mSendFailedDialog.show(mContext.getString(R.string.error_channel_close));
                            }
                        }
                    });
                }

                @Override
                public void onResponse(byte[] bytes) {
                    try {
                        LightningOuterClass.CloseStatusUpdate resp = LightningOuterClass.CloseStatusUpdate.parseFrom(bytes);
                        LogUtils.e(TAG, "------------------oBSafeBoxCloseChannelOnResponse------------------" + resp.getClosePending().getTxid());
                        if (resp.hasClosePending()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (assetId != 0) {
                                        // 存储txid
                                        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
                                        String txidListJson = txidSp.getString("txidListKey", "");
                                        if (StringUtils.isEmpty(txidListJson)) {
                                            txidList = new ArrayList<>();
                                            txidList.add(resp.getClosePending().getTxidStr());
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
                                            }.getType());
                                            txidList.add(resp.getClosePending().getTxidStr());
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        }
                                    }
                                    EventBus.getDefault().post(new CloseChannelEvent());
                                    if (mBasePopWindow != null) {
                                        mBasePopWindow.dismiss();
                                    }
                                    if (mLoadingDialog != null) {
                                        mLoadingDialog.dismiss();
                                    }
                                    mSendSuccessDialog.show(mContext.getString(R.string.channel_close_success));
                                }
                            });
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
        if (mCreateChannelDialog != null) {
            mCreateChannelDialog.release();
        }
    }
}
