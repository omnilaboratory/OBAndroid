package com.omni.wallet.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.CheckInputRules;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.utils.WalletState;

import java.util.ArrayList;
import android.os.Handler;
import java.util.logging.LogRecord;

import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class ForgetPwdNextDialog {
    public static final String TAG = ForgetPwdNextDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCanClick = true;
    private boolean mConfirmCanClick = true;
    private AlertDialog mForgetPwdDialog;
    private AlertDialog mUnlockDialog;
    private LoadingDialog mLoadingDialog;
    Handler mHandler = new Handler();


    public ForgetPwdNextDialog(Context context, AlertDialog forgetPwdDialog, AlertDialog unlockDialog){
        this.mContext = context;
        this.mForgetPwdDialog = forgetPwdDialog;
        this.mUnlockDialog = unlockDialog;
        mLoadingDialog = new LoadingDialog(context);
    }

    public void show(){
        if (mAlertDialog == null){
            Log.e(TAG, "show" );
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_forget_password_next)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }

        new Thread(()->{
            subscribeState();
        }).run();

        EditText passwordInputEditText = mAlertDialog.findViewById(R.id.password_input);
        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordChangeCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordChangeCheck();
            }
        });

        EditText passwordInputRepeatEditText = mAlertDialog.findViewById(R.id.password_input_repeat);
        passwordInputRepeatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordRepeatChangeCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordRepeatChangeCheck();
            }
        });

        mAlertDialog.findViewById(R.id.pass_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPwdEye();
            }
        });

        mAlertDialog.findViewById(R.id.pass_switch_repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickConfirmPwdEye();
            }
        });

        mAlertDialog.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBack();
            }
        });

        mAlertDialog.findViewById(R.id.btn_forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickForward();
            }
        });

        mAlertDialog.show();

    }

    public void passwordChangeCheck(){
        EditText mPwdEdit =  mAlertDialog.findViewById(R.id.password_input);
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkePwd(password);
        System.out.println(strongerPwd);
        View easy = mAlertDialog.findViewById(R.id.pass_strong_state_easy);
        View normal = mAlertDialog.findViewById(R.id.pass_strong_state_normal);
        View strong = mAlertDialog.findViewById(R.id.pass_strong_state_strong);
        ImageView pass_input_check = mAlertDialog.findViewById(R.id.pass_input_check);
        TextView pass_strong_text = mAlertDialog.findViewById(R.id.pass_strong_text);
        if (strongerPwd>0){
            pass_input_check.setVisibility(View.VISIBLE);
            pass_input_check.setImageResource(R.mipmap.icon_correct_green);
            switch (strongerPwd){
                case 1:
                    easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("EASY");
                    pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_red));
                    break;
                case 2:
                case 3:
                    easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_orange));
                    strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("NORMAL");
                    pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_orange));
                    break;
                case 4:
                    easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_red));
                    normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_orange));
                    strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_green));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("STRONG");
                    pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_green));
                    break;
                default:
                    easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    pass_strong_text.setVisibility(View.VISIBLE);
                    pass_strong_text.setText("EMPTY");
                    pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_todo_grey));
                    break;
            }

        }else if(strongerPwd==0){
            pass_strong_text.setVisibility(View.VISIBLE);
            pass_input_check.setVisibility(View.INVISIBLE);
            easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            pass_strong_text.setText("EMPTY");
            pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_todo_grey));
        }else{
            pass_strong_text.setVisibility(View.VISIBLE);
            pass_input_check.setVisibility(View.INVISIBLE);
            easy.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            normal.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            strong.setBackgroundColor(mContext.getResources().getColor(R.color.color_todo_grey));
            pass_input_check.setImageResource(R.mipmap.icon_wrong_red);
            pass_strong_text.setText("EMPTY");
            pass_strong_text.setTextColor(mContext.getResources().getColor(R.color.color_todo_grey));
        }
    }

    public void passwordRepeatChangeCheck(){
        TextView passwordView = mAlertDialog.findViewById(R.id.password_input);
        String passwordString = passwordView.getText().toString();
        TextView passwordViewRepeat = mAlertDialog.findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        ImageView passwordRepeatCheck = mAlertDialog.findViewById(R.id.pass_input_check_repeat);
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

    public void clickPwdEye() {
        ImageView mPwdEyeIv = mAlertDialog.findViewById(R.id.pass_switch);
        EditText mPwdEdit = mAlertDialog.findViewById(R.id.password_input);
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

    public void clickConfirmPwdEye() {
        ImageView mConfirmPwdEyeIv = mAlertDialog.findViewById(R.id.pass_switch_repeat);
        EditText mConfirmPwdEdit = mAlertDialog.findViewById(R.id.password_input_repeat);
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

    public void clickBack() {
        release();
    }

    public void clickForward() {
        mLoadingDialog.show();
        EditText mPwdEdit = mAlertDialog.findViewById(R.id.password_input);
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkePwd(password);
        TextView passwordViewRepeat = mAlertDialog.findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        if(strongerPwd>0 && passwordRepeatString.equals(password)){
            Log.e(TAG,"start change password");
            /**
             * 使用SharedPreferences 对象，在生成密码md5字符串时候将,密码的md5字符串备份到本地文件
             * Use SharedPreferences Class to backup password md5 string to local file when create password md5 string
             */
            String newPassMd5String = Md5Util.getMD5Str(password);
            String oldPassMd5String = User.getInstance().getPasswordMd5(mContext);
            Walletunlocker.ChangePasswordRequest changePasswordRequest = Walletunlocker.ChangePasswordRequest.newBuilder()
                    .setCurrentPassword(ByteString.copyFromUtf8(oldPassMd5String))
                    .setNewPassword(ByteString.copyFromUtf8(newPassMd5String))
                    .build();
            Obdmobile.changePassword(changePasswordRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    mHandler.post(()->{
                        mLoadingDialog.dismiss();
                    });
                    String errorMessage = e.getMessage();
                    Log.e(TAG+"onError: ", errorMessage);
                    if (errorMessage.equals("rpc error: code = Unknown desc = wallet already unlocked, WalletUnlocker service is no longer available")){
                        release();
                        if (mForgetPwdDialog!=null){
                            mForgetPwdDialog.dismiss();
                            mForgetPwdDialog = null;
                        }

                        if (mUnlockDialog!=null){
                            mUnlockDialog.dismiss();
                            mUnlockDialog = null;
                        }
                    }
                    e.printStackTrace();
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if(bytes == null){
                        mHandler.post(()->{
                            mLoadingDialog.dismiss();
                        });
                        return;
                    }
                    try {
                        Walletunlocker.ChangePasswordResponse changePasswordResponse = Walletunlocker.ChangePasswordResponse.parseFrom(bytes);
                        String macaroon = changePasswordResponse.getAdminMacaroon().toString();
                        Log.e("macaroon",macaroon);
                        User.getInstance().setPasswordMd5(mContext,newPassMd5String);
                        User.getInstance().setMacaroonString(mContext,macaroon);
                        mHandler.post(()->{
                            mLoadingDialog.dismiss();
                        });
                    } catch (InvalidProtocolBufferException e) {
                        mHandler.post(()->{
                            mLoadingDialog.dismiss();
                        });
                        e.printStackTrace();
                    }


                }
            });
        }else{
            String checkSetPassWrongString = "";
            if(strongerPwd<0){
                checkSetPassWrongString = mContext.getResources().getString(R.string.toast_create_check_pass_wrong);
            }else if(!passwordRepeatString.equals(password)){
                checkSetPassWrongString =  mContext.getResources().getString(R.string.toast_create_check_pass_diff);;
            }
            Toast checkSetPassToast = Toast.makeText(mContext,checkSetPassWrongString,Toast.LENGTH_LONG);
            checkSetPassToast.setGravity(Gravity.TOP,0,30);
            checkSetPassToast.show();
        }

    }

    public void subscribeState() {
        WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
            switch (walletState){
                case 4:
                    release();
                    if (mForgetPwdDialog!=null){
                        mForgetPwdDialog.dismiss();
                        mForgetPwdDialog = null;
                    }

                    if (mUnlockDialog!=null){
                        mUnlockDialog.dismiss();
                        mUnlockDialog = null;
                    }
                    break;
                default:
                    break;
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

    public void release(){
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
