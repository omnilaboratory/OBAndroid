package com.omni.wallet_mainnet.framelibrary.view.dialog;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.utils.RoundProgressBar;
import com.omni.wallet_mainnet.framelibrary.R;


/**
 * 带圆形进度条的上传对话框
 */

public class UploadProgressDialog {
    private static final String TAG = UploadProgressDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private RoundProgressBar mProgressBar;
    private int mCurrentProgress;


    public UploadProgressDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_upload)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .create();
            mProgressBar = mAlertDialog.getViewById(R.id.rpb_dialog_uploading);
        }
        mProgressBar.setProgress(0);
        mAlertDialog.show();
    }


    public void updateProgress(int progress) {
        if (progress < mCurrentProgress) {
            return;
        }
        if (mProgressBar != null && mAlertDialog.isShowing()) {
            mProgressBar.setProgress(progress);
            mCurrentProgress = progress;
        }
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}
