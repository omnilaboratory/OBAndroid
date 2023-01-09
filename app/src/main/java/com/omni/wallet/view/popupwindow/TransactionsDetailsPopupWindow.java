package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.framelibrary.entity.User;

import java.text.DecimalFormat;

import lnrpc.LightningOuterClass;

/**
 * 汉: 交易详情的弹窗
 * En: TransactionsDetailsPopupWindow
 * author: guoyalei
 * date: 2022/10/19
 */
public class TransactionsDetailsPopupWindow {
    private static final String TAG = TransactionsDetailsPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public TransactionsDetailsPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, LightningOuterClass.Payment payment) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_transactions_details);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            TextView typeTv = rootView.findViewById(R.id.tv_type);
            ImageView typeIv = rootView.findViewById(R.id.iv_type);
            TextView statusTv = rootView.findViewById(R.id.tv_status);
            TextView nonceTv = rootView.findViewById(R.id.tv_nonce);
            TextView fromAddressTv = rootView.findViewById(R.id.tv_from_address);
            TextView toNameTv = rootView.findViewById(R.id.tv_to_name);
            TextView toAddressTv = rootView.findViewById(R.id.tv_to_address);
            TextView txIdTv = rootView.findViewById(R.id.tv_txid);
            ImageView assetTypeIv = rootView.findViewById(R.id.iv_asset_type);
            TextView assetTypeTv = rootView.findViewById(R.id.tv_asset_type);
            TextView amountTv = rootView.findViewById(R.id.tv_amount);
            TextView amountTypeTv = rootView.findViewById(R.id.tv_amount_type);
            TextView feeTv = rootView.findViewById(R.id.tv_fee);
            TextView feeTypeTv = rootView.findViewById(R.id.tv_fee_type);
            TextView totalAmountTv = rootView.findViewById(R.id.tv_total_amount);

            typeTv.setText("SENT");
            typeIv.setImageResource(R.mipmap.icon_failed_green);
            if (payment.getAssetId() == 0) {
                assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                assetTypeTv.setText("BTC");
                amountTypeTv.setText("BTC");
                feeTypeTv.setText("satoshis");
            } else {
                assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                assetTypeTv.setText("dollar");
                amountTypeTv.setText("dollar");
                feeTypeTv.setText("unit");
            }
            LightningOuterClass.Hop firstHop = payment.getHtlcs(0).getRoute().getHops(0);
            LightningOuterClass.Hop lastHop = payment.getHtlcs(0).getRoute().getHops(1);
            String fromAddress = firstHop.getPubKey();
            String toAddress = lastHop.getPubKey();
            fromAddressTv.setText(fromAddress);
            toAddressTv.setText(toAddress);
            txIdTv.setText(payment.getPaymentHash());
            long totalAmount = payment.getValueMsat() + payment.getFeeMsat();
            DecimalFormat df = new DecimalFormat("0.00######");
            if (payment.getAssetId() == 0) {
                amountTv.setText(df.format(Double.parseDouble(String.valueOf(payment.getValueMsat() / 1000)) / 100000000));
                feeTv.setText(df.format(Double.parseDouble(String.valueOf(payment.getFeeMsat() / 1000)) / 100000000));
                totalAmountTv.setText(df.format(Double.parseDouble(String.valueOf(totalAmount / 1000)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            } else {
                amountTv.setText(df.format(Double.parseDouble(String.valueOf(payment.getValueMsat())) / 100000000));
                feeTv.setText(df.format(Double.parseDouble(String.valueOf(payment.getFeeMsat())) / 100000000));
                totalAmountTv.setText(df.format(Double.parseDouble(String.valueOf(totalAmount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            }

            // click explorer button
            // 点击explorer
            rootView.findViewById(R.id.layout_explorer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
