package com.omni.wallet.baselibrary.http.interceptor;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omni.wallet.baselibrary.common.Constants;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 数据加密的拦截器
 */

public abstract class EncryptInterceptor implements Interceptor {
    private static final String TAG = EncryptInterceptor.class.getSimpleName();
    private Gson gson;

    public EncryptInterceptor() {
        gson = new GsonBuilder().serializeNulls().create();
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody oldBody = request.body();
        if (oldBody != null) {
            // 获取请求体中的参数信息
            Buffer buffer = new Buffer();
            oldBody.writeTo(buffer);
            String strOldBody = buffer.readUtf8();
            // 将参数加密，返回密钥和参数的Key-value对
            Map<String, String> newBodyMap = getEncryptBody(strOldBody);
            // 把该Map转成Json并重新构建请求体
            String strNewBody = gson.toJson(newBodyMap);
            // 加密之后把该传参的Post请求改成String的post请求
            MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, strNewBody);
            request = request.newBuilder()
//                    .header("Content-Type", body.contentType().toString())
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .header("Content-Length", String.valueOf(body.contentLength()))
                    .header(Constants.ENCRYPT_REQUEST_HEADER_KEY, "adbs")
                    .method(request.method(), body)
                    .build();
        }
        return chain.proceed(request);
    }

    /**
     * 获取加密之后的请求体和加密的密钥的Map
     */
    protected abstract Map<String, String> getEncryptBody(String requestBody);
}