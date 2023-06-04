package com.omni.wallet_mainnet.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.framelibrary.entity.User;

/**
 * 汉: 通用创建通道提示的弹窗
 * En: GeneralCreateChannelTipDialog
 * author: guoyalei
 * date: 2023/2/23
 */
public class GeneralCreateChannelTipDialog {
    private static final String TAG = GeneralCreateChannelTipDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public GeneralCreateChannelTipDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_popupwindow_create_channel_tip_general)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        TextView tipTv = mAlertDialog.findViewById(R.id.tv_tip);
        /**
         * @描述： 点击back
         * @desc: click back button
         */
        mAlertDialog.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        /**
         * @描述： 点击create
         * @desc: click create button
         */
        mAlertDialog.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                CreateChannelDialog mCreateChannelDialog = new CreateChannelDialog(mContext);
                mCreateChannelDialog.show(User.getInstance().getBalanceAmount(mContext), User.getInstance().getWalletAddress(mContext), "");
            }
        });
        /**
         * @描述： 点击cancel
         * @desc: click cancel button
         */
        mAlertDialog.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
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
