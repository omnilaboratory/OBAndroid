package com.omni.testnet.baselibrary.http.interceptor;

import android.support.annotation.NonNull;

import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;

import java.io.IOException;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 日志拦截器
 */

public class LogInterceptor implements Interceptor {
    private static final String TAG = LogInterceptor.class.getSimpleName();

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
//        return interceptMethod(chain);
        return interceptMethod2(chain);
    }

    @NonNull
    private Response interceptMethod(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
//        okhttp3.MediaType mediaType = response.body().contentType();
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        String content = responseBody.string();
//        String content = response.body().string();
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "----------Start----------------");
        LogUtils.e(TAG, "| " + request.toString());
        // 请求头
        Headers headers = request.headers();
        if (headers != null && headers.size() > 0) {
            LogUtils.e(TAG, "| RequestHeader:");
            int headerSize = headers.size();
            for (int i = 0; i < headerSize; i++) {
                LogUtils.e(TAG, "| " + headers.name(i) + ":" + headers.value(i));
            }
        }
        //
        String method = request.method();
        if ("POST".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.e(TAG, "| RequestParams:{" + sb.toString() + "}");
            } else {
                LogUtils.e(TAG, "| RequestParams:" + bodyToString(request));
            }
        }
        LogUtils.e(TAG, "| ResponseCode:" + response.code());
        LogUtils.e(TAG, "| Response:" + content);
        LogUtils.e(TAG, "----------End:" + duration + "毫秒----------");
//        return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();
        return response;
    }


    /**
     * 拦截接口请求信息并打印日志
     * (注意：没有试验在文件上传上是否有问题)
     *
     * @throws IOException
     */
    @NonNull
    private Response interceptMethod2(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "-------------------------------------------------Start---------------------" +
                "--------------------------------------------------------");
        // 打印请求信息
        logRequest(request);
        // 打印返回信息
        Response resp = logResponse(response);
        LogUtils.e(TAG, "-----------------------------------------------End:" + duration + "毫秒--" +
                "--------------------------------------------------------------------");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "\n");
        return resp;
    }

    /**
     * 打印请求信息
     */
    private void logRequest(Request request) {
        // 打印Header
        Headers headers = request.headers();
        if (headers != null && headers.size() > 0) {
            StringBuilder sb = new StringBuilder();
            int headerSize = headers.size();
            for (int i = 0; i < headerSize; i++) {
                sb.append(headers.name(i) + " = " + headers.value(i) + ",");
            }
            sb.delete(sb.length() - 1, sb.length());
            LogUtils.e(TAG, "| RequestMethod：" + request.method());
            LogUtils.e(TAG, "| RequestHeader：{" + sb.toString() + "}");
        }
        //
        String requestUrl = request.url().toString();
        LogUtils.e(TAG, "| RequestUrl：" + requestUrl);
        // 打印请求体
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            MediaType mediaType = requestBody.contentType();
            if (mediaType != null) {
                LogUtils.e(TAG, "| RequestContentType：" + mediaType.toString());
                if (isTextRequest(mediaType)) {
                    LogUtils.e(TAG, "| RequestContentStr：" + bodyToString(request));
                } else if (request.body() instanceof FormBody) {
                    StringBuilder sb = new StringBuilder();
                    FormBody body = (FormBody) request.body();
                    if (body != null && body.size() > 0) {
                        for (int i = 0; i < body.size(); i++) {
                            sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                        LogUtils.e(TAG, "| RequestParams:{" + sb.toString() + "}");
                    } else {
                        LogUtils.e(TAG, "| RequestParams:{ Request Body Is Null }");
                    }
                } else {
                    LogUtils.e(TAG, "| RequestParams: 未知类型参数");
                }
            }
        }
    }

    /**
     * 打印返回的信息
     */
    private Response logResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response cloneResponse = builder.build();
            LogUtils.e(TAG, "| ResponseUrl：" + cloneResponse.request().url());
            LogUtils.e(TAG, "| ResponseCode：" + cloneResponse.code());
            if (!StringUtils.isEmpty(cloneResponse.message())) {
                LogUtils.e(TAG, "| ResponseMessage：" + cloneResponse.message());
            }
            ResponseBody responseBody = cloneResponse.body();
            if (responseBody != null) {
                MediaType mediaType = responseBody.contentType();
                if (mediaType != null) {
                    LogUtils.e(TAG, "| ResponseContentType：" + mediaType.toString());
                    if (isTextRequest(mediaType)) {
                        String responseStr = responseBody.string();
                        LogUtils.e(TAG, "| Response：" + responseStr);
                        responseBody = ResponseBody.create(mediaType, responseStr);
                        return response.newBuilder().body(responseBody).build();
                    } else {
                        LogUtils.e(TAG, "| Response：可能是文件类型，信息过大，忽略打印");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 判断是文本类型的接口请求还是文件类型的
     */
    private boolean isTextRequest(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml"))
                return true;
        }
        return false;
    }

    /**
     * 得到RequestBody中的String信息
     */
    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
            return "something error when show requestBody.";
        }
    }


    @NonNull
    private Response interceptMethod3(Chain chain) throws IOException {
        Request request = chain.request();
        LogUtils.e(TAG, "请求接口:" + request.toString());
        if ("POST".equals(chain.request().method())) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.e(TAG, "POST参数:{" + sb.toString() + "}");
            }
        }
        long t1 = System.nanoTime();
        Response response = chain.proceed(chain.request());
        long t2 = System.nanoTime();
        LogUtils.e(TAG, String.format(Locale.getDefault(), "接口返回 %s ，用时： %.1fms%n",
                response.request().url(), (t2 - t1) / 1e6d));
        LogUtils.i(TAG, "" + response.headers());
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        String content = responseBody.string();
        LogUtils.e(TAG, "接口返回是:" + content);
        return response;
    }
}
