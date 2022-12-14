package com.omni.wallet.view.dialog;

import android.content.Context;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;

/**
 * 汉: 发送成功的弹窗
 * En: Send Success Dialog
 * author: guoyalei
 * date: 2022/10/24
 */
public class SendSuccessDialog {
    private Context mContext;
    private AlertDialog mAlertDialog;

    public SendSuccessDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.dialog_send_success)
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .create();
        }
        mAlertDialog.show();
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
