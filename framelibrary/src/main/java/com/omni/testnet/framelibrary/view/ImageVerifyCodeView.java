package com.omni.testnet.framelibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.omni.testnet.baselibrary.base.BaseActivity;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.PermissionChecker;
import com.omni.testnet.baselibrary.utils.image.BitmapUtils;
import com.omni.testnet.framelibrary.R;
import com.omni.testnet.framelibrary.http.callback.DefaultHttpCallback;


/**
 * 图形验证码控件
 * 宽高：10x30吧
 */

public class ImageVerifyCodeView extends AppCompatImageView implements View.OnClickListener {
    private static final String TAG = ImageVerifyCodeView.class.getSimpleName();
    private Context mContext;
    private boolean isClickable = true;
    private String mReGetTet = "点击重新获取";
    private Paint mPaint;
    private Rect mTextBounds = new Rect();
    private int mTextWidth;
    private String mRandomStr;

    public ImageVerifyCodeView(Context context) {
        this(context, null);
    }

    public ImageVerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageVerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(sp2px(11));
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.color_1977da));
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_f7f7f7));
        setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.e(TAG, "初始化获取图形验证码");
        // 初始化的时候获取一次验证码
        mRandomStr = String.valueOf(System.currentTimeMillis());
//        HttpRequestUtils.getImageVerify(mContext, mRandomStr, new ImageVerifyRequestCallback());
    }

    @Override
    public void onClick(View v) {
        if (!isClickable) {
            LogUtils.e(TAG, "重复点击");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionChecker.checkReadPhoneStatePermission(mContext)) {
            ((BaseActivity) mContext).requestIMEI();
            return;
        }
        LogUtils.e(TAG, "点击获取图形验证码");
        isClickable = false;
        mRandomStr = String.valueOf(System.currentTimeMillis());
//        HttpRequestUtils.getImageVerify(mContext, mRandomStr, new ImageVerifyRequestCallback());
    }

    /**
     * 刷新图形验证码
     */
    public void refresh() {
        isClickable = false;
        mRandomStr = String.valueOf(System.currentTimeMillis());
//        HttpRequestUtils.getImageVerify(mContext, mRandomStr, new ImageVerifyRequestCallback());
    }

    /**
     * 图形验证码接口回调
     */
    private class ImageVerifyRequestCallback extends DefaultHttpCallback {
        @Override
        public void onSuccess(final Context context, final byte[] result) {
            isClickable = true;
            if (result != null) {
                LogUtils.e(TAG, "====返回的图片数组长度是====>" + result.length);
                post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap imageBitmap = BitmapUtils.arrayToBitmap(result);
                        setImageBitmap(imageBitmap);
                    }
                });
            }
        }

        @Override
        public void onError(Context context, String errorCode, String errorMsg) {
            super.onError(context, errorCode, errorMsg);
            isClickable = true;
            post(new Runnable() {
                @Override
                public void run() {
                    // 显示点击重新获取
                    drawReGetText();
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureTextWidth();
    }

    /**
     * 显示重新获取文字
     */
    private void drawReGetText() {
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            Bitmap bmp = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float y = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
            canvas.drawText(mReGetTet, (getMeasuredWidth() - mTextWidth) / 2, y, mPaint);
            setImageBitmap(bmp);
        }
    }

    /**
     * 测量文字的宽度
     */
    private void measureTextWidth() {
        mPaint.getTextBounds(mReGetTet, 0, mReGetTet.length(), mTextBounds);
        mTextWidth = mTextBounds.width();
    }

    public String getRandomStr() {
        return mRandomStr;
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());
    }
}
