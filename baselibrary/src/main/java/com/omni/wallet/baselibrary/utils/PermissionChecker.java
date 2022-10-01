package com.omni.wallet.baselibrary.utils;

import android.Manifest;
import android.content.Context;


/**
 * 权限检查工具类（涉及几个敏感权限）
 */

public class PermissionChecker {

    /**
     * 检查外部存储权限
     */
    public static boolean checkExternalPermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 检查电话权限
     */
    public static boolean checkCallPhonePermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, Manifest.permission.CALL_PHONE);
    }

    /**
     * 检查读取手机状态权限
     */
    public static boolean checkReadPhoneStatePermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * 检查定位权限
     */
    public static boolean checkLocationPermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }


}
