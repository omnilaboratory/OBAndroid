package com.omni.testnet.baselibrary.utils;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * TabLayout控件用来改变底部下划线宽度的工具类
 */

public class TabLayoutUtils {
    private static final String TAG = TabLayoutUtils.class.getSimpleName();

    /**
     * 设置指示器底部下划线的宽度
     * 要在设置了TabLayout的条目之后调用
     */
    public static void setIndicator(TabLayout tabs, int leftMargin, int rightMargin) {
        try {
            Class<?> tabLayout = tabs.getClass();
            Field tabStrip = tabLayout.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout llTab = (LinearLayout) tabStrip.get(tabs);
            for (int i = 0; i < llTab.getChildCount(); i++) {
                View child = llTab.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.leftMargin = leftMargin;
                params.rightMargin = rightMargin;
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置指示器底部下划线的宽度
     * 要在设置了TabLayout的条目之后调用
     * 该方法仅仅适用于不自定义Item的情况，在TabLayout设置自定义Item的时候不起作用，无法反射到属性mTextView
     */
    public static void setIndicator2(TabLayout tabLayout, int leftMargin, int rightMargin) {
        try {
            //拿到tabLayout的mTabStrip属性
            Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
            mTabStripField.setAccessible(true);
            LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);
                //拿到tabView的mTextView属性
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);
                TextView mTextView = (TextView) mTextViewField.get(tabView);
                tabView.setPadding(0, 0, 0, 0);
                //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                int width;
                width = mTextView.getWidth();
                if (width == 0) {
                    mTextView.measure(0, 0);
                    width = mTextView.getMeasuredWidth();
                }
                //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.width = width;
                params.leftMargin = leftMargin;
                params.rightMargin = rightMargin;
                tabView.setLayoutParams(params);
                tabView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
