package com.omni.wallet_mainnet.view.popupwindow;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;

/**
 * 汉: 选择时间的弹窗
 * En: SelectTimePopupWindow
 * author: guoyalei
 * date: 2022/10/21
 */
public class SelectTimePopupWindow {
    private static final String TAG = SelectTimePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;

    public SelectTimePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_time);
            mBasePopWindow.setWidth(view.getWidth());
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mBasePopWindow.setOutsideTouchable(true);
            mBasePopWindow.setFocusable(false);
            // Click Minutes
            rootView.findViewById(R.id.tv_minutes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            // Click Hours
            rootView.findViewById(R.id.tv_hours).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            // Click Days
            rootView.findViewById(R.id.tv_days).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAsDropDown(view);
        }
    }

    public void setOnItemClickCallback(ItemCleckListener callback) {
        this.mCallback = callback;
    }

    public interface ItemCleckListener {
        void onItemClick(View view);
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
