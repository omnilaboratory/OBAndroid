package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
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
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.OpenChannelEvent;
import com.omni.wallet.entity.event.SelectAccountEvent;
import com.omni.wallet.entity.event.SendSuccessEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.popupwindow.AccountManagePopupWindow;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.FundPopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;
import com.omni.wallet.view.popupwindow.send.SendStepOnePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class AccountLightningActivity extends AppBaseActivity {
    private static final String TAG = AccountLightningActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.tv_network_type)
    TextView mNetworkTypeTv;
    @BindView(R.id.iv_menu)
    ImageView mMenuIv;
    @BindView(R.id.tv_balance_value)
    TextView mBalanceValueTv;
    @BindView(R.id.tv_balance_amount)
    TextView mBalanceAmountTv;
    @BindView(R.id.tv_wallet_address)
    TextView mWalletAddressTv;
    @BindView(R.id.recycler_assets_list_block)
    public RecyclerView mRecyclerViewBlock;// 资产列表的RecyclerViewBlock(The Recycler View Block for Assets List)
    private List<ListAssetItemEntity> blockData = new ArrayList<>();
    private List<ListAssetItemEntity> lightningData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<ListAssetItemEntity> allData = new ArrayList<>();
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
    private String address;

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
        initRecyclerView();
        // TODO: 2022/11/21 待修改
        User.getInstance().setWalletAddress(mContext, "mqztMoe8LpFrkpZDKMm4kzVTSDr1vUvJXZ");
    }

    /**
     * 测试用方法生成block assets 数据
     * Test data for block assets
     */
    private void initBlockAssets() {
//        Map a = new HashMap<String, String>();
//        a.put("tokenImageSource", R.mipmap.icon_usdt_logo_small);
//        a.put("networkImageSource", R.mipmap.icon_network_link_black);
//        a.put("amount", 10000.0000f);
//        a.put("value", 70000.0000f);
//        Map b = new HashMap<String, String>();
//        b.put("tokenImageSource", R.mipmap.icon_btc_logo_small);
//        b.put("networkImageSource", R.mipmap.icon_network_link_black);
//        b.put("amount", 10000.0000f);
//        b.put("value", 70000.0000f);
//        blockData.add(a);
//        blockData.add(b);
//        blockData.add(a);
//        blockData.add(b);
    }

    /**
     * 测试用方法生成 Lightning assets 数据
     * Test data for Lightning assets
     */
    private void initLightningAssets() {
//        Map c = new HashMap<String, String>();
//        c.put("tokenImageSource", R.mipmap.icon_usdt_logo_small);
//        c.put("networkImageSource", R.mipmap.icon_network_vector);
//        c.put("amount", 10000.0000f);
//        c.put("value", 70000.0000f);
//        Map d = new HashMap<String, String>();
//        d.put("tokenImageSource", R.mipmap.icon_btc_logo_small);
//        d.put("networkImageSource", R.mipmap.icon_network_vector);
//        d.put("amount", 10000.0000f);
//        d.put("value", 70000.0000f);
//        lightningData.add(c);
//        lightningData.add(d);
//        lightningData.add(c);
//        lightningData.add(d);
    }

    private void initAllData() {
        allData.addAll(blockData);
        allData.addAll(lightningData);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
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
        EventBus.getDefault().register(this);
        getInfo();
        getAssetAndBtcData();
        setDefaultAddress();
    }

    /**
     * Get wallet related information
     * 获取钱包相关信息
     */
    private void getInfo() {
        Obdmobile.getInfo(LightningOuterClass.GetInfoRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------getInfoOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.GetInfoResponse resp = LightningOuterClass.GetInfoResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------getInfoOnResponse-----------------" + resp);
                    pubkey = resp.getIdentityPubkey();
                    mNetworkTypeTv.setText(resp.getChains(0).getNetwork());
                    User.getInstance().setNetwork(mContext, resp.getChains(0).getNetwork());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create a new wallet address first, and then request the interface of each asset balance list
     * 先创建新的钱包地址后再去请求各资产余额列表的接口
     */
    private void getAssetAndBtcData() {
        allData.clear();
        LightningOuterClass.NewAddressRequest asyncNewAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder()
                .setTypeValue(2)
                .build();
        Obdmobile.newAddress(asyncNewAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------newAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    addressResp = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    mWalletAddressTv.setText(addressResp.getAddress());
                    LogUtils.e(TAG, "------------------newAddressOnResponse-----------------" + addressResp.getAddress());
                    LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                            .setAddress("mqztMoe8LpFrkpZDKMm4kzVTSDr1vUvJXZ")
                            .build();
                    Obdmobile.walletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------walletBalanceByAddressOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            if (bytes == null) {
                                return;
                            }
                            try {
                                LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------walletBalanceByAddressOnResponse-----------------" + resp);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBalanceValueTv.setText("$ " + resp.getConfirmedBalance());
                                        balanceAmount = resp.getConfirmedBalance();
                                        mBalanceAmountTv.setText("My account " + balanceAmount + " balance");
                                        blockData.clear();
                                        ListAssetItemEntity entity = new ListAssetItemEntity();
                                        entity.setAmount(resp.getConfirmedBalance());
                                        entity.setPropertyid(0);
                                        entity.setType(1);
                                        blockData.add(entity);
                                        allData.addAll(blockData);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                                /**
                                 * request the interface of each asset balance list
                                 * 请求各资产余额列表的接口
                                 */
                                LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                                        .setAddress("mqztMoe8LpFrkpZDKMm4kzVTSDr1vUvJXZ")
                                        .build();
                                Obdmobile.assetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
                                    @Override
                                    public void onError(Exception e) {
                                        LogUtils.e(TAG, "------------------assetsBalanceOnError------------------" + e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(byte[] bytes) {
                                        if (bytes == null) {
                                            return;
                                        }
                                        try {
                                            LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                                            LogUtils.e(TAG, "------------------assetsBalanceOnResponse------------------" + resp.getListList().toString());
                                            blockData.clear();
                                            for (int i = 0; i < resp.getListList().size(); i++) {
                                                ListAssetItemEntity entity = new ListAssetItemEntity();
                                                entity.setAmount(resp.getListList().get(i).getBalance());
                                                entity.setPropertyid(resp.getListList().get(i).getPropertyid());
                                                entity.setType(1);
                                                blockData.add(entity);
                                                getChannelBalance(resp.getListList().get(i).getPropertyid());
                                            }
                                            allData.addAll(blockData);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            });
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
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * get Channel Balance
     * 查询通道余额
     *
     * @param propertyid
     */
    private void getChannelBalance(long propertyid) {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) propertyid)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    lightningData = null;
                    allData.addAll(lightningData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    return;
                }
                try {
                    LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightningData.clear();
                            ListAssetItemEntity entity = new ListAssetItemEntity();
                            entity.setAmount(resp.getLocalBalance().getMsat());
                            entity.setPropertyid(propertyid);
                            entity.setType(2);
                            lightningData.add(entity);
                            allData.addAll(lightningData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the created new wallet address as the default wallet address
     * 将创建新的钱包地址设置为默认钱包地址
     */
    private void setDefaultAddress() {
        LightningOuterClass.SetDefaultAddressRequest setDefaultAddressRequest = LightningOuterClass.SetDefaultAddressRequest.newBuilder()
                .setAddress("mqztMoe8LpFrkpZDKMm4kzVTSDr1vUvJXZ")
                .build();
        Obdmobile.setDefaultAddress(setDefaultAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------setDefaultAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.SetDefaultAddressResponse resp = LightningOuterClass.SetDefaultAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------setDefaultAddressOnResponse------------------" + resp.toString());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 资产列表适配器
     * The adapter for assets list
     */
    private class MyAdapter extends CommonRecyclerAdapter<ListAssetItemEntity> {

        public MyAdapter(Context context, List<ListAssetItemEntity> data, int layoutId) {
            super(context, data, layoutId);
        }


        @Override
        public void convert(ViewHolder holder, final int position, final ListAssetItemEntity item) {
            if (position == blockData.size() && item.getType() == 1) {
                LinearLayout lvContent = holder.getView(R.id.lv_item_content);
                lvContent.setPadding(0, 0, 0, 100);
            }
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
            // TODO: 2022/11/14 暂定待修改与完善
            if (item.getPropertyid() == 0) {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_btc_logo_small);
            } else {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_usdt_logo_small);
            }
            holder.setText(R.id.tv_asset_amount, String.valueOf(item.getAmount()));
            holder.setText(R.id.tv_asset_value, String.valueOf(item.getAmount()));
            if (item.getType() == 1) {
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_link_black);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
                        bundle.putString(BalanceDetailActivity.KEY_PUBKEY, pubkey);
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_ACCOUNT, item.getAmount());
                        bundle.putLong(BalanceDetailActivity.KEY_ASSET_ID, item.getPropertyid());
                        bundle.putString(BalanceDetailActivity.KEY_NETWORK, "link");
                        switchActivity(BalanceDetailActivity.class, bundle);
                    }
                });
            } else {
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_vector);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
                        bundle.putString(BalanceDetailActivity.KEY_PUBKEY, pubkey);
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_ACCOUNT, item.getAmount());
                        bundle.putLong(BalanceDetailActivity.KEY_ASSET_ID, item.getPropertyid());
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
        String toCopyAddress = mWalletAddressTv.getText().toString();
        //接收需要复制成功的提示语
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
        LightningOuterClass.ListRecAddressRequest listRecAddressRequest = LightningOuterClass.ListRecAddressRequest.newBuilder()
                .build();
        Obdmobile.listRecAddress(listRecAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listRecAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListRecAddressResponse resp = LightningOuterClass.ListRecAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listRecAddressOnResponse-----------------" + resp);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            mAccountManagePopupWindow = new AccountManagePopupWindow(mContext);
                            mAccountManagePopupWindow.show(mParentLayout, resp.getItemsList());
                            Looper.loop();
                        }
                    }).start();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * click Fund button
     * 点击Fund按钮
     */
    @OnClick(R.id.iv_fund)
    public void clickFund() {
        mFundPopupWindow = new FundPopupWindow(mContext);
        mFundPopupWindow.show(mParentLayout, addressResp.getAddress());
    }

    /**
     * click send button
     * 点击send按钮
     */
    @OnClick(R.id.iv_send)
    public void clickSend() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * click search button
     * 点击Search按钮
     */
    @OnClick(R.id.iv_search)
    public void clickSearch() {
        switchActivity(SearchActivity.class);
    }

    /**
     * click filter button
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
        Bundle bundle = new Bundle();
        bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
        bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, addressResp.getAddress());
        bundle.putString(ChannelsActivity.KEY_PUBKEY, pubkey);
        switchActivity(ChannelsActivity.class, bundle);
    }

    /**
     * click scan button at the top-right in page
     * 点击右上角扫码按钮
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
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
            }
        });
    }

    /**
     * click top-right menu button
     * 点击右上角菜单按钮
     */
    @OnClick(R.id.iv_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv, balanceAmount, addressResp.getAddress(), pubkey);
    }

    /**
     * Click create channel
     * 点击创建通道
     */
    @OnClick(R.id.layout_create_channel)
    public void clickCreateChannel() {
        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
        mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, addressResp.getAddress(), pubkey);
    }


    /**
     * 选择钱包地址后的消息通知监听
     * Message notification monitoring after selecting wallet address
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectAccountEvent(SelectAccountEvent event) {
        if (event == null) {
            return;
        }
        address = event.getAddress();
    }

    /**
     * 支付成功后的消息通知监听
     * Message notification monitoring after successful payment
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSuccessEvent(SendSuccessEvent event) {
        getAssetAndBtcData();
    }

    /**
     * 开通通道后的消息通知监听
     * Message notification monitoring after open channel
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenChannelEvent(OpenChannelEvent event) {
        getAssetAndBtcData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
