package com.omni.wallet_mainnet.framelibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.framelibrary.utils.CrashLogUtils;

import java.io.File;

/**
 * 日志上传工具类
 */

public class LogService extends Service {
    private static final String TAG = LogService.class.getSimpleName();
    private static Handler handler = new Handler();

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //这里上传崩溃日志
            String logPath = LogUtils.getCrashLogPath();
            String logName = LogUtils.getCrashLogName();
            LogUtils.e(TAG, "崩溃日志文件路径：" + logPath + File.separator + logName);
            CrashLogUtils.getInstance().uploadNowCrash(LogService.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogService.this.stopSelf();
            }
        }, 3000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "LogService onDestroy: =========================》");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
