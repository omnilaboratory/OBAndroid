package com.omni.testnet.baselibrary.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.common.Constants;
import com.omni.testnet.baselibrary.http.HttpUtils;
import com.omni.testnet.baselibrary.utils.ActivityUtils;
import com.omni.testnet.baselibrary.utils.AppManager;
import com.omni.testnet.baselibrary.utils.AppUtils;
import com.omni.testnet.baselibrary.utils.KeyboardUtils;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.MemoryUtils;
import com.omni.testnet.baselibrary.utils.PermissionUtils;
import com.omni.testnet.baselibrary.utils.StatusBarUtil;
import com.omni.testnet.baselibrary.utils.ToastUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 安卓Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Context mContext;
    // ButterKnife
    public Unbinder mBinder;
    // 获取到的Bundle
    public Bundle mBundle;
    // 双击退出相关
    protected long exitTime = 0L;
    //    protected Bundle mBundle;
    protected InputMethodManager inputMethodManager;
    private boolean mExitApp = false;

    // 如果矢量图用于选择器的话需要特别注意：你就可以正常使用Selector这样的DrawableContainers了。
    // 同时，你还开启了类似android:drawableLeft这样的compound drawable的使用权限，
    // 以及RadioButton的使用权限，以及ImageView’s src属性。
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(Constants.isCompatVectorSupport);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Activity统一管理（一定要写在super前边，否则如果初始化的时候有异常了这个Activity不会添加到列表，就没办法关闭了）
        ActivityUtils.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        this.mContext = this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 设置整体背景
        if (getWindowBackground() != null) {
            this.getWindow().setBackgroundDrawable(getWindowBackground());
        }
        // 获取Bundle中传递的数据
        mBundle = getIntent().getExtras();
        // 保证回调getBundleData方法
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        getBundleData(mBundle);
        beforeSetContentView();
        // 设置布局文件
        if (getContentView() != 0) {
            setContentView(getContentView());
        }
        // 初始化ButterKnife注解框架
        mBinder = ButterKnife.bind(this);
        // 设置是否沉浸式
        setStatusBar();
        // 如果全屏，隐藏状态栏和底部导航栏，并沉浸
        if (isFullScreenStyle()) {
            StatusBarUtil.hideSystemBarAndFullScreen(this);
        }
        // 初始化头部
        initHeader();
        // 初始化页面
        initView();
        // 初始化数据
        initData();
    }

    protected void beforeSetContentView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新设置是否全屏的Flag，因为这个Flag在切换页面的时候可能会被取消
        if (isFullScreenStyle()) {
            StatusBarUtil.hideSystemBarAndFullScreen(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 获取Bundle中传递的数据
        mBundle = getIntent().getExtras();
        // 保证回调getBundleData方法
        if (mBundle == null) {
            mBundle = new Bundle();
        }
    }

    protected abstract int getContentView();

    protected abstract void initHeader();

    protected abstract void initView();

    protected abstract void initData();


    protected Drawable getWindowBackground() {
        return null;
    }

    /**
     * 设置沉浸式，并且深色字体状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (isTransparentStatusBar()) {
            // 状态栏透明
            StatusBarUtil.setStatusBarTransparent(this);
            // 设置顶部填充条高度(可以在这里返回，目前统一在DefaultNavigationBar中配置了，所以这个方法目前基本用不到)
            if (getStatusBarTopView() != null) {
                StatusBarUtil.setStatusBarTopViewHeight(this, getStatusBarTopView());
            }
            // 状态栏深色字体
            if (isDarkFont()) {
                int result = StatusBarUtil.StatusBarLightMode(this, isDarkFont());
                // result为0，说明没有设置成功（MIUI6以上和Flyme以及安卓6.0都能设置,其余的系统只能设置状态栏颜色）
                if (result == 0) {
                    // 设置状态栏颜色
                    StatusBarUtil.setStatusBarColor(this, R.color.color_20_transparent);
                }
            }
        }
    }

    /**
     * 是否透明状态栏（默认true，子类覆写该方法即可）
     */
    protected boolean isTransparentStatusBar() {
        return true;
    }


    /**
     * 是否深色字体(由于基准色调是偏白色，所以默认深色字体)
     */
    protected boolean isDarkFont() {
        return true;
    }

    /**
     * 获取替代状态栏的View，由子类覆盖返回真正的TopView
     * 可以在这里返回，目前统一在DefaultNavigationBar中配置了，所以这个方法目前基本用不到
     */
    protected View getStatusBarTopView() {
        return null;
    }

    /**
     * 获取Bundle传递过来的数据
     */
    protected void getBundleData(Bundle bundle) {
    }

    /**
     * 是否设置全屏
     */
    protected boolean isFullScreenStyle() {
        return false;
    }

    /**
     * Activity中是否包含Fragment
     * 友盟统计中会使用，网络请求也会使用
     */
    public boolean containFragment() {
        return false;
    }


    /**
     * 不需要手动强转的通过ID获取控件的方法
     */
    protected final <T extends View> T findView(int ids) {
        return (T) findViewById(ids);
    }


    // 使用阿里ARouter进行页面跳转
    protected void switchActivity(String router, Bundle bundle) {
        ARouter.getInstance().build(router).with(bundle).navigation();
    }

    // 使用阿里ARouter进行页面跳转
    protected void switchActivityFinish(String router, Bundle bundle) {
        switchActivity(router, bundle);
        finish();
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
     * 简单的页面跳转
     */
    protected void switchActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 简单的带参数的页面跳转
     */
    protected void switchActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 简单的页面跳转
     */
    protected void switchActivityFinish(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * 简单的带参数的页面跳转
     */
    protected void switchActivityFinish(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    // *********************************权限相关*************************************//

    /**
     * 权限请求成功
     */
    protected void onPermissionSuccess(String permission) {
    }

    /**
     * 权限被拒绝
     */
    protected void onPermissionDenied(String permission) {
    }

    /**
     * 权限勾选不再提示
     */
    protected void onPermissionNeverAskAgain(String permission) {
    }

    /**
     * 请求IMEI权限
     */
    public void requestIMEI() {
        PermissionUtils.readPhoneState(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                AppUtils.getPhoneIMEI(mContext);
                onPermissionSuccess(Manifest.permission.READ_PHONE_STATE);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                LogUtils.e(TAG, "拒绝手机状态权限，IMEI获取失败");
                onPermissionDenied(Manifest.permission.READ_PHONE_STATE);
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                LogUtils.e(TAG, "手机状态权限勾选不再提示，IMEI获取失败");
                onPermissionNeverAskAgain(Manifest.permission.READ_PHONE_STATE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 放在这里是因为小米手机的onDestroy函数会延时调用，造成快速进同一个页面的时候网络请求会被cancel，但是这个方法会及时调用
        if (isFinishing()) {
            // 取消正在执行的请求
            HttpUtils.cancelCall(getClass());
        }
    }


    protected void hideSoftKeyboard(View view) {
        KeyboardUtils.hideKeyboard(view);
    }


    protected boolean onDoubleClickExit(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        // 双击退出程序
        if (System.currentTimeMillis() - exitTime > 2000) {
            final String appName = AppUtils.getAppName(mContext);
            ToastUtils.showToast(mContext, "再按一次退出" + appName);
            exitTime = System.currentTimeMillis();
        } else {
            // 退出应用的时候的操作
            onExitApplication();
            // 退出的标志位置为true
            mExitApp = true;
            // Toast取消显示
            ToastUtils.cancelToast();
            // 关闭页面，onDestroy里边会跟进标志位exitApp
            finish();
        }
        return true;
    }


    /**
     * 当应用退出的时候回调
     */
    protected void onExitApplication() {

    }


    @Override
    protected void onDestroy() {
        // 修复InputMethod导致的内存泄漏（部分机型好像没啥用）
        try {
            MemoryUtils.releaseInputMethodManagerFocus(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 打开的Activity出栈
        ActivityUtils.getInstance().finishActivity(this);
        // ButterKnife解绑
        mBinder.unbind();
        // 异步请求的时候有可能页面已经Destroy了，所以可以在异步请求的回调中根据mBinder是否为null
        // 来判断是否已经解绑，如果已经解绑了就return，否则NullPointException
        mBinder = null;
        super.onDestroy();
        // 是否退出应用，放在这里是为了解决退出应用的时候Toast不消失的问题
        if (mExitApp) {
            AppManager.getInstance().appExit(mContext);
            mExitApp = false;
        }
    }
}
