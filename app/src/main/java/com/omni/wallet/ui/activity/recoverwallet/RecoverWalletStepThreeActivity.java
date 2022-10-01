package com.omni.wallet.ui.activity.recoverwallet;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.ChooseBackupTypeActivity;

import butterknife.OnClick;

public class RecoverWalletStepThreeActivity extends AppBaseActivity {

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int titleId() {
        return R.string.recover_wallet;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_recover_wallet_step_three;
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
