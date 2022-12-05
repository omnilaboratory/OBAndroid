package com.omni.wallet.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.protobuf.ByteString;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.ObdLogFileObserverCheckStarted;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class UnlockActivity extends AppBaseActivity {
    Context ctx = UnlockActivity.this;
    String localPass ="";
    String localSeed = "";
    LoadingDialog mLoadingDialog;

    @BindView(R.id.password_input)
    public EditText mPwdEdit;
    @BindView(R.id.pass_switch)
    public ImageView mPwdEyeIv;
    private boolean mCanClick = true;
    @BindView(R.id.bottom_btn_group)
    public RelativeLayout bottomBtnGroup;
    ObdLogFileObserverCheckStarted obdLogFileObserverCheckStarted;
    SharedPreferences blockData = null;

    @SuppressLint("LongLogTag")
    private final SharedPreferences.OnSharedPreferenceChangeListener isOpenedSharePreferenceChangeListener = (sharedPreferences, key) -> {
        if (key.equals("isOpened")){
            Boolean isOpened = sharedPreferences.getBoolean("isOpened",false);
            if(isOpened){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        obdLogFileObserverCheckStarted.stopWatching();
                    }
                }).start();
                
                mLoadingDialog.dismiss();
                switchActivityFinish(AccountLightningActivity.class);
            }

        }
    };


    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

//    @Override
//    protected int titleId() {
//        return R.string.unlock;
//    }

    @Override
    protected int getContentView() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
    }

    @Override
    protected void initData() {
        /**
         * 获取本地pass,用于后续判断输入的密码是否正确。获取本地seed，判断是否显示bottom_btn_group,如果有则隐藏如果没有则显示，为方便测试暂时隐藏.
         *Obtain the local pass to determine whether the password entered is correct. Get the local seed and determine whether to display bottom_ btn_ Group. If there is, it will be hidden. If not, it will be displayed. It is temporarily hidden for the convenience of testing
         */
        SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
        localPass = secretData.getString("password","");
        localSeed = secretData.getString("seeds","");
        if (localSeed.isEmpty()){
            bottomBtnGroup.setVisibility(View.VISIBLE);
        }else{
            bottomBtnGroup.setVisibility(View.INVISIBLE);
        }
        blockData = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        SharedPreferences.Editor editor = blockData.edit();
        editor.putBoolean("isOpened",false);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        blockData.unregisterOnSharedPreferenceChangeListener(isOpenedSharePreferenceChangeListener);
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
    public void clickToForgetPassword(){
        switchActivity(ForgetPwdActivity.class);
    }

    /**
     * click unlock button
     * 点击Unlock
     */
    @OnClick(R.id.btn_unlock)
    public void clickUnlock() {
        String passwordString = mPwdEdit.getText().toString();
        String passMd5 = Md5Util.getMD5Str(passwordString);
        mLoadingDialog.show();
        if(localPass.equals(passMd5)){
            if(blockData.getBoolean("isOpened",false)){
                mLoadingDialog.dismiss();
                switchActivity(AccountLightningActivity.class);
            }else{
                Walletunlocker.UnlockWalletRequest unlockWalletRequest =  Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
                Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        if(mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                        Log.e("unlock failed","unlock failed");
                        e.printStackTrace();

                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        getTotalBlockHeight();
                    }
                });
            }

//            
        }else{
            mLoadingDialog.dismiss();
            String toastString = getResources().getString(R.string.toast_unlock_error);
            Toast checkPassToast = Toast.makeText(UnlockActivity.this,toastString,Toast.LENGTH_LONG);
            checkPassToast.setGravity(Gravity.TOP,0,20);
            checkPassToast.show();
        }
    }

    /**
     * click create button
     * 点击Create
     */
    @OnClick(R.id.btn_create)
    public void clickCreate() {
        switchActivity(CreateWalletStepOneActivity.class);
    }

    /**
     * click recover button
     * 点击Recover
     */
    @OnClick(R.id.btn_recover)
    public void clickRecover() {
        switchActivity(RecoverWalletStepOneActivity.class);
    }

    /**
     * click forgot password
     * 点击忘记密码
     */
    @OnClick(R.id.btv_forget_button)
    public void clickForgetPass(){
        switchActivity(ForgetPwdActivity.class);
    }

    public void getTotalBlockHeight (){
        String jsonStr = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"omni_getinfo\", \"params\": []}";
        HttpUtils.with(ctx)
                .postString()
                .url("http://43.138.107.248:18332")
                .addContent(jsonStr)
                .execute(new EngineCallback() {

                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {

                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            String block = jsonObject1.getString("block");
                            int totalBlock = Integer.parseInt(block);
                            String fileLocal = ctx.getExternalCacheDir() + "/logs/bitcoin/regtest/lnd.log";
                            obdLogFileObserverCheckStarted = new ObdLogFileObserverCheckStarted(fileLocal,ctx,totalBlock);
                            blockData.registerOnSharedPreferenceChangeListener(isOpenedSharePreferenceChangeListener);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    obdLogFileObserverCheckStarted.startWatching();
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
}
