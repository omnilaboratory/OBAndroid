package com.omni.wallet_mainnet.baselibrary.view.refreshView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * RefreshLayout的header接口
 */
public abstract class RefreshLayoutHeaderCreator {
    /**
     * 获取下拉刷新的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getRefreshView(Context context, ViewGroup parent);

    /**
     * 松手，头部隐藏后会回调这个方法
     */
    public abstract void onReset();

    /**
     * 下拉出头部的一瞬间调用
     */
    public abstract void onShowHeader();

    /**
     * 正在刷新的时候调用
     */
    public abstract void onRefreshing();

    /**
     * 头部滚动的时候持续调用
     *
     * @param currentMarginTop    target当前偏移高度
     * @param lastMarginTop       target上一次的偏移高度
     * @param showRefreshDistance 可以松手刷新的高度
     * @param isTouch             手指是否按下状态（通过scroll自动滚动时需要判断）
     * @param state               当前状态
     */
    public abstract void onPulling(float currentMarginTop, float lastMarginTop, float showRefreshDistance, boolean isTouch, int state);

    /**
     * 刷新成功的时候调用
     */
    public abstract void onComplete();

    public void onRelease() {

    }
}
