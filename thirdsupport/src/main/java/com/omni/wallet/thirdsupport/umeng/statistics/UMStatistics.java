package com.omni.wallet.thirdsupport.umeng.statistics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 友盟统计的工具类
 */

public class UMStatistics implements IStatistics {

    @Override
    public void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    @Override
    public void onPageStart(String viewName) {
        MobclickAgent.onPageStart(viewName);
    }

    @Override
    public void onPageEnd(String viewName) {
        MobclickAgent.onPageEnd(viewName);
    }

    @Override
    public void onEvent(Context context, String eventID, String label) {
        MobclickAgent.onEvent(context, eventID, label);
    }

    @Override
    public void onEvent(Context context, String eventID) {
        MobclickAgent.onEvent(context, eventID);
    }

    @Override
    public void onEvent(Context context, String eventID, Map<String, String> map) {
        MobclickAgent.onEvent(context, eventID, map);
    }

    @Override
    public void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }

}
