package com.omni.wallet.baselibrary.view.imageView;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import com.omni.wallet.baselibrary.utils.LogUtils;


/**
 * 支持缩放的ImageView
 */
public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private static final String TAG = ZoomImageView.class.getSimpleName();

    private onSingleTapListener listener;

    public void setListener(onSingleTapListener listener) {
        this.listener = listener;
    }

    public interface onSingleTapListener {
        void onSingleTap();
    }

    private long mDelaMills = 0;
    private boolean mOnce = false;

    private float mInitScale;
    private float mMidScale;
    private float mMaxScale;
    private Matrix mMatrix;
    private ScaleGestureDetector mScaleGestureDetector;//捕获用户多指触控时缩放的比例

    //---------------------自由移动的变量------------------------
    /**
     * 记录上次多点触控的数量
     */
    private int mLastPointerCount;
    //记录上次中心点的坐标
    private float mLastPointerX;
    private float mLastPointerY;

    /**
     * 系统触发的最小滑动距离
     */
    private float mTouchSlop;

    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    //---------------------双击放大与缩小变量-------------------------
    private GestureDetector mGestureDetector;//用户双击手势的对象变量
    private boolean isScaling;//是否正在放大或缩小---防止用户在正在放大或缩小时疯狂点击

    /**
     * 项目名称：ScaleImageView
     * 类名称：SlowlyScaleRunnable
     *
     * @author wanglu 2017年2月4日 上午10:47:42
     *         E-mail:wang_lu90125@163.com
     *         <p>
     *         自动放大与缩小
     */
    private class SlowlyScaleRunnable implements Runnable {
        //缩放的目标值
        private float mTargetScale;
        //缩放的中心点
        private float x;
        private float y;

        //放大与缩小的梯度
        private final float BIGGER = 1.07F;
        private final float SMALL = 0.97F;

        private float tmpScale;

        public SlowlyScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            //进行缩放
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, mDelaMills);
            } else {
                isScaling = false;
                //到达了目标值
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);

            }
        }
    }

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 初始化
        mMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        //系统触发的最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        //双击放大与缩小
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaling) {
                    return true;
                }
                //以此点为缩放中心
                float x = e.getX();
                float y = e.getY();

                if (getScale() < mMidScale) {
                    postDelayed(new SlowlyScaleRunnable(mMidScale, x, y), mDelaMills);
                    isScaling = true;
                } else {
                    postDelayed(new SlowlyScaleRunnable(mInitScale, x, y), mDelaMills);
                    isScaling = true;
                }

                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (listener != null) {
                    listener.onSingleTap();
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //注册onGlobalLayoutListener
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //移除onGlobalLayoutListener
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 捕获图片加载完成事件 onMeasure 和onDraw都不适合
     */
    @Override
    public void onGlobalLayout() {
        //初始化的操作 一次就好  为了保证对缩放只进行一次
        if (!mOnce) {

            //得到控件的宽和高--不一定是屏幕的宽和高 可能会有actionBar等等
            int width = getWidth();
            int height = getHeight();

            //得到我们的图片 以及宽和高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            /**
             * 这里说下Drawable这个抽象类，具体实现类为BitmapDrawable
             * BitmapDrawable这个类重写了getIntrinsicWidth()和getIntrinsicHeight()方法
             * 这个两个方法看字面意思就知道是什么了，就是得到图片固有的宽和高的
             */
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            LogUtils.e(TAG, "控件宽度：" + width + "||控件高度：" + height);
            LogUtils.e(TAG, "图片宽度：" + intrinsicWidth + "||图片高度：" + intrinsicHeight);
            // 如果图片宽度比控件宽度小  高度比控件大 需要缩小
            float scale = 1.0f;
            if (width > intrinsicWidth && height < intrinsicHeight) {
                scale = height * 1.0f / intrinsicHeight;
            }
            // 如果图片比控件大 需要缩小
            if (width < intrinsicWidth && height > intrinsicHeight) {
                scale = width * 1.0f / intrinsicWidth;
            }

            if ((width < intrinsicWidth && height < intrinsicHeight) || (width > intrinsicWidth && height > intrinsicHeight)) {
                scale = Math.min(width * 1.0f / intrinsicWidth, height * 1.0f / intrinsicHeight);
            }

            /**
             * 得到初始化缩放的比例
             */
            mInitScale = scale;
            mMidScale = 2 * mInitScale;//双击放大的值
            mMaxScale = 4 * mInitScale;//放大的最大值

            //将图片移动到控件的中心
            int dx = width / 2 - intrinsicWidth / 2;
            int dy = height / 2 - intrinsicHeight / 2;
            //将一些参数设置到图片或控件上 设置平移缩放 旋转
            mMatrix.postTranslate(dx, dy);
            mMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);//以控件的中心进行缩放
            setImageMatrix(mMatrix);

            mOnce = true;
        }
    }

    /**
     * 获取图片当前的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    //缩放区间 initScale --- maxScale
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        //捕获用户多指触控时缩放的比例
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        //最大最小控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            //不断检测 控制白边和中心位置
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
        }
        return true;
    }

    /**
     * 获得图片放大或缩小之后的宽和高 以及 left top right bottom的坐标点
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 在缩放的时候进行边界控制以及我们的中心位置控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float delaX = 0;
        float delaY = 0;
        //控件的宽和高
        int width = getWidth();
        int height = getHeight();
        //如果图片的宽和高大于控件的宽和高 在缩放过程中会产生border 进行偏移补偿
        if (rect.width() >= width) {
            if (rect.left > 0) {
                delaX = -rect.left;
            }
            if (rect.right < width) {
                delaX = width - rect.right;
            }
        }

        if (rect.height() >= height) {
            if (rect.top > 0) {
                delaY = -rect.top;
            }
            if (rect.bottom < height) {
                delaY = height - rect.bottom;
            }
        }

        //如果图片的宽和高小于控件的宽和高 让其居中
        if (rect.width() < width) {
            delaX = width / 2 - rect.right + rect.width() / 2f;
        }
        if (rect.height() < height) {
            delaY = height / 2 - rect.bottom + rect.height() / 2f;
        }
        mMatrix.postTranslate(delaX, delaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;//修改为true 才会进入onScale()这个函数  否则多指触控一直走onScaleBegin方法 不走 onScale和 onScaleEnd方法
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //双击放大与缩小事件传递给GestureDetector 放在最前面 防止双击时还能产生移动的事件响应
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        //将手势传递给ScaleGestureDetector
        boolean onTouchEvent = mScaleGestureDetector.onTouchEvent(event);

        //-------------------------将放大的图片自由移动逻辑处理-----------------start------------
        //得到触控中心点的坐标
        float pointerX = 0;
        float pointerY = 0;
        //拿到多点触控的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            pointerX += event.getX(i);
            pointerY += event.getY(i);
        }
        pointerX /= pointerCount;
        pointerY /= pointerCount;
        if (mLastPointerCount != pointerCount) {
            //手指发生改变时 需要重新判断 是否能够移动
            isCanDrag = false;
            mLastPointerX = pointerX;
            mLastPointerY = pointerY;
        }
        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getParent() instanceof ViewPager) {
                    //如果图片放大时 处理图片平移与ViewPager的滑动冲突
                    if (rectF.width() - getWidth() > 0.01 || rectF.height() - getHeight() > 0.01) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果图片放大时 处理图片平移与ViewPager的滑动冲突
                if (getParent() instanceof ViewPager) {
                    if (rectF.width() - getWidth() > 0.01 || rectF.height() - getHeight() > 0.01) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                float dx = pointerX - mLastPointerX;
                float dy = pointerY - mLastPointerY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        //如果图片宽度小于控件宽度 不允许横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        //如果图片的高度小于控件的高度 不允许纵向移动
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mMatrix);
                    }
                }
                mLastPointerX = pointerX;
                mLastPointerY = pointerY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        //-------------------------将放大的图片自由移动逻辑处理-------------------end----------
        return true;
    }

    /**
     * 当移动时 进行边界检查
     */
    private void checkBorderWhenTranslate() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rect.bottom;
        }

        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < width && isCheckLeftAndRight) {
            deltaX = width - rect.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 判断滑动的距离是否触发滑动的临界条件
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
