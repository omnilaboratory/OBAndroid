package com.omni.wallet_mainnet.baselibrary.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可以设置是否能滑动的ViewPager
 */

public class ControlScrollViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public ControlScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public ControlScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean canScroll) {
        this.isCanScroll = canScroll;

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isCanScroll) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCanScroll) {
            return super.onTouchEvent(event);
        }
        return false;
    }
}
