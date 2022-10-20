package com.omni.wallet.view.popupwindow.payinvoice;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * PayInvoiceStepOne的弹窗
 */
public class PayInvoiceStepOnePopupWindow {
    private static final String TAG = PayInvoiceStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    PayInvoiceStepTwoPopupWindow mPayInvoiceStepTwoPopupWindow;
    RelativeLayout shareLayout;

    public PayInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_pay_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击next
            rootView.findViewById(R.id.layout_next_to_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                }
            });
            // 点击back
            rootView.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.VISIBLE);
                }
            });
            // 点击pay
            rootView.findViewById(R.id.layout_pay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
//                    rootView.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);

                }
            });
            shareLayout = rootView.findViewById(R.id.layout_share);
            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击back
            rootView.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
                }
            });
            // 点击share to
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(shareLayout.toString());
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            // 点击facebook
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击twitter
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击底部cancel
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });

            // 点击底部close
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
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
