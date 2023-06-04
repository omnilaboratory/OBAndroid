package com.omni.wallet_mainnet.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private String TAG = NetworkChangeReceiver.class.getSimpleName();
    private String typeName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            // WIFI 和 移动网络都关闭 即没有有效网络
            callBackNetWork.callBack(-1);
            Log.e(TAG, "当前无网络连接");
            return;
        }
        callBackNetWork.callBack(networkInfo.getType());
    }

    public static CallBackNetWork callBackNetWork = null;

    public void setCallBackNetWork(CallBackNetWork callBackNetWork) { //公开接口 能访问接口
        this.callBackNetWork = callBackNetWork;
    }

    public interface CallBackNetWork {
        void callBack(int networkType);
    }
}
