package com.omni.wallet_mainnet.baselibrary.common;

/**
 * 全局常量
 */

public class Constants {
    private Constants() {
    }

    // 是否debug模式=======》打包上线改为 false
    public static final boolean isDebug = true;
    // 是否显示日志=======》打包上线改为 false
    public static final boolean isShowLog = true;
    // 小程序是否预览模式=======》打包上线改为 false
    public static final boolean isMinPre = true;
    // 小程序是否测试模式=======》打包上线改为 false
    public static final boolean isMinTest = false;
    // 环信是否debug模式=======》打包上线改为false
    public static final boolean isIMDebug = false;
    // 友盟是否使用测试AppKey=======》打包上线改为false
    public static final boolean isUMTest = false;
    // 是否保存日志到本地
    public static final boolean isSaveLog = false;
    // Activity是否支持矢量图
    public static final boolean isCompatVectorSupport = false;

    // 网络连接的超时时间（秒）
    public static final int CONNECT_TIMEOUT = 15;
    // 上传下载的网络连接超时时间（秒）
    public static final int CONNECT_TIMEOUT_FILE = 100;
    // 网络数据读取的超时时间（秒）
    public static final int READ_TIMEOUT = 100;
    // 网络数据写入的超时时间（秒）
    public static final int WRITE_TIMEOUT = 100;
    // Json解析异常的错误码
    public static final String CODE_ERROR_FORMAT = "formatError";
    // 请求超时的提示错误码
    public static final String CODE_ERROR_REQUEST = "timeOut";
    // 网络无连接的错误码
    public static final String CODE_NETWORK_CONNECTIONLESS = "unConnect";
    // 崩溃后要启动的Activity的全类名
    public static final String RESTART_ACTIVITY_NAME = "com.omni.wallet.ui.activity.SplashActivity";

    // 存放加密密钥的Header的Key
    public static final String ENCRYPT_REQUEST_HEADER_KEY = "encrypt-key";

}
