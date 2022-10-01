package com.omni.wallet.base;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.omni.wallet.baselibrary.base.BaseApplication;
import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.engine.OkHttpEngine;
import com.omni.wallet.baselibrary.http.interceptor.LogInterceptor;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.framelibrary.base.DefaultExceptionCrashHandler;
import com.omni.wallet.thirdsupport.umeng.UMUtils;

/**
 * Application
 * Created by fa on 2019/11/22.
 */

public class AppApplication extends BaseApplication {
    private static final String TAG = AppApplication.class.getSimpleName();
    private RefWatcher mRefWatcher;

    @Override
    protected void init() {
// 去掉安卓P启动时候的警告弹窗
        AppUtils.closeAndroidPDialog();
        // 检测内存泄漏相关
        mRefWatcher = LeakCanary.install(this);
        // 注册全局的异常处理类
        DefaultExceptionCrashHandler.getInstance().init(getApplicationContext());
        // 初始化网络引擎(使用OKHttp)
        OkHttpEngine okHttpEngine = new OkHttpEngine();
//        // 设置数据加密的拦截器（设置在Log拦截器之前，打印的时候才会打印加密之后的数据）
//        okHttpEngine.addInterceptor(new DefaultEncryptInterceptor(this));
//        // 设置token过期的拦截器
//        okHttpEngine.addInterceptor(new DefaultTokenInterceptor(this));
        // 设置Log拦截器（一般写在最后就行）
        okHttpEngine.addInterceptor(new LogInterceptor());
        // 初始化
        HttpUtils.init(this, okHttpEngine);
        // 调试模式允许使用代理
        HttpUtils.allowWifiProxy(Constants.isDebug);
        // ARouter
        if (Constants.isDebug) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    @Override
    protected void beforeInit(String processName) {
        if (needRegisterPush(processName)) {
            // 友盟初始化
            UMUtils.init(getApplicationContext());
//            // 友盟设置消息处理的Handler
//            UMUtils.setMessageHandler(this, new DefaultPushHandler());
//            // 友盟设置消息点击处理的handler
//            UMUtils.setNotificationClickHandler(this, new DefaultNotificationClickHandler());
//            // 注册推送服务（判断进程名字  包名:channel  才是友盟推送进程）
//            UMUtils.registerPush(this, new DefaultPushRegisterCallback(getApplicationContext()));
//            LogUtils.e(TAG, "进程" + processName + "注册推送");
        } else {
//            LogUtils.e(TAG, "进程" + processName + "不是App或者友盟进程，不注册友盟推送");
        }
    }

    /**
     * 判读是否主进程或者友盟推送进程
     */
    private boolean needRegisterPush(String processName) {
        String packageName = getApplicationContext().getPackageName();
        return (packageName + ":channel").equals(processName) || packageName.equals(processName);
    }

    /**
     * 解决放法数量超过65536
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
