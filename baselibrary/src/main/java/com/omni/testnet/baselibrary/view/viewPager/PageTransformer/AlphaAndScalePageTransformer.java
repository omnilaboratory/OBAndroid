package com.omni.testnet.baselibrary.view.viewPager.PageTransformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager切换时间的透明缩放动画
 */
public class AlphaAndScalePageTransformer implements ViewPager.PageTransformer {
    private static final float SCALE_MAX = 0.9f;
    private static final float ALPHA_MAX = 0.9f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scale = position < 0 ? ((1 - SCALE_MAX) * position + 1) : ((SCALE_MAX - 1) * position + 1);
        float alpha = position < 0 ? ((1 - ALPHA_MAX) * position + 1) : ((ALPHA_MAX - 1) * position + 1);
        if (position < 0) {
            page.setPivotX(page.getWidth());
            page.setPivotY(page.getHeight() / 2);
        } else {
            page.setPivotX(0);
            page.setPivotY(page.getHeight() / 2);
        }
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setAlpha(Math.abs(alpha));
    }
}
