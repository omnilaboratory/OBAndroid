package com.omni.wallet.view.popupwindow.payinvoice;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.RefConstants;
import com.omni.wallet.utils.UriUtil;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import routerrpc.RouterOuterClass;

/**
 * PayInvoiceStepOne的弹窗
 */
public class PayInvoiceStepOnePopupWindow {
    private static final String TAG = PayInvoiceStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    PayInvoiceStepTwoPopupWindow mPayInvoiceStepTwoPopupWindow;
    RelativeLayout shareLayout;
    String toNodeAddress;
    long payAmount;
    String lnInvoice;

    public PayInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view, String address, long assetId) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_pay_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            TextView fromNodeAddressTv = rootView.findViewById(R.id.tv_from_node_address);
            TextView fromNodeNameTv = rootView.findViewById(R.id.tv_from_node_name);
            EditText invoiceEdit = rootView.findViewById(R.id.edit_invoice);

            TextView fromNodeAddress1Tv = rootView.findViewById(R.id.tv_from_node_address_1);
            TextView fromNodeName1Tv = rootView.findViewById(R.id.tv_from_node_name_1);
            TextView toNodeAddress1Tv = rootView.findViewById(R.id.tv_to_node_address_1);
            TextView toNodeName1Tv = rootView.findViewById(R.id.tv_to_node_name_1);
            ImageView amountLogoTv = rootView.findViewById(R.id.iv_amount_logo);
            TextView amountPayTv = rootView.findViewById(R.id.tv_amount_pay);
            TextView amountPayExchangeTv = rootView.findViewById(R.id.tv_amount_pay_exchange);

            TextView fromNodeAddress2Tv = rootView.findViewById(R.id.tv_from_node_address_2);
            TextView fromNodeName2Tv = rootView.findViewById(R.id.tv_from_node_name_2);
            TextView toNodeAddress2Tv = rootView.findViewById(R.id.tv_to_node_address_2);
            TextView toNodeName2Tv = rootView.findViewById(R.id.tv_to_node_name_2);
            ImageView amountLogo1Tv = rootView.findViewById(R.id.iv_amount_logo_1);
            TextView amountPay1Tv = rootView.findViewById(R.id.tv_amount_pay_1);
            TextView amountPayExchange1Tv = rootView.findViewById(R.id.tv_amount_pay_exchange_1);

            TextView fromNodeAddress3Tv = rootView.findViewById(R.id.tv_from_node_address_3);
            TextView toNodeAddress3Tv = rootView.findViewById(R.id.tv_to_node_address_3);
            ImageView amountLogo2Tv = rootView.findViewById(R.id.iv_amount_logo_2);
            TextView amountUnitTv = rootView.findViewById(R.id.tv_amount_unit);
            TextView amountPay2Tv = rootView.findViewById(R.id.tv_amount_pay_2);
            TextView amountUnit1Tv = rootView.findViewById(R.id.tv_amount_unit_1);
            TextView payTimeTv = rootView.findViewById(R.id.tv_pay_time);
            TextView payTimeUnitTv = rootView.findViewById(R.id.tv_pay_time_unit);
            TextView failedMessageTv = rootView.findViewById(R.id.tv_failed_message);

            fromNodeAddressTv.setText(address);
            fromNodeAddress1Tv.setText(address);
            fromNodeAddress2Tv.setText(address);
            fromNodeAddress3Tv.setText(address);
            /**
             * @备注： 点击back关闭pay invoice 窗口
             * @description: Close pay invoice popup window when click back
             */
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            /**
             * @备注： 点击next显示invoice step two
             * @description: Show pay invoice step two when click next
             */
            rootView.findViewById(R.id.layout_next_to_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = invoiceEdit.getText().toString();
                    // Avoid index out of bounds. An Request with less than 11 characters isn't valid.
                    if (data.length() < 11) {
                        ToastUtils.showToast(mContext, "Length is greater than 11 characters");
                        return;
                    }
                    // convert to lower case
                    lnInvoice = data.toLowerCase();
                    // Remove the "lightning:" uri scheme if it is present, LND needs it without uri scheme
                    lnInvoice = UriUtil.removeURI(lnInvoice);
                    LightningOuterClass.PayReqString decodePaymentRequest = LightningOuterClass.PayReqString.newBuilder()
                            .setPayReq(lnInvoice)
                            .build();
                    Obdmobile.decodePayReq(decodePaymentRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------decodePaymentOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.PayReq resp = LightningOuterClass.PayReq.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + resp);
                                toNodeAddress = resp.getDestination();
                                payAmount = resp.getAmount();
                                rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                                rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.GONE);
                                toNodeAddress1Tv.setText(toNodeAddress);
                                amountPayTv.setText(payAmount + "");
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            /**
             * @备注： 点击back显示invoice step one
             * @description: Show pay invoice step one when click next
             */
            rootView.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_one).setVisibility(View.VISIBLE);
                }
            });
            /**
             * @备注： 点击pay执行支付操作，根据支付结果跳转到succeed或者failed步骤
             * @description: Click pay to execute the payment operation, and jump to the successful or failed step according to the payment result
             */
            rootView.findViewById(R.id.layout_pay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long feeLimit = calculateAbsoluteFeeLimit(payAmount);
                    RouterOuterClass.SendPaymentRequest sendPaymentRequest = RouterOuterClass.SendPaymentRequest.newBuilder()
                            .setAssetId((int) assetId)
                            .setPaymentRequest(lnInvoice)
                            .setFeeLimitMsat(feeLimit)
                            .setTimeoutSeconds(RefConstants.TIMEOUT_MEDIUM * RefConstants.TOR_TIMEOUT_MULTIPLIER)
                            .setMaxParts(RefConstants.LN_MAX_PARTS)
                            .build();
                    Obdmobile.sendPaymentSync(sendPaymentRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------sendPaymentOnError------------------" + e.getMessage());
                            rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                            rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                            rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                            toNodeAddress3Tv.setText(toNodeAddress);
                            amountPay2Tv.setText(payAmount + "");
                            failedMessageTv.setText(e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.Payment resp = LightningOuterClass.Payment.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------sendPaymentOnResponse-----------------" + resp);
                                rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.GONE);
                                rootView.findViewById(R.id.lv_pay_invoice_step_three).setVisibility(View.VISIBLE);
                                rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                toNodeAddress2Tv.setText(toNodeAddress);
                                amountPay1Tv.setText(payAmount + "");
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
            shareLayout = rootView.findViewById(R.id.layout_share);
            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @备注： 点击back后退到第二步
             * @description: Click back button,back to step two
             */
            rootView.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_pay_invoice_step_two).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_pay_invoice_step_failed).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.GONE);
                }
            });
            /**
             * @备注： 点击share to，显示可分享的选项
             * @description: Click share to button,then show the layout for select options
             */
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(shareLayout.toString());
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            /**
             * @备注： 点击face book 图标
             * @description: Click face book icon
             */
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @备注： 点击twitter 图标
             * @description: Click twitter icon
             */
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @备注： 点击cancel 按钮
             * @description: Click cancel button
             */
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });

            /**
             * @备注： close 按钮
             * @description: Click close button
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

    public static long calculateAbsoluteFeeLimit(long amountSatToSend) {
        long absFee;
        if (amountSatToSend <= RefConstants.LN_PAYMENT_FEE_THRESHOLD) {
            absFee = (long) (Math.sqrt(amountSatToSend));
        } else {
            absFee = (long) (getRelativeSettingsFeeLimit() * amountSatToSend);
        }
        return Math.max(absFee, 3L);
    }

    public static float getRelativeSettingsFeeLimit() {
        String lightning_feeLimit = "3%";
        String feePercent = lightning_feeLimit.replace("%", "");
        float feeMultiplier = 1f;
        if (!feePercent.equals("None")) {
            feeMultiplier = Integer.parseInt(feePercent) / 100f;
        }
        return feeMultiplier;
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
