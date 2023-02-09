package com.omni.testnet.framelibrary.http.interceptor;

import android.content.Context;

import com.google.gson.Gson;
import com.omni.testnet.baselibrary.http.interceptor.TokenInterceptor;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.framelibrary.common.HttpConfig;
import com.omni.testnet.framelibrary.entity.HttpResponseEntity;
import com.omni.testnet.framelibrary.http.gson.GSonUtils;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 带有业务逻辑的token过期拦截器
 */

public class DefaultTokenInterceptor extends TokenInterceptor {
    private static final String TAG = DefaultTokenInterceptor.class.getSimpleName();
    private Context mContext;

    public DefaultTokenInterceptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected boolean isTokenExpired(Response response) {
        try {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.peekBody(1024 * 1024);
                String result = responseBody.string();
                Gson gson = GSonUtils.buildGSon();
                HttpResponseEntity resultEntity = gson.fromJson(result, HttpResponseEntity.class);
                if (resultEntity != null) {
                    final String code = resultEntity.getCode();
                    // 根据返回的错误码判断是否返回token过期
                    if (HttpConfig.SERVICE_CODE_TOKEN_OUT_DATE.equals(String.valueOf(code))) {
                        LogUtils.e(TAG, "拦截器拦截到Token过期");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected String getNewToken() {
        // 重新获取Token
        return null;
    }
}
