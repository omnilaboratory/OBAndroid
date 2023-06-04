package com.omni.wallet_mainnet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.framelibrary.entity.UserInfoEntity;
import com.omni.wallet_mainnet.framelibrary.http.HttpRequestUtils;
import com.omni.wallet_mainnet.framelibrary.http.callback.DefaultHttpCallback;


/**
 * 用户信息的工具类
 */

public class UserInfoUtils {
    private static final String TAG = UserInfoUtils.class.getSimpleName();

    /**
     * 接口获取用户信息
     */
    public void getUserInfo(Context context, UserInfoCallback callback) {
        HttpRequestUtils.getUserInfo(context, new MyUserInfoRequestCallback(context, callback));
    }

    /**
     * 用户信息接口回调
     */
    private class MyUserInfoRequestCallback extends DefaultHttpCallback<UserInfoEntity> {
        private Context mContext;
        private UserInfoCallback mCallback;


        public MyUserInfoRequestCallback(Context context, UserInfoCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        protected void onResponseSuccess(UserInfoEntity result) {
            // 保存用户信息
            saveUserInfo(mContext, result);
            //
            if (mCallback != null) {
                mCallback.onUserInfoSuccess(result);
            }
        }

        @Override
        protected void onResponseFail(Context context, String errorCode, String errorMsg) {
            super.onResponseFail(context, errorCode, errorMsg);
            if (mCallback != null) {
                mCallback.onUserInfoFail(errorCode, errorMsg);
            }
        }

        @Override
        protected void onResponseError(Context context, String errorCode, String errorMsg) {
            super.onResponseError(context, errorCode, errorMsg);
            if (mCallback != null) {
                mCallback.onUserInfoFail(errorCode, errorMsg);
            }
        }
    }


    /**
     * 用户信息保存
     */
    public static void saveUserInfo(Context context, UserInfoEntity entity) {
        if (entity == null) {
            return;
        }
        // 登录和用户信息公用的同一个实体，但是用户信息接口不会返回Token，所以Token的存储不在这里做
        // 个人信息不返回用户ID，这里不更新，只在登录接口获取
        User.getInstance().setPhone(context, entity.getLoginPhone());// 用户手机号本地化
        User.getInstance().setHeader(context, entity.getHeaderUrl());// 头像
        User.getInstance().setRealName(context, entity.getRealName());// 真实姓名
        User.getInstance().setUserJob(context, entity.getUserJob());// 用户职位
        User.getInstance().setCompanyId(context, entity.getCompanyId()); // 物业公司ID
    }


    public interface UserInfoCallback {
        void onUserInfoSuccess(UserInfoEntity result);

        void onUserInfoFail(String code, String msg);
    }

}
