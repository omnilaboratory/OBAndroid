package com.omni.testnet.baselibrary.utils;

import android.app.Activity;
import android.arch.core.BuildConfig;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 与手机系统类型相关的工具类
 * 比如：判断手机类型、跳转到权限管理页面、设置状态栏中图文的颜色模式(深色或亮色)
 */

public class SystemTypeUtil {
    private static final String TAG = SystemTypeUtil.class.getSimpleName();

    public static final int REQ_CODE_PERMISSION = 123;

    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
    private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";


    /**
     * 是否为华为手机
     */
    public static boolean isEMUI() {
        try {
            return getProperty(KEY_EMUI_API_LEVEL, null) != null || getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null || getProperty(KEY_EMUI_VERSION, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 是否为小米手机
     */
    public static boolean isMIUI() {
        try {
            return getProperty(KEY_MIUI_VERSION_CODE, null) != null || getProperty(KEY_MIUI_VERSION_NAME, null) != null || getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 是否为魅族手机
     */
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }


    public static String getProperty(String name, String defaultValue) throws IOException {
        //Android 8.0以下可通过访问build.prop文件获取相关属性，8.0及以上无法访问，需采用反射获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            return properties.getProperty(name, defaultValue);
        } else {
            try {
                Class<?> clz = Class.forName("android.os.SystemProperties");
                Method get = clz.getMethod("get", String.class, String.class);
                String property = (String) get.invoke(clz, name, defaultValue);
                if (TextUtils.isEmpty(property)) return null;
                else return property;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return defaultValue;
        }
    }

    //跳转到权限管理页面，兼容不同手机系统类型
    public static void goPermissionPage(Activity context) {
        if (isFlyme()) {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            try {
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
            }
        } else if (isMIUI()) {
            try {
                // 高版本MIUI 访问的是PermissionsEditorActivity，如果不存在再去访问AppPermissionsEditorActivity
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.setComponent(componentName);
                intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                try {
                    // 低版本MIUI
                    Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.setComponent(componentName);
                    intent.putExtra("extra_pkgname", BuildConfig.APPLICATION_ID);
                    context.startActivityForResult(intent, REQ_CODE_PERMISSION);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
                }
            }
        } else if (isEMUI()) {
            Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            try {
                context.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
            }
        } else {
            context.startActivityForResult(getAppDetailSettingIntent(context), REQ_CODE_PERMISSION);
        }
    }

    /**
     * 获取应用详情页面intent
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

    //设置状态栏中图标、字体的颜色模式（深色模式/亮色模式）
    //只有魅族（Flyme4+），小米（MIUI6+），android（6.0+）可以设置
    public static boolean setStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (setMiuiStatusBarLightMode(window, isDark)) {
                result = true;
            } else if (setFlymeStatusBarLightMode(window, isDark)) {
                result = true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setAndroid6StatusBarLightMode(window, isDark);
                result = true;
            }
        }
        return result;
    }

    public static boolean setFlymeStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static boolean setMiuiStatusBarLightMode(Window window, boolean isDark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void setAndroid6StatusBarLightMode(Window window, boolean isDark) {
        if (isDark) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}
