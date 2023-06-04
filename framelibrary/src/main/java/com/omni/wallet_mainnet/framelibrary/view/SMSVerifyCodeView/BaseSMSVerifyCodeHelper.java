package com.omni.wallet_mainnet.framelibrary.view.SMSVerifyCodeView;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;


/**
 * 接口发送短息验证码帮助类基类
 */

public abstract class BaseSMSVerifyCodeHelper {
    private static final String TAG = BaseSMSVerifyCodeHelper.class.getSimpleName();

    protected Context mContext;
    // 短信验证码接口防止重复请求
    protected boolean mRequestEnable = true;
    // 回调
    protected SMSVerifyCodeCallback mCallback;

    public BaseSMSVerifyCodeHelper(Context context) {
        this.mContext = context;
    }

    public void getSmsVerifyCode(String phone, String imageCode, String random) {
        if (!mRequestEnable) {
            return;
        }
        mRequestEnable = false;
        if (mCallback != null) {
            mCallback.onRequestSMSVerifyCode();
        }
        LogUtils.e(TAG, "接口获取验证码");
        // 接口获取验证码
        request(phone, imageCode, random);
    }

    /**
     * 接口发送短信验证码
     */
    protected abstract void request(String phone, String imageCode, String random);

    public void setCallback(SMSVerifyCodeCallback callback) {
        this.mCallback = callback;
    }

    public interface SMSVerifyCodeCallback {
        void onRequestSMSVerifyCode();

        void onSMSVerifyCodeSuccess();

        void onSMSVerifyCodeFail(String errorCode);

    }

    protected void onSMSVerifySuccess() {
        // 可以点击置为true
        mRequestEnable = true;
        // 回调
        if (mCallback != null) {
            mCallback.onSMSVerifyCodeSuccess();
        }
    }

    protected void onSMSVerifyFail(String code) {
        mRequestEnable = true;
        if (mCallback != null) {
            mCallback.onSMSVerifyCodeFail(code);
        }
    }
}
