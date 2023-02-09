package com.omni.testnet.baselibrary.view.webView;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Browser;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.omni.testnet.baselibrary.utils.BasePreferencesUtils;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.utils.ToastUtils;

import java.io.File;


/**
 * WebView下载文件的工具类
 */
class WebViewLoadUtil implements DownloadListener {

    private static final String TAG = WebViewLoadUtil.class.getSimpleName();

    private DownLoadSuccessReceiver receiver;// 文件下载完成监听
    private PackageAddedReceiver paReceiver;// 程序安装完成的监听
    private Context mContext;
    private DownloadManager mDownloadManager;
    private WebView mWebView;
    private String mimeType;
    private String fileName;
    private long mAppId = -1;
    // 是否注册下载完成的监听
    private boolean isRegisterDownloadReceiver;
    // 是否注册应用安装完成的广播接收者
    private boolean isRegisterPackAddReceiver;

    public WebViewLoadUtil(Context context, WebView webView) {
        this.mContext = context.getApplicationContext();
        this.mWebView = webView;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        this.mimeType = mimeType;
        if (mWebView == null) {
            return;
        }
        mWebView.stopLoading();
        // 检测外部存储卡状态
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showToast(mContext, "没有找到SD卡");
            return;
        }
        // 开始下载
        startDownLoad(url, mimeType);
    }

    /**
     * 获取下载的文件的名字
     */
    private String getDownloadedFileName() {
        String fileName = "";
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = mDownloadManager.query(query);
        if (c.moveToFirst()) {
            fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
        }
        return fileName;
    }

    /**
     * 打开下载的文件
     */
    private void openFileFromSDCard(String fileDir, String fileName, String mimeType) {
        if (StringUtils.isEmpty(mimeType)) {
            mimeType = "application/vnd.android.package-archive";
        }
        File file = new File(fileDir, fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        mContext.startActivity(intent);
    }

    /**
     * 下载完成的广播接收者
     */
    private class DownLoadSuccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 匹配下载的文件的ID
            long appId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (appId != -1 && appId == mAppId) {
                // 注册应用安装完成的广播接收者
                registerPackAddedReceiver(context);
                if (!StringUtils.isEmpty(fileName) && fileName.endsWith("apk")) {
                    // 打开并安装该应用
                    openFileFromSDCard(WebViewConfig.getWebViewDownLoadPath(mContext), fileName, mimeType);
                } else {
                    ToastUtils.showToast(mContext, fileName + "下载成功");
                }
            }
            unRegisterDownLoadSuccessReceiver(context);
        }
    }

    /**
     * 注册下载成功的广播接收者
     */
    private void registerDownLoadSuccessReceiver(Context context) {
        receiver = new DownLoadSuccessReceiver();
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        isRegisterDownloadReceiver = true;
    }

    /**
     * 下载成功的广播接收者解除注册
     */
    private void unRegisterDownLoadSuccessReceiver(Context context) {
        if (!isRegisterDownloadReceiver) {
            return;
        }
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        isRegisterDownloadReceiver = false;
    }


    /**
     * 注册应用安装完成的广播接收者
     */
    private void registerPackAddedReceiver(Context context) {
        paReceiver = new PackageAddedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        context.registerReceiver(paReceiver, filter);
        isRegisterPackAddReceiver = true;
    }

    /**
     * 应用安装完成的广播接收者解除注册
     */
    private void unRegisterPackAddedReceiver(Context context) {
        if (!isRegisterPackAddReceiver) {
            return;
        }
        if (paReceiver != null) {
            context.unregisterReceiver(paReceiver);
            paReceiver = null;
        }
        isRegisterPackAddReceiver = false;
    }

    /**
     * 程序安装完成的监听
     */
    private class PackageAddedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getData() != null) {
                String packageName = intent.getData().getEncodedSchemeSpecificPart();
                LogUtils.e(TAG, "安装了:" + packageName + "包名的程序;");
            }
            // 打开APP
            String url = BasePreferencesUtils.getRequestUrlFromLocal(context);
            if (!StringUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "===Exception===本地没有安装此App====>");
                    BasePreferencesUtils.saveRequestUrlToLocal(context, url);
                }
            }
            // 解除注册paReceiver
            unRegisterPackAddedReceiver(context);
        }
    }

    /**
     * 获取下载链接中的APK名称
     */
    private static String getUrlApkName(Uri uri) {
        String apkName;
        String path = uri.getPath();
        int indexOf = path.lastIndexOf("/");
        apkName = path.substring(indexOf + 1, path.length());
        return apkName;
    }

    /**
     * 开始下载
     */
    private void startDownLoad(String url, String mimeType) {
        // 处理下载
        Uri uri = Uri.parse(url);
        // 直接调用系统的下载管理应用下载。使用方法可以查找DownloadManager
        // 获取下载的文件名
        fileName = getUrlApkName(uri);
        // 检查文件如果存在就删除
        File apkFile = new File(WebViewConfig.getWebViewDownLoadPath(mContext), fileName);
        if (!apkFile.exists()) {
            // 弹出Toast
            ToastUtils.showLongToast(mContext, "开始下载：" + WebViewLoadUtil.getUrlApkName(uri));
            if (Build.VERSION.SDK_INT >= 9) {//
                // 注册下载成功的广播接收者
                registerDownLoadSuccessReceiver(mContext);
                // 创建下载请求
                Request request = new Request(uri);
                // 设置MIME类型，这里看服务器配置，一般国家化的都为UTF-8编码。
                request.setMimeType(mimeType);
                // 设置下载的通知的描述
                request.setDescription(fileName);
                // 设置下载通知的标题
                request.setTitle(fileName);
                // 设置下载的文件保存地址
                request.setDestinationInExternalPublicDir(WebViewConfig.getWebViewDownLoadPath(mContext), fileName);
                // 设置下载允许的网络和通知
                if (Build.VERSION.SDK_INT >= 11) {
                    // 设置允许使用的网络类型
                    // 这一步Android2.3做的很好，目前有两种定义分别为NETWORK_MOBILE和NETWORK_WIFI我们可以选择使用移动网络或WIFI方式来下载。
                    request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
                    request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//
                } else {
                    request.setShowRunningNotification(true);
                }
                mAppId = mDownloadManager.enqueue(request);// 存入队列一个新的下载项，返回该应用在队列中的ID
            } else {
                /***************************** 直接调用外部浏览器下载 ***********************************/
                // 如果低版本或者不想使用上边方法，可以直接调用外部浏览器
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, mContext.getPackageName());
                mContext.startActivity(intent);
                /***************************** 直接调用外部浏览器下载 ***********************************/
            }
        } else {
            if (!StringUtils.isEmpty(fileName) && fileName.endsWith("apk")) {
                ToastUtils.showLongToast(mContext, "开始安装" + WebViewLoadUtil.getUrlApkName(uri));
                openFileFromSDCard(WebViewConfig.getWebViewDownLoadPath(mContext), fileName, mimeType);
            }
        }
    }

    public void release() {
        unRegisterDownLoadSuccessReceiver(mContext);
        unRegisterPackAddedReceiver(mContext);
    }
}
