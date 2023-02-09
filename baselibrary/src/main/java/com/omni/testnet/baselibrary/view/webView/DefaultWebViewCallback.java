package com.omni.testnet.baselibrary.view.webView;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * WebView的默认回调
 */

public class DefaultWebViewCallback implements CustomWebView.WebViewCallback {
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return null;
    }

    @Override
    public void onWebViewPageStarted(WebView view) {

    }

    @Override
    public void onWebViewPageFinish(WebView webView) {

    }

    @Override
    public void onWebViewLoadError(WebView webView) {

    }

    @Override
    public boolean onWebViewShouldOverrideUrl(WebView webView, String url) {
        return false;
    }

    @Override
    public void onNotNetwork(WebView webView) {

    }

    @Override
    public void onLoadUrl(WebView webView) {

    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {

    }
}
