package com.omni.wallet.utils;

import android.content.Context;

import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.framelibrary.utils.PreferencesUtils;


/**
 * App版本控制工具类
 */

public class AppVersionUtils {
    private static final String TAG = AppVersionUtils.class.getSimpleName();

    /**
     * 版本号检查
     */
    public static void checkVersion(Context context) {
        int localVersion = PreferencesUtils.getVersionFromLocal(context);
        int currentVersion = AppUtils.getAppVersionCode(context);
        // 版本号增加了，重置相关属性
        if (currentVersion > localVersion) {
            // 更新本地版本号
            PreferencesUtils.saveVersionToLocal(context, currentVersion);
        }
    }

}
