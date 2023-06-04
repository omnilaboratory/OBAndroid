/**
 * Copyright 2015 ZhangQu Li
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.omni.wallet_mainnet.baselibrary.http.progress.helper;


import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.progress.body.ProgressRequestBody;
import com.omni.wallet_mainnet.baselibrary.http.progress.body.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 进度回调辅助类
 */
public class ProgressHelper {
    /**
     * 包装OkHttpClient，用于下载文件的回调
     *
     * @param client   待包装的OkHttpClient
     * @param callBack 进度回调接口
     * @return 包装后的OkHttpClient，使用clone方法返回
     */
    public static OkHttpClient getProgressClient(final Context context, OkHttpClient client, final EngineCallback callBack) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(context, originalResponse, callBack))
                        .build();
            }
        };
        return client.newBuilder()
                .addInterceptor(interceptor)
                .build();
    }

    /**
     * 包装请求体用于上传文件的回调
     *
     * @param requestBody 请求体RequestBody
     * @param callBack    进度回调接口
     * @return 包装后的进度回调请求体
     */
    public static ProgressRequestBody getProgressRequestBody(Context context, RequestBody requestBody, EngineCallback callBack) {
        //包装请求体
        return new ProgressRequestBody(context, requestBody, callBack);
    }
}
