package com.omni.wallet.baselibrary.utils;

import android.util.SparseArray;
import android.widget.ImageView;

/**
 * 帧动画播放工具类
 */

public class FrameAnimationUtils {
    private static final String TAG = FrameAnimationUtils.class.getSimpleName();
    private boolean mIsRepeat;
    private ImageView mImageView;
    private int[] mFrameRes;
    private int mLastFrame;
    private int mDuration;//每帧动画的播放间隔
    private boolean isPlaying;
    private int mCurrentFrame = 0;
    // 缓存
    private SparseArray<Runnable> mTempArray = new SparseArray<>();

    public FrameAnimationUtils(ImageView mImageView, int[] mFrameRes, int mDuration, boolean mIsRepeat) {
        this.mIsRepeat = mIsRepeat;
        this.mImageView = mImageView;
        this.mFrameRes = mFrameRes;
        this.mDuration = mDuration;
        this.mLastFrame = mFrameRes.length - 1;
    }

    public void start() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        play(mCurrentFrame);
    }

    public void reset() {
        mCurrentFrame = 0;
        isPlaying = false;
    }

    public void stop() {
        isPlaying = false;
        for (int i = 0; i < mTempArray.size(); i++) {
            int key = mTempArray.keyAt(i);
            Runnable runnable = mTempArray.get(key);
            mImageView.removeCallbacks(runnable);
        }
    }

    private void play(final int index) {
        if (!isPlaying) {
            mCurrentFrame = index;
            return;
        }
        MyRunnable runnable = (MyRunnable) mTempArray.get(index);
        if (runnable == null) {
            runnable = new MyRunnable(index);
            mTempArray.put(index, runnable);
        }
        mImageView.postDelayed(runnable, mDuration);
    }

    private class MyRunnable implements Runnable {
        private int index;

        public MyRunnable(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            mImageView.setImageResource(mFrameRes[index]);
            if (index == mLastFrame) {
                if (mIsRepeat) {
                    play(0);
                }
            } else {
                play(index + 1);
            }
        }
    }
}
