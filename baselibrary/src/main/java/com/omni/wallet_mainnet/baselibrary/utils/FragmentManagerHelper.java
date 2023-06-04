package com.omni.wallet_mainnet.baselibrary.utils;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Fragment切换的管理帮助类
 */
public class FragmentManagerHelper {
    private static final String TAG = FragmentManagerHelper.class.getSimpleName();

    // 管理类FragmentManager
    private FragmentManager mFragmentManager;
    // 容器布局id containerViewId
    private int mContainerViewId;

    /**
     * 构造函数
     *
     * @param fragmentManager 管理类FragmentManager
     * @param containerViewId 容器布局id containerViewId
     */
    public FragmentManagerHelper(@Nullable FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
    }

    /**
     * 获取已经添加的Fragment的数量
     */
    public int getChildCount() {
        List<Fragment> childFragments = mFragmentManager.getFragments();
        if (childFragments == null) {
            return 0;
        }
        return childFragments.size();
    }

    /**
     * 添加Fragment
     */
    private void add(Fragment fragment) {
        // 开启事物
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // 第一个参数是Fragment的容器id，需要添加的Fragment
        fragmentTransaction.add(mContainerViewId, fragment, fragment.getClass().getName());
        // 一定要commit
        fragmentTransaction.commit();
    }

    /**
     * 切换显示Fragment
     */
    public synchronized void switchFragment(Fragment fragment) {
        // 开启事物
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // 1.先隐藏当前所有的Fragment
        List<Fragment> childFragments = mFragmentManager.getFragments();
        if (childFragments != null && childFragments.size() > 0) {
            for (Fragment childFragment : childFragments) {
                fragmentTransaction.hide(childFragment);
            }
        }
//        LogUtils.e(TAG, "<<<<<<<<<<<<<< childFragments is >>>>>>>>>>>>>" + childFragments);
        // 2.如果容器里面没有我们就添加，并用全类名作为TAG，否则显示
        if (childFragments == null || !childFragments.contains(fragment)) {
            fragmentTransaction.add(mContainerViewId, fragment, fragment.getClass().getName());
        } else {
            fragmentTransaction.show(fragment);
        }
        // 替换Fragment
        // fragmentTransaction.replace(R.id.main_tab_fl,mHomeFragment);
        // 一定要commit
//        fragmentTransaction.commit();
        // 使用EventBus的时候会有问题，使用这种就能解决
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 根据Tag获取对应Fragment
     */
    public Fragment getFragmentByTag(String tag) {
        return mFragmentManager.findFragmentByTag(tag);
    }
}
