package com.omni.wallet.thirdsupport.umeng.share.bean;

import android.app.Activity;
import android.content.Context;

import com.omni.wallet.thirdsupport.umeng.UMUtils;
import com.omni.wallet.thirdsupport.umeng.share.callback.ShareCallback;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;

/**
 * 分享信息构造
 */

public class ShareBuilder {
    public ShareBuilder(Activity activity) {
        this.mActivity = activity;
    }

    public ShareBuilder(Context context) {
        this.mActivity = (Activity) context;
    }

    public Activity mActivity;
    public String mText;
    public BaseShareBean mShareBean;
    public Target mShareTarget;
    public ShareCallback mShareCallback;


    public UMUtils create() {
        return new UMUtils(this);
    }

    public ShareBuilder text(String text) {
        mText = text;
        return this;
    }

    public ShareBuilder shareBean(BaseShareBean bean) {
        mShareBean = bean;
        return this;
    }

    public ShareBuilder target(Target target) {
        mShareTarget = target;
        return this;
    }

    public ShareBuilder callback(ShareCallback callback) {
        mShareCallback = callback;
        return this;
    }
}
