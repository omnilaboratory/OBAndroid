package com.omni.wallet.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.lightning.LightningNodeUri;
import com.omni.wallet.lightning.LightningParser;
import com.omni.wallet.ui.activity.ScanActivity;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

/**
 * CreateChannelStepOne的弹窗
 */
public class CreateChannelStepOnePopupWindow {
    private static final String TAG = CreateChannelStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    TextView channelFeeTv;
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;
    SelectAssetUnitPopupWindow mSelectAssetUnitPopupWindow;
    String nodePubkey;
    long assetId;
    int time;
    long feeStr;
    String assetBalance;
    String assetBalanceMax;
    // Not necessary
    long mBalanceAmount;
    String mWalletAddress;

    public CreateChannelStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, long balanceAmount, String walletAddress, String pubKey) {
        if (mBasePopWindow == null) {
            nodePubkey = pubKey;
            mBalanceAmount = balanceAmount;
            mWalletAddress = walletAddress;

            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_channel_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            fetchWalletBalance();
            showStepOne(rootView);
            /**
             * @描述： 点击 cancel
             * @desc: click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
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
        EditText localEdit = rootView.findViewById(R.id.edit_local);
        EditText waterDripEdit = rootView.findViewById(R.id.edit_water_drip);
        EditText remoteEdit = rootView.findViewById(R.id.edit_remote);
        localEdit.setText(nodePubkey);
        waterDripEdit.setText(nodePubkey);
        remoteEdit.setText(nodePubkey);
        /**
         * @描述： 扫描二维码
         * @desc: scan qrcode
         */
        rootView.findViewById(R.id.layout_scan_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        mBasePopWindow.dismiss();
                        Intent intent = new Intent(mContext, ScanActivity.class);
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
        rootView.findViewById(R.id.layout_fill_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
                rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
                showStepTwo(rootView);
            }
        });
    }

    private void showStepTwo(View rootView) {
        EditText vaildPubkeyEdit = rootView.findViewById(R.id.edit_vaild_pubkey);
        EditText channelAmountEdit = rootView.findViewById(R.id.edit_channel_amount);
        TextView channelAmountTv = rootView.findViewById(R.id.tv_channel_amount);
        channelFeeTv = rootView.findViewById(R.id.tv_channel_fee);
        Button amountUnitButton = rootView.findViewById(R.id.btn_amount_unit);
        Button speedButton = rootView.findViewById(R.id.btn_speed);

//        vaildPubkeyEdit.setText(nodePubkey);
        channelAmountTv.setText(assetBalanceMax);
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
                    estimateOnChainFee(Long.parseLong(s.toString()), time);
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
                                estimateOnChainFee(Long.parseLong(channelAmountEdit.getText().toString()), time);
                                break;
                            case R.id.tv_medium:
                                speedButton.setText(R.string.medium);
                                time = 6 * 6; // 6 Hours
                                estimateOnChainFee(Long.parseLong(channelAmountEdit.getText().toString()), time);
                                break;
                            case R.id.tv_fast:
                                speedButton.setText(R.string.fast);
                                time = 6 * 24; // 24 Hours
                                estimateOnChainFee(Long.parseLong(channelAmountEdit.getText().toString()), time);
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
                        } else {
                            amountUnitButton.setText("USDT");
                        }
                        assetId = item.getPropertyid();
                        assetBalanceMax = item.getAmount() + "";
                        channelAmountTv.setText(assetBalanceMax);
                    }
                });
                mSelectAssetUnitPopupWindow.show(v);
            }
        });
        /**
         * @描述： 点击back
         * @desc: click back button
         */
        rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.GONE);
            }
        });
        /**
         * @描述： 点击create
         * @desc: click create button
         */
        rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodePubkey = vaildPubkeyEdit.getText().toString();
                assetBalance = channelAmountEdit.getText().toString();
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
                if (Long.parseLong(assetBalance) - Long.parseLong(assetBalanceMax) > 0) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.credit_is_running_low));
                    return;
                }
                Obdmobile.listPeers(LightningOuterClass.ListPeersRequest.newBuilder().build().toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "Error listing peers request: " + e.getMessage());
                        if (e.getMessage().toLowerCase().contains("terminated")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_get_peers_timeout));
                        } else {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_get_peers));
                        }
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        if (bytes == null) {
                            connectPeer(mBalanceAmount, mWalletAddress);
                        } else {
                            try {
                                LightningOuterClass.ListPeersResponse resp = LightningOuterClass.ListPeersResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------listPeersonResponse------------------" + resp.toString());
                                boolean connected = false;
                                for (LightningOuterClass.Peer node : resp.getPeersList()) {
                                    if (node.getPubKey().equals(nodePubkey)) {
                                        connected = true;
                                        break;
                                    }
                                }
                                if (connected) {
                                    LogUtils.e(TAG, "Already connected to peer, trying to open channel...");
                                    openChannelConnected(mBalanceAmount, mWalletAddress);
                                } else {
                                    LogUtils.e(TAG, "Not connected to peer, trying to connect...");
                                    connectPeer(mBalanceAmount, mWalletAddress);
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Opening transaction channel
     * 开通交易通道
     */
    private void openChannelConnected(long balanceAmount, String walletAddress) {
        byte[] nodeKeyBytes = hexStringToByteArray(nodePubkey);
        LightningOuterClass.OpenChannelRequest openChannelRequest = LightningOuterClass.OpenChannelRequest.newBuilder()
                .setNodePubkey(ByteString.copyFrom(nodeKeyBytes))
                .setTargetConf(Integer.parseInt(channelFeeTv.getText().toString()))
                .setPrivate(false)
                .setLocalFundingBtcAmount(Long.parseLong(assetBalance))
                .setLocalFundingAssetAmount(Long.parseLong(assetBalance))
                .setAssetId((int) assetId)
                .build();
        Obdmobile.openChannel(openChannelRequest.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "Error opening channel: " + e.getMessage());
                if (e.getMessage().toLowerCase().contains("pending channels exceed maximum")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open_pending_max));
                } else if (e.getMessage().toLowerCase().contains("terminated")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open_timeout));
                } else {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.error_channel_open));
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.OpenStatusUpdate resp = LightningOuterClass.OpenStatusUpdate.parseFrom(bytes);
                    LogUtils.e(TAG, "Open channel update: " + resp.getUpdateCase().getNumber());
                    mBasePopWindow.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                    bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, walletAddress);
                    bundle.putString(ChannelsActivity.KEY_PUBKEY, nodePubkey);
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    mContext.startActivity(intent, bundle);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void connectPeer(long balanceAmount, String walletAddress) {
        LightningNodeUri nodeUri = LightningParser.parseNodeUri(nodePubkey);
        if (nodeUri.getHost() == null || nodeUri.getHost().isEmpty()) {
            LogUtils.d(TAG, "Host info missing. Trying to fetch host info to connect peer...");
            fetchNodeInfoToConnectPeer(balanceAmount, walletAddress);
            return;
        }

        LightningOuterClass.LightningAddress lightningAddress = LightningOuterClass.LightningAddress.newBuilder()
                .setHostBytes(ByteString.copyFrom(nodeUri.getHost().getBytes(StandardCharsets.UTF_8)))
                .setPubkeyBytes(ByteString.copyFrom(nodeUri.getPubKey().getBytes(StandardCharsets.UTF_8))).build();
        LightningOuterClass.ConnectPeerRequest connectPeerRequest = LightningOuterClass.ConnectPeerRequest.newBuilder().setAddr(lightningAddress).build();
        Obdmobile.connectPeer(connectPeerRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        LogUtils.e(TAG, "Error connecting to peer: " + e.getMessage());
                        if (e.getMessage().toLowerCase().contains("refused")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_refused));
                        } else if (e.getMessage().toLowerCase().contains("self")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_self));
                        } else if (e.getMessage().toLowerCase().contains("terminated")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_timeout));
                        } else {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer));
                        }
                        Looper.loop();
                    }
                }).start();
            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "Successfully connected to peer, trying to open channel...");
                openChannelConnected(balanceAmount, walletAddress);
            }
        });
    }

    public void fetchNodeInfoToConnectPeer(long balanceAmount, String walletAddress) {
        LightningOuterClass.NodeInfoRequest nodeInfoRequest = LightningOuterClass.NodeInfoRequest.newBuilder()
                .setPubKey(nodePubkey)
                .build();
        Obdmobile.getNodeInfo(nodeInfoRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "Fetching host info failed. Exception in get node info (" + nodePubkey + ") request task: " + e.getMessage());
                ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.NodeInfo nodeInfo = LightningOuterClass.NodeInfo.parseFrom(bytes);
                    if (nodeInfo.getNode().getAddressesCount() > 0) {
                        String tempUri = nodePubkey + "@" + nodeInfo.getNode().getAddresses(0).getAddr();
                        LightningNodeUri nodeUriWithHost = LightningParser.parseNodeUri(tempUri);
                        if (nodeUriWithHost != null) {
                            LogUtils.e(TAG, "Host info successfully fetched. NodeUriWithHost: " + nodeUriWithHost.getAsString());
                            connectPeer(balanceAmount, walletAddress);
                        } else {
                            LogUtils.e(TAG, "Failed to parse nodeUri");
                            ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
                        }
                    } else {
                        LogUtils.e(TAG, "Node Info does not contain any addresses.");
                        ToastUtils.showToast(mContext, mContext.getString(R.string.error_connect_peer_no_host));
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
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
                .putAddrToAmount(address, amount)
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
        LightningOuterClass.NewAddressRequest asyncNewAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder()
                .setTypeValue(2)
                .build();
        Obdmobile.newAddress(asyncNewAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------newAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.NewAddressResponse addressResp = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------newAddressOnResponse-----------------" + addressResp.getAddress());
                    LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                            .setAddress(addressResp.getAddress())
                            .build();
                    Obdmobile.walletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
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
                                assetBalanceMax = resp.getTotalBalance() + "";
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

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
