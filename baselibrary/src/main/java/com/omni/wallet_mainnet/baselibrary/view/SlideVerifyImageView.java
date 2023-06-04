package com.omni.wallet_mainnet.baselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 滑动验证的View
 */

public class SlideVerifyImageView extends AppCompatImageView {
    private static final String TAG = SlideVerifyImageView.class.getSimpleName();
    // 定义画笔
    private Paint mPaint;
    // 验证的图像
    private Bitmap mBitmap;
    // 验证滑块的高
    private int mUnitHeight;
    // 验证滑块的宽
    private int mUnitWidth;
    // 验证滑块宽占用整体图片大小的比例,默认1/5
    private int mUnitWidthScale;
    // 验证滑块高度占用整体图片大小的比例,默认1/4
    private int mUnitHeightScale;
    // 随机生成滑块的X坐标
    private int mUnitRandomX;
    // 随机生成滑块的Y坐标
    private int mUnitRandomY;
    // 滑块移动的距离
    private float mUnitMoveDistance = 0;
    // 滑块图像
    private Bitmap mUnitBitmap;
    //  验证位置图像
    private Bitmap mShowBitmap;
    // 背景阴影图像
    private Bitmap mShadeBitmap;
    // 是否需要旋转
    private boolean mNeedRotate;
    // 旋转的角度
    private int mRotate;
    // 判断是否完成的偏差量，默认为10
    public int mDeviate;
    // 判断是否重新绘制图像
    private boolean isReSet = true;
    // 回调
    private ImageVerifyListener mListener;
    // 默认的误差
    private static final int DEFAULT_DEVIATE = 10;


    /**
     * 设置回调
     */
    public void setListener(ImageVerifyListener listener) {
        this.mListener = listener;
    }

    public SlideVerifyImageView(Context context) {
        this(context, null);
    }

    public SlideVerifyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideVerifyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化属性
        initAttribute(context, attrs);
        // 初始化
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (mNeedRotate) {
            mRotate = (int) (Math.random() * 3) * 90;
        } else {
            mRotate = 0;
        }
    }

    /**
     * 初始化属性
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideVerifyImageView);
        mUnitWidth = typedArray.getDimensionPixelOffset(R.styleable.SlideVerifyImageView_unitHeight, 0);
        mUnitHeight = typedArray.getDimensionPixelOffset(R.styleable.SlideVerifyImageView_unitHeight, 0);
        mUnitHeightScale = typedArray.getInteger(R.styleable.SlideVerifyImageView_unitHeightScale, 4);
        mUnitWidthScale = typedArray.getInteger(R.styleable.SlideVerifyImageView_unitWidthScale, 5);
        Drawable showBitmap = typedArray.getDrawable(R.styleable.SlideVerifyImageView_unitShowSrc);
        mShowBitmap = drawableToBitmap(showBitmap);
        Drawable shadeBitmap = typedArray.getDrawable(R.styleable.SlideVerifyImageView_unitShadeSrc);
        mShadeBitmap = drawableToBitmap(shadeBitmap);
        mNeedRotate = typedArray.getBoolean(R.styleable.SlideVerifyImageView_needRotate, true);
        mDeviate = typedArray.getInteger(R.styleable.SlideVerifyImageView_deviate, DEFAULT_DEVIATE);
        typedArray.recycle();
    }

    /**
     * 随机生成生成滑块的XY坐标
     */
    private void initUnitXY() {
        mUnitRandomX = (int) (Math.random() * (mBitmap.getWidth() - mUnitWidth));
        mUnitRandomY = (int) (Math.random() * (mBitmap.getHeight() - mUnitHeight));
        // 防止生成的位置距离太近
        if (mUnitRandomX <= mBitmap.getWidth() / 2) {
            mUnitRandomX = mUnitRandomX + mBitmap.getWidth() / 4;
        }
        // 防止生成的X坐标截图时导致异常
        if (mUnitRandomX + mUnitWidth > getWidth()) {
            initUnitXY();
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isReSet) {
            mBitmap = getBaseBitmap();
            if (0 == mUnitWidth) {
                mUnitWidth = mBitmap.getWidth() / mUnitWidthScale;
            }
            if (0 == mUnitHeight) {
                mUnitHeight = mBitmap.getHeight() / mUnitHeightScale;
            }
            initUnitXY();
            mUnitBitmap = Bitmap.createBitmap(mBitmap, mUnitRandomX, mUnitRandomY, mUnitWidth, mUnitHeight);
        }
        isReSet = false;
        canvas.drawBitmap(drawTargetBitmap(), mUnitRandomX, mUnitRandomY, mPaint);
        canvas.drawBitmap(drawResultBitmap(mUnitBitmap), mUnitMoveDistance, mUnitRandomY, mPaint);
    }

    /**
     * 重置
     */
    public void reSet() {
        isReSet = true;
        mUnitMoveDistance = 0;
        if (mNeedRotate) {
            mRotate = (int) (Math.random() * 3) * 90;
        } else {
            mRotate = 0;
        }
        invalidate();
    }

    /**
     * 获取每次滑动的平均偏移值
     */

    public float getAverageDistance(int max) {
        return (float) (mBitmap.getWidth() - mUnitWidth) / max;
    }

    /**
     * 滑块移动距离
     */
    public void setUnitMoveDistance(float distance) {
        mUnitMoveDistance = distance;
        // 防止滑块滑出图片
        if (mUnitMoveDistance > mBitmap.getWidth() - mUnitWidth) {
            mUnitMoveDistance = mBitmap.getWidth() - mUnitWidth;
        }
        invalidate();
    }

    /**
     * 验证是否拼接成功
     */
    public void testPuzzle() {
        if (Math.abs(mUnitMoveDistance - mUnitRandomX) <= mDeviate) {
            if (null != mListener) {
                mListener.onSuccess();
            }
        } else {
            if (null != mListener) {
                mListener.onFail();
            }
        }
    }

    /**
     * 创建遮挡的图片(阴影部分)
     */

    private Bitmap drawTargetBitmap() {
        // 绘制图片
        Bitmap showBitmap;
        if (null != mShowBitmap) {
            showBitmap = handleBitmap(mShowBitmap, mUnitWidth, mUnitHeight);
        } else {
            showBitmap = handleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shape_verify_image_show), mUnitWidth, mUnitHeight);
        }
        // 如果需要旋转图片,进行旋转，旋转后为了保持和滑块大小一致,需要重新缩放比例
        if (mNeedRotate) {
            showBitmap = handleBitmap(rotateBitmap(mRotate, showBitmap), mUnitWidth, mUnitHeight);
        }
        return showBitmap;
    }

    /**
     * 创建结合的图片(滑块)
     */
    private Bitmap drawResultBitmap(Bitmap bitmap) {
        // 绘制图片
        Bitmap shadeBitmap;
        if (null != mShadeBitmap) {
            shadeBitmap = handleBitmap(mShadeBitmap, mUnitWidth, mUnitHeight);
        } else {
            shadeBitmap = handleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shape_verify_image_shade), mUnitWidth, mUnitHeight);
        }
        // 如果需要旋转图片,进行旋转,旋转后为了和画布大小保持一致,避免出现图像显示不全,需要重新缩放比例
        if (mNeedRotate) {
            shadeBitmap = handleBitmap(rotateBitmap(mRotate, shadeBitmap), mUnitWidth, mUnitHeight);
        }
        Bitmap resultBmp = Bitmap.createBitmap(mUnitWidth, mUnitHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(resultBmp);
        canvas.drawBitmap(shadeBitmap, new Rect(0, 0, mUnitWidth, mUnitHeight), new Rect(0, 0, mUnitWidth, mUnitHeight), paint);
        // 选择交集取上层图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas.drawBitmap(bitmap, new Rect(0, 0, mUnitWidth, mUnitHeight), new Rect(0, 0, mUnitWidth, mUnitHeight), paint);
        return resultBmp;
    }

    /**
     * 获取实际显示的图片
     */
    public Bitmap getBaseBitmap() {
        Bitmap b = drawableToBitmap(getDrawable());
        // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
        float scaleX = getWidth() * 1.0f / b.getWidth();
        float scaleY = getHeight() * 1.0f / b.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
    }

    /**
     * drawable转bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 缩放图片
     */

    public static Bitmap handleBitmap(Bitmap bp, float x, float y) {
        int w = bp.getWidth();
        int h = bp.getHeight();
        float sx = x / (float) w;
        float sy = y / (float) h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bp, 0, 0, w, h, matrix, true);
    }

    /**
     * 旋转图片
     */
    public Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 拼图成功的回调
     **/
    public interface ImageVerifyListener {
        void onSuccess();

        void onFail();
    }
}
