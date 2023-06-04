package com.omni.wallet_mainnet.view.popupwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;
import com.omni.wallet_mainnet.common.ConstantWithNetwork;
import com.omni.wallet_mainnet.common.NetworkType;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.CopyUtil;
import com.omni.wallet_mainnet.view.dialog.LoadingDialog;
import com.omni.wallet_mainnet.view.dialog.UnlockDialog;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * 汉: 节点详情的弹窗
 * En: NodeInfoPopupWindow
 * author: guoyalei
 * date: 2022/11/29
 */
public class NodeInfoPopupWindow {
    private static final String TAG = NodeInfoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    TextView nameTv;
    LoadingDialog mLoadingDialog;

    public NodeInfoPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String pubKey) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_node_info);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            mLoadingDialog = new LoadingDialog(mContext);
            nameTv = rootView.findViewById(R.id.tv_node_name);
            nameTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    User.getInstance().setAlias(mContext, s.toString());
                }
            });
            TextView versionTv = rootView.findViewById(R.id.tv_node_version);
            TextView backendTv = rootView.findViewById(R.id.tv_node_backend);
            TextView modeTv = rootView.findViewById(R.id.tv_node_mode);
            TextView netWorkTv = rootView.findViewById(R.id.tv_node_network);
            TextView rpchostTv = rootView.findViewById(R.id.tv_node_rpchost);
            TextView portsTv = rootView.findViewById(R.id.tv_node_ports);
            TextView zmqpubrawblockTv = rootView.findViewById(R.id.tv_node_zmqpubrawblock);
            TextView zmqpubrawtxTv = rootView.findViewById(R.id.tv_node_zmqpubrawtx);
            if (!StringUtils.isEmpty(pubKey)) {
                getNodeInfo(pubKey);
            }
            if (!StringUtils.isEmpty(User.getInstance().getNodeVersion(mContext))) {
                versionTv.setText(User.getInstance().getNodeVersion(mContext).substring(0, User.getInstance().getNodeVersion(mContext).indexOf(" ")));
            }
            backendTv.setText("omnicoreproxy");
            modeTv.setText("noSeedBackup");
            // 网络类型
            // Network type
            if (ConstantInOB.networkType == NetworkType.TEST) {
                netWorkTv.setText("testnet");
            } else if (ConstantInOB.networkType == NetworkType.REG) {
                netWorkTv.setText("regtest");
            } else if (ConstantInOB.networkType == NetworkType.MAIN) {
                netWorkTv.setText("mainnet");
            }
            rpchostTv.setText(ConstantWithNetwork.getInstance(ConstantInOB.networkType).getOMNI_HOST_ADDRESS_PORT());
            portsTv.setText("9735:9735");
            zmqpubrawblockTv.setText("omnicoreproxy.zmqpubrawblock=tcp://43.138.107.248:28332");
            zmqpubrawtxTv.setText("omnicoreproxy.zmqpubrawtx=tcp://43.138.107.248:28333");
            // 点击copy
            rootView.findViewById(R.id.layout_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isEmpty(User.getInstance().getWalletAddress(mContext))) {
                        ToastUtils.showToast(mContext, "Please waiting for a while");
                        return;
                    }
                    //接收需要复制到粘贴板的地址
                    //Get the address which will copy to clipboard
                    String toCopyAddress = User.getInstance().getWalletAddress(mContext);
                    //接收需要复制成功的提示语
                    //Get the notice when you copy success
                    String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                    CopyUtil.SelfCopy(mContext, toCopyAddress, toastString);
                    mBasePopWindow.dismiss();
                }
            });
            // 点击reboot
            rootView.findViewById(R.id.layout_reboot).setOnClickListener(new View.OnClickListener() {
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
//                                    mBasePopWindow.dismiss();
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
//                                    mBasePopWindow.dismiss();
//                                    mLoadingDialog.dismiss();
//                                }
//                            });
//                        }
//                    });
                }
            });

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

    private void getNodeInfo(String pubKey) {
        LightningOuterClass.NodeInfoRequest nodeInfoRequest = LightningOuterClass.NodeInfoRequest.newBuilder()
                .setPubKey(pubKey)
                .build();
        Obdmobile.getNodeInfo(nodeInfoRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------nodeInfoOnError-----------------" + e.getMessage());
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
                            LightningOuterClass.NodeInfo nodeInfo = LightningOuterClass.NodeInfo.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------nodeInfoOnResponse-----------------" + nodeInfo);
                            nameTv.setText(StringUtils.cleanString(nodeInfo.getNode().getAlias()));
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
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
                        mBasePopWindow.dismiss();
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
                        mBasePopWindow.dismiss();
                        mLoadingDialog.dismiss();
                        UnlockDialog mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                    }
                });
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
