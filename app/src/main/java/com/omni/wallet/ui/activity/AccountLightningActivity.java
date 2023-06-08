package com.omni.wallet.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.baselibrary.view.refreshView.RefreshLayout;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.data.AssetsActions;
import com.omni.wallet.data.AssetsValueDataItem;
import com.omni.wallet.data.ChangeData;
import com.omni.wallet.data.ChartData;
import com.omni.wallet.entity.AssetEntity;
import com.omni.wallet.entity.AssetTrendEntity;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.BackUpEvent;
import com.omni.wallet.entity.event.BtcAndUsdtEvent;
import com.omni.wallet.entity.event.CloseChannelEvent;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.entity.event.CreateInvoiceEvent;
import com.omni.wallet.entity.event.DownloadEvent;
import com.omni.wallet.entity.event.InitChartEvent;
import com.omni.wallet.entity.event.LaunchEvent;
import com.omni.wallet.entity.event.LockEvent;
import com.omni.wallet.entity.event.LoginOutEvent;
import com.omni.wallet.entity.event.OpenChannelEvent;
import com.omni.wallet.entity.event.PayInvoiceSuccessEvent;
import com.omni.wallet.entity.event.RebootEvent;
import com.omni.wallet.entity.event.ScanResultEvent;
import com.omni.wallet.entity.event.SelectAccountEvent;
import com.omni.wallet.entity.event.SendSuccessEvent;
import com.omni.wallet.entity.event.StartNodeEvent;
import com.omni.wallet.entity.event.UpdateAssetsDataEvent;
import com.omni.wallet.entity.event.UpdateBalanceEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.framelibrary.view.refreshlayout.LayoutRefreshView;
import com.omni.wallet.obdMethods.NodeStart;
import com.omni.wallet.obdMethods.WalletState;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.DriveServiceHelper;
import com.omni.wallet.utils.GetResourceUtil;
import com.omni.wallet.utils.MoveCacheFileToFileObd;
import com.omni.wallet.utils.TimeFormatUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.view.AssetTrendChartView;
import com.omni.wallet.view.dialog.CreateChannelDialog;
import com.omni.wallet.view.dialog.DataStatusDialog;
import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.dialog.PayInvoiceDialog;
import com.omni.wallet.view.dialog.ReceiveLuckyPacketDialog;
import com.omni.wallet.view.dialog.SendDialog;
import com.omni.wallet.view.popupwindow.AccountManagePopupWindow;
import com.omni.wallet.view.popupwindow.FundPopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;
import com.omni.wallet.view.popupwindow.SelectReceiveOrSendTypePopupWindow;
import com.omni.wallet.view.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.refresh_layout_account_lightning)
    public RefreshLayout mRefreshLayout;
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
    @BindView(R.id.iv_percent_change)
    ImageView mPercentChangeView;
    @BindView(R.id.sync_percent)
    TextView syncPercentView;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.view_receive_bottom)
    View mReceiveBottomView;
    @BindView(R.id.view_send_bottom)
    View mSendBottomView;
    private List<ListAssetItemEntity> blockData = new ArrayList<>();
    private List<ListAssetItemEntity> lightningData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<ListAssetItemEntity> allData = new ArrayList<>();
    List<AssetsValueDataItem> allChartDataList;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    MenuPopupWindow mMenuPopupWindow;
    FundPopupWindow mFundPopupWindow;
    AccountManagePopupWindow mAccountManagePopupWindow;
    SendDialog mSendDialog;
    SelectNodePopupWindow mSelectNodePopupWindow;
    private LoadingDialog mLoadingDialog;
    CreateChannelDialog mCreateChannelDialog;
    PayInvoiceDialog mPayInvoiceDialog;

    long balanceAmount;
    private String pubkey;
    boolean isRequest = false;

    Handler handler = new Handler();

    private static final int REQUEST_CODE_SIGN_IN = 3;
    private DriveServiceHelper mDriveServiceHelper;

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
        // 初始化下拉刷新(Initialize pull-down refresh)
        mRefreshLayout.setRefreshListener(new MyRefreshListener());
        mRefreshLayout.addRefreshHeader(new LayoutRefreshView());
//        mRefreshLayout.autoRefresh();
        //解决RefreshLayout与ScrollView滑动冲突(Resolve sliding conflicts between RefreshLayout and ScrollView)
//        myScrollView.setScrollViewListener(new MyScrollView.ScrollViewListener() {
//            @Override
//            public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
//                if (y <= 0) {
//                    mRefreshLayout.setEnabled(true);
//                } else {
//                    mRefreshLayout.setEnabled(false);
//                }
//            }
//        });
        EventBus.getDefault().post(new CloseUselessActivityEvent());
        mLoadingDialog = new LoadingDialog(mContext);
        initRecyclerView();
    }

    /**
     * Load the refresh listener
     * 加载刷新的监听
     */
    private class MyRefreshListener implements RefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            if (!User.getInstance().isNeutrinoDbChecked(mContext)) {
                mRefreshLayout.stopRefresh();
                return;
            }
            if (StringUtils.isEmpty(User.getInstance().getWalletAddress(mContext))) {
                showPageData();
            } else {
                getAssetAndBtcData();
            }
        }
    }

    @Override
    protected void initData() {
        if (ConstantInOB.networkType == NetworkType.TEST) {
            mNetworkTypeTv.setText("testnet");
        } else if (ConstantInOB.networkType == NetworkType.REG) {
            mNetworkTypeTv.setText("regtest");
        } else if (ConstantInOB.networkType == NetworkType.MAIN) {
            mNetworkTypeTv.setText("mainnet");
        }
        EventBus.getDefault().register(this);
        if (User.getInstance().isBackUp(mContext) == true) {
            mLoadingDialog.show();
            showPageData();
        }
        if (User.getInstance().isNeutrinoDbChecked(mContext)) {
            // TODO: 2023/5/31 暂定
            if (StringUtils.isEmpty(User.getInstance().getFirstLogin(mContext))) {
                startNode();
                User.getInstance().setFirstLogin(mContext, "first");
            }
            double percent = (100 / 100 * 100);
            String percentString = String.format("%.2f", percent) + "%";
            syncPercentView.setText(percentString);
            mProgressBar.setProgress((int) percent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequest == true) {
            getAssetAndBtcData();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewBlock.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, allData, R.layout.layout_item_assets_list);
        mRecyclerViewBlock.setAdapter(mAdapter);
        mRecyclerViewBlock.setHasFixedSize(true);
        mRecyclerViewBlock.setNestedScrollingEnabled(false);
        mRecyclerViewBlock.setFocusable(false);
        mRecyclerViewBlock.addOnScrollListener(new RecyclerView.OnScrollListener() {
            View firstChild;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // 方法一(Method One)
                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    firstChild = recyclerView.getChildAt(0);
                }
                int firstChildPosition = firstChild == null ? 0 : recyclerView.getChildAdapterPosition(firstChild);
                mRefreshLayout.setEnabled(firstChildPosition == 0 && firstChild.getTop() >= 0);
                /*// 方法二(Method Two)
                int position = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mRefreshLayout.setEnabled(position >= 0 && recyclerView != null && !recyclerView.canScrollVertically(-1));*/
            }
        });
    }

    // TODO: 2023/1/12 待完善
    @SuppressLint("LongLogTag")
    private void setAssetTrendChartViewShow() {
        // get data for line chart
        ChartData data = AssetsActions.getDataForChart(mContext);
        Log.d(TAG + "setAssetTrendChartViewShow", data.toString());
        List<AssetsValueDataItem> allList;
        try {
            allList = data.getChartDataList();
            assert allList != null;
            if (!allList.equals(allChartDataList)) {
                List<AssetTrendEntity> list = new ArrayList<>();
                Log.d(TAG, "allList:" + allList.toString());
                if (allList.size() == 1) {
                    AssetTrendEntity entity = new AssetTrendEntity();
                    String date = TimeFormatUtil.formatDateLong(TimeFormatUtil.getCurrentDayMills() - ConstantInOB.DAY_MILLIS);
                    entity.setTime(date);
                    entity.setAsset(Double.toString(0));
                    list.add(entity);
                }
                for (int i = 0; i < allList.size(); i++) {
                    AssetTrendEntity entity = new AssetTrendEntity();
                    AssetsValueDataItem item = allList.get(i);
                    String date = TimeFormatUtil.formatDateLong(item.getUpdate_date());
                    entity.setTime(date);
                    entity.setAsset(Double.toString(item.getValue()));
                    list.add(entity);
                }

                // set text for change percent and now assets value
                ChangeData changeData = data.getChangeData();
                assert changeData != null;
                Log.d(TAG + "changeData", changeData.toString());
                double percent = changeData.getPercent();
                @SuppressLint("DefaultLocale") String percentString = String.format("%.2f", percent) + "%";
                Log.d(TAG, "setAssetTrendChartViewShow: " + percentString);
                double value = changeData.getValue();
                @SuppressLint("DefaultLocale") String valueString = "$ " + String.format("%.2f", value);
                mPriceChangeTv.setText(percentString);
                if (percent > 0) {
                    mPriceChangeTv.setTextColor(GetResourceUtil.getColorId(mContext, R.color.color_06d78f));
                    mPercentChangeView.setImageResource(R.mipmap.icon_arrow_up_green);
                } else {
                    mPriceChangeTv.setTextColor(GetResourceUtil.getColorId(mContext, R.color.color_F13A3A));
                    mPercentChangeView.setImageResource(R.mipmap.icon_arrow_down_red);
                }
                mBalanceValueTv.setText(valueString);
                mAssetTrendChartView.setViewShow(list);
            }

            // Notify the page to update data
            EventBus.getDefault().post(new UpdateAssetsDataEvent());
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                    runOnUiThread(() -> {
                        pubkey = resp.getIdentityPubkey();
//                        mNetworkTypeTv.setText(resp.getChains(0).getNetwork());
                        User.getInstance().setNetwork(mContext, resp.getChains(0).getNetwork());
                        User.getInstance().setNodeVersion(mContext, resp.getVersion());
                        User.getInstance().setFromPubKey(mContext, resp.getIdentityPubkey());
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

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------walletBalanceByAddressOnResponse-----------------" + resp);
                    runOnUiThread(() -> {
                        if (resp.getConfirmedBalance() == 0) {
                            mBalanceAmountTv.setText("My account 0.00 balance");
                        } else {
                            DecimalFormat df = new DecimalFormat("0.00######");
                            User.getInstance().setBalanceAmount(mContext, resp.getConfirmedBalance());
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
                        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                        getBtcChannelBalance(0);
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
                            runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                            if (mRefreshLayout != null) {
                                mRefreshLayout.stopRefresh();
                            }
                            getAssetData();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * request the interface of each asset balance list
     * 请求各资产余额列表的接口
     */
    private void getAssetData() {
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_AssetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------assetsBalanceOnError------------------" + e.getMessage());
                setDefaultData();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    setDefaultData();
                    return;
                }
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------assetsBalanceOnResponse------------------" + resp.getListList().toString());
                    if (ConstantInOB.networkType == NetworkType.TEST) {
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("2147485160")) {
                                setDefaultData();
                            }
                        }
                    } else if (ConstantInOB.networkType == NetworkType.REG) {
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("2147483651")) {
                                setDefaultData();
                            }
                        }
                    } else { //mainnet
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("31")) {
                                setDefaultData();
                            }
                        }
                    }
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
                    runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDefaultData() {
        blockData.clear();
        ListAssetItemEntity entity = new ListAssetItemEntity();
        entity.setAmount(0);
        if (ConstantInOB.networkType == NetworkType.TEST) {
            entity.setPropertyid(Long.parseLong("2147485160"));
        } else if (ConstantInOB.networkType == NetworkType.REG) {
            entity.setPropertyid(Long.parseLong("2147483651"));
        } else { //mainnet
            entity.setPropertyid(Long.parseLong("31"));
        }
        entity.setType(1);
        blockData.add(entity);
        allData.addAll(blockData);

        lightningData.clear();
        ListAssetItemEntity entity1 = new ListAssetItemEntity();
        entity1.setAmount(0);
        if (ConstantInOB.networkType == NetworkType.TEST) {
            entity1.setPropertyid(Long.parseLong("2147485160"));
        } else if (ConstantInOB.networkType == NetworkType.REG) {
            entity1.setPropertyid(Long.parseLong("2147483651"));
        } else { //mainnet
            entity1.setPropertyid(Long.parseLong("31"));
        }
        entity1.setType(2);
        lightningData.add(entity1);
        allData.addAll(lightningData);
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());

//        if (ConstantInOB.networkType == NetworkType.TEST) {
//            getChannelBalance(Long.parseLong("2147485160"));
//        } else if (ConstantInOB.networkType == NetworkType.REG) {
//            getChannelBalance(Long.parseLong("2147483651"));
//        } else { //mainnet
//            getChannelBalance(Long.parseLong("31"));
//        }
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
                            runOnUiThread(() -> mAdapter.notifyDataSetChanged());
                            if (mRefreshLayout != null) {
                                mRefreshLayout.stopRefresh();
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
            if ((position + 1) % 2 == 0) {
                LinearLayout lvContent = holder.getView(R.id.lv_item_content);
                lvContent.setPadding(0, 0, 0, 32);
                holder.getView(R.id.iv_asset_logo).setVisibility(View.INVISIBLE);
            } else {
                LinearLayout lvContent = holder.getView(R.id.lv_item_content);
                lvContent.setPadding(0, 0, 0, 0);
                holder.getView(R.id.iv_asset_logo).setVisibility(View.VISIBLE);
            }
            ImageView imageView = holder.getView(R.id.iv_asset_logo);
            mAssetData.clear();
            Gson gson = new Gson();
            mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
            }.getType());
            for (AssetEntity entity : mAssetData) {
                if (Long.parseLong(entity.getAssetId()) == item.getPropertyid()) {
                    ImageUtils.showImage(mContext, entity.getImgUrl(), imageView);
                }
            }
            if (item.getAmount() == 0) {
                holder.setText(R.id.tv_asset_amount, "0.00");
                holder.setText(R.id.tv_asset_value, "0.00");
            } else {
                DecimalFormat df = new DecimalFormat("0.00######");
                DecimalFormat df1 = new DecimalFormat("0.00");
                if (item.getPropertyid() == 0) {
                    holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                    holder.setText(R.id.tv_asset_value, df1.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                } else {
                    holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                    holder.setText(R.id.tv_asset_value, df1.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                }
            }
            if (item.getType() == 1) {
                holder.setImageResource(R.id.iv_asset_net, R.mipmap.icon_network_link_black_small);
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
                        for (AssetEntity entity : mAssetData) {
                            if (Long.parseLong(entity.getAssetId()) == item.getPropertyid()) {
                                bundle.putString(BalanceDetailActivity.KEY_NAME, entity.getName());
                                bundle.putString(BalanceDetailActivity.KEY_IMAGE_URL, entity.getImgUrl());
                            }
                        }
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
                        for (AssetEntity entity : mAssetData) {
                            if (Long.parseLong(entity.getAssetId()) == item.getPropertyid()) {
                                bundle.putString(BalanceDetailActivity.KEY_NAME, entity.getName());
                                bundle.putString(BalanceDetailActivity.KEY_IMAGE_URL, entity.getImgUrl());
                            }
                        }
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
        if (StringUtils.isEmpty(User.getInstance().getWalletAddress(mContext))) {
            ToastUtils.showToast(mContext, "Please waiting for a while");
            return;
        }
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
     * click scan button at the top-right in page
     * 点击右上角扫码按钮
     */
    @OnClick(R.id.layout_scan)
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
    @OnClick(R.id.layout_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv, balanceAmount, User.getInstance().getWalletAddress(mContext), pubkey);
    }

    /**
     * Click progress bar
     * 点击进度条
     */
    @OnClick(R.id.download_view)
    public void clickDownloadView() {
        DataStatusDialog mDataStatusDialog = new DataStatusDialog(mContext);
        mDataStatusDialog.show();
    }

    /**
     * click Receive button
     * 点击Receive按钮
     */
    @OnClick(R.id.layout_receive)
    public void clickFund() {
        SelectReceiveOrSendTypePopupWindow mTypePopupWindow = new SelectReceiveOrSendTypePopupWindow(mContext);
        mTypePopupWindow.setOnItemClickCallback(new SelectReceiveOrSendTypePopupWindow.ItemCleckListener() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_type_one:
                        CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
                        mCreateInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, 0, 0);
                        break;
                    case R.id.tv_type_two:
                        if (StringUtils.isEmpty(User.getInstance().getWalletAddress(mContext))) {
                            ToastUtils.showToast(mContext, "Please waiting for a while");
                            return;
                        }
                        mFundPopupWindow = new FundPopupWindow(mContext);
                        mFundPopupWindow.show(mParentLayout, User.getInstance().getWalletAddress(mContext));
                        break;
                }
            }
        });
        mTypePopupWindow.show(mReceiveBottomView, 1);
    }

    /**
     * click send button
     * 点击send按钮
     */
    @OnClick(R.id.layout_send)
    public void clickSend() {
        SelectReceiveOrSendTypePopupWindow mTypePopupWindow = new SelectReceiveOrSendTypePopupWindow(mContext);
        mTypePopupWindow.setOnItemClickCallback(new SelectReceiveOrSendTypePopupWindow.ItemCleckListener() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_type_one:
                        mPayInvoiceDialog = new PayInvoiceDialog(mContext);
                        mPayInvoiceDialog.show(pubkey, 0, "", 1);
                        break;
                    case R.id.tv_type_two:
                        mSendDialog = new SendDialog(mContext);
                        mSendDialog.show("");
                        break;
                }
            }
        });
        mTypePopupWindow.show(mSendBottomView, 2);

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
        bundle.putString(ChannelsActivity.KEY_CHANNEL, "all");
        switchActivity(ChannelsActivity.class, bundle);
    }

    /**
     * Click Swap channel
     * 点击Swap按钮
     */
    @OnClick(R.id.layout_swap)
    public void clickSwap() {
        ToastUtils.showToast(mContext, "Not yet open, please wait");
    }

    /**
     * Click create channel
     * 点击创建通道
     */
    @OnClick(R.id.layout_create_channel)
    public void clickCreateChannel() {
        mCreateChannelDialog = new CreateChannelDialog(mContext);
        mCreateChannelDialog.show(balanceAmount, User.getInstance().getWalletAddress(mContext), "");
    }

    /**
     * Click bottom NFTs and MARKETs
     * 点击底部NFTs和MARKETs
     */
    @OnClick({R.id.layout_nfts, R.id.layout_markets})
    public void clickBottomNFTs() {
        ToastUtils.showToast(mContext, "Not yet open, please wait");
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
                                    mPayInvoiceDialog.show(pubkey, resp.getAssetId(), event.getData(), 1);
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            } else if (event.getType().equals("receiveLuckyPacket")) {
                LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + event.getData());
                ReceiveLuckyPacketDialog mReceiveLuckyPacketDialog = new ReceiveLuckyPacketDialog(mContext);
                mReceiveLuckyPacketDialog.show(event.getData());
            } else if (event.getType().equals("openChannel")) {
                mCreateChannelDialog = new CreateChannelDialog(mContext);
                mCreateChannelDialog.show(balanceAmount, User.getInstance().getWalletAddress(mContext), event.getData());
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
     * 关闭通道后的消息通知监听
     * Message notification monitoring after close channel
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseChannelEvent(CloseChannelEvent event) {
        getAssetAndBtcData();
    }

    /**
     * btc和usdt变化后的消息通知监听
     * Message notification monitoring after Btc and Usdt change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBtcAndUsdtEvent(BtcAndUsdtEvent event) {
        getAssetAndBtcData();
    }

    /**
     * 余额变化后的消息通知监听
     * Message notification monitoring after update balance
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateBalanceEvent(UpdateBalanceEvent event) {
        getAssetAndBtcData();
    }

    /**
     * 支付发票成功的消息通知监听
     * Message notification monitoring after pay invoice success
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayInvoiceSuccessEvent(PayInvoiceSuccessEvent event) {
        getAssetAndBtcData();
    }

    /**
     * 收取红包后的消息通知监听
     * Message notification monitoring after receive lucky packet
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateInvoiceEvent(CreateInvoiceEvent event) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateAssetsDataOver(UpdateAssetsDataEvent event) {
        Runnable runnable = () -> AssetsActions.initOrUpdateAction(mContext);
        handler.postDelayed(runnable, 60000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitChartOver(InitChartEvent event) {
        setAssetTrendChartViewShow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadEvent(DownloadEvent event) {
        double percent = (event.getCurrent() / event.getTotal() * 100);
        String percentString = String.format("%.2f", percent) + "%";
        syncPercentView.setText(percentString);
        mProgressBar.setProgress((int) percent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaunchEvent(LaunchEvent event) {
        startNode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartNodeEvent(StartNodeEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.show();
                showPageData();
            }
        });
    }

    private void showPageData() {
        WalletState.getInstance().subscribeWalletState(mContext);
        subscribeWalletState();
    }

    public void subscribeWalletState() {
        WalletState.WalletStateCallback walletStateCallback = (int walletState) -> {
            LogUtils.e("---------------", String.valueOf(walletState));
            if (walletState == 4) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        isRequest = true;
                        User.getInstance().setUserId(mContext, "1");
                        User.getInstance().setBackUp(mContext, true);
                    }
                });
                if (StringUtils.isEmpty(User.getInstance().getWalletAddress(mContext))) {
                    LightningOuterClass.NewAddressRequest newAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder().setTypeValue(2).build();
                    Obdmobile.oB_NewAddress(newAddressRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            if (bytes == null) {
                                return;
                            }
                            try {
                                LightningOuterClass.NewAddressResponse newAddressResponse = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                                String address = newAddressResponse.getAddress();
                                runOnUiThread(() -> {
                                    // 保存地址到本地(save wallet address to local)
                                    User.getInstance().setWalletAddress(mContext, address);
                                    mWalletAddressTv.setText(User.getInstance().getWalletAddress(mContext));
                                    getAssetAndBtcData();
                                    getInfo();
                                    setDefaultAddress();
                                    setAssetTrendChartViewShow();
                                });
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        mWalletAddressTv.setText(User.getInstance().getWalletAddress(mContext));
                        getAssetAndBtcData();
                        getInfo();
                        setDefaultAddress();
                        setAssetTrendChartViewShow();
                    });
                }
            }
        };
        WalletState.getInstance().setWalletStateCallback(walletStateCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackUpEventEvent(BackUpEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (event.getCode() == 1) {
                    File walletPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db");
                    File channelPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db");
                    String storagePath = Environment.getExternalStorageDirectory() + "/OBBackupFiles";
                    File toWalletPath = new File(Environment.getExternalStorageDirectory() + "/OBBackupFiles/wallet.db");
                    File toChannelPath = new File(Environment.getExternalStorageDirectory() + "/OBBackupFiles/channel.db");
                    if (walletPath.exists() && channelPath.exists()) {
                        // 本地备份(Local backup)
                        MoveCacheFileToFileObd.createDirs(storagePath);
                        MoveCacheFileToFileObd.copyFile(walletPath, toWalletPath);
                        MoveCacheFileToFileObd.copyFile(channelPath, toChannelPath);
                        MoveCacheFileToFileObd.createFile(storagePath + "/address.txt", User.getInstance().getWalletAddress(mContext));
                        // Authenticate the user. For most apps, this should be done when the user performs an
                        // action that requires Drive access rather than in onCreate.
                        requestSignIn();
                    } else {
                        ToastUtils.showToast(mContext, "The backup file does not exist");
                    }
                }
            }
        });
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        LogUtils.e(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    LogUtils.e(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("OB Wallet")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    createAddressFile();
                })
                .addOnFailureListener(exception -> LogUtils.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createAddressFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating a address file.");
            mLoadingDialog.show();
            mDriveServiceHelper.createFile(User.getInstance().getWalletAddress(mContext))
                    .addOnSuccessListener(fileId -> createWalletFile())
                    .addOnFailureListener(exception -> {
                        mLoadingDialog.dismiss();
                        LogUtils.e(TAG, "Couldn't create address file.", exception);
                    });
        }
    }

    private void createWalletFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating wallet file.");
            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db";
            LogUtils.e(TAG, filePath);
            mDriveServiceHelper.createFile(filePath, "wallet.db").addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    createChannelFile();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Couldn't create wallet file.", e);
                }
            });
        }
    }

    private void createChannelFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating channel file.");
            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db";
            LogUtils.e(TAG, filePath);
            mDriveServiceHelper.createFile(filePath, "channel.db").addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    LogUtils.e(TAG, "Channel fileId" + s);
                    mLoadingDialog.dismiss();
                    ToastUtils.showToast(mContext, "Backup Successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Couldn't create channel file.", e);
                }
            });
        }
    }

    /**
     * Start node
     * 启动节点
     */
    public void startNode() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                NodeStart.getInstance().startWhenStopWithSubscribeState(mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onDoubleClickExit(keyCode, event);
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
