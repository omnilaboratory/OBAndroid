package com.omni.wallet_mainnet.baselibrary.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * 像素和dp转换类
 */
public class DisplayUtil {
    private static final String TAG = DisplayUtil.class.getSimpleName();

    // 把dip转换成px
    public static int dp2px(Context context, float dpValue) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
        return dip2px(dpValue, scale);
    }
//    // 把dip转换成px
//    public static int dp2px(Context context, float dpValue) {
////        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
//        float scale = context.getResources().getDisplayMetrics().density;
////        return (int) (dpValue * scale + 0.5f);
//        return dip2px(dpValue, scale);
//    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px2dip(pxValue, scale);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (spValue * scale + 0.5f);
        return dip2px(spValue, scale);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (pxValue / fontScale + 0.5f);
        return px2dip(pxValue, fontScale);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    private static int dip2px(float dipValue, float scale) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    private static int px2dip(float pxValue, float scale) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取View的高度
     */
    public static int getViewHeight(View view) {
        if (view == null) {
            return 0;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    /**
     * 获取View宽度
     */
    public static int getViewWidth(View view) {
        if (view == null) {
            return 0;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    /**
     * 获取屏幕分辨率
     */
    public static Point getScreenMetrics(Context context) {
        // 获取屏幕分辨率
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        return point;
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void setTextSizePX(TextView textView, int px) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
    }

    public static void setTextSizeSP(TextView textView, int sp) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public static void setTextSizeDP(TextView textView, float dp) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    public static double getResetX(Context context, double x) {
        double ratioX = (double) 375 / getScreenWidth(context);
        return x / ratioX;
    }

    public static double getResetY(Context context, double y) {
        double ratioY = (double) 667 / getScreenHeight(context);
        return y / ratioY;
    }

//    public static int[] getScreenSize(Context context) {
//        int[] size = new int[2];
//
//        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display d = w.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        d.getMetrics(metrics);
//        int widthPixels = metrics.widthPixels;
//        int heightPixels = metrics.heightPixels;
//
//        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
//            try {
//                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
//                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
//            } catch (Exception ignored) {
//            }
//        if (Build.VERSION.SDK_INT >= 17)
//            try {
//                Point realSize = new Point();
//                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
//                widthPixels = realSize.x;
//                heightPixels = realSize.y;
//            } catch (Exception ignored) {
//            }
//        size[0] = widthPixels;
//        size[1] = heightPixels;
//        return size;
//    }


}
