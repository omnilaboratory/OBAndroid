package com.omni.testnet.view.popupwindow.createinvoice;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.testnet.R;
import com.omni.testnet.baselibrary.utils.DisplayUtil;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.utils.ToastUtils;
import com.omni.testnet.baselibrary.view.BasePopWindow;
import com.omni.testnet.entity.ListAssetItemEntity;
import com.omni.testnet.entity.event.CreateInvoiceEvent;
import com.omni.testnet.thirdsupport.zxing.util.CodeUtils;
import com.omni.testnet.utils.CopyUtil;
import com.omni.testnet.utils.ShareUtil;
import com.omni.testnet.utils.UriUtil;
import com.omni.testnet.view.dialog.LoadingDialog;
import com.omni.testnet.view.popupwindow.SelectChannelBalancePopupWindow;
import com.omni.testnet.view.popupwindow.SelectTimePopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

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
    TextView assetMaxTv;
    TextView mCanSendTv;
    TextView mCanReceiveTv;
    ProgressBar mProgressBar;
    SelectChannelBalancePopupWindow mSelectChannelBalancePopupWindow;
    SelectTimePopupWindow mSelectTimePopupWindow;
    String mAddress;
    long mAssetId;
    String assetBalanceMax;
    String canReceive;
    String amountInput;
    String timeInput;
    String timeType;
    String qrCodeUrl;
    LoadingDialog mLoadingDialog;

    public CreateInvoiceStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String address, long assetId, long balanceAccount) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_invoice_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            mLoadingDialog = new LoadingDialog(mContext);
            mAddress = address;
            mAssetId = assetId;
            showStepOne(rootView);
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

    private void showStepOne(View rootView) {
        TextView addressTv = rootView.findViewById(R.id.tv_address);
        addressTv.setText(StringUtils.encodePubkey(mAddress));
        ImageView assetTypeIv = rootView.findViewById(R.id.iv_asset_type);
        TextView assetTypeTv = rootView.findViewById(R.id.tv_asset_type);
        assetMaxTv = rootView.findViewById(R.id.tv_asset_max);
        mCanSendTv = rootView.findViewById(R.id.tv_can_send);
        mCanReceiveTv = rootView.findViewById(R.id.tv_can_receive);
        mProgressBar = rootView.findViewById(R.id.progressbar);
        TextView amountMaxTv = rootView.findViewById(R.id.tv_amount_max);
        EditText amountEdit = rootView.findViewById(R.id.edit_amount);
        TextView amountUnitTv = rootView.findViewById(R.id.tv_amount_unit);
        Button timeButton = rootView.findViewById(R.id.btn_time);
        EditText amountTimeEdit = rootView.findViewById(R.id.edit_time);
        EditText memoEdit = rootView.findViewById(R.id.edit_memo);
        if (mAssetId == 0) {
            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeTv.setText("BTC");
            amountUnitTv.setText("BTC");
        } else {
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountUnitTv.setText("dollar");
        }
        getChannelBalance(mAssetId);
        RelativeLayout selectAssetLayout = rootView.findViewById(R.id.layout_select_asset);
        selectAssetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectChannelBalancePopupWindow = new SelectChannelBalancePopupWindow(mContext);
                mSelectChannelBalancePopupWindow.setOnItemClickCallback(new SelectChannelBalancePopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view, ListAssetItemEntity item) {
                        if (item.getPropertyid() == 0) {
                            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                            assetTypeTv.setText("BTC");
                            amountUnitTv.setText("BTC");
                        } else {
                            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                            assetTypeTv.setText("dollar");
                            amountUnitTv.setText("dollar");
                        }
                        mAssetId = item.getPropertyid();
                        getChannelBalance(mAssetId);
                    }
                });
                mSelectChannelBalancePopupWindow.show(v);
            }
        });
        amountMaxTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEdit.setText(assetBalanceMax);
            }
        });
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
                amountInput = amountEdit.getText().toString();
                timeInput = amountTimeEdit.getText().toString();
                timeType = timeButton.getText().toString();
                if (StringUtils.isEmpty(amountInput)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.create_invoice_amount));
                    return;
                }
                if (amountInput.equals("0")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.amount_greater_than_0));
                    return;
                }
                // TODO: 2022/11/23 最大值最小值的判断需要完善一下
                if ((Double.parseDouble(amountInput) * 100000000) - (Double.parseDouble(canReceive) * 100000000) > 0) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.credit_is_running_low));
                    return;
                }
                if (StringUtils.isEmpty(timeInput)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.enter_the_time));
                    return;
                }
                mLoadingDialog.show();
                LightningOuterClass.Invoice asyncInvoiceRequest;
                if (mAssetId == 0) {
                    asyncInvoiceRequest = LightningOuterClass.Invoice.newBuilder()
                            .setAssetId((int) mAssetId)
                            .setValueMsat((long) (Double.parseDouble(amountEdit.getText().toString()) * 100000000 * 1000))
                            .setMemo(memoEdit.getText().toString())
                            .setExpiry(Long.parseLong("86400")) // in seconds
                            .setPrivate(false)
                            .build();
                } else {
                    asyncInvoiceRequest = LightningOuterClass.Invoice.newBuilder()
                            .setAssetId((int) mAssetId)
                            .setAmount((long) (Double.parseDouble(amountEdit.getText().toString()) * 100000000))
                            .setMemo(memoEdit.getText().toString())
                            .setExpiry(Long.parseLong("86400")) // in seconds
                            .setPrivate(false)
                            .build();
                }
                Obdmobile.oB_AddInvoice(asyncInvoiceRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------addInvoiceOnError------------------" + e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.dismiss();
                                rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.GONE);
                                rootView.findViewById(R.id.lv_create_invoice_failed).setVisibility(View.VISIBLE);
                                rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                showStepFailed(rootView, e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        if (bytes == null) {
                            return;
                        }
                        try {
                            LightningOuterClass.AddInvoiceResponse resp = LightningOuterClass.AddInvoiceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------addInvoiceOnResponse-----------------" + resp);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new CreateInvoiceEvent());
                                    qrCodeUrl = UriUtil.generateLightningUri(resp.getPaymentRequest());
                                    mLoadingDialog.dismiss();
                                    rootView.findViewById(R.id.lv_create_invoice_step_one).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.lv_create_invoice_success).setVisibility(View.VISIBLE);
                                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
                                    showStepSuccess(rootView);
                                }
                            });
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    private void showStepSuccess(View rootView) {
        ImageView assetTypeSuccessIv = rootView.findViewById(R.id.iv_asset_type_success);
        TextView assetTypeSuccessTv = rootView.findViewById(R.id.tv_asset_type_success);
        TextView amountSuccessTv = rootView.findViewById(R.id.tv_amount_success);
        TextView amountUnitSuccessTv = rootView.findViewById(R.id.tv_amount_unit_success);
        TextView timeSuccessTv = rootView.findViewById(R.id.tv_time_success);
        TextView timeUnitSuccessTv = rootView.findViewById(R.id.tv_time_unit_success);
        ImageView qrCodeIv = rootView.findViewById(R.id.iv_success_qrcode);
        TextView paymentSuccessTv = rootView.findViewById(R.id.tv_success_payment);
        ImageView copyIv = rootView.findViewById(R.id.iv_success_copy);
        if (mAssetId == 0) {
            assetTypeSuccessIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeSuccessTv.setText("BTC");
            amountUnitSuccessTv.setText("BTC");
        } else {
            assetTypeSuccessIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeSuccessTv.setText("dollar");
            amountUnitSuccessTv.setText("dollar");
        }
        amountSuccessTv.setText(amountInput);
        timeSuccessTv.setText(timeInput);
        timeUnitSuccessTv.setText(timeType);
        paymentSuccessTv.setText(qrCodeUrl);
        Bitmap mQRBitmap = CodeUtils.createQRCode(qrCodeUrl, DisplayUtil.dp2px(mContext, 100));
        qrCodeIv.setImageBitmap(mQRBitmap);

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
                showStepOne(rootView);
            }
        });

        /**
         * for success page
         * 成功页面
         */
        RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share_success);
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
                ToastUtils.showToast(mContext, "Not yet open, please wait");
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
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, qrCodeUrl));
                shareLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showStepFailed(View rootView, String message) {
        ImageView assetTypeFailedIv = rootView.findViewById(R.id.iv_asset_type_failed);
        TextView assetTypeFailedTv = rootView.findViewById(R.id.tv_asset_type_failed);
        TextView amountFailedTv = rootView.findViewById(R.id.tv_amount_failed);
        TextView amountUnitFailedTv = rootView.findViewById(R.id.tv_amount_unit_failed);
        TextView timeFailedTv = rootView.findViewById(R.id.tv_time_failed);
        TextView timeUnitFailedTv = rootView.findViewById(R.id.tv_time_unit_failed);
        if (mAssetId == 0) {
            assetTypeFailedIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeFailedTv.setText("BTC");
            amountUnitFailedTv.setText("BTC");
        } else {
            assetTypeFailedIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeFailedTv.setText("dollar");
            amountUnitFailedTv.setText("dollar");
        }
        amountFailedTv.setText(amountInput);
        timeFailedTv.setText(timeInput);
        timeUnitFailedTv.setText(timeType);
        TextView messageFailedTv = rootView.findViewById(R.id.tv_failed_message);
        messageFailedTv.setText(message);
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
                showStepOne(rootView);
            }
        });
        /**
         * for fail page
         * 失败页面
         */
        RelativeLayout shareFailedLayout = rootView.findViewById(R.id.layout_share_failed);
        rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFailedLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @描述： 点击失败页share to
         * @desc: click share to button in failed page
         */
        rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFailedLayout.setVisibility(View.VISIBLE);
            }
        });
        /**
         * @描述： 点击失败页 facebook
         * @desc: click facebook button in fail page
         */
        rootView.findViewById(R.id.iv_facebook_share_failed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(mContext, "Not yet open, please wait");
                shareFailedLayout.setVisibility(View.GONE);
            }
        });
        /**
         * @描述： 点击失败页 twitter
         * @desc: click twitter button in fail page
         */
        rootView.findViewById(R.id.iv_twitter_share_failed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, message));
                shareFailedLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * get Channel Balance
     * 查询通道余额
     *
     * @param propertyid
     */
    private void getChannelBalance(long propertyid) {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) propertyid)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                            if (propertyid == 0) {
                                if (resp.getLocalBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    mCanSendTv.setText(df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000)) / 100000000));
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    mCanSendTv.setText(df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000)) / 100000000));
                                }
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                }
                                mCanReceiveTv.setText(canReceive);
                                if (resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000 + resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() / 1000 + resp.getRemoteBalance().getMsat() / 1000)) / 100000000);
                                }
                                assetMaxTv.setText(assetBalanceMax);
                            } else {
                                if (resp.getLocalBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    mCanSendTv.setText(df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat())) / 100000000));
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    mCanSendTv.setText(df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat())) / 100000000));
                                }
                                if (resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    canReceive = df.format(Double.parseDouble(String.valueOf(resp.getRemoteBalance().getMsat())) / 100000000);
                                }
                                mCanReceiveTv.setText(canReceive);
                                if (resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat() == 0) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat())) / 100000000);
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.00######");
                                    assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat())) / 100000000);
                                }
                                assetMaxTv.setText(assetBalanceMax);
                            }
                            /**
                             * @描述： 设置进度条
                             * @desc: set progress bar
                             */
                            long totalBalance = resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat();
                            float barValue = (float) ((double) resp.getLocalBalance().getMsat() / (double) totalBalance);
                            mProgressBar.setProgress((int) (barValue * 100f));
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
