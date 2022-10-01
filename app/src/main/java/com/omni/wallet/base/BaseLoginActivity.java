package com.omni.wallet.base;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.omni.wallet.baselibrary.utils.ActivityUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.view.editText.MultiEditTextWatcher;
import com.omni.wallet.calback.DefaultLoginCallback;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.framelibrary.utils.LoginUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 登录的基类
 * Created by fa on 2019/11/22.
 */

public abstract class BaseLoginActivity extends AppBaseActivity {
    private static final String TAG = BaseLoginActivity.class.getSimpleName();

    // 登录工具类
    private LoginUtils mLoginUtils;
    // 携带的Bundle
    protected Bundle mBundle;
    // 控制按钮是否可用的文本变动监听
    protected MultiEditTextWatcher mMultiTextWatcher;
    // 跳转或者需要关闭的页面的Class
    protected String mPageClass;
    // 登录成功之后是否关闭页面
    protected boolean mClosePage;
    // 登录成功之后是不是仅仅关闭登录界面
    protected boolean mJustCloseLoginPage = false;
    // 需要关闭的页面的Class集合
    protected List<String> mPageClassList;

    @Override
    protected void getBundleData(Bundle bundle) {
        this.mBundle = bundle;
        mPageClass = bundle.getString(LoginUtils.KEY_PAGE_CLASS);
        mClosePage = bundle.getBoolean(LoginUtils.KEY_CLOSE_PAGE_ON_LOGIN_SUCCESS);
        mJustCloseLoginPage = bundle.getBoolean(LoginUtils.KEY_JUST_CLOSE_LOGIN_PAGE);
        mPageClassList = bundle.getStringArrayList(LoginUtils.KEY_CLOSED_PAGE_LIST);
    }

    @Override
    protected void initData() {
        mLoginUtils = new LoginUtils();
        EventBus.getDefault().register(this);
    }

    /**
     * 登录
     */
    protected void login(String account, String password) {
        mLoginUtils.login(mContext, account, password, new DefaultLoginCallback() {

            @Override
            public void onLoginSuccess(Context context) {
                super.onLoginSuccess(context);
                onSuccessLogin();
            }

            @Override
            public void onLoginFail(Context context, String code, String msg) {
                onFailLogin(code, msg);
            }
        });
    }

    /**
     * 登录成功统一处理
     */
    protected void onSuccessLogin() {
        // 根据Class 跳转
        if (!StringUtils.isEmpty(mPageClass)) {
            Class<?> pageClazz = null;
            try {
                pageClazz = Class.forName(mPageClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (pageClazz != null) { // 根据页面Class跳转
                if (mClosePage) {
                    ActivityUtils.getInstance().finishActivity(mPageClass);
                }
                switchActivityFinish(pageClazz, mBundle);
            }
            return;
        }
        // 判断页面关闭方式
        if (mJustCloseLoginPage) {
            finish();
        } else if (StringUtils.isEmpty(User.getInstance().getFirstLogin(mContext))) {
            Bundle bundle = new Bundle();
        } else {
            // 否则直接去主页
            // 是否需要关闭传递的某些个页面
            if (mClosePage) {
                if (mPageClassList != null && mPageClassList.size() > 0) {
                    for (String clazz : mPageClassList) {
                        ActivityUtils.getInstance().finishActivity(clazz);
                    }
                }
                if (!StringUtils.isEmpty(mPageClass)) {
                    ActivityUtils.getInstance().finishActivity(mPageClass);
                }
            }
        }
    }

    protected void onFailLogin(String errorCode, String errorMessage) {
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mLoginUtils != null) {
            mLoginUtils.release();
            mLoginUtils = null;
        }
        super.onDestroy();
    }

    protected abstract List<EditText> getEditList();
}
