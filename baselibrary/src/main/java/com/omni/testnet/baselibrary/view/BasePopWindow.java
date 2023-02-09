package com.omni.testnet.baselibrary.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.omni.testnet.baselibrary.utils.DisplayUtil;


/**
 * PopWindow的基类，封装适配后的showAsDropDown等方法
 */

public class BasePopWindow extends PopupWindow {

    private Context mContext;

    public BasePopWindow(Context context) {
        super(context);
        this.mContext = context;
        setBackgroundDrawable(new ColorDrawable());
        // true：可以响应点击事件
        setTouchable(true);
        // 设置默认获取焦点(false:点击外部的时候消失了又出现)
        setFocusable(true);
//        // false：点击popupWindow外面的部分以及返回键popupWindow不消失
//        setOutsideTouchable(true);
    }

    public View setContentView(int layoutId) {
        View rootView = LayoutInflater.from(mContext).inflate(layoutId, null, false);
        setContentView(rootView);
        return rootView;
    }

    // 将popupWindow显示在anchor下方
    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xOff, int yOff) {
        if (Build.VERSION.SDK_INT == 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor, xOff, yOff);
    }

    /**
     * 在触摸位置弹出
     *
     * @param anchor 父控件View
     * @param view   弹出的PopWindowView
     * @param x      触摸位置X
     * @param y      触摸位置Y
     */
    public void showAtLocation(View anchor, View view, int x, int y) {
        int windowPos[] = calculatePopWindowPos(mContext, view, x, y);
        showAtLocation(anchor, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }


    /**
     * 计算popupWindow在长按view 的什么位置显示
     *
     * @param contentView 弹出框的布局View
     * @param touchX      锚点距离屏幕左边的距离
     * @param touchY      锚点距离屏幕上方的距离
     * @return popupWindow在长按view中的xy轴的偏移量
     */
    private static int[] calculatePopWindowPos(Context context, final View contentView, int touchX, int touchY) {
        final int windowLoc[] = new int[2];
        int offset = 144;
        // 获取屏幕的高宽
        final int screenHeight = DisplayUtil.getScreenHeight(context);
        final int screenWidth = DisplayUtil.getScreenWidth(context);
        // 测量弹出框View的宽高
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int popHeight = contentView.getMeasuredHeight();
        final int popWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        // 屏幕高度-触点距离左上角的高度 < popupWindow的高度
        // 如果小于弹出框的高度那么说明下方空间不够显示 popupWindow，需要放在触点的上方显示
        final boolean isNeedShowTop = (popHeight + touchY > screenHeight);
        // 判断需要向右边弹出还是向左边弹出显示
        //判断触点右边的剩余空间是否够显示popupWindow 大于就说明够显示
        final boolean isNeedShowRight = (touchX < (screenWidth / 2));
        if (isNeedShowTop) {
            //如果在上方显示 则用 触点的距离上方的距离 - 弹框的高度
            windowLoc[1] = touchY - popHeight;
        } else {
            //如果在下方显示 则用 触点的距离上方的距离
            windowLoc[1] = touchY;
        }
        if (isNeedShowRight) {
            windowLoc[0] = touchX;
        } else {
            //显示在左边的话 那么弹出框的位置在触点左边出现，则是触点距离左边距离 - 弹出框的宽度
            windowLoc[0] = touchX - popWidth - offset;
        }
        return windowLoc;
    }

}
