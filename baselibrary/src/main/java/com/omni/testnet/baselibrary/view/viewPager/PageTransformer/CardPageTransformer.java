package com.omni.testnet.baselibrary.view.viewPager.PageTransformer;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;


public class CardPageTransformer implements ViewPager.PageTransformer {
    private static final String TAG = CardPageTransformer.class.getSimpleName();
    // 偏移量
    private int mTranslation = 10;

    /**
     * @param translation 缩放偏移量 单位 px
     */
    public CardPageTransformer(int translation) {
        this.mTranslation = translation;
    }

    @SuppressLint("NewApi")
    public void transformPage(View page, float position) {
        if (position <= 0.0f) {//被滑动的那页  position 是-下标~ 0
            page.setTranslationX(0f);
//            //旋转角度  45° * -0.1 = -4.5°
//            page.setRotation((45 * position));
            //X轴偏移 li:  300/3 * -0.1 = -10
            page.setTranslationX((page.getWidth() / 3 * position));
        } else {
            //缩放比例
            float scale = (page.getWidth() - mTranslation * position) / (float) (page.getWidth());
            if (scale > 0) {
                page.setScaleX(scale);
                page.setScaleY(scale);
            }

//            page.setTranslationX((-page.getWidth() * position));
//            page.setTranslationY((mTranslation * 0.8f) * position);

            page.setTranslationX(-(page.getWidth() - mTranslation) * position);
        }

    }
}
