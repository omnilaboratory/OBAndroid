package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.popupwindow.FundPopupWindow;
import com.omni.wallet.popupwindow.MenuPopupWindow;
import com.omni.wallet.popupwindow.send.SendStepOnePopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AccountLightningActivity extends AppBaseActivity {
    private static final String TAG = AccountLightningActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_menu)
    ImageView mMenuIv;
    @BindView(R.id.recycler_assets_list)
    public RecyclerView mRecyclerView;// 资产列表的RecyclerView
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;

    MenuPopupWindow mMenuPopupWindow;
    FundPopupWindow mFundPopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;

    @Override
    protected View getStatusBarTopView() {
        return mTopView;
    }

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_account_lightning;
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        for (int i = 0; i < 5; i++) {
            String str = new String();
            mData.add(str);
        }
        mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_assets_list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

    }

    /**
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<String> {

        public MyAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {
            if (position == 0 || position == 1 || position == 2) {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_btc_logo_small);
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_link_black);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "link");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            } else {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_usdt_logo_small);
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_vector);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "lightning");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            }
        }
    }

    /**
     * 点击Copy按钮
     */
    @OnClick(R.id.iv_copy)
    public void clickCopy() {

    }


    /**
     * 点击Fund按钮
     */
    @OnClick(R.id.iv_fund)
    public void clickFund() {
        mFundPopupWindow = new FundPopupWindow(mContext);
        mFundPopupWindow.show(mParentLayout);
    }

    /**
     * 点击send按钮
     */
    @OnClick(R.id.iv_send)
    public void clickSend() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * 点击扫二维码按钮
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {

    }

    /**
     * 点击Search按钮
     */
    @OnClick(R.id.iv_search)
    public void clickSearch() {

    }

    /**
     * 点击Filter按钮
     */
    @OnClick(R.id.iv_filter)
    public void clickFilter() {

    }


    /**
     * 点击channel List按钮
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        switchActivity(ChannelsActivity.class);
    }

    /**
     * 点击右上角菜单按钮
     */
    @OnClick(R.id.iv_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv);
    }

    /**
     * 点击底部create channel按钮
     */
    @OnClick(R.id.layout_create_channel)
    public void clickCreateChannel() {
        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
        mCreateChannelStepOnePopupWindow.show(mParentLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.release();
        }
        if (mFundPopupWindow != null) {
            mFundPopupWindow.release();
        }
        if (mCreateChannelStepOnePopupWindow != null) {
            mCreateChannelStepOnePopupWindow.release();
        }
        if (mSendStepOnePopupWindow != null) {
            mSendStepOnePopupWindow.release();
        }
    }
}
