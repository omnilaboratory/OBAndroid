package com.omni.wallet.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.OpenChannelEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.lightning.LightningNodeUri;
import com.omni.wallet.lightning.LightningParser;
import com.omni.wallet.ui.activity.ScanChannelActivity;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.BackupUtils;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.view.popupwindow.SelectAssetUnitPopupWindow;
import com.omni.wallet.view.popupwindow.SelectSpeedPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

/**
 * 汉: 创建通道的弹窗
 * En: CreateChannelDialog
 * author: guoyalei
 * date: 2022/12/5
 */
public class CreateChannelDialog implements Wallet.ScanChannelListener {
    private static final String TAG = CreateChannelDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    TextView localEdit;
    TextView channelAmountTv;
    TextView channelFeeTv;
    TextView feePerByteTv;
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;
    SelectAssetUnitPopupWindow mSelectAssetUnitPopupWindow;
    String nodePubkey;
    long assetId = 0;
    int time;
    long feeStr;
    String assetBalance;
    String assetBalanceMax;
    // Not necessary
    long mBalanceAmount;
    String mWalletAddress;
    LoadingDialog mLoadingDialog;

    public CreateChannelDialog(Context context) {
        this.mContext = context;
    }

    public void show(long balanceAmount, String walletAddress, String pubKey) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_popupwindow_create_channel_stepone)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        mBalanceAmount = balanceAmount;
        mWalletAddress = walletAddress;
        Wallet.getInstance().registerScanChannelListener(this);
        mLoadingDialog = new LoadingDialog(mContext);
        if (!StringUtils.isEmpty(pubKey)) {
            nodePubkey = pubKey;
            mAlertDialog.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
            mAlertDialog.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
            showStepTwo();
        } else {
            nodePubkey = "02224240424337d37c7aea165529adfeff90f1c1bf8b2d21fb44a1a690fff1ef23@43.138.107.248:9735";
            showStepOne();
        }
        /**
         * @描述： 点击 cancel
         * @desc: click cancel button
         */
        mAlertDialog.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
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

    private void showStepOne() {
        localEdit = mAlertDialog.findViewById(R.id.edit_local);
        EditText waterDripEdit = mAlertDialog.findViewById(R.id.edit_water_drip);
        EditText remoteEdit = mAlertDialog.findViewById(R.id.edit_remote);
        localEdit.setText(nodePubkey);
        waterDripEdit.setText(nodePubkey);
        remoteEdit.setText(nodePubkey);
        /**
         * 点击默认节点
         * @desc: click default addr
         */
        mAlertDialog.findViewById(R.id.layout_defaylt_addr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodePubkey = localEdit.getText().toString();
                mAlertDialog.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
                showStepTwo();
            }
        });
        /**
         * @描述： 扫描二维码
         * @desc: scan qrcode
         */
        mAlertDialog.findViewById(R.id.layout_scan_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
//                        mBasePopWindow.dismiss();
                        Intent intent = new Intent(mContext, ScanChannelActivity.class);
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
         * @描述： 手动填写
         * @desc: fill in
         */
        mAlertDialog.findViewById(R.id.layout_fill_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodePubkey = "";
                mAlertDialog.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
                showStepTwo();
            }
        });
    }

    private void showStepTwo() {
        EditText vaildPubkeyEdit = mAlertDialog.findViewById(R.id.edit_vaild_pubkey);
        TextView nodeNameTv = mAlertDialog.findViewById(R.id.tv_node_name);
        TextView validPubkeyTv = mAlertDialog.findViewById(R.id.tv_valid_pubkey);
        EditText channelAmountEdit = mAlertDialog.findViewById(R.id.edit_channel_amount);
        channelAmountTv = mAlertDialog.findViewById(R.id.tv_channel_amount);
        channelFeeTv = mAlertDialog.findViewById(R.id.tv_channel_fee);
        feePerByteTv = mAlertDialog.findViewById(R.id.tv_fee_per_byte);
        Button amountUnitButton = mAlertDialog.findViewById(R.id.btn_amount_unit);
        Button speedButton = mAlertDialog.findViewById(R.id.btn_speed);

        vaildPubkeyEdit.setText(nodePubkey);
        vaildPubkeyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    LightningNodeUri nodeUri = LightningParser.parseNodeUri(s.toString());
                    if (nodeUri == null) {
                        validPubkeyTv.setText("Invalid pubkey");
                        validPubkeyTv.setTextColor(Color.parseColor("#ffE51414"));
                    } else {
                        validPubkeyTv.setText("Valid Pubkey");
                        validPubkeyTv.setTextColor(Color.parseColor("#ff00BA6C"));
                    }
                }
            }
        });
        nodeNameTv.setText(Wallet.getInstance().getNodeAliasFromPubKey(nodePubkey, mContext));
        fetchWalletBalance();
        channelAmountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isEmpty(s.toString())) {
                    if (assetId == 0) {
                        estimateOnChainFee((long) (Double.parseDouble(s.toString()) * 100000000), time);
                    } else {
                        estimateOnChainFee(20000, time);
                    }
                } else {
                    estimateOnChainFee(0, time);
                }
            }
        });
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectSpeedPopupWindow = new SelectSpeedPopupWindow(mContext);
                mSelectSpeedPopupWindow.setOnItemClickCallback(new SelectSpeedPopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_slow:
                                speedButton.setText(R.string.slow);
                                time = 1; // 10 Minutes
                                if (!StringUtils.isEmpty(channelAmountEdit.getText().toString())) {
                                    if (assetId == 0) {
                                        estimateOnChainFee((long) (Double.parseDouble(channelAmountEdit.getText().toString()) * 100000000), time);
                                    } else {
                                        estimateOnChainFee(20000, time);
                                    }
                                }
                                break;
                            case R.id.tv_medium:
                                speedButton.setText(R.string.medium);
                                time = 6 * 6; // 6 Hours
                                if (!StringUtils.isEmpty(channelAmountEdit.getText().toString())) {
                                    if (assetId == 0) {
                                        estimateOnChainFee((long) (Double.parseDouble(channelAmountEdit.getText().toString()) * 100000000), time);
                                    } else {
                                        estimateOnChainFee(20000, time);
                                    }
                                }
                                break;
                            case R.id.tv_fast:
                                speedButton.setText(R.string.fast);
                                time = 6 * 24; // 24 Hours
                                if (!StringUtils.isEmpty(channelAmountEdit.getText().toString())) {
                                    if (assetId == 0) {
                                        estimateOnChainFee((long) (Double.parseDouble(channelAmountEdit.getText().toString()) * 100000000), time);
                                    } else {
                                        estimateOnChainFee(20000, time);
                                    }
                                }
                                break;
                        }
                    }
                });
                mSelectSpeedPopupWindow.show(v);
            }
        });
        amountUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectAssetUnitPopupWindow = new SelectAssetUnitPopupWindow(mContext);
                mSelectAssetUnitPopupWindow.setOnItemClickCallback(new SelectAssetUnitPopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view, ListAssetItemEntity item) {
                        if (item.getPropertyid() == 0) {
                            amountUnitButton.setText("BTC");
                            feePerByteTv.setText(R.string.satoshi_per_byte);
                        } else {
                            amountUnitButton.setText("Doallar");
                            feePerByteTv.setText(R.string.unit_per_byte);
                        }
                        assetId = item.getPropertyid();
                        if (item.getAmount() == 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            assetBalanceMax = df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000);
                        } else {
                            DecimalFormat df = new DecimalFormat("0.00######");
                            assetBalanceMax = df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000);
                        }
                        channelAmountTv.setText(assetBalanceMax);
                        if (!StringUtils.isEmpty(channelAmountEdit.getText().toString())) {
                            if (assetId == 0) {
                                estimateOnChainFee((long) (Double.parseDouble(channelAmountEdit.getText().toString()) * 100000000), time);
                            } else {
                                estimateOnChainFee(20000, time);
                            }
                        }
                    }
                });
                mSelectAssetUnitPopupWindow.show(v);
            }
        });
        /**
         * @描述： 点击back
         * @desc: click back button
         */
        mAlertDialog.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodePubkey = "02224240424337d37c7aea165529adfeff90f1c1bf8b2d21fb44a1a690fff1ef23@43.138.107.248:9735";
                mAlertDialog.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.GONE);
                showStepOne();
            }
        });
        /**
         * @描述： 点击create
         * @desc: click create button
         */
        mAlertDialog.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // values from LND
                long minSendAmount = 20000;
                long maxSendAmount = 16777215;
                nodePubkey = vaildPubkeyEdit.getText().toString();
                assetBalance = channelAmountEdit.getText().toString();
                LightningNodeUri nodeUri = LightningParser.parseNodeUri(nodePubkey);
                if (nodeUri == null) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.enter_valid_node_pubkey));
                    return;
                }
                if (StringUtils.isEmpty(nodePubkey)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.enter_node_pubkey));
                    return;
                }
                if (StringUtils.isEmpty(assetBalance)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.create_invoice_amount));
                    return;
                }
                if (assetBalance.equals("0")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.amount_greater_than_0));
                    return;
                }

//                if (Long.parseLong(assetBalance) - minSendAmount < 0) {
//                    ToastUtils.showToast(mContext, mContext.getString(R.string.minimum_input));
//                    return;
//                }

//                if (Long.parseLong(assetBalance) - maxSendAmount > 0) {
//                    ToastUtils.showToast(mContext, mContext.getString(R.string.maximum_input));
//                    return;
//                }

                if ((Double.parseDouble(assetBalance) * 100000000) - (Double.parseDouble(assetBalanceMax) * 100000000) > 0) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.credit_is_running_low));
                    return;
                }
                mLoadingDialog.show();
                // 先链接后再开通通道
                connectPeer(nodePubkey, mBalanceAmount, mWalletAddress);
            }
        });
    }

    /**
     * Opening transaction channel
     * 开通交易通道
     */
    private void openChannelConnected(String pubkey, long balanceAmount, String walletAddress) {
        byte[] nodeKeyBytes = hexStringToByteArray(pubkey);
        LightningOuterClass.OpenChannelRequest openChannelRequest;
        if (assetId == 0) {
            LogUtils.e(TAG, "==========33333==========");
            openChannelRequest = LightningOuterClass.OpenChannelRequest.newBuilder()
                    .setNodePubkey(ByteString.copyFrom(nodeKeyBytes))
                    .setTargetConf(Integer.parseInt(channelFeeTv.getText().toString()))
                    .setPrivate(false)
                    .setLocalFundingBtcAmount((long) (Double.parseDouble(assetBalance) * 100000000))
                    .setPushBtcSat((long) ((Double.parseDouble(assetBalance) * 100000000) / 2))
                    .setAssetId((int) assetId)
                    .build();
        } else {
            LogUtils.e(TAG, "==========44444==========");
            openChannelRequest = LightningOuterClass.OpenChannelRequest.newBuilder()
                    .setNodePubkey(ByteString.copyFrom(nodeKeyBytes))
                    .setTargetConf(Integer.parseInt(channelFeeTv.getText().toString()))
                    .setPrivate(false)
                    .setLocalFundingBtcAmount(20000)
                    .setLocalFundingAssetAmount((long) (Double.parseDouble(assetBalance) * 100000000))
                    .setPushAssetSat((long) ((Double.parseDouble(assetBalance) * 100000000) / 2))
                    .setAssetId((int) assetId)
                    .build();
        }
        LogUtils.e(TAG, "==========55555==========" + pubkey);
        Obdmobile.oB_OpenChannel(openChannelRequest.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        LogUtils.e(TAG, "Error opening channel: " + e.getMessage());
                        if (e.getMessage().toLowerCase().contains("pending channels exceed maximum")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open_pending_max));
                        } else if (e.getMessage().toLowerCase().contains("terminated")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open_timeout));
                        } else if (e.getMessage().toLowerCase().contains("funding amount is too large")) {
                            ToastUtils.showToast(mContext, e.getMessage());
                        } else {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open));
                        }
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.OpenStatusUpdate resp = LightningOuterClass.OpenStatusUpdate.parseFrom(bytes);
                            LogUtils.e(TAG, "Open channel update: " + resp.getUpdateCase().getNumber());
                            EventBus.getDefault().post(new OpenChannelEvent());
                            mLoadingDialog.dismiss();
                            mAlertDialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                            bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, walletAddress);
                            bundle.putString(ChannelsActivity.KEY_PUBKEY, User.getInstance().getFromPubKey(mContext));
                            Intent intent = new Intent(mContext, ChannelsActivity.class);
                            BackupUtils.getInstance().BackupChannelToFile(mContext);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void connectPeer(String pubkey, long balanceAmount, String walletAddress) {
        LightningNodeUri nodeUri = LightningParser.parseNodeUri(pubkey);
//        if (nodeUri.getHost() == null || nodeUri.getHost().isEmpty()) {
//            LogUtils.e(TAG, "Host info missing. Trying to fetch host info to connect peer...");
//            fetchNodeInfoToConnectPeer(pubkey, balanceAmount, walletAddress);
//            return;
//        }
        LogUtils.e(TAG, "==========11111==========" + nodeUri.getHost());
        LogUtils.e(TAG, "==========22222==========" + nodeUri.getPubKey());

        LightningOuterClass.LightningAddress lightningAddress = LightningOuterClass.LightningAddress.newBuilder()
                .setHostBytes(ByteString.copyFrom(nodeUri.getHost().getBytes(StandardCharsets.UTF_8)))
                .setPubkeyBytes(ByteString.copyFrom(nodeUri.getPubKey().getBytes(StandardCharsets.UTF_8))).build();
        LightningOuterClass.ConnectPeerRequest connectPeerRequest = LightningOuterClass.ConnectPeerRequest.newBuilder().setAddr(lightningAddress).build();
        Obdmobile.connectPeer(connectPeerRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(TAG, "Error connecting to peer: " + e.getMessage());
                        if (e.getMessage().toLowerCase().contains("refused")) {
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_refused));
                        } else if (e.getMessage().toLowerCase().contains("self")) {
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_self));
                        } else if (e.getMessage().toLowerCase().contains("terminated")) {
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_timeout));
                        } else if (e.getMessage().toLowerCase().contains("already connected to peer")) {
                            openChannelConnected(nodeUri.getPubKey(), balanceAmount, walletAddress);
                        } else {
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer));
                        }
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "Successfully connected to peer, trying to open channel...");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        openChannelConnected(nodeUri.getPubKey(), balanceAmount, walletAddress);
                    }
                });
            }
        });
    }

    public void fetchNodeInfoToConnectPeer(String pubkey, long balanceAmount, String walletAddress) {
        LightningOuterClass.NodeInfoRequest nodeInfoRequest = LightningOuterClass.NodeInfoRequest.newBuilder()
                .setPubKey(pubkey)
                .build();
        Obdmobile.getNodeInfo(nodeInfoRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "Fetching host info failed. Exception in get node info (" + nodePubkey + ") request task: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.NodeInfo nodeInfo = LightningOuterClass.NodeInfo.parseFrom(bytes);
                            if (nodeInfo.getNode().getAddressesCount() > 0) {
                                String tempUri = pubkey + "@" + nodeInfo.getNode().getAddresses(0).getAddr();
                                LightningNodeUri nodeUriWithHost = LightningParser.parseNodeUri(tempUri);
                                if (nodeUriWithHost != null) {
                                    LogUtils.e(TAG, "Host info successfully fetched. NodeUriWithHost: " + nodeUriWithHost.getAsString());
                                    connectPeer(tempUri, balanceAmount, walletAddress);
                                } else {
                                    mLoadingDialog.dismiss();
                                    LogUtils.e(TAG, "Failed to parse nodeUri");
                                    ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
                                }
                            } else {
                                mLoadingDialog.dismiss();
                                LogUtils.e(TAG, "Node Info does not contain any addresses.");
                                ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private void estimateOnChainFee(long amount, int targetConf) {
        String address;
        switch (User.getInstance().getNetwork(mContext)) {
            case "testnet":
                address = "tb1qw508d6qejxtdg4y5r3zarvary0c5xw7kxpjzsx";
                break;
            case "regtest":
                address = "bcrt1qsdtedxkv2mdgtstsv9fhyq03dsv9dyu5qmeh2w";
                break;
            default:
                address = "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"; // Mainnet
        }
        // let LND estimate fee
        LightningOuterClass.EstimateFeeRequest asyncEstimateFeeRequest = LightningOuterClass.EstimateFeeRequest.newBuilder()
                .putAddrToAmount(mWalletAddress, amount)
                .setTargetConf(targetConf)
                .build();
        Obdmobile.estimateFee(asyncEstimateFeeRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------asyncEstimateFeeOnError------------------" + e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        feeStr = 0;
                        channelFeeTv.setText(feeStr + "");
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.EstimateFeeResponse resp = LightningOuterClass.EstimateFeeResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------asyncEstimateFeeOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            feeStr = resp.getFeeSat();
                            channelFeeTv.setText(feeStr + "");
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create a new wallet address first, and then request the interface of each asset balance list
     * 先创建新的钱包地址后再去请求各资产余额列表的接口
     */
    public void fetchWalletBalance() {
        LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_WalletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------walletBalanceByAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------walletBalanceByAddressOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (resp.getConfirmedBalance() == 0) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getConfirmedBalance())) / 100000000);
                            } else {
                                DecimalFormat df = new DecimalFormat("0.00######");
                                assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getConfirmedBalance())) / 100000000);
                            }
                            channelAmountTv.setText(assetBalanceMax);
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onScanChannelUpdated(String result) {
        nodePubkey = result;
        mAlertDialog.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
        mAlertDialog.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
        showStepTwo();
    }

    public void release() {
        Wallet.getInstance().unregisterScanChannelListener(this);
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
