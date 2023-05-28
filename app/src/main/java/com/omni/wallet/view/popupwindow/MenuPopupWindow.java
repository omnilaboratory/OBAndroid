package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.entity.event.BackUpEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.dialog.NewVersionDialog;
import com.omni.wallet.view.dialog.UnlockDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

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
            RelativeLayout mainnetLayout = rootView.findViewById(R.id.layout_network_mainnet);
            RelativeLayout testnetLayout = rootView.findViewById(R.id.layout_network_testnet);
            RelativeLayout regtestLayout = rootView.findViewById(R.id.layout_network_regtest);

            // 网络类型
            // Network type
            if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                vectorMainnetIv.setVisibility(View.INVISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorTestnetIv.setVisibility(View.VISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
                vectorRegtestIv.setVisibility(View.INVISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                mainnetLayout.setVisibility(View.GONE);
                testnetLayout.setVisibility(View.VISIBLE);
                regtestLayout.setVisibility(View.GONE);
            } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                vectorMainnetIv.setVisibility(View.INVISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorTestnetIv.setVisibility(View.INVISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorRegtestIv.setVisibility(View.VISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
                mainnetLayout.setVisibility(View.GONE);
                testnetLayout.setVisibility(View.GONE);
                regtestLayout.setVisibility(View.VISIBLE);
            } else if (User.getInstance().getNetwork(mContext).equals("mainnet")) {
                vectorMainnetIv.setVisibility(View.VISIBLE);
                mainnetIv.setImageResource(R.drawable.bg_btn_round_06d78f_25);
                vectorTestnetIv.setVisibility(View.INVISIBLE);
                testnetIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                vectorRegtestIv.setVisibility(View.INVISIBLE);
                regtestIv.setImageResource(R.drawable.bg_btn_round_d9d9d9_25);
                mainnetLayout.setVisibility(View.VISIBLE);
                testnetLayout.setVisibility(View.GONE);
                regtestLayout.setVisibility(View.GONE);
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
                    bundle.putString(ChannelsActivity.KEY_CHANNEL, "all");
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
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
//            select directory
            rootView.findViewById(R.id.backup_directory_select).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuPopWindow.dismiss();
                    BackUpEvent event = new BackUpEvent();
                    event.setCode(1);
                    EventBus.getDefault().post(event);
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
//                    EventBus.getDefault().post(new LockEvent());
                    mMenuPopWindow.dismiss();
                    UnlockDialog mUnlockDialog = new UnlockDialog(mContext);
                    mUnlockDialog.show();
                }
            });
            // new version
            rootView.findViewById(R.id.layout_new_version).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoadingDialog.show();
                    HttpUtils.with(mContext)
                            .get()
                            .url("https://omnilaboratory.github.io/OBAndroid/app/src/main/assets/newVersion.json")
                            .execute(new EngineCallback() {
                                @Override
                                public void onPreExecute(Context context, Map<String, Object> params) {

                                }

                                @Override
                                public void onCancel(Context context) {

                                }

                                @Override
                                public void onError(Context context, String errorCode, String errorMsg) {
                                    LogUtils.e(TAG, "newVersionError:" + errorMsg);
                                }

                                @Override
                                public void onSuccess(Context context, String result) {
                                    LogUtils.e(TAG, "---------------newVersion---------------------" + result.toString());
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            try {
                                                if (AppUtils.getAppVersionName(mContext).equals(jsonObject.getString("version"))) {
                                                    mLoadingDialog.dismiss();
                                                    mMenuPopWindow.dismiss();
                                                    ToastUtils.showToast(mContext, "It is currently the latest version.");
                                                } else {
                                                    mLoadingDialog.dismiss();
                                                    mMenuPopWindow.dismiss();
                                                    boolean force = jsonObject.getBoolean("force");
                                                    NewVersionDialog mNewVersionDialog = new NewVersionDialog(mContext);
                                                    mNewVersionDialog.show(force);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onSuccess(Context context, byte[] result) {

                                }

                                @Override
                                public void onProgressInThread(Context context, Progress progress) {

                                }

                                @Override
                                public void onFileSuccess(Context context, String filePath) {

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

    public void startNode() {
        String lndDir = mContext.getApplicationContext().getExternalFilesDir(null).toString() + "/obd";
        String startParams = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getStartParams();
        Obdmobile.start("--lnddir=" + lndDir + startParams + User.getInstance().getAlias(mContext), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------startOnError------------------" + e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        EventBus.getDefault().post(new LockEvent());
                        mMenuPopWindow.dismiss();
                        mLoadingDialog.dismiss();
                        UnlockDialog mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "------------------startOnSuccess------------------");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        EventBus.getDefault().post(new LockEvent());
                        mMenuPopWindow.dismiss();
                        mLoadingDialog.dismiss();
                        UnlockDialog mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                    }
                });
            }
        });
    }

    public void release() {
        if (mMenuPopWindow != null) {
            mMenuPopWindow.dismiss();
            mMenuPopWindow = null;
        }
    }
}