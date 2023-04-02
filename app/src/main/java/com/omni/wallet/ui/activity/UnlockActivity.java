package com.omni.wallet.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.utils.KeyboardScrollView;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.obdMethods.WalletState;
import com.omni.wallet.utils.SecretAESOperator;
import com.omni.wallet.view.dialog.LoginLoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Stateservice;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class UnlockActivity extends AppBaseActivity {
    String TAG = UnlockActivity.class.getSimpleName();
    String localSeed = "";
    LoginLoadingDialog mLoadingDialog;
    boolean isCreated = false;
    boolean isSynced = false;
    boolean seedChecked = false;
    boolean isStartCreate = false;
    String walletAddress = "";
    String initWalletType = "";

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

    @Override
    protected int getContentView() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoginLoadingDialog(mContext);
        PasswordFilter passwordFilter = new PasswordFilter();
        mPwdEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),passwordFilter});
        TextView.OnEditorActionListener listener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                clickUnlock();
            }
            return true;
        };
        mPwdEdit.setOnEditorActionListener(listener);
        LinearLayout pageContent = findViewById(R.id.pageContent);
        RelativeLayout mOutView = findViewById(R.id.form_unlock_content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, mOutView);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);

        localSeed = User.getInstance().getSeedString(mContext);
        isCreated = User.getInstance().getCreated(mContext);
        isSynced = User.getInstance().getSynced(mContext);
        seedChecked = User.getInstance().getSeedChecked(mContext);
        walletAddress = User.getInstance().getWalletAddress(mContext);
        initWalletType = User.getInstance().getInitWalletType(mContext);
        isStartCreate = User.getInstance().getStartCreate(mContext);
        changePassword();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onDoubleClickExit(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
    public void clickToForgetPassword() {
        Intent intent = new Intent();
        intent.setClass(mContext,ForgetPwdActivity.class);
        startActivityForResult(intent, ConstantInOB.beforeHomePageRequestCode);
    }
    public void unlockWallet(String passMd5) {
        Stateservice.GetStateRequest getStateRequest = Stateservice.GetStateRequest.newBuilder().build();
        Obdmobile.getState(getStateRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                ToastUtils.showToast(mContext, e.getMessage());
                runOnUiThread(() -> mLoadingDialog.dismiss());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    return;
                }
                try {
                    Stateservice.GetStateResponse getStateResponse = Stateservice.GetStateResponse.parseFrom(bytes);
                    int walletState = getStateResponse.getStateValue();
                    if (walletState == 4){
                        runOnUiThread(()->{
                            PublicUtils.closeLoading(mLoadingDialog);
                            switchActivityFinish(AccountLightningActivity.class);
                        });
                    }else {
                        runOnUiThread(()-> subscribeState());
                        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
                        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
                            @Override
                            public void onError(Exception e) {
                                Log.e("unlock failed", "unlock failed");
                                runOnUiThread(
                                        () -> {
                                            PublicUtils.closeLoading(mLoadingDialog);
                                            if(e.getMessage().contains("wallet already unlocked")){
                                                switchActivityFinish(AccountLightningActivity.class);
                                            } else if(e.getMessage().contains("wallet not found")){
                                                ToastUtils.showToast(mContext,"wallet not found,please contact administrator");
                                            } else{
                                                ToastUtils.showToast(mContext,e.getMessage());
                                            }
                                        }
                                );
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(byte[] bytes) {
                            }
                        });
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void changePassword(){
        mLoadingDialog.show();
        String newPasswordStr = User.getInstance().getNewPasswordMd5(mContext);
        Log.d(TAG, "changePassword newPasswordStr: " + newPasswordStr);
        if (!newPasswordStr.equals("")){
            Log.d(TAG, "changePassword : will be change");
            Handler handler = new Handler();
            runOnUiThread(this::subscribeStateForChangePass);
            handler.postDelayed(this::changePasswordAction,10000);
        }else {
            mLoadingDialog.dismiss();
        }
    }

    public void changePasswordAction(){
        String currentPasswordStr = User.getInstance().getPasswordMd5(mContext);
        String newPasswordStr = User.getInstance().getNewPasswordMd5(mContext);
        Walletunlocker.ChangePasswordRequest changePasswordRequest = Walletunlocker.ChangePasswordRequest.newBuilder()
                .setCurrentPassword(ByteString.copyFromUtf8(currentPasswordStr))
                .setNewPassword(ByteString.copyFromUtf8(newPasswordStr))
                .build();
        Obdmobile.changePassword(changePasswordRequest.toByteArray(),new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "changePassword onError: " + e.getMessage());
                runOnUiThread(()-> ToastUtils.showToast(mContext,e.getMessage()));

                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Walletunlocker.ChangePasswordResponse changePasswordResponse = Walletunlocker.ChangePasswordResponse.parseFrom(bytes);
                    String macaroon = changePasswordResponse.getAdminMacaroon().toString();
                    User.getInstance().setMacaroonString(mContext,macaroon);
                    User.getInstance().setPasswordMd5(mContext,newPasswordStr);
                    User.getInstance().setNewPasswordMd5(mContext,"");
                    Log.d(TAG, "changePassword onResponse: changeOver");
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean checkedPassMatched(String inputPass){
        boolean isMatched;
        String localPass = User.getInstance().getPasswordMd5(mContext);
        isMatched = inputPass.equals(localPass);
        return isMatched;
    }


    /**
     * click unlock button
     * 点击Unlock
     */
    @OnClick(R.id.btn_unlock)
    public void clickUnlock() {
        String passwordString = mPwdEdit.getText().toString();
        String newSecretString = SecretAESOperator.getInstance().encrypt(passwordString);
        boolean passIsMatched = checkedPassMatched(newSecretString);
        PublicUtils.showLoading(mLoadingDialog);
        if (passIsMatched){
            unlockWallet(newSecretString);
        }else{
            PublicUtils.closeLoading(mLoadingDialog);
            String toastString = getResources().getString(R.string.toast_unlock_error);
            Toast checkPassToast = Toast.makeText(UnlockActivity.this, toastString, Toast.LENGTH_LONG);
            checkPassToast.setGravity(Gravity.TOP, 0, 20);
            checkPassToast.show();
        }
}

    public void subscribeState() {
        WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
            if (walletState == 4) {
                runOnUiThread(() -> {
                    PublicUtils.closeLoading(mLoadingDialog);
                    switchActivityFinish(AccountLightningActivity.class);
                });
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

    public void subscribeStateForChangePass() {
        WalletState.WalletStateCallback walletStateCallback = (int walletState)->{
            Log.d(TAG, "subscribeStateForChangePass: " + walletState);
            if (walletState == 4) {
                runOnUiThread(() -> PublicUtils.closeLoading(mLoadingDialog));
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

    /**
     * click create button
     * 点击Create
     */
    @OnClick(R.id.btn_create)
    public void clickCreate() {
        User.getInstance().setInitWalletType(mContext, "create");
        Intent intent = new Intent();
        intent.setClass(mContext,CreateWalletStepOneActivity.class);
        startActivityForResult(intent, ConstantInOB.beforeHomePageRequestCode);
    }

    /**
     * click recover button
     * 点击Recover
     */
    @OnClick(R.id.btn_recover)
    public void clickRecover() {
        User.getInstance().setInitWalletType(mContext, "recovery");
        Intent intent = new Intent();
        intent.setClass(mContext,RecoverWalletStepOneActivity.class);
        startActivityForResult(intent, ConstantInOB.beforeHomePageRequestCode);
    }

    /**
     * click forgot password
     * 点击忘记密码
     */
    @OnClick(R.id.btv_forget_button)
    public void clickForgetPass() {
        Intent intent = new Intent();
        intent.setClass(mContext,ForgetPwdActivity.class);
        startActivityForResult(intent, ConstantInOB.beforeHomePageRequestCode);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
