package com.omni.wallet.ui.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.backup.BackupBlockProcessActivity;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepOneActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepTwoActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepOneActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepTwoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class InitWalletMenuActivity extends AppBaseActivity {

    private final static String TAG = InitWalletMenuActivity.class.getSimpleName();

    @BindView(R.id.welcome_content)
    LinearLayout welcomeContent;

    @BindView(R.id.continue_to_create_btn)
    LinearLayout continueToCreateBtn;

    @BindView(R.id.continue_to_recovery_btn)
    LinearLayout continueToRecoveryBtn;

    String walletType;

    @Override
    protected int getContentView() {
        return R.layout.activity_init_wallet_menu;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        walletType = User.getInstance().getInitWalletType(mContext);
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
                switchActivity(BackupBlockProcessActivity.class);
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
                switchActivity(BackupBlockProcessActivity.class);
                break;
            case "toBeRestoreChannel":
                switchActivity(RestoreChannelActivity.class);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
