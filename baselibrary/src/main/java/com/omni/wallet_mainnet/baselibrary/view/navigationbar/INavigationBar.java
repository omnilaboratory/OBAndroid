package com.omni.wallet_mainnet.baselibrary.view.navigationbar;

/**
 * 自定义title的规范接口
 */

public interface INavigationBar {

    /**
     * 获取头部布局文件
     *
     * @return
     */
    int bindLayoutResId();


    /**
     * 头部数据和View的绑定
     */
    void applyView();

}
