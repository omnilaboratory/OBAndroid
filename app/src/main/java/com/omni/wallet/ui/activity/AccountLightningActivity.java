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

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.popupwindow.AccountManagePopupWindow;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.FundPopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;
import com.omni.wallet.view.popupwindow.send.SendStepOnePopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.LightningOuterClass;

public class AccountLightningActivity extends AppBaseActivity {
    private static final String TAG = AccountLightningActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_menu)
    ImageView mMenuIv;
    @BindView(R.id.tv_balance_value)
    TextView mBalanceValueTv;
    @BindView(R.id.tv_balance_amount)
    TextView mBalanceAmountTv;
    @BindView(R.id.tv_wallet_address)
    TextView mWalletAddressTv;
    @BindView(R.id.recycler_assets_list_block)
    public RecyclerView mRecyclerViewBlock;// ???????????????RecyclerViewBlock(The Recycler View Block for Assets List)
    private List<Map> blockData = new ArrayList<>();
    private List<Map> lightningData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<LightningOuterClass.AssetBalanceByAddressResponse> allData = new ArrayList<>();
    LightningOuterClass.NewAddressResponse addressResp;

    MenuPopupWindow mMenuPopupWindow;
    FundPopupWindow mFundPopupWindow;
    AccountManagePopupWindow mAccountManagePopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    SelectNodePopupWindow mSelectNodePopupWindow;
    private LoadingDialog mLoadingDialog;

    long balanceAmount;
    private String pubkey;

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
        mLoadingDialog = new LoadingDialog(mContext);
        initAllData();
        initRecyclerView();
    }

    /**
     * ?????????????????????block assets ??????
     * Test data for block assets
     */
    private void initBlockAssets() {
        Map a = new HashMap<String, String>();
        a.put("tokenImageSource", R.mipmap.icon_usdt_logo_small);
        a.put("networkImageSource", R.mipmap.icon_network_link_black);
        a.put("amount", 10000.0000f);
        a.put("value", 70000.0000f);
        Map b = new HashMap<String, String>();
        b.put("tokenImageSource", R.mipmap.icon_btc_logo_small);
        b.put("networkImageSource", R.mipmap.icon_network_link_black);
        b.put("amount", 10000.0000f);
        b.put("value", 70000.0000f);
        blockData.add(a);
        blockData.add(b);
        blockData.add(a);
        blockData.add(b);
    }

    /**
     * ????????????????????? Lightning assets ??????
     * Test data for Lightning assets
     */
    private void initLightningAssets() {
        Map c = new HashMap<String, String>();
        c.put("tokenImageSource", R.mipmap.icon_usdt_logo_small);
        c.put("networkImageSource", R.mipmap.icon_network_vector);
        c.put("amount", 10000.0000f);
        c.put("value", 70000.0000f);
        Map d = new HashMap<String, String>();
        d.put("tokenImageSource", R.mipmap.icon_btc_logo_small);
        d.put("networkImageSource", R.mipmap.icon_network_vector);
        d.put("amount", 10000.0000f);
        d.put("value", 70000.0000f);
        lightningData.add(c);
        lightningData.add(d);
        lightningData.add(c);
        lightningData.add(d);
    }

    private void initAllData() {
        initBlockAssets();
        initLightningAssets();
//        allData.addAll(blockData);
//        allData.addAll(lightningData);

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewBlock.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, allData, R.layout.layout_item_assets_list);
        mRecyclerViewBlock.setAdapter(mAdapter);
    }


    @Override
    protected void initData() {
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        /**
         * Get wallet related information
         * ????????????????????????
         */
        Lndmobile.getInfo(LightningOuterClass.GetInfoRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------getInfoOnError------------------" + e.getMessage());
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.GetInfoResponse resp = LightningOuterClass.GetInfoResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------getInfoOnResponse-----------------" + resp);
                    pubkey = resp.getIdentityPubkey();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
        /**
         * Create a new wallet address first, and then request the interface of each asset balance list
         * ????????????????????????????????????????????????????????????????????????
         */
        LightningOuterClass.NewAddressRequest asyncNewAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder()
                .setTypeValue(2)
                .build();
        Lndmobile.newAddress(asyncNewAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------newAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    addressResp = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    mWalletAddressTv.setText(addressResp.getAddress());
                    LogUtils.e(TAG, "------------------newAddressOnResponse-----------------" + addressResp.getAddress());
                    LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                            .setAddress(addressResp.getAddress())
                            .build();
                    Lndmobile.walletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------walletBalanceByAddressOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------walletBalanceByAddressOnResponse-----------------" + resp);
                                mBalanceValueTv.setText("$ " + resp.getTotalBalance());
                                balanceAmount = resp.getConfirmedBalance();
                                mBalanceAmountTv.setText("My account " + balanceAmount + " balance");
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        /**
         * request the interface of each asset balance list
         * ????????????????????????????????????
         */
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress("ms5u6Wmc8xF8wFBo9w5HFouFNAmnWzkVa6")
                .build();
        Lndmobile.assetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------assetsBalanceOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------assetsBalanceOnResponse------------------" + resp.getListList().toString());
                    allData.clear();
                    allData.addAll(resp.getListList());
                    mAdapter.notifyDataSetChanged();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
//        /**
//         * Set the created new wallet address as the default wallet address
//         * ??????????????????????????????????????????????????????
//         */
//        LightningOuterClass.SetDefaultAddressRequest setDefaultAddressRequest = LightningOuterClass.SetDefaultAddressRequest.newBuilder()
//                .setAddress(addressResp.getAddress())
//                .build();
//        Lndmobile.setDefaultAddress(setDefaultAddressRequest.toByteArray(), new Callback() {
//            @Override
//            public void onError(Exception e) {
//                LogUtils.e(TAG, "------------------setDefaultAddressOnError------------------" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(byte[] bytes) {
//                try {
//                    LightningOuterClass.SetDefaultAddressResponse resp = LightningOuterClass.SetDefaultAddressResponse.parseFrom(bytes);
//                    LogUtils.e(TAG, "------------------setDefaultAddressOnResponse------------------" + resp.toString());
//                } catch (InvalidProtocolBufferException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    /**
     * ?????????????????????
     * The adapter for assets list
     */
    private class MyAdapter extends CommonRecyclerAdapter<LightningOuterClass.AssetBalanceByAddressResponse> {

        public MyAdapter(Context context, List<LightningOuterClass.AssetBalanceByAddressResponse> data, int layoutId) {
            super(context, data, layoutId);
        }


        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.AssetBalanceByAddressResponse item) {
//            if (position == blockData.size() - 1) {
//                LinearLayout lvContent = holder.getView(R.id.lv_item_content);
//                lvContent.setPadding(0, 0, 0, 100);
//            }
//
//
//            Integer tokenImageSourceId = Integer.parseInt(item.get("tokenImageSource").toString());
//            Integer networkImageSource = Integer.parseInt(item.get("networkImageSource").toString());
//            String assetsAmount = item.get("amount").toString();
//            String assetsValue = item.get("value").toString();
//            holder.setImageResource(R.id.iv_asset_logo, tokenImageSourceId);
//            holder.setImageResource(R.id.iv_asset_net, networkImageSource);
//            holder.setText(R.id.tv_asset_amount, assetsAmount);
//            holder.setText(R.id.tv_asset_value, assetsValue);
//            if (networkImageSource == R.mipmap.icon_network_link_black) {
//                holder.setOnItemClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "link");
//                        switchActivity(BalanceDetailActivity.class, bundle);
//                    }
//                });
//            } else {
//                holder.setOnItemClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "lightning");
//                        switchActivity(BalanceDetailActivity.class, bundle);
//                    }
//                });
//            }
            // TODO: 2022/11/3 ????????????????????????
            holder.setText(R.id.tv_asset_amount, String.valueOf(item.getBalance()));
            holder.setText(R.id.tv_asset_value, item.getFrozen());
            if (item.getName().equals("ftoken")) {
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
                        bundle.putString(BalanceDetailActivity.KEY_PUBKEY, pubkey);
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_ACCOUNT, item.getBalance());
                        bundle.putLong(BalanceDetailActivity.KEY_ASSET_ID, item.getPropertyid());
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "lightning");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            } else {
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
                        bundle.putString(BalanceDetailActivity.KEY_PUBKEY, pubkey);
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_ACCOUNT, item.getBalance());
                        bundle.putLong(BalanceDetailActivity.KEY_ASSET_ID, item.getPropertyid());
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "link");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            }
        }
    }

    /**
     * ????????????copy??????????????????
     * En???Click copy icon button,duplicate user`s wallet address to clipboard
     * author:Tong ChangHui
     * E-mail:tch081092@gmail.com
     * date:2022-10-08
     */
    @OnClick(R.id.iv_copy)
    public void clickCopy() {
        //???????????????????????????????????????
        //Get the address which will copy to clipboard
        String toCopyAddress = mWalletAddressTv.getText().toString();
        //????????????????????????????????????
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(AccountLightningActivity.this, toCopyAddress, toastString);
    }

    @OnClick(R.id.lv_network_title_content)
    public void clickSelectNode() {
        mSelectNodePopupWindow = new SelectNodePopupWindow(mContext);
        mSelectNodePopupWindow.show(mParentLayout);
    }

    @OnClick(R.id.iv_account_manage)
    public void clickAccount() {
        mAccountManagePopupWindow = new AccountManagePopupWindow(mContext);
        mAccountManagePopupWindow.show(mParentLayout);
    }

    /**
     * click Fund button
     * ??????Fund??????
     */
    @OnClick(R.id.iv_fund)
    public void clickFund() {
        mFundPopupWindow = new FundPopupWindow(mContext);
        mFundPopupWindow.show(mParentLayout, addressResp.getAddress());
    }

    /**
     * click send button
     * ??????send??????
     */
    @OnClick(R.id.iv_send)
    public void clickSend() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * click search button
     * ??????Search??????
     */
    @OnClick(R.id.iv_search)
    public void clickSearch() {
        switchActivity(SearchActivity.class);
    }

    /**
     * click filter button
     * ??????Filter??????
     */
    @OnClick(R.id.iv_filter)
    public void clickFilter() {

    }

    /**
     * ??????channel List??????
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        Bundle bundle = new Bundle();
        bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
        bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
        bundle.putString(ChannelsActivity.KEY_PUBKEY, pubkey);
        switchActivity(ChannelsActivity.class, bundle);
    }

    /**
     * click scan button at the top-right in page
     * ???????????????????????????
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        PermissionUtils.launchCamera(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                switchActivity(ScanActivity.class);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                LogUtils.e(TAG, "?????????????????????????????????");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                LogUtils.e(TAG, "?????????????????????????????????????????????????????????");
            }
        });
    }

    /**
     * click top-right menu button
     * ???????????????????????????
     */
    @OnClick(R.id.iv_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv, balanceAmount, addressResp.getAddress(), pubkey);
    }

    /**
     * Click create channel
     * ??????????????????
     */
    @OnClick(R.id.layout_create_channel)
    public void clickCreateChannel() {
        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
        mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, addressResp.getAddress(), pubkey);
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
        if (mAccountManagePopupWindow != null) {
            mAccountManagePopupWindow.release();
        }
        if (mSelectNodePopupWindow != null) {
            mSelectNodePopupWindow.release();
        }
    }
}
