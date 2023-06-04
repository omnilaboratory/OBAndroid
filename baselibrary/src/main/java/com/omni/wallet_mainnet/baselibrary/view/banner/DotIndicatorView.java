package com.omni.wallet_mainnet.baselibrary.view.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * BannerView中下面的点的指示器的View
 */

public class DotIndicatorView extends View {
    private static final String TAG = DotIndicatorView.class.getSimpleName();

    private Drawable mDrawable;

    public DotIndicatorView(Context context) {
        super(context);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable != null) {
            // 1.将drawable转换成Bitmap
            Bitmap bitmap = drawableToBitmap(mDrawable);
            // 2.得到圆形的Bitmap
//            Bitmap circleBitmap = getCircleBitmap(bitmap);
            // 3.将圆形的Bitmap绘制到画布上
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
        }
    }

    // 将drawable转换成Bitmap
    private Bitmap drawableToBitmap(Drawable drawable) {
        // 第一种：是BitmapDrawable的时候直接强转返回
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // 第二种：其他情况 ColorDrawable
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outBitmap);
            drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            drawable.draw(canvas);
            // 将生成的bitmap返回
            return outBitmap;
        }
        return null;
    }

    // 得到圆形的Bitmap
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        // 创建一个空的圆的Bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        // 利用circleBitmap创建一个画布
        Canvas canvas = new Canvas(circleBitmap);
        // 创建一个画笔
        Paint paint = new Paint();
        // 设置抗锯齿(下一个方法也是)
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        // 设置防抖动
        paint.setDither(true);
        // 在这个画布上画一个圆形
        // 参数：前两个代表圆心坐标，第三个是半径，第四个是一个画笔
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, paint);
        // 注意这个方法要在上面画圆之后设置，否则就没了
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 把传递进来的bitmap绘制到这个圆上面，要设置两个bitmap重叠时候的属性是（只取重叠部分）
        canvas.drawBitmap(bitmap, 0, 0, paint);
        // 将绘制好的Bitmap返回
        return circleBitmap;
    }

    // 设置图片
    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        invalidate();
    }
}
