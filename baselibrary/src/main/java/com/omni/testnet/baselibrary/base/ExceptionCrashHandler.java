package com.omni.testnet.baselibrary.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;

import com.omni.testnet.baselibrary.common.Constants;
import com.omni.testnet.baselibrary.utils.ActivityUtils;
import com.omni.testnet.baselibrary.utils.DisplayUtil;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.NetWorkHelper;
import com.omni.testnet.baselibrary.utils.PermissionChecker;
import com.omni.testnet.baselibrary.utils.ToastUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 全局异常处理类
 */

public abstract class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = ExceptionCrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    protected Context mContext;
    // 用来存储设备信息和异常信息的map
    private Map<String, String> mExceptionMsgMap;

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        // handler初始化
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将系统默认的崩溃处理设置为this
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            // 延时2秒
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            // 延时重新开启程序，跳转启动页
            if (Constants.isDebug) {
                try {
                    Intent intent = new Intent(mContext, Class.forName(Constants.RESTART_ACTIVITY_NAME));
                    PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
//                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                    //启动延时任务，延时1秒启动开屏页
                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    if (mgr != null) {
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                    }
                } catch (ClassNotFoundException e2) {
                    LogUtils.e(TAG, "未找到Activity" + "" + e2.getMessage());
                    e2.printStackTrace();
                }
            }
            // 关闭所有Activity
            ActivityUtils.getInstance().finishAllActivity();
            //杀死当前进程（不能直接调用退出程序，否则不能重启了）
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * 如果处理了该异常信息返回true;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.showToast(mContext, "很抱歉,程序出现异常,即将退出.");
                Looper.loop();
            }
        }.start();
        // 存放信息的Map
        mExceptionMsgMap = new LinkedHashMap<>();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 收集异常信息
        String exceptionInfo = collectionExceptionInfo(ex);
        // 异常信息写入本地(判断权限)
        if (PermissionChecker.checkExternalPermission(mContext)) {
            LogUtils.crashFile(TAG, exceptionInfo);
        }
        // 异常信息上传到服务器
        if (!Constants.isDebug) {
            logUpload();
        }
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo(Context c) {
        try {
            PackageManager pm = c.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                mExceptionMsgMap.put("OS", "Android" + Build.VERSION.RELEASE); // 固件版本
                mExceptionMsgMap.put("model", Build.MODEL);
                mExceptionMsgMap.put("netMode", NetWorkHelper.getNetWorkType(c));// 网络类型
                mExceptionMsgMap.put("operator", NetWorkHelper.getSimOperatorName(c));// 运营商
                mExceptionMsgMap.put("resolution", DisplayUtil.getScreenHeight(c) + "x" + DisplayUtil.getScreenWidth(c));// 分辨率
                mExceptionMsgMap.put("versionName", "v" + versionName);
                mExceptionMsgMap.put("versionCode", versionCode);
                mExceptionMsgMap.put("*", "********************************************************************");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mExceptionMsgMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼接收集的设备参数和捕获的异常信息
     */
    private String collectionExceptionInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\n");
        for (Map.Entry<String, String> entry : mExceptionMsgMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "  =  " + value + "\n");
        }
        sb.append("\n");
        sb.append("\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        LogUtils.e(TAG, "--------------------------未捕获异常------------------------------------------------------------------------------------");
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "\n" + result);
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "------------------------------------------------------------------------------------------------------------------------");
        sb.append(result);
        return sb.toString();
    }

    /**
     * 崩溃日志上传
     */
    protected abstract void logUpload();
}
