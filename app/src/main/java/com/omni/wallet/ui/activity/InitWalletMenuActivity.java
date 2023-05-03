package com.omni.wallet.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.protobuf.ByteString;
import com.omni.wallet.R;
import com.omni.wallet.SharedPreferences.WalletInfo;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.backup.BackupBlockProcessActivity;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepTwoActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepTwoActivity;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class InitWalletMenuActivity extends AppBaseActivity {

    @BindView(R.id.welcome_content)
    LinearLayout welcomeContent;

    @BindView(R.id.continue_to_create_btn)
    LinearLayout continueToCreateBtn;

    @BindView(R.id.continue_to_recovery_btn)
    LinearLayout continueToRecoveryBtn;

    String walletType;

    LoadingDialog mLoadingDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_init_wallet_menu;
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
        EventBus.getDefault().register(this);
        walletType = WalletInfo.getInstance().getInitWalletType(mContext, ConstantInOB.networkType);
        if(walletType.equals("createStepOne")||walletType.equals("createStepTwo")||walletType.equals("createStepThree")){
            welcomeContent.setVisibility(View.GONE);
            continueToCreateBtn.setVisibility(View.VISIBLE);
            continueToRecoveryBtn.setVisibility(View.GONE);
        }else if(walletType.equals("recoveryStepOne")||walletType.equals("recoveryStepTwo")||walletType.equals("toBeRestoreChannel")){
            welcomeContent.setVisibility(View.GONE);
            continueToCreateBtn.setVisibility(View.GONE);
            continueToRecoveryBtn.setVisibility(View.VISIBLE);
        }else{
            welcomeContent.setVisibility(View.VISIBLE);
            continueToCreateBtn.setVisibility(View.GONE);
            continueToRecoveryBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.btn_create)
    public void clickCreate(){
        switchActivity(CreateWalletStepOneActivity.class);
    }

    @OnClick(R.id.btn_recover)
    public void clickRecover(){
        switchActivity(RecoverWalletStepOneActivity.class);
    }

    @OnClick(R.id.continue_to_create_btn)
    public void clickContinueToCreate(){
        switch (walletType){
            case "createStepOne" :
                switchActivity(CreateWalletStepTwoActivity.class);
                break;
            case "createStepTwo" :
                switchActivity(CreateWalletStepThreeActivity.class);
                break;
            case "createStepThree" :
                unlockWalletToBackupBlockProcess();
                break;
            default:
                break;

        }
    }

    @OnClick(R.id.continue_to_recovery_btn)
    public void clickContinueToRecovery(){
        switch (walletType){
            case "recoveryStepOne" :
                switchActivity(RecoverWalletStepTwoActivity.class);
                break;
            case "recoveryStepTwo" :
                unlockWalletToBackupBlockProcess();
                break;
            case "toBeRestoreChannel":
                unlockWalletToRestoreChannel();
                break;
            default:
                break;
        }
    }

    public void unlockWalletToRestoreChannel(){
        mLoadingDialog.show();
        String passMd5 = WalletInfo.getInstance().getPasswordSecret(mContext,ConstantInOB.networkType);
        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("unlock failed", e.getMessage());
                runOnUiThread(
                        () -> {
                            PublicUtils.closeLoading(mLoadingDialog);
                            if(e.getMessage().equals("rpc error: code = Unknown desc = wallet already unlocked, WalletUnlocker service is no longer available")){
                                switchActivity(RestoreChannelActivity.class);
                            }
                        }
                );

                e.printStackTrace();

            }

            @Override
            public void onResponse(byte[] bytes) {
                switchActivity(RestoreChannelActivity.class);
            }
        });
    }

    public void unlockWalletToBackupBlockProcess(){
        mLoadingDialog.show();
        String passMd5 = WalletInfo.getInstance().getPasswordSecret(mContext,ConstantInOB.networkType);
        Walletunlocker.UnlockWalletRequest unlockWalletRequest = Walletunlocker.UnlockWalletRequest.newBuilder().setWalletPassword(ByteString.copyFromUtf8(passMd5)).build();
        Obdmobile.unlockWallet(unlockWalletRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("unlock failed", e.getMessage());
                runOnUiThread(
                        () -> {
                            PublicUtils.closeLoading(mLoadingDialog);
                            if(e.getMessage().equals("rpc error: code = Unknown desc = wallet already unlocked, WalletUnlocker service is no longer available")){
                                switchActivity(BackupBlockProcessActivity.class);
                            }
                        }
                );

                e.printStackTrace();

            }

            @Override
            public void onResponse(byte[] bytes) {
                switchActivity(BackupBlockProcessActivity.class);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
