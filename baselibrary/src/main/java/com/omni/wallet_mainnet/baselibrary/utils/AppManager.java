package com.omni.wallet_mainnet.baselibrary.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序
 */
public class AppManager {
    private static final String TAG = AppManager.class.getSimpleName();

    private static AppManager instance = new AppManager();

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        return instance;
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void appExit(Context context) {
        try {
            LogUtils.e(TAG, "==================退出程序==============");
            ActivityUtils.getInstance().finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityMgr != null) {
                activityMgr.restartPackage(context.getPackageName());
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空 在Debug模式下打印不出来渠道的信息！
     * 但是在发布的版本就可以打印出信息！
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

}