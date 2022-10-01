package com.omni.wallet.baselibrary.view.webView;

import android.content.Context;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.omni.wallet.baselibrary.utils.LogUtils;


class MyWebChromeClient extends WebChromeClient {

    private static final String TAG = MyWebChromeClient.class.getSimpleName();
    private Context mContext;
    private ProgressBar progressBar;

    MyWebChromeClient(Context context, ProgressBar progressBar) {
        this.mContext = context;
        this.progressBar = progressBar;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
                LogUtils.e(TAG, "======加载完成======>");
            }
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        // 获取网页标题，但是在goBack的时候可能不回调该方法，所以需要自己维护一个url和title对应的栈
        super.onReceivedTitle(view, title);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new JSAlertUtils(mContext).showAlertDialog(message, result);
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new JSAlertUtils(mContext).showConfirmDialog(message, result);
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // 自定义一个带输入的对话框由TextView和EditText构成
        new JSAlertUtils(mContext).showPromptDialog(message, defaultValue, result);
        return true;
    }
}
