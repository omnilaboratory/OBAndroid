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
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.NetworkChangeReceiver;
import com.omni.wallet.utils.ObdLogFileObserver;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.utils.WalletState;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
    ConstantInOB constantInOB = null;
    ConnectivityManager connectivityManager = null;
    boolean networkIsConnected = true;
    NetworkChangeReceiver networkChangeReceiver = null;
    String initWalletType = "";
    long totalBlock = 0;

    @Override
    protected Drawable getWindowBackground(){
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_backup_block_process;
    }

    WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
        Log.e(TAG,User.getInstance().getWalletAddress(mContext));
        switch (walletState){
            case 1:
                unlockWallet();
                break;
            case 4:
                if(User.getInstance().getWalletAddress(mContext).isEmpty()){
                    newAddressToWallet();
                }
                break;
            default:
                break;

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        User user = User.getInstance();
        constantInOB = new ConstantInOB(mContext);
        String fileLocal = constantInOB.getRegTestLogPath();
        obdLogFileObserver = new ObdLogFileObserver(fileLocal,ctx);
        blockData = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        isSynced = user.getSynced(mContext);
        isCreated = user.getCreated(mContext);
        walletAddress = user.getWalletAddress(mContext);
        mLoadingDialog = new LoadingDialog(mContext);
        String passwordMd5 = user.getPasswordMd5(mContext);
        connectivityManager = getSystemService(ConnectivityManager.class);
        Log.e("password",passwordMd5);
        initWalletType = user.getInitWalletType(mContext);
        totalBlock = user.getTotalBlock(mContext);
        commitNumSyncView.setText(String.valueOf(totalBlock));
        syncBlockNumView.setText(String.valueOf(totalBlock));
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
        runOnUiThread(()->{
            obdLogFileObserver.startWatching();
        });
        runOnUiThread(()->{
            blockData.registerOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
        });
    }
    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        PublicUtils.closeLoading(mLoadingDialog);
        unregisterReceiver(networkChangeReceiver);
        blockData.unregisterOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
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
    private void updateSyncDataView(long syncedHeight){
        Log.e(TAG,"update_synced_Height");
        double totalHeight =  totalBlock;
        double currentHeight =  syncedHeight;
        if(syncedHeight>totalBlock){
            syncedHeight = totalBlock;
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
        syncedBlockNumView.setText(Long.toString(syncedHeight));
        commitNumSyncedView.setText(Long.toString(syncedHeight));
        
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
        Log.e(TAG,"click start");
        if(newCreatedAddress.isEmpty()){
            String toastMsg = "Block is syncing now,please wait a moment!";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }else{
//            switchActivityFinish(AccountLightningActivity.class);
            Log.e(TAG,"click start initWalletType:" + initWalletType);
            if(initWalletType.equals("initialed")){
                switchActivityFinish(AccountLightningActivity.class);
            }else if(initWalletType.equals("toBeRestoreChannel")){
                switchActivity(RestoreChannelActivity.class);
            }
        }

    }


    public void newAddressToWallet (){
        Log.e(TAG,"new Address count");
        String createType = User.getInstance().getInitWalletType(mContext);
        if(createType.equals("recoveryStepTwo")){
            getOldAddress();

        }else{
            newAddress();

        }

    }

    public void newAddress(){

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
                        User.getInstance().setWalletAddress(mContext,address);
                        updateSyncDataView(totalBlock);
                        if(initWalletType.equals("recoveryStepTwo")){
                            User.getInstance().setInitWalletType(mContext,"toBeRestoreChannel");
                            initWalletType = "toBeRestoreChannel";
                        }else{
                            User.getInstance().setInitWalletType(mContext,"initialed");
                            initWalletType = "initialed";
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

    public void getOldAddress(){
        Log.e(TAG, "getOldAddress: ");
        LightningOuterClass.ListAddressesRequest listAddressesRequest = LightningOuterClass.ListAddressesRequest.newBuilder().build();
        Obdmobile.oB_ListAddresses(listAddressesRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("getAddress Error",e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if(bytes == null){
                    Log.e(TAG, "getOldAddress: no address");
                    newAddress();
                }
                try {
                    LightningOuterClass.ListAddressesResponse listAddressesResponse = LightningOuterClass.ListAddressesResponse.parseFrom(bytes);
                    String address = listAddressesResponse.getItems(0);
                    newCreatedAddress = address;
                    Bitmap mQRBitmap = CodeUtils.createQRCode(address, DisplayUtil.dp2px(mContext, 100));
                    runOnUiThread(() -> {
                        qrAddressTv.setText(address);
                        qrAddressIv.setImageBitmap(mQRBitmap);
                        obdLogFileObserver.stopWatching();
                        User.getInstance().setWalletAddress(mContext,address);
                        updateSyncDataView(totalBlock);
                        User.getInstance().setInitWalletType(mContext,"toBeRestoreChannel");
                        initWalletType = "toBeRestoreChannel";
                    });

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
        Obdmobile.start("--lnddir=" + getApplicationContext().getExternalCacheDir() + ConstantInOB.usingNeutrinoConfig, new Callback() {
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

    public void getExistAddress(){
        LightningOuterClass.ListAddressesRequest listAddressesRequest = LightningOuterClass.ListAddressesRequest.newBuilder().build();
        Obdmobile.oB_ListAddresses(listAddressesRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("getAddress Error",e.toString());
                e.printStackTrace();
                mLoadingDialog.dismiss();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if(bytes == null){
                    return;
                }
                try {
                    LightningOuterClass.ListAddressesResponse listAddressesResponse = LightningOuterClass.ListAddressesResponse.parseFrom(bytes);
                    String address = listAddressesResponse.getItems(0);
                    Log.e("TAG",address);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
