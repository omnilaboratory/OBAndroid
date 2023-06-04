package com.omni.wallet_mainnet.ui.activity;

import android.os.Bundle;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.base.BaseWebViewActivity;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;

/**
 * 展示H5的Activity
 */

public class WebViewActivity extends BaseWebViewActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private String mNextPageClass;// 下一个页面的class(The class for next page)
    private Bundle mBundle;

    /**
     * The activity for the page to jump when close the page
     * 关闭之后需要跳转的Activity
     */

    public static final String KEY_NEXT_PAGE = "nextPageKey";

    @Override
    protected int getContentView() {
        return R.layout.activity_webview;
    }

    @Override
    protected int getWebViewId() {
        return R.id.main_web_view;
    }

    @Override
    protected void getBundleData(Bundle bundle) {
        super.getBundleData(bundle);
        this.mBundle = bundle;
        mNextPageClass = bundle.getString(KEY_NEXT_PAGE);
    }


    @Override
    protected void release() {
        if (!StringUtils.isEmpty(mNextPageClass)) {
            try {
                Class clazz = Class.forName(mNextPageClass);
                switchActivity(clazz, mBundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
