package com.omni.testnet.framelibrary.utils;

import android.content.Context;

import com.omni.testnet.baselibrary.utils.LogUtils;

import java.io.File;

/**
 * 崩溃日志上传工具类
 */

public class CrashLogUtils {
    private static final String TAG = CrashLogUtils.class.getSimpleName();

    protected boolean mUploadAllFile = false;// 是否上传所有文件

    private static CrashLogUtils mInstance = new CrashLogUtils();

    public static CrashLogUtils getInstance() {
        return mInstance;
    }

    public void uploadNowCrash(Context context) {
        mUploadAllFile = false;
        String logPath = LogUtils.getCrashLogPath();
        String logName = LogUtils.getCrashLogName();
        final File logFile = new File(logPath, logName);
        if (!logFile.exists()) {
            return;
        }
        uploadFile(context, logFile);
    }

    public void uploadAllCrashFile(Context context) {
        String logPath = LogUtils.getCrashLogPath();
        File logPathFile = new File(logPath);
        if (!logPathFile.exists()) {
            return;
        }
        File[] fileArray = logPathFile.listFiles();
        int size = fileArray.length;
        //
        if (size <= 0) {
            mUploadAllFile = false;
            return;
        }
        File logFile = fileArray[0];
        // 如果文件是当天的，不上传
        if (logFile != null && logFile.getName().equals(LogUtils.getCrashLogName())) {
            mUploadAllFile = false;
            return;
        }
        // 如果文件不是当天的，上传
        mUploadAllFile = true;
        uploadFile(context, logFile);
    }

    private void uploadFile(final Context context, final File logFile) {
        LogUtils.e(TAG, "上传崩溃日志文件");
//        HttpRequestUtils.logUpload(context, logFile, new DefaultHttpCallback<Object>() {
//            @Override
//            protected void onResponseSuccess(Object result) {
//                LogUtils.e(TAG, "文件" + logFile.getAbsoluteFile() + "上传成功");
//                // 如果是上传全部，就删除上传成功的，并继续调用上传
//                if (mUploadAllFile) {
//                    // 上传成功就删除文件
//                    boolean delResult = FileUtils.delFile(logFile.getAbsolutePath());
//                    LogUtils.e(TAG, "文件删除" + (delResult ? "成功" : "失败"));
//                    uploadAllCrashFile(context);
//                }
//            }
//
//            @Override
//            protected void onResponseFail(Context context, String errorCode, String errorMsg) {
//                LogUtils.e(TAG, "文件" + logFile.getAbsoluteFile() + "上传失败：" + errorMsg);
//            }
//
//            @Override
//            protected void onResponseError(Context context, String errorCode, String errorMsg) {
//                LogUtils.e(TAG, "文件" + logFile.getAbsoluteFile() + "上传失败：" + errorMsg);
//            }
//        });
    }
}
