package com.omni.wallet.view.popupwindow.createinvoice;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.view.popupwindow.SelectAssetPopupWindow;
import com.omni.wallet.view.popupwindow.SelectTimePopupWindow;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

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
    SelectAssetPopupWindow mSelectAssetPopupWindow;
    SelectTimePopupWindow mSelectTimePopupWindow;
    String qrCodeUrl;

    public CreateInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String address, long assetId) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            TextView addressTv = rootView.findViewById(R.id.tv_address);
            LinearLayout selectAssetLayout = rootView.findViewById(R.id.layout_select_asset);
            timeButton = rootView.findViewById(R.id.btn_time);
            EditText amountEdit = rootView.findViewById(R.id.edit_amount);
            EditText amountTimeEdit = rootView.findViewById(R.id.edit_time);
            TextView amountSuccessTv = rootView.findViewById(R.id.tv_amount_success);
            TextView amountUnitSuccessTv = rootView.findViewById(R.id.tv_amount_unit_success);
            TextView timeSuccessTv = rootView.findViewById(R.id.tv_time_success);
            TextView timeUnitSuccessTv = rootView.findViewById(R.id.tv_time_unit_success);
            ImageView qrCodeIv = rootView.findViewById(R.id.iv_success_qrcode);
            TextView paymentSuccessTv = rootView.findViewById(R.id.tv_success_payment);
            ImageView copyIv = rootView.findViewById(R.id.iv_success_copy);
            TextView amountFailedTv = rootView.findViewById(R.id.tv_amount_failed);
            TextView amountUnitFailedTv = rootView.findViewById(R.id.tv_amount_unit_failed);
            TextView timeFailedTv = rootView.findViewById(R.id.tv_time_failed);
            TextView timeUnitFailedTv = rootView.findViewById(R.id.tv_time_unit_failed);
            TextView messageFailedTv = rootView.findViewById(R.id.tv_failed_message);
            addressTv.setText(address);
            amountSuccessTv.setText(amountEdit.getText().toString());
            timeSuccessTv.setText(amountTimeEdit.getText().toString());
            amountFailedTv.setText(amountEdit.getText().toString());
            timeFailedTv.setText(amountTimeEdit.getText().toString());

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
            selectAssetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectAssetPopupWindow = new SelectAssetPopupWindow(mContext);
                    mSelectAssetPopupWindow.setOnItemClickCallback(new SelectAssetPopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view, String item) {

                        }
                    });
                    mSelectAssetPopupWindow.show(v);
                }
            });
            copyIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //接收需要复制到粘贴板的地址
                    //Get the address which will copy to clipboard
                    String toCopyAddress = qrCodeUrl;
                    //接收需要复制成功的提示语
                    //Get the notice when you copy success
                    String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                    CopyUtil.SelfCopy(mContext, toCopyAddress, toastString);
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
                    LightningOuterClass.Invoice asyncInvoiceRequest = LightningOuterClass.Invoice.newBuilder()
                            .setAssetId((int) assetId)
                            .setAmount(Long.parseLong(amountEdit.getText().toString()))
                            .setMemo("暂未设置")
                            .setExpiry(Long.parseLong("86400")) // in seconds
                            .setPrivate(false)
                            .build();
                    Obdmobile.addInvoice(asyncInvoiceRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------addInvoiceOnError------------------" + e.getMessage());
                            messageFailedTv.setText(e.getMessage());
                            rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.GONE);
                            rootView.findViewById(R.id.lv_create_invoice_failed).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                            rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.AddInvoiceResponse resp = LightningOuterClass.AddInvoiceResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------addInvoiceOnResponse-----------------" + resp);
                                qrCodeUrl = UriUtil.generateLightningUri(resp.getPaymentRequest());
                                paymentSuccessTv.setText(qrCodeUrl);
                                Bitmap mQRBitmap = CodeUtils.createQRCode(qrCodeUrl, DisplayUtil.dp2px(mContext, 100));
                                qrCodeIv.setImageBitmap(mQRBitmap);
                                rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.GONE);
                                rootView.findViewById(R.id.lv_create_invoice_success).setVisibility(View.VISIBLE);
                                rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });

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
