package com.omni.testnet.framelibrary.http.callback;

import android.app.Activity;
import android.content.Context;

import com.omni.testnet.baselibrary.base.DefaultActivityLifecycleCallbacks;
import com.omni.testnet.framelibrary.view.dialog.WaitingDialog;


/**
 * 带Dialog的回调
 */

public class DialogHttpCallback<T> extends DefaultHttpCallback<T> {
    private static final String TAG = DialogHttpCallback.class.getSimpleName();
    private WaitingDialog mWaitingDialog;
    private String mShowText;


    /**
     * 这个方法主要是用来暴露给使用者，因为这个方法调用之后网络请求就真正开始了
     * 不用写成抽象方法，因为不一定所有使用者都需要覆盖
     */
    protected void onPreExecute(final Context context) {
        ((Activity) context).getApplication().registerActivityLifecycleCallbacks(new DefaultActivityLifecycleCallbacks());
        postMainThread(new Runnable() {
            @Override
            public void run() {
                if (mWaitingDialog == null) {
                    mWaitingDialog = new WaitingDialog(context);
                }
                mWaitingDialog.showWaitingDialog(mShowText);
            }
        });
    }

    @Override
    public void onSuccess(Context context, String result) {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
        super.onSuccess(context, result);
    }

    @Override
    protected void onResponseError(Context context, String errorCode, String errorMsg) {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
        super.onResponseError(context, errorCode, errorMsg);
    }

    public DialogHttpCallback setShowText(String showText) {
        this.mShowText = showText;
        return this;
    }
}
