package com.omni.wallet.obdMethods;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.framelibrary.entity.User;

import java.util.Random;

import obdmobile.Callback;
import obdmobile.Obdmobile;

public class NodeStart {
    private final String TAG = NodeStart.class.getSimpleName();
    private static boolean isStart = false;
    private static NodeStart mInstance;
    private NodeStart() {}
    private String alias;

    public static NodeStart getInstance() {
        if (mInstance == null) {
            synchronized (WalletState.class) {
                if (mInstance == null) {
                    mInstance = new NodeStart();
                }
            }
        }
        return mInstance;
    }

    public static boolean isNodeStart() {
        return isStart;
    }

    private static void setIsStart(boolean isStart) {
        NodeStart.isStart = isStart;
    }


    private void startNode(Context context){
        if(StringUtils.isEmpty(User.getInstance().getAlias(context))){
            Random random = new Random();
            int randomNum = random.nextInt(100) +1;
            alias = "alice"+ "(" + randomNum + ")";
            User.getInstance().setAlias(context,alias);
        } else {
            alias = User.getInstance().getAlias(context);
        }
        LogUtils.e("================", alias);
        String lndDir = context.getExternalFilesDir(null).toString() + "/ObdMobile/" + ConstantInOB.networkType;
        String startParams = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getStartParams();
        Log.d(TAG, "startNode startParams: " + startParams + alias);
        LogUtils.e("================", startParams + alias);
        Log.d(TAG, "startNode: lndDir" + lndDir);
        Obdmobile.start("--lnddir=" + lndDir + startParams + alias, new Callback() {
            @Override
            public void onError(Exception e) {
                 if (e.getMessage().contains("unable to start server: unable to unpack single backups: chacha20poly1305: message authentication failed")) {
                    ToastUtils.showToast(context, "unable to unpack single backups that message authentication failed");
                } else if (e.getMessage().contains("error creating wallet config: unable to initialize neutrino backend: unable to create neutrino database: cannot allocate memory")) {
                    ToastUtils.showToast(context, "Failed to start, please check your cache is sufficient. After confirming that the cache is sufficient, please restart the App.");
                }

                LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                setIsStart(true);
                LogUtils.e(TAG, "------------------startonSuccess------------------");
            }
        });
    }

    private void startWhenStop(Context context) {
        if (!isStart){
            startNode(context);
        }
    }
    public void startWhenStopWithSubscribeState (Context context){
        WalletState.getInstance().subscribeWalletState(context);
        startWhenStop(context);
    }
}
