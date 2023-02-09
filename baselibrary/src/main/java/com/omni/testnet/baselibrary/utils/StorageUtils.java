package com.omni.testnet.baselibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.omni.testnet.baselibrary.base.PermissionConfig;

import java.io.File;
import java.io.IOException;

/**
 * 存储相关工具类
 */

public class StorageUtils {
    private static final String TAG = StorageUtils.class.getSimpleName();

    /**
     * 检测SD卡是否可
     */
    public static boolean checkSDCardState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡中包名下的缓存路径  /SD卡/Android/data/程序的包名/cache 路径
     */
    public static String SDCachePath(Context context) {
        if (checkSDCardState() && PermissionChecker.checkExternalPermission(context)) {
            File cacheFile = context.getExternalCacheDir();
            return cacheFile != null ? cacheFile.getAbsolutePath() :
                    context.getCacheDir().getAbsolutePath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 获取存储路径
     */
    public static String storagePath(Context context) {
        // 有外置存储权限，并且SD卡可用就用外部存储，否则用内部存储
        if (PermissionChecker.checkExternalPermission(context) && StorageUtils.checkSDCardState()) {
            return SDCardPath() + File.separator + context.getPackageName();
        } else {
            return DataPath() + File.separator + context.getPackageName();
        }
    }

    /**
     * 获取外置存储卡路径
     */
    public static String SDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取内置存储卡路径
     */
    public static String DataPath() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    /**
     * 获取内置存储卡中包名路径
     */
    public static String DataPackagePath(Context context) {
        String packagePath = context.getPackageCodePath();
        LogUtils.e(TAG, "包名目录是：" + packagePath);
        return packagePath;
    }

    /**
     * 手机内部缓存空间
     * /data/data/包名/cache/
     */
    public static String dataCachePath(Context context) {
        String path = context.getCacheDir().getAbsolutePath();
        LogUtils.e(TAG, "内部缓存目录是：" + path);
        return path;
    }

    /**
     * 手机内部文件存储目录
     * /data/data/包名/files/
     */
    public static String dataFilesPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            LogUtils.e(TAG, "Can't define system cache directory! " + cacheDirPath + " will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }


    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String dirName) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, dirName);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
     * is mounted and app has appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.d(TAG, "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                LogUtils.e(TAG, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }


    /**
     * 是否有外置存储卡读写权限
     */
    public static boolean hasExternalStoragePermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, PermissionConfig.STORAGE);
    }

    /**
     * SD卡是否可用
     */
    public static boolean SdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


}
