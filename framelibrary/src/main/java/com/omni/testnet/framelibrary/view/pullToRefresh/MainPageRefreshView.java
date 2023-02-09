package com.omni.testnet.framelibrary.view.pullToRefresh;


import com.omni.testnet.framelibrary.R;

/**
 * 主页专用的下拉刷新的Header
 */

public class MainPageRefreshView extends DefaultRefreshView {

    @Override
    protected int getLayoutId() {
        return R.layout.view_refresh_header_main_page;
    }
}
