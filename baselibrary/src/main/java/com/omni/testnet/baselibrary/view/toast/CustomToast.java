package com.omni.testnet.baselibrary.view.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * 自定义Toast
 */

public class CustomToast {
    private static final String TAG = CustomToast.class.getSimpleName();
    private static Toast mToast = null;
    private static TimeCount mCountTimer;

    @SuppressLint("ShowToast")
    private static void makeToast(Context context, CharSequence text, View view, int duration, int gravity, int yOffset) {
        // 区分版本，安卓9.0之后系统Toast是单例了，所以可以直接拿来用了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
            if (view != null) {
                mToast.setView(view);
            }
        } else {
            if (mToast == null) {
                mToast = new Toast(context.getApplicationContext());
            }
            if (view == null) {
                view = Toast.makeText(context.getApplicationContext(), text, duration).getView();
            }
            mToast.setView(view);
        }
        gravity = gravity == 0 ? mToast.getGravity() : gravity;
        yOffset = yOffset == 0 ? mToast.getYOffset() : yOffset;
        mToast.setGravity(gravity, mToast.getXOffset(), yOffset);
    }

    public static void showShortToast(Context context, CharSequence text, int gravity, int yOffset) {
        makeToast(context, text, null, Toast.LENGTH_SHORT, gravity, yOffset);
        mToast.show();
    }

    public static void showShortToast(Context context, CharSequence text) {
        showShortToast(context, text, 0, 0);
    }

    public static void showCenterShortToast(Context context, CharSequence text, int yOffset) {
        showShortToast(context, text, Gravity.CENTER, yOffset);
    }

    public static void showLongToast(Context context, CharSequence text, int gravity, int yOffset) {
        makeToast(context, text, null, Toast.LENGTH_LONG, gravity, yOffset);
        mToast.show();
    }

    public static void showLongToast(Context context, CharSequence text) {
        showLongToast(context, text, 0, 0);
    }

    public static void showCenterLongToast(Context context, CharSequence text, int yOffset) {
        showLongToast(context, text, Gravity.CENTER, yOffset);
    }

    public static void showHowLongToast(Context context, CharSequence text, int duration) {
        showLongToast(context, text);
        if (mCountTimer == null) {
            mCountTimer = new TimeCount(duration, Toast.LENGTH_LONG);
        }
        mCountTimer.cancel();
        mCountTimer.start();
    }

    /**
     * 自定义计时器
     */
    private static class TimeCount extends CountDownTimer {

        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); //millisInFuture总计时长，countDownInterval时间间隔(一般为1000ms)
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (mToast != null) {
                mToast.show();
            }
        }

        @Override
        public void onFinish() {
            cancelToast();
        }
    }

    /**
     * 取消Toast
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer = null;
        }
    }
}
