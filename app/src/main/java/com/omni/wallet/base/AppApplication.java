package com.omni.wallet.base;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.omni.wallet.baselibrary.base.BaseApplication;
import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.engine.OkHttpEngine;
import com.omni.wallet.baselibrary.http.interceptor.LogInterceptor;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.framelibrary.base.DefaultExceptionCrashHandler;

import lndmobile.Callback;
import lndmobile.Lndmobile;

/**
 * Application
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
        /**
         * Start the lnd node during initialization
         * 初始化的时候启动lnd节点
         */
        LogUtils.e(TAG, "------------------getFilesDir------------------" + getApplicationContext().getFilesDir());
        LogUtils.e(TAG, "------------------getExternalCacheDir------------------" + getApplicationContext().getExternalCacheDir());
        Lndmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + "  --trickledelay=5000 --alias=alice\n" +
                "--listen=0.0.0.0:9735\n" +
                "--bitcoin.active --bitcoin.regtest --bitcoin.node=bitcoind\n" +
                "--bitcoind.rpchost=16.162.119.13:18332 --bitcoind.rpcuser=polaruser\n" +
                "--bitcoind.rpcpass=polarpass\n" +
                "--bitcoind.zmqpubrawblock=tcp://16.162.119.13:28332\n" +
                "--bitcoind.zmqpubrawtx=tcp://16.162.119.13:28333", new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                LogUtils.e(TAG, "------------------startonResponse-----------------" + bytes.toString());
            }
        });
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
