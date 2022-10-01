package com.omni.wallet.baselibrary.utils;

import android.content.Context;

import com.omni.wallet.baselibrary.view.toast.CustomToast;

/**
 * Toast显示的工具类
 */

public class ToastUtils {
    private static final String TAG = ToastUtils.class.getSimpleName();

    //
    public static void showToast(Context context, CharSequence msg) {
        CustomToast.showCenterShortToast(context, msg, 0);
    }

    public static void showToast(Context context, int res) {
        CustomToast.showCenterShortToast(context, context.getString(res), 0);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        CustomToast.showCenterLongToast(context, msg, 0);
    }

    /**
     * 取消Toast
     */
    public static void cancelToast() {
        CustomToast.cancelToast();
    }

}
