package com.omni.wallet_mainnet.view.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;


public class LoadingDialog {

    private static final String TAG = LoadingDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCancelable = false;
    private ObjectAnimator mRotationAnimator;
    private DialogInterface.OnDismissListener mDismissListener;

    public LoadingDialog(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * show the dialog
     * 展示
     */
    public void show() {
        
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme_loading)
                    .setContentView(com.omni.wallet_mainnet.framelibrary.R.layout.layout_dialog_loading)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(mCancelable)
                    .setOnDismissListener(mDismissListener)
                    .create();
        }
        ImageView waitingIcon = mAlertDialog.getViewById(R.id.iv_dialog_loading);
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
        // 旋转动画(rotate animate)
        waitingIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRotationAnimator = ObjectAnimator.ofFloat(waitingIcon, "rotation", 0, 359f);
        mRotationAnimator.setInterpolator(new LinearInterpolator());
        mRotationAnimator.setDuration(4000);
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

