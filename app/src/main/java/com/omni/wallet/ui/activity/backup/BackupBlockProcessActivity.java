package com.omni.wallet.ui.activity.backup;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.ui.activity.UnlockActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.NetworkChangeReceiver;
import com.omni.wallet.utils.ObdLogFileObserver;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.utils.WalletGetInfo;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import lnrpc.Stateservice;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

public class BackupBlockProcessActivity extends AppBaseActivity {

    private static final String TAG = BackupBlockProcessActivity.class.getSimpleName();
    private Context ctx = BackupBlockProcessActivity.this;

    List<String> accountList = new ArrayList<>();
    LoadingDialog mLoadingDialog;
    @BindView(R.id.sync_percent)
    TextView syncPercentView;
    @BindView(R.id.block_num_synced)
    TextView syncedBlockNumView;
    @BindView(R.id.commit_num_synced)
    TextView commitNumSyncedView;
    @BindView(R.id.block_num_sync)
    TextView syncBlockNumView;
    @BindView(R.id.commit_num_sync)
    TextView commitNumSyncView;
    @BindView(R.id.process_inner)
    RelativeLayout rvProcessInner;
    @BindView(R.id.progress_bar_outer)
    RelativeLayout rvMyProcessOuter;
    @BindView(R.id.qr_address)
    TextView qrAddressTv;
    @BindView(R.id.qr_image)
    ImageView qrAddressIv;
    @BindView(R.id.tv_doing_explain)
    TextView doExplainTv;
    @BindView(R.id.tv_type_sync)
    TextView typeSyncTV;
    @BindView(R.id.commit_content)
    RelativeLayout commitContentRL;
    @BindView(R.id.btn_start_text)
    TextView startBtnText;
    @BindView(R.id.refresh_btn)
    ImageView refreshBtnImageView;
    String newCreatedAddress ="";
    ObdLogFileObserver obdLogFileObserver = null;
    SharedPreferences blockData = null;
    boolean isSynced = false;
    boolean isCreated = false;
    String walletAddress = "";
    int totalBlockHeight = 0;
    ConstantInOB constantInOB = null;
    ConnectivityManager connectivityManager = null;
    boolean networkIsConnected = true;
    NetworkChangeReceiver networkChangeReceiver = null;

    @Override
    protected Drawable getWindowBackground(){
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_backup_block_process;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        constantInOB = new ConstantInOB(mContext);
        String fileLocal = constantInOB.getRegTestLogPath();
        obdLogFileObserver = new ObdLogFileObserver(fileLocal,ctx);
        blockData = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        isSynced = User.getInstance().getSynced(mContext);
        isCreated = User.getInstance().getCreated(mContext);
        walletAddress = User.getInstance().getWalletAddress(mContext);
        mLoadingDialog = new LoadingDialog(mContext);
        String passwordMd5 = User.getInstance().getPasswordMd5(mContext);
        connectivityManager = getSystemService(ConnectivityManager.class);
        Log.e("password",passwordMd5);
        subscribeState();
    }
    @Override
    protected void initData() {
        networkChangeReceiver = new NetworkChangeReceiver();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                registerReceiver(networkChangeReceiver, intentFilter);
                networkChangeReceiver.setCallBackNetWork(new NetworkChangeReceiver.CallBackNetWork(){
                    @Override
                    public void callBack(int networkType) {
                        switch (networkType){
                            case ConnectivityManager.TYPE_WIFI:
                                if(!networkIsConnected){
                                    refreshBtnImageView.setVisibility(View.VISIBLE);
                                }
                                networkIsConnected = true;
                                Log.e(TAG,"Network is wifi!");
                                ToastUtils.showToast(mContext,"Network is wifi!");
                                break;
                            case ConnectivityManager.TYPE_MOBILE:
                                if(!networkIsConnected){
                                    refreshBtnImageView.setVisibility(View.VISIBLE);
                                }
                                networkIsConnected = true;
                                Log.e(TAG,"Network is mobile!");
                                ToastUtils.showToast(mContext,"Network is mobile!");
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
                                Log.e(TAG,"Network is disconnected!");
                                ToastUtils.showToast(mContext,"Network is disconnected!");
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
    }

    @SuppressLint("LongLogTag")
    private final SharedPreferences.OnSharedPreferenceChangeListener currentBlockSharePreferenceChangeListener = (sharedPreferences, key) -> {
        if (key == "currentBlockHeight"){
            int currentHeight = sharedPreferences.getInt("currentBlockHeight",0);
            updateSyncDataView(currentHeight);
            
        }
    };

    @SuppressLint("SetTextI18n")
    private void updateSyncDataView(int syncedHeight){
        double totalHeight =  totalBlockHeight;
        double currentHeight =  syncedHeight;
        if(syncedHeight>totalBlockHeight){
            syncedHeight = totalBlockHeight;
            currentHeight = totalHeight;
        }
        double percent = (currentHeight/totalHeight * 100);
        double totalWidth =  rvMyProcessOuter.getWidth();
        int innerHeight = (int)rvMyProcessOuter.getHeight()-2;
        int innerWidth = (int) (totalWidth*percent/100);
        @SuppressLint("DefaultLocale") String percentString = String.format("%.2f",percent);
        syncPercentView.setText(percentString + "%");
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth,innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);
        syncedBlockNumView.setText(Integer.toString(syncedHeight));
        commitNumSyncedView.setText(Integer.toString(syncedHeight));
        
        if(totalHeight == currentHeight){
            User.getInstance().setSynced(mContext,true);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDataView(double currentMb, double totalMb){
        double percent = (currentMb/totalMb * 100);
        double totalWidth =  rvMyProcessOuter.getWidth();
        int innerHeight = (int)rvMyProcessOuter.getHeight()-2;
        int innerWidth = (int) (totalWidth*percent/100);
        String percentString = String.format("%.0f",percent);
        syncPercentView.setText(percentString + "%");
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth,innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);

        syncedBlockNumView.setText(String.format("%.0f",currentMb) +"MB");
        commitNumSyncedView.setText(String.format("%.0f",currentMb) +"MB");
    }





    /**
    *点击Copy address
    * click Copy Address
    */

    @OnClick(R.id.btn_copy_addresss)
    public void clickCopyAddress(){
        if(newCreatedAddress.isEmpty()){
            String toastMsg = "Block is syncing now,please wait a moment!";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }else{
            String toastMsg = "Address is copied.";
            CopyUtil.SelfCopy(ctx,newCreatedAddress,toastMsg);
        }

    }

    /**
     *点击Start
     * click Start
     */

    @OnClick(R.id.btn_start)
    public void clickStart(){
        if(newCreatedAddress.isEmpty()){
            String toastMsg = "Block is syncing now,please wait a moment!";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }else{
            switchActivityFinish(AccountLightningActivity.class);
//            if(initWalletType.equals("create")){ 
//                switchActivityFinish(AccountLightningActivity.class);
//            }else if(initWalletType.equals("recovery")){
//                switchActivity(RestoreChannelActivity.class);
//            }
            
        }

    }

    public void getTotalBlockHeight (){
        String jsonStr = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"omni_getinfo\", \"params\": []}";
        HttpUtils.with(ctx)
                .postString()
                .url("http://"+Wallet.BTC_HOST_ADDRESS_REGTEST+":18332")
                .addContent(jsonStr)
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {

                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            String block = jsonObject1.getString("block");
                            blockData.registerOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
                            totalBlockHeight = Integer.parseInt(block);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    doExplainTv.setText(ctx.getString(R.string.sync_block));
                                    typeSyncTV.setText(ctx.getString(R.string.block));
                                    commitContentRL.setVisibility(View.VISIBLE);
                                    commitNumSyncView.setText(block);
                                    syncBlockNumView.setText(block);
                                    updateSyncDataView(0);
                                    obdLogFileObserver.startWatching();

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

    public void newAddressToWallet (){
        LightningOuterClass.NewAddressRequest newAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder().setTypeValue(2).build();
        Obdmobile.oB_NewAddress(newAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
            @Override
            public void onResponse(byte[] bytes) {                
                if(bytes == null){
                    return;
                }
                try {
                    LightningOuterClass.NewAddressResponse newAddressResponse = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    String address = newAddressResponse.getAddress();
                    newCreatedAddress = address;
                    Bitmap mQRBitmap = CodeUtils.createQRCode(address, DisplayUtil.dp2px(mContext, 100));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrAddressTv.setText(address);
                            qrAddressIv.setImageBitmap(mQRBitmap);
                            obdLogFileObserver.stopWatching();
                            blockData.unregisterOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
                        }
                    });
                    // save wallet address to local
                    // 保存地址到本地
                    User.getInstance().setWalletAddress(mContext,address);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();

                }
            }
        });
    }
    
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @Description Download database and bin files
     * @描述 下载数据以及头部执行文件
     * @Date 2022/12/13 22:11
    */
    public void downloadFiles (){
        
        String downloadDirectoryPath = constantInOB.getDownloadDirectoryPath();
        //下载头部文件
        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.blockHeaderBin,downloadDirectoryPath,ConstantInOB.blockHeaderBin).build()
                .setOnStartOrResumeListener(() -> {
                    commitContentRL.setVisibility(View.INVISIBLE);
                    doExplainTv.setText(ctx.getString(R.string.download_header));
                    typeSyncTV.setText(ctx.getString(R.string.size));
                })
                .setOnPauseListener(()->{})
                .setOnCancelListener(()->{})
                .setOnProgressListener(new OnProgressListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onProgress(com.downloader.Progress progress) {
                        double currentM = (double)progress.currentBytes/1024/1024;
                        double totalBytes = (double)progress.totalBytes/1024/1024;
                        commitNumSyncView.setText(String.format("%.0f",totalBytes)+"MB");
                        syncBlockNumView.setText(String.format("%.0f",totalBytes)+"MB");
                        updateDataView(currentM,totalBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        //下载数据文件
                        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.neutrinoDB,downloadDirectoryPath,ConstantInOB.neutrinoDB).build()
                                .setOnStartOrResumeListener(() -> {
                                    doExplainTv.setText(ctx.getString(R.string.download_db));
                                })
                                .setOnPauseListener(()->{})
                                .setOnCancelListener(()->{})
                                .setOnProgressListener(new OnProgressListener() {
                                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                    @Override
                                    public void onProgress(com.downloader.Progress progress) {
                                        Log.e("Progress String db",progress.toString());
                                        double currentM = (double)progress.currentBytes/1024/1024;
                                        double totalBytes = (double)progress.totalBytes/1024/1024;
                                        commitNumSyncView.setText(String.format("%.0f",totalBytes)+"MB");
                                        syncBlockNumView.setText(String.format("%.0f",totalBytes)+"MB");
                                        updateDataView(currentM,totalBytes);
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {

                                        //下载数据文件
                                        PRDownloader.download(ConstantInOB.downloadBaseUrl + ConstantInOB.regFilterHeaderBin,downloadDirectoryPath,ConstantInOB.regFilterHeaderBin).build()
                                                .setOnStartOrResumeListener(() -> {
                                                    doExplainTv.setText(ctx.getString(R.string.download_db));
                                                })
                                                .setOnPauseListener(()->{})
                                                .setOnCancelListener(()->{})
                                                .setOnProgressListener(new OnProgressListener() {
                                                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                                    @Override
                                                    public void onProgress(com.downloader.Progress progress) {
                                                        Log.e("Progress String filter",progress.toString());
                                                        double currentM = (double)progress.currentBytes/1024/1024;
                                                        double totalBytes = (double)progress.totalBytes/1024/1024;
                                                        commitNumSyncView.setText(String.format("%.0f",totalBytes)+"MB");
                                                        syncBlockNumView.setText(String.format("%.0f",totalBytes)+"MB");
                                                        updateDataView(currentM,totalBytes);
                                                    }
                                                })
                                                .start(new OnDownloadListener() {
                                                    @Override
                                                    public void onDownloadComplete() {
                                                        subscribeState();
                                                    }

                                                    @Override
                                                    public void onError(Error error) {

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError(Error error) {

                                    }
                                });
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }
    
    @OnClick(R.id.refresh_btn)
    public void refreshBtnClick (){
        Log.e("Click refresh","Click refresh");
        startOBMobile();
    }
    
    public void startOBMobile(){
        Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + Wallet.START_NODE_OMNI_WITH_SEED, new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------startonError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
//                LogUtils.e(TAG, "------------------startonResponse-----------------" + bytes.toString());
            }
        });
    }
    
    public void unlockWallet(){
        String passwordMd5 = User.getInstance().getPasswordMd5(mContext);
        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passwordMd5)).build();
        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
            }
        });
    }
    
    public void subscribeState(){
        Stateservice.SubscribeStateRequest subscribeStateRequest = Stateservice.SubscribeStateRequest.newBuilder().build();
        Obdmobile.subscribeState(subscribeStateRequest.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Stateservice.SubscribeStateResponse subscribeStateResponse = Stateservice.SubscribeStateResponse.parseFrom(bytes);
                    Stateservice.WalletState stateValue = subscribeStateResponse.getState();
                    switch (stateValue){
                        case NON_EXISTING:
                        case WAITING_TO_START:
                        case UNRECOGNIZED:
                            startOBMobile();
                            break;
                        case LOCKED:
                            unlockWallet();
                            break;
                        case UNLOCKED:
                            getTotalBlockHeight();
                        case RPC_ACTIVE:
                            break;
                        case SERVER_ACTIVE:
                            newAddressToWallet();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
