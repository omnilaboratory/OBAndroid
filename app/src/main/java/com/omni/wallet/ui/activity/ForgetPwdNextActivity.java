package com.omni.wallet.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.CheckInputRules;
import com.omni.wallet.utils.KeyboardScrollView;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.obdMethods.WalletState;
import com.omni.wallet.utils.SecretAESOperator;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class ForgetPwdNextActivity extends AppBaseActivity {
    public final static String TAG = ForgetPwdNextActivity.class.getSimpleName();
    @BindView(R.id.password_input)
    public EditText mPwdEdit;
    @BindView(R.id.pass_switch)
    public ImageView mPwdEyeIv;
    @BindView(R.id.password_input_repeat)
    public EditText mConfirmPwdEdit;
    @BindView(R.id.pass_switch_repeat)
    public ImageView mConfirmPwdEyeIv;
    private boolean mCanClick = true;
    private boolean mConfirmCanClick = true;
    LoadingDialog mLoadingDialog;

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_forget_pwd_next;
//        return R.layout.activity_create_wallet_step_three;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mLoadingDialog = new LoadingDialog(mContext);
        PasswordFilter passwordFilter = new PasswordFilter();
        mPwdEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),passwordFilter});
        mConfirmPwdEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),passwordFilter});
        TextView.OnEditorActionListener listener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                clickForward();
            }
            return true;
        };
        mConfirmPwdEdit.setOnEditorActionListener(listener);
        LinearLayout pageContent = findViewById(R.id.forget_pwd_next);
        RelativeLayout mOutView = findViewById(R.id.form_unlock_content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, mOutView);
        runOnUiThread(this::subscribeState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * passwordInput 值变更
     * When the value of password input changed
     */

    @SuppressLint("SetTextI18n")
    @OnTextChanged(R.id.password_input)
    public void passwordChangeCheck(){
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkPwd(password);
        System.out.println(strongerPwd);
        View easy =findViewById(R.id.pass_strong_state_easy);
        View normal = findViewById(R.id.pass_strong_state_normal);
        View strong = findViewById(R.id.pass_strong_state_strong);
        ImageView pass_input_check = findViewById(R.id.pass_input_check);
        TextView pass_strong_text = findViewById(R.id.pass_strong_text);
        if (strongerPwd>0){
            pass_input_check.setVisibility(View.VISIBLE);
            pass_input_check.setImageResource(R.mipmap.icon_correct_green);
            switch (strongerPwd){
                case 1:
                    easy.setBackgroundColor(getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("EASY");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_red));
                    break;
                case 2:
                case 3:
                    easy.setBackgroundColor(getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(getResources().getColor(R.color.color_orange));
                    strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("NORMAL");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_orange));
                    break;
                case 4:
                    easy.setBackgroundColor(getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(getResources().getColor(R.color.color_orange));
                    strong.setBackgroundColor(getResources().getColor(R.color.color_green));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("STRONG");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_green));
                    break;
                default:
                    easy.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    normal.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("EMPTY");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_todo_grey));
                    break;
            }

        }else if(strongerPwd==0){
            pass_strong_text.setVisibility(View.VISIBLE);
            pass_input_check.setVisibility(View.INVISIBLE);
            easy.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            normal.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            pass_strong_text.setText("EMPTY");
            pass_strong_text.setTextColor(getResources().getColor(R.color.color_todo_grey));
        }else{
            pass_strong_text.setVisibility(View.VISIBLE);
            pass_input_check.setVisibility(View.INVISIBLE);
            easy.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            normal.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
            pass_input_check.setImageResource(R.mipmap.icon_wrong_red);
            pass_strong_text.setText("EMPTY");
            pass_strong_text.setTextColor(getResources().getColor(R.color.color_todo_grey));
        }
        passwordRepeatChangeCheck();
    }

    /**
     * passwordInputRepeat 值变更
     * When the value of password repeat input changed
     */
    @OnTextChanged(R.id.password_input_repeat)
    public void passwordRepeatChangeCheck(){
        TextView passwordView = findViewById(R.id.password_input);
        String passwordString = passwordView.getText().toString();
        TextView passwordViewRepeat = findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        ImageView passwordRepeatCheck = findViewById(R.id.pass_input_check_repeat);
        if(passwordRepeatString.equals("")){
            passwordRepeatCheck.setVisibility(View.INVISIBLE);
        }else{
            if(passwordString.equals(passwordRepeatString)){
                passwordRepeatCheck.setVisibility(View.VISIBLE);
                passwordRepeatCheck.setImageResource(R.mipmap.icon_correct_green);
            }else {
                passwordRepeatCheck.setVisibility(View.VISIBLE);
                passwordRepeatCheck.setImageResource(R.mipmap.icon_wrong_red);
            }
        }

    }

    /**
     * 点击眼睛上
     * click eye passInput
     */
    @OnClick(R.id.pass_switch)
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
     * 点击眼睛下
     * click eye passInputRepeat
     */
    @OnClick(R.id.pass_switch_repeat)
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
     * 点击Back
     * click back button
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        switchActivityFinish(ForgetPwdActivity.class);
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        mLoadingDialog.show();
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkPwd(password);
        TextView passwordViewRepeat = findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        if(strongerPwd>0 && passwordRepeatString.equals(password)){
            Log.d(TAG,"start change password");
            /**
             * 使用SharedPreferences 对象，在生成密码加密字符串时候将,密码的加密字符串备份到本地文件
             * Use SharedPreferences Class to backup password secret string to local file when create password secret string
             */
            String newPassMd5String = SecretAESOperator.getInstance().encrypt(password);
            String oldPassMd5String = User.getInstance().getPasswordMd5(mContext);
            Walletunlocker.ChangePasswordRequest changePasswordRequest = Walletunlocker.ChangePasswordRequest.newBuilder()
                    .setCurrentPassword(ByteString.copyFromUtf8(oldPassMd5String))
                    .setNewPassword(ByteString.copyFromUtf8(newPassMd5String))
                    .build();
            Obdmobile.changePassword(changePasswordRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        if(e.getMessage().equals("rpc error: code = Unknown desc = wallet already unlocked, WalletUnlocker service is no longer available")){
                            switchActivity(AccountLightningActivity.class);
                        }
                        PublicUtils.closeLoading(mLoadingDialog);
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if(bytes == null){
                        runOnUiThread(() -> PublicUtils.closeLoading(mLoadingDialog));
                        return;
                    }
                    try {
                        Walletunlocker.ChangePasswordResponse changePasswordResponse = Walletunlocker.ChangePasswordResponse.parseFrom(bytes);
                        String macaroon = changePasswordResponse.getAdminMacaroon().toString();
                        Log.d("macaroon",macaroon);
                        User.getInstance().setPasswordMd5(mContext,newPassMd5String);
                        User.getInstance().setMacaroonString(mContext,macaroon);

                    } catch (InvalidProtocolBufferException e) {
                        runOnUiThread(() -> PublicUtils.closeLoading(mLoadingDialog));
                        e.printStackTrace();
                    }
                    
                    
                }
            });
        }else{
            String checkSetPassWrongString = "";
            if(strongerPwd<0){
                checkSetPassWrongString = getResources().getString(R.string.toast_create_check_pass_wrong);
            }else if(!passwordRepeatString.equals(password)){
                checkSetPassWrongString =  getResources().getString(R.string.toast_create_check_pass_diff);
            }
            Toast checkSetPassToast = Toast.makeText(ForgetPwdNextActivity.this,checkSetPassWrongString,Toast.LENGTH_LONG);
            checkSetPassToast.setGravity(Gravity.TOP,0,30);
            checkSetPassToast.show();
            mLoadingDialog.dismiss();
        }

    }

    public void subscribeState() {
        WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
            if (walletState == 4) {
                runOnUiThread(() -> {
                    PublicUtils.closeLoading(mLoadingDialog);
                    switchActivity(AccountLightningActivity.class);
                });
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }

}
