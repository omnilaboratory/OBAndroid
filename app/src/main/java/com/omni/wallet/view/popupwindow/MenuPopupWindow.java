package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * popup window for Menu
 * Menu的弹窗
 */
public class MenuPopupWindow {
    private static final String TAG = MenuPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mMenuPopWindow;

    public MenuPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view, long balanceAmount, String walletAddress, String pubKey) {
        if (mMenuPopWindow == null) {
            mMenuPopWindow = new BasePopWindow(mContext);
            View rootView = mMenuPopWindow.setContentView(R.layout.layout_popupwindow_menu);
            mMenuPopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mMenuPopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

            ImageView vectorMainnetIv = rootView.findViewById(R.id.iv_network_vector_mainnet);
            ImageView mainnetIv = rootView.findViewById(R.id.iv_network_mainnet);
            ImageView vectorTestnetIv = rootView.findViewById(R.id.iv_network_vector_testnet);
            ImageView testnetIv = rootView.findViewById(R.id.iv_network_testnet);
            ImageView vectorRegtestIv = rootView.findViewById(R.id.iv_network_vector_regtest);
            ImageView regtestIv = rootView.findViewById(R.id.iv_network_regtest);

            // 网络类型
            // Network type
            if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                vectorMainnetIv.setVisibility(View.INVISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorTestnetIv.setVisibility(View.VISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
                vectorRegtestIv.setVisibility(View.INVISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
            } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                vectorMainnetIv.setVisibility(View.INVISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorTestnetIv.setVisibility(View.INVISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorRegtestIv.setVisibility(View.VISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
            } else if (User.getInstance().getNetwork(mContext).equals("mainnet")) {
                vectorMainnetIv.setVisibility(View.VISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
                vectorTestnetIv.setVisibility(View.INVISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorRegtestIv.setVisibility(View.INVISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
            }

            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.dismiss();
                }
            });
            rootView.findViewById(R.id.layout_son).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.isShowing();
                }
            });
            rootView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.dismiss();
                }
            });
            // channel manage
            rootView.findViewById(R.id.layout_channel_manage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                    bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, walletAddress);
                    bundle.putString(ChannelsActivity.KEY_PUBKEY, pubKey);
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    mContext.startActivity(intent, bundle);
                }
            });
            // node_info
            rootView.findViewById(R.id.layout_node_info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.dismiss();
                    NodeInfoPopupWindow mNodeInfoPopupWindow = new NodeInfoPopupWindow(mContext);
                    mNodeInfoPopupWindow.show(view, pubKey);
                }
            });
            // disconnect
            rootView.findViewById(R.id.layout_disconnect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LightningOuterClass.DisconnectPeerRequest disconnectPeerRequest = LightningOuterClass.DisconnectPeerRequest.newBuilder()
                            .setPubKey(pubKey)
                            .build();
                    Obdmobile.disconnectPeer(disconnectPeerRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------disconnectPeerOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.DisconnectPeerResponse resp = LightningOuterClass.DisconnectPeerResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------disconnectPeerOnResponse-----------------" + resp);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            if (mMenuPopWindow.isShowing()) {
                return;
            }
            mMenuPopWindow.showAsDropDown(view);
        }
    }

    public void release() {
        if (mMenuPopWindow != null) {
            mMenuPopWindow.dismiss();
            mMenuPopWindow = null;
        }
    }
}