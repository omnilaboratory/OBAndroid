package com.omni.wallet_mainnet.baselibrary.view.webView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.omni.wallet_mainnet.baselibrary.R;
import com.omni.wallet_mainnet.baselibrary.utils.AppUtils;
import com.omni.wallet_mainnet.baselibrary.utils.BasePreferencesUtils;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.NetWorkHelper;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;

import java.io.File;
import java.util.Stack;

/**
 * 自定义的WebView
 */
public class CustomWebView extends LinearLayout {

    private static final String TAG = CustomWebView.class.getSimpleName();
    private Context mContext;
    private WebView mWebView;
    private WebViewCallback mCallBack;// WebView的操作回调
    private String url;
    private static String cacheDirPath;
    private WebSettings mWebSettings;
    private WebViewBgView mWebViewBgView;// 处理加载错误
    private ProgressBar mProgressBar;
    private int mTouchX, mTouchY;
    private LongClickCallback mLonClickCallback;// WebView的长按监听

    public void setLonClickCallback(LongClickCallback lonClickCallback) {
        this.mLonClickCallback = lonClickCallback;
    }

    /**
     * 记录URL的栈
     * 规则：
     * 1.不可在{@code WebView.onPageFinished();}中开始记录URL
     * 2.记录需要屏蔽重定向URL
     */
    private final Stack<String> mUrls = new Stack<>();
    /**
     * 判断页面是否加载完成
     */
    private boolean mIsLoading = false;
    private WebViewLoadUtil mWebViewLoadUtils;

    private static String appCacheDirPath;// 缓存目录

    public CustomWebView(Context context) {
        this(context, null);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public WebSettings getWebSettings() {
        return mWebSettings;
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void init(Context context) {
        this.mContext = context;
        initView();
        initData(context);
    }

    private void initView() {
        cacheDirPath = mContext.getCacheDir().getAbsolutePath() + WebViewConfig.WEB_CACHE_DIRNAME;
        appCacheDirPath = AppUtils.SDCachePath(mContext) + WebViewConfig.WEB_CACHE_DIRNAME;
        View view = View.inflate(mContext, R.layout.view_custom_web_view, null);
        mProgressBar = view.findViewById(R.id.pb_page_load);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout mWebViewParent = view.findViewById(R.id.ll_web_view_parent);
        LayoutParams webViewParentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 传递ApplicationContext可以避免由于一直保持对某个Activity的引用，导致内存泄漏
        // 但是某些情况下在从ApplicationContext转换成Activity的时候会出现一些问题，所以这里还是需要使用Activity的Context
        // 在release的时候彻底释放WebView，避免内存泄漏  见下方release() 方法
        mWebView = new WebView(mContext);
        mWebViewParent.addView(mWebView, webViewParentParams);
        // 添加触摸监听
        mWebView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取触摸位置
                mTouchX = (int) event.getRawX();
                mTouchY = (int) event.getRawY();
                return false;
            }
        });
        // 添加长按监听
        mWebView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLonClickCallback != null) {
                    mLonClickCallback.onLongClick(mWebView, v, mTouchX, mTouchY);
                }
                return false;
            }
        });
        addView(view, params);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initData(final Context context) {
        mWebView.setVerticalScrollBarEnabled(false);  // 设置横竖滚动条不显示
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebSettings = mWebView.getSettings();
        // 设置图片是否自动加载
        if (Build.VERSION.SDK_INT >= 19) {
            mWebSettings.setLoadsImagesAutomatically(true);
        } else {
            mWebSettings.setLoadsImagesAutomatically(false);
        }
        mWebSettings.setJavaScriptEnabled(true);// 加载JS
        mWebSettings.setRenderPriority(RenderPriority.HIGH);// 设置渲染优先级
        mWebSettings.setBlockNetworkImage(false);// 是否阻止加载图片
        mWebSettings.setDatabaseEnabled(true);  // 开启 database storage API 功能
        mWebSettings.setDatabasePath(context.getCacheDir().getAbsolutePath());
        // Http和Https混合问题
        // MIXED_CONTENT_NEVER_ALLOW：Webview不允许一个安全的站点（https）去加载非安全的站点内容（http）,
        // 比如，https网页内容的图片是http链接。强烈建议App使用这种模式，因为这样更安全。
        // MIXED_CONTENT_ALWAYS_ALLOW：在这种模式下，WebView是可以在一个安全的站点（Https）里加载非安全的站点内容（Http）,
        // 这是WebView最不安全的操作模式，尽可能地不要使用这种模式。
        // MIXED_CONTENT_COMPATIBILITY_MODE：在这种模式下，当涉及到混合式内容时，WebView会尝试去兼容最新Web浏览器的风格。
        // 一些不安全的内容（Http）能被加载到一个安全的站点上（Https），而其他类型的内容将会被阻塞。
        // 这些内容的类型是被允许加载还是被阻塞可能会随着版本的不同而改变，并没有明确的定义。这种模式主要用于在App里面不能控制内容的渲染，
        // 但是又希望在一个安全的环境下运行。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebSettings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
        mWebSettings.setAppCacheEnabled(true);  // 设置 Application Caches 缓存目录
        mWebSettings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小10M
        // 设置当前Application缓存文件路径，Application Cache API能够开启需要指定Application具备写入权限的路径
        mWebSettings.setAppCachePath(context.getCacheDir().getAbsolutePath());
        mWebSettings.setAllowFileAccess(true);// 启用或禁止WebView访问文件数据
        // 设置缓存模式-默认——注意：使用LOAD_DEFAULT时，一定要建立自己缓存路径（系统默认路径不生效）)。
        // LOAD_CACHE_ONLY:  不使用网络，只读取本地缓存数据
        // LOAD_DEFAULT:  根据cache-control决定是否从网络上取数据。
        // LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no - cache，都使用缓存中的数据。
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放。
        mWebSettings.setSupportZoom(true);
        //设置WebView是否需要设置一个节点获取焦点当被回调的时候，默认true
        mWebSettings.setNeedInitialFocus(true);
        //设置WebView底层的布局算法，参考LayoutAlgorithm#NARROW_COLUMNS，将会重新生成WebView布局
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置WebView是否使用预览模式加载界面。
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        // 设置WebView代理字符串，如果String为null或为空，将使用系统默认值
        mWebSettings.setUserAgentString("");
        // 设置字体百分比(100%)
        mWebSettings.setTextZoom(100);
        //
        mWebView.setWebChromeClient(new MyWebChromeClient(context, mProgressBar));
        mWebView.setWebViewClient(new MyWebViewClient());
        // 设置文件下载的监听
        mWebViewLoadUtils = new WebViewLoadUtil(mContext, mWebView);
        mWebView.setDownloadListener(mWebViewLoadUtils);
        // 设置是否手势控制背景音乐播放
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mCallBack != null) {
                WebResourceResponse response = mCallBack.shouldInterceptRequest(view, url);
                if (response != null) {
                    return response;
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = request.getUrl().toString();
                if (mCallBack != null) {
                    WebResourceResponse response = mCallBack.shouldInterceptRequest(view, url);
                    if (response != null) {
                        return response;
                    }
                }
            }
            return super.shouldInterceptRequest(view, request);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (mIsLoading && mUrls.size() > 0) {
                mUrls.pop();
            }
            recordUrl(url);
            super.onPageStarted(view, url, favicon);
            if (mCallBack != null) {
                mCallBack.onWebViewPageStarted(view);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return overrideUrlLoading(mContext, view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mIsLoading || url.startsWith("about:")) {
                mIsLoading = false;
            }
            pageFinished(view);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            receivedError(mContext, view, errorCode);
        }

        // 重写此方法可以让webView处理https请求。
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 接受所有的证书
//            handler.proceed();
            handler.cancel();
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            if (mCallBack != null) {
                mCallBack.doUpdateVisitedHistory(view, url, isReload);
            }
        }
    }


    /**
     * 记录非重定向链接 避免刷新页面造成的重复入栈
     */
    private void recordUrl(String url) {
        //这里还可以根据自身业务来屏蔽一些链接放入URL栈
        if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase(getLastPageUrl())) {
            mUrls.push(url);
        }
    }

    /**
     * 获取上一页的链接
     */
    private synchronized String getLastPageUrl() {
        return mUrls.size() > 0 ? mUrls.peek() : null;
    }

    /**
     * 推出上一页链接
     */
    public String popLastPageUrl() {
        if (mUrls.size() >= 2) {
            mUrls.pop();// 将当前页的Url抛出
            return mUrls.pop();// 返回上一页的Url
        }
        return null;
    }

    /**
     * 处理抓取的链接
     */
    private boolean overrideUrlLoading(final Context context, WebView view, String url) {
        LogUtils.e(TAG, "捕捉到的链接是：" + url);
        // 过滤非Http的请求，比如打开外部App的URI,本地没有安装就下载
        if (!StringUtils.isEmpty(url) && url.toLowerCase().startsWith("file")) {
            if (mCallBack != null) {
                return mCallBack.onWebViewShouldOverrideUrl(view, url);
            }
            return false;
        } else if (!StringUtils.isEmpty(url) && (!url.toLowerCase().startsWith("http") || url.toLowerCase().endsWith("apk"))) {
            Uri uri = Uri.parse(url);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "===Exception===本地没有安装此App====>");
                BasePreferencesUtils.saveRequestUrlToLocal(context, url);
            }
            return true;
        } else if (!isRedirectUrl(view)) {
            if (mCallBack != null) {
                return mCallBack.onWebViewShouldOverrideUrl(view, url);
            }
            return false;
        }
        return false;
    }


    /**
     * 是否重定向链接
     */
    private boolean isRedirectUrl(WebView webView) {
        WebView.HitTestResult hit = webView.getHitTestResult();
        return hit == null;
    }

    /**
     * 加载完成的时候
     */
    private void pageFinished(WebView view) {
        mProgressBar.setProgress(100);
        postDelayed(new Runnable() {

            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        }, 100);
        // 等页面finish后再发起图片加载。
        if (!mWebSettings.getLoadsImagesAutomatically()) {
            mWebSettings.setLoadsImagesAutomatically(true);
        }
        if (mCallBack != null) {
            mCallBack.onWebViewPageFinish(view);
        }
    }

    /**
     * 页面请求出错
     */
    private void receivedError(final Context context, WebView view, int errorCode) {
        view.stopLoading();
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        if (WebViewClient.ERROR_HOST_LOOKUP == errorCode) {
            if (!NetWorkHelper.checkNetState(context)) {// 无网络
                onLoadNoNetwork(context);
                LogUtils.e(TAG, "======>无网络");
            } else {
                onLoadError(view);
                LogUtils.e(TAG, "======>加载出错");
            }
        } else {// 其余算是加载错误
            onLoadError(view);
            LogUtils.e(TAG, "======>加载出错");
        }
    }

    /**
     * 加载错误
     */
    private void onLoadError(WebView view) {
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setProgress(0);
        mWebViewBgView = new WebViewBgView(mContext.getApplicationContext(), mWebView, WebViewBgView.LOAD_ERROR, new BgViewClickCallBack());
        mWebViewBgView.showBgPage();
        if (mCallBack != null) {
            mCallBack.onWebViewLoadError(view);
        }
    }

    /**
     * 加载的时候网络无连接
     */
    private void onLoadNoNetwork(final Context context) {
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setProgress(0);
        mWebViewBgView = new WebViewBgView(context, mWebView, WebViewBgView.NO_NETWORK, new BgViewClickCallBack());
        mWebViewBgView.showBgPage();
        if (mCallBack != null) {
            mCallBack.onNotNetwork(mWebView);
        }
    }

    private class BgViewClickCallBack implements WebViewBgView.clickCallBack {

        @Override
        public void clickLoadError() {
            loadUrl(url);
        }

        @Override
        public void clickLoadNoNetwork() {
            loadUrl(url);
        }

    }

    /**
     * 加载网址
     */
    public void loadUrl(String url) {
        this.url = url;
        LogUtils.e(TAG, "请求的url====>" + url);
        // 加载之前判断网络
        if (!NetWorkHelper.checkNetState(mContext)) {// 无网络时
            ToastUtils.showToast(mContext, R.string.base_tip_network_not_connected);
        }
        // 加载
        if (mWebView != null) {
            if (mCallBack != null) {// 不是加载错误重新加载，就走正常加载
                mCallBack.onLoadUrl(mWebView);
            }
            if (mWebViewBgView != null && mWebViewBgView.isShowBgView) {
                mWebViewBgView.hideBgPage();
            }
            if (!StringUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
            }
        }
    }

    /**
     * 加载js方法
     *
     * @param methodName 方法名
     * @param params     参数
     */
    public void loadJavaScript(String methodName, String... params) {
        loadJavaScript(methodName, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果
                if (StringUtils.isEmpty(value)) {
                    LogUtils.e(TAG, "JS无返回，或者返回为空");
                } else {
                    LogUtils.e(TAG, "JS返回为：" + value);
                }
            }
        }, params);
    }

    /**
     * 加载js方法
     *
     * @param methodName 方法名
     * @param params     参数
     */
    public void loadJavaScript(String methodName, ValueCallback<String> callback, String... params) {
        StringBuilder builder = new StringBuilder();
        builder.append("javascript:");
        builder.append(methodName);
        builder.append("(");
        if (params != null && params.length > 0) {
            int length = params.length;
            for (int i = 0; i < length; i++) {
                builder.append("'");
                builder.append(params[i]);
                builder.append("'");
                if (i < length - 1) {
                    builder.append(",");
                }
            }
        }
        builder.append(")");
        LogUtils.e(TAG, "Js方法：" + builder.toString());
        // 因为该方法在 Android 4.4及以上版本才可使用，所以使用时需进行版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(builder.toString(), callback);
        } else {
            loadUrl(builder.toString());
        }
    }

    /**
     * 加载js方法，参数为Object
     *
     * @param methodName 方法名
     * @param params     参数
     */
    public void loadJavaScriptObject(String methodName, Object... params) {
        StringBuilder builder = new StringBuilder();
        builder.append("javascript:");
        builder.append(methodName);
        builder.append("(");
        if (params != null && params.length > 0) {
            int length = params.length;
            for (int i = 0; i < length; i++) {
                builder.append(params[i]);
                if (i < length - 1) {
                    builder.append(",");
                }
            }
        }
        builder.append(")");
        // 因为该方法在 Android 4.4及以上版本才可使用，所以使用时需进行版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(builder.toString(), new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    if (StringUtils.isEmpty(value)) {
                        LogUtils.e(TAG, "JS无返回，或者返回为空");
                    } else {
                        LogUtils.e(TAG, "JS返回为：" + value);
                    }
                }
            });
        } else {
            loadUrl(builder.toString());
        }
        LogUtils.e(TAG, "加载Js：" + builder.toString());
    }

    /**
     * WebView的重新加载
     */
    public void webViewReload() {
        mWebView.reload();
    }

    /**
     * 停止加载
     */
    public void stopLoading() {
        mWebView.stopLoading();
    }

    /**
     * 设置回调函数
     */
    public void setWebViewCallBack(WebViewCallback callBack) {
        this.mCallBack = callBack;
    }

    /**
     * WebView操作的回调
     */
    public interface WebViewCallback {

        WebResourceResponse shouldInterceptRequest(WebView view, String url);

        void onWebViewPageStarted(WebView view);

        /**
         * 加载完成
         */
        void onWebViewPageFinish(WebView webView);

        /**
         * 加载出错
         */
        void onWebViewLoadError(WebView webView);

        /**
         * 加载Url(如果需要对捕获的URL做处理return true；否则return false，让浏览器自己处理；)
         */
        boolean onWebViewShouldOverrideUrl(WebView webView, String url);

        /**
         * 当没有网络连接的时候的回调
         */
        void onNotNetwork(WebView webView);

        /**
         * 加载连接
         */
        void onLoadUrl(WebView webView);

        /**
         * 当更新历史记录
         */
        void doUpdateVisitedHistory(WebView view, String url, boolean isReload);

    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        mWebView.clearCache(true);
    }

    /**
     * 设置WebView无缓存
     */
    public void setWebViewNoCache() {
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    /**
     * 删除某个时间点之前的缓存
     */
    public static int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 清除WebView缓存
     */
    public static void clearWebViewCache(Context context) {
        LogUtils.e(TAG, "删除缓存====>");
        // 清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // h5 缓存文件
        File appCacheFile = new File(appCacheDirPath);
        LogUtils.e(TAG, "appCacheDir path=" + appCacheFile.getAbsolutePath());
        // WebView 缓存文件
        File webViewCacheDir = new File(cacheDirPath);
        LogUtils.e(TAG, "webViewCacheDir path=" + webViewCacheDir.getAbsolutePath());
        // 删除webView 缓存目录
        if (webViewCacheDir.exists()) {
            deleteFile(webViewCacheDir);
        }
        // 删除webView 缓存 缓存目录
        if (appCacheFile.exists()) {
            deleteFile(appCacheFile);
        }
    }

    /**
     * 递归删除 文件/文件夹
     */
    public static void deleteFile(File file) {
        LogUtils.i(TAG, "delete logToFile path=" + file.getAbsolutePath());
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            LogUtils.e(TAG, "delete logToFile no exists " + file.getAbsolutePath());
        }
    }

    /**
     * 添加JS和安卓方法绑定的接口
     */
    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object htmlObject, String string) {
        mWebView.addJavascriptInterface(htmlObject, string);
    }

    public void reload() {
        mWebView.reload();
    }

    public void onResume() {
        mWebView.onResume();
    }

    public void onPause() {
//        if (mPageReloadOnPause) {
//            LogUtils.e(TAG, "========mWebView reload=======》");
//            mWebView.reload();// 便于切换到后台的时候暂停视频
//        }
        mWebView.onPause();
    }

    public void resumeTimer() {
        mWebView.resumeTimers();
    }

    public void pauseTimer() {
        mWebView.pauseTimers();
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public void clearHistory() {
        mWebView.clearHistory();
    }

    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(script, resultCallback);
        }
    }

    /**
     * 长按回调
     */
    public interface LongClickCallback {
        boolean onLongClick(WebView webView, View v, int x, int y);
    }

    /**
     * 释放WebView
     */
    public void releaseWebView() {
        if (mWebView != null) {
            LogUtils.e(TAG, "WebView被释放===>");
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，
            // 需要先onDetachedFromWindow()，再destroy()
            //  mWebViewParent.removeAllViews(); 跟下边getParent()貌似一个意思
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            // 该方法是替换的过期方法  mWebView.clearView();
            mWebView.loadUrl("about:blank");
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        // 下载工具类里边的监听解除注册
        if (mWebViewLoadUtils != null) {
            mWebViewLoadUtils.release();
        }
    }
}
