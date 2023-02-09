package com.omni.testnet.baselibrary.http.interceptor;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 配置请求头的拦截器
 */

public class HeaderInterceptor implements Interceptor {
    // 包含请求头的Map
    private Map<String, String> mHeader;

    public HeaderInterceptor() {
    }

//    public HeaderInterceptor(Map<String, String> mHeader) {
//        this.mHeader = mHeader;
//    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Content-Type", "application/json; charset=UTF-8");
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here");
//        if (mHeader != null) {
//            for (String key : mHeader.keySet()) {
//                // 移除已经存在的Header
//                builder.removeHeader(key);
//                // 添加
//                builder.addHeader(key, mHeader.get(key));
//            }
//        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}
