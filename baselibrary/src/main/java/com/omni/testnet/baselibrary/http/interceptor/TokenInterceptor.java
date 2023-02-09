package com.omni.testnet.baselibrary.http.interceptor;

import android.support.annotation.NonNull;

import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp3对于token过期的拦截器
 */

public abstract class TokenInterceptor implements Interceptor {
    private static final String TAG = TokenInterceptor.class.getSimpleName();

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            LogUtils.e(TAG, "=============Token已经过期=============>");
            // 同步请求方式，获取最新的Token
            String newSession = getNewToken();
            Request.Builder builder = chain.request().newBuilder();
            if (!StringUtils.isEmpty(newSession)) {
                // 添加重新获取到的token
                builder = builder.header("Cookie", "token=" + newSession);
//                Request newRequest = chain.request().newBuilder().header("Cookie", "token=" + newSession).build();
            }
            Request newRequest = builder.build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;

    }

    /**
     * 判断Token是否过期
     */
    protected abstract boolean isTokenExpired(Response response);
//    {
//        try {
//            if (response.isSuccessful()) {
//                ResponseBody responseBody = response.peekBody(1024 * 1024);
//                String result = responseBody.string();
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                HttpResponseEntity responseEntity = gson.fromJson(result, HttpResponseEntity.class);
//                if (responseEntity != null) {
//                    final Object status = responseEntity.getStatus();
//                    final Object code = responseEntity.getCode();
//                    if (status == null || "FAIL".equals(status.toString())) {// 接口返回失败
//                        // 根据返回的错误码判断是否返回token过期
//                        String errorCode = code == null ? "" : code.toString();
//                        if ("0004".equals(errorCode)) {
//                            return true;
//                        }
//                    }
//                }
//            } else {
//                final String errorMsg = response.message();
//                final int errorCode = response.code();
//                LogUtils.e(TAG, "=======token过期的拦截器=======>errorMsg：" + errorMsg + "\nerrorCode：" + errorCode);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    /**
     * 获取新的Token
     */
    protected abstract String getNewToken();
}
