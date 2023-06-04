package com.omni.wallet_mainnet.baselibrary.http.engine;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;

import java.util.Map;

/**
 * 封装第三方网络框架的规范接口
 */

public interface IHttpEngine {
    // get请求
    void get(Context context, String url, Map<String, Object> params, Map<String, String> header, EngineCallback callBack);

    // post请求
    void post(Context context, String url, Map<String, Object> params, Map<String, String> header, EngineCallback callBack);

    // get请求
    void getByte(Context context, String url, Map<String, Object> params, EngineCallback callBack);

    // post请求
    void postByte(Context context, String url, Map<String, Object> params, EngineCallback callBack);

    // post请求
    void postString(Context context, String url, String content, EngineCallback callBack);

    // 同步的get请求
    String syncGet(Context context, String url, Map<String, Object> params);

    // 同步的post请求
    String syncPost(Context context, String url, Map<String, Object> params);

    // 文件上传
    void uploadFile(Context context, String url, Map<String, Object> params, Map<String, String> header, EngineCallback callBack);

    // 文件下载
    void downLoadFile(Context context, String url, String savePath, String fileName, EngineCallback callBack);

    // 设置请求头
    void addHeader(Map<String, String> header);

    // https添加证书
    void addHttpsCertificate();

    // 设置请求的Tag
    void setTag(Class<?> tag);

    // 取消请求
    void cancelCall(Object tag);
}
