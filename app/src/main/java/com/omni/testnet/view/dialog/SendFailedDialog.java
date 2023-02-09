package com.omni.testnet.view.dialog;

import android.content.Context;
import android.widget.TextView;

import com.omni.testnet.R;
import com.omni.testnet.baselibrary.dialog.AlertDialog;

/**
 * 汉: 发送失败的弹窗
 * En: Send Failed Dialog
 * author: guoyalei
 * date: 2022/10/24
 */
public class SendFailedDialog {
    private Context mContext;
    private AlertDialog mAlertDialog;

    public SendFailedDialog(Context context) {
        this.mContext = context;
    }

    public void show(String message) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.dialog_send_failed)
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .create();
        }
        TextView info = mAlertDialog.getViewById(R.id.tv_failed_message);
        info.setText(message);
        mAlertDialog.show();
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
