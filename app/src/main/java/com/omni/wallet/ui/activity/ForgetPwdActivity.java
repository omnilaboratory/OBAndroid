package com.omni.wallet.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;

import butterknife.OnClick;

public class ForgetPwdActivity extends AppBaseActivity {
    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int titleId() {
        return R.string.forget_pwd;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    /**
     * click back button
     * 点击back
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * click next button
     * 点击next
     */
    @OnClick(R.id.btn_next)
    public void clickNext() {
        switchActivity(ForgetPwdNextActivity.class);
    }
}
