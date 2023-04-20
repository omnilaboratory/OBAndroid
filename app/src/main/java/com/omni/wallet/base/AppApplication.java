package com.omni.wallet.base;

import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.omni.wallet.baselibrary.base.BaseApplication;
import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.engine.OkHttpEngine;
import com.omni.wallet.baselibrary.http.interceptor.LogInterceptor;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.entity.event.BtcAndUsdtEvent;
import com.omni.wallet.entity.event.UpdateBalanceEvent;
import com.omni.wallet.framelibrary.base.DefaultExceptionCrashHandler;
import com.omni.wallet.framelibrary.entity.User;

import org.conscrypt.Conscrypt;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Security;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * Application
 */
public class AppApplication extends BaseApplication {
    private static final String TAG = AppApplication.class.getSimpleName();
    private static AppApplication mContext;
    Handler handler = new Handler();
    Handler balanceHandler = new Handler();

    public AppApplication() {
        mContext = this;

        RxJavaPlugins.setErrorHandler(e -> {
            if (e.getMessage() != null && e.getMessage().contains("shutdownNow")) {
                // Is propagated from gRPC when shutting down channel
            } else {
                LogUtils.e("RxJava", e.getMessage());
            }
        });
    }

    public static AppApplication getAppContext() {
        return mContext;
    }

    @Override
    protected void init() {
        /**
         * @描述： grpc相关：在使用前必须“安装”Conscrypt
         * @desc: Bundling Conscrypt,you must still "install" Conscrypt before use.
         */
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        /**
         * @描述： 去掉安卓P启动时候的警告弹窗
         * @desc: Remove the warning pop-up window when Android P starts
         */
        AppUtils.closeAndroidPDialog();
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
        LogUtils.e(TAG, "------------------getExternalFilesDir------------------" + getApplicationContext().getExternalFilesDir(null));
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setConnectTimeout(30000)
                .setReadTimeout(30000)
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(mContext, config);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String btcPrice = User.getInstance().getBtcPrice(mContext);
                if (btcPrice == null) {
                    User.getInstance().setBtcPrice(mContext, "16000");
                    User.getInstance().setBtcPriceChange(mContext, "0");
                }
                String usdtPrice = User.getInstance().getUsdtPrice(mContext);
                if (usdtPrice == null) {
                    User.getInstance().setUsdtPrice(mContext, "1");
                }
                getBtcPrice();
                // 在此处添加执行的代码
                handler.postDelayed(this, 60000);// 60s后执行

                getTotalBlock();
            }
        };

        handler.postDelayed(runnable, 0);// 打开定时器立即执行
        // 更新余额的定时监听
        Runnable balanceRunnable = new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new UpdateBalanceEvent());
                // 在此处添加执行的代码
                balanceHandler.postDelayed(this, 360000);// 360s后执行
            }
        };
        balanceHandler.postDelayed(balanceRunnable, 0);// 打开定时器立即执行
        getAssetList();
    }

    /**
     * get asset list
     * 获取资产列表数据
     */
    private void getAssetList() {
        HttpUtils.with(mContext)
                .get()
                .url("https://omnilaboratory.github.io/OBAndroid/app/src/main/assets/assetList.json")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        LogUtils.e(TAG, "getAssetListError:" + errorMsg);
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------getAssetList---------------------" + result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (ConstantInOB.networkType == NetworkType.TEST) {
                                LogUtils.e(TAG, "---------------testnet---------------------");
                                User.getInstance().setAssetListString(mContext, jsonObject.getString("testnet"));
                            } else if (ConstantInOB.networkType == NetworkType.REG) {
                                LogUtils.e(TAG, "---------------regtest---------------------");
                                User.getInstance().setAssetListString(mContext, jsonObject.getString("regtest"));
                            } else if (ConstantInOB.networkType == NetworkType.MAIN) {
                                LogUtils.e(TAG, "---------------mainnet---------------------");
                                User.getInstance().setAssetListString(mContext, jsonObject.getString("mainnet"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
    }

    public void getTotalBlock() {
        String jsonStr = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"omni_getinfo\", \"params\": []}";
        HttpUtils httpRequestUtils = HttpUtils.with(mContext);
        if (ConstantInOB.networkType.equals(NetworkType.MAIN)) {
            httpRequestUtils.get();
        } else {
            httpRequestUtils.postString().addContent(jsonStr);
        }
        httpRequestUtils.url(ConstantWithNetwork.getInstance(ConstantInOB.networkType).getGetBlockHeightUrl());
        httpRequestUtils.execute(new EngineCallback() {
            @Override
            public void onPreExecute(Context context, Map<String, Object> params) {

            }

            @Override
            public void onCancel(Context context) {

            }

            @Override
            public void onError(Context context, String errorCode, String errorMsg) {
                Log.d(TAG, "getTotalBlock onError: " + errorMsg);
            }

            @Override
            public void onSuccess(Context context, String result) {
                try {
                    if (ConstantInOB.networkType.equals(NetworkType.MAIN)) {
                        JSONObject jsonObject = new JSONObject(result);
                        String block = jsonObject.getString("height");
                        Log.e(TAG, "Total block:" + block);
                        User.getInstance().setTotalBlock(mContext, Long.parseLong(block));
                    } else {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                        String block = jsonObject1.getString("block");
                        Log.e(TAG, "Total block:" + block);
                        User.getInstance().setTotalBlock(mContext, Long.parseLong(block));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(Context context, byte[] result) {

            }

            @Override
            public void onProgressInThread(Context context, Progress progress) {

            }

            @Override
            public void onFileSuccess(Context context, String filePath) {

            }
        });
    }

    /**
     * Get BTC price related information
     * 获取btc价格相关信息
     */
    public void getBtcPrice() {
        HttpUtils.with(mContext)
                .get()
                .url("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=bitcoin&order=market_cap_desc&per_page=100&page=1&sparkline=false")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        Log.e(TAG, "getBTCPriceError:" + errorMsg);
                        /*try {
                            BTCData btcData = new BTCData(mContext);
                            if(btcData.checkDataIsEmpty()){
                                btcData.insert(0,btcData.getLastPrice());
                            }
                            getUsdtPrice();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "----------------BTC--------------------" + result);
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String btcPrice = jsonObject.getString("current_price");
                            String priceChange24h = jsonObject.getString("price_change_percentage_24h");
                            User.getInstance().setBtcPrice(mContext, btcPrice);
                            User.getInstance().setBtcPriceChange(mContext, priceChange24h);
                            /*BTCData btcData = new BTCData(mContext);
                            if(btcData.checkDataIsEmpty()){
                                btcData.insert(0,Double.parseDouble(btcPrice));
                            }else{
                                btcData.updatePrice(Double.parseDouble(btcPrice));
                            }*/

                            getUsdtPrice();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
    }

    /**
     * Get Usdt price related information
     * 获取Usdt价格相关信息
     */
    public void getUsdtPrice() {
        HttpUtils.with(mContext)
                .get()
                .url("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=tether&order=market_cap_desc&per_page=100&page=1&sparkline=false")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        Log.e(TAG, "getUsdtPriceError:" + errorMsg);
                        /*try {
                            DollarData dollarData = new DollarData(mContext);
                            if(dollarData.checkDataIsEmpty()){
                                dollarData.insert(0,dollarData.getLastPrice());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------Usdt---------------------" + result.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String usdtPrice = jsonObject.getString("current_price");
                            User.getInstance().setUsdtPrice(mContext, usdtPrice);
                            /*DollarData dollarData = new DollarData(mContext);
                            if(dollarData.checkDataIsEmpty()){
                                dollarData.insert(0,Double.parseDouble(usdtPrice));
                            }else{
                                dollarData.updatePrice(Double.parseDouble(usdtPrice));
                            }*/
                            EventBus.getDefault().post(new BtcAndUsdtEvent());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
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
