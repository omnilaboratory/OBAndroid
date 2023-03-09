package com.omni.wallet.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.downloader.internal.DownloadRequestQueue;
import com.downloader.request.DownloadRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.base.PermissionConfig;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.common.Constants;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.AppVersionUtils;
import com.omni.wallet.utils.FilesUtils;
import com.omni.wallet.utils.NetworkChangeReceiver;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.utils.WalletState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Stateservice;
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
    @BindView(R.id.download_view)
    LinearLayout downloadView;
    @BindView(R.id.refresh_btn)
    ImageView refreshBtnImageView;

    ConstantInOB constantInOB = null;
    String downloadVersion = "";
    boolean networkIsConnected = true;
    NetworkChangeReceiver networkChangeReceiver = null;

    NetworkChangeReceiver.CallBackNetWork callBackNetWork = null;

    Map<String, String> manifestInfo = new HashMap<>();

    boolean isDownloading = false;
    int downloadingId = -1;

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
        networkChangeReceiver = new NetworkChangeReceiver();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        long newMillis = millis - ConstantInOB.DAY_MILLIS;
        Date newDate = new Date(newMillis);
        downloadVersion = simpleDateFormat.format(newDate);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");


        callBackNetWork = networkType -> {
            switch (networkType) {
                case ConnectivityManager.TYPE_WIFI:
                    if (!networkIsConnected) {
                        refreshBtnImageView.setVisibility(View.VISIBLE);
                        ToastUtils.showToast(mContext, "Network is wifi!");
                    }
                    networkIsConnected = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if (!networkIsConnected) {
                        refreshBtnImageView.setVisibility(View.VISIBLE);
                        ToastUtils.showToast(mContext, "Network is mobile!");
                    }
                    networkIsConnected = true;
                    break;
                case ConnectivityManager.TYPE_BLUETOOTH:
                case ConnectivityManager.TYPE_DUMMY:
                case ConnectivityManager.TYPE_ETHERNET:
                case ConnectivityManager.TYPE_MOBILE_DUN:
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                case ConnectivityManager.TYPE_MOBILE_MMS:
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                case ConnectivityManager.TYPE_VPN:
                case ConnectivityManager.TYPE_WIMAX:
                case -1:
                    networkIsConnected = false;
                    Log.e(TAG, "Network is disconnected!");
                    ToastUtils.showToast(mContext, "Network is disconnected!");
                    PRDownloader.pause(downloadingId);
                    break;
                default:
                    break;
            }

        };
        networkChangeReceiver.setCallBackNetWork(callBackNetWork);

        runOnUiThread(() -> {
            registerReceiver(networkChangeReceiver, intentFilter);
        });

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
                    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        if (!isDownloading) {
                            getManifestFile();
                        }
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
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getManifestFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        String filePath = downloadDirectoryPath + "manifest.txt";
        File file = new File(filePath);
        if (file.exists()) {
            BufferedReader bfr;
            try {
                bfr = new BufferedReader(new FileReader(downloadDirectoryPath + "manifest.txt"));
                String line = bfr.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    String oldLine = line;
                    sb.append(line);
                    sb.append("\n");
                    Log.e(TAG, line);
                    String[] lineArray = oldLine.split(" {2}");
                    if (lineArray[1].endsWith(ConstantInOB.blockHeaderBin)) {
                        manifestInfo.put(ConstantInOB.blockHeader, lineArray[0]);
                    } else if (lineArray[1].endsWith(ConstantInOB.neutrinoDB)) {
                        manifestInfo.put(ConstantInOB.neutrino, lineArray[0]);
                    } else if (lineArray[1].endsWith(ConstantInOB.regFilterHeaderBin)) {
                        manifestInfo.put(ConstantInOB.regFilterHeader, lineArray[0]);
                    }
                    line = bfr.readLine();
                    if (line == null) {
                        actionAfterPromise();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            DownloadRequest pro =  PRDownloader.download(ConstantInOB.usingDownloadBaseUrl + downloadVersion + "manifest.txt", downloadDirectoryPath, "manifest.txt").build();
            downloadingId = pro.getDownloadId();
            pro.start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            try {
                                BufferedReader bfr;
                                bfr = new BufferedReader(new FileReader(downloadDirectoryPath + "manifest.txt"));
                                String line = bfr.readLine();
                                StringBuilder sb = new StringBuilder();
                                while (line != null) {
                                    String oldLine = line;
                                    sb.append(line);
                                    sb.append("\n");
                                    Log.e(TAG, line);
                                    String[] lineArray = oldLine.split(" {2}");
                                    if (lineArray[1].endsWith(ConstantInOB.blockHeaderBin)) {
                                        manifestInfo.put(ConstantInOB.blockHeader, lineArray[0]);
                                    } else if (lineArray[1].endsWith(ConstantInOB.neutrinoDB)) {
                                        manifestInfo.put(ConstantInOB.neutrino, lineArray[0]);
                                    } else if (lineArray[1].endsWith(ConstantInOB.regFilterHeaderBin)) {
                                        manifestInfo.put(ConstantInOB.regFilterHeader, lineArray[0]);
                                    }
                                    line = bfr.readLine();
                                    if (line == null) {
                                        actionAfterPromise();
                                    }
                                }

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Error error) {
                            boolean connectionError = error.isConnectionError();
                            boolean serverError = error.isServerError();
                            if (connectionError) {
                                Log.e(TAG, "Manifest download ConnectError");
                            } else if (serverError) {
                                Log.e(TAG, "Manifest download ConnectError");
                            } else {
                                Log.e(TAG, "Manifest" + error.toString());
                            }

                        }
                    });
        }

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void downloadHeaderBinFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        String filePath = downloadDirectoryPath + ConstantInOB.blockHeaderBin;
        File file = new File(filePath);
        if (file.exists()) {
            downloadDBFile();
            return;
        }
        DownloadRequest downloadRequest = PRDownloader.download(ConstantInOB.usingDownloadBaseUrl + downloadVersion + ConstantInOB.blockHeaderBin, downloadDirectoryPath, ConstantInOB.blockHeaderBin).build();
        downloadRequest.setDownloadId(1);
        downloadingId = downloadRequest.getDownloadId();
        Log.d(TAG,"downloadHeaderBinFile: " + downloadingId);
        final double[] totalBytes = {(double) downloadRequest.getTotalBytes() / 1024 / 1024};
        downloadRequest.setOnStartOrResumeListener(() -> {
            Log.d(TAG, "downloadHeaderBinFile: download resume");
            refreshBtnImageView.setVisibility(View.INVISIBLE);
            isDownloading = true;
            doExplainTv.setText(mContext.getString(R.string.download_header));
            totalBytes[0] = (double) downloadRequest.getTotalBytes() / 1024 / 1024;
            syncBlockNumView.setText(String.format("%.2f", totalBytes[0]) + "MB");
        });
        downloadRequest.setOnPauseListener(() -> {
            Log.e(TAG, "Pause download " + ConstantInOB.blockHeaderBin);
        });
        downloadRequest.setOnCancelListener(() -> {
            Log.e(TAG, "Cancel download " + ConstantInOB.blockHeaderBin);
        });
        downloadRequest.setOnProgressListener(progress -> {
            refreshBtnImageView.setVisibility(View.INVISIBLE);
            double currentM = (double) progress.currentBytes / 1024 / 1024;
            updateDataView(currentM, totalBytes[0]);
        });
        downloadRequest.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                String fileMd5 = manifestInfo.get(ConstantInOB.blockHeader);
                Log.e(TAG, fileMd5);
                boolean checkFileMd5Matched = FilesUtils.checkFileMd5Matched(filePath, fileMd5);
                if (checkFileMd5Matched) {
                    downloadFilterHeaderBinFile();
                } else {
                    File file1 = new File(filePath);
                    file1.delete();
                    file1.exists();
                    downloadHeaderBinFile();
                }
            }

            @Override
            public void onError(Error error) {
                boolean connectionError = error.isConnectionError();
                boolean serverError = error.isServerError();
                if (connectionError) {
                    refreshBtnImageView.setVisibility(View.VISIBLE);
                    ToastUtils.showToast(mContext,"HeaderBin download Connect Error");
                    Log.e(TAG, "HeaderBin download Connect Error");
                } else if (serverError) {
                    ToastUtils.showToast(mContext,"HeaderBin download server Error");
                    Log.e(TAG, "HeaderBin download server Error");
                } else {
                    Log.e(TAG, "HeaderBin" + error.toString());
                }
            }
        });
    }

    public void downloadFilterHeaderBinFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        String filePath = downloadDirectoryPath + ConstantInOB.regFilterHeaderBin;
        File file = new File(filePath);
        if (file.exists()) {
            downloadDBFile();
            return;
        }
        DownloadRequest downloadRequest = PRDownloader.download(ConstantInOB.usingDownloadBaseUrl + downloadVersion + ConstantInOB.regFilterHeaderBin, downloadDirectoryPath, ConstantInOB.regFilterHeaderBin).build();
        downloadRequest.setDownloadId(2);
        downloadingId = downloadRequest.getDownloadId();
        Log.d(TAG, "downloadFilterHeaderBinFile: " + downloadingId);
        final double[] totalBytes = {0};
        downloadRequest.setOnStartOrResumeListener(() -> {
            refreshBtnImageView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "downloadFilterHeaderBinFile: download resume");
            isDownloading = true;
            totalBytes[0] = (double) downloadRequest.getTotalBytes() / 1024 / 1024;
            syncBlockNumView.setText(String.format("%.2f", totalBytes[0]) + "MB");
            doExplainTv.setText(mContext.getString(R.string.download_filter_header));
        });
        downloadRequest.setOnPauseListener(() -> {
            Log.e(TAG, "Pause download " + ConstantInOB.regFilterHeaderBin);
        });
        downloadRequest.setOnCancelListener(() -> {
            Log.e(TAG, "Cancel download " + ConstantInOB.regFilterHeaderBin);
        });
        downloadRequest.setOnProgressListener(new OnProgressListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onProgress(com.downloader.Progress progress) {
                refreshBtnImageView.setVisibility(View.INVISIBLE);
                double currentM = (double) progress.currentBytes / 1024 / 1024;
                updateDataView(currentM, totalBytes[0]);
            }
        });
        downloadRequest.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                String fileMd5 = manifestInfo.get(ConstantInOB.regFilterHeader);
                Log.e(TAG, fileMd5);
                boolean checkFileMd5Matched = FilesUtils.checkFileMd5Matched(filePath, fileMd5);
                if (checkFileMd5Matched) {
                    downloadDBFile();
                } else {
                    File file1 = new File(filePath);
                    file1.delete();
                    file1.exists();
                    downloadFilterHeaderBinFile();
                }
            }


            @Override
            public void onError(Error error) {
                boolean connectionError = error.isConnectionError();
                boolean serverError = error.isServerError();
                if (connectionError) {
                    refreshBtnImageView.setVisibility(View.VISIBLE);
                    ToastUtils.showToast(mContext,"FilterHeaderBin download Connect Error");
                    Log.e(TAG, "FilterHeaderBin download Connect Error");

                } else if (serverError) {
                    ToastUtils.showToast(mContext,"FilterHeaderBin download server Error");
                    Log.e(TAG, "FilterHeaderBin download server Error");
                } else {
                    Log.e(TAG, "FilterHeaderBin" + error.toString());
                }
            }
        });
    }

    public void downloadDBFile() {
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        String filePath = downloadDirectoryPath + ConstantInOB.neutrinoDB;
        File file = new File(filePath);
        if (file.exists()) {
            startNode();
            return;
        }
        DownloadRequest downloadRequest = PRDownloader.download(ConstantInOB.usingDownloadBaseUrl + downloadVersion + ConstantInOB.neutrinoDB, downloadDirectoryPath, ConstantInOB.neutrinoDB).build();
        downloadRequest.setDownloadId(3);
        downloadingId = downloadRequest.getDownloadId();
        Log.d(TAG, "downloadDBFile: " + downloadingId);
        final double[] totalBytes = {0};
        downloadRequest.setOnStartOrResumeListener(() -> {
            refreshBtnImageView.setVisibility(View.INVISIBLE);
            Log.d(TAG, "downloadDBFile: download resume");
            isDownloading = true;
            totalBytes[0] = (double) downloadRequest.getTotalBytes() / 1024 / 1024;
            doExplainTv.setText(mContext.getString(R.string.download_db));
            syncBlockNumView.setText(String.format("%.2f", totalBytes[0]) + "MB");
        });
        downloadRequest.setOnPauseListener(() -> {
            Log.e(TAG, "Pause download " + ConstantInOB.neutrinoDB);
        });
        downloadRequest.setOnCancelListener(() -> {
            Log.e(TAG, "Cancel download " + ConstantInOB.neutrinoDB);
        });
        downloadRequest.setOnProgressListener(new OnProgressListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onProgress(com.downloader.Progress progress) {
                refreshBtnImageView.setVisibility(View.INVISIBLE);
                double currentM = (double) progress.currentBytes / 1024 / 1024;
                updateDataView(currentM, totalBytes[0]);
            }
        });
        downloadRequest.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                String fileMd5 = manifestInfo.get(ConstantInOB.neutrino);
                Log.e(TAG, fileMd5);
                boolean checkFileMd5Matched = FilesUtils.checkFileMd5Matched(filePath, fileMd5);
                if (checkFileMd5Matched) {
                    startNode();
                } else {
                    File file1 = new File(filePath);
                    file1.delete();
                    file1.exists();
                    downloadDBFile();
                }

            }

            @Override
            public void onError(Error error) {
                boolean connectionError = error.isConnectionError();
                boolean serverError = error.isServerError();
                if (connectionError) {
                    refreshBtnImageView.setVisibility(View.VISIBLE);
                    ToastUtils.showToast(mContext,"DBFile download Connect Error");
                    Log.e(TAG, "DBFile download Connect Error");

                } else if (serverError) {
                    ToastUtils.showToast(mContext,"DBFile download server Error");
                    Log.e(TAG, "DBFile download server Error");
                } else {
                    Log.e(TAG, "DBFile" + error.toString());
                }
            }
        });
    }

    public void startNode() {
        Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + ConstantInOB.usingNeutrinoConfig, new Callback() {
            @Override
            public void onError(Exception e) {
                if (e.getMessage().equals("lnd already started")) {
                    runOnUiThread(() -> {
                        String walletInitType = User.getInstance().getInitWalletType(mContext);
                        if (walletInitType.equals("initialed")) {
                            switchActivityFinish(UnlockActivity.class);
                        } else {
                            switchActivityFinish(InitWalletMenuActivity.class);
                        }
                    });
                } else if (e.getMessage().equals("unable to start server: unable to unpack single backups: chacha20poly1305: message authentication failed")) {

                }else if(e.getMessage().equals("error creating wallet config: unable to initialize neutrino backend: unable to create neutrino database: cannot allocate memory")){
                    ToastUtils.showToast(mContext,"Failed to start, please check your cache is sufficient. After confirming that the cache is sufficient, please restart the App.");
                }

                LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                runOnUiThread(() -> {
                    subscribeWalletState();
                });
                LogUtils.e(TAG, "------------------startonSuccess------------------");
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDataView(double currentMb, double totalMb) {
        double percent = (currentMb / totalMb * 100);
        double totalWidth = rvMyProcessOuter.getWidth();
        int innerHeight = (int) rvMyProcessOuter.getHeight() - 2;
        int innerWidth = (int) (totalWidth * percent / 100);
        String percentString = String.format("%.2f", percent);
        syncPercentView.setText(percentString + "%");
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth, innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);
        syncedBlockNumView.setText(String.format("%.2f", currentMb) + "MB");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actionAfterPromise() {
        String initWalletType = User.getInstance().getInitWalletType(mContext);
        long nowMillis = Calendar.getInstance().getTimeInMillis();
        if (initWalletType.equals("")) {
            downloadView.setVisibility(View.VISIBLE);
            downloadHeaderBinFile();
        } else {
            String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
            long fileHeaderLastEdit = FilesUtils.fileLastUpdate(downloadDirectoryPath + ConstantInOB.blockHeaderBin);
            if (nowMillis - fileHeaderLastEdit > ConstantInOB.DAY_MILLIS * 2) {
                downloadView.setVisibility(View.VISIBLE);
                downloadHeaderBinFile();
            } else {
                handler.postDelayed(() -> {
                    startNode();
                }, Constants.SPLASH_SLEEP_TIME);

            }
        }
//        startNode();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.refresh_btn)
    public void clickRefreshBtn() {
        refreshBtnImageView.setVisibility(View.INVISIBLE);
        switch (downloadingId){
            case 1 :
                downloadHeaderBinFile();
                break;
            case 2 :
                downloadFilterHeaderBinFile();
                break;
            case 3 :
                downloadDBFile();
                break;
            default :
                break;
        }

    }


    @Override
    protected void onExitApplication() {
        super.onExitApplication();
    }

    public void subscribeWalletState() {
        Log.e(TAG, "do subscribe action");
        String walletInitType = User.getInstance().getInitWalletType(mContext);
        WalletState.WalletStateCallback walletStateCallback = walletState -> {
            Log.e(TAG,"walletState:" + String.valueOf(walletState));
            switch (walletState) {
                case 4:
                    switchActivityFinish(AccountLightningActivity.class);
                    break;
                case 255:
                    if (walletInitType.equals("initialed")) {
                        switchActivityFinish(UnlockActivity.class);
                        break;
                    } else {
                        switchActivityFinish(InitWalletMenuActivity.class);
                        break;
                    }

                default:
                    break;
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
        WalletState.getInstance().subscribeWalletState(mContext);
    }

}