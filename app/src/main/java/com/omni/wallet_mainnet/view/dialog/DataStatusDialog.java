package com.omni.wallet_mainnet.view.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.common.ConstantWithNetwork;
import com.omni.wallet_mainnet.common.NetworkType;
import com.omni.wallet_mainnet.entity.event.DownloadEvent;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.FilesUtils;
import com.omni.wallet_mainnet.utils.PreFilesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;

/**
 * 汉: 下载进度状态的弹窗
 * En: DataStatusDialog
 * author: guoyalei
 * date: 2023/5/11
 */
public class DataStatusDialog {
    private static final String TAG = DataStatusDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private TextView syncPercentView;
    private ProgressBar mProgressBar;
    private TextView doExplainTv;

    ConstantInOB constantInOB = null;
    PreFilesUtils preFilesUtils;

    public DataStatusDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_data_status)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        constantInOB = new ConstantInOB(mContext);
        preFilesUtils = PreFilesUtils.getInstance(mContext);
        doExplainTv = mAlertDialog.findViewById(R.id.tv_doing_explain);
        syncPercentView = mAlertDialog.findViewById(R.id.sync_percent);
        mProgressBar = mAlertDialog.findViewById(R.id.progressbar);

        if (User.getInstance().isNeutrinoDbChecked(mContext)) {
            doExplainTv.setText(R.string.download_db);
            double percent = (100 / 100 * 100);
            String percentString = String.format("%.2f", percent) + "%";
            syncPercentView.setText(percentString);
            mProgressBar.setProgress((int) percent);
        }

        /**
         * @描述： 点击刷新按钮
         * @desc: click refresh button
         */
        if(User.getInstance().isNeutrinoDbChecked(mContext)){
            mAlertDialog.findViewById(R.id.layout_refresh).setOnClickListener(null);
        } else {
            mAlertDialog.findViewById(R.id.layout_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int downloadingId = preFilesUtils.downloadingId;
                    LogUtils.e(TAG, "clickRefreshBtn: " + downloadingId);
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
            });
        }

        /**
         * @描述： 点击关闭按钮
         * @desc: click close button
         */
        mAlertDialog.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
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
                preFilesUtils.downloadManifest(null, downloadCallback);
            }else{
                preFilesUtils.readManifestFile();
                if (ConstantInOB.networkType.equals(NetworkType.MAIN)){
                    getPeerFile();
                }else {
                    getHeaderBinFile();
                }
            }
        } else {
            preFilesUtils.downloadManifest(null, downloadCallback);
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
        preFilesUtils.downloadPeerFile(null,downloadCallback);
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
            preFilesUtils.downloadBlockHeader(null, downloadCallback);
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
            preFilesUtils.downloadFilterHeader(null, downloadCallback);
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
            } else {
                File file = new File(filePath);
                file.deleteOnExit();
                getNeutrinoFile();
            }
        };
        boolean isExist = preFilesUtils.checkNeutrinoFileExist();
        boolean isMatched = preFilesUtils.checkNeutrinoMd5Matched();
        if (!(isExist && isMatched)) {
            preFilesUtils.downloadNeutrino(null, downloadCallback);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadEvent(DownloadEvent event) {
        doExplainTv.setText(event.getFileName());
        double percent = (event.getCurrent() / event.getTotal() * 100);
        String percentString = String.format("%.2f", percent) + "%";
        syncPercentView.setText(percentString);
        mProgressBar.setProgress((int) percent);
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}