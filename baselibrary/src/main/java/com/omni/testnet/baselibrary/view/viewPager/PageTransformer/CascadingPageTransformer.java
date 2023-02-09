package com.omni.testnet.baselibrary.view.viewPager.PageTransformer;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;

public class CascadingPageTransformer implements ViewPager.PageTransformer {
    /**
     * 偏移量
     */
    private int mScaleOffset = 40;


    /**
     * @param mScaleOffset 缩放偏移量 单位 px
     */
    public void setScaleOffset(int mScaleOffset) {
        this.mScaleOffset = mScaleOffset;
    }

    @SuppressLint("NewApi")
    public void transformPage(View page, float position) {
        if (position <= 0.0f) {//被滑动的那页  position 是-下标~ 0
            page.setTranslationX(0f);
            //旋转角度  45° * -0.1 = -4.5°
            page.setRotation((45 * position));
            //X轴偏移 li:  300/3 * -0.1 = -10
            page.setTranslationX((page.getWidth() / 3 * position));
        } else {
            //缩放比例
            float scale = (page.getWidth() - mScaleOffset * position) / (float) (page.getWidth());

            page.setScaleX(scale);
            page.setScaleY(scale);

            page.setTranslationX((-page.getWidth() * position));
            page.setTranslationY((mScaleOffset * 0.8f) * position);
        }
    }
}
