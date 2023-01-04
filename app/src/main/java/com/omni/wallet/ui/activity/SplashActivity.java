package com.omni.wallet.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.base.PermissionConfig;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.common.Constants;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.backup.BackupChannelActivity;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.utils.AppVersionUtils;

import java.io.File;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * The page for initial
 * 启动页
 */
public class SplashActivity extends AppBaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static Handler handler = new Handler();
    /**
     * refuse access dialog
     * 权限拒绝的对话框
     */
    private AlertDialog mDeniedDialog;
    /**
     * guidance  access dialog
     * 权限指引的对话框
     */
    private AlertDialog mGuideDialog;

    @BindView(R.id.tv_doing_explain)
    TextView doExplainTv;
    @BindView(R.id.block_num_sync)
    TextView syncBlockNumView;
    @BindView(R.id.progress_bar_outer)
    RelativeLayout rvMyProcessOuter;
    @BindView(R.id.sync_percent)
    TextView syncPercentView;
    @BindView(R.id.process_inner)
    RelativeLayout rvProcessInner;
    @BindView(R.id.block_num_synced)
    TextView syncedBlockNumView;

    ConstantInOB constantInOB = null;

    @Override
    protected boolean isFullScreenStyle() {
        return true;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        constantInOB = new ConstantInOB(mContext);
        /**
         * check version code to update all states
         * 检查版本号，更新各个状态
         */
        AppVersionUtils.checkVersion(mContext);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * check access to read local file,if did not have the accession ,then quit from app
         * 初始化的时候检查外部存储权限，如果不授予不进APP
         */
        requestPermission();
    }

    /**
     * ask for permission
     * 请求权限
     */
    private void requestPermission() {
        PermissionUtils.requestPermission(this, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        /**
                         * close permission dialog
                         * 权限框消失
                         */
                        if (mDeniedDialog != null && mDeniedDialog.isShowing()) {
                            mDeniedDialog.dismiss();
                        }
                        if (mGuideDialog != null && mGuideDialog.isShowing()) {
                            mGuideDialog.dismiss();
                        }
                        /**
                         * To home page after 3s
                         * 延时3秒跳转主页
                         */
                        handler.postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                actionAfterPromise();
                            }
                        }, Constants.SPLASH_SLEEP_TIME);
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "=====用户拒绝======>");
                        if (mGuideDialog != null && mGuideDialog.isShowing()) {
                            mGuideDialog.dismiss();
                        }
                        if (!PermissionUtils.hasSelfPermissions(mContext, PermissionConfig.STORAGE)) {
                            showDeniedDialog("需要您授予该APP读写手机外部存储的权限");
                        } else {
                            showDeniedDialog("需要您授予该APP读写手机状态的权限");
                        }
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "=====勾选不再提示======>");
                        if (mDeniedDialog != null && mDeniedDialog.isShowing()) {
                            mDeniedDialog.dismiss();
                        }
                        if (!PermissionUtils.hasSelfPermissions(mContext, PermissionConfig.STORAGE)) {
                            showPermissionGuideDialog("该APP需要您授予读写手机存储的权限，请到“设置->应用”或者“设置->权限管理”授予存储权限");
                        } else {
                            showPermissionGuideDialog("该APP需要您授予读写手机状态的权限，请到“设置->应用”或者“设置->权限管理”授予存储权限");
                        }
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * To next page
     * 跳转下一页
     */
    private void turnToNextPage() {
        if (User.getInstance().isLogin(mContext) & !StringUtils.isEmpty(User.getInstance().getFirstLogin(mContext))) {
            switchActivityFinish(UnlockActivity.class, mBundle);
//            switchActivityFinish(RestoreChannelActivity.class, mBundle);
//            switchActivityFinish(BackupChannelActivity.class, mBundle);
        } else {
            switchActivityFinish(UnlockActivity.class, mBundle);
//            switchActivityFinish(RestoreChannelActivity.class, mBundle);
//            switchActivityFinish(BackupChannelActivity.class, mBundle);
        }
    }

    /**
     * When be refused by permission, show ask for permission dialog.
     * 权限被拒绝，显示权限描述对话框
     */
    private void showDeniedDialog(String desc) {
        if (mDeniedDialog == null) {
            mDeniedDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_permission_desc)
                    .setWidthAndHeight(DisplayUtil.getScreenWidth(mContext) * 3 / 4, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setText(R.id.tv_dialog_permission_desc, desc)
                    .fullWidth()
                    .setCanceledOnTouchOutside(false)
                    .create();
            /**
             * click cancel
             * 点击取消
             */
            mDeniedDialog.setOnClickListener(R.id.tv_dialog_permission_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeniedDialog.dismiss();
                    finish();
                }
            });
            /**
             * click allow
             * 点击授权
             */
            mDeniedDialog.setOnClickListener(R.id.tv_dialog_permission_confirm, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermission();
                    mDeniedDialog.dismiss();
                }
            });
        }
        if (!mDeniedDialog.isShowing()) {
            mDeniedDialog.show();
        }
    }

    /**
     * show the dialog about guidance ask for permission
     * 显示权限指引对话框
     */
    private void showPermissionGuideDialog(String desc) {
        if (mGuideDialog == null) {
            mGuideDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_permission_guide)
                    .setWidthAndHeight(DisplayUtil.getScreenWidth(mContext) * 3 / 4, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setText(R.id.tv_dialog_permission_desc, desc)
                    .fullWidth()
                    .setCanceledOnTouchOutside(false)
                    .create();
            /**
             * click confirm
             * 点击确定
             */
            mGuideDialog.setOnClickListener(R.id.tv_dialog_permission_confirm, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGuideDialog.dismiss();
                    finish();
                }
            });
        }
        if (!mGuideDialog.isShowing()) {
            mGuideDialog.show();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        if (mDeniedDialog != null) {
            mDeniedDialog.dismiss();
            mDeniedDialog = null;
        }
        if (mGuideDialog != null) {
            mGuideDialog.dismiss();
            mGuideDialog = null;
        }
        super.onDestroy();
    }

    public void downloadHeaderBinFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        /*File file = new File(ConstantInOB.downloadBaseUrl + ConstantInOB.blockHeaderBin);
        if (file.exists()) {
            downloadDBFile();
        } else {

        }*/
        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.blockHeaderBin, downloadDirectoryPath, ConstantInOB.blockHeaderBin).build()
                .setOnStartOrResumeListener(() -> {
                    doExplainTv.setText(mContext.getString(R.string.download_header));
                })
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                })
                .setOnProgressListener(new OnProgressListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onProgress(com.downloader.Progress progress) {
                        double currentM = (double) progress.currentBytes / 1024 / 1024;
                        double totalBytes = (double) progress.totalBytes / 1024 / 1024;
                        syncBlockNumView.setText(String.format("%.0f", totalBytes) + "MB");
                        updateDataView(currentM, totalBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        downloadDBFile();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });

    }

    public void downloadDBFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        /*File file = new File(ConstantInOB.downloadBaseUrl + ConstantInOB.neutrinoDB);
        if (file.exists()) {
            downloadFilterHeaderBinFile();
        } else {

        }*/
        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.neutrinoDB, downloadDirectoryPath, ConstantInOB.neutrinoDB).build()
                .setOnStartOrResumeListener(() -> {
                    doExplainTv.setText(mContext.getString(R.string.download_db));
                })
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                })
                .setOnProgressListener(new OnProgressListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onProgress(com.downloader.Progress progress) {
                        double currentM = (double) progress.currentBytes / 1024 / 1024;
                        double totalBytes = (double) progress.totalBytes / 1024 / 1024;
                        syncBlockNumView.setText(String.format("%.0f", totalBytes) + "MB");
                        updateDataView(currentM, totalBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        downloadFilterHeaderBinFile();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }

    public void downloadFilterHeaderBinFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        /*File file = new File(ConstantInOB.downloadBaseUrl + ConstantInOB.neutrinoDB);
        if (file.exists()) {
            Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + ConstantInOB.neutrinoRegTestConfig, new Callback() {
                @Override
                public void onError(Exception e) {
                    LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
                }

                @Override
                public void onResponse(byte[] bytes) {
//                LogUtils.e(TAG, "------------------startonResponse-----------------" + bytes.toString());
                    turnToNextPage();
                }
            });
        } else {
        }*/

        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.regFilterHeaderBin, downloadDirectoryPath, ConstantInOB.regFilterHeaderBin).build()
                .setOnStartOrResumeListener(() -> {
                    doExplainTv.setText(mContext.getString(R.string.download_filter_header));
                })
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                })
                .setOnProgressListener(new OnProgressListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onProgress(com.downloader.Progress progress) {
                        double currentM = (double) progress.currentBytes / 1024 / 1024;
                        double totalBytes = (double) progress.totalBytes / 1024 / 1024;
                        syncBlockNumView.setText(String.format("%.0f", totalBytes) + "MB");
                        updateDataView(currentM, totalBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        startNode();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
        
    }

    public void startNode(){
        Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + ConstantInOB.neutrinoRegTestConfig, new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
//                LogUtils.e(TAG, "------------------startonResponse-----------------" + bytes.toString());
                turnToNextPage();
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDataView(double currentMb, double totalMb) {
        double percent = (currentMb / totalMb * 100);
        double totalWidth = rvMyProcessOuter.getWidth();
        int innerHeight = (int) rvMyProcessOuter.getHeight() - 2;
        int innerWidth = (int) (totalWidth * percent / 100);
        String percentString = String.format("%.0f", percent);
        syncPercentView.setText(percentString + "%");
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth, innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);

        syncedBlockNumView.setText(String.format("%.0f", currentMb) + "MB");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actionAfterPromise(){

        downloadHeaderBinFile();
        /*long lastUpdate = User.getInstance().getBaseFileLastUpdate(mContext);
        long nowTimeMillis = Calendar.getInstance().getTimeInMillis();
        Log.e("时间戳", String.valueOf(nowTimeMillis-lastUpdate));
        if (lastUpdate == -1){
            downloadHeaderBinFile();
        }else{
            if (nowTimeMillis - lastUpdate > ConstantInOB.DAY_MILLIS*2){
                downloadHeaderBinFile();
            }else{
                startNode();
            }
        }*/

    }
}
