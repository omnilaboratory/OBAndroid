package com.omni.wallet_mainnet.framelibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.framelibrary.entity.event.NetStateEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * 网络状态变化监听的广播接收者
 */
public class NetStateReceiver extends BroadcastReceiver {

    private static final String TAG = NetStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean dataState = false;// 移动数据连接状态
            boolean wifiState = false;// wifi数据连接状态
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                //获得ConnectivityManager对象
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取ConnectivityManager对象对应的NetworkInfo对象
                if (connectivityManager != null) {
                    //获取移动数据连接的信息
                    NetworkInfo dataNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    dataState = dataNetworkInfo != null && dataNetworkInfo.isConnected();
                    LogUtils.e(TAG, "移动数据连接状态：" + dataState);
                    //获取WIFI连接的信息
                    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    wifiState = wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
                    LogUtils.e(TAG, "WIFI数据连接状态：" + wifiState);
                } else {
                    dataState = false;
                    wifiState = false;
                    LogUtils.e(TAG, "获取ConnectivityManager为空");
                }
            } else { //API大于23时使用下面的方式进行网络监听
                //获得ConnectivityManager对象
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    //获取所有网络连接的信息
                    Network[] networks = connectivityManager.getAllNetworks();
                    if (networks == null || networks.length == 0) {
                        dataState = false;
                        wifiState = false;
                        LogUtils.e(TAG, "SDKVersion：23+；移动数据和WIFI数据均未连接");
                    } else {
                        //通过循环将网络信息逐个取出来
                        for (int i = 0; i < networks.length; i++) {
                            //获取ConnectivityManager对象对应的NetworkInfo对象
                            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networks[i]);
                            if (networkInfo != null) {
                                // MOBILE：数据网络，WIFI：wifi网络
                                String name = networkInfo.getTypeName();
                                boolean state = networkInfo.isConnected();
                                if (!StringUtils.isEmpty(name) && "MOBILE".equals(name)) {
                                    dataState = networkInfo.isConnected();
                                    LogUtils.e(TAG, "SDKVersion：23+；移动数据连接状态：" + state);
                                } else if (!StringUtils.isEmpty(name) && "WIFI".equals(name)) {
                                    wifiState = networkInfo.isConnected();
                                    LogUtils.e(TAG, "SDKVersion：23+；WIFI数据连接状态：" + state);
                                }
                            }
                        }
                    }
                } else {
                    LogUtils.e(TAG, "获取ConnectivityManager为空");
                }
            }
            // 状态发送出去
            NetStateEvent event = new NetStateEvent();
            event.setDataState(dataState);
            event.setWifiState(wifiState);
            event.setNetState(dataState || wifiState);
            // 发出去
            EventBus.getDefault().post(event);
        }
    }
}
