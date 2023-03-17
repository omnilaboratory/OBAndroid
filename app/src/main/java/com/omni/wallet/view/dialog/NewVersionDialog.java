package com.omni.wallet.view.dialog;

import android.content.Context;
import android.view.View;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;

/**
 * 汉: 新版本提示的弹窗
 * En: NewVersionDialog
 * author: guoyalei
 * date: 2023/3/17
 */
public class NewVersionDialog {
    private static final String TAG = NewVersionDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public NewVersionDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_new_version)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        /**
         * @描述： 点击 update
         * @desc: click update button
         */
        mAlertDialog.findViewById(R.id.tv_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        /**
         * @描述： 点击 close
         * @desc: click close button
         */
        mAlertDialog.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
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
