package com.omni.wallet.baselibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.omni.wallet.baselibrary.entity.HomeKeyEvent;
import com.omni.wallet.baselibrary.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Home按键的监听
 */

public class HomeKeyEventReceiver extends BroadcastReceiver {
    private static final String TAG = HomeKeyEventReceiver.class.getSimpleName();
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "homekey";
    private static final String SYSTEM_HOME_KEY_LONG = "recentapps";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            HomeKeyEvent event = new HomeKeyEvent();
            String reason = intent.getStringExtra(SYSTEM_REASON);
            if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                //表示按了home键,程序到了后台
                event.setHomeKeyAction(HomeKeyEvent.ACTION_HOME_KEY_PRESS);
                LogUtils.e(TAG, "=============按下Home按键================》");
            } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                LogUtils.e(TAG, "=============长按Home按键================》");
                //表示长按home键,显示最近使用的程序列表
                event.setHomeKeyAction(HomeKeyEvent.ACTION_HOME_KEY_PRESS_LONG);
            }
            // 通知发送出去
            EventBus.getDefault().post(event);
        }
    }
}
