package com.omni.wallet.ui.activity.backup;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
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
    SharedPreferences blockData;
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
        blockData = ctx.getSharedPreferences("BlockData", MODE_PRIVATE);
        mLoadingDialog = new LoadingDialog(mContext);
        accountList = WalletGetInfo.getAccountList(ctx);
        for (int i = 0; i < accountList.size();i++){
            Log.e("AccountList",accountList.get(i));
        }
        BlockReaderUtil blockReaderUtil = new BlockReaderUtil(ctx);
        blockReaderUtil.getTotalBlockHeight();
    }
    public void initViewByData(){
        totalBlockHeight = blockData.getString("totalBlockHeight","0");
        currentBlockHeight = blockData.getString("currentBlockHeight","0");
        if(!(!totalBlockHeight.equals("0") && !currentBlockHeight.equals("0") &&Integer.parseInt(currentBlockHeight)>=Integer.parseInt(totalBlockHeight))){
            float totalHeight =  Float.parseFloat(totalBlockHeight);
            float currentHeight =  Float.parseFloat(currentBlockHeight);
            float percent = (currentHeight/totalHeight * 100);
            String percentString = String.format("%.2f",percent);
            
            syncedBlockNumView.setText(currentBlockHeight);
            commitNumSyncedView.setText(currentBlockHeight);
            syncBlockNumView.setText(totalBlockHeight);
            commitNumSyncView.setText(totalBlockHeight);
            try {
                Thread.sleep(5000);
                initViewByData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
    
    

    public void newAddressToWallet (){
        mLoadingDialog.show();
        Log.e("new address","start");
        LightningOuterClass.NewAddressRequest newAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder().setTypeValue(2).build();
        Obdmobile.newAddress(newAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Log.e("newAddressError",e.toString());
                mLoadingDialog.dismiss();
            }

            @Override
            public void onResponse(byte[] bytes) {
                Log.e("newAddressS","success");
                if(bytes == null){
                    mLoadingDialog.dismiss();
                    return;
                }
                try {
                    Log.e("new address","success");
                    LightningOuterClass.NewAddressResponse newAddressResponse = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    String address = newAddressResponse.getAddress();
                    Log.e("new address",address);
                    SharedPreferences addressList = ctx.getSharedPreferences("Account", MODE_PRIVATE);
                    SharedPreferences.Editor editor = addressList.edit();
                    editor.putString("accountList",address);
                    editor.commit();
                    mLoadingDialog.dismiss();
                    Log.e("new address","will jump");

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void initData() {
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

    }

    /**
     *点击Start
     * click Start
     */

    @OnClick(R.id.btn_start)
    public void clickStart(){
        switchActivity(AccountLightningActivity.class);
    }
}
