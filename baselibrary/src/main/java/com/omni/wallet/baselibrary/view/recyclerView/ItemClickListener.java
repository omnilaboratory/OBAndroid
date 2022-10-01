package com.omni.wallet.baselibrary.view.recyclerView;


/**
 * 父布局Item点击监听接口
 */

public interface ItemClickListener<T> {
    /**
     * 展开子Item
     *
     * @param bean
     */
    void onExpandChildren(T bean);

    /**
     * 隐藏子Item
     *
     * @param bean
     */
    void onHideChildren(T bean);
}
