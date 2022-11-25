package com.omni.wallet.ui.activity.backup;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.utils.BlockReaderUtil;
import com.omni.wallet.utils.WalletGetInfo;
import com.omni.wallet.view.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import lnrpc.Stateservice;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupBlockProcessActivity extends AppBaseActivity {
    
    private Context ctx = BackupBlockProcessActivity.this;
    
    List<String> accountList = new ArrayList<>();
    LoadingDialog mLoadingDialog;
    String totalBlockHeight = "";
    String currentBlockHeight = "";
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
    @BindView(R.id.my_process_outer)
    RelativeLayout rvMyProcessOuter;
    @BindView(R.id.qr_address)
    TextView qrAddressTv;
    @BindView(R.id.qr_image)
    ImageView qrAddressIv;
    
    String newCreatedAddress;
    

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
        for (int i = 0; i < accountList.size();i++){
            Log.e("AccountList",accountList.get(i));
        }
        BlockReaderUtil blockReaderUtil = new BlockReaderUtil(ctx);
        blockReaderUtil.getTotalBlockHeight();
    }
    @SuppressLint("LongLogTag")
    public void initViewByData(){
        SharedPreferences blockData = ctx.getSharedPreferences("BlockData", MODE_PRIVATE);
        totalBlockHeight = blockData.getString("totalBlockHeight","0");
        currentBlockHeight = blockData.getString("currentBlockHeight","0");
        double totalHeight =  Double.parseDouble(totalBlockHeight);
        double currentHeight =  Double.parseDouble(currentBlockHeight);
        if(currentHeight>totalHeight){
            currentBlockHeight = totalBlockHeight;
        }
        double percent = (currentHeight/totalHeight * 100);
        double totalWidth =  rvMyProcessOuter.getWidth();
        int innerWidth =0;
        int innerHeight = (int)rvMyProcessOuter.getHeight()-2;

        String percentString = String.format("%.2f",percent);
        if(Double.isNaN(percent)){
            percentString = "0";
            percent = 0.00d;
            innerWidth = 0;
        }else {
            innerWidth = (int) (totalWidth*percent/100);
        }
        Log.e("innerWidth,innerHeight",innerWidth+","+innerHeight);
        if(!syncPercentView.getText().equals(percentString)){
            syncPercentView.setText(percentString + "%");
            RelativeLayout.LayoutParams rlInnerParam = new RelativeLayout.LayoutParams(innerWidth,innerHeight);
            rvProcessInner.setLayoutParams(rlInnerParam);
        }
        if(!syncedBlockNumView.getText().equals(currentBlockHeight)){
            syncedBlockNumView.setText(currentBlockHeight);
            commitNumSyncedView.setText(currentBlockHeight);
        }
        if(!syncBlockNumView.getText().equals(totalBlockHeight)){
            syncBlockNumView.setText(totalBlockHeight);
            commitNumSyncView.setText(totalBlockHeight);
        }
        if(!(!totalBlockHeight.equals("0") && !currentBlockHeight.equals("0") &&Integer.parseInt(currentBlockHeight)>=Integer.parseInt(totalBlockHeight))){
            
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            initViewByData();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                    }
                });
            
        }else{
            newAddressToWallet();
            Log.e("-----------------------let do something------------","koko");
        }
    }
    
    

    public void newAddressToWallet (){
        
        Log.e("new address","start");
        LightningOuterClass.NewAddressRequest newAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder().setTypeValue(2).build();
        Obdmobile.newAddress(newAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Log.e("newAddressError",e.toString());
                
            }

            @Override
            public void onResponse(byte[] bytes) {
                Log.e("newAddressS","success");
                if(bytes == null){
                    
                    return;
                }
                try {
                    Log.e("new address","success");
                    LightningOuterClass.NewAddressResponse newAddressResponse = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    String address = newAddressResponse.getAddress();
                    Log.e("new address",address);
                    newCreatedAddress = address;
                    qrAddressTv.setText(address);
                    Bitmap mQRBitmap = CodeUtils.createQRCode(address, DisplayUtil.dp2px(mContext, 100));
                    qrAddressIv.setImageBitmap(mQRBitmap);
                    SharedPreferences addressList = ctx.getSharedPreferences("Account", MODE_PRIVATE);
                    SharedPreferences.Editor editor = addressList.edit();
                    editor.putString("accountList",address);
                    editor.commit();
                    
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initViewByData();
            }
        }).start();
        
    }

    private void checkWalletAlready () throws InterruptedException {
        Thread.sleep(5000);
        Stateservice.GetStateRequest getStateRequest = Stateservice.GetStateRequest.newBuilder().build();
        Obdmobile.getState(getStateRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Log.e("checkWalletReady","Error");
                Log.e("checkWalletReady",e.toString());
                try {
                    checkWalletAlready ();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    try {
                        checkWalletAlready ();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Stateservice.GetStateResponse getStateResponse = Stateservice.GetStateResponse.parseFrom(bytes);
                    int stateValue = getStateResponse.getStateValue();
                    if(stateValue ==4){
                        Log.e("checkWalletReady", String.valueOf(stateValue));
                        runOnUiThread(()->mLoadingDialog.dismiss());
                        
                    }else{
                        Log.e("checkWalletReady", String.valueOf(stateValue));
                        Log.e("checkWalletReady","No");
                        checkWalletAlready ();
                    }
                } catch (InvalidProtocolBufferException | InterruptedException e) {
                    e.printStackTrace();
                    Log.e("checkWalletReady","response error");
                    try {
                        checkWalletAlready ();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
    }

 


    /**
    *点击Copy address
    * click Copy Address
    */

    @OnClick(R.id.btn_copy_addresss)
    public void clickCopyAddress(){
        if(newCreatedAddress==null){
            String toastMsg = "Block is syncing now,please wait a moment!";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }else{
            String toastMsg = "Address is copied.";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }
        
    }

    /**
     *点击Start
     * click Start
     */

    @OnClick(R.id.btn_start)
    public void clickStart(){
        if(newCreatedAddress==null){
            String toastMsg = "Block is syncing now,please wait a moment!";
            Toast copySuccessToast = Toast.makeText(ctx,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }else{
            switchActivity(AccountLightningActivity.class);
        }
        
    }
}
