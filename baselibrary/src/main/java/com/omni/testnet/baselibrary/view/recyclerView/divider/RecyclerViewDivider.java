package com.omni.testnet.baselibrary.view.recyclerView.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView的分割线
 */

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Drawable mDivider;
    private int mOrientation;
    // 用来避免添加水平分割线的时候，如果添加了头部的时候，会在列表第一条上添加分割线的情况
    // 这个值就是列表的真正起始值
    private int mStartPosition = 0;
    public static final int HORIZONTAL_DIVIDER = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_DIVIDER = LinearLayoutManager.VERTICAL;

//    //我们通过获取系统属性中的listDivider来添加，在系统中的AppTheme中设置
//    public static final int[] ATRRS = new int[]{
//            android.R.attr.listDivider
//    };

    public RecyclerViewDivider(Context context, Drawable divider, int orientation) {
        this.mContext = context;
//        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
//        this.mDivider = ta.getBitmap(0);
//        ta.recycle();
        this.mDivider = divider;
        setOrientation(orientation);
    }

    public RecyclerViewDivider(Context context, Drawable divider, int orientation, int startPosition) {
        this.mContext = context;
//        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
//        this.mDivider = ta.getBitmap(0);
//        ta.recycle();
        this.mDivider = divider;
        this.mStartPosition = startPosition;
        setOrientation(orientation);
    }

    //设置屏幕的方向
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_DIVIDER && orientation != VERTICAL_DIVIDER) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_DIVIDER) {
            drawHorizontalLine(c, parent, state);
        } else {
            drawVerticalLine(c, parent, state);
        }
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    public void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //画竖线
    public void drawVerticalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL_DIVIDER) {
            //画横线，就是往下偏移一个分割线的高度
            int position = parent.getChildAdapterPosition(view);
            // 只要不是列表实际的第一条就空出来分割线的距离（如果有头部的时候，头部上方默认也不添加）
            if (position > mStartPosition) {
                outRect.top = mDivider.getIntrinsicHeight();
            }
        } else {
            // TODO GridView的时候分割线需要修改，有问题
            //画竖线，就是往右偏移一个分割线的宽度
            outRect.right = mDivider.getIntrinsicWidth();
        }
    }
}
