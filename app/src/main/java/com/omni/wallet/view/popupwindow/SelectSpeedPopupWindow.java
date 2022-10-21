package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 选择速度的弹窗
 * En: SelectSpeedPopupWindow
 * author: guoyalei
 * date: 2022/10/21
 */
public class SelectSpeedPopupWindow {
    private static final String TAG = SelectSpeedPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;

    public SelectSpeedPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_speed);
            mBasePopWindow.setWidth(view.getWidth());
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            // Click SLOW
            rootView.findViewById(R.id.tv_slow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            // Click MEDIUM
            rootView.findViewById(R.id.tv_medium).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            // Click FAST
            rootView.findViewById(R.id.tv_fast).setOnClickListener(new View.OnClickListener() {
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
