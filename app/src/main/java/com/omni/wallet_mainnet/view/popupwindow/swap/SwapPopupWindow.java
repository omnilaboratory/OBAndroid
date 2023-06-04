package com.omni.wallet_mainnet.view.popupwindow.swap;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;
import com.omni.wallet_mainnet.view.popupwindow.SelectAssetTypePopupWindow;
import com.omni.wallet_mainnet.view.popupwindow.SelectChannelBalancePopupWindow;

/**
 * 汉: 原子交换的弹窗
 * En: SwapPopupWindow
 * author: guoyalei
 * date: 2023/2/24
 */
public class SwapPopupWindow {
    private static final String TAG = SwapPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private TextView mPercent2Tv, mPercent3Tv;
    SelectChannelBalancePopupWindow mSelectChannelBalancePopupWindow;
    SelectAssetTypePopupWindow mSelectAssetTypePopupWindow;

    public SwapPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_swap);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            mPercent2Tv = rootView.findViewById(R.id.tv_percent_2);
            mPercent3Tv = rootView.findViewById(R.id.tv_percent_3);
            mPercent2Tv.setSelected(true);

            RelativeLayout selectAssetLayout = rootView.findViewById(R.id.layout_select_asset);
            selectAssetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectChannelBalancePopupWindow = new SelectChannelBalancePopupWindow(mContext);
                    mSelectChannelBalancePopupWindow.show(v);
                }
            });
            RelativeLayout assetLayout = rootView.findViewById(R.id.layout_asset_type);
            assetLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectAssetTypePopupWindow = new SelectAssetTypePopupWindow(mContext);
                    mSelectAssetTypePopupWindow.show(v);
                }
            });
            mPercent2Tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPercent2Tv.setSelected(true);
                    mPercent3Tv.setSelected(false);
                }
            });

            mPercent3Tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPercent2Tv.setSelected(false);
                    mPercent3Tv.setSelected(true);
                }
            });

            rootView.findViewById(R.id.layout_check_swap).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_swap_step_one).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_swap_step_two).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.layout_cancel).setVisibility(View.GONE);
                    rootView.findViewById(R.id.layout_close).setVisibility(View.VISIBLE);
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
