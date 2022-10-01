package com.omni.wallet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.framelibrary.entity.event.LoginStateEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 退出登录的工具类
 */

public class LogoutUtils {
    public void logout(Context context, LogoutCallback callback) {
        if (callback != null) {
            callback.onLogout(context);
        }
        // 清空用户登录信息
        User.getInstance().clearUserLoginInfo(context);
        // 发通知
        EventBus.getDefault().post(new LoginStateEvent(false));
    }

    /**
     * 退出应用
     */
    public void exitApplication(Context context) {
        // 清空内存中的User实体
        User.getInstance().clear();
    }

    public interface LogoutCallback {
        void onLogout(Context context);
    }
}
