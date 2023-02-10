package com.omni.wallet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.utils.UtilFunctions;
import com.omni.wallet.utils.WalletState;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class UnlockActivity extends AppBaseActivity {
    String TAG = UnlockActivity.class.getSimpleName();
    Context ctx = UnlockActivity.this;
    String localPass = "";
    String localSeed = "";
    LoadingDialog mLoadingDialog;
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
        mLoadingDialog = new LoadingDialog(mContext);
        PasswordFilter passwordFilter = new PasswordFilter();
        mPwdEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16),passwordFilter});
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    clickUnlock();
                }
                return true;
            }
        };
        mPwdEdit.setOnEditorActionListener(listener);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        localPass = User.getInstance().getPasswordMd5(mContext);
        localSeed = User.getInstance().getSeedString(mContext);
        isCreated = User.getInstance().getCreated(mContext);
        isSynced = User.getInstance().getSynced(mContext);
        seedChecked = User.getInstance().getSeedChecked(mContext);
        walletAddress = User.getInstance().getWalletAddress(mContext);
        initWalletType = User.getInstance().getInitWalletType(mContext);
        isStartCreate = User.getInstance().getStartCreate(mContext);

        runOnUiThread(() -> {
            WalletState.getInstance().setWalletStateCallback(null);
            subscribeState();
        });


    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("unlock failed", "unlock failed");
                runOnUiThread(
                        () -> {
                            PublicUtils.closeLoading(mLoadingDialog);
                            if(e.getMessage().equals("rpc error: code = Unknown desc = wallet already unlocked, WalletUnlocker service is no longer available")){
                                switchActivityFinish(AccountLightningActivity.class);
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

    public boolean checkedPassMatched(String inputPass){
        boolean isMatched = false;
        if(inputPass.equals(localPass)){
            isMatched = true;
        }else{
            isMatched = false;
        }
        return isMatched;
    }

    public void passWrongShow (){
        PublicUtils.closeLoading(mLoadingDialog);
        String toastString = getResources().getString(R.string.toast_unlock_error);
        Toast checkPassToast = Toast.makeText(UnlockActivity.this, toastString, Toast.LENGTH_LONG);
        checkPassToast.setGravity(Gravity.TOP, 0, 20);
        checkPassToast.show();
    }


    /**
     * click unlock button
     * 点击Unlock
     */
    @OnClick(R.id.btn_unlock)
    public void clickUnlock() {
        String passwordString = mPwdEdit.getText().toString();
        String passMd5 = Md5Util.getMD5Str(passwordString);
        boolean passIsMatched = checkedPassMatched(passMd5);
        PublicUtils.showLoading(mLoadingDialog);
        if (passIsMatched){
            unlockWallet(passMd5);
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
            switch (walletState){
                case 4:
                    runOnUiThread(()->{
                        PublicUtils.closeLoading(mLoadingDialog);
                        switchActivityFinish(AccountLightningActivity.class);
                    });
                    break;
                default:
                    break;
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
