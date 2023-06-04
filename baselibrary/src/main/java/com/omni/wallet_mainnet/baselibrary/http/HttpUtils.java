package com.omni.wallet_mainnet.baselibrary.http;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.omni.wallet_mainnet.baselibrary.common.Constants;
import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.callback.HttpActivityLifecycleCallback;
import com.omni.wallet_mainnet.baselibrary.http.engine.IHttpEngine;
import com.omni.wallet_mainnet.baselibrary.http.engine.OkHttpEngine;
import com.omni.wallet_mainnet.baselibrary.utils.NetWorkHelper;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求Util类，最好使用链式调用，方便以后扩展
 */

public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();
    // 设置默认的网络引擎
    private static IHttpEngine mHttpEngine = new OkHttpEngine();


    /**
     * 网络引擎初始化方法，在这里可以配置我们使用的网络引擎
     *
     * @param httpEngine
     */
    public static void init(Application application, IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
        // 注册一个全局的Activity的生命周期回调
        application.registerActivityLifecycleCallbacks(new HttpActivityLifecycleCallback());
    }

    /**
     * 切换网咯引擎，可以在每次做网络请求的时候切换
     *
     * @param httpEngine
     */
    public HttpUtils exchangeEngine(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
        return this;
    }

    /**************
     * 下面为设置链式调用的一些方法
     **************/
    private Context mContext;
    // 是否允许使用代理或VPN上网
    private static boolean mAllowWifiProxy = false;
    // 请求地址
    private String mUrl;
    // 请求的TAG
    private Class<?> mTag;
    // 是否支持Https
    private boolean mIsHttps = true;
    //请求方式
    private int mType = TYPE_GET;
    // 请求参数
    private Map<String, Object> mParams;
    // String类型的请求体参数
    private String mContent;
    // 是否缓存
    private boolean mCache = false;
    // 是否检测网络状态(默认检查)
    private boolean mCheckNetState = true;
    // 无网络时的提示语
    private String mNoNetworkTips = "";
    // 下载文件的保存路径
    private String mSavePath;
    // 下载文件的文件名
    private String mFileName;
    // 添加的请求头
    private Map<String, String> mHeader;

    private static final int TYPE_GET = 0x0011;
    private static final int TYPE_POST = 0x0022;
    private static final int TYPE_POST_STRING = 0x0033;
    private static final int TYPE_LOAD_FILE = 0x0044;
    private static final int TYPE_UPLOAD_FILE = 0x0055;
    private static final int TYPE_POST_BYTE = 0x0066;
//    private static final int TYPE_CONNECT = 0x0077;

    // 私有构造
    private HttpUtils(Context context) {
        this.mContext = context;
        setTag(context);
        mParams = new HashMap<>();
        mHeader = new HashMap<>();
    }

    //设置上下文，并初始化HttpUtils对象
    public static HttpUtils with(Context context) {
        return new HttpUtils(context);
    }

    // 设置Url
    public HttpUtils url(String url) {
        mUrl = url;
        return this;
    }

    // 设置TAG
    public HttpUtils setTag(Object object) {
        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            this.mTag = activity.getClass();
        }
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            this.mTag = fragment.getClass();
        }
        if (object instanceof Application) {
            Application application = (Application) object;
            this.mTag = application.getClass();
        }
        return this;
    }

    // 是否允许代理上网
    public static void allowWifiProxy(boolean state) {
        mAllowWifiProxy = state;
    }

    // 设置是否Https请求
    public HttpUtils isHttps(boolean isHttps) {
        mIsHttps = isHttps;
        return this;
    }

    // 设置是get调用
    public HttpUtils get() {
        mType = TYPE_GET;
        return this;
    }

    // 设置是post调用
    public HttpUtils post() {
        mType = TYPE_POST;
        return this;
    }

    // 设置是post调用
    public HttpUtils postByte() {
        mType = TYPE_POST_BYTE;
        return this;
    }

    // 设置是post调用
    public HttpUtils postString() {
        mType = TYPE_POST_STRING;
        return this;
    }

    // 下载文件
    public HttpUtils download() {
        mType = TYPE_LOAD_FILE;
        return this;
    }

    // 上传文件
    public HttpUtils upload() {
        mType = TYPE_UPLOAD_FILE;
        return this;
    }

//    // 上传文件
//    public HttpUtils connect() {
//        mType = TYPE_CONNECT;
//        return this;
//    }

    // 添加参数
    public HttpUtils addParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    // 添加参数
    public HttpUtils addParams(Map<String, Object> values) {
        if (values != null) {
            mParams.putAll(values);
        }
        return this;
    }

    // 添加String类型的参数
    public HttpUtils addContent(String content) {
        mContent = content;
        return this;
    }

    // 添加请求头
    public HttpUtils addHeader(Map<String, String> header) {
        if (header != null) {
            mHeader.putAll(header);
        }
        return this;
    }

    // 添加请求头
    public HttpUtils addHeader(String key, String header) {
        mHeader.put(key, header);
        return this;
    }

    // 下载文件的保存路径
    public HttpUtils savePath(String savePath) {
        this.mSavePath = savePath;
        return this;
    }

    // 下载文件的文件名字
    public HttpUtils fileName(String fileName) {
        this.mFileName = fileName;
        return this;
    }

    // 是否配置缓存
    public HttpUtils cache(boolean cache) {
        this.mCache = cache;
        return this;
    }

    // 是否检测网络状况
    public HttpUtils checkNetState(boolean checked) {
        this.mCheckNetState = checked;
        return this;
    }

    // 无网络时的提示语
    public HttpUtils noNetworkTips(String noNetworkTips) {
        this.mNoNetworkTips = noNetworkTips;
        return this;
    }


    // 执行
    public void execute(EngineCallback callBack) {
        // 判断callBack
        if (callBack == null) {
            callBack = EngineCallback.DEFAULT_CALLBACK;
        }
        // 网络状态检查
        if (mCheckNetState) {
            if (!NetWorkHelper.checkNetState(mContext)) {
                // 无网络的时候统一返回错误码0，如果指定了无网络的提示语，那么错误码为空，
                // 提示语做为错误信息（errorMsg）回传
                if (StringUtils.isEmpty(mNoNetworkTips)) {
                    callBack.onError(mContext, Constants.CODE_NETWORK_CONNECTIONLESS, "");
                } else {
                    callBack.onError(mContext, "", mNoNetworkTips);
                }
                return;
            }
        }
        // 判断是否代理或者VPN上网
        if (!mAllowWifiProxy && (NetWorkHelper.isVpnUsed() || NetWorkHelper.isWifiProxy(mContext))) {
            callBack.onError(mContext, "", "当前网络可能存在网络劫持");
            return;
        }
        // 由于每次网络请求的执行都会执行这个方法，但是这里是不会涉及业务逻辑的
        // 所以在这里添加公有请求参数是行不通的，frameLibrary是处理业务逻辑的module
        // 这里我们可以使用callBack添加一个onPreExecute方法，在这个方法里添加公有参数
        // 所以在那个library里边创建一个HttpCallBack去实现

        // 调用ponPreExecute方法去拼装公有参数
        callBack.onPreExecute(mContext, mParams);
        // 是否Https方式
        if (mIsHttps) {
            mHttpEngine.addHttpsCertificate();
        }
        // 设置Tag
        mHttpEngine.setTag(mTag);
//        Call call = null;
        //get执行
        if (mType == TYPE_GET) {
            mHttpEngine.get(mContext, mUrl, mParams, mHeader, callBack);
        }
        // post执行
        if (mType == TYPE_POST) {
            mHttpEngine.post(mContext, mUrl, mParams, mHeader, callBack);
        }
        // post返回byte执行
        if (mType == TYPE_POST_BYTE) {
            mHttpEngine.postByte(mContext, mUrl, mParams, callBack);
        }
        // post执行（传递String作为请求体）
        if (mType == TYPE_POST_STRING) {
            mHttpEngine.postString(mContext, mUrl, mContent, callBack);
        }
        // 下载文件
        if (mType == TYPE_LOAD_FILE) {
            mHttpEngine.downLoadFile(mContext, mUrl, mSavePath, mFileName, callBack);
        }
        // 上传文件
        if (mType == TYPE_UPLOAD_FILE) {
            mHttpEngine.uploadFile(mContext, mUrl, mParams, mHeader, callBack);
        }
    }


    // 同步执行
    public String syncExecute(EngineCallback callBack) {
        // 判断callBack
        if (callBack == null) {
            callBack = EngineCallback.DEFAULT_CALLBACK;
        }
        // 网络状态检查
        if (mCheckNetState) {
            if (!NetWorkHelper.checkNetState(mContext)) {
                // 无网络的时候统一返回错误码0，如果指定了无网络的提示语，那么错误码为空，
                // 提示语做为错误信息（errorMsg）回传
                if (StringUtils.isEmpty(mNoNetworkTips)) {
                    callBack.onError(mContext, Constants.CODE_NETWORK_CONNECTIONLESS, "");
                } else {
                    callBack.onError(mContext, "", mNoNetworkTips);
                }
                return null;
            }
        }
        // 由于每次网络请求的执行都会执行这个方法，但是这里是不会涉及业务逻辑的
        // 所以在这里添加公有请求参数是行不通的，frameLibrary是处理业务逻辑的module
        // 这里我们可以使用callBack添加一个onPreExecute方法，在这个方法里添加公有参数
        // 所以在那个library里边创建一个HttpCallBack去实现

        // 调用ponPreExecute方法去拼装公有参数
        callBack.onPreExecute(mContext, mParams);
        String result = null;
        //get执行
        if (mType == TYPE_GET) {
            result = mHttpEngine.syncGet(mContext, mUrl, mParams);
        }
        // post执行
        if (mType == TYPE_POST) {
            result = mHttpEngine.syncPost(mContext, mUrl, mParams);
        }
        return result;
    }


    // 执行
    public void execute() {
        execute(null);
    }

    /**
     * 拼接参数
     */
    public static String jointParams(String url, Map<String, Object> params) {
        if (params == null || params.size() <= 0) {
            return url;
        }
        StringBuffer stringBuffer = new StringBuffer(url);
        if (!url.contains("?")) {
            stringBuffer.append("?");
        } else {
            if (!url.endsWith("?")) {
                stringBuffer.append("&");
            }
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }


    /**
     * 获取泛型的class
     *
     * @param object 这个好像是设置泛型的类（比如：HttpCallBack<T> 这时候要穿HttpCallBack对象）
     * @return Class
     */
    public static Class<?> analysisClassInfo(Object object) {
        Type genType = object.getClass().getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type paramsType = params[0];
        if (paramsType instanceof Class) {
            return (Class<?>) paramsType;
        }
        return Object.class;
    }

    /**
     * 取消请求
     */
    public static void cancelCall(Class<?> tag) {
        if (tag == null) {
            return;
        }
        mHttpEngine.cancelCall(tag);
    }

    /**
     * 取消所有请求
     */
    public void cancelAllCall() {
        // tag传空就可以取消所有请求
        mHttpEngine.cancelCall(null);
    }
}
