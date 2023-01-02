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
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.listItems.BackupFile;
import com.omni.wallet.ui.activity.backup.BackupBlockProcessActivity;
import com.omni.wallet.ui.activity.backup.BackupChannelActivity;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepTwoActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepTwoActivity;
import com.omni.wallet.utils.FilesUtils;
import com.omni.wallet.utils.Md5Util;
import com.omni.wallet.utils.ObdLogFileObserverCheckStarted;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class UnlockActivity extends AppBaseActivity {
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
    ObdLogFileObserverCheckStarted obdLogFileObserverCheckStarted;
    SharedPreferences blockData = null;

    @SuppressLint("LongLogTag")
    private final SharedPreferences.OnSharedPreferenceChangeListener isOpenedSharePreferenceChangeListener = (sharedPreferences, key) -> {
        if (key.equals("isOpened")) {
            Boolean isOpened = sharedPreferences.getBoolean("isOpened", false);
            if (isOpened) {
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
        localPass = User.getInstance().getPasswordMd5(mContext);
        localSeed = User.getInstance().getSeedString(mContext);
        isCreated = User.getInstance().getCreated(mContext);
        isSynced = User.getInstance().getSynced(mContext);
        seedChecked = User.getInstance().getSeedChecked(mContext);
        walletAddress = User.getInstance().getWalletAddress(mContext);
        initWalletType = User.getInstance().getInitWalletType(mContext);
        isStartCreate = User.getInstance().getStartCreate(mContext);
        if(initWalletType.isEmpty()){
            bottomBtnGroup.setVisibility(View.VISIBLE);
        }else{
            bottomBtnGroup.setVisibility(View.INVISIBLE);
        }
        
        blockData = ctx.getSharedPreferences("blockData", MODE_PRIVATE);
        SharedPreferences.Editor editor = blockData.edit();
        editor.putBoolean("isOpened", false);
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
    public void clickToForgetPassword() {
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
        Log.e("walletAddress",walletAddress);
        if (localPass.isEmpty()){
            if (initWalletType.equals("create")){
                if (localSeed.isEmpty()){
                    switchActivity(CreateWalletStepOneActivity.class);
                }else if(!seedChecked){
                    switchActivity(CreateWalletStepTwoActivity.class);
                }else if (!isStartCreate){
                    switchActivity(CreateWalletStepThreeActivity.class);
                }
            }else if (initWalletType.equals("recovery")){
                String recoverySeedString = User.getInstance().getRecoverySeedString(mContext);
                String backupFilePath = User.getInstance().getChannelBackupPath(mContext);
                if (recoverySeedString.isEmpty()){
                    switchActivity(RecoverWalletStepOneActivity.class);
                }else if(backupFilePath.isEmpty()){
                    switchActivity(RestoreChannelActivity.class);
                }else if(!isStartCreate){
                    switchActivity(RecoverWalletStepTwoActivity.class);
                }
            }else{
                boolean isShowing = mLoadingDialog.isShowing();
                if(isShowing){
                    mLoadingDialog.dismiss();
                    ToastUtils.showToast(mContext,"Your wallet is uninitialized, please create or recover your wallet!");
                }else {
                    ToastUtils.showToast(mContext,"Your wallet is uninitialized, please create or recover your wallet!");
                }

            }
        }else{
            if(localPass.equals(passMd5)){
                if(walletAddress.isEmpty()){
                    if (initWalletType.equals("create")){
                        if(!isCreated){
                            String dataPath = ctx.getExternalCacheDir() + "/data/chain/bitcoin/regtest";
                            List<BackupFile> backupFileList = FilesUtils.getDirectoryAndFile(dataPath,mContext);
                            for (int i = 0;i<backupFileList.size();i++){
                                BackupFile backupFileInfo = backupFileList.get(i);
                                String fileName = backupFileInfo.getFilename();
                                if (!fileName.equals("macaroons.db")){
                                    String fileNamePath = dataPath + "/" + fileName;
                                    File file = new File(fileNamePath);
                                    if (file.exists()){
                                        file.delete();
                                    }
                                }
                            }
                            switchActivity(CreateWalletStepThreeActivity.class);
                        }else if(!isSynced){
                            switchActivity(BackupBlockProcessActivity.class);
                        }else {
                            switchActivity(BackupBlockProcessActivity.class);
                        }
                    }else if(initWalletType.equals("recovery")){
                        if(!isCreated){
                            String dataPath = ctx.getExternalCacheDir() + "/data/chain/bitcoin/regtest";
                            List<BackupFile> backupFileList = FilesUtils.getDirectoryAndFile(dataPath,mContext);
                            for (int i = 0;i<backupFileList.size();i++){
                                BackupFile backupFileInfo = backupFileList.get(i);
                                String fileName = backupFileInfo.getFilename();
                                if (!fileName.equals("macaroons.db")){
                                    String fileNamePath = dataPath + "/" + fileName;
                                    File file = new File(fileNamePath);
                                    if (file.exists()){
                                        file.delete();
                                    }
                                }
                            }
                            switchActivity(RecoverWalletStepTwoActivity.class);
                        }else if(!isSynced){
                            switchActivity(BackupBlockProcessActivity.class);
                        }else{
                            switchActivity(BackupBlockProcessActivity.class);
                        }
                    }else{
                        boolean isShowing = mLoadingDialog.isShowing();
                        if(isShowing){
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext,"Your wallet is uninitialized, please create or recover your wallet!");
                        }else {
                            ToastUtils.showToast(mContext,"Your wallet is uninitialized, please create or recover your wallet!");
                        }
                    }
                }else{
                    boolean isOpened = blockData.getBoolean("isOpened",false);
                    if (isOpened){
                        mLoadingDialog.dismiss();
                        switchActivity(AccountLightningActivity.class);
                    }else{
                        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
                        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
                            @Override
                            public void onError(Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mLoadingDialog.isShowing()) {
                                            mLoadingDialog.dismiss();
                                        }
                                    }
                                });
                                Log.e("unlock failed", "unlock failed");
                                e.printStackTrace();

                            }

                            @Override
                            public void onResponse(byte[] bytes) {
                                getTotalBlockHeight();
                            }
                        });
                    }
                    
                }
            }else{
                mLoadingDialog.dismiss();
                String toastString = getResources().getString(R.string.toast_unlock_error);
                Toast checkPassToast = Toast.makeText(UnlockActivity.this, toastString, Toast.LENGTH_LONG);
                checkPassToast.setGravity(Gravity.TOP, 0, 20);
                checkPassToast.show();
            }
        }
    }

    /**
     * click create button
     * 点击Create
     */
    @OnClick(R.id.btn_create)
    public void clickCreate() {
        User.getInstance().setInitWalletType(mContext,"create");
        switchActivity(CreateWalletStepOneActivity.class);
    }

    /**
     * click recover button
     * 点击Recover
     */
    @OnClick(R.id.btn_recover)
    public void clickRecover() {
        User.getInstance().setInitWalletType(mContext,"recovery");
        switchActivity(RecoverWalletStepOneActivity.class);
    }

    /**
     * click forgot password
     * 点击忘记密码
     */
    @OnClick(R.id.btv_forget_button)
    public void clickForgetPass() {
        switchActivity(ForgetPwdActivity.class);
    }

    public void getTotalBlockHeight() {
        String jsonStr = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"omni_getinfo\", \"params\": []}";
        HttpUtils.with(ctx)
                .postString()
                .url("http://" + Wallet.BTC_HOST_ADDRESS_REGTEST + ":18332")
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
//                            String fileLocal = ctx.getExternalCacheDir() + "/logs/bitcoin/testnet/lnd.log";
                            obdLogFileObserverCheckStarted = new ObdLogFileObserverCheckStarted(fileLocal, ctx, totalBlock);
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
