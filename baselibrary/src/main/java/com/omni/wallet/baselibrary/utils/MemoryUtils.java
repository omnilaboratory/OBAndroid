package com.omni.wallet.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 内存相关工具类（解决InputMethod导致的内存泄漏）
 */

public class MemoryUtils {
    private static final String TAG = MemoryUtils.class.getSimpleName();

    /**
     * 释放InputMethod，避免InputMethod导致的内存泄漏
     */
    public static void releaseInputMethodManagerFocus(Activity paramActivity) {
        if (paramActivity == null) {
            return;
        }
        int count = 0;
        while (true) {
            //给个5次机会 省得无限循环
            count++;
            if (count == 5) {
                return;
            }
            try {
                InputMethodManager localInputMethodManager = (InputMethodManager) paramActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (localInputMethodManager != null) {
                    Method localMethod = InputMethodManager.class.getMethod("windowDismissed", new Class[]{IBinder.class});
                    if (localMethod != null) {
                        localMethod.invoke(localInputMethodManager, new Object[]{paramActivity.getWindow().getDecorView().getWindowToken()});
                    }
                    Field mLastSrvView = InputMethodManager.class.getDeclaredField("mLastSrvView");
                    if (mLastSrvView != null) {
                        mLastSrvView.setAccessible(true);
                        mLastSrvView.set(localInputMethodManager, null);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
