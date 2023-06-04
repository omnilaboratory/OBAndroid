package com.omni.wallet_mainnet.framelibrary.view.SMSVerifyCodeView;

import android.content.Context;


/**
 * 接口发送短息验证码帮助类
 */

public class SMSVerifyCodeHelper extends BaseSMSVerifyCodeHelper {
    private static final String TAG = SMSVerifyCodeHelper.class.getSimpleName();

    public SMSVerifyCodeHelper(Context context) {
        super(context);
    }


    @Override
    protected void request(String phone, String imageCode, String random) {
//        HttpRequestUtils.sendSMS(mContext, phone, new DefaultHttpCallback<Object>() {
//            @Override
//            protected void onResponseSuccess(Object result) {
//                onSMSVerifySuccess();
//            }
//
//            @Override
//            protected void onResponseFail(Context context, String errorCode, String errorMsg) {
//                super.onResponseFail(context, errorCode, errorMsg);
//                onSMSVerifyFail(errorCode);
//            }
//
//            @Override
//            protected void onResponseError(Context context, String errorCode, String errorMsg) {
//                super.onResponseError(context, errorCode, errorMsg);
//                onSMSVerifyFail(errorCode);
//            }
//        });

    }

//    private void onSMSVerifySuccess() {
//        // 可以点击置为true
//        mRequestEnable = true;
//        // 回调
//        if (mCallback != null) {
//            mCallback.onSMSVerifyCodeSuccess();
//        }
//    }
//
//    private void onSMSVerifyFail() {
//        mRequestEnable = true;
//        if (mCallback != null) {
//            mCallback.onSMSVerifyCodeFail("-1");
//        }
//    }
}
