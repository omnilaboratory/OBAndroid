package com.omni.wallet.framelibrary.entity.event;

/**
 * 网络状态变化的Event
 */

public class NetStateEvent {
    private boolean netState;// 网络连接状态（数据网络和WIFI网络）
    private boolean dataState;// 数据网络连接状态
    private boolean wifiState;// wifi网络连接状态


    public boolean isNetState() {
        return netState;
    }

    public void setNetState(boolean netState) {
        this.netState = netState;
    }

    public boolean isDataState() {
        return dataState;
    }

    public void setDataState(boolean dataState) {
        this.dataState = dataState;
    }

    public boolean isWifiState() {
        return wifiState;
    }

    public void setWifiState(boolean wifiState) {
        this.wifiState = wifiState;
    }
}
