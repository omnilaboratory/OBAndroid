package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
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