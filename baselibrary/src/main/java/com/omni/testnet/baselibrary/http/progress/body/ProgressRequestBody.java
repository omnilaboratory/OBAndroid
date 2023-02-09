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
package com.omni.testnet.baselibrary.http.progress.body;


import android.content.Context;
import android.support.annotation.NonNull;

import com.omni.testnet.baselibrary.http.callback.EngineCallback;
import com.omni.testnet.baselibrary.http.progress.entity.Progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 包装的请求体，处理进度
 */
public class ProgressRequestBody extends RequestBody {
    //
    private Context mContext;
    //实际的待包装请求体
    private final RequestBody requestBody;
    //进度回调接口
    private final EngineCallback mCallBack;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;
    // 进度实体
    private Progress mProgress;

    /**
     * 构造函数，赋值
     *
     * @param requestBody 待包装的请求体
     * @param callBack    回调接口
     */
    public ProgressRequestBody(Context context, RequestBody requestBody, EngineCallback callBack) {
        this.mContext = context;
        this.requestBody = requestBody;
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
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写进行写入
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();

    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {

            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;
//            //
//            int progress = 0;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
//                if (contentLength != -1) {
//                    float temp = (float) bytesWritten / contentLength * 100;
//                    progress = new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
//                }
                //回调
                if (mCallBack != null) {
                    mProgress.setComplete(contentLength == 100);
                    mProgress.setContentLength(contentLength);
                    mProgress.setReadLength(bytesWritten);
                    mProgress.setProgress();
                    // 回调回去
                    mCallBack.onProgressInThread(mContext, mProgress);
//                    mCallBack.onProgressInThread(mContext, bytesWritten, contentLength, contentLength == 100);
                }
            }
        };
    }

//    /**
//     * 获取进度
//     */
//    private int getProgress(long readLength, long contentLength) {
//        // 计算当前进度
//        float temp = readLength / contentLength * 100;
//        return new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
//    }
}