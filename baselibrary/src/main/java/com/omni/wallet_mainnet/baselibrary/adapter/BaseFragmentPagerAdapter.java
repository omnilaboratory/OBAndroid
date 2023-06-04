package com.omni.wallet_mainnet.baselibrary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

/**
 * 解决数据更新之后缓存的Fragment不更新的问题
 */

public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager mFragmentManager;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragmentManager = fm;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        //拿到缓存的fragment，如果没有缓存的，就新建一个，新建发生在fragment的第一次初始化时
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = fragment.getTag();
        //如果是新建的fragment，f 就和getItem(position)是同一个fragment，否则进入下面
        if (fragment != getItem(position)) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            //移除旧的fragment
            fragmentTransaction.remove(fragment);
            //换成新的fragment
            fragment = getItem(position);
            //添加新fragment时必须用前面获得的tag
            fragmentTransaction.add(container.getId(), fragment, fragmentTag);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        return fragment;
    }
}
