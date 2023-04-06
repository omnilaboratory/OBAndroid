package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.omni.wallet.R;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.common.NetworkType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PreFilesUtils {
    private static final String TAG = PreFilesUtils.class.getSimpleName();
    public final String MANIFEST_FILE_NAME = "manifest-crc32.txt";
    private static final String BLOCK_HEADER_FILE_NAME = "block_headers.bin";
    private static final String REG_FILTER_HEADER_FILE_NAME = "reg_filter_headers.bin";
    private static final String NEUTRINO_FILE_NAME = "neutrino.db";
    private static final String PEER_FILE_NAME = "peers.json";

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String downloadDictionaryPath;
    private String downloadVersion;
    public int downloadingId;
    private DownloadRequest downloadingRequest;
    private Map<String, String> manifestInfo = new HashMap<>();

    private PreFilesUtils(Context context) {
        this.mContext = context;
        ConstantInOB constantInOB = new ConstantInOB(context);
        this.downloadDictionaryPath = constantInOB.getBasePath()
                + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
        boolean manifestFileExist = checkManifestFileExist();
        if (manifestFileExist) {
            readManifestFile();
        } else {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1;
            Log.d(TAG, "PreFilesUtils year month day: " + year + month + (day + 1));
            String monthStr = month >= 10 ? String.valueOf(month) : ("0" + month);
            String dayStr = day >= 10 ? String.valueOf(day) : ("0" + day);
            downloadVersion = "" + year + "-" + monthStr + "-" + dayStr;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static PreFilesUtils mInstance;

    public static PreFilesUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PreFilesUtils.class) {
                if (mInstance == null) {
                    mInstance = new PreFilesUtils(context);
                }
            }
        }
        return mInstance;
    }

    public int getDownloadingId() {
        return downloadingId;
    }

    public void setDownloadingId(int downloadingId) {
        this.downloadingId = downloadingId;
    }

    public boolean checkPeerJsonFileExist() {
        String filePath = downloadDictionaryPath + PEER_FILE_NAME;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean checkManifestFileExist() {
        String filePath = downloadDictionaryPath + MANIFEST_FILE_NAME;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean checkHeaderBinFileExist() {
        String filePath = downloadDictionaryPath + BLOCK_HEADER_FILE_NAME;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean checkNeutrinoFileExist() {
        String filePath = downloadDictionaryPath + NEUTRINO_FILE_NAME;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean checkFilterHeaderBinFileExist() {
        String filePath = downloadDictionaryPath + REG_FILTER_HEADER_FILE_NAME;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean checkBlockHeaderMd5Matched() {
        String filePath = downloadDictionaryPath + BLOCK_HEADER_FILE_NAME;
        String fileCRC32 = manifestInfo.get(BLOCK_HEADER_FILE_NAME);
        File file = new File(filePath);
        if (file.exists()) {
            return FilesUtils.checkFileCRC32Matched(filePath, fileCRC32);
        }
        return false;

    }

    public boolean checkFilterHeaderMd5Matched() {
        String filePath = downloadDictionaryPath + REG_FILTER_HEADER_FILE_NAME;
        String fileCRC32 = manifestInfo.get(REG_FILTER_HEADER_FILE_NAME);
        File file = new File(filePath);
        if (file.exists()) {
            return FilesUtils.checkFileCRC32Matched(filePath, fileCRC32);
        }
        return false;
    }

    public boolean checkNeutrinoMd5Matched() {
        String filePath = downloadDictionaryPath + NEUTRINO_FILE_NAME;
        String fileCRC32 = manifestInfo.get(NEUTRINO_FILE_NAME);
        File file = new File(filePath);
        if (file.exists()) {
            return FilesUtils.checkFileCRC32Matched(filePath, fileCRC32);
        }
        return false;
    }

    public interface DownloadCallback {
        void callback();
    }

    private void downloadFile(View view, String fileName, String filePath, String downloadUrl, OnDownloadListener onDownloadListener) {
        downloadingRequest = PRDownloader.download(downloadUrl, filePath, fileName).build()
                .setOnStartOrResumeListener(() -> {
                    double total = fileName.equals(MANIFEST_FILE_NAME)
                            ? (double) downloadingRequest.getTotalBytes() / 1024
                            : (double) downloadingRequest.getTotalBytes() / 1024 / 1024;
                    switch (fileName) {
                        case BLOCK_HEADER_FILE_NAME:
                            setStartViewText(view, total, mContext.getString(R.string.download_header), fileName);
                            break;
                        case REG_FILTER_HEADER_FILE_NAME:
                            setStartViewText(view, total, mContext.getString(R.string.download_filter_header), fileName);
                            break;
                        case NEUTRINO_FILE_NAME:
                            setStartViewText(view, total, mContext.getString(R.string.download_db), fileName);
                            break;
                        case MANIFEST_FILE_NAME:
                            setStartViewText(view, total, mContext.getString(R.string.download_manifest), fileName);
                            break;
                        case PEER_FILE_NAME:
//                            setStartViewText(view, total, mContext.getString(R.string.download_peers), fileName);
                            break;
                    }

                })
                .setOnProgressListener((progress) -> {
                    boolean b = fileName.equals(MANIFEST_FILE_NAME) || fileName.equals(PEER_FILE_NAME);
                    double total = b
                            ? (double) progress.totalBytes / 1024
                            : (double) progress.totalBytes / 1024 / 1024;
                    double current = b
                            ? (double) progress.currentBytes / 1024
                            : (double) progress.currentBytes / 1024 / 1024;
                    Log.d(TAG, "downloadFile: total " + total + " current " + current);
                    if(!fileName.equals(PEER_FILE_NAME)) {
                        setProgressViewText(view, total, current, fileName);
                    }

                })
                .setOnPauseListener(() -> ToastUtils.showToast(mContext, fileName + " downloading is paused!"))
                .setOnCancelListener(() -> ToastUtils.showToast(mContext, fileName + " downloading is canceled!"));
        switch (fileName) {
            case MANIFEST_FILE_NAME:
                downloadingId = 1;
                break;
            case BLOCK_HEADER_FILE_NAME:
                downloadingId = 2;
                break;
            case REG_FILTER_HEADER_FILE_NAME:
                downloadingId = 3;
                break;
            case NEUTRINO_FILE_NAME:
                downloadingId = 4;
                break;
            case PEER_FILE_NAME:
                downloadingId = 5;
                break;
        }

        downloadingRequest.start(onDownloadListener);
    }

    public void downloadBlockHeader(View view, DownloadCallback downloadCallback) {
        Log.d(TAG, "downloadBlockHeader: ");
        String fileName = BLOCK_HEADER_FILE_NAME;
        String downloadFileName = downloadVersion + fileName;
        String downloadUrl = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadBaseUrl() + downloadFileName;
        String filePath = downloadDictionaryPath;
        OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                downloadCallback.callback();
            }

            @Override
            public void onError(Error error) {
                view.findViewById(R.id.refresh_btn).setVisibility(View.VISIBLE);
                if (error.isServerError()) {
                    ToastUtils.showToast(mContext, fileName + "server occur error!");
                } else if (error.isConnectionError()) {
                    ToastUtils.showToast(mContext, fileName + "connection occur error!");
                } else {
                    ToastUtils.showToast(mContext, fileName + "download occur error:" + error.toString());
                }
            }
        };
        boolean isExist = checkHeaderBinFileExist();
        boolean isMatched = checkBlockHeaderMd5Matched();
        if (!(isExist && isMatched)) {
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        }

    }

    public void downloadFilterHeader(View view, DownloadCallback downloadCallback) {
        Log.d(TAG, "downloadFilterHeader: ");
        String fileName = REG_FILTER_HEADER_FILE_NAME;
        String downloadFileName = downloadVersion + fileName;
        String downloadUrl = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadBaseUrl() + downloadFileName;
        String filePath = downloadDictionaryPath;
        OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                downloadCallback.callback();
            }

            @Override
            public void onError(Error error) {
                view.findViewById(R.id.refresh_btn).setVisibility(View.VISIBLE);
                if (error.isServerError()) {
                    ToastUtils.showToast(mContext, fileName + "server occur error!");
                } else if (error.isConnectionError()) {
                    ToastUtils.showToast(mContext, fileName + "connection occur error!");
                } else {
                    ToastUtils.showToast(mContext, fileName + "download occur error:" + error.toString());
                }
            }
        };
        boolean isExist = checkFilterHeaderBinFileExist();
        boolean isMatched = checkFilterHeaderMd5Matched();
        if (!(isExist && isMatched)) {
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        }
    }

    public void downloadNeutrino(View view, DownloadCallback downloadCallback) {
        Log.d(TAG, "downloadNeutrino: ");
        String fileName = NEUTRINO_FILE_NAME;
        String downloadFileName = downloadVersion + fileName;
        String downloadUrl = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadBaseUrl() + downloadFileName;
        String filePath = downloadDictionaryPath;
        OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                downloadCallback.callback();
            }

            @Override
            public void onError(Error error) {
                view.findViewById(R.id.refresh_btn).setVisibility(View.VISIBLE);
                if (error.isServerError()) {
                    ToastUtils.showToast(mContext, fileName + "server occur error!");
                } else if (error.isConnectionError()) {
                    ToastUtils.showToast(mContext, fileName + "connection occur error!");
                } else {
                    ToastUtils.showToast(mContext, fileName + "download occur error:" + error.toString());
                }
            }
        };
        boolean isExist = checkNeutrinoFileExist();
        boolean isMatched = checkNeutrinoMd5Matched();
        if (!(isExist && isMatched)) {
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        }
    }

    public void downloadManifest(View view, DownloadCallback downloadCallback) {
        String fileName = MANIFEST_FILE_NAME;
        String downloadFileName = downloadVersion + fileName;
        String downloadUrl = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadBaseUrl() + downloadFileName;
        String filePath = downloadDictionaryPath;
        OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Log.d(TAG, "onDownloadComplete: success");
                downloadCallback.callback();
            }

            @Override
            public void onError(Error error) {
                view.findViewById(R.id.refresh_btn).setVisibility(View.VISIBLE);
                if (error.isServerError()) {
                    ToastUtils.showToast(mContext, fileName + "server occur error!");
                } else if (error.isConnectionError()) {
                    ToastUtils.showToast(mContext, fileName + "connection occur error!");
                } else {
                    ToastUtils.showToast(mContext, fileName + "download occur error:" + error.toString());
                }
            }
        };
        boolean isExist = checkManifestFileExist();
        Log.d(TAG, "downloadManifest isExist: " + isExist);
        if (!isExist) {
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        } else {
            File file = new File(filePath);
            file.deleteOnExit();
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        }
    }

    public void downloadPeerFile(View view, DownloadCallback downloadCallback) {
        Log.d(TAG, "downloadPeerFile");
        String fileName = PEER_FILE_NAME;
        String downloadFileName = fileName;
        String downloadUrl = ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadBaseUrl() + downloadFileName;
        String filePath = downloadDictionaryPath;
        OnDownloadListener onDownloadListener = new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                downloadCallback.callback();
            }

            @Override
            public void onError(Error error) {
                view.findViewById(R.id.refresh_btn).setVisibility(View.VISIBLE);
                if (error.isServerError()) {
                    ToastUtils.showToast(mContext, fileName + "server occur error!");
                } else if (error.isConnectionError()) {
                    ToastUtils.showToast(mContext, fileName + "connection occur error!");
                } else {
                    ToastUtils.showToast(mContext, fileName + "download occur error:" + error.toString());
                }
            }
        };
        boolean isExist = checkPeerJsonFileExist();
        if (!isExist) {
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        } else {
            File file = new File(filePath);
            file.deleteOnExit();
            downloadFile(view, fileName, filePath, downloadUrl, onDownloadListener);
        }
    }

    @SuppressLint("DefaultLocale")
    private void setStartViewText(View view, double total, String downloadString, String filename) {
        LinearLayout downloadView = view.findViewById(R.id.download_view);
        downloadView.setVisibility(View.VISIBLE);
        downloadView.findViewById(R.id.refresh_btn).setVisibility(View.INVISIBLE);
        TextView doExplainTv = downloadView.findViewById(R.id.tv_doing_explain);
        doExplainTv.setText(downloadString);
        TextView syncBlockNumView = downloadView.findViewById(R.id.block_num_sync);
        String totalString = String.format("%.2f", total) + (filename.equals(MANIFEST_FILE_NAME) ? "KB" : "MB");
        syncBlockNumView.setText(totalString);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setProgressViewText(View view, double total, double current, String fileName) {
        boolean b = fileName.equals(MANIFEST_FILE_NAME) || fileName.equals(PEER_FILE_NAME);
        LinearLayout downloadView = view.findViewById(R.id.download_view);
        downloadView.findViewById(R.id.refresh_btn).setVisibility(View.INVISIBLE);
        RelativeLayout rvProcessInner = downloadView.findViewById(R.id.process_inner);
        RelativeLayout rvMyProcessOuter = downloadView.findViewById(R.id.progress_bar_outer);
        double percent = (current / total * 100);
        double totalWidth = rvMyProcessOuter.getWidth();
        int innerHeight = rvMyProcessOuter.getHeight() - 2;
        int innerWidth = (int) (totalWidth * percent / 100);
        TextView syncPercentView = downloadView.findViewById(R.id.sync_percent);
        String percentString = String.format("%.2f", percent) + "%";
        syncPercentView.setText(percentString);
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth, innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);
        TextView syncedBlockNumView = downloadView.findViewById(R.id.block_num_synced);
        syncedBlockNumView.setText(String.format("%.2f", current) + (b ? "KB" : "MB"));
    }

    public void readManifestFile() {
        BufferedReader bfr;
        try {
            bfr = new BufferedReader(new FileReader(downloadDictionaryPath + MANIFEST_FILE_NAME));
            String line = bfr.readLine();
            String[] lineArray = line.split("\\t");
            String fileName = lineArray[lineArray.length - 1];
            downloadVersion = fileName.substring(0, 10);
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                String oldLine = line;
                sb.append(line);
                sb.append("\n");
                String[] readingLineArray = oldLine.split("\\t");
                String readingFileName = readingLineArray[lineArray.length - 1];
                Log.d(TAG, "readManifestFile manifestInfo line: " + line);
                if (readingFileName.endsWith(BLOCK_HEADER_FILE_NAME)) {
                    manifestInfo.put(BLOCK_HEADER_FILE_NAME, readingLineArray[0].trim().toLowerCase());
                } else if (readingFileName.endsWith(NEUTRINO_FILE_NAME)) {
                    manifestInfo.put(NEUTRINO_FILE_NAME, readingLineArray[0].trim().toLowerCase());
                } else if (readingFileName.endsWith(REG_FILTER_HEADER_FILE_NAME)) {
                    manifestInfo.put(REG_FILTER_HEADER_FILE_NAME, readingLineArray[0].trim().toLowerCase());
                }
                Log.d(TAG, "readManifestFile manifestInfo: " + manifestInfo.toString());
                line = bfr.readLine();
                if (line == null) {
                    bfr.close();
                }
            }
            Log.d(TAG, "readManifestFile manifestInfo: " + manifestInfo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseDownloading() {
        PRDownloader.pause(downloadingId);
    }

    public void resumeDownloading() {
        PRDownloader.resume(downloadingId);
    }
}
