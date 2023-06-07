package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.utils.KeyboardScrollView;
import com.omni.wallet.utils.PasswordFilter;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.utils.SecretAESOperator;
import com.omni.wallet.view.dialog.LoginLoadingDialog;
import com.omni.wallet.view.dialog.NewVersionDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class UnlockActivity extends AppBaseActivity {
    String TAG = UnlockActivity.class.getSimpleName();

    @BindView(R.id.password_input)
    public EditText mPwdEdit;
    @BindView(R.id.pass_switch)
    public ImageView mPwdEyeIv;
    private boolean mCanClick = false;
    @BindView(R.id.bottom_btn_group)
    public RelativeLayout bottomBtnGroup;

    LoginLoadingDialog mLoadingDialog;

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
        mPwdEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16), passwordFilter});
        TextView.OnEditorActionListener listener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // 检查更新(Check for updates)
        checkUpdate();
    }

    private void checkUpdate() {
        HttpUtils.with(mContext)
                .get()
                .url("https://omnilaboratory.github.io/OBAndroid/app/src/main/assets/newVersion.json")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        LogUtils.e(TAG, "newVersionError:" + errorMsg);
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------newVersion---------------------" + result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    JSONObject netObject = null;
                                    if (ConstantInOB.networkType == NetworkType.TEST) {
                                        netObject = jsonObject.getJSONObject("testnet");
                                    } else if (ConstantInOB.networkType == NetworkType.REG) {
                                        netObject = jsonObject.getJSONObject("regtest");
                                    } else if (ConstantInOB.networkType == NetworkType.MAIN) {
                                        netObject = jsonObject.getJSONObject("mainnet");
                                    }
                                    if (!AppUtils.getAppVersionName(mContext).equals(netObject.getString("version"))) {
                                        boolean force = netObject.getBoolean("force");
                                        if (force) {
                                            NewVersionDialog mNewVersionDialog = new NewVersionDialog(mContext);
                                            mNewVersionDialog.show(force);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
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
        if (passIsMatched) {
            PublicUtils.closeLoading(mLoadingDialog);
            switchActivityFinish(AccountLightningActivity.class);
        } else {
            PublicUtils.closeLoading(mLoadingDialog);
            String toastString = getResources().getString(R.string.toast_unlock_error);
            Toast checkPassToast = Toast.makeText(UnlockActivity.this, toastString, Toast.LENGTH_LONG);
            checkPassToast.setGravity(Gravity.TOP, 0, 20);
            checkPassToast.show();
        }
    }

    public boolean checkedPassMatched(String inputPass) {
        boolean isMatched;
        String localPass = User.getInstance().getPasswordMd5(mContext);
        isMatched = inputPass.equals(localPass);
        return isMatched;
    }

    /**
     * click forgot password
     * 点击忘记密码
     */
    @OnClick({R.id.btv_forget_button,R.id.tv_pass_text})
    public void clickForgetPass() {
        switchActivity(CreateWalletStepThreeActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
