package com.omni.wallet_mainnet.framelibrary.view.refreshlayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet_mainnet.framelibrary.R;


/**
 * 可以隐藏topView的Header
 */

public class DetailsPageRefreshView extends LayoutRefreshView {
    private static final String TAG = DetailsPageRefreshView.class.getSimpleName();

    // 是否有TopView
    private boolean mHasTopView;

    public DetailsPageRefreshView(boolean mHasTopView) {
        this.mHasTopView = mHasTopView;
    }

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        View rootView = super.getRefreshView(context, parent);
        if (rootView != null) {
            View topView = rootView.findViewById(R.id.view_refresh_top);
            // 控制是否显示header顶部的占位View
            if (topView != null) {
                if (mHasTopView) {
                    topView.setVisibility(View.VISIBLE);
                } else {
                    topView.setVisibility(View.GONE);
                }
            }
        }
        return rootView;
    }
}
