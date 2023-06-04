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
package com.omni.wallet_mainnet.baselibrary.http.progress.body;


import android.content.Context;
import android.support.annotation.NonNull;

import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.progress.entity.Progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


/**
 * 包装的响体，处理进度
 */
public class ProgressResponseBody extends ResponseBody {
    private static final String TAG = ProgressResponseBody.class.getSimpleName();

    private Context mContext;
    // 响应对象
    private final Response mResponse;
    //实际的待包装响应体
    private final ResponseBody mResponseBody;
    //进度回调接口
    private final EngineCallback mCallBack;
    //包装完成的BufferedSource
    private BufferedSource mBufferedSource;
    // 进度实体
    private Progress mProgress;

    /**
     * 构造函数，赋值
     *
     * @param response 待包装的响应体
     * @param callBack 回调接口
     */
    public ProgressResponseBody(Context context, Response response, EngineCallback callBack) {
        this.mContext = context;
        this.mResponse = response;
        this.mResponseBody = response.body();
        this.mCallBack = callBack;
        mProgress = new Progress();
    }


    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     */
    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     */
    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            //包装
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;
            long contentLength = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                if (!mResponse.isSuccessful()) {
                    return bytesRead;
                }
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = mResponseBody.contentLength();
                }
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //注意：如果contentLength()不知道长度，会返回-1
                if (mCallBack != null) {
                    mProgress.setComplete(bytesRead == -1);
                    mProgress.setContentLength(contentLength);
                    mProgress.setReadLength(totalBytesRead);
                    mProgress.setProgress();
                    // 回调回去
                    mCallBack.onProgressInThread(mContext, mProgress);
//                    mCallBack.onProgressInThread(mContext, totalBytesRead, contentLength, bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}