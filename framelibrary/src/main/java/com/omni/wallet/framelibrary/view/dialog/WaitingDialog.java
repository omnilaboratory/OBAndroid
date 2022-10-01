package com.omni.wallet.framelibrary.view.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.R;


public class WaitingDialog {

    private static final String TAG = WaitingDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCancelable = false;
    private ObjectAnimator mRotationAnimator;
    private DialogInterface.OnDismissListener mDismissListener;

    public WaitingDialog(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 展示
     */
    public void show() {
        showWaitingDialog("");
    }

    /**
     * 展示
     */
    public void showWaitingDialog(String showText) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_loading)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(mCancelable)
                    .setOnDismissListener(mDismissListener)
                    .create();
        }
        ImageView waitingIcon = mAlertDialog.getViewById(R.id.iv_dialog_waiting);
        TextView waitingTv = mAlertDialog.getViewById(R.id.tv_dialog_waiting);
        // 设置描述文字
        if (!StringUtils.isEmpty(showText)) {
            waitingTv.setText(showText);
        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
        // 旋转动画
        waitingIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRotationAnimator = ObjectAnimator.ofFloat(waitingIcon, "rotation", 0, 359f);
        mRotationAnimator.setInterpolator(new LinearInterpolator());
        mRotationAnimator.setDuration(1200);
        mRotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mRotationAnimator.start();
    }

    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            if (mRotationAnimator != null) {
                mRotationAnimator.cancel();
            }
        }
    }

    public boolean isShowing() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }
}

