package com.omni.wallet_mainnet.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.CopyUtil;
import com.omni.wallet_mainnet.utils.SecretAESOperator;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * 汉: 导出WIF的弹窗
 * En: ExportWifDialog
 * author: guoyalei
 * date: 2023/6/13
 */
public class ExportWifDialog {
    private static final String TAG = ExportWifDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCanClick = false;
    String wifStr;

    public ExportWifDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_export_wif)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        EditText mPwdEdit = mAlertDialog.findViewById(R.id.password_input);
        mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mAlertDialog.findViewById(R.id.btn_unlock).setOnClickListener(v -> unlockWallet());
        mAlertDialog.findViewById(R.id.pass_switch).setOnClickListener(v -> {
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
        });
        /**
         * @描述： 点击 close
         * @desc: click close button
         */
        mAlertDialog.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    private void unlockWallet() {
        EditText mPwdEdit = mAlertDialog.findViewById(R.id.password_input);
        String passwordString = mPwdEdit.getText().toString();
        String newSecretString = SecretAESOperator.getInstance().encrypt(passwordString);
        boolean passIsMatched = checkedPassMatched(newSecretString);
        if (passIsMatched) {
            showWif();
        } else {
            String toastString = mContext.getResources().getString(R.string.toast_unlock_error);
            ToastUtils.showToast(mContext, toastString);
        }
    }

    private void showWif() {
        mAlertDialog.findViewById(R.id.layout_unlock).setVisibility(View.GONE);
        mAlertDialog.findViewById(R.id.layout_wif).setVisibility(View.VISIBLE);
        TextView wifTv = mAlertDialog.findViewById(R.id.tv_wif);
        LightningOuterClass.DumpPrivkeyRequest dumpPrivkeyRequest = LightningOuterClass.DumpPrivkeyRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_DumpPrivkey(dumpPrivkeyRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------dumpPrivkeyOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.DumpPrivkeyResponse resp = LightningOuterClass.DumpPrivkeyResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------dumpPrivkeyOnResponse------------------" + resp.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            wifStr = resp.getKeyWif();
                            wifTv.setText(wifStr);
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        mAlertDialog.findViewById(R.id.iv_copy_wif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(wifStr)) {
                    ToastUtils.showToast(mContext, "WIF is empty");
                    return;
                }
                //接收需要复制到粘贴板的地址
                //Get the address which will copy to clipboard
                String toCopyAddress = wifStr;
                //接收需要复制成功的提示语
                //Get the notice when you copy success
                String toastString = "Already copy the WIF to clipboard!";
                CopyUtil.SelfCopy(mContext, toCopyAddress, toastString);
            }
        });
    }

    public boolean checkedPassMatched(String inputPass) {
        boolean isMatched;
        String localPass = User.getInstance().getPasswordMd5(mContext);
        isMatched = inputPass.equals(localPass);
        return isMatched;
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}