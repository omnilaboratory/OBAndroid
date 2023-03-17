package com.omni.wallet.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardScrollView {
    public static int recordVisibleRec = 0;
    public static void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            //获取root在窗体的可视区域
            root.getWindowVisibleDisplayFrame(rect);
            //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
            int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
            if (Math.abs(rootInvisibleHeight - recordVisibleRec) > 200) {
                //若不可视区域高度大于200，则键盘显示
                if (rootInvisibleHeight > 200) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                    srollHeight = srollHeight < 0 ? 0 : srollHeight;
                    root.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
            recordVisibleRec = rootInvisibleHeight;
        });
    }
}
