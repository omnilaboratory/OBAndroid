package com.omni.wallet.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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

    public void show(String message) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.dialog_send_success)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .setOnClickListener(R.id.layout_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    })
                    .create();
        }
        TextView info = mAlertDialog.getViewById(R.id.tv_success_message);
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
