package com.omni.wallet.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
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

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.CheckInputRules;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.SecretAESOperator;

public class ForgetPwdNextDialog {
    public static final String TAG = ForgetPwdNextDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCanClick = false;
    private boolean mConfirmCanClick = false;
    private AlertDialog mForgetPwdDialog;
    private AlertDialog mUnlockDialog;
    private LoadingDialog mLoadingDialog;


    ForgetPwdNextDialog(Context context, AlertDialog forgetPwdDialog, AlertDialog unlockDialog){
        this.mContext = context;
        this.mForgetPwdDialog = forgetPwdDialog;
        this.mUnlockDialog = unlockDialog;
        mLoadingDialog = new LoadingDialog(context);
    }

    public void show(){
        if (mAlertDialog == null){
            Log.d(TAG, "show" );
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_forget_password_next)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }

        EditText passwordInputEditText = mAlertDialog.findViewById(R.id.password_input);
        PasswordFilter passwordFilter = new PasswordFilter();
        passwordInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),passwordFilter});

        /*LinearLayout pageContent = mAlertDialog.findViewById(R.id.layout_parent);
        LinearLayout unlockContain = mAlertDialog.findViewById(R.id.form_unlock_content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, unlockContain);*/

        passwordInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
        @SuppressLint("CutPasteId") EditText mPwdEdit =  mAlertDialog.findViewById(R.id.password_input);
        @SuppressLint("CutPasteId") EditText mConfirmPwdEdit = mAlertDialog.findViewById(R.id.password_input_repeat);
        mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mConfirmPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mAlertDialog.findViewById(R.id.pass_switch).setOnClickListener(v -> clickPwdEye());

        mAlertDialog.findViewById(R.id.pass_switch_repeat).setOnClickListener(v -> clickConfirmPwdEye());

        mAlertDialog.findViewById(R.id.btn_back).setOnClickListener(v -> clickBack());

        mAlertDialog.findViewById(R.id.btn_forward).setOnClickListener(v -> clickForward());

        mAlertDialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void passwordChangeCheck(){
        EditText mPwdEdit =  mAlertDialog.findViewById(R.id.password_input);
        String password = mPwdEdit.getText().toString();
        int strongerPwd = CheckInputRules.checkPwd(password);
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
        passwordRepeatChangeCheck();
    }

    private void passwordRepeatChangeCheck(){
        TextView passwordView = mAlertDialog.findViewById(R.id.password_input);
        String passwordString = passwordView.getText().toString();
        TextView passwordViewRepeat = mAlertDialog.findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        ImageView passwordRepeatCheck = mAlertDialog.findViewById(R.id.pass_input_check_repeat);
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

    private void clickConfirmPwdEye() {
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
        int strongerPwd = CheckInputRules.checkPwd(password);
        TextView passwordViewRepeat = mAlertDialog.findViewById(R.id.password_input_repeat);
        String passwordRepeatString = passwordViewRepeat.getText().toString();
        if(strongerPwd>0 && passwordRepeatString.equals(password)){
            Log.d(TAG,"start change password");
            /*
              使用SharedPreferences 对象，在生成密码md5字符串时候将,密码的md5字符串备份到本地文件
              Use SharedPreferences Class to backup password md5 string to local file when create password md5 string
             */
            String newPassMd5String = SecretAESOperator.getInstance().encrypt(password);
            User.getInstance().setNewPasswordMd5(mContext,newPassMd5String);
            mLoadingDialog.dismiss();
            release();
            if (mForgetPwdDialog!=null){
                mForgetPwdDialog.dismiss();
                mForgetPwdDialog = null;
            }

            if (mUnlockDialog!=null){
                mUnlockDialog.dismiss();
                mUnlockDialog = null;
            }
        }else{
            String checkSetPassWrongString = "";
            if(strongerPwd<0){
                checkSetPassWrongString = mContext.getResources().getString(R.string.toast_create_check_pass_wrong);
            }else if(!passwordRepeatString.equals(password)){
                checkSetPassWrongString =  mContext.getResources().getString(R.string.toast_create_check_pass_diff);
            }
            Toast checkSetPassToast = Toast.makeText(mContext,checkSetPassWrongString,Toast.LENGTH_LONG);
            checkSetPassToast.setGravity(Gravity.TOP,0,30);
            checkSetPassToast.show();
        }

    }

    public void release(){
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
