package com.omni.wallet.baselibrary.view.recyclerView.divider;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.StringUtils;

import java.util.List;

/**
 * 悬浮标题的Decoration
 * 注意的是标题的布局最外层目前不能设置具体数值，只能是包裹或者填充
 */

public abstract class TitleItemDecoration<T> extends RecyclerView.ItemDecoration {
    private static final String TAG = TitleItemDecoration.class.getSimpleName();
    // 数据源
    private List<T> mData;

    public TitleItemDecoration(List<T> data) {
        this.mData = data;
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //获取到视图中第一个可见的item的position
        int firstVisiblePosition = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        // 初始化和重绘的时候会出现position为-1的情况，需要避免
        if (firstVisiblePosition < 0) {
            return;
        }
        String currentTitle = getTitleText(firstVisiblePosition);
        // 标题位移
        boolean flag = translateTitle(c, firstVisiblePosition, currentTitle, parent);
        // 绘制标题
        View topTitleView = getTopTitleView(firstVisiblePosition);
        if (topTitleView == null) {
            // 不需要标题的Item直接不绘制
            return;
        }
        int toDrawWidthSpec;//用于测量的widthMeasureSpec
        int toDrawHeightSpec;//用于测量的heightMeasureSpec
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) topTitleView.getLayoutParams();
        if (params == null) {
            //这里是根据复杂布局layout的width height，new一个Lp
            params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        topTitleView.setLayoutParams(params);
        if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
        } else if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);
        } else {
            // 设置具体数据的时候这里有问题
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(params.width, View.MeasureSpec.EXACTLY);
        }
        //高度同理
        if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        } else if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
        } else {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY);
        }
        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        topTitleView.measure(toDrawWidthSpec, toDrawHeightSpec);
        topTitleView.layout(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingLeft() + topTitleView.getMeasuredWidth(), parent.getPaddingTop() + topTitleView.getMeasuredHeight());
        topTitleView.draw(c);//Canvas默认在视图顶部，无需平移，直接绘制
        if (flag) {
            c.restore();//恢复画布到之前保存的状态
        }

    }

    /**
     * 标题移动
     */
    private boolean translateTitle(Canvas c, int position, String currentTitle, RecyclerView parent) {
        RecyclerView.ViewHolder holder = parent.findViewHolderForLayoutPosition(position);
        if (holder == null) {
            return false;
        }
        boolean flag = false;
        View titleItemView = holder.itemView;
        if (position < mData.size() - 1) {
            // 默认（LinearLayout）下一个标题的索引是当前索引加1（LinearLayout和GridLayoutManager有区别）
            int nextTitlePosition = position + 1;
            // 如果是GridLayoutManager的时候
            if (parent.getLayoutManager() instanceof GridLayoutManager) {
                // 获取每行的列数
                int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
                // 获取当前position的Item占的span比例
                int spanSize = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup().getSpanSize(position);
                // 如果不是占的整行的时候，就认为是非Title类型的Item，如果占的是整行的话，就认为是Title类型的Item
                if (spanSize != spanCount) {
                    // 按照每列行数遍历，当前position逐渐增大并+1去获取下一条目，
                    // 一直增加到得到的标题与当前标题不相同的时候，就是下一个标题项的索引
                    for (int i = 0; i < spanCount; i++) {
                        nextTitlePosition = position + i + 1;
                        String nextTitleText = getTitleText(nextTitlePosition);
                        if (!currentTitle.equals(nextTitleText)) {
                            break;
                        }
                    }
                }
            }
            // 获取下一标题
            String nextItemTitle = getTitleText(nextTitlePosition);
            if (!StringUtils.isEmpty(currentTitle) && !currentTitle.equals(nextItemTitle)) {
                View topTitleView = getTopTitleView(position);
                if (topTitleView == null) {
                    return false;
                }
                int topTitleHeight = DisplayUtil.getViewHeight(topTitleView);
                if (titleItemView.getHeight() + titleItemView.getTop() < topTitleHeight) {
                    c.save();
                    flag = true;
                    c.translate(0, titleItemView.getHeight() + titleItemView.getTop() - topTitleHeight);
                }
            }
        }
        return flag;
    }

    /**
     * 获取悬浮的TopView的布局
     */
    protected abstract View getTopTitleView(int position);

    /**
     * 获取当前Position的Title文字
     */
    protected abstract String getTitleText(int position);

}
