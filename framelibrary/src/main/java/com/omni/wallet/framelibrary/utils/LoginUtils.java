package com.omni.wallet.framelibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.omni.wallet.baselibrary.utils.ActivityUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.common.PageRouteConfig;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.framelibrary.entity.UserInfoEntity;
import com.omni.wallet.framelibrary.entity.event.LoginStateEvent;
import com.omni.wallet.framelibrary.http.HttpRequestUtils;
import com.omni.wallet.framelibrary.http.callback.DefaultHttpCallback;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录的工具类
 */

public class LoginUtils {
    private static final String TAG = LoginUtils.class.getSimpleName();
    //================================登录界面传递参数的Key====================================//
    // 登录之后跳转页面的Class全名
    public static final String KEY_PAGE_CLASS = "pageClassKey";
    // 登录成功之后是否需要关闭某个Activity，这里一般是上一个
    public static final String KEY_CLOSE_PAGE_ON_LOGIN_SUCCESS = "closePageKey";
    // 登录成功之后仅仅是关闭登录界面
    public static final String KEY_JUST_CLOSE_LOGIN_PAGE = "justCloseLoginPageKey";
    // 登录成功之后需要关闭的页面集合
    public static final String KEY_CLOSED_PAGE_LIST = "closePageListKey";

    public void login(final Context context, final String mobile, final String password, final LoginCallback callback) {
        MyLoginRequestCallback loginCallback = new MyLoginRequestCallback((Activity) context, true, callback);
        loginCallback.dialogText("正在登录...");
        HttpRequestUtils.login(context, mobile, password, loginCallback);
    }

    /**
     * 登录回调
     */
    private class MyLoginRequestCallback extends DefaultHttpCallback<UserInfoEntity> {
        private Context mContext;
        private LoginCallback mCallback;

        public MyLoginRequestCallback(Activity activity, boolean showLoadDialog, LoginCallback mCallback) {
            super(activity, showLoadDialog);
            this.mContext = activity;
            this.mCallback = mCallback;
        }

        @Override
        protected void onResponseSuccess(UserInfoEntity result) {
            onLoginSuccess(mContext, result);
            if (mCallback != null) {
                mCallback.onLoginSuccess(mContext);
            }
        }

        @Override
        protected void onResponseFail(Context context, String errorCode, String errorMsg) {
            super.onResponseFail(context, errorCode, errorMsg);
            User.getInstance().setToken(context, "");
            User.getInstance().setUserId(context, "");
            User.getInstance().setQrCodeLink(context, "");
            User.getInstance().setCompanyId(context, "");
            if (mCallback != null) {
                mCallback.onLoginFail(context, errorCode, errorMsg);
            }
        }

        @Override
        protected void onResponseError(Context context, String errorCode, String errorMsg) {
            super.onResponseError(context, errorCode, errorMsg);
            User.getInstance().setToken(context, "");
            User.getInstance().setUserId(context, "");
            User.getInstance().setQrCodeLink(context, "");
            User.getInstance().setCompanyId(context, "");
            if (mCallback != null) {
                mCallback.onLoginFail(context, errorCode, errorMsg);
            }
        }
    }

    /**
     * 登录成功的处理
     */
    public void onLoginSuccess(Context context, UserInfoEntity result) {
//        ToastUtils.showToast(context, "登录成功");
        // 用户Token本地化
        User.getInstance().setToken(context, result.getToken());
        // 用户ID本地化
        User.getInstance().setUserId(context, result.getUserId());
        // 二维码链接本地化
        User.getInstance().setQrCodeLink(context, result.getQRCode());
        // 用户其他信息本地化
        UserInfoUtils.saveUserInfo(context, result);
        // 发通知
        EventBus.getDefault().post(new LoginStateEvent(true));
        // 第一次登陆标识
        User.getInstance().setFirstLogin(context, result.getFirstLogin());
    }

    public void release() {
    }

    /**
     * 其他设备登录的处理
     */
    public void onLoginOtherDevices(Context context) {
        // 判断栈顶是不是登录的Activity
        String topActivityName = ActivityUtils.getTopActivitySimpleName();
        if (!StringUtils.isEmpty(topActivityName) && topActivityName.startsWith("Login")) {
            return;
        }
        // 获取该页面的Bundle
        Bundle bundle = ((Activity) context).getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
//        // 获取页面的Class
//        String clazz = ((Activity) context).getClass().getName();
//        // 将class 放到Bundle中传到登录页面
//        bundle.putString(KEY_PAGE_CLASS, clazz);
        // 打开登录界面
        switchActivity(PageRouteConfig.PAGE_LOGIN, bundle);
        // 关闭所有页面
        ActivityUtils.getInstance().finishAllActivity();
    }

    /**
     * 使用阿里ARouter进行页面跳转
     */
    protected void switchActivity(String router, Bundle bundle) {
        ARouter.getInstance().build(router).with(bundle).navigation();
    }

    /**
     * 跳转登录页，成功之后仅仅关闭登录页
     */
    public static void loginJustClosePage() {
        // 登录，成功之后仅仅关闭登录页面
        Bundle bundle = new Bundle();
        bundle.putBoolean(LoginUtils.KEY_JUST_CLOSE_LOGIN_PAGE, true);
        ARouter.getInstance().build(PageRouteConfig.PAGE_LOGIN).with(bundle).navigation();
    }

    /**
     * 登录回调
     */
    public interface LoginCallback {
        void onLoginSuccess(Context context);

        void onLoginFail(Context context, String code, String msg);
    }
}
