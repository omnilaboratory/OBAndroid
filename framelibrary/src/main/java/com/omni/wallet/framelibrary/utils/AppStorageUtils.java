package com.omni.wallet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet.baselibrary.base.BaseApplication;
import com.omni.wallet.baselibrary.utils.StorageUtils;

import java.io.File;


/**
 * App路径管理Util类
 */

public class AppStorageUtils {
    private static final String TAG = AppStorageUtils.class.getSimpleName();
    private static Context context = BaseApplication.applicationContext;

    // APP存储路径名字
    private static final String APP_PATH_NAME = "U-WORLD";
    // Log的存储路径名称
    public static final String LOG_PATH_NAME = "Log";
    // 数据库的路径名
    public static final String DB_NAME = "DB";


    /**
     * 获取Log的存储路径
     */
    public static String getLogPath() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StorageUtils.storagePath(context));
        buffer.append(File.separator);
        buffer.append(LOG_PATH_NAME);
        return buffer.toString();
    }


    /**
     * 获取APP的数据库存储路径
     */
    public static String getDBRoot(Context context) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(StorageUtils.storagePath(context));
        buffer.append(File.separator);
        buffer.append(DB_NAME);
        return buffer.toString();
    }

    /**
     * 获取外置存储卡中APP的包名目录
     */
    public static String packagePath(Context context) {
        return StorageUtils.SDCardPath() + File.separator + context.getPackageName();
    }


    /**
     * 获取外置存储卡中APP下载文件的存储路径
     */
    public static String downLoadPath(Context context) {
        return StorageUtils.SDCardPath() + File.separator + context.getPackageName();
    }


    /**
     * 获取下载的APk保存路径
     * 当外置SD卡不可用的时候，将文件保存到内部存储目录
     */
    public static String getApkDownLoadDir(Context context) {
        if (StorageUtils.SdCardAvailable() && StorageUtils.hasExternalStoragePermission(context)) {
            return downLoadPath(context) + File.separator + "apk";
        } else {
            return StorageUtils.dataFilesPath(context) + File.separator + "apk";
        }
    }

    /**
     * 获取补丁保存路径
     * 当外置SD卡不可用的时候，将文件保存到内部存储目录
     */
    public static String getPatchDownLoadDir(Context context) {
        if (StorageUtils.SdCardAvailable() && StorageUtils.hasExternalStoragePermission(context)) {
            return downLoadPath(context) + File.separator + "patch";
        } else {
            return StorageUtils.dataFilesPath(context) + File.separator + "patch";
        }
    }

    /**
     * 获取拍照的临时存储路径
     * 当外置SD卡不可用的时候，将文件保存到内部存储目录
     */
    public static String getTempCameraImageDir(Context context) {
        if (StorageUtils.SdCardAvailable() && StorageUtils.hasExternalStoragePermission(context)) {
            return downLoadPath(context) + File.separator + "picture";
        } else {
            return StorageUtils.dataFilesPath(context) + File.separator + "picture";
        }
    }

    /**
     * 获取图片的临时存储路径
     * 当外置SD卡不可用的时候，将文件保存到内部存储目录
     */
    public static String getTempImageDir(Context context) {
        if (StorageUtils.SdCardAvailable() && StorageUtils.hasExternalStoragePermission(context)) {
            return downLoadPath(context) + File.separator + "picture";
        } else {
            return StorageUtils.dataFilesPath(context) + File.separator + "picture";
        }
    }

}
