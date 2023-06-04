package com.omni.wallet_mainnet.ui.activity;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.base.AppBaseActivity;
import com.omni.wallet_mainnet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet_mainnet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet_mainnet.utils.DriveServiceHelper;
import com.omni.wallet_mainnet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

public class InitWalletMenuActivity extends AppBaseActivity {
    String TAG = InitWalletMenuActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 3;
    private DriveServiceHelper mDriveServiceHelper;
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.btn_create)
    public void clickCreate() {
        switchActivity(CreateWalletStepThreeActivity.class);
    }

    @OnClick(R.id.btn_recover)
    public void clickRecover() {
        switchActivity(ChooseRestoreTypeActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
