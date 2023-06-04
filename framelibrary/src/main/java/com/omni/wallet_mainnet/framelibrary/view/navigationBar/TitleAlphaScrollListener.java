package com.omni.wallet_mainnet.framelibrary.view.navigationBar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 标题栏渐变的RecyclerView滚动监听
 */

public abstract class TitleAlphaScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = TitleAlphaScrollListener.class.getSimpleName();
    // 需要滚动的高度
    private int mScrollHeight;

    public TitleAlphaScrollListener() {
    }

    public void setScrollHeight(int scrollHeight) {
        this.mScrollHeight = scrollHeight;
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        // 获取当前第一条可见的Item的View。
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        // 获取该View的getTop
        int top = firstVisibleChildView.getTop();
        // 索引大于1，不管
        if (position > 1) {
            return;
        }
        // 索引为0，为RefreshHeader，默认调用全透明
        if (position == 0) {
            // 由于默认显示refreshHeader的1Px，所以初始化的时候需要调用一次使得标题栏默认透明
            onTitleAlphaChange(0);
            return;
        }
        // 其他的全部根据滚动距离判断
        if (top <= 0 && -top <= mScrollHeight) {
            float scale = (float) -top / mScrollHeight;
            float alpha = scale * 255;
            onTitleAlphaChange((int) alpha);
        } else if (top <= 0 && -top > mScrollHeight) {// 大于滚动限制距离只会透明度置为255，避免快速滑动出现透明度到不了255
            onTitleAlphaChange(255);
        }
    }

    /**
     * 改变标题栏的背景色透明度(0-255)
     */
    protected abstract void onTitleAlphaChange(int alpha);


}
