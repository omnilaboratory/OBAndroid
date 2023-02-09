package com.omni.testnet.framelibrary.base;

import android.content.Intent;

import com.omni.testnet.baselibrary.base.ExceptionCrashHandler;
import com.omni.testnet.framelibrary.service.LogService;


/**
 * 全局异常捕获类
 */

public class DefaultExceptionCrashHandler extends ExceptionCrashHandler {
    private static final String TAG = DefaultExceptionCrashHandler.class.getSimpleName();
    private static final ExceptionCrashHandler instance = new DefaultExceptionCrashHandler();


    public static ExceptionCrashHandler getInstance() {
        return instance;
    }

    @Override
    protected void logUpload() {
//        String logPath = LogUtils.getCrashLogPath();
//        String logName = LogUtils.getCrashLogName();
//        LogUtils.e(TAG, "崩溃日志文件路径：" + logPath + File.separator + logName);
//        CrashLogUtils.getInstance().uploadNowCrash(mContext);
        Intent intent = new Intent(mContext, LogService.class);
        mContext.startService(intent);
    }
}
