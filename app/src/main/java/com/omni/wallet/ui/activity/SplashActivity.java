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

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.base.PermissionConfig;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.obdMethods.NodeStart;
import com.omni.wallet.obdMethods.WalletState;
import com.omni.wallet.utils.AppVersionUtils;
import com.omni.wallet.utils.FilesUtils;
import com.omni.wallet.utils.NetworkChangeReceiver;
import com.omni.wallet.utils.PreFilesUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.omni.wallet.utils.MoveCacheFileToFileObd.copyDirectiory;
import static com.omni.wallet.utils.MoveCacheFileToFileObd.deleteDirectory;

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

    PreFilesUtils preFilesUtils;

    boolean isDownloading = false;

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
        preFilesUtils = PreFilesUtils.getInstance(mContext);
        networkChangeReceiver = new NetworkChangeReceiver();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                        downloadView.setVisibility(View.VISIBLE);
                        refreshBtnImageView.setVisibility(View.VISIBLE);
                        ToastUtils.showToast(mContext, "Network is wifi!");
                    }
                    networkIsConnected = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if (!networkIsConnected) {
                        downloadView.setVisibility(View.VISIBLE);
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
                    break;
                default:
                    break;
            }

        };
        networkChangeReceiver.setCallBackNetWork(callBackNetWork);
        subscribeWalletState();
        runOnUiThread(() -> registerReceiver(networkChangeReceiver, intentFilter));

        /*
          check version code to update all states
          检查版本号，更新各个状态
         */
        AppVersionUtils.checkVersion(mContext);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
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
                        /*
                          close permission dialog
                          权限框消失
                         */
                        if (mDeniedDialog != null && mDeniedDialog.isShowing()) {
                            mDeniedDialog.dismiss();
                        }
                        if (mGuideDialog != null && mGuideDialog.isShowing()) {
                            mGuideDialog.dismiss();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(StringUtils.isEmpty(User.getInstance().getPasswordMd5(mContext))){
                                    switchActivityFinish(InitWalletMenuActivity.class);
                                }else {
                                    switchActivityFinish(UnlockActivity.class);
                                }
                            }
                        }, 2000);
//                        if (!isDownloading) {
//                            actionAfterPromise();
//                        }
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "=====用户拒绝======>");
                        if (mGuideDialog != null && mGuideDialog.isShowing()) {
                            mGuideDialog.dismiss();
                        }
                        if (!PermissionUtils.hasSelfPermissions(mContext, PermissionConfig.STORAGE)) {
                            showDeniedDialog(mContext.getString(R.string.tv_dialog_permission_desc_1));
                        } else {
                            showDeniedDialog(mContext.getString(R.string.tv_dialog_permission_desc_2));
                        }
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "=====勾选不再提示======>");
                        if (mDeniedDialog != null && mDeniedDialog.isShowing()) {
                            mDeniedDialog.dismiss();
                        }
                        if (!PermissionUtils.hasSelfPermissions(mContext, PermissionConfig.STORAGE)) {
                            showPermissionGuideDialog(mContext.getString(R.string.tv_dialog_permission_desc_3));
                        } else {
                            showPermissionGuideDialog(mContext.getString(R.string.tv_dialog_permission_desc_4));
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
            /*
             * click cancel
             * 点击取消
             */
            mDeniedDialog.setOnClickListener(R.id.tv_dialog_permission_cancel, v -> {
                mDeniedDialog.dismiss();
                finish();
            });
            /*
             * click allow
             * 点击授权
             */
            mDeniedDialog.setOnClickListener(R.id.tv_dialog_permission_confirm, v -> {
                requestPermission();
                mDeniedDialog.dismiss();
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
            /*
             * click confirm
             * 点击确定
             */
            mGuideDialog.setOnClickListener(R.id.tv_dialog_permission_confirm, v -> {
                mGuideDialog.dismiss();
                finish();
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
        super.onDestroy();
        if (mDeniedDialog != null) {
            mDeniedDialog.dismiss();
            mDeniedDialog = null;
        }
        if (mGuideDialog != null) {
            mGuideDialog.dismiss();
            mGuideDialog = null;
        }
        unregisterReceiver(networkChangeReceiver);

    }


    public void getManifest() {
        PreFilesUtils.DownloadCallback downloadCallback = () -> {
            preFilesUtils.readManifestFile();
            if (ConstantInOB.networkType.equals(NetworkType.MAIN)){
                getPeerFile();
            }else {
                getHeaderBinFile();
            }

        };
        String downloadDirectoryPath = constantInOB.getBasePath()
                + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
        String filePath = downloadDirectoryPath + preFilesUtils.MANIFEST_FILE_NAME;
        File file = new File(filePath);
        Log.d(TAG, "getManifest: manifest exist" + file.exists());
        if (file.exists()) {
            long nowMillis = Calendar.getInstance().getTimeInMillis();
            long fileHeaderLastEdit = FilesUtils.fileLastUpdate(downloadDirectoryPath + ConstantInOB.blockHeaderBin);
            if (nowMillis - fileHeaderLastEdit > ConstantInOB.WEEK_MILLIS) {
                preFilesUtils.downloadManifest(downloadView, downloadCallback);
            }else{
                preFilesUtils.readManifestFile();
                if (ConstantInOB.networkType.equals(NetworkType.MAIN)){
                    getPeerFile();
                }else {
                    getHeaderBinFile();
                }
            }
        } else {
            preFilesUtils.downloadManifest(downloadView, downloadCallback);
        }
    }

    public void getPeerFile() {
        String downloadDirectoryPath = constantInOB.getBasePath()
                + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
        String filePath = downloadDirectoryPath + ConstantInOB.peerJson;
        PreFilesUtils.DownloadCallback downloadCallback = this::getHeaderBinFile;
        boolean isExist = preFilesUtils.checkPeerJsonFileExist();
        if (isExist){
            File file = new File(filePath);
            file.deleteOnExit();
        }
        preFilesUtils.downloadPeerFile(downloadView,downloadCallback);
    }

    public void getHeaderBinFile() {
        PreFilesUtils.DownloadCallback downloadCallback = () -> {
            String downloadDirectoryPath = constantInOB.getBasePath()
                    + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
            String filePath = downloadDirectoryPath + ConstantInOB.blockHeaderBin;
            if (preFilesUtils.checkBlockHeaderMd5Matched()) {
                User.getInstance().setHeaderBinChecked(mContext, true);
                getRegHeadersFile();
            } else {
                File file = new File(filePath);
                file.deleteOnExit();
                getHeaderBinFile();
            }
        };
        boolean isExist = preFilesUtils.checkHeaderBinFileExist();
        boolean isMatched = preFilesUtils.checkBlockHeaderMd5Matched();
        Log.d(TAG, "getHeaderBinFile isMatched: " + isMatched);
        if (!(isExist && isMatched)) {
            preFilesUtils.downloadBlockHeader(downloadView, downloadCallback);
        } else {
            getRegHeadersFile();
        }
    }

    public void getRegHeadersFile() {
        PreFilesUtils.DownloadCallback downloadCallback = () -> {
            String downloadDirectoryPath = constantInOB.getBasePath()
                    + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
            String filePath = downloadDirectoryPath + ConstantInOB.regFilterHeaderBin;
            if (preFilesUtils.checkFilterHeaderMd5Matched()) {
                User.getInstance().setFilterHeaderBinChecked(mContext, true);
                getNeutrinoFile();
            } else {
                File file = new File(filePath);
                file.deleteOnExit();
                getRegHeadersFile();
            }
        };
        boolean isExist = preFilesUtils.checkFilterHeaderBinFileExist();
        boolean isMatched = preFilesUtils.checkFilterHeaderMd5Matched();
        if (!(isExist && isMatched)) {
            preFilesUtils.downloadFilterHeader(downloadView, downloadCallback);
        } else {
            getNeutrinoFile();
        }
    }

    public void getNeutrinoFile() {
        PreFilesUtils.DownloadCallback downloadCallback = () -> {
            String downloadDirectoryPath = constantInOB.getBasePath()
                    + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
            String filePath = downloadDirectoryPath + ConstantInOB.neutrinoDB;
            if (preFilesUtils.checkNeutrinoMd5Matched()) {
                User.getInstance().setNeutrinoDbChecked(mContext, true);
                startNode();
            } else {
                File file = new File(filePath);
                file.deleteOnExit();
                getNeutrinoFile();
            }
        };
        boolean isExist = preFilesUtils.checkNeutrinoFileExist();
        boolean isMatched = preFilesUtils.checkNeutrinoMd5Matched();
        if (!(isExist && isMatched)) {
            preFilesUtils.downloadNeutrino(downloadView, downloadCallback);
        } else {
            startNode();
        }
    }


    public void startNode() {
        runOnUiThread(()->{
            NodeStart.getInstance().startWhenStopWithSubscribeState(mContext);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void actionAfterPromise() {
        String sourceDir = mContext.getExternalCacheDir() + "/";
        String targetDir = mContext.getExternalFilesDir(null).toString() + "/obd";
        File fileDirectory = new File(sourceDir);
        if(fileDirectory.exists()){
            copyDirectiory(sourceDir,targetDir);
            deleteDirectory(sourceDir);
            downloadFiles();
        }else{
            downloadFiles();
        }
//        startNode();
    }

    public void downloadFiles(){
        isDownloading = true;
        boolean isHeaderBinChecked = User.getInstance().isHeaderBinChecked(mContext);
        boolean isFilterHeaderBinChecked = User.getInstance().isFilterHeaderBinChecked(mContext);
        boolean isNeutrinoDbChecked = User.getInstance().isNeutrinoDbChecked(mContext);

        if (isHeaderBinChecked) {
            if (isFilterHeaderBinChecked) {
                if (isNeutrinoDbChecked) {
                    long nowMillis = Calendar.getInstance().getTimeInMillis();
                    String downloadDirectoryPath = constantInOB.getBasePath()
                            + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
                    long fileHeaderLastEdit = FilesUtils.fileLastUpdate(downloadDirectoryPath + ConstantInOB.blockHeaderBin);
                    if (nowMillis - fileHeaderLastEdit > ConstantInOB.WEEK_MILLIS) {
                        Log.d(TAG, "actionAfterPromise: is7Days" + (nowMillis - fileHeaderLastEdit > ConstantInOB.WEEK_MILLIS));
                        getManifest();
                    } else {
                        startNode();
                    }
                } else {
                    preFilesUtils.readManifestFile();
                    getNeutrinoFile();
                }
            } else {
                preFilesUtils.readManifestFile();
                getRegHeadersFile();
            }
        } else {
            getManifest();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.refresh_btn)
    public void clickRefreshBtn() {
        refreshBtnImageView.setVisibility(View.INVISIBLE);
        int downloadingId = preFilesUtils.downloadingId;
        Log.d(TAG, "clickRefreshBtn: " + downloadingId);
//        preFilesUtils.resumeDownloading();
        switch (downloadingId) {
            case 1:
                getManifest();
                break;
            case 2:
                getHeaderBinFile();
                break;
            case 3:
                getRegHeadersFile();
                break;
            case 4:
                getNeutrinoFile();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onExitApplication() {
        super.onExitApplication();
    }

    public void subscribeWalletState() {
        WalletState.getInstance().setWalletState(-100);
        String walletInitType = User.getInstance().getInitWalletType(mContext);
        WalletState.WalletStateCallback walletStateCallback = walletState -> {
            Log.d(TAG, "walletState:" + walletState);
            switch (walletState) {
                case 4:
//                    handler.postDelayed(() -> {
//                        switchActivityFinish(AccountLightningActivity.class);
//                    }, Constants.SPLASH_SLEEP_TIME);
//                    break;
                case 255:
//                    startNode();
                    break;
                case 1:
                case -1:
//                    handler.postDelayed(() -> {
//                        if (walletInitType.equals("initialed")) {
//                            switchActivityFinish(UnlockActivity.class);
//                        } else {
//                            switchActivityFinish(InitWalletMenuActivity.class);
//                        }
//                    }, Constants.SPLASH_SLEEP_TIME);
//                    break;
                default:
                    break;
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

}