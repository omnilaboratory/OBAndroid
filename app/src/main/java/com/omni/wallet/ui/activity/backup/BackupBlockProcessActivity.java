package com.omni.wallet.ui.activity.backup;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.utils.WalletGetInfo;
import com.omni.wallet.view.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupBlockProcessActivity extends AppBaseActivity {
    
    private Context ctx = BackupBlockProcessActivity.this;
    List<String> accountList = new ArrayList<>();
    LoadingDialog mLoadingDialog;

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
        newAddressToWallet();
        accountList = WalletGetInfo.getAccountList(ctx);
        for (int i = 0; i < accountList.size();i++){
            Log.e("AccountList",accountList.get(i));
        }
        
    }

    public void newAddressToWallet (){
        mLoadingDialog.show();
        Log.e("new address","start");
        LightningOuterClass.NewAddressRequest newAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder().setType(LightningOuterClass.AddressType.UNUSED_WITNESS_PUBKEY_HASH).build();
        Obdmobile.newAddress(newAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                mLoadingDialog.dismiss();
            }

            @Override
            public void onResponse(byte[] bytes) {
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

    @Override
    protected void initData() {

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
