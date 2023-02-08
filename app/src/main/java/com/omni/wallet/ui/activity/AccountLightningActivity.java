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

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.data.AccountAssetsData;
import com.omni.wallet.entity.AssetTrendEntity;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.BtcAndUsdtEvent;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.entity.event.LockEvent;
import com.omni.wallet.entity.event.LoginOutEvent;
import com.omni.wallet.entity.event.OpenChannelEvent;
import com.omni.wallet.entity.event.RebootEvent;
import com.omni.wallet.entity.event.ScanResultEvent;
import com.omni.wallet.entity.event.SelectAccountEvent;
import com.omni.wallet.entity.event.SendSuccessEvent;
import com.omni.wallet.entity.event.UpdateBalanceEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.view.AssetTrendChartView;
import com.omni.wallet.view.dialog.CreateChannelDialog;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.dialog.PayInvoiceDialog;
import com.omni.wallet.view.dialog.SendDialog;
import com.omni.wallet.view.popupwindow.AccountManagePopupWindow;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.FundPopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import lnrpc.Stateservice;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

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
    @BindView(R.id.tv_price_change)
    TextView mPriceChangeTv;
    @BindView(R.id.tv_balance_amount)
    TextView mBalanceAmountTv;
    @BindView(R.id.tv_wallet_address)
    TextView mWalletAddressTv;
    @BindView(R.id.layout_asset_trend_chart_view)
    AssetTrendChartView mAssetTrendChartView;
    @BindView(R.id.recycler_assets_list_block)
    public RecyclerView mRecyclerViewBlock;// 资产列表的RecyclerViewBlock(The Recycler View Block for Assets List)
    private List<ListAssetItemEntity> blockData = new ArrayList<>();
    private List<ListAssetItemEntity> lightningData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<ListAssetItemEntity> allData = new ArrayList<>();
    private ByteString backupChannelBytes;
    private List<ByteString> channelBackupBytesList = new ArrayList<ByteString>();
    private List<Integer> outputIndexList = new ArrayList<Integer>();
    private List<ByteString> fundingTxIdBytesList = new ArrayList<ByteString>();
    private List<String> fundingTxIdStrList = new ArrayList<String>();


    MenuPopupWindow mMenuPopupWindow;
    FundPopupWindow mFundPopupWindow;
    AccountManagePopupWindow mAccountManagePopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    SendDialog mSendDialog;
    SelectNodePopupWindow mSelectNodePopupWindow;
    private LoadingDialog mLoadingDialog;
    CreateChannelDialog mCreateChannelDialog;
    PayInvoiceDialog mPayInvoiceDialog;

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
        EventBus.getDefault().post(new CloseUselessActivityEvent());
        mLoadingDialog = new LoadingDialog(mContext);
        DecimalFormat df = new DecimalFormat("0.00");
        mPriceChangeTv.setText(df.format(Double.parseDouble(User.getInstance().getBtcPriceChange(mContext))) + "%");
        mWalletAddressTv.setText(User.getInstance().getWalletAddress(mContext));
        initRecyclerView();
        setAssetTrendChartViewShow();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewBlock.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, allData, R.layout.layout_item_assets_list);
        mRecyclerViewBlock.setAdapter(mAdapter);
    }

    // TODO: 2023/1/12 待完善
    private void setAssetTrendChartViewShow() {
        AccountAssetsData accountAssetsData = AccountAssetsData.getInstance(mContext);
        /*List<Map<String,Object>> allList = null;
        try {
            allList = accountAssetsData.queryAmountForAll();
            List<AssetTrendEntity> list = new ArrayList<>();
            Log.e(TAG,"allList:" + allList.toString());
            if(allList.size()==1){
                AssetTrendEntity entity = new AssetTrendEntity();
                String date = TimeFormatUtil.formatDateLong(TimeFormatUtil.getCurrentDayMills()- ConstantInOB.DAY_MILLIS,mContext);
                entity.setTime(date);
                entity.setAsset(Double.toString(0));
                list.add(entity);
            }
            for (int i =0;i<allList.size();i++){
                AssetTrendEntity entity = new AssetTrendEntity();
                Map<String,Object> item = allList.get(i);
                String date = TimeFormatUtil.formatDateLong(Long.parseLong((String) item.get("date")),mContext);
                entity.setTime(date);
                entity.setAsset(Double.toString((Double) item.get("value")));
                list.add(entity);
            }
            mAssetTrendChartView.setViewShow(list);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        List<AssetTrendEntity> list = new ArrayList<>();
        AssetTrendEntity entity1 = new AssetTrendEntity();
        entity1.setTime("Jan");
        entity1.setAsset("5");
        AssetTrendEntity entity2 = new AssetTrendEntity();
        entity2.setTime("Feb");
        entity2.setAsset("2");
        AssetTrendEntity entity3 = new AssetTrendEntity();
        entity3.setTime("Mar");
        entity3.setAsset("7");
        AssetTrendEntity entity4 = new AssetTrendEntity();
        entity4.setTime("Apr");
        entity4.setAsset("2");
        AssetTrendEntity entity5 = new AssetTrendEntity();
        entity5.setTime("May");
        entity5.setAsset("4");
        AssetTrendEntity entity6 = new AssetTrendEntity();
        entity6.setTime("Jun");
        entity6.setAsset("5");
        AssetTrendEntity entity7 = new AssetTrendEntity();
        entity7.setTime("Jul");
        entity7.setAsset("8");
        AssetTrendEntity entity8 = new AssetTrendEntity();
        entity8.setTime("Aug");
        entity8.setAsset("4");
        AssetTrendEntity entity9 = new AssetTrendEntity();
        entity9.setTime("Sep");
        entity9.setAsset("6");
        AssetTrendEntity entity10 = new AssetTrendEntity();
        entity10.setTime("Oct");
        entity10.setAsset("7");
        AssetTrendEntity entity11 = new AssetTrendEntity();
        entity11.setTime("Nov");
        entity11.setAsset("10");
        AssetTrendEntity entity12 = new AssetTrendEntity();
        entity12.setTime("Dec");
        entity12.setAsset("7");
        list.add(entity1);
        list.add(entity2);
        list.add(entity3);
        list.add(entity4);
        list.add(entity5);
        list.add(entity6);
        list.add(entity7);
        list.add(entity8);
        list.add(entity9);
        list.add(entity10);
        list.add(entity11);
        list.add(entity12);
        mAssetTrendChartView.setViewShow(list);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        getInfo();
        setDefaultAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAssetAndBtcData();
        Stateservice.SubscribeStateRequest subscribeStateRequest = Stateservice.SubscribeStateRequest.newBuilder().build();
        Obdmobile.subscribeState(subscribeStateRequest.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    Stateservice.SubscribeStateResponse subscribeStateResponse = Stateservice.SubscribeStateResponse.parseFrom(bytes);
                    int stateValue = subscribeStateResponse.getStateValue();
                    LogUtils.e("state value", Integer.toString(stateValue));
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get wallet related information
     * 获取钱包相关信息
     */
    private void getInfo() {
        Obdmobile.oB_GetInfo(LightningOuterClass.GetInfoRequest.newBuilder().build().toByteArray(), new Callback() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pubkey = resp.getIdentityPubkey();
                            mNetworkTypeTv.setText(resp.getChains(0).getNetwork());
                            User.getInstance().setNetwork(mContext, resp.getChains(0).getNetwork());
                            User.getInstance().setNodeVersion(mContext, resp.getVersion());
                            User.getInstance().setFromPubKey(mContext, resp.getIdentityPubkey());
                        }
                    });
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
        LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_WalletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
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
                            if (resp.getConfirmedBalance() == 0) {
                                mBalanceValueTv.setText("$ 0.00");
                                mBalanceAmountTv.setText("My account 0.00 balance");
                            } else {
                                DecimalFormat df = new DecimalFormat("0.00######");
                                mBalanceValueTv.setText("$ " + df.format(Double.parseDouble(String.valueOf(resp.getConfirmedBalance())) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                                balanceAmount = resp.getConfirmedBalance();
                                mBalanceAmountTv.setText("My account " + df.format(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000) + " balance");
                                /*BTCData btcData = new BTCData(mContext);
                                try {
                                    btcData.updateAmount(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }*/
                            }
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
                            .setAddress(User.getInstance().getWalletAddress(mContext))
                            .build();
                    Obdmobile.oB_AssetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
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
                                    /*switch (Long.toString(resp.getListList().get(i).getPropertyid())){
                                        case "2147483651":
                                            DollarData dollarData = new DollarData(mContext);
                                            try {
                                                dollarData.updateAmount(resp.getListList().get(i).getBalance() / 100000000);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        default:
                                            break;
                                    }*/
                                    getChannelBalance(resp.getListList().get(i).getPropertyid());
                                }
                                allData.addAll(blockData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                getBtcChannelBalance(0);
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
     * get btc Channel Balance
     * 查询BTC通道余额
     *
     * @param propertyid
     */
    private void getBtcChannelBalance(long propertyid) {
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
                            entity.setAmount(resp.getLocalBalance().getMsat() / 1000);
                            entity.setPropertyid(0);
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
//                    lightningData = null;
//                    allData.addAll(lightningData);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    });
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
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_SetDefaultAddress(setDefaultAddressRequest.toByteArray(), new Callback() {
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
            } else {
                LinearLayout lvContent = holder.getView(R.id.lv_item_content);
                lvContent.setPadding(0, 0, 0, 0);
            }
            // TODO: 2022/11/14 暂定待修改与完善
            if (item.getPropertyid() == 0) {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_btc_logo_small);
            } else {
                holder.setImageResource(R.id.iv_asset_logo, R.mipmap.icon_usdt_logo_small);
            }
            if (item.getAmount() == 0) {
                holder.setText(R.id.tv_asset_amount, "0.00");
                holder.setText(R.id.tv_asset_value, "0.00");
            } else {
                DecimalFormat df = new DecimalFormat("0.00######");
                if (item.getPropertyid() == 0) {
                    holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                    holder.setText(R.id.tv_asset_value, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                } else {
                    holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                    holder.setText(R.id.tv_asset_value, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                }
            }
            if (item.getType() == 1) {
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_link_black);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(BalanceDetailActivity.KEY_BALANCE_AMOUNT, balanceAmount);
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, User.getInstance().getWalletAddress(mContext));
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
                        bundle.putString(BalanceDetailActivity.KEY_WALLET_ADDRESS, User.getInstance().getWalletAddress(mContext));
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
        mAccountManagePopupWindow = new AccountManagePopupWindow(mContext);
        mAccountManagePopupWindow.show(mParentLayout);
    }

    /**
     * click Fund button
     * 点击Fund按钮
     */
    @OnClick(R.id.iv_fund)
    public void clickFund() {
        mFundPopupWindow = new FundPopupWindow(mContext);
        mFundPopupWindow.show(mParentLayout, User.getInstance().getWalletAddress(mContext));
    }

    /**
     * click send button
     * 点击send按钮
     */
    @OnClick(R.id.iv_send)
    public void clickSend() {
        mSendDialog = new SendDialog(mContext);
        mSendDialog.show("");
    }

    /**
     * click search button
     * 点击Search按钮
     */
    @OnClick(R.id.iv_search)
    public void clickSearch() {
        ToastUtils.showToast(mContext, "Not yet open, please wait");
//        switchActivity(SearchActivity.class);
    }

    /**
     * click filter button
     * 点击Filter按钮
     */
    @OnClick(R.id.iv_filter)
    public void clickFilter() {
        ToastUtils.showToast(mContext, "Not yet open, please wait");
    }

    /**
     * 点击channel List按钮
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        Bundle bundle = new Bundle();
        bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
        bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, User.getInstance().getWalletAddress(mContext));
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
                Bundle bundle = new Bundle();
                bundle.putInt(ScanActivity.KEY_SCAN_CODE, 1);
                switchActivity(ScanActivity.class, bundle);
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
        mMenuPopupWindow.show(mMenuIv, balanceAmount, User.getInstance().getWalletAddress(mContext), pubkey);
    }

    /**
     * Click create channel
     * 点击创建通道
     */
    @OnClick(R.id.layout_create_channel)
    public void clickCreateChannel() {
        mCreateChannelDialog = new CreateChannelDialog(mContext);
        mCreateChannelDialog.show(balanceAmount, User.getInstance().getWalletAddress(mContext), "");
//        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//        mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, User.getInstance().getWalletAddress(mContext), "");
    }

    /**
     * 扫码后的消息通知监听
     * Message notification monitoring after Scan qrcode
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResultEvent(ScanResultEvent event) {
        if (event.getCode() == 1) {
            if (event.getType().equals("payInvoice")) {
                LightningOuterClass.PayReqString decodePaymentRequest = LightningOuterClass.PayReqString.newBuilder()
                        .setPayReq(UriUtil.removeURI(event.getData().toLowerCase()))
                        .build();
                Obdmobile.decodePayReq(decodePaymentRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------decodePaymentOnError------------------" + e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        if (bytes == null) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    LightningOuterClass.PayReq resp = LightningOuterClass.PayReq.parseFrom(bytes);
                                    LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + resp);
                                    mPayInvoiceDialog = new PayInvoiceDialog(mContext);
                                    mPayInvoiceDialog.show(pubkey, resp.getAssetId(), event.getData());
//                                    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
//                                    mPayInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, resp.getAssetId(), event.getData());
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } else if (event.getType().equals("openChannel")) {
                mCreateChannelDialog = new CreateChannelDialog(mContext);
                mCreateChannelDialog.show(balanceAmount, User.getInstance().getWalletAddress(mContext), event.getData());
//                mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//                mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, User.getInstance().getWalletAddress(mContext), event.getData());
            } else if (event.getType().equals("send")) {
                mSendDialog = new SendDialog(mContext);
                mSendDialog.show(event.getData());
            }
        }
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
        User.getInstance().setWalletAddress(mContext, event.getAddress());
        getInfo();
        getAssetAndBtcData();
        setDefaultAddress();
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

    /**
     * btc和usdt变化后的消息通知监听
     * Message notification monitoring after Btc and Usdt change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBtcAndUsdtEvent(BtcAndUsdtEvent event) {
        DecimalFormat df = new DecimalFormat("0.00");
        mPriceChangeTv.setText(df.format(Double.parseDouble(User.getInstance().getBtcPriceChange(mContext))) + "%");
        getAssetAndBtcData();
    }

    /**
     * 余额变化后的消息通知监听
     * Message notification monitoring after update balance
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateBalanceEvent(UpdateBalanceEvent event) {
        DecimalFormat df = new DecimalFormat("0.00");
        mPriceChangeTv.setText(df.format(Double.parseDouble(User.getInstance().getBtcPriceChange(mContext))) + "%");
        getAssetAndBtcData();
    }

    /**
     * 退出登录后的消息通知监听
     * Message notification monitoring after login out
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginOutEvent(LoginOutEvent event) {
        finish();
    }

    /**
     * 锁住钱包后的消息通知监听
     * Message notification monitoring after lock
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLockEvent(LockEvent event) {
        switchActivityFinish(UnlockActivity.class);
    }

    /**
     * 重启节点后的消息通知监听
     * Message notification monitoring after reboot
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRebootEvent(RebootEvent event) {
        finish();
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
        if (mSendDialog != null) {
            mSendDialog.release();
        }
        if (mAccountManagePopupWindow != null) {
            mAccountManagePopupWindow.release();
        }
        if (mSelectNodePopupWindow != null) {
            mSelectNodePopupWindow.release();
        }
        if (mPayInvoiceDialog != null) {
            mPayInvoiceDialog.release();
        }
        if (mCreateChannelDialog != null) {
            mCreateChannelDialog.release();
        }
    }
}
