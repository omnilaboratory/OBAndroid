package com.omni.wallet_mainnet.baselibrary.entity;

/**
 * Home按键监听的通知实体
 */

public class HomeKeyEvent {
    public static final int ACTION_HOME_KEY_PRESS = 1;//按下Home按键
    public static final int ACTION_HOME_KEY_PRESS_LONG = 2;// 长按Home按键
    private int homeKeyAction;

    public int getHomeKeyAction() {
        return homeKeyAction;
    }

    public void setHomeKeyAction(int homeKeyAction) {
        this.homeKeyAction = homeKeyAction;
    }
}
