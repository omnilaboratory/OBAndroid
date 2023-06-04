package com.omni.wallet_mainnet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.common.Constants;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.framelibrary.R;

import java.util.HashMap;
import java.util.Map;


/**
 * 错误码和错误信息的工具类
 */

public class ErrorCodeTips {
    private static Map<String, Integer> mErrorTipsMap = new HashMap<>();

    static {
        mErrorTipsMap.put(Constants.CODE_NETWORK_CONNECTIONLESS, R.string.error_code_no_network);
        mErrorTipsMap.put(Constants.CODE_ERROR_REQUEST, R.string.error_code_common);
        mErrorTipsMap.put("01", R.string.error_code_01);
        mErrorTipsMap.put("1003", R.string.error_code_1003);
        mErrorTipsMap.put("1005", R.string.error_code_1005);
    }

    public static int getErrorStringRes(String code) {
        if (StringUtils.isEmpty(code)) {
            return -1;
        }
        Integer StringRes = mErrorTipsMap.get(code);
        return StringRes != null ? StringRes : R.string.error_code_common;
    }

    public static String getErrorString(Context context, String code) {
        int resId = getErrorStringRes(code);
        if (resId != -1) {
            return context.getResources().getString(resId);
        }
        return "";
    }

}
