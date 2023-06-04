package com.omni.wallet_mainnet.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.utils.DisplayUtil;
import com.omni.wallet_mainnet.thirdsupport.zxing.util.CodeUtils;

/**
 * 汉: 发票二维码弹窗额页面
 * En: InvoiceQRCodeDialog
 * author: guoyalei
 * date: 2023/3/16
 */
public class InvoiceQRCodeDialog {
    private static final String TAG = InvoiceQRCodeDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;

    public InvoiceQRCodeDialog(Context context) {
        this.mContext = context;
    }

    public void show(String qrCodeCotent) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_qr_code)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        ImageView qrCodeIv = mAlertDialog.findViewById(R.id.iv_qrcode);
        Bitmap mQRBitmap = CodeUtils.createQRCode(qrCodeCotent, DisplayUtil.dp2px(mContext, 250));
        qrCodeIv.setImageBitmap(mQRBitmap);
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
