package com.omni.wallet.popupwindow.payinvoice;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * PayInvoiceStepTwo的弹窗
 */
public class PayInvoiceStepTwoPopupWindow {
    private static final String TAG = PayInvoiceStepTwoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    PayInvoiceSuccessPopupWindow mPayInvoiceSuccessPopupWindow;
    PayInvoiceFailedPopupWindow mPayInvoiceFailedPopupWindow;

    public PayInvoiceStepTwoPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_pay_invoice_steptwo);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
                    mPayInvoiceStepOnePopupWindow.show(view);
                }
            });
            // 点击pay
            rootView.findViewById(R.id.layout_pay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mPayInvoiceSuccessPopupWindow = new PayInvoiceSuccessPopupWindow(mContext);
                    mPayInvoiceSuccessPopupWindow.show(view);
//                    mPayInvoiceFailedPopupWindow = new PayInvoiceFailedPopupWindow(mContext);
//                    mPayInvoiceFailedPopupWindow.show(view);
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
