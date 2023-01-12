package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.entity.event.LockEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.WalletState;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import lnrpc.Stateservice;
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
    LoadingDialog mLoadingDialog;

    public MenuPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view, long balanceAmount, String walletAddress, String pubKey) {
        if (mMenuPopWindow == null) {
            mMenuPopWindow = new BasePopWindow(mContext);
            View rootView = mMenuPopWindow.setContentView(R.layout.layout_popupwindow_menu);
            mMenuPopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mMenuPopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);

            mLoadingDialog = new LoadingDialog(mContext);
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
            // profile
            rootView.findViewById(R.id.layout_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(mContext, "Not yet open, please wait");
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
                    mLoadingDialog.show();
                    startNode();
//                    LightningOuterClass.StopRequest stopRequest = LightningOuterClass.StopRequest.newBuilder().build();
//                    Obdmobile.stopDaemon(stopRequest.toByteArray(), new Callback() {
//                        @Override
//                        public void onError(Exception e) {
//                            LogUtils.e(TAG, "------------------stopDaemonOnError------------------" + e.getMessage());
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mMenuPopWindow.dismiss();
//                                    mLoadingDialog.dismiss();
//                                    ToastUtils.showToast(mContext, e.getMessage());
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onResponse(byte[] bytes) {
//                            LogUtils.e(TAG, "------------------stopDaemonOnResponse-----------------");
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    EventBus.getDefault().post(new LoginOutEvent());
//                                    mMenuPopWindow.dismiss();
//                                    mLoadingDialog.dismiss();
//                                }
//                            });
//                        }
//                    });
                }
            });
            // lock
            rootView.findViewById(R.id.layout_lock).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new LockEvent());
                }
            });
            if (mMenuPopWindow.isShowing()) {
                return;
            }
            mMenuPopWindow.showAsDropDown(view);
        }
    }

    public void startNode() {
        Obdmobile.start("--lnddir=" + mContext.getApplicationContext().getExternalCacheDir() + ConstantInOB.neutrinoRegTestConfig, new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------startOnError------------------" + e.getMessage());
                if (e.getMessage().contains("lnd already started")) {
                    Stateservice.GetStateRequest getStateRequest = Stateservice.GetStateRequest.newBuilder().build();
                    Obdmobile.getState(getStateRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------getStateOnError------------------" + e.getMessage());
                            startNode();
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            if (bytes == null) {
                                EventBus.getDefault().post(new LockEvent());
                                mMenuPopWindow.dismiss();
                                mLoadingDialog.dismiss();
                                return;
                            }
                            try {
                                Stateservice.GetStateResponse getStateResponse = Stateservice.GetStateResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------getStateOnResponse------------------" + getStateResponse);
                                Stateservice.WalletState state = getStateResponse.getState();
                                Log.e(TAG, state.toString());
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (state) {
                                            case LOCKED:
                                                EventBus.getDefault().post(new LockEvent());
                                                mMenuPopWindow.dismiss();
                                                mLoadingDialog.dismiss();
                                                break;
                                            case UNLOCKED:
                                            case RPC_ACTIVE:
                                            case SERVER_ACTIVE:
                                                mMenuPopWindow.dismiss();
                                                mLoadingDialog.dismiss();
                                                break;
                                        }
                                    }
                                });
                            } catch (InvalidProtocolBufferException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } else if (e.getMessage().equals("unable to start server: unable to unpack single backups: chacha20poly1305: message authentication failed")) {
                    EventBus.getDefault().post(new LockEvent());
                    mMenuPopWindow.dismiss();
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "------------------startOnSuccess------------------");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        subscribeWalletState();
                    }
                });
            }
        });
    }

    public void subscribeWalletState() {
        WalletState.WalletStateCallback walletStateCallback = walletState -> {
            switch (walletState) {
                case 0:
                case 255:
                case 1:
                    EventBus.getDefault().post(new LockEvent());
                    mMenuPopWindow.dismiss();
                    mLoadingDialog.dismiss();
                    break;
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
        WalletState.getInstance().subscribeWalletState(mContext);
    }

    public void release() {
        if (mMenuPopWindow != null) {
            mMenuPopWindow.dismiss();
            mMenuPopWindow = null;
        }
    }
}