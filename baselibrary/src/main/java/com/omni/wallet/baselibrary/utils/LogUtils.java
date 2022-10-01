package com.omni.wallet.baselibrary.utils;


import android.content.Context;

import com.omni.wallet.baselibrary.base.BaseApplication;
import com.omni.wallet.baselibrary.common.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 日志工具类
 */

public class LogUtils {

    private static final String TAG = LogUtils.class.getSimpleName();

    // 是否给log结尾加统一标志 方便信息过滤
    private static final boolean isExpandMsg = true;

    // log标记
    private static final String mark = "<XFLProperty>  ";

    // 日志文件存储路径
    private static String LOG_PATH = "XFLProperty";// 日志文件在sdcard中的路径
    //


    // 日志打印类型
    // INFO
    private static final int TYPE_D = 1;
    // DEBUG
    private static final int TYPE_I = 2;
    // ERROR
    private static final int TYPE_E = 3;


    // 获取添加标志之后的日志信息
    private static String getMsg(String msg) {
        if (isExpandMsg)
            msg = mark + msg;
        return msg;
    }

    public static void d(String tag, String msg) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_D, tag, msg);
        }
    }

    public static void d(String tag, boolean msg) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_D, tag, String.valueOf(msg));
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_D, tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_I, tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_I, tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_E, tag, msg);
        }
    }

    public static void e(String tag, boolean msg) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_E, tag, String.valueOf(msg));
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (Constants.isShowLog) {
            cutAndLogMsg(TYPE_E, tag, msg, t);
        }
    }

    public static void file(String tag, String text) {
        if (Constants.isSaveLog) {
            File logFile = getLogFile(getLogPath(), DateUtils.formatCurrentDate() + ".log");
            logToFile(logFile, tag, text);
        }
    }

    public static void file(Context context, String tag, String text) {
        if (Constants.isSaveLog) {
            File logFile = getLogFile(getLogPath(context), DateUtils.formatCurrentDate() + ".log");
            logToFile(logFile, tag, text);
        }
    }

    public static void crashFile(String tag, String text) {
        File logFile = getLogFile(getCrashLogPath(), getCrashLogName());
        logToFile(logFile, tag, text);
    }

    public static void crashFile(Context context, String tag, String text) {
        File logFile = getLogFile(getLogPath(context) + File.separator + "crash", getCrashLogName());
        logToFile(logFile, tag, text);
    }

    /**
     * 日志信息写入文件
     */
    public static void logToFile(final File logFile, final String tag, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (logFile != null) {
                        String needWriteMessage = "\n\n\n\n" + DateUtils.formatCurrentTime() + "    " + tag + "    " + text;
                        FileWriter filerWriter = new FileWriter(logFile, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                        BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                        bufWriter.write(needWriteMessage);
                        bufWriter.newLine();
                        bufWriter.close();
                        filerWriter.close();
                    }
                } catch (IOException e) {
                    e(TAG, "日志写入文件失败" + "\n" + e.getCause() + "\n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获得日志File
     */
    public static File getLogFile(String filePath, String fileName) {
        // 创建目录
        File dir = new File(filePath);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
        }
        // 打开文件
        return new File(filePath, fileName);
    }


    /**
     * 获取日志文件保存路径
     */
    private static String getLogPath() {
        return StorageUtils.storagePath(BaseApplication.applicationContext) + File.separator + LOG_PATH;
    }

    /**
     * 获取日志文件保存路径
     */
    public static String getCrashLogPath() {
        return getLogPath() + File.separator + "crash";
    }

    /**
     * 获取崩溃日志文件名称
     */
    public static String getCrashLogName() {
        return "crash_" + DateUtils.formatCurrentDate() + ".log";
    }

    /**
     * 获取日志文件保存路径（应用包下）
     */
    private static String getLogPath(Context context) {
        return StorageUtils.SDCachePath(context);
    }

    /**
     * 日志长度过长进行切割
     */
    private static void cutAndLogMsg(int type, String tag, String msg) {
        cutAndLogMsg(type, tag, msg, null);
    }

    /**
     * 日志长度过长进行切割
     */
    private static void cutAndLogMsg(int type, String tag, String msg, Throwable exception) {
        // 限制长度
        int segmentSize = 4 * 1024;
        // 日志长度
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            logMsgByType(type, tag, msg, exception);
        } else {
            // 剩余的日志长度大于限定长度的话，循环分段打印日志
            while (msg.length() > segmentSize) {
                // 获取固定长度的日志
                String logContent = msg.substring(0, segmentSize);
                logMsgByType(type, tag, logContent, exception);
                // 替换掉已经打印的日志
                msg = msg.replace(logContent, "");
            }
            // 打印剩余日志
            logMsgByType(type, tag, msg, exception);
        }
    }

    /**
     * 根据类型打印日志
     */
    private static void logMsgByType(int type, String tag, String msg, Throwable exception) {
        if (type == TYPE_D) {
            android.util.Log.d(tag, getMsg(msg), exception);
            return;
        }
        if (type == TYPE_I) {
            android.util.Log.i(tag, getMsg(msg), exception);
            return;
        }
        if (type == TYPE_E) {
            android.util.Log.e(tag, getMsg(msg), exception);
        }
    }
}
