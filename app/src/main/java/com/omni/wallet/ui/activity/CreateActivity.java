package com.omni.wallet.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;

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
     * 点击Create New Wallet
     */
    @OnClick(R.id.layout_create_new_wallet)
    public void clickCreateNewWallet() {
        switchActivity(CreateWalletStepOneActivity.class);
    }

    /**
     * 点击Recover Your Wallet
     */
    @OnClick(R.id.layout_recover_your_wallet)
    public void clickRecoverYourWallet() {
        switchActivity(RecoverWalletStepOneActivity.class);
    }
}
