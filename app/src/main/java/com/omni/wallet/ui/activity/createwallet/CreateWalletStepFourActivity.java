package com.omni.wallet.ui.activity.createwallet;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.ChooseBackupTypeActivity;

import butterknife.OnClick;

public class CreateWalletStepFourActivity extends AppBaseActivity {

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int titleId() {
        return R.string.create_wallet;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_create_wallet_step_four;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 点击Back
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击Start
     */
    @OnClick(R.id.btn_start)
    public void clickStart() {
        switchActivity(ChooseBackupTypeActivity.class);
    }
}
