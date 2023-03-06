package com.omni.wallet.view.dialog;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;

import java.util.ArrayList;

public class ForgetPwdNextDialog {
    public static final String TAG = ForgetPwdNextDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public ForgetPwdNextDialog(Context context){
        this.mContext = context;
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

    }

    public void release(){
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
