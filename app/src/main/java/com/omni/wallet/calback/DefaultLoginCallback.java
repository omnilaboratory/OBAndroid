package com.omni.wallet.calback;

import android.content.Context;

import com.omni.wallet.framelibrary.utils.LoginUtils;


/**
 * 默认的登录回调
 * Login default callback
 * Created by fa on 2019/9/18.
 */

public class DefaultLoginCallback implements LoginUtils.LoginCallback {
    private static final String TAG = DefaultLoginCallback.class.getSimpleName();

    @Override
    public void onLoginSuccess(Context context) {
    }

    @Override
    public void onLoginFail(Context context, String code, String msg) {

    }
}
