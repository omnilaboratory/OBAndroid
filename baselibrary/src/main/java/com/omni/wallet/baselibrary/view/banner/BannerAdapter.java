package com.omni.wallet.baselibrary.view.banner;

import android.view.View;

/**
 * 我们自定义ViewPager要使用的Adapter，目前是用来传递自定义的BannerItemView
 */

public abstract class BannerAdapter {

    // 这个方法用来返回我们自定义的BannerItemView，交给使用者去实现，我们只提供抽象类
    public abstract View getView(int position, View convertView);

    // 获取需要显示的页面数量
    public abstract int getPageCount();

    // 获取当前需要展示的文字描述
    // 由于文字描述不是必须展示的，所以这里不用抽象方法，是否覆盖由使用者决定
    public String getBannerDesc(int position) {
        return "";
    }

}
