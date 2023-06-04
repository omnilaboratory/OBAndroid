package com.omni.wallet_mainnet.baselibrary.view.recyclerView.pullToRefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 上拉加载更多的辅助类为了匹配所有效果，主要用来创建加载的时候显示的View
 */

public abstract class LoadViewCreator {
    /**
     * 获取上拉加载更多的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getLoadView(Context context, ViewGroup parent);

    /**
     * 正在上拉
     *
     * @param currentDragHeight 当前拖动的高度
     * @param loadViewHeight    总的加载高度
     * @param currentLoadStatus 当前状态
     */
    public abstract void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus);

    /**
     * 正在加载中
     */
    public abstract void onLoading();

    /**
     * 取消上拉加载
     */
    public abstract void onCancelLoad();

    /**
     * 停止加载
     */
    public abstract void onStopLoad();

    /**
     * 获取不到更多数据的时候回调
     */
    public abstract void setLoadNoData(boolean isNoData);


}
