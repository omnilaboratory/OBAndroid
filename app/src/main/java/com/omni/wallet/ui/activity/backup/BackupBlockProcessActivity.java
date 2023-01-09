package com.omni.wallet.ui.activity.backup;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.NetworkChangeReceiver;
import com.omni.wallet.utils.ObdLogFileObserver;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.utils.WalletState;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupBlockProcessActivity extends AppBaseActivity {

    private static final String TAG = BackupBlockProcessActivity.class.getSimpleName();
    private Context ctx = BackupBlockProcessActivity.this;


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
    String initWalletType = "";

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
        initWalletType = User.getInstance().getInitWalletType(mContext);
        runOnUiThread(()->{
            subscribeState();
        });

    }
    @Override
    protected void initData() {
        networkChangeReceiver = new NetworkChangeReceiver();
        runOnUiThread(() -> {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            networkChangeReceiver.setCallBackNetWork(callBackNetWork);
            registerReceiver(networkChangeReceiver, intentFilter);
        });
    }

    @Override
    protected void onDestroy() {
        PublicUtils.closeLoading(mLoadingDialog);
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
//            switchActivityFinish(AccountLightningActivity.class);
            if(initWalletType.equals("create")){
                switchActivity(AccountLightningActivity.class);
            }else if(initWalletType.equals("recovery")){
                switchActivity(RestoreChannelActivity.class);
            }
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
                            runOnUiThread(() -> {
                                doExplainTv.setText(ctx.getString(R.string.sync_block));
                                typeSyncTV.setText(ctx.getString(R.string.block));
                                commitContentRL.setVisibility(View.VISIBLE);
                                commitNumSyncView.setText(block);
                                syncBlockNumView.setText(block);
                                updateSyncDataView(0);
                                obdLogFileObserver.startWatching();

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
        Log.e(TAG,"new Address count");
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
                    runOnUiThread(() -> {
                        qrAddressTv.setText(address);
                        qrAddressIv.setImageBitmap(mQRBitmap);
                        obdLogFileObserver.stopWatching();
                        blockData.unregisterOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
                        User.getInstance().setWalletAddress(mContext,address);
                        if(initWalletType.equals("create")){
                            User.getInstance().setInitWalletType(mContext,"created");
                        }else if(initWalletType.equals("recovery")){
                            User.getInstance().setInitWalletType(mContext,"recovered");
                        }
                    });
                    // save wallet address to local
                    // 保存地址到本地

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    
    @OnClick(R.id.refresh_btn)
    public void refreshBtnClick (){
        Log.e("Click refresh","Click refresh");
        startOBMobile();
    }
    
    public void startOBMobile(){
        Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + ConstantInOB.neutrinoRegTestConfig, new Callback() {
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
        WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
            Log.e(TAG,"wallet state changed:" + walletState);
            switch (walletState){
                case 1:
                    unlockWallet();
                    break;
                case 2:
                    getTotalBlockHeight();
                case 4:
                    if(User.getInstance().getWalletAddress(mContext).isEmpty()){
                        newAddressToWallet();
                    }

                    break;
                default:
                    break;

            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

    NetworkChangeReceiver.CallBackNetWork callBackNetWork = new NetworkChangeReceiver.CallBackNetWork() {
        @Override
        public void callBack(int networkType) {
            switch (networkType){
                case ConnectivityManager.TYPE_WIFI:
                    if(!networkIsConnected){
                        refreshBtnImageView.setVisibility(View.VISIBLE);
                        ToastUtils.showToast(mContext,"Network is wifi!");
                    }
                    networkIsConnected = true;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if(!networkIsConnected){
                        refreshBtnImageView.setVisibility(View.VISIBLE);
                        ToastUtils.showToast(mContext,"Network is mobile!");
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
                    Log.e(TAG,"Network is disconnected!");
                    ToastUtils.showToast(mContext,"Network is disconnected!");
                    break;
            }
        }
    };

}
