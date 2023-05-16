package com.omni.wallet.ui.activity;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.ui.activity.backup.RestoreChannelActivity;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

public class InitWalletMenuActivity extends AppBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_init_wallet_menu;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.btn_create})
    public void clickCreate(){
        switchActivity(CreateWalletStepThreeActivity.class);
    }

    @OnClick({R.id.btn_recover})
    public void clickRecover(){
        switchActivity(RestoreChannelActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
