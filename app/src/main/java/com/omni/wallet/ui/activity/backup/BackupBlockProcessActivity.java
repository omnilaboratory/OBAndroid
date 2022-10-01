package com.omni.wallet.ui.activity.backup;


import android.content.Context;
//import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.AccountLightningActivity;
//import com.omni.wallet.ui.activity.createwallet.CreateWalletStepTwoActivity;

//import butterknife.BindView;
import butterknife.OnClick;

public class BackupBlockProcessActivity extends AppBaseActivity {
    /*@BindView(R.id.qr_image)
    public ImageView qrImage;
    @BindView(R.id.qr_address)
    public TextView qrText;
    @BindView(R.id.sync_percent)
    public TextView syncPercentText;
    @BindView(R.id.process_inner)
    public RelativeLayout processInnerWidget;
    @BindView(R.id.block_num_synced)
    public TextView blockNumSyncedText;
    @BindView(R.id.block_num_sync)
    public TextView blockNumSyncText;
    @BindView(R.id.commit_num_synced)
    public TextView commitNumSynced;
    @BindView(R.id.commit_num_sync)
    public TextView commitNumSync;*/

//    Context ctx = BackupBlockProcessActivity.this;

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
    }

    @Override
    protected void initData() {

    }


   /**
    *点击Copy address
    */

    @OnClick(R.id.btn_copy_addresss)
    public void clickCopyAddress(){

    }

    /**
     *点击Start
     */

    @OnClick(R.id.btn_start)
    public void clickStart(){
        switchActivity(AccountLightningActivity.class);
    }
}
