package com.omni.wallet.ui.activity.backup;


import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.AccountLightningActivity;

import butterknife.OnClick;

public class BackupBlockProcessActivity extends AppBaseActivity {

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
