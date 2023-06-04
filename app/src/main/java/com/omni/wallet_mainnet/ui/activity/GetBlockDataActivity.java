package com.omni.wallet_mainnet.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.base.AppBaseActivity;
import com.omni.wallet_mainnet.framelibrary.view.navigationBar.DefaultNavigationBar;

public class GetBlockDataActivity extends AppBaseActivity {
    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected void initHeader() {
        super.initHeader();
        new DefaultNavigationBar.Builder(mContext)
                .setTitle(R.string.welcome)
                .setRightView(R.layout.layout_get_block_data_right)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchActivityFinish(AccountLightningActivity.class);
                    }
                })
                .build();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_get_block_data;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
