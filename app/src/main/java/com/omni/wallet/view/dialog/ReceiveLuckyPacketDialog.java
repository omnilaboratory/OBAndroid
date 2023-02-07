package com.omni.wallet.view.dialog;

import android.content.Context;
import android.view.View;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;

/**
 * 汉: 收红包的弹窗
 * En: ReceiveRedPacketDialog
 * author: guoyalei
 * date: 2023/2/7
 */
public class ReceiveLuckyPacketDialog {
    private static final String TAG = ReceiveLuckyPacketDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public ReceiveLuckyPacketDialog(Context context) {
        this.mContext = context;
    }

    public void show(String address, long assetId, String invoiceAddr) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_receive_lucky_packet)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        /**
         * @备注： 扫描按钮
         * @description: Click scan button
         */
        mAlertDialog.findViewById(R.id.layout_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        /**
         * @备注： 关闭按钮
         * @description: Click close button
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
}
