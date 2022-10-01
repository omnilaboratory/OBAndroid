package com.omni.wallet.thirdsupport.umeng.statistics;

import android.content.Context;

import java.util.Map;

/**
 * 统计接口
 */

public interface IStatistics {

    void onResume(Context context);

    void onPause(Context context);

    void onPageStart(String viewName);

    void onPageEnd(String viewName);

    void onEvent(Context context, String eventID, String label);

    void onEvent(Context context, String eventID);

    void onEvent(Context context, String eventID, Map<String, String> map);

    void onKillProcess(Context context);
}
