package com.omni.wallet.baselibrary.utils;

import android.os.Handler;
import android.os.Message;

/**
 * 简单的倒计时工具类
 */

public class SimpleCountDownUtils extends Handler {
    private static final String TAG = SimpleCountDownUtils.class.getSimpleName();
    private boolean isRunning = false;
    private OnCountDownListener mListener;
    private long mCountCycle = 1000;// 倒计时的通知周期(默认1000ms)

    public void setListener(OnCountDownListener listener) {
        this.mListener = listener;
    }

    public void setCountCycle(long countCycle) {
        this.mCountCycle = countCycle;
    }


//    private long startTime = 0;

    @Override
    public void handleMessage(Message msg) {
        long tempTime = System.currentTimeMillis() - (long) msg.obj;
        if (mListener != null) {
            mListener.onTick(tempTime);
        }
        if (isRunning) {
            Message message = Message.obtain();
            message.obj = System.currentTimeMillis();
            sendMessageDelayed(message, mCountCycle);
        }
    }

    /**
     * 启动
     */
    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        // 把执行耗时传递过去
        Message message = Message.obtain();
        message.obj = System.currentTimeMillis();
        sendMessage(message);
    }

    /**
     * 停止
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        removeMessages(0);
    }

    /**
     * 倒计时的回调，用来处理UI更新
     */
    public interface OnCountDownListener {
        void onTick(long tempTime);
    }

}
