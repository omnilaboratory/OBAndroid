package com.omni.testnet.baselibrary.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;

/**
 * 资源获取工具类
 */

public class ResourceUtils {

    /**
     * 创建TextView在Check状态时其文字颜色的选择器集合。
     *
     * @param defaultColor 默认颜色
     * @param checkedColor checked状态的颜色
     */
    public static ColorStateList getCheckStateColorList(int defaultColor, int checkedColor) {
        // 颜色的数组
        int[] color = new int[]{checkedColor, defaultColor};
        // 状态的数组
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        return new ColorStateList(states, color);
    }

    /**
     * 创建TextView在Enable状态时其文字颜色的选择器集合。
     *
     * @param defaultColor 默认颜色
     * @param enableColor  enable状态的颜色
     */
    public static ColorStateList getEnableStateColorList(int defaultColor, int enableColor) {
        // 颜色的数组
        int[] color = new int[]{enableColor, defaultColor};
        // 状态的数组
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_enabled};
        states[1] = new int[]{};
        return new ColorStateList(states, color);
    }

    /**
     * 根据DrawableResId获取drawable的DrawableList
     */
    public static StateListDrawable getStateDrawableResList(Context context, int defaultDrawable, int enableDrawable,
                                                            int checkedDrawable, int selectedDrawable) {
        return getStateDrawableList(ContextCompat.getDrawable(context, defaultDrawable), ContextCompat.getDrawable(context, enableDrawable),
                ContextCompat.getDrawable(context, checkedDrawable), ContextCompat.getDrawable(context, selectedDrawable));
    }

    /**
     * 根据Drawable获取drawable的DrawableList
     */
    public static StateListDrawable getStateDrawableList(Drawable defaultDrawable, Drawable enableDrawable,
                                                         Drawable checkedDrawable, Drawable selectedDrawable) {
        StateListDrawable stateList = new StateListDrawable();
        // View.PRESSED_ENABLED_STATE_SET
        stateList.addState(new int[]{android.R.attr.state_enabled}, enableDrawable);
        // View.ENABLED_FOCUSED_STATE_SET
        stateList.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        // View.ENABLED_STATE_SET
        stateList.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        // View.EMPTY_STATE_SET
        stateList.addState(new int[]{}, defaultDrawable);
        return stateList;
    }


    /**
     * 根据字符串获取Dimen资源ID
     */
    public static int getDimenResIdByName(Context context, String nameStr) {
        //如果没有获取到,将会返回0
        return context.getResources().getIdentifier(nameStr, "dimen", context.getPackageName());
    }

    /**
     * 根据字符串获取Color资源ID
     */
    public static int getColorResIdByName(Context context, String nameStr) {
        //如果没有获取到,将会返回0
        return context.getResources().getIdentifier(nameStr, "color", context.getPackageName());
    }

    /**
     * 根据字符串获取String资源ID
     */
    public static int getStringResIdByName(Context context, String nameStr) {
        //如果没有获取到,将会返回0
        return context.getResources().getIdentifier(nameStr, "string", context.getPackageName());
    }

    /**
     * 根据字符串获取Drawable资源ID
     */
    public static int getDrawableResIdByName(Context context, String nameStr) {
        //如果没有获取到,将会返回0
        return context.getResources().getIdentifier(nameStr, "drawable", context.getPackageName());
    }

}
