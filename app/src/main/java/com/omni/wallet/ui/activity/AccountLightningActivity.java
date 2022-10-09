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
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.popupwindow.FundPopupWindow;
import com.omni.wallet.popupwindow.MenuPopupWindow;
import com.omni.wallet.popupwindow.send.SendStepOnePopupWindow;
import com.omni.wallet.utils.CopyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.recycler_assets_list_block)
    public RecyclerView mRecyclerViewBlock;// 资产列表的RecyclerViewBlock
    @BindView(R.id.recycler_assets_list_lightning)
    public RecyclerView mRecyclerViewLightning;// 资产列表的RecyclerViewLighting
    @BindView(R.id.tv_account_value)
    public TextView accountValue;//资产列表所有的资产价值总和
    private List<Map> blockData = new ArrayList<>();
    private List<Map> lightningData = new ArrayList<>();
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
        initBlockAssets();
        initLightningAssets();
        initRecyclerViewBlock(blockData);
        initRecyclerViewLighting(lightningData);
    }

    //    测试用方法生成block assets 数据
    private void initBlockAssets(){
        Map a = new HashMap<String,String>();
        a.put("tokenImageSource",R.mipmap.icon_usdt_logo_small);
        a.put("networkImageSource",R.mipmap.icon_network_link_black);
        a.put("amount",10000.0000f);
        a.put("value",70000.0000f);
        Map b = new HashMap<String,String>();
        b.put("tokenImageSource",R.mipmap.icon_btc_logo_small);
        b.put("networkImageSource",R.mipmap.icon_network_link_black);
        b.put("amount",10000.0000f);
        b.put("value",70000.0000f);
        blockData.add(a);
        blockData.add(b);
        blockData.add(a);
        blockData.add(b);
    }
    //    测试用方法生成block assets 数据
    private void initLightningAssets(){
        Map a = new HashMap<String,String>();
        a.put("tokenImageSource",R.mipmap.icon_usdt_logo_small);
        a.put("networkImageSource",R.mipmap.icon_network_vector);
        a.put("amount",10000.0000f);
        a.put("value",70000.0000f);
        Map b = new HashMap<String,String>();
        b.put("tokenImageSource",R.mipmap.icon_btc_logo_small);
        b.put("networkImageSource",R.mipmap.icon_network_vector);
        b.put("amount",10000.0000f);
        b.put("value",70000.0000f);
        lightningData.add(a);
        lightningData.add(b);
        lightningData.add(a);
        lightningData.add(b);
    }

    private void initRecyclerViewBlock(List data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewBlock.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, data, R.layout.layout_item_assets_list);
        mRecyclerViewBlock.setAdapter(mAdapter);
    }

    private void initRecyclerViewLighting(List data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewLightning.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, data, R.layout.layout_item_assets_list);
        mRecyclerViewLightning.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

    }

    /**
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<Map> {

        public MyAdapter(Context context, List<Map> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final Map item) {
            Integer tokenImageSourceId = Integer.parseInt(item.get("tokenImageSource").toString());
            Integer networkImageSource = Integer.parseInt(item.get("networkImageSource").toString());
            String assetsAmount = item.get("amount").toString();
            String assetsValue = item.get("value").toString();
            holder.setImageResource(R.id.iv_asset_logo, tokenImageSourceId);
            holder.setImageResource(R.id.iv_asset_net, networkImageSource);
            holder.setText(R.id.tv_asset_amount,assetsAmount);
            holder.setText(R.id.tv_asset_value,assetsValue);
            if(networkImageSource == R.mipmap.icon_network_link_black ){
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "link");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            }else{
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
     * 汉：点击copy图标复制地址
     * En：Click copy icon button,duplicate user`s wallet address to clipboard
     * author:Tong ChangHui
     * E-mail:tch081092@gmail.com
     * date:2022-10-08
     */
    @OnClick(R.id.iv_copy)
    public void clickCopy() {
        //接收需要复制到粘贴板的地址
        //Get the address which will copy to clipboard
        String toCopyAddress = "01234e*****bg453123";
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(AccountLightningActivity.this,toCopyAddress,toastString);
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
