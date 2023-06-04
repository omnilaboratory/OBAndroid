package com.omni.wallet_mainnet.utils;

/**
 * 汉: 防止连续点击工具类
 * En: PreventContinuousClicksUtil
 * author: guoyalei
 * date: 2023/3/28
 */
public class PreventContinuousClicksUtil {
    public static final int DELAY = 1000;
    private static long lastClickTime = 0;

    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        } else {
            return false;
        }
    }
}
