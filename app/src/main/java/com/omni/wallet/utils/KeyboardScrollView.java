package com.omni.wallet.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardScrollView {
    public static int recordVisibleRec = 0;
    public static void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            //Get the visible area of ​​root in the form
            root.getWindowVisibleDisplayFrame(rect);
            //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
            int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
            if (Math.abs(rootInvisibleHeight - recordVisibleRec) > 200) {
                //If the height of the invisible area is greater than 200, the keyboard will be displayed
                if (rootInvisibleHeight > 200) {
                    int[] location = new int[2];
                    //Get the coordinates of scrollToView in the form
                    scrollToView.getLocationInWindow(location);
                    //Calculate the root scroll height so that scrollToView is in the visible area
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                    srollHeight = srollHeight < 0 ? 0 : srollHeight;
                    root.scrollTo(0, srollHeight);
                } else {
                    //hide keyboard
                    root.scrollTo(0, 0);
                }
            }
            recordVisibleRec = rootInvisibleHeight;
        });
    }
}
