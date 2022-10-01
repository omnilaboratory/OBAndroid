package com.omni.wallet.baselibrary.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.utils.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment的基类
 */

public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    protected Context mContext;
    protected View rootView;
    protected boolean mIsVisibility;
    // 是否第一次初始化
    private boolean isFirstInit = true;
    // 延时加载相关
    private boolean isPrepared;
    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;
    // ButterKnife
    protected Unbinder mBinder;
    // 保存数据的Bundle
    private Bundle savedState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mContext = getActivity();
        // 获取传递过来的数据
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        // 回调回去
        getBundleData(bundle);
        //
        if (getLayoutId() != 0) {
            rootView = View.inflate(mContext, getLayoutId(), null);
        }
        mBinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore State Here
        if (!restoreStateFromArguments()) {
            // First Time, Initialize something here
            onFirstTimeLaunched();
        }
        initPrepare(); //第一次置isPrepared为true
        initView(rootView);
        initHeader();
        setStatusBar();
        initData();
    }


    @Override
    public void onResume() {
        super.onResume();
        // 第一次加载的时候isVisible是false，所以这里用了一个变量去判断是否首次加载
        // 如果是首次的话，直接调用，加载完了把变量置为false
        // 如果不是首次加载的话，根据isVisible去判断是否调用
        if (isFirstInit) {
            onPageResume();
            isFirstInit = false;
        } else if (isVisible()) {
            onPageResume();
        }
        if (isVisible()) {
            onPageVisible();
        }
    }


    @Override
    public void onDestroy() {
        // ButterKnife解除注入
        mBinder.unbind();
        // 用来判断页面是不是销毁了，避免页面销毁之后回调的加载导致页面崩溃
        mBinder = null;
        // 释放接口请求
        HttpUtils.cancelCall(getClass());
        super.onDestroy();
    }


    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    // 该方法在初始化和页面pause的时候都不会调用，只在每次fragment可见不可见切换的时候会调用
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.mIsVisibility = !hidden;
        if (mIsVisibility) {
            onPageVisible();
        } else {
            onPageInVisible();
        }
    }


    /**
     * 设置沉浸式，并且深色字体状态栏
     * TODO 这里一般不要使用，Activity的状态栏默认是沉浸式并且已经着色的，这里重复调用设置颜色，
     * TODO 在4.4到5.0的系统会出现随着着色次数增加导致状态栏颜色越来越深的问题
     */
    private void setStatusBar() {
        // 状态栏透明
        if (isTransparentStatusBar()) {
            StatusBarUtil.setStatusBarTransparent(getActivity());
        }
        // 状态栏颜色
        if (getStatusBarColor() != 0) {
            StatusBarUtil.setStatusBarColor(getActivity(), getStatusBarColor());
        }
        // 设置顶部填充条高度
        if (getStatusBarTopView() != null) {
            StatusBarUtil.setStatusBarTopViewHeight(getActivity(), getStatusBarTopView());
        }
        // 状态栏深色字体
        if (isDarkFont()) {
            StatusBarUtil.StatusBarLightMode(getActivity(), isDarkFont());
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View rootView);

    protected abstract void initData();

    protected void getBundleData(Bundle bundle) {
    }

    /**
     * 初始化头部
     */
    protected void initHeader() {
    }

    /**
     * 第一次fragment可见
     */
    protected void onFirstUserVisible() {
    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    protected void onUserVisible() {
    }

    /**
     * 第一次fragment不可见
     */
    protected void onFirstUserInvisible() {
    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    protected void onUserInvisible() {
    }


    /**
     * Fragment可见状态的时候调用，这个方法 用来做页面切换时候的数据刷新
     * 1.几个Fragment切换的时候
     * 2.从其他Activity回到该Activity的时候
     */
    protected void onPageVisible() {
    }

    protected void onPageInVisible() {
    }

    protected void onPageResume() {
    }


    /**
     * 是否透明状态栏（默认false，子类覆写该方法即可）
     */
    protected boolean isTransparentStatusBar() {
        return false;
    }

    /**
     * 状态栏颜色
     */
    protected int getStatusBarColor() {
        return 0;
    }

    /**
     * 是否深色字体
     */
    protected boolean isDarkFont() {
        return false;
    }

    /**
     * 获取状态栏顶部的View
     */
    protected View getStatusBarTopView() {
        return null;
    }

    // ==========================因内存被回收和恢复相关=============================//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Save State Here
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            if (b != null) {
                b.putBundle(TAG, savedState);
            }
        }
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        onSaveState(state);
        return state;
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (b != null) {
            savedState = b.getBundle(TAG);
            if (savedState != null) {
                restoreState();
                return true;
            }
        }
        return false;
    }

    private void restoreState() {
        if (savedState != null) {
            // For Example
            //tv1.setText(savedState.getString(text));
            onRestoreState(savedState);
        }
    }

    /**
     * 首次启动的时候回调
     */
    protected void onFirstTimeLaunched() {

    }

    /**
     * Fragment因内存被回收的时候回调
     */
    protected void onSaveState(Bundle outState) {

    }

    /**
     * Fragment因内存被回然后收恢复的时候回调
     */
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    // ==========================因内存被回收和恢复相关=============================//


    /**
     * 不需要手动强转的通过ID获取控件的方法
     */
    protected final <T extends View> T findView(int ids) {
        return (T) rootView.findViewById(ids);
    }

    // 使用阿里ARouter进行页面跳转
    protected void switchActivity(String router, Bundle bundle) {
        ARouter.getInstance().build(router).with(bundle).navigation();
    }

    // 使用阿里ARouter进行页面跳转
    protected void switchActivityFinish(String router, Bundle bundle) {
        switchActivity(router, bundle);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    // 使用阿里ARouter进行页面跳转
    protected void switchActivity(String router) {
        switchActivity(router, null);
    }

    // 使用阿里ARouter进行页面跳转
    protected void switchActivityFinish(String router) {
        switchActivityFinish(router, null);
    }

    /**
     * 简单的页面跳转并获取结果
     */
    protected void switchActivityForResult(Class clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 简单的页面跳转
     */
    protected void switchActivity(Class clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * 简单的带参数的页面跳转
     */
    protected void switchActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 简单的页面跳转
     */
    protected void switchActivityFinish(Class clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * 简单的带参数的页面跳转
     */
    protected void switchActivityFinish(Class clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtras(bundle);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
