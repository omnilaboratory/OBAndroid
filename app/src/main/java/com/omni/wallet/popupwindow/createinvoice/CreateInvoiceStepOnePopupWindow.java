package com.omni.wallet.popupwindow.createinvoice;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 创建发票的步骤一弹窗
 * En: CreateInvoiceStepOnePopupWindow
 * author: guoyalei
 * date: 2022/10/9
 */
public class CreateInvoiceStepOnePopupWindow {
    private static final String TAG = CreateInvoiceStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    CreateInvoiceFailedPopupWindow mCreateInvoiceFailedPopupWindow;
    CreateInvoiceSuccessPopupWindow mCreateInvoiceSuccessPopupWindow;

    public CreateInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 设置进度条
            ProgressBar mProgressBar = rootView.findViewById(R.id.progressbar);
            float barValue = (float) ((double) 100 / (double) 600);
            mProgressBar.setProgress((int) (barValue * 100f));
            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击next
            rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
//                    mCreateInvoiceFailedPopupWindow = new CreateInvoiceFailedPopupWindow(mContext);
//                    mCreateInvoiceFailedPopupWindow.show(view);
                    mCreateInvoiceSuccessPopupWindow = new CreateInvoiceSuccessPopupWindow(mContext);
                    mCreateInvoiceSuccessPopupWindow.show(view);
                }
            });
            // 点击底部cancel
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
