package com.omni.testnet.utils;

import android.os.SystemClock;
import android.view.View;

/**
 * This class can be used to prevent double clicking on a button that is meant to be clicked only once.
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    private static final long MIN_CLICK_INTERVAL = 600;

    private long mLastClickTime;

    /**
     * click
     *
     * @param v The view that was clicked.
     */
    public abstract void onSingleClick(View v);

    @Override
    public final void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;

        mLastClickTime = currentClickTime;

        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return;

        onSingleClick(v);
    }

}