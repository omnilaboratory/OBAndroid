package com.omni.wallet.ui.activity.recoverwallet;

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
import com.omni.wallet.ui.activity.backup.BackupBlockProcessActivity;
import com.omni.wallet.utils.CheckInputRules;
import com.omni.wallet.utils.KeyboardScrollView;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.SecretAESOperator;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class RecoverWalletStepTwoActivity extends AppBaseActivity {
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
        return R.layout.activity_recover_wallet_step_two;
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
        LinearLayout pageContent = findViewById(R.id.recover_wallet_step_two);
        RelativeLayout mOutView = findViewById(R.id.form_unlock_content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, mOutView);
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

    @OnTextChanged(R.id.password_input)
    public void passwordChangeCheck(){
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkPwd(password);
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
                    pass_strong_text.setText("STORNG");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_green));
                    break;
                default:
                    easy.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    normal.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    strong.setBackgroundColor(getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("");
                    pass_strong_text.setTextColor(getResources().getColor(R.color.color_10_white));
                    break;
            }

        }else if(strongerPwd==0){
            pass_strong_text.setVisibility(View.INVISIBLE);
            pass_input_check.setVisibility(View.INVISIBLE);
            pass_strong_text.setText("");
            pass_strong_text.setTextColor(getResources().getColor(R.color.color_10_white));
        }else{
            pass_strong_text.setVisibility(View.INVISIBLE);
            pass_input_check.setVisibility(View.VISIBLE);
            pass_input_check.setImageResource(R.mipmap.icon_wrong_red);
            pass_strong_text.setText("");
            pass_strong_text.setTextColor(getResources().getColor(R.color.color_10_white));
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
        if(passwordRepeatString==""){
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
        finish();
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkPwd(password);
        TextView passwordViewRepeat = findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        if(strongerPwd>0 && passwordRepeatString.equals(password)){
            mLoadingDialog.show();
            String md5String = SecretAESOperator.getInstance().encrypt(password);
            /**
             * 使用SharedPreferences 对象，在生成密码md5字符串时候将,密码的md5字符串备份到本地文件
             * Use SharedPreferences Class to backup password md5 string to local file when create password md5 string
             */
            
            String seedsString = User.getInstance().getRecoverySeedString(mContext);
            String[] seedList = seedsString.split(" ");
            
            Walletunlocker.InitWalletRequest.Builder initWalletRequestBuilder = Walletunlocker.InitWalletRequest.newBuilder();
            initWalletRequestBuilder.addAllCipherSeedMnemonic(Arrays.asList(seedList));
//
//            for (int i =0;i<seedList.length;i++){
//                initWalletRequestBuilder.addCipherSeedMnemonic(seedList[i]);
//                String mnemonicString = initWalletRequestBuilder.getCipherSeedMnemonic(i);
//                Log.e("mnemonicString",mnemonicString);
//            }
            initWalletRequestBuilder.setWalletPassword(ByteString.copyFromUtf8(md5String));
            initWalletRequestBuilder.setRecoveryWindow(2500);
            Walletunlocker.InitWalletRequest initWalletRequest = initWalletRequestBuilder.build();
            Obdmobile.initWallet(initWalletRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    Log.e("initWallet Error",e.toString());
                    if (e.getMessage().contains("wallet already exists")){
                        switchActivity(BackupBlockProcessActivity.class);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.dismiss();
                        }});
                    e.printStackTrace();


                }
                @Override
                public void onResponse(byte[] bytes) {
                    if (bytes == null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.dismiss();
                            }});
                        return;
                    }
                    try {
                        Walletunlocker.InitWalletResponse initWalletResponse = Walletunlocker.InitWalletResponse.parseFrom(bytes);
                        ByteString macaroon = initWalletResponse.getAdminMacaroon();
                        User.getInstance().setMacaroonString(mContext,macaroon.toStringUtf8());
                        User.getInstance().setInitWalletType(mContext,"recoveryStepTwo");
                        User.getInstance().setCreated(mContext,true);
                        User.getInstance().setSeedChecked(mContext,true);
                        User.getInstance().setSeedString(mContext,seedsString);
                        User.getInstance().setPasswordMd5(mContext,md5String);
                        switchActivity(BackupBlockProcessActivity.class);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                        mLoadingDialog.dismiss();
                    }
                }
            });

        }else{
            String checkSetPassWrongString = "";
            if(strongerPwd<0){
                checkSetPassWrongString = getResources().getString(R.string.toast_create_check_pass_wrong);
            }else if(!passwordRepeatString.equals(password)){
                checkSetPassWrongString =  getResources().getString(R.string.toast_create_check_pass_diff);;
            }
            Toast checkSetPassToast = Toast.makeText(RecoverWalletStepTwoActivity.this,checkSetPassWrongString,Toast.LENGTH_LONG);
            checkSetPassToast.setGravity(Gravity.TOP,0,30);
            checkSetPassToast.show();
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
