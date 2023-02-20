package com.omni.wallet.view.dialog;

import android.content.Context;
import android.view.View;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;

/**
 * 汉: 通道释义的弹窗
 * En: WhatIsChannelPopupWindow
 * author: guoyalei
 * date: 2023/2/20
 */
public class WhatIsChannelDialog {
    private static final String TAG = WhatIsChannelDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public WhatIsChannelDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_what_is_channel)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
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
