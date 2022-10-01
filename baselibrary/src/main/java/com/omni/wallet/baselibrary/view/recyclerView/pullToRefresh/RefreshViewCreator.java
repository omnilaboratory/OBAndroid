package com.omni.wallet.baselibrary.view.recyclerView.pullToRefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 下拉刷新的辅助类为了匹配所有效果
 */

public abstract class RefreshViewCreator {

    /**
     * 获取下拉刷新的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getRefreshView(Context context, ViewGroup parent);

    /**
     * 正在下拉
     *
     * @param currentDragHeight    当前拖动的高度
     * @param refreshViewHeight    刷新控件的高度
     * @param currentRefreshStatus 当前状态
     */
    public abstract void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus);

    /**
     * 取消刷新
     */
    public abstract void onCancelRefresh();

    /**
     * 正在刷新中
     */
    public abstract void onRefreshing();

    /**
     * 停止刷新
     */
    public abstract void onStopRefresh();

    public void onRelease() {
    }

}
