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
import com.omni.wallet.framelibrary.entity.User;

import java.text.DecimalFormat;

import lnrpc.LightningOuterClass;

/**
 * 汉: 链上交易详情的弹窗
 * En: TransactionsDetailsChainPopupWindow
 * author: guoyalei
 * date: 2022/12/20
 */
public class TransactionsDetailsChainPopupWindow {
    private static final String TAG = TransactionsDetailsChainPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public TransactionsDetailsChainPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, LightningOuterClass.Transaction item) {
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

            DecimalFormat df = new DecimalFormat("0.00######");
            DecimalFormat df1 = new DecimalFormat("0.00");
            if (item.getAmount() < 0) {
                amountTv.setText(df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000).replace("-", ""));
                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getAmount()).replace("-", ""))) + item.getTotalFees() + "";
                totalAmountTv.setText(df1.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    typeTv.setText("PENDING");
                    typeIv.setImageResource(R.mipmap.icon_failed_red);
                    statusTv.setText("Pending");
                } else {
                    typeTv.setText("SENT");
                    typeIv.setImageResource(R.mipmap.icon_failed_green);
                    statusTv.setText("Confirmed");
                }
                fromAddressTv.setText(User.getInstance().getWalletAddress(mContext));
                if (item.getDestAddresses(0).equals(User.getInstance().getWalletAddress(mContext)) & !item.getDestAddresses(1).equals(User.getInstance().getWalletAddress(mContext))) {
                    toAddressTv.setText(item.getDestAddresses(1));
                } else if (!item.getDestAddresses(0).equals(User.getInstance().getWalletAddress(mContext)) & item.getDestAddresses(1).equals(User.getInstance().getWalletAddress(mContext))) {
                    toAddressTv.setText(item.getDestAddresses(0));
                }
            } else if (item.getAmount() > 0) {
                amountTv.setText(df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getAmount()))) + item.getTotalFees() + "";
                totalAmountTv.setText(df1.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    typeTv.setText("PENDING");
                    typeIv.setImageResource(R.mipmap.icon_failed_red);
                    statusTv.setText("Pending");
                } else {
                    typeTv.setText("RECEIVED");
                    typeIv.setImageResource(R.mipmap.icon_failed_green);
                    statusTv.setText("Confirmed");
                }
                if (item.getDestAddresses(0).equals(User.getInstance().getWalletAddress(mContext)) & !item.getDestAddresses(1).equals(User.getInstance().getWalletAddress(mContext))) {
                    fromAddressTv.setText(item.getDestAddresses(1));
                } else if (!item.getDestAddresses(0).equals(User.getInstance().getWalletAddress(mContext)) & item.getDestAddresses(1).equals(User.getInstance().getWalletAddress(mContext))) {
                    fromAddressTv.setText(item.getDestAddresses(0));
                }
                toAddressTv.setText(User.getInstance().getWalletAddress(mContext));
            }
            txIdTv.setText(item.getTxHash());
            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeTv.setText("BTC");
            amountTypeTv.setText("BTC");
            feeTv.setText(item.getTotalFees() + "");
            feeTypeTv.setText("satoshis");

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
