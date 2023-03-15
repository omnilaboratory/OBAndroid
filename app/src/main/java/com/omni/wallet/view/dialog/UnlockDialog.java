package com.omni.wallet.view.dialog;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.Md5Util;

public class UnlockDialog {
    private static final String TAG = UnlockDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCanClick = true;

    public UnlockDialog(Context context){
        this.mContext = context;
    }

    public void show(){
        if (mAlertDialog == null){
            Log.d(TAG, "show" );
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_popupwindow_unlock)
                    .setAnimation(R.style.popup_anim_style)
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        mAlertDialog.findViewById(R.id.btn_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockWallet();
            }
        });
        mAlertDialog.findViewById(R.id.pass_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mPwdEdit = mAlertDialog.findViewById(R.id.password_input);
                ImageView mPwdEyeIv = mAlertDialog.findViewById(R.id.pass_switch);
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
        });
        mAlertDialog.findViewById(R.id.btv_forget_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
        mAlertDialog.show();
    }

    private void unlockWallet(){
        String passwordMd5 = User.getInstance().getPasswordMd5(mContext);
        String newPasswordMd5 = User.getInstance().getNewPasswordMd5(mContext);
        EditText textView = mAlertDialog.findViewById(R.id.password_input);
        String passwordInput = textView.getText().toString();
        String passwordInputMd5 = Md5Util.getMD5Str(passwordInput);
        if (!newPasswordMd5.equals("")){
            if (passwordInputMd5.equals(newPasswordMd5)){
                release();
            }else{
                String toastString = mContext.getResources().getString(R.string.toast_unlock_error);
                ToastUtils.showToast(mContext,toastString);
            }
        }else{
            if (passwordInputMd5.equals(passwordMd5)){
                release();
            }else{
                String toastString = mContext.getResources().getString(R.string.toast_unlock_error);
                ToastUtils.showToast(mContext,toastString);
            }
        }
    }

    private void forgetPassword(){
        ForgetPwdDialog forgetPwdDialog = new ForgetPwdDialog(mContext,mAlertDialog);
        forgetPwdDialog.show();
    }

    public void release(){
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }


}
