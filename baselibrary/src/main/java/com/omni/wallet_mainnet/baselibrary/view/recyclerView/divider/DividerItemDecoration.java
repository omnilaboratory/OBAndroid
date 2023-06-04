package com.omni.wallet_mainnet.baselibrary.view.recyclerView.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * RecyclerView的万能分割线
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = DividerItemDecoration.class.getSimpleName();
    // 这个值就是列表的真正起始值
    private int mStartPosition = 0;

    private Paint mPaint;
    //取名mDivider似乎更恰当
    private Drawable mDrawable;
    //分割线高度，默认为1px
    private int mDividerHeight = 2;
    //列表的方向
    private int mOrientation;
    //系统自带的参数
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    //水平
    public static final int HORIZONTAL_LINE = RecyclerView.HORIZONTAL;
    //垂直
    public static final int VERTICAL_LINE = RecyclerView.VERTICAL;
    //水平+垂直
    public static final int BOTH_SET = 2;


    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context     上下文
     * @param orientation 列表方向
     */
    public DividerItemDecoration(Context context, int orientation) {
        this.setOrientation(orientation);
        //获取xml配置的参数
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        //typedArray.getBitmap(attr)这句是说我们可以通过我们的资源获得资源，使用我们的资源名attr去获得资源id
        //看不懂就用自己写一个分割线的图片吧，方法：ContextCompat.getBitmap(context, drawableId);
        mDrawable = a.getDrawable(0);
        //官方的解释是：回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
        //在TypedArray后调用recycle主要是为了缓存。
        a.recycle();
    }

    /**
     * 自定义分割线
     *
     * @param context     上下文
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public DividerItemDecoration(Context context, int orientation, int drawableId) {
        this.setOrientation(orientation);
        //旧的getDrawable方法弃用了，这个是新的
        mDrawable = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDrawable.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context     上下文
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    public DividerItemDecoration(int startPosition, Context context, int orientation, int drawableId) {
        this.setOrientation(orientation);
        //旧的getDrawable方法弃用了，这个是新的
        mDrawable = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDrawable.getIntrinsicHeight();
        this.mStartPosition = startPosition;
    }


    /**
     * 自定义分割线
     *
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public DividerItemDecoration(int orientation, int dividerHeight, int dividerColor) {
        this.setOrientation(orientation);
        mDividerHeight = dividerHeight;
        //抗锯齿画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        //填满颜色
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 自定义分割线
     *
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public DividerItemDecoration(int startPosition, int orientation, int dividerHeight, int dividerColor) {
        this.setOrientation(orientation);
        mDividerHeight = dividerHeight;
        //抗锯齿画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        //填满颜色
        mPaint.setStyle(Paint.Style.FILL);
        this.mStartPosition = startPosition;
    }

    /**
     * 设置方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        if (orientation < 0 || orientation > 2)
            throw new IllegalArgumentException("invalid orientation");
        mOrientation = orientation;
    }


    /**
     * 绘制分割线之后,需要留出一个外边框,就是说item之间的间距要换一下
     *
     * @param outRect outRect.set(0, 0, 0, 0);的四个参数理解成margin就好了
     * @param view    视图
     * @param parent  父级view
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //下面super...代码其实调用的就是那个过时的getItemOffsets,也就是说这个方法体内容也可以通通移到那个过时的getItemOffsets中
        super.getItemOffsets(outRect, view, parent, state);
        //获取layoutParams参数
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        //当前位置
        int itemPosition = layoutParams.getViewLayoutPosition();
        //ItemView数量
        int childCount = parent.getAdapter().getItemCount();
        switch (mOrientation) {
            case BOTH_SET:
                //获取Layout的相关参数
                int spanCount = this.getSpanCount(parent);
                if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一行，则不需要绘制底部
                    outRect.set(0, 0, mDividerHeight, 0);
                } else if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一列，则不需要绘制右边
                    outRect.set(0, 0, 0, mDividerHeight);
                } else {
                    outRect.set(0, 0, mDividerHeight, mDividerHeight);
                }
                break;
            case VERTICAL_LINE:
                childCount -= 1;
                //水平布局右侧留Margin,如果是最后一列,就不要留Margin了
                outRect.set(0, 0, (itemPosition != childCount) ? mDividerHeight : 0, 0);
                break;
            case HORIZONTAL_LINE:
                childCount -= 1;
                int position = parent.getChildAdapterPosition(view);
                // 头部的下面位置不留距离，也就是不画分割线
                if (position >= mStartPosition) {
                    //垂直布局底部留边，最后一行不留
                    outRect.set(0, 0, 0, (itemPosition != childCount) ? mDividerHeight : 0);
                }
                break;
        }
    }

    /**
     * 绘制分割线
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL_LINE) {
            drawVertical(c, parent);
        } else if (mOrientation == HORIZONTAL_LINE) {
            drawHorizontal(c, parent);
        } else {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }
    }

    /**
     * 绘制横向 item 分割线
     *
     * @param canvas 画布
     * @param parent 父容器
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int x = parent.getPaddingLeft();
        final int width = parent.getMeasuredWidth() - parent.getPaddingRight();
        //getChildCount()(ViewGroup.getChildCount) 返回的是显示层面上的“所包含的子 View 个数”。
        final int childSize = parent.getChildCount();
        // 最后一个条目不画分割线
        for (int i = 0; i < childSize - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            //item底部的Y轴坐标+margin值
            final int y = child.getBottom() + layoutParams.bottomMargin;
            final int height = y + mDividerHeight;
//            LogUtils.e(TAG, height + "===================");
            if (mDrawable != null) {
                //setBounds(x,y,width,height); x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点
                // width:组件的长度 height:组件的高度
                mDrawable.setBounds(x, y, width, height);
                mDrawable.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(x, y, width, height, mPaint);
            }
        }
    }

    /**
     * 绘制纵向 item 分割线
     *
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
//        LogUtils.e(TAG, "drawVertical============>");
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDrawable != null) {
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }


    /**
     * 获取列数
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }


    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0)
                    return true;
            } else {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount)
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        int orientation;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
//            childCount = childCount - childCount % spanCount;
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一行，则不需要绘制底部
                if (childCount % spanCount == 0) {// 整行的时候
                    if (pos >= childCount - spanCount) {
                        return true;
                    }
                } else {// 不是整行的时候
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount) {
                        return true;
                    }
                }
            } else {// StaggeredGridLayoutManager 横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一行，则不需要绘制底部
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            } else {// StaggeredGridLayoutManager 横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                    return true;
            }
        }
        return false;
    }
}
