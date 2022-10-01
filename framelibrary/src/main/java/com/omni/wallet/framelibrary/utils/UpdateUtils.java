package com.omni.wallet.framelibrary.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omni.wallet.baselibrary.base.BaseActivity;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.utils.AppInstallUtils;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.R;
import com.omni.wallet.framelibrary.common.Constants;
import com.omni.wallet.framelibrary.entity.UpdateEntity;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.framelibrary.http.HttpRequestUtils;
import com.omni.wallet.framelibrary.http.callback.DefaultHttpCallback;

import java.io.File;

/**
 * 版本更新工具类
 */

public class UpdateUtils {
    private static final String TAG = UpdateUtils.class.getSimpleName();
    private Context mContext;
    // 更新信息的实体
    private UpdateEntity mUpdateEntity;
    // 版本更新的Dialog
    private AlertDialog mUpdateDialog;
    // 下载APK的Dialog
    private AlertDialog mDownLoadDialog;
    // Apk文件保存路径
    private String mAPKFileDir;
    // 通知相关
    private NotificationCompat.Builder mBuilder;
    // 通知相关
    private NotificationManager mNotificationManager;
    // 通知相关
    private Notification mNotification;
    // 通知栏更新通知相关
    private long beforeTime = System.currentTimeMillis();
    // 通知ID
    private static final int NOTIFICATION_ID = 1;

    public UpdateUtils(Context context) {
        this.mContext = context;
        this.mAPKFileDir = AppStorageUtils.getApkDownLoadDir(mContext);
        // 创建通知的Channel，只需创建一遍即可
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = Constants.CHANNEL_NORMAL_DESC;// 渠道描述，告知用户该类型通知是什么
            int importance = NotificationManager.IMPORTANCE_LOW;// 通知的等级：低
            AppUtils.createNotificationChannel(mContext, Constants.CHANNEL_NORMAL, channelName, importance);
        }
    }

    /**
     * 更新到新版本
     */
    public void updateNewVersion(UpdateEntity entity) {
        // Application中注册友盟Token可能触发更新，所以这里需要判断
        if (mContext instanceof BaseActivity && ((BaseActivity) mContext).mBinder != null) {
            if (User.getInstance().isShowUpdateDialog()) {
                LogUtils.e(TAG, "更新对话框当前已经展示");
                return;
            }
            this.mUpdateEntity = entity;
            if (mUpdateEntity == null) {
                mUpdateEntity = new UpdateEntity();
            }
            // 更新全局的显示更新对话框的标志
            User.getInstance().setShowUpdateDialog(true);
            // 显示更新对话框
            showUpdateDialog();
        } else {
            LogUtils.e(TAG, "非Activity触发更新");
        }
    }


    /**
     * 显示版本更新的Dialog
     */
    private void showUpdateDialog() {
        if (mUpdateDialog == null) {
            mUpdateDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_updating)
                    .setOnClickListener(R.id.tv_dialog_update_cancel, new MyClickListener())
                    .setOnClickListener(R.id.tv_dialog_update_confirm, new MyClickListener())
                    .setOnClickListener(R.id.tv_dialog_update_force_btn, new MyClickListener())
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            // 如果不是强制更新的时候更新标志位
                            if (!mUpdateEntity.isForceUpdate()) {
                                User.getInstance().setShowUpdateDialog(false);
                            }
                            // 回调回去
                            if (mCallback != null) {
                                mCallback.onDialogDismiss();
                            }
                        }
                    })
                    .fullWidth()
                    .create();
        }
        mUpdateDialog.setText(R.id.tv_dialog_update_title_version, "V" + mUpdateEntity.getVersion());
        mUpdateDialog.setText(R.id.tv_dialog_update_desc, mUpdateEntity.getDesc());
        // 判断是不是需要强制更新
        if (mUpdateEntity.isForceUpdate()) {
            mUpdateDialog.getViewById(R.id.tv_dialog_update_force_btn).setVisibility(View.VISIBLE);
            mUpdateDialog.getViewById(R.id.view_dialog_update_bottom).setVisibility(View.GONE);
            mUpdateDialog.getViewById(R.id.layout_dialog_update_not_force).setVisibility(View.GONE);
            mUpdateDialog.setCancelable(false);
            mUpdateDialog.setCanceledOnTouchOutside(false);
        } else {
            mUpdateDialog.getViewById(R.id.tv_dialog_update_force_btn).setVisibility(View.GONE);
            mUpdateDialog.getViewById(R.id.view_dialog_update_bottom).setVisibility(View.VISIBLE);
            mUpdateDialog.getViewById(R.id.layout_dialog_update_not_force).setVisibility(View.VISIBLE);
            mUpdateDialog.setCancelable(true);
            mUpdateDialog.setCanceledOnTouchOutside(true);
        }
//        // 如果Apk文件存在，就显示立即安装，否则显示立即体验
//        if (checkFileExists()) {
//            mUpdateDialog.setText(R.id.tv_dialog_update_force_btn, "立即安装");
//            mUpdateDialog.setText(R.id.tv_dialog_update_confirm, "立即安装");
//        } else {
//            mUpdateDialog.setText(R.id.tv_dialog_update_force_btn, "立即体验");
//            mUpdateDialog.setText(R.id.tv_dialog_update_confirm, "立即体验");
//        }
        if (!mUpdateDialog.isShowing()) {
            mUpdateDialog.show();
        }
    }

    /**
     * 更新对话框的点击事件监听
     */
    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_dialog_update_confirm || id == R.id.tv_dialog_update_force_btn) {
                // 如果本地存在APK文件，则直接安装
                if (checkFileExists()) {
                    installAPK();
                    if (!mUpdateEntity.isForceUpdate()) {
                        mUpdateDialog.dismiss();
                    }
                    return;
                }
                // 提示更新的弹窗消失，如果是强制更新，并且本地存在已经下载好的APK文件的时候，弹窗不能消失
                mUpdateDialog.dismiss();
                // 根据是否强制更新判断显示dialog还是通知栏提示
                if (mUpdateEntity.isForceUpdate()) {
                    showDownLoadDialog();
                } else {
                    showDownLoadNotification();
                }
                // 下载APK文件
                downloadAPKFile();
            } else if (id == R.id.tv_dialog_update_cancel) {
                mUpdateDialog.dismiss();
            } else if (id == R.id.tv_dialog_load_cancel) {
                mDownLoadDialog.dismiss();
            }
        }
    }

    /**
     * 检查文件是否存在
     */
    private boolean checkFileExists() {
        String fileName = getApkFileNameByUrl(mUpdateEntity.getUrl());
        File localApkFile = new File(mAPKFileDir, fileName);
        long apkSize = PreferencesUtils.getUpdateAPKSizeFromLocal(mContext);
        return localApkFile.exists() && localApkFile.length() == apkSize;
    }

    /**
     * 根据Url获取APK文件名
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
     */
    private void installAPK() {
        //安装应用的逻辑
        AppInstallUtils.getInstance().installAPKFile(mContext, getLocalApkFile(), Constants.FILE_CONTENT_FILE_PROVIDER);
    }

    /**
     * 获取本地APK文件
     */
    private File getLocalApkFile() {
        return new File(mAPKFileDir, getApkFileNameByUrl(mUpdateEntity.getUrl()));
    }

    /**
     * 显示下载APK的Dialog
     */
    private void showDownLoadDialog() {
        mDownLoadDialog = new AlertDialog.Builder(mContext)
                .setContentView(R.layout.layout_dialog_down_load)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setOnClickListener(R.id.tv_dialog_load_cancel, new MyClickListener())
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // 更新标志位
                        User.getInstance().setShowUpdateDialog(false);
                        // 回调回去
                        if (mCallback != null) {
                            mCallback.onDialogDismiss();
                        }
                    }
                })
                .fullWidth()
                .create();
        if (!mDownLoadDialog.isShowing()) {
            mDownLoadDialog.show();
        }
    }

    /**
     * 下载任务发送到通知栏
     */
    private void showDownLoadNotification() {
        ToastUtils.showLongToast(mContext, "已经切换到后台下载，下载进度可以在通知栏查看");
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 安卓8.0发送等级为HIGH的通知默认会震动，控制8。0以上控制发送通知的时候不震动
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_NORMAL,
                    Constants.CHANNEL_NORMAL_DESC, NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);// 设置通知出现时不震动
            channel.enableLights(false);// 无灯光
            mNotificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(mContext, Constants.CHANNEL_NORMAL);
        } else {
            mBuilder = new NotificationCompat.Builder(mContext);
        }
        mBuilder.setOnlyAlertOnce(true);// 8.0之后有bug，会一直响，这里设置通知如果设置为响铃或者震动，只提醒一次
        mBuilder.setContentTitle(AppUtils.getAppName(mContext) + "版本更新") //设置通知标题
                .setSmallIcon(R.drawable.ic_launcher_small) //设置通知的小图标
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher)) //设置通知的大图标
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS) //设置通知的提醒方式： 只灯光
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //设置通知的优先级：默认
                .setAutoCancel(false)//设置通知被点击一次是否自动取消
                .setContentText("下载进度:" + "0%")
                .setProgress(100, 0, false);
        mNotification = mBuilder.build();//构建通知对象
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT;// 放置在"正在运行"栏目中
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * 下载APK文件
     */
    private void downloadAPKFile() {
        String downLoadAPKUrl = mUpdateEntity.getUrl();
        if (StringUtils.isEmpty(downLoadAPKUrl) || !downLoadAPKUrl.startsWith("http")) {
            LogUtils.e(TAG, "===========下载链接为空或者不规范==========》" + downLoadAPKUrl);
            // 取消弹窗或者通知
            if (mUpdateEntity.isForceUpdate()) {
                mDownLoadDialog.dismiss();
            } else {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
            // 重新显示更新的提示框
            showUpdateDialog();
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
     */
    private class MyDownLoadCallBack extends DefaultHttpCallback<Object> {
        @Override
        public void onFileSuccess() {
            super.onFileSuccess();
            ToastUtils.showToast(mContext, "下载完成");
            //
            if (mUpdateEntity.isForceUpdate()) {
                mDownLoadDialog.dismiss();
                // 继续显示更新弹窗，否则会显示首页，强制更新的意义就不存在了
//                // 如果Apk文件存在，就显示立即安装，否则显示立即体验
//                if (checkFileExists()) {
//                    mUpdateDialog.setText(R.id.tv_dialog_update_force_btn, "立即安装");
//                    mUpdateDialog.setText(R.id.tv_dialog_update_confirm, "立即安装");
//                } else {
//                    mUpdateDialog.setText(R.id.tv_dialog_update_force_btn, "立即体验");
//                    mUpdateDialog.setText(R.id.tv_dialog_update_confirm, "立即体验");
//                }
                if (!mUpdateDialog.isShowing()) {
                    mUpdateDialog.show();
                }
            } else {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
            // 安装APK
            installAPK();
        }

        @Override
        protected void onProgress(Context context, int progress) {
            super.onProgress(context, progress);
            // 通知更新
            if (mUpdateEntity.isForceUpdate()) {
                updateDialogProgress(progress);
            } else {
                updateNotificationProgress(progress);
            }
        }

        @Override
        protected void onResponseError(Context context, String errorCode, String errorMsg) {
            super.onResponseError(context, errorCode, errorMsg);
            LogUtils.e(TAG, "=====下载失败====>" + "||errorCode:" + errorCode + "||errorMsg:" + errorMsg);
            // 删除已经下载的APK文件
            boolean result = getLocalApkFile().delete();
            LogUtils.e(TAG, "删除已经下载的APK文件" + (result ? "成功" : "失败"));
            // 取消弹窗或者通知
            if (mUpdateEntity.isForceUpdate()) {
                mDownLoadDialog.dismiss();
            } else {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
            // 重新显示更新的提示框
            showUpdateDialog();
        }
    }

    /**
     * 更新Dialog的下载进度
     */
    private void updateDialogProgress(int progress) {
        TextView showProgressTv = mDownLoadDialog.getViewById(R.id.tv_dialog_show_progress);
        showProgressTv.setText(progress + "%");
        ProgressBar progressBar = mDownLoadDialog.getViewById(R.id.pb_dialog_down_load);
        progressBar.setProgress(progress);
    }

    /**
     * 更新通知栏进度
     */
    private void updateNotificationProgress(int progress) {
        // TODO 1秒更新2次进度,非常重要,否则如果频繁发送通知系统会慢慢卡死
        if (System.currentTimeMillis() - beforeTime > 500) {
            mBuilder.setProgress(100, progress, false);
            mBuilder.setContentText("下载进度:" + progress + "%");
            mNotification = mBuilder.build();
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
            beforeTime = System.currentTimeMillis();
        }
    }

    private UpdateCallback mCallback;

    public void setCallback(UpdateCallback callback) {
        this.mCallback = callback;
    }

    public interface UpdateCallback {
        void onDialogDismiss();

    }

    public interface CheckUpdateCallback {

        void onCheckSuccess(UpdateEntity entity);

        void onCheckFail(String code, String message);
    }


    public void checkUpdate(final CheckUpdateCallback callback) {
        HttpRequestUtils.checkUpdate(mContext, new DefaultHttpCallback<UpdateEntity>() {
            @Override
            protected void onResponseSuccess(UpdateEntity result) {
                if (callback != null) {
                    callback.onCheckSuccess(result);
                }
            }

            @Override
            protected void onResponseFail(Context context, String errorCode, String errorMsg) {
                super.onResponseFail(context, errorCode, errorMsg);
                if (callback != null) {
                    callback.onCheckFail(errorCode, errorMsg);
                }
            }

            @Override
            protected void onResponseError(Context context, String errorCode, String errorMsg) {
                super.onResponseError(context, errorCode, errorMsg);
                if (callback != null) {
                    callback.onCheckFail(errorCode, errorMsg);
                }
            }
        });
    }


//    public void makeNotification() {
//        showDownLoadNotification();
//    }
//
//    public void notifyProgress(int progress) {
//        mBuilder.setProgress(100, progress, false);
//        mBuilder.setContentText("下载进度:" + progress + "%");
//        mNotification = mBuilder.build();
//        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
//    }
//
//
//    public void cancelNotification() {
//        mNotificationManager.cancel(NOTIFICATION_ID);
//    }
}
