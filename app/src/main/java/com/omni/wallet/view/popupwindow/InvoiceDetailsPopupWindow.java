package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.ShareUtil;
import com.omni.wallet.utils.UriUtil;

import java.text.DecimalFormat;

import lnrpc.LightningOuterClass;

/**
 * 汉: 发票详情的弹窗
 * En: InvoiceDetailsPopupWindow
 * author: guoyalei
 * date: 2023/3/6
 */
public class InvoiceDetailsPopupWindow {
    private static final String TAG = InvoiceDetailsPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public InvoiceDetailsPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, LightningOuterClass.Invoice item) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_invoice_details);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            TextView typeTv = rootView.findViewById(R.id.tv_type);
            ImageView typeIv = rootView.findViewById(R.id.iv_type);
            TextView statusTv = rootView.findViewById(R.id.tv_status);
            TextView memoTv = rootView.findViewById(R.id.tv_memo);
            ImageView assetTypeIv = rootView.findViewById(R.id.iv_asset_type);
            TextView assetTypeTv = rootView.findViewById(R.id.tv_asset_type);
            TextView amountTv = rootView.findViewById(R.id.tv_amount);
            TextView amountTypeTv = rootView.findViewById(R.id.tv_amount_type);
            TextView invoiceTv = rootView.findViewById(R.id.tv_invoice);

            typeTv.setText("INVOICE");
            Long amt = item.getValueMsat();
            Long amtPayed = item.getAmtPaidMsat();
            if (amt.equals(0L)) {
                // if no specific value was requested
                if (!amtPayed.equals(0L)) {
                    // The invoice has been payed
                    typeIv.setImageResource(R.mipmap.icon_failed_green);
                    statusTv.setText("Confirmed");
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (item.getAssetId() == 0) {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amtPayed / 1000)) / 100000000));
                    } else {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amtPayed)) / 100000000));
                    }
                } else {
                    // The invoice has not been payed yet
                    amountTv.setText("0.00");
                    typeIv.setImageResource(R.mipmap.icon_failed_red);
                    if (StringUtils.isEmpty(String.valueOf(item.getState()))) {
                        if (isInvoiceExpired(item)) {
                            // The invoice has expired
                            statusTv.setText("Expiryed");
                        } else {
                            // The invoice has not yet expired
                            statusTv.setText("Pending");
                        }
                    } else {
                        if (isInvoiceExpired(item) || item.getState() == LightningOuterClass.Invoice.InvoiceState.CANCELED) {
                            // The invoice has expired
                            statusTv.setText("Expiryed");
                        } else {
                            // The invoice has not yet expired
                            statusTv.setText("Pending");
                        }
                    }
                }
            } else {
                // if a specific value was requested
                if (isInvoicePayed(item)) {
                    // The invoice has been payed
                    typeIv.setImageResource(R.mipmap.icon_failed_green);
                    statusTv.setText("Confirmed");
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (item.getAssetId() == 0) {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amtPayed / 1000)) / 100000000));
                    } else {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amtPayed)) / 100000000));
                    }
                } else {
                    typeIv.setImageResource(R.mipmap.icon_failed_red);
                    // The invoice has not been payed yet
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (item.getAssetId() == 0) {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amt / 1000)) / 100000000));
                    } else {
                        amountTv.setText(df.format(Double.parseDouble(String.valueOf(amt)) / 100000000));
                    }
                    if (StringUtils.isEmpty(String.valueOf(item.getState()))) {
                        if (isInvoiceExpired(item)) {
                            // The invoice has expired
                            statusTv.setText("Expiryed");
                        } else {
                            // The invoice has not yet expired
                            statusTv.setText("Pending");
                        }
                    } else {
                        if (isInvoiceExpired(item) || item.getState() == LightningOuterClass.Invoice.InvoiceState.CANCELED) {
                            // The invoice has expired
                            statusTv.setText("Expiryed");
                        } else {
                            // The invoice has not yet expired
                            statusTv.setText("Pending");
                        }
                    }
                }
            }
            if (item.getAssetId() == 0) {
                assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                assetTypeTv.setText("BTC");
                amountTypeTv.setText("BTC");
            } else {
                assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                assetTypeTv.setText("dollar");
                amountTypeTv.setText("dollar");
            }
            memoTv.setText(StringUtils.cleanString(item.getMemo()));
            invoiceTv.setText(UriUtil.generateLightningUri(item.getPaymentRequest()));
            // click explorer button
            // 点击explorer
            rootView.findViewById(R.id.layout_explorer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ShareUtil.getTwitterIntent(mContext, UriUtil.generateLightningUri(item.getPaymentRequest())));
                    mBasePopWindow.dismiss();
                }
            });
            // click close button at bottom
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

    /**
     * Returns if the invoice has been payed already.
     *
     * @param invoice
     * @return
     */
    public boolean isInvoicePayed(LightningOuterClass.Invoice invoice) {
        boolean payed;
        if (invoice.getValueMsat() == 0) {
            payed = invoice.getAmtPaidMsat() != 0;
        } else {
            payed = invoice.getValueMsat() <= invoice.getAmtPaidMsat();
        }
        return payed;
    }

    /**
     * Returns if the invoice has been expired. This function just checks if the expiration date is in the past.
     * It will also return expired for already payed invoices.
     *
     * @param invoice
     * @return
     */
    public boolean isInvoiceExpired(LightningOuterClass.Invoice invoice) {
        return invoice.getCreationDate() + invoice.getExpiry() < System.currentTimeMillis() / 1000;
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}