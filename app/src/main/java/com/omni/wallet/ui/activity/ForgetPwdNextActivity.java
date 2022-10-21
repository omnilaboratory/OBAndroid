package com.omni.wallet.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgetPwdNextActivity extends AppBaseActivity {
    @BindView(R.id.edit_pwd)
    public EditText mPwdEdit;
    @BindView(R.id.iv_pwd_eye)
    public ImageView mPwdEyeIv;
    @BindView(R.id.edit_confirm_pwd)
    public EditText mConfirmPwdEdit;
    @BindView(R.id.iv_confirm_pwd_eye)
    public ImageView mConfirmPwdEyeIv;
    private boolean mCanClick = true;
    private boolean mConfirmCanClick = true;

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
        return R.layout.activity_forget_pwd_next;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    /**
     * click the eye icon of password input
     * 点击眼睛上
     */
    @OnClick(R.id.layout_pwd_eye)
    public void clickPwdEye() {
        if (mCanClick) {
            mCanClick = false;
            mPwdEyeIv.setImageResource(R.mipmap.icon_eye_open);
            //显示密码
            mPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mCanClick = true;
            mPwdEyeIv.setImageResource(R.mipmap.icon_eye_close);
            //隐藏密码
            mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * click the eye icon of password repeat input
     * 点击眼睛下
     */
    @OnClick(R.id.layout_confirm_pwd_eye)
    public void clickConfirmPwdEye() {
        if (mConfirmCanClick) {
            mConfirmCanClick = false;
            mConfirmPwdEyeIv.setImageResource(R.mipmap.icon_eye_open);
            //显示密码
            mConfirmPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mConfirmCanClick = true;
            mConfirmPwdEyeIv.setImageResource(R.mipmap.icon_eye_close);
            //隐藏密码
            mConfirmPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * click unlock without password button
     * 点击Unlock without Password
     */
    @OnClick(R.id.btn_unlock_without_password)
    public void clickBtnUnlockWithoutWPassword() {

    }

    /**
     * click Reset Password & Unlock button
     * 点击Reset Password & Unlock
     */
    @OnClick(R.id.btn_reset_password_unlock)
    public void clickBtnResetPasswordUnlock() {

    }
}
