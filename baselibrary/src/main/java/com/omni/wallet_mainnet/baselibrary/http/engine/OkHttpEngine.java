package com.omni.wallet_mainnet.baselibrary.http.engine;

import android.content.Context;
import android.support.annotation.NonNull;

import com.socks.library.KLog;
import com.omni.wallet_mainnet.baselibrary.common.Constants;
import com.omni.wallet_mainnet.baselibrary.http.HttpUtils;
import com.omni.wallet_mainnet.baselibrary.http.SSL.DefaultHostNameVerifier;
import com.omni.wallet_mainnet.baselibrary.http.SSL.TrustAllManager;
import com.omni.wallet_mainnet.baselibrary.http.SSL.TrustAllSSLSocketFactory;
import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.interceptor.HeaderInterceptor;
import com.omni.wallet_mainnet.baselibrary.http.progress.helper.ProgressHelper;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 利用OkHttp封装的网络引擎
 */

public class OkHttpEngine implements IHttpEngine {
    private static final String TAG = OkHttpEngine.class.getSimpleName();
    private static OkHttpClient mOkHttpClient;
    // 请求的TAG（为每个Activity的class）
    private Class<?> mTag;

    static {
        mOkHttpClient = new OkHttpClient.Builder()
                // 默认添加请求头
                .addInterceptor(new HeaderInterceptor())
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)// 连接超时时间
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)// 读取超时时间
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)// 写入超时时间
                .build();
    }

    /**
     * 添加拦截器
     */
    public void addInterceptor(Interceptor interceptor) {
        // 注意在调用newBuilder方法之后需要将得到的OkHttpClient对象赋给原来的对象，否则设置的拦截器无效
        mOkHttpClient = mOkHttpClient.newBuilder().addInterceptor(interceptor).build();
    }


    /**
     * 添加请求头的拦截器
     */
    @Override
    public void addHeader(Map<String, String> header) {
        Interceptor interceptor = new HeaderInterceptor();
        mOkHttpClient = mOkHttpClient.newBuilder().addInterceptor(interceptor).build();
    }

    /**
     * 添加Https证书
     */
    @Override
    public void addHttpsCertificate() {
        mOkHttpClient = mOkHttpClient.newBuilder()
                .sslSocketFactory(new TrustAllSSLSocketFactory().createSSLSocketFactory(), new TrustAllManager())
                .hostnameVerifier(new DefaultHostNameVerifier())
                .build();
    }


    @Override
    public void setTag(Class<?> tag) {
        this.mTag = tag;
    }

    @Override
    public void cancelCall(Object tag) {
        // tag为空就取消所有
        if (tag == null) {
            synchronized (mOkHttpClient.dispatcher().getClass()) {
                for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                    call.cancel();
                }
                for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                    call.cancel();
                }
            }
            return;
        }
        // 否则按照Tag取消
        synchronized (mOkHttpClient.dispatcher().getClass()) {
            for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    LogUtils.e(TAG, "取消队列中的请求：" + call.request().url());
                    call.cancel();
                }
            }
            for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    LogUtils.e(TAG, "取消执行中的请求：" + call.request().url());
                    call.cancel();
                }
            }
        }
    }

    @Override
    public void get(final Context context, String url, final Map<String, Object> params, Map<String, String> header, final EngineCallback callBack) {
        final String requestUrl = HttpUtils.jointParams(url, params);
        //可以省略，默认是GET请求
        Request request = new Request.Builder()
                .url(requestUrl)
                .tag(mTag)
                .build();
        // 添加请求头
        request = addHeader(request, header);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                if (call.isCanceled()) {
                    LogUtils.e(TAG, "==(onFailure)请求被取消==>" + call.request().url());
                    callBack.onCancel(context);
                    return;
                }
                // 日志
                logGetFail(call, e, params);
                // 回调
                callBack.onError(context, Constants.CODE_ERROR_REQUEST, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                if (call.isCanceled()) {
                    LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                    callBack.onCancel(context);
                    return;
                }
                if (response.isSuccessful()) {
                    String result;
                    if (response.cacheResponse() != null) {
                        result = response.cacheResponse().toString();
                    } else {
                        result = response.body().string();
                    }
                    // KLog打印相应数据
                    if (Constants.isShowLog) {
                        KLog.json(TAG, result);
                    }
                    // 回调
                    callBack.onSuccess(context, result);
                } else {
                    final String errorMsg = response.message();
                    final int errorCode = response.code();
                    callBack.onError(context, errorCode + "", errorMsg);
                }
            }
        });
    }

    /**
     * Get Fail 日志
     */
    private void logGetFail(@NonNull Call call, @NonNull IOException e, Map<String, Object> params) {
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "  | ===============================================================start" +
                "================================================================");
        LogUtils.e(TAG, "  | Get请求失败：" + e.getMessage());
        LogUtils.e(TAG, "  | RequestUrl：" + call.request().url());
        LogUtils.e(TAG, "  | RequestParams：" + params.toString());
        LogUtils.e(TAG, "  | ===============================================================end" +
                "================================================================");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "\n");
    }

    /**
     * Request添加请求头
     *
     * @return Request
     */
    public Request addHeader(Request request, Map<String, String> header) {
        if (header == null || header.size() == 0) {
            return request;
        }
        Request.Builder builder = request.newBuilder();
        for (String key : header.keySet()) {
            builder.addHeader(key, header.get(key));
        }
        return builder.build();
    }

    @Override
    public void post(final Context context, final String url, final Map<String, Object> params, Map<String, String> header, final EngineCallback callBack) {
        // 了解 OkHttp
        // 这里使用简单的FormBody便于打印日志
        RequestBody requestBody = appendPostBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(mTag)
                .post(requestBody)
                .build();
        // 添加请求头
        request = addHeader(request, header);
        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "==(onFailure)请求被取消==>" + call.request().url());
                            callBack.onCancel(context);
                            return;
                        }
                        // 日志
                        logPostFail(call, e, params);
                        // 回调
                        callBack.onError(context, Constants.CODE_ERROR_REQUEST, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (response.isSuccessful()) {
                            // 转成byteArray回调（发明很早。string()方法上回调，不然流就被关闭了）
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                try {
                                    final String result = responseBody.string();
                                    if (Constants.isShowLog) {
                                        KLog.json(TAG, result);
                                    }
                                    // 转成String回调
                                    callBack.onSuccess(context, result);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    callBack.onError(context, "-1", e.getMessage());
                                }
                            }
                        } else {
                            final int errorCode = response.code();
                            final String errorMsg = response.message();
                            callBack.onError(context, errorCode + "", errorMsg);
                        }
                    }
                }
        );
    }

    /**
     * Post Fail日志
     */
    private void logPostFail(@NonNull Call call, @NonNull IOException e, Map<String, Object> params) {
        LogUtils.e(TAG, "\n");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "  | =======================================================start" +
                "================================================================");
        LogUtils.e(TAG, "  | Post请求失败：" + e.getMessage());
        LogUtils.e(TAG, "  | RequestUrl：" + call.request().url());
        LogUtils.e(TAG, "  | RequestParams：" + params.toString());
        LogUtils.e(TAG, "  | =======================================================end" +
                "================================================================");
        LogUtils.e(TAG, "*\n");
        LogUtils.e(TAG, "\n");
    }


    @Override
    public void getByte(Context context, String url, Map<String, Object> params, EngineCallback callBack) {

    }

    @Override
    public void postByte(final Context context, String url, Map<String, Object> params, final EngineCallback callBack) {
        // 了解 OkHttp
        // 这里使用简单的FormBody便于打印日志
        RequestBody requestBody = appendPostBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(mTag)
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onFailure)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        final String errorMsg = e.getMessage();
                        LogUtils.e(TAG, "Post请求失败：" + errorMsg);
                        callBack.onError(context, Constants.CODE_ERROR_REQUEST, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (response.isSuccessful()) {
                            // 转成byteArray回调（发明很早。string()方法上回调，不然流就被关闭了）
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                byte[] result = responseBody.bytes();
                                if (Constants.isShowLog) {
                                    KLog.json(TAG, new String(result));
                                }
                                // 转成String回调
                                callBack.onSuccess(context, result);
                            }
                        } else {
                            final int errorCode = response.code();
                            final String errorMsg = response.message();
                            callBack.onError(context, errorCode + "", errorMsg);
                        }
                    }
                }
        );
    }

    @Override
    public void postString(final Context context, String url, String content, final EngineCallback callBack) {
        // 将作为参数的JSON放到requestBody里
        // 提交一个GSon字符串到服务器端，注意：传递JSON的时候，不要通过addHeader去设置contentType，
        // 而使用.mediaType(MediaType.parse("application/json; charset=utf-8"))。
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .url(url)
                .tag(mTag)
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onFailure)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        final String errorMsg = e.getMessage();
                        LogUtils.e(TAG, "Post请求失败：" + errorMsg);
                        callBack.onError(context, Constants.CODE_ERROR_REQUEST, e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (response.isSuccessful()) {
                            final String result = response.body().string();
                            if (Constants.isShowLog) {
                                KLog.json(TAG, result);
                            }
                            callBack.onSuccess(context, result);
                        } else {
                            final int errorCode = response.code();
                            final String errorMsg = response.message();
                            callBack.onError(context, errorCode + "", errorMsg);
                        }
                    }
                }
        );
    }

    /**
     * 文件上传
     *
     * @param params 文件直接放到Map中，会自动解析格式
     */

    @Override
    public void uploadFile(final Context context, String url, Map<String, Object> params, Map<String, String> header, final EngineCallback callBack) {
        RequestBody requestBody = appendBody(params);
        Request request = new Request.Builder()
                .url(url)
                .post(ProgressHelper.getProgressRequestBody(context, requestBody, callBack))
                .build();
        // 添加请求头
        request = addHeader(request, header);
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onFailure)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (callBack != null) {
                            String errorMsg = e.getCause() + e.getMessage();
                            callBack.onError(context, Constants.CODE_ERROR_REQUEST, errorMsg);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (callBack != null) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                String responseStr = responseBody.string();
                                if (response.isSuccessful()) {
                                    callBack.onSuccess(context, responseStr);
                                }
                                callBack.onFileSuccess(context, responseStr);
                            }
                        }
                    }
                });

    }

    /**
     * 文件下载，自动保存
     */
    @Override
    public void downLoadFile(final Context context, String url, final String savePath,
                             final String fileName, final EngineCallback callBack) {
        ProgressHelper.getProgressClient(context, mOkHttpClient, callBack)
                .newCall(new Request.Builder().url(url).tag(url).build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onFailure)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (callBack != null) {
                            String errorMsg = e.getCause() + e.getMessage();
                            callBack.onError(context, Constants.CODE_ERROR_REQUEST, errorMsg);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (call.isCanceled()) {
                            LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                            callBack.onCancel(context);
                            return;
                        }
                        if (!response.isSuccessful()) {
                            if (callBack != null) {
                                callBack.onError(context, Constants.CODE_ERROR_REQUEST, response.message());
                            }
                            return;
                        }
                        try {
                            // 保存文件到本地
                            boolean result = saveDownloadFile(response, savePath, fileName);
                            if (result) {
                                if (callBack != null) {
                                    callBack.onFileSuccess(context, savePath + File.separator + fileName);
                                }
                            } else {
                                if (callBack != null) {
                                    callBack.onError(context, "", "文件保存失败");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtils.e(TAG, "文件保存异常：\ncause:" + e.getCause() + "\n||message:" + e.getMessage());
                            if (callBack != null) {
                                callBack.onError(context, "", "文件保存失败");
                            }
                        }
                    }
                });
    }

    /**
     * 同步Get方法
     */
    @Override
    public String syncGet(final Context context, String url, Map<String, Object> params) {
        final String requestUrl = HttpUtils.jointParams(url, params);
        //可以省略，默认是GET请求
        Request request = new Request.Builder()
                .url(requestUrl)
                .tag(mTag)
                .build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
            if (call.isCanceled()) {
                LogUtils.e(TAG, "=========(onResponse)请求被取消=========>");
                return null;
            }
            if (response.isSuccessful()) {
                String result;
                if (response.cacheResponse() != null) {
                    result = response.cacheResponse().toString();
                } else {
                    result = response.body().string();
                }
                if (Constants.isShowLog) {
                    KLog.json(TAG, result);
                }
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String syncPost(final Context context, final String url, Map<String, Object> params) {
        // 了解 OkHttp
        // 这里使用简单的FormBody便于打印日志
        RequestBody requestBody = appendPostBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(mTag)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            // 由于请求会在页面destroy的时候统一取消，所以需要在这里进行判断，避免回调的时候出现空指针
            if (call.isCanceled()) {
                LogUtils.e(TAG, "=========请求被取消=========>");
                return null;
            }
            if (response.isSuccessful()) {
                final String result = response.body().string();
                if (Constants.isShowLog) {
                    KLog.json(TAG, result);
                }
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 组装post请求参数body
     * 其中会根据Map集合中value的类型判断是不是文件上传
     */
    private RequestBody appendBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    /**
     * 添加参数
     */
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
//                builder.addFormDataPart(key, params.get(key) + "");
                Object value = params.get(key);
                if (value instanceof File) {
                    // 处理文件 --> Object File
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(guessMimeType(file
                                    .getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 代表提交的是 List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            // 获取文件
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(guessMimeType(file
                                            .getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.addFormDataPart(key, String.valueOf(value));
                }
            }
        }
    }

    /**
     * 添加参数
     */
    private RequestBody appendPostBody(Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key) + "");
            }
        }
        return builder.build();
    }

    /**
     * 猜测文件类型
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 保存下载的文件到本地
     */
    private boolean saveDownloadFile(Response response, String savePath, String fileName) throws IOException {
        File tempFile = new File(savePath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        File downLoadFile = new File(savePath, fileName);
        if (downLoadFile.exists()) {
            downLoadFile.delete();
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID().toString();
        }
        InputStream inputStream = getInputStreamFromResponse(response);
        if (inputStream == null) {
            return false;
        }
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        FileOutputStream fos = new FileOutputStream(savePath + File.separator + fileName);
        byte[] data = new byte[10 * 1024];
        int len;
        while ((len = bis.read(data)) != -1) {
            fos.write(data, 0, len);
        }
        LogUtils.e(TAG, "文件" + savePath + "/" + fileName + "保存成功");
        fos.flush();
        fos.close();
        bis.close();
        return true;
    }

    /**
     * 根据响应获得输入流
     */
    private InputStream getInputStreamFromResponse(Response response) throws IOException {
        if (response != null && response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.byteStream();
            }
        }
        return null;
    }
}
