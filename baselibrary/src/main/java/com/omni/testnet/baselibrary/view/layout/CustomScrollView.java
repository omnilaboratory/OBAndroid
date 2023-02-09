package com.omni.testnet.baselibrary.view.layout;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.ScrollView;

/**
 * 自定义LinearLayout,修复一些沉浸式和键盘的冲突
 * 修复问题：不设置fitsSystemWindows = true;的时候adjustResize失效；但是设置了fitsSystemWindows = true;
 * 的时候，状态栏不沉浸了，由于系统默认添加了顶部padding，这里重写相关方法去掉相应的padding
 */

public class CustomScrollView extends ScrollView {
    private static final String TAG = CustomScrollView.class.getSimpleName();
    private int[] mInsets = new int[4];

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 避免ScrollView中的OnClick和ScrollView的滚动事件冲突
     */
    @Override
    public boolean canScrollVertically(int direction) {
        return true;
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            // TODO: Figure out why.
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            mInsets[1] = insets.getSystemWindowInsetTop();
            mInsets[2] = insets.getSystemWindowInsetRight();
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
