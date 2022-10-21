package com.omni.wallet.view.popupwindow.createinvoice;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.view.popupwindow.SelectTimePopupWindow;

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
    RelativeLayout shareLayout;
    Button timeButton;
    SelectTimePopupWindow mSelectTimePopupWindow;

    public CreateInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            timeButton = rootView.findViewById(R.id.btn_time);
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectTimePopupWindow = new SelectTimePopupWindow(mContext);
                    mSelectTimePopupWindow.setOnItemClickCallback(new SelectTimePopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view) {
                            switch (view.getId()) {
                                case R.id.tv_minutes:
                                    timeButton.setText(R.string.minutes);
                                    break;
                                case R.id.tv_hours:
                                    timeButton.setText(R.string.hours);
                                    break;
                                case R.id.tv_days:
                                    timeButton.setText(R.string.day);
                                    break;
                            }
                        }
                    });
                    mSelectTimePopupWindow.show(v);
                }
            });

            /**
             * @描述： 设置进度条
             * @desc: set progress bar
             */
            ProgressBar mProgressBar = rootView.findViewById(R.id.progressbar);
            float barValue = (float) ((double) 100 / (double) 600);
            mProgressBar.setProgress((int) (barValue * 100f));
            /**
             * @描述： 点击Back
             * @desc: click back button
             */
            rootView.findViewById(R.id.layout_back_to_none).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            /**
             * @描述： 点击Next
             * @desc: click next button
             */
            rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.GONE);
//                    rootView.findViewById(R.id.lv_create_invoice_failed).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_create_invoice_success).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                }
            });
            /**
             * @描述： 点击Back
             * @desc: click back button
             */
            rootView.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_create_invoice_failed).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击失败页share to
             * @desc: click share to button in failed page
             */
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            /**
             * @描述： 点击Back
             * @desc: click back button
             */
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_create_invoice_success).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
                }
            });

            /**
             * for success page
             * 成功页面
             */
            shareLayout = rootView.findViewById(R.id.layout_share_success);
            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击成功页 share to
             * @desc: click share to button in success page
             */
            rootView.findViewById(R.id.layout_share_to_success).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            /**
             * @描述： 点击成功页 facebook
             * @desc: click facebook button in success page
             */
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击成功页 twitter
             * @desc: click twitter button in success page
             */
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击cancel
             * @desc: click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            /**
             * @描述： 点击close
             * @desc: click close button
             */
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
