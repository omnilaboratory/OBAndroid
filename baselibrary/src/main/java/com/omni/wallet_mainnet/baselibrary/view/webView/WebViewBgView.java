package com.omni.wallet_mainnet.baselibrary.view.webView;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * @ClassName: WebViewBgView
 * @Description: WebView加载错误或者无网络的背景
 */
public class WebViewBgView {

    private static final String TAG = WebViewBgView.class.getSimpleName();

    private Context context;
    private WebView webView;
    private int flag;
    private View mBgView = null;// 背景的View
    private clickCallBack callBack;

    public static final int LOAD_ERROR = 1;// 连接出错
    public static final int NO_NETWORK = 2;// 网络无连接
    public boolean isShowBgView = false;// 是否显示页面

    public WebViewBgView(Context context, WebView webView, int flag, clickCallBack callBack) {
        this.context = context;
        this.webView = webView;
        this.flag = flag;
        this.callBack = callBack;
    }

    /**
     * 显示自定义错误提示页面，用一个View覆盖在WebView上
     */
    protected void showBgPage() {
        isShowBgView = true;
        webView.setVisibility(View.GONE);
        LinearLayout webParentView = (LinearLayout) webView.getParent();
        initBgPage();
        while (webParentView.getChildCount() > 1) {
            webParentView.removeViewAt(0);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        webParentView.addView(mBgView, 0, lp);
    }

    /**
     * @return void
     * @Title: hideErrorPage
     * @Description: 隐藏错误页面
     * @author eye_fa
     */
    protected void hideBgPage() {
        isShowBgView = false;
        webView.setVisibility(View.VISIBLE);
        LinearLayout webParentView = (LinearLayout) webView.getParent();
        while (webParentView.getChildCount() > 1) {
            webParentView.removeViewAt(0);
        }
    }

    /**
     * @return void
     * @Title: initErrorPage
     * @Description: 初始化
     * @author eye_fa
     */
    protected void initBgPage() {
        if (mBgView == null) {
            switch (flag) {
                case LOAD_ERROR:
                    mBgView = View.inflate(context, R.layout.view_webview_error_bg, null);
                    RelativeLayout rlLoadError = (RelativeLayout) mBgView.findViewById(R.id.rl_load_error);
                    rlLoadError.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                callBack.clickLoadError();
                            }
                        }
                    });
                    break;
                case NO_NETWORK:
                    mBgView = View.inflate(context, R.layout.view_webview_no_network_bg, null);
                    TextView tvReConnect = (TextView) mBgView.findViewById(R.id.tv_reconnect);
                    tvReConnect.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (callBack != null) {
                                callBack.clickLoadNoNetwork();
                            }
                        }
                    });
                    break;
            }
        }
    }

    /**
     * @author eye_fa
     * @ClassName: clickCallBack
     * @Description: 点击事件的回调接口
     * @date 2016-2-16 下午12:11:12
     */
    public interface clickCallBack {

        void clickLoadError();

        void clickLoadNoNetwork();
    }

}
