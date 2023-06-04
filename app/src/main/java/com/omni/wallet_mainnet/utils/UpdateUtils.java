package com.omni.wallet_mainnet.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.omni.wallet_mainnet.baselibrary.base.BaseActivity;
import com.omni.wallet_mainnet.baselibrary.http.HttpUtils;
import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet_mainnet.baselibrary.utils.AppInstallUtils;
import com.omni.wallet_mainnet.baselibrary.utils.AppUtils;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.entity.UpdateEntity;
import com.omni.wallet_mainnet.framelibrary.common.Constants;
import com.omni.wallet_mainnet.framelibrary.http.callback.DefaultHttpCallback;
import com.omni.wallet_mainnet.framelibrary.utils.AppStorageUtils;
import com.omni.wallet_mainnet.framelibrary.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * 汉: 版本更新工具类
 * En: UpdateUtils
 * author: guoyalei
 * date: 2023/3/22
 */
public class UpdateUtils {
    private static final String TAG = UpdateUtils.class.getSimpleName();
    private Context mContext;
    // 更新信息的实体(Entities that update information)
    private UpdateEntity mUpdateEntity;
    // Apk文件保存路径(Apk file save path)
    private String mAPKFileDir;
    // 通知相关(Notification related)
    private NotificationCompat.Builder mBuilder;
    // 通知相关(Notification related)
    private NotificationManager mNotificationManager;
    // 通知相关(Notification related)
    private Notification mNotification;
    // 通知栏更新通知相关(Notification Bar Update Notification Related)
    private long beforeTime = System.currentTimeMillis();
    // 通知ID(Notification ID)
    private static final int NOTIFICATION_ID = 1;

    public UpdateUtils(Context context) {
        this.mContext = context;
        this.mAPKFileDir = AppStorageUtils.getApkDownLoadDir(mContext);
        // 创建通知的Channel，只需创建一遍即可(To create a notification channel, you only need to create it once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = Constants.CHANNEL_NORMAL_DESC;// 渠道描述，告知用户该类型通知是什么(Channel description, which tells the user what this type of notification is)
            int importance = NotificationManager.IMPORTANCE_LOW;// 通知的等级：低(Notification level: Low)
            AppUtils.createNotificationChannel(mContext, Constants.CHANNEL_NORMAL, channelName, importance);
        }
    }

    /**
     * 更新到新版本
     */
    public void updateNewVersion(UpdateEntity entity) {
        if (mContext instanceof BaseActivity && ((BaseActivity) mContext).mBinder != null) {
            this.mUpdateEntity = entity;
            if (mUpdateEntity == null) {
                mUpdateEntity = new UpdateEntity();
            }
            // 触发更新操作(Trigger update operation)
            makeUpdate();
        } else {
            LogUtils.e(TAG, "非Activity触发更新");
        }
    }

    /**
     * 触发更新操作
     * Trigger update operation
     */
    private void makeUpdate() {
        // 如果本地存在APK文件，则直接安装(If an APK file exists locally, install it directly)
        if (checkFileExists()) {
            installAPK();
            return;
        }
        // 显示通知栏提示(Show notification bar tips)
        showDownLoadNotification();
        // 下载APK文件(Download APK file)
        downloadAPKFile();
    }

    /**
     * 检查文件是否存在
     * Check if the file exists
     */
    private boolean checkFileExists() {
        String fileName = getApkFileNameByUrl(mUpdateEntity.getUrl());
        File localApkFile = new File(mAPKFileDir, fileName);
        long apkSize = PreferencesUtils.getUpdateAPKSizeFromLocal(mContext);
        return localApkFile.exists() && localApkFile.length() == apkSize;
    }

    /**
     * 根据Url获取APK文件名
     * Obtain the APK file name from the Url
     */
    private String getApkFileNameByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String[] temp = url.split("/");
        String fileName = temp[temp.length - 1];
        if (!StringUtils.isEmpty(fileName) && fileName.contains(".apk") && !fileName.contains(mUpdateEntity.getVersion())) {
            fileName = fileName.replace(".apk", "_" + mUpdateEntity.getVersion() + ".apk");
        }
        return fileName;
    }

    /**
     * 安装APK文件
     * Install APK files
     */
    private void installAPK() {
        //安装应用的逻辑(Logic for installing applications)
        AppInstallUtils.getInstance().installAPKFile(mContext, getLocalApkFile(), Constants.FILE_CONTENT_FILE_PROVIDER);
    }

    /**
     * 获取本地APK文件
     * Get local APK file
     */
    private File getLocalApkFile() {
        return new File(mAPKFileDir, getApkFileNameByUrl(mUpdateEntity.getUrl()));
    }

    /**
     * 下载任务发送到通知栏
     * Download tasks and send them to the notification bar
     */
    private void showDownLoadNotification() {
//        ToastUtils.showLongToast(mContext, "You have switched to background downloading, and the download progress can be viewed in the notification bar.");
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 安卓8.0发送等级为HIGH的通知默认会震动，控制8。0以上控制发送通知的时候不震动(Android 8.0 sends notifications with a level of HIGH that vibrate by default, controlling 8. The control above 0 does not vibrate when sending notifications)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_NORMAL,
                    Constants.CHANNEL_NORMAL_DESC, NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);// 设置通知出现时不震动(Set not to vibrate when notifications appear)
            channel.enableLights(false);// 无灯光(No light)
            mNotificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(mContext, Constants.CHANNEL_NORMAL);
        } else {
            mBuilder = new NotificationCompat.Builder(mContext);
        }
        mBuilder.setOnlyAlertOnce(true);// 8.0之后有bug，会一直响，这里设置通知如果设置为响铃或者震动，只提醒一次("After 8.0, there are bugs that will continue to ring. If the notification is set to ring or vibrate, it will only be prompted once.)
        mBuilder.setContentTitle(AppUtils.getAppName(mContext) + " Version Update") //设置通知标题(Set alert title)
                .setSmallIcon(com.omni.wallet_mainnet.framelibrary.R.drawable.ic_launcher) //设置通知的小图标(Set small icons for notifications)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), com.omni.wallet_mainnet.framelibrary.R.drawable.ic_launcher)) //设置通知的大图标(Set large icons for notifications)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS) //设置通知的提醒方式： 只灯光(Set the reminder method for notifications: only lights)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //设置通知的优先级：默认(Set the priority of notifications: Default)
                .setAutoCancel(false)//设置通知被点击一次是否自动取消(Set whether the notification will be automatically canceled after being clicked once)
                .setContentText("Download Progress:" + "0%")
                .setProgress(100, 0, false);
        mNotification = mBuilder.build();//构建通知对象(Building notification objects)
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT;// 放置在"正在运行"栏目中(Place in the "Running" column)
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * 下载APK文件
     * Download APK file
     */
    private void downloadAPKFile() {
        String downLoadAPKUrl = mUpdateEntity.getUrl();
        if (StringUtils.isEmpty(downLoadAPKUrl) || !downLoadAPKUrl.startsWith("http")) {
            LogUtils.e(TAG, "===========下载链接为空或者不规范==========》" + downLoadAPKUrl);
            // 取消通知(Cancel notification)
            mNotificationManager.cancel(NOTIFICATION_ID);
            // 重新触发更新操作(Retrigger the update operation)
            makeUpdate();
            return;
        }
        HttpUtils.with(mContext)
                .url(downLoadAPKUrl)
                .download()
                .savePath(mAPKFileDir)
                .fileName(getApkFileNameByUrl(downLoadAPKUrl))
                .execute(new MyDownLoadCallBack());
    }

    /**
     * 文件下载的监听
     * Listening for file downloads
     */
    private class MyDownLoadCallBack extends DefaultHttpCallback<Object> {
        @Override
        public void onFileSuccess() {
            super.onFileSuccess();
            ToastUtils.showToast(mContext, "Download complete");
            mNotificationManager.cancel(NOTIFICATION_ID);
            // 安装APK(Install APK)
            installAPK();
        }

        @Override
        protected void onProgress(Context context, int progress) {
            super.onProgress(context, progress);
            updateNotificationProgress(progress);
        }

        @Override
        protected void onResponseError(Context context, String errorCode, String errorMsg) {
            super.onResponseError(context, errorCode, errorMsg);
            LogUtils.e(TAG, "=====下载失败====>" + "||errorCode:" + errorCode + "||errorMsg:" + errorMsg);
            // 删除已经下载的APK文件(Delete downloaded APK files)
            boolean result = getLocalApkFile().delete();
            LogUtils.e(TAG, "删除已经下载的APK文件" + (result ? "成功" : "失败"));
            // 取消通知(Cancel notification)
            mNotificationManager.cancel(NOTIFICATION_ID);
            // 重新触发更新操作(Retrigger the update operation)
            makeUpdate();
        }
    }

    /**
     * 更新通知栏进度
     */
    private void updateNotificationProgress(int progress) {
        // TODO 1秒更新2次进度,非常重要,否则如果频繁发送通知系统会慢慢卡死(It is very important to update the progress twice a second, otherwise the system will slowly get stuck if frequent notifications are sent)
        if (System.currentTimeMillis() - beforeTime > 500) {
            mBuilder.setProgress(100, progress, false);
            mBuilder.setContentText("Download Progress:" + progress + "%");
            mNotification = mBuilder.build();
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
            beforeTime = System.currentTimeMillis();
        }
    }

    public interface CheckUpdateCallback {

        void onCheckSuccess(UpdateEntity entity);

        void onCheckFail(String code, String message);
    }

    public void checkUpdate(final CheckUpdateCallback callback) {
        HttpUtils.with(mContext)
                .get()
                .url("https://omnilaboratory.github.io/OBAndroid/app/src/main/assets/newVersion.json")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        LogUtils.e(TAG, "newVersionError:" + errorMsg);
                        if (callback != null) {
                            callback.onCheckFail(errorCode, errorMsg);
                        }
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------newVersion---------------------" + result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    UpdateEntity entity = new UpdateEntity();
                                    entity.setUrl(jsonObject.getString("url"));
                                    entity.setVersion(jsonObject.getString("version"));
                                    if (callback != null) {
                                        callback.onCheckSuccess(entity);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
    }
}
