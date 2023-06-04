package com.omni.wallet_mainnet.entity.js;

import android.content.Context;

import com.omni.wallet_mainnet.framelibrary.view.dialog.WaitingDialog;


/**
 * JS与安卓交互接口
 * Imp of JS and Android
 * author: fa
 * date: 2018/2/24 12:03.
 */
public class JavaScriptInterface extends BaseJavaScriptInterface {
    private static final String TAG = JavaScriptInterface.class.getSimpleName();
    private WaitingDialog mWaitingDialog;

    public JavaScriptInterface(Context context) {
        super(context);
    }

    @Override
    public void release() {
        super.release();
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }
}
