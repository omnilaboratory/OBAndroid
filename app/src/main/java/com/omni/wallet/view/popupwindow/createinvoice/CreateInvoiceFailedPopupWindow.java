package com.omni.wallet.view.popupwindow.createinvoice;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉:创建发票失败的弹窗
 * En:CreateInvoiceFailedPopupWindow
 * author: guoyalei
 * date: 2022/10/10
 */
public class CreateInvoiceFailedPopupWindow {
    private static final String TAG = CreateInvoiceFailedPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow;

    public CreateInvoiceFailedPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_invoice_failed);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 点击back
            // click back button
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
                    mCreateInvoiceStepOnePopupWindow.show(view,"",0);
                }
            });
            // 点击share to
            // click share to button
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击底部close
            // click close button at bottom
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
