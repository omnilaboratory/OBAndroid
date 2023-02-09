package com.omni.testnet.baselibrary.view.banner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Banner的切换
 */

public class BannerScroller extends Scroller {

    // 切换动画的时间
    private int mScrollerDuration = 600;

    /**
     * 设置切换动画的时间
     *
     * @param scrollerDuration 时间
     */
    public void setScrollerDuration(int scrollerDuration) {
        this.mScrollerDuration = scrollerDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // 在这里将我们自己设置的时间传递进去
        super.startScroll(startX, startY, dx, dy, mScrollerDuration);
    }

    public BannerScroller(Context context) {
        super(context);
    }

    public BannerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BannerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }
}
