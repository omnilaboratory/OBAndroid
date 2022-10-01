package com.omni.wallet.baselibrary.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自定义倒计时工具 暂时未使用
 */

public class CustomCountDownTimer {
    private static final String TAG = CustomCountDownTimer.class.getSimpleName();

    private final long mTickCycle;
    private long mStopTime;
    private boolean mIsRunning = false;
    private TimerTickListener mListener;
    private ScheduledExecutorService mScheduler;

    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    public CustomCountDownTimer(long countCycle, long tickCycle, TimerTickListener listener) {
        this.mStopTime = countCycle;
        this.mTickCycle = tickCycle;
        this.mListener = listener;
        mScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public CustomCountDownTimer(long tickCycle, TimerTickListener listener) {
        mTickCycle = tickCycle;
        this.mListener = listener;
    }

    private class TimerRunnable implements Runnable {
        public void run() {
            // 设置了倒计时时长才计算剩余时间
            if (mStopTime > 0) {
                mStopTime = mStopTime - mTickCycle;
            }
            mMainThreadHandler.post(new Runnable() {
                long millisLeft = mStopTime;

                @Override
                public void run() {
                    // 剩余时间小于0的时候都当成0
                    if (millisLeft <= 0) {
                        millisLeft = 0;
                    }
                    mListener.onTick(millisLeft);
                }
            });
        }
    }

    public synchronized void start() {
        if (mIsRunning) {
            return;
        }
        mScheduler = Executors.newSingleThreadScheduledExecutor();
        mScheduler.scheduleWithFixedDelay(new TimerRunnable(), mTickCycle, mTickCycle, TimeUnit.MILLISECONDS);
        mIsRunning = true;
    }

    public synchronized final void stop() {
        mIsRunning = false;
        if (mScheduler != null) {
            mScheduler.shutdownNow();
        }
    }

    public interface TimerTickListener {
        void onTick(long millisLeft);
    }
}
