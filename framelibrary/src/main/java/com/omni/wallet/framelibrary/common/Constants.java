package com.omni.wallet.framelibrary.common;

/**
 * 一些公共参数常量
 */

public class Constants {

    // App加密Key
    public static final String AppKey = "08a31e5b351225863a843aa2820b4189";
    // SplashActivity开屏页延时时间
    public static final int SPLASH_SLEEP_TIME = 2000;
    // 与H5交互的JS中元素名称
    public static final String H5_JS_ELEMENT = "app";
    // 安卓7.0Uri获取相关Provider名字
    public static final String FILE_CONTENT_FILE_PROVIDER = "com.omni.wallet.fileProvider";

    public static final String DEFAULT_CITY_ID = "15";// 默认城市的ID
    public static final String DEFAULT_CITY = "郑州市";// 默认城市


    // ========启动微信QQ相关参数==========//
    // 启动微信的相关参数
    public static final String WE_CHAT_PACKAGE_NAME = "com.tencent.mm";
    public static final String WE_CHAT_PAGE_NAME = "com.tencent.mm.ui.LauncherUI";
    // 启动QQ相关参数
    public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    public static final String QQ_PAGE_NAME = "com.tencent.mobileqq.activity.HomeActivity";

    // 接口区分访问类型1:Android   2:IOS
    public static final int SERVER_TYPE_ANDROID = 1;
    // ==================IM相关======================//
    // 系统消息ID
    public static final String IM_ADMIN = "admin";
    // 点击通知的时候需要在MainActivity切换的Fragment的索引的Key
    public static final String KEY_PAGE_INDEX = "pageIndexKey";
    // ==================IM相关 end======================//


    // ==================常量======================//

    // ==================通知相关======================//
    public static final String CHANNEL_NORMAL = "normalNotify";// 安卓8.0中通知对应的渠道ID
    public static final String CHANNEL_NORMAL_DESC = "普通通知";// 安卓8.0中通知对应的渠道ID
    public static final String CHANNEL_CHAT = "chat";// 安卓8.0中通知对应的渠道ID
    public static final String CHANNEL_CHAT_DESC = "聊天消息";// 安卓8.0中通知对应的渠道ID
    // ==================IM相关======================//

    //================================账号封停界面传递参数的Key====================================//
    public static final String KEY_SEAL_UP_REASON = "sealUpReasonKey";
}
