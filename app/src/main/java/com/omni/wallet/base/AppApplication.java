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
        /**
         * @描述： 去掉安卓P启动时候的警告弹窗
         * @desc: Remove the warning pop-up window when Android P starts
         */
        AppUtils.closeAndroidPDialog();
        /**
         * @描述： 去掉安卓P启动时候的警告弹窗
         * @desc: Detect memory leak correlation
         */
        mRefWatcher = LeakCanary.install(this);
        /**
         * @描述： 注册全局的异常处理类
         * @desc: Register global exception handling classes
         */
        DefaultExceptionCrashHandler.getInstance().init(getApplicationContext());
        /**
         * @描述： 初始化网络引擎(使用OKHttp)
         * @desc: Initialize the network engine (using OKHttp)
         */
        OkHttpEngine okHttpEngine = new OkHttpEngine();

//        // 设置数据加密的拦截器（设置在Log拦截器之前，打印的时候才会打印加密之后的数据）
//        okHttpEngine.addInterceptor(new DefaultEncryptInterceptor(this));
//        // 设置token过期的拦截器
//        okHttpEngine.addInterceptor(new DefaultTokenInterceptor(this));
        // 设置Log拦截器（一般写在最后就行）
        okHttpEngine.addInterceptor(new LogInterceptor());
        /**
         * @描述： 初始化
         * @desc: Initialize
         */
        HttpUtils.init(this, okHttpEngine);
        /**
         * @描述： 调试模式允许使用代理
         * @desc: Debug mode allows the use of agents
         */
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
            /**
             * @描述： 友盟初始化
             * @desc: Alliance initialization
             */
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
     * @描述： 判读是否主进程或者友盟推送进程
     * @desc: Judge whether it is the main process or the process pushed by the Alliance
     */
    private boolean needRegisterPush(String processName) {
        String packageName = getApplicationContext().getPackageName();
        return (packageName + ":channel").equals(processName) || packageName.equals(processName);
    }
    /**
     * @描述： 解决放法数量超过65536
     * @desc: The number of solutions exceeds 65536
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
