package com.omni.wallet.ui.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        walletType = User.getInstance().getInitWalletType(mContext);
        if(walletType.equals("create")){
            welcomeContent.setVisibility(View.GONE);
            continueToCreateBtn.setVisibility(View.VISIBLE);
            continueToRecoveryBtn.setVisibility(View.GONE);
        }else if(walletType.equals("recovery")){
            welcomeContent.setVisibility(View.GONE);
            continueToCreateBtn.setVisibility(View.GONE);
            continueToRecoveryBtn.setVisibility(View.VISIBLE);
        }else{
            welcomeContent.setVisibility(View.VISIBLE);
            continueToCreateBtn.setVisibility(View.GONE);
            continueToRecoveryBtn.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.btn_create,R.id.continue_to_create_btn})
    public void clickCreate(){
        User.getInstance().setInitWalletType(mContext, "create");
        switchActivity(CreateWalletStepThreeActivity.class);
    }

    @OnClick({R.id.btn_recover,R.id.continue_to_recovery_btn})
    public void clickRecover(){
        User.getInstance().setInitWalletType(mContext, "recovery");
        switchActivity(RestoreChannelActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
