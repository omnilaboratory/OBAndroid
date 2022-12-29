package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.MonetaryUtil;

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

            // TODO: 2022/11/28 待完善
            if (payment.getAssetId() == 0) {
                assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                assetTypeTv.setText("BTC");
            } else {
                assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                assetTypeTv.setText("Doallar");
            }
            LightningOuterClass.Hop lastHop = payment.getHtlcs(0).getRoute().getHops(payment.getHtlcs(0).getRoute().getHopsCount() - 1);
            String payee = lastHop.getPubKey();
            fromAddressTv.setText(payee);
            txIdTv.setText(payment.getPaymentPreimage());
            amountTv.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(payment.getValueMsat()));
            feeTv.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(payment.getFeeMsat()));
            long totalAmount = payment.getValueMsat() + payment.getFeeMsat();
            totalAmountTv.setText(MonetaryUtil.getInstance().getPrimaryDisplayAmountAndUnit(totalAmount));

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
