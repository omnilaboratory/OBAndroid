package com.omni.testnet.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.testnet.R;
import com.omni.testnet.base.AppBaseActivity;
import com.omni.testnet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.testnet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;

import butterknife.OnClick;

public class CreateActivity extends AppBaseActivity {

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_create;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    /**
     * click new testnet option
     * 点击Create New Wallet
     */
    @OnClick(R.id.layout_create_new_wallet)
    public void clickCreateNewWallet() {
        switchActivity(CreateWalletStepOneActivity.class);
    }

    /**
     * click recover your testnet option
     * 点击Recover Your Wallet
     */
    @OnClick(R.id.layout_recover_your_wallet)
    public void clickRecoverYourWallet() {
        switchActivity(RecoverWalletStepOneActivity.class);
    }
}
