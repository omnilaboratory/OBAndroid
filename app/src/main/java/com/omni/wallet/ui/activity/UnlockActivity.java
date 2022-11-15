package com.omni.wallet.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.utils.Md5Util;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class UnlockActivity extends AppBaseActivity {
    Context ctx = UnlockActivity.this;
    String localPass ="";
    String localSeed = "";

    @BindView(R.id.password_input)
    public EditText mPwdEdit;
    @BindView(R.id.pass_switch)
    public ImageView mPwdEyeIv;
    private boolean mCanClick = true;
    @BindView(R.id.bottom_btn_group)
    public RelativeLayout bottomBtnGroup;


    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

//    @Override
//    protected int titleId() {
//        return R.string.unlock;
//    }

    @Override
    protected int getContentView() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        /**
         * 获取本地pass,用于后续判断输入的密码是否正确。获取本地seed，判断是否显示bottom_btn_group,如果有则隐藏如果没有则显示，为方便测试暂时隐藏.
         *Obtain the local pass to determine whether the password entered is correct. Get the local seed and determine whether to display bottom_ btn_ Group. If there is, it will be hidden. If not, it will be displayed. It is temporarily hidden for the convenience of testing
         */
        SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
        localPass = secretData.getString("password","");
        localSeed = secretData.getString("seeds","");
        if (localSeed.isEmpty()){
            bottomBtnGroup.setVisibility(View.VISIBLE);
        }else{
            bottomBtnGroup.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * click eye icon
     * 点击眼睛
     */
    @OnClick(R.id.pass_switch)
    public void clickPwdEye() {
        if (mCanClick) {
            mCanClick = false;
            mPwdEyeIv.setImageResource(R.mipmap.icon_eye_open);
            //显示密码(show password)
            mPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mCanClick = true;
            mPwdEyeIv.setImageResource(R.mipmap.icon_eye_close);
            //隐藏密码(hide password)
            mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @OnClick(R.id.tv_pass_text)
    public void clickToForgetPassword(){
        switchActivity(ForgetPwdActivity.class);
    }

    /**
     * click unlock button
     * 点击Unlock
     */
    @OnClick(R.id.btn_unlock)
    public void clickUnlock() {
        String passwordString = mPwdEdit.getText().toString();
        String passMd5 = Md5Util.getMD5Str(passwordString);
        Log.e("unlock password",passMd5);
        Log.e("unlock localPass",localPass);
        if(localPass.equals(passMd5)){
            Walletunlocker.UnlockWalletRequest unlockWalletRequest =  Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
            Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    Log.e("unlock failed","unlock failed");
                    e.printStackTrace();

                }

                @Override
                public void onResponse(byte[] bytes) {
                    switchActivity(AccountLightningActivity.class);
                    
                }
            });

//            
        }else{
            String toastString = getResources().getString(R.string.toast_unlock_error);
            Toast checkPassToast = Toast.makeText(UnlockActivity.this,toastString,Toast.LENGTH_LONG);
            checkPassToast.setGravity(Gravity.TOP,0,20);
            checkPassToast.show();
        }
    }

    /**
     * click create button
     * 点击Create
     */
    @OnClick(R.id.btn_create)
    public void clickCreate() {
        switchActivity(CreateWalletStepOneActivity.class);
    }

    /**
     * click recover button
     * 点击Recover
     */
    @OnClick(R.id.btn_recover)
    public void clickRecover() {
        switchActivity(RecoverWalletStepOneActivity.class);
    }

    /**
     * click forgot password
     * 点击忘记密码
     */
    @OnClick(R.id.btv_forget_button)
    public void clickForgetPass(){
        switchActivity(ForgetPwdActivity.class);
    }
}
