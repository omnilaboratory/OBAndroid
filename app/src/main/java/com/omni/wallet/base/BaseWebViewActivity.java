package com.omni.wallet.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.view.webView.CustomWebView;
import com.omni.wallet.baselibrary.view.webView.DefaultWebViewCallback;
import com.omni.wallet.entity.js.JavaScriptInterface;
import com.omni.wallet.framelibrary.common.Constants;
import com.omni.wallet.framelibrary.view.navigationBar.DefaultNavigationBar;


/**
 * WebView的基类
 * Created by fa on 2018/10/18.
 */

public abstract class BaseWebViewActivity extends AppBaseActivity {
    private static final String TAG = BaseWebViewActivity.class.getSimpleName();

    protected CustomWebView mWebView;
    protected String mUrl;
    protected String mTitle;
    protected JavaScriptInterface mInterfaceObject;
    private boolean mChangeTitle = true;// 标题是否随网页变化(Whether the title changes with the page)

    // Url
    public static final String KEY_PAGE_URL = "showUrlKey";
    // 标题文字(Title Text)
    public static final String KEY_PAGE_TITLE = "pageTitleKey";
    // 标题是否随网页变化(Whether the title changes with the page)
    public static final String KEY_CHANGE_TITLE = "changeTitleKey";

    @Override
    protected void getBundleData(Bundle bundle) {
        mUrl = bundle.getString(KEY_PAGE_URL);
        mTitle = bundle.getString(KEY_PAGE_TITLE);
        mChangeTitle = bundle.getBoolean(KEY_CHANGE_TITLE, true);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_web_view_common;
    }


    @Override
    protected void initHeader() {
        super.initHeader();
        if (mTitleView == null) {
            mTitle = StringUtils.isEmpty(mTitle) ? "" : mTitle;
            mTitleView = new DefaultNavigationBar.Builder(mContext)
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mWebView.canGoBack()) {
                                mWebView.goBack();
                            } else {
                                finish();
                            }
                        }
                    })
                    .setTitle(mTitle)
                    .build();
        }
    }

    @Override
    protected void initView() {
        if (getWebViewId() != 0) {
            mWebView = findViewById(getWebViewId());
        }
        // 设置JS调用原生方法的接口(Set the interface for JS to call native methods)
        mInterfaceObject = getJavaScriptInterface();
        if (mWebView != null) {
            mWebView.addJavascriptInterface(mInterfaceObject, Constants.H5_JS_ELEMENT);
            mWebView.setWebViewCallBack(new MyWebViewCallback());
        }
    }

    protected JavaScriptInterface getJavaScriptInterface() {
        return new JavaScriptInterface(mContext);
    }

    @Override
    protected void initData() {
        if (mWebView == null) {
            return;
        }
        if (!StringUtils.isEmpty(mUrl) && mUrl.startsWith("http")) {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    /**
     * @描述： WebView的回调
     * @desc: Callback of WebView
     */
    private class MyWebViewCallback extends DefaultWebViewCallback {

        @Override
        public void onWebViewPageFinish(WebView webView) {
            /**
             * @描述： 设置标题
             * @desc: set title
             */
            if (changeTitle() && mTitleView != null) {
                String title = webView.getTitle();
                mTitleView.setTitleText(title);
            }
        }

        @Override
        public boolean onWebViewShouldOverrideUrl(WebView webView, String url) {
//            LogUtils.e(TAG, "===捕获的Url是===>" + url);
            return BaseWebViewActivity.this.onWebViewShouldOverrideUrl(webView, url);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        // 友盟QQ分享的回调
//        UMUtils.with(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * @描述： 释放WebView
         * @desc: Release WebView
         */
        if (mWebView != null) {
            mWebView.releaseWebView();
        }
        /**
         * @描述： 释放Js接口里边的相关资源
         * @desc: Release relevant resources in the Js interface
         */
        if (mInterfaceObject != null) {
            mInterfaceObject.release();
        }
        /**
         * @描述： 资源释放
         * @desc: Resource release
         */
        release();
    }


    protected int getWebViewId() {
        return R.id.web_view_common;
    }

    protected boolean changeTitle() {
        return mChangeTitle;
    }

    protected void release() {

    }

    protected boolean onWebViewShouldOverrideUrl(WebView webView, String url) {
        return false;
    }
}
