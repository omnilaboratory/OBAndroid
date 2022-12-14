package com.omni.wallet.ui.activity.backup;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.utils.CopyUtil;
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
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupBlockProcessActivity extends AppBaseActivity {

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

    String newCreatedAddress ="";
    ObdLogFileObserver obdLogFileObserver = null;
    SharedPreferences blockData = null;

    @SuppressLint("LongLogTag")
    private final SharedPreferences.OnSharedPreferenceChangeListener currentBlockSharePreferenceChangeListener = (sharedPreferences, key) -> {
        if (key == "currentBlockHeight"){
            int totalHeight = sharedPreferences.getInt("totalBlockHeight",0);
            int currentHeight = sharedPreferences.getInt("currentBlockHeight",0);
            if(totalHeight<=currentHeight){
                int endCurrentHeight = totalHeight;
                updateSyncDataView(endCurrentHeight,totalHeight);
                newAddressToWallet();
            }else{
                boolean isSynced = blockData.getBoolean("isSynced",false);
                if(isSynced){
                    currentHeight = totalHeight;
                    updateSyncDataView(currentHeight,totalHeight);
                    newAddressToWallet();
                }else{
                    updateSyncDataView(currentHeight,totalHeight);    
                }
                
            }
        }
    };

    private void updateSyncDataView(int syncedHeight,int syncHeight){
        double totalHeight =  syncHeight;
        double currentHeight =  syncedHeight;
        double percent = (currentHeight/totalHeight * 100);
        double totalWidth =  rvMyProcessOuter.getWidth();
        int innerHeight = (int)rvMyProcessOuter.getHeight()-2;
        int innerWidth = (int) (totalWidth*percent/100);
        String percentString = String.format("%.2f",percent);
        syncPercentView.setText(percentString + "%");
        RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth,innerHeight);
        rvProcessInner.setLayoutParams(rlInnerParam);

        syncedBlockNumView.setText(Integer.toString(syncedHeight));
        commitNumSyncedView.setText(Integer.toString(syncedHeight));
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



    @Override
    protected Drawable getWindowBackground(){
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_backup_block_process;
    }

    @Override
    protected void initView() {
        
        mLoadingDialog = new LoadingDialog(mContext);
        accountList = WalletGetInfo.getAccountList(ctx);
        String fileLocal = ctx.getExternalCacheDir() + "/logs/bitcoin/regtest/lnd.log";
//        String fileLocal = ctx.getExternalCacheDir() + "/logs/bitcoin/testnet/lnd.log";
        obdLogFileObserver = new ObdLogFileObserver(fileLocal,ctx);
        blockData = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        blockData.edit().putBoolean("isSynced",false);
        blockData.edit().commit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFiles();
            }
        }).start();
        
        
        /*PRDownloaderConfig config = PRDownloaderConfig.newBuilder().setDatabaseEnabled(true)
                .setReadTimeout(30000)
                .setConnectTimeout(30000)
                .build();
        PRDownloader.initialize(ctx,config);*/
    }
    @Override
    protected void initData() {
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
                            SharedPreferences.Editor editor = blockData.edit();
                            editor.putInt("totalBlockHeight",Integer.parseInt(block));
                            editor.commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    doExplainTv.setText(ctx.getString(R.string.sync_block));
                                    typeSyncTV.setText(ctx.getString(R.string.block));
                                    commitContentRL.setVisibility(View.VISIBLE);
                                    commitNumSyncView.setText(block);
                                    syncBlockNumView.setText(block);
                                    updateSyncDataView(0,Integer.parseInt(block));
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
        Obdmobile.newAddress(newAddressRequest.toByteArray(), new Callback() {
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
                    @SuppressLint("WorldWriteableFiles") SharedPreferences addressList = ctx.getSharedPreferences("Account", MODE_WORLD_WRITEABLE);
                    SharedPreferences.Editor editor = addressList.edit();
                    editor.putString("accountList",address);
                    editor.commit();
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
        //Save files path 存储路径
        String basePath = ctx.getExternalCacheDir() + "/data/chain/bitcoin/testnet/";
        //Header.bin file name 头部文件名称
        String headerFileName = "block_header.bin";
        //data base file name 数据文件名称 
        String dbFileName = "neutrino.db";
        //data base file download url 数据文件下载路径
        String dbUrlStr = "http://43.138.107.248:9090/obd-android-build.tar.gz";
        //header bin file download url 头部执行文件下载路径
        String headerUrlStr = "http://43.138.107.248:9090/obd-android-build.tar.gz";
        //下载头部文件
        PRDownloader.download(headerUrlStr,basePath,headerFileName).build()
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
                        Log.e("Progress String header",progress.toString());
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
                        PRDownloader.download(dbUrlStr,basePath,dbFileName).build()
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
                                        getTotalBlockHeight();
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
}
