package com.omni.wallet.baselibrary.view.recyclerView.adapter;

/**
 * RecyclerView的多布局支持
 */

public interface MultiTypeSupport<T> {
    // 根据当前位置或者条目数据返回布局
    int getLayoutId(T item, int position);
}
