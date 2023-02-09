package com.omni.testnet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.testnet.R;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.view.BasePopWindow;
import com.omni.testnet.framelibrary.entity.User;

import java.text.DecimalFormat;

import lnrpc.LightningOuterClass;

/**
 * 汉:
 * En:
 * author: guoyalei
 * date: 2022/12/20
 */
public class TransactionsDetailsAssetPopupWindow {
    private static final String TAG = TransactionsDetailsAssetPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public TransactionsDetailsAssetPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, LightningOuterClass.AssetTx item) {
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
            if (item.getType().equals("Simple Send")) {
                amountTv.setText(df.format(Double.parseDouble(item.getAmount())));
                toAddressTv.setText(item.getReferenceaddress());
                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getAmount())) * 100000000) + (long) (Double.parseDouble(String.valueOf(item.getFee())) * 100000000) + "";
                totalAmountTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                    typeTv.setText("PENDING");
                    typeIv.setImageResource(R.mipmap.icon_failed_red);
                    statusTv.setText("Pending");
                } else {
                    typeTv.setText("RECEIVED");
                    typeIv.setImageResource(R.mipmap.icon_failed_green);
                    statusTv.setText("Confirmed");
                }
            } else if (item.getType().equals("Send To Many")) {
                if (item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    amountTv.setText(df.format(Double.parseDouble(item.getTotalamount())));
                    if (item.getReceiversList() != null) {
                        if (item.getReceiversList().size() == 1) {
                            toAddressTv.setText(item.getReceivers(0).getAddress());
                        }
                    }
                    String totalValue = (long) (Double.parseDouble(String.valueOf(item.getTotalamount())) * 100000000) + (long) (Double.parseDouble(String.valueOf(item.getFee())) * 100000000) + "";
                    totalAmountTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        typeTv.setText("PENDING");
                        typeIv.setImageResource(R.mipmap.icon_failed_red);
                        statusTv.setText("Pending");
                    } else {
                        typeTv.setText("SENT");
                        typeIv.setImageResource(R.mipmap.icon_failed_green);
                        statusTv.setText("Confirmed");
                    }
                } else if (!item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    if (item.getReceiversList() != null) {
                        if (item.getReceiversList().size() == 1) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                amountTv.setText(df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                                toAddressTv.setText(item.getReceivers(0).getAddress());
                                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getReceivers(0).getAmount())) * 100000000) + (long) (Double.parseDouble(String.valueOf(item.getFee())) * 100000000) + "";
                                totalAmountTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                            }
                        } else if (item.getReceiversList().size() == 2) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & !item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                amountTv.setText(df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                                toAddressTv.setText(item.getReceivers(0).getAddress());
                                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getReceivers(0).getAmount())) * 100000000) + (long) (Double.parseDouble(String.valueOf(item.getFee())) * 100000000) + "";
                                totalAmountTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                            } else if (!item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                amountTv.setText(df.format(Double.parseDouble(item.getReceivers(1).getAmount())));
                                toAddressTv.setText(item.getReceivers(1).getAddress());
                                String totalValue = (long) (Double.parseDouble(String.valueOf(item.getReceivers(1).getAmount())) * 100000000) + (long) (Double.parseDouble(String.valueOf(item.getFee())) * 100000000) + "";
                                totalAmountTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                            }
                        }
                    }
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        typeTv.setText("PENDING");
                        typeIv.setImageResource(R.mipmap.icon_failed_red);
                        statusTv.setText("Pending");
                    } else {
                        typeTv.setText("RECEIVED");
                        typeIv.setImageResource(R.mipmap.icon_failed_green);
                        statusTv.setText("Confirmed");
                    }
                }
            }
            fromAddressTv.setText(item.getSendingaddress());
            txIdTv.setText(item.getTxid());
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountTypeTv.setText("dollar");
            feeTv.setText(df.format(Double.parseDouble(String.valueOf(item.getFee())) * 100000000));
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
