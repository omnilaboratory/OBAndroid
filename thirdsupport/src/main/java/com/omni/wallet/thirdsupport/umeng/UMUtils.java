package com.omni.wallet.thirdsupport.umeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;
import com.umeng.socialize.Config;
import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.thirdsupport.common.UMConfig;
import com.omni.wallet.thirdsupport.umeng.share.UMShare;
import com.omni.wallet.thirdsupport.umeng.share.bean.BaseShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.ShareBuilder;
import com.omni.wallet.thirdsupport.umeng.share.callback.ShareCallback;
import com.omni.wallet.thirdsupport.umeng.share.common.IShare;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;
import com.omni.wallet.thirdsupport.umeng.statistics.IStatistics;
import com.omni.wallet.thirdsupport.umeng.statistics.UMStatistics;

import java.util.Map;


/**
 * 友盟相关的工具类
 */

public class UMUtils {
    private static final String TAG = UMUtils.class.getSimpleName();
    private static IShare mShare = new UMShare();
    //    private static IPush mPush = new UMPush();
    private static IStatistics mStatistics = new UMStatistics();
    private ShareBuilder mBuilder;

    /**
     * 初始化
     */
    public static void init(Context context) {
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(Constants.isDebug);
        // 设置分享微信小程序测试版、预览版，不设置默认正式版
        if (Constants.isMinPre) {
            Config.setMiniPreView();// 预览版
        }
        if (Constants.isMinTest) {// 小程序是否测试版
            Config.setMiniTest();// 测试版
        }
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        // 其中第一个参数是application
        // 第二个是Appkey
        // 第三个是channel（只用share可以不写）
        // 第四个参数是设备类型，默认填写UMConfigure.DEVICE_TYPE_PHONE即可
        // 第五个参数为push的secret，没有使用，填写空字符串即可
        // 需要多渠道的时候渠道名称在这里必须传null，使其自动从清单文件获取渠道包中对应的channel
        if (Constants.isUMTest) {
            UMConfigure.init(context, UMConfig.APP_KEY_TEST, null, UMConfigure.DEVICE_TYPE_PHONE, UMConfig.PUSH_SECRET_TEST);
        } else {
            UMConfigure.init(context, UMConfig.APP_KEY, null, UMConfigure.DEVICE_TYPE_PHONE, UMConfig.PUSH_SECRET);
        }
        // 初始化统计
        initStatistics(context);
    }

    /**
     * 初始化统计
     */
    private static void initStatistics(Context context) {
        // 初始化友盟统计
        // EScenarioType.E_UM_NORMAL 普通统计场景，如果您在埋点过程中没有使用到
        // U-Game统计接口，请使用普通统计场景。
        // EScenarioType.E_UM_GAME 游戏场景 ，如果您在埋点过程中需要使用到U-Game
        // 统计接口，则必须设置游戏场景，否则所有的U-Game统计接口不会生效
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        // 设置SecretKey
//        MobclickAgent.setSecret(context.getApplicationContext(), "");
//        // 是否禁止默认的页面统计功能，这样将不会再自动统计Activity页面。
//        MobclickAgent.openActivityDurationTrack(false);
        // 将默认Session间隔时长改为40秒。
        MobclickAgent.setSessionContinueMillis(1000 * 40);
    }

    /**
     * 获取测试设备信息
     */
    public static String getTestDeviceInfo(Context context) {
        String[] deviceInfo = new String[2];
        if (context != null) {
            deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
            deviceInfo[1] = DeviceConfig.getMac(context);
        }
        return "device_id:" + deviceInfo[0] + ",mac:" + deviceInfo[1];
    }

    public UMUtils(ShareBuilder builder) {
        this.mBuilder = builder;
    }

    public static UMUtils with(Activity activity) {
        return new UMUtils(new ShareBuilder(activity));
    }

    public static UMUtils with(Context context) {
        return with((Activity) context);
    }

    public UMUtils text(String text) {
        mBuilder.mText = text;
        return this;
    }

    public UMUtils shareBean(BaseShareBean bean) {
        mBuilder.mShareBean = bean;
        return this;
    }

    public UMUtils target(Target target) {
        mBuilder.mShareTarget = target;
        return this;
    }

    public UMUtils callback(ShareCallback callback) {
        mBuilder.mShareCallback = callback;
        return this;
    }
//    /**
//     * 注册推送服务
//     */
//    public static void registerPush(Context context, IPushRegisterCallback callback) {
//        mPush.registerPush(context, callback);
//    }

//    /**
//     * 设置消息处理的Handler
//     */
//    public static void setMessageHandler(Context context, BasePushMessageHandler handler) {
//        mPush.setMessageHandler(context, handler);
//    }

    //    /**
//     * 设置通知点击的handler
//     */
//    public static void setNotificationClickHandler(Context context, BaseNotificationClickHandler handler) {
//        mPush.setNotificationClickHandler(context, handler);
//    }

    /**
     * 分享
     */
    public void share() {
        mShare.share(mBuilder);
    }
//    /**
//     * 推送统计应用启动数据
//     */
//    public void onAppStart(Context context) {
//        if (mPush instanceof UMPush) {
//            ((UMPush) mPush).onAppStart(context);
//        }
//    }
//

    /**
     * 页面停留统计
     */
    public static void onResume(Activity activity) {
        mStatistics.onResume(activity);
    }

    /**
     * 页面停留统计
     */
    public static void onPause(Activity activity) {
        mStatistics.onPause(activity);
    }

    /**
     * 页面数量统计
     */
    public static void onPageStart(String viewName) {
        mStatistics.onPageStart(viewName);
    }

    /**
     * 页面数量统计
     */
    public static void onPageEnd(String viewName) {
        mStatistics.onPageEnd(viewName);
    }

    /**
     * 事件统计
     */
    public static void onEvent(Context context, String eventID, String label) {
        if (StringUtils.isEmpty(label)) {
            mStatistics.onEvent(context, eventID);
        } else {
            mStatistics.onEvent(context, eventID, label);
        }
    }

    /**
     * 事件统计
     */
    public static void onEvent(Context context, String eventID, Map<String, String> map) {
        mStatistics.onEvent(context, eventID, map);
    }

    /**
     * 退出进程之前调用，保存数据
     */
    public static void onKillProcess(Context context) {
        mStatistics.onKillProcess(context);
    }

    /**
     * 是否调用了分享
     */
    public static boolean isRequestShare() {
        return mShare.isRequestShare();
    }

    /**
     * 资源释放
     */
    public static void release(Activity activity) {
        mShare.release(activity);
    }

    /**
     * QQ分享回调
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mShare.onActivityResult(mBuilder.mActivity, requestCode, resultCode, data);
    }
}
