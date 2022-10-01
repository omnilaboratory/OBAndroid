package com.omni.wallet.baselibrary.view.imageView;

import android.view.MotionEvent;

public class RotateGestureDetector {
    private static final int MAX_DEGREES_STEP = 120;

    private OnRotateListener mListener;

    private float mPrevSlope;
    private float mCurrSlope;

    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private boolean ischange;

    public RotateGestureDetector(OnRotateListener l) {
        mListener = l;
    }

    public void onTouchEvent(MotionEvent event) {
        final int Action = event.getActionMasked();
        switch (Action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                //屏幕中多个点被按住，松开其中一个点
            case MotionEvent.ACTION_POINTER_UP:
                //如果这个时候正好有两个点，那么，计算这两个点的斜率
                //三个触控点抬起哪一个，都会使斜率发生比较大的变化（图片会突然跳一个圈），所以要给一个标识，让接下来的第一次MOVE事件，不计算旋转角度
                ischange = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {
                    mCurrSlope = caculateSlope(event);

                    double currDegrees = Math.toDegrees(Math.atan(mCurrSlope));
                    double prevDegrees = Math.toDegrees(Math.atan(mPrevSlope));

                    double deltaSlope = currDegrees - prevDegrees;

                    if (Math.abs(deltaSlope) <= MAX_DEGREES_STEP && !ischange) {
                        mListener.onRotate((float) deltaSlope, (x2 + x1) / 2, (y2 + y1) / 2);
                    }
                    if (ischange) {
                        ischange = false;
                    }
                    mPrevSlope = mCurrSlope;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 计算斜率y2-y1,x2-x1相当于直角三角形的两个直边
     *
     * @param event
     * @return 只计算前两个触控点的斜率
     */
    private float caculateSlope(MotionEvent event) {
        x1 = event.getX(0);
        y1 = event.getY(0);
        x2 = event.getX(1);
        y2 = event.getY(1);
        return (y2 - y1) / (x2 - x1);
    }

    public interface OnRotateListener {
        void onRotate(float degrees, float focusX, float focusY);
    }
}
