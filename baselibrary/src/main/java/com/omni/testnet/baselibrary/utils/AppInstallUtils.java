package com.omni.testnet.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.omni.testnet.baselibrary.base.BaseActivity;

import java.io.File;
import java.util.List;

/**
 * 应用程序APK安装工具类
 */

public class AppInstallUtils {
    private static final String TAG = AppInstallUtils.class.getSimpleName();

    public static final int REQUEST_CODE_UN_KNOW_SOURCE_INSTALL_APK = 1;// 未知来源安装应用程序的RequestCode
    public static final int REQUEST_CODE_INSTALL_APK = 2;// 安装应用程序的RequestCode

    private static AppInstallUtils mInstance;
    private File mApkFile;// 需要安装的Apk文件

    public static AppInstallUtils getInstance() {
        if (mInstance == null) {
            synchronized (AppInstallUtils.class) {
                if (mInstance == null) {
                    mInstance = new AppInstallUtils();
                }
            }
        }
        return mInstance;
    }

    public void installAPKFile(Context context, String provider) {
        installAPKFile(context, mApkFile, provider);
    }

    /**
     * 安装APK文件
     *
     * @param provider 适配7.0的时候的provider名字，在xml文件里面
     */
    public void installAPKFile(Context context, File file, String provider) {
        this.mApkFile = file;
        if (file == null) {
            LogUtils.e(TAG, "APK File Is Null");
            return;
        }
        if (!file.exists()) {
            LogUtils.e(TAG, "APK File Not Exists");
            return;
        }
        LogUtils.e(TAG, "安装文件：" + file.getAbsolutePath());
        try {
            //兼容8.0，检查用户是否授予未知来源应用安装权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean canInstall = context.getPackageManager().canRequestPackageInstalls();
                // 跳转到设置-允许安装未知来源-页面
                if (!canInstall) {
                    LogUtils.e(TAG, "未允许该来源应用安装，打开未知来源应用设置界面");
                    startInstallPermissionSettingActivity(context);
                    return;
                }
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 以下代码是为了兼容安卓7.0使用原来的安装方式崩溃的问题
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // fileProvider就是在清单文件中申明的Provider
                Uri contentUri = FileProvider.getUriForFile(context, provider, file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。
                // 外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
                ((BaseActivity) context).startActivityForResult(intent, REQUEST_CODE_INSTALL_APK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "安装应用异常：" + e.getMessage() + "\n" + e.getCause());
        }
    }

    /**
     * 跳转到设置-->允许未知来源应用安装页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        // 后面跟上包名，可以直接跳转到对应APP的未知来源权限设置界面。使用startActivityForResult 是为了在关闭设置界面之后，
        // 获取用户的操作结果，然后根据结果做其他处理
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
        ((BaseActivity) context).startActivityForResult(intent, REQUEST_CODE_UN_KNOW_SOURCE_INSTALL_APK);
    }

    /**
     * 判断QQ是否安装
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断微信是否可用
     */
    public static boolean isWeChatAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

}
