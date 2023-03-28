package com.omni.wallet.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.baselibrary.view.recyclerView.swipeMenu.SwipeMenuLayout;
import com.omni.wallet.entity.InvoiceEntity;
import com.omni.wallet.entity.PaymentEntity;
import com.omni.wallet.entity.TransactionAssetEntity;
import com.omni.wallet.entity.TransactionChainEntity;
import com.omni.wallet.entity.TransactionLightingEntity;
import com.omni.wallet.entity.event.BtcAndUsdtEvent;
import com.omni.wallet.entity.event.CreateInvoiceEvent;
import com.omni.wallet.entity.event.LoginOutEvent;
import com.omni.wallet.entity.event.PayInvoiceFailedEvent;
import com.omni.wallet.entity.event.PayInvoiceSuccessEvent;
import com.omni.wallet.entity.event.RebootEvent;
import com.omni.wallet.entity.event.ScanResultEvent;
import com.omni.wallet.entity.event.SendSuccessEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.PaymentRequestUtil;
import com.omni.wallet.utils.PreventContinuousClicksUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.view.TransactionsAssetView;
import com.omni.wallet.view.TransactionsChainView;
import com.omni.wallet.view.TransactionsLightingView;
import com.omni.wallet.view.dialog.CreateChannelDialog;
import com.omni.wallet.view.dialog.CreateChannelTipDialog;
import com.omni.wallet.view.dialog.PayInvoiceDialog;
import com.omni.wallet.view.dialog.ReceiveLuckyPacketDialog;
import com.omni.wallet.view.dialog.SendDialog;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.FundPopupWindow;
import com.omni.wallet.view.popupwindow.InvoiceDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.Menu1PopupWindow;
import com.omni.wallet.view.popupwindow.TokenInfoPopupWindow;
import com.omni.wallet.view.popupwindow.TransactionsDetailsAssetPopupWindow;
import com.omni.wallet.view.popupwindow.TransactionsDetailsChainPopupWindow;
import com.omni.wallet.view.popupwindow.TransactionsDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.createinvoice.CreateLuckyPacketPopupWindow;
import com.omni.wallet.view.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import invoicesrpc.InvoicesOuterClass;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BalanceDetailActivity extends AppBaseActivity {
    private static final String TAG = BalanceDetailActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    RelativeLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_network)
    ImageView mNetworkIv;
    @BindView(R.id.tv_network_type)
    TextView mNetworkTypeTv;
    @BindView(R.id.iv_more)
    ImageView mMoreIv;
    @BindView(R.id.tv_network)
    TextView mNetworkTv;
    @BindView(R.id.iv_token_info)
    ImageView mTokenInfoTv;
    @BindView(R.id.tv_balance_amount)
    TextView mBalanceAmountTv;
    @BindView(R.id.tv_wallet_address)
    TextView mWalletAddressTv;
    @BindView(R.id.iv_asset_logo)
    ImageView mAssetLogoIv;
    @BindView(R.id.tv_asset_name)
    TextView mAssetNameTv;
    @BindView(R.id.tv_balance_account)
    TextView mBalanceAccountTv;
    @BindView(R.id.tv_balance_unit)
    TextView mBalanceUnitTv;
    @BindView(R.id.tv_balance_account_exchange)
    TextView mBalanceAccountExchangeTv;
    @BindView(R.id.tv_balance_unit_exchange)
    TextView mBalanceUnitExchangeTv;
    @BindView(R.id.tv_balance_account_1)
    TextView mBalanceAccount1Tv;
    @BindView(R.id.tv_balance_unit_1)
    TextView mBalanceUnit1Tv;
    @BindView(R.id.tv_balance_account_exchange_1)
    TextView mBalanceAccountExchange1Tv;
    @BindView(R.id.tv_balance_unit_exchange_1)
    TextView mBalanceUnitExchange1Tv;
    @BindView(R.id.iv_asset_logo_1)
    ImageView mAssetLogo1Iv;
    @BindView(R.id.tv_network_1)
    TextView mNetwork1Tv;
    @BindView(R.id.tv_balance_account_2)
    TextView mBalanceAccount2Tv;
    @BindView(R.id.tv_balance_unit_2)
    TextView mBalanceUnit2Tv;
    @BindView(R.id.tv_balance_account_exchange_2)
    TextView mBalanceAccountExchange2Tv;
    @BindView(R.id.tv_balance_unit_exchange_2)
    TextView mBalanceUnitExchange2Tv;
    @BindView(R.id.tv_network_2)
    TextView mNetwork2Tv;
    @BindView(R.id.tv_balance_account_3)
    TextView mBalanceAccount3Tv;
    @BindView(R.id.tv_balance_unit_3)
    TextView mBalanceUnit3Tv;
    @BindView(R.id.tv_balance_account_exchange_3)
    TextView mBalanceAccountExchange3Tv;
    @BindView(R.id.tv_balance_unit_exchange_3)
    TextView mBalanceUnitExchange3Tv;
    @BindView(R.id.tv_network_3)
    TextView mNetwork3Tv;
    @BindView(R.id.layout_network_lightning)
    RelativeLayout mLightningNetworkLayout;
    @BindView(R.id.layout_network_link)
    RelativeLayout mLinkNetworkLayout;
    @BindView(R.id.layout_root_channel_activities)
    RelativeLayout mRootChannelActivitiesLayout;
    @BindView(R.id.layout_channel_activities)
    RelativeLayout mChannelActivitiesLayout;
    @BindView(R.id.recycler_transactions_list)
    RecyclerView mTransactionsRecyclerView;
    @BindView(R.id.layout_root_both_parent)
    LinearLayout mRootBothParentLayout;
    @BindView(R.id.layout_root_to_be_paid)
    LinearLayout mRootToBePaidLayout;
    @BindView(R.id.tv_to_be_paid_num)
    TextView mToBePaidNumTv;
    @BindView(R.id.layout_to_be_paid)
    RelativeLayout mToBePaidLayout;
    @BindView(R.id.recycler_to_be_paid_list)
    RecyclerView mToBePaidRecyclerView;
    @BindView(R.id.layout_root_my_invoices)
    LinearLayout mRootMyInvoicesLayout;
    @BindView(R.id.tv_my_invoices_num)
    TextView mMyInvoicesNumTv;
    @BindView(R.id.layout_my_invoices)
    RelativeLayout mMyInvoicesLayout;
    @BindView(R.id.recycler_my_invoices_list)
    RecyclerView mMyInvoicesRecyclerView;
    @BindView(R.id.tv_channel_activities)
    TextView mChannelActivitiesTv;
    @BindView(R.id.tv_to_be_paid)
    TextView mToBePaidTv;
    @BindView(R.id.tv_to_be_paid_title)
    TextView mToBePaidTitleTv;
    @BindView(R.id.view_line)
    View mLineView;
    @BindView(R.id.tv_receiver)
    TextView mReceiverTv;
    @BindView(R.id.tv_filter_time)
    TextView mFilterTimeTv;
    private List<PaymentEntity> mPayData = new ArrayList<>();
    private List<PaymentEntity> mReceiveData = new ArrayList<>();
    private List<TransactionLightingEntity> mTransactionsData = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    private List<InvoiceEntity> mToBePaidData = new ArrayList<>();
    private ToBePaidAdapter mToBePaidAdapter;
    private List<LightningOuterClass.Invoice> mMyInvoicesData = new ArrayList<>();
    private MyInvoicesAdapter mMyInvoicesAdapter;
    private List<TransactionChainEntity> mTransactionsChainData = new ArrayList<>();
    private TransactionsChainAdapter mTransactionsChainAdapter;
    private List<LightningOuterClass.Transaction> mPendingTxsChainData = new ArrayList<>();
    private PendingTxsChainAdapter mPendingTxsChainAdapter;
    private List<TransactionAssetEntity> mTransactionsAssetData = new ArrayList<>();
    private TransactionsAssetAdapter mTransactionsAssetAdapter;
    private List<LightningOuterClass.AssetTx> mPendingTxsAssetData = new ArrayList<>();
    private PendingTxsAssetAdapter mPendingTxsAssetAdapter;

    public static final String KEY_BALANCE_AMOUNT = "balanceAmountKey";
    public static final String KEY_WALLET_ADDRESS = "walletAddressKey";
    public static final String KEY_PUBKEY = "pubkeyKey";
    public static final String KEY_BALANCE_ACCOUNT = "balanceAccountKey";
    public static final String KEY_ASSET_ID = "assetIdKey";
    public static final String KEY_NETWORK = "networkKey";
    long balanceAmount;
    long balanceAccount;
    long assetId;
    String walletAddress;
    String network;
    private String pubkey;
    private List<String> txidList;
    String filterTime;

    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    SendDialog mSendDialog;
    CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow;
    CreateLuckyPacketPopupWindow mCreateLuckyPacketPopupWindow;
    TransactionsDetailsPopupWindow mTransactionsDetailsPopupWindow;
    TransactionsDetailsChainPopupWindow mTransactionsDetailsChainPopupWindow;
    TransactionsDetailsAssetPopupWindow mTransactionsDetailsAssetPopupWindow;
    TokenInfoPopupWindow mTokenInfoPopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    Menu1PopupWindow mMenuPopupWindow;

    PayInvoiceDialog mPayInvoiceDialog;
    CreateChannelDialog mCreateChannelDialog;
    CreateChannelTipDialog mCreateChannelTipDialog;
    TimePickerView mTimePickerView;

    @Override
    protected void getBundleData(Bundle bundle) {
        balanceAmount = bundle.getLong(KEY_BALANCE_AMOUNT);
        balanceAccount = bundle.getLong(KEY_BALANCE_ACCOUNT);
        walletAddress = bundle.getString(KEY_WALLET_ADDRESS);
        assetId = bundle.getLong(KEY_ASSET_ID);
        network = bundle.getString(KEY_NETWORK);
        pubkey = bundle.getString(KEY_PUBKEY);
    }

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
        return R.layout.activity_balance_detail;
    }

    @Override
    protected void initView() {
        if (network.equals("lightning")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_vector);
            if (assetId == 0) {
                mNetworkTypeTv.setText("BTC lightning network");
                mNetworkTv.setText("BTC lightning network");
                mNetwork1Tv.setText("BTC lightning network");
                mNetwork2Tv.setText("BTC lightning network");
                mNetwork3Tv.setText("BTC lightning network");
            } else {
                mNetworkTypeTv.setText("dollar lightning network");
                mNetworkTv.setText("dollar lightning network");
                mNetwork1Tv.setText("dollar lightning network");
                mNetwork2Tv.setText("dollar lightning network");
                mNetwork3Tv.setText("dollar lightning network");
            }
            mLightningNetworkLayout.setVisibility(View.VISIBLE);
            mLinkNetworkLayout.setVisibility(View.GONE);
            mChannelActivitiesTv.setText(R.string.channel_activities);
            mChannelActivitiesTv.setTextColor(Color.parseColor("#4A92FF"));
            mToBePaidTv.setText(R.string.to_be_paid);
            mToBePaidTitleTv.setText(R.string.to_be_paid);
            mReceiverTv.setText(R.string.receiver);
            mToBePaidTv.setTextColor(Color.parseColor("#4A92FF"));
            mLineView.setVisibility(View.VISIBLE);
            mRootMyInvoicesLayout.setVisibility(View.VISIBLE);
        } else if (network.equals("link")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_link_black);
            if (assetId == 0) {
                if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                    mNetworkTypeTv.setText("BTC Testnet");
                    mNetworkTv.setText("BTC Testnet");
                    mNetwork1Tv.setText("BTC Testnet");
                    mNetwork2Tv.setText("BTC Testnet");
                    mNetwork3Tv.setText("BTC Testnet");
                } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                    mNetworkTypeTv.setText("BTC Regtest");
                    mNetworkTv.setText("BTC Regtest");
                    mNetwork1Tv.setText("BTC Regtest");
                    mNetwork2Tv.setText("BTC Regtest");
                    mNetwork3Tv.setText("BTC Regtest");
                } else { //mainnet
                    mNetworkTypeTv.setText("BTC Mainnet");
                    mNetworkTv.setText("BTC Mainnet");
                    mNetwork1Tv.setText("BTC Mainnet");
                    mNetwork2Tv.setText("BTC Mainnet");
                    mNetwork3Tv.setText("BTC Mainnet");
                }
            } else {
                if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                    mNetworkTypeTv.setText("Omnilayer Testnet");
                    mNetworkTv.setText("Omnilayer Testnet");
                    mNetwork1Tv.setText("Omnilayer Testnet");
                    mNetwork2Tv.setText("Omnilayer Testnet");
                    mNetwork3Tv.setText("Omnilayer Testnet");
                } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                    mNetworkTypeTv.setText("Omnilayer Regtest");
                    mNetworkTv.setText("Omnilayer Regtest");
                    mNetwork1Tv.setText("Omnilayer Regtest");
                    mNetwork2Tv.setText("Omnilayer Regtest");
                    mNetwork3Tv.setText("Omnilayer Regtest");
                } else { //mainnet
                    mNetworkTypeTv.setText("Omnilayer Mainnet");
                    mNetworkTv.setText("Omnilayer Mainnet");
                    mNetwork1Tv.setText("Omnilayer Mainnet");
                    mNetwork2Tv.setText("Omnilayer Mainnet");
                    mNetwork3Tv.setText("Omnilayer Mainnet");
                }
            }
            mLightningNetworkLayout.setVisibility(View.GONE);
            mLinkNetworkLayout.setVisibility(View.VISIBLE);
            mChannelActivitiesTv.setText("My Account Activities");
            mChannelActivitiesTv.setTextColor(Color.parseColor("#000000"));
            mToBePaidTv.setText(R.string.pending_txs);
            mToBePaidTitleTv.setText(R.string.pending_txs);
            mReceiverTv.setText(R.string.status);
            mToBePaidTv.setTextColor(Color.parseColor("#000000"));
            mLineView.setVisibility(View.GONE);
            mRootMyInvoicesLayout.setVisibility(View.GONE);
        }
        if (assetId == 0) {
            mAssetLogoIv.setImageResource(R.mipmap.icon_btc_logo_small);
            mAssetLogo1Iv.setImageResource(R.mipmap.icon_btc_logo_small);
            mAssetNameTv.setText("BTC");
            mBalanceUnitTv.setText("BTC");
            mBalanceUnit1Tv.setText("BTC");
            mBalanceUnit2Tv.setText("BTC");
            mBalanceUnit3Tv.setText("BTC");
            mTokenInfoTv.setVisibility(View.GONE);
        } else {
            mAssetLogoIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            mAssetLogo1Iv.setImageResource(R.mipmap.icon_usdt_logo_small);
            mAssetNameTv.setText("dollar");
            mBalanceUnitTv.setText("dollar");
            mBalanceUnit1Tv.setText("dollar");
            mBalanceUnit2Tv.setText("dollar");
            mBalanceUnit3Tv.setText("dollar");
            mTokenInfoTv.setVisibility(View.VISIBLE);
        }
        if (balanceAmount == 0) {
            mBalanceAmountTv.setText("My account 0.00");
        } else {
            DecimalFormat df = new DecimalFormat("0.00######");
            mBalanceAmountTv.setText("My account " + df.format(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000));
        }
        mWalletAddressTv.setText(walletAddress);
        initBalanceAccount();
        // 初始化日期选择器
        filterTime = String.valueOf(DateUtils.getMonthFirstdayDateZero()).substring(0, 10);
        mFilterTimeTv.setText(DateUtils.YearMonth(filterTime));
        showTimePicker();
        LogUtils.e(TAG, "------------------getCurrentMonth------------------" + filterTime);
    }

    private void initBalanceAccount() {
        if (balanceAccount == 0) {
            mBalanceAccountTv.setText("0.00");
            mBalanceAccountExchangeTv.setText("0.00");
            mBalanceAccount1Tv.setText("0.00");
            mBalanceAccountExchange1Tv.setText("0.00");
            mBalanceAccount2Tv.setText("0.00");
            mBalanceAccountExchange2Tv.setText("0.00");
            mBalanceAccount3Tv.setText("0.00");
            mBalanceAccountExchange3Tv.setText("0.00");
        } else {
            DecimalFormat df = new DecimalFormat("0.00######");
            DecimalFormat df1 = new DecimalFormat("0.00");
            mBalanceAccountTv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount1Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount2Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount3Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            if (assetId == 0) {
                mBalanceAccountExchangeTv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange1Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange2Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange3Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            } else {
                mBalanceAccountExchangeTv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange1Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange2Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange3Tv.setText(df1.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            }
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initTransactionsData(filterTime);
        initToBePaidData();
        initMyInvoicesData();
    }

    /**
     * initialize the list of activity
     * 初始化交易列表
     */
    private void initTransactionsData(String time) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTransactionsRecyclerView.setLayoutManager(layoutManager);
        if (network.equals("link")) {
            if (assetId == 0) {
                mTransactionsChainAdapter = new TransactionsChainAdapter(mContext, mTransactionsChainData, R.layout.layout_item_transactions_list_chain);
                mTransactionsRecyclerView.setAdapter(mTransactionsChainAdapter);
                getTransactions(time);
            } else {
                mTransactionsAssetAdapter = new TransactionsAssetAdapter(mContext, mTransactionsAssetData, R.layout.layout_item_transactions_list_asset);
                mTransactionsRecyclerView.setAdapter(mTransactionsAssetAdapter);
                listTransactions(time);
            }
        } else if (network.equals("lightning")) {
            mTransactionsAdapter = new TransactionsAdapter(mContext, mTransactionsData, R.layout.layout_item_transactions_list_lighting);
            mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);
            fetchTransactionsFromLND(time);
        }
        // Solve unsmooth sliding(解决滑动不流畅)
        mTransactionsRecyclerView.scrollToPosition(0);
        mTransactionsRecyclerView.setHasFixedSize(true);
        mTransactionsRecyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * @description: getTransactions
     * @描述： 获取链上btc交易记录
     */
    private void getTransactions(String time) {
        mTransactionsChainData.clear();
        Obdmobile.getTransactions(LightningOuterClass.GetTransactionsRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------getTransactionsOnError------------------" + e.getMessage());
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
                            LightningOuterClass.TransactionDetails resp = LightningOuterClass.TransactionDetails.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------getTransactionsOnResponse-----------------" + resp);
                            for (int i = 0; i < resp.getTransactionsList().size(); i++) {
                                TransactionChainEntity entity = new TransactionChainEntity();
                                entity.setTimeStamp(resp.getTransactionsList().get(i).getTimeStamp());
                                entity.setList(Collections.singletonList(resp.getTransactionsList().get(i)));
                                mTransactionsChainData.add(entity);
                            }
                            List<TransactionChainEntity> list = new ArrayList<>();
                            Map<String, TransactionChainEntity> hashMap = new HashMap<>();
                            for (TransactionChainEntity entity : mTransactionsChainData) {
                                String key = DateUtils.MonthDay(entity.getTimeStamp() + "");
                                if (hashMap.containsKey(key)) {
                                    List<LightningOuterClass.Transaction> transactionList = new ArrayList<>();
                                    transactionList.addAll(hashMap.get(key).getList());
                                    transactionList.addAll(entity.getList());
                                    entity.setList(transactionList);
                                    hashMap.put(key, entity);
                                } else {
                                    hashMap.put(key, entity);
                                }
                            }
                            for (Map.Entry<String, TransactionChainEntity> entry : hashMap.entrySet()) {
                                list.add(entry.getValue());
                            }
                            List<TransactionChainEntity> filterList = new ArrayList<>();
                            for (TransactionChainEntity entity : list) {
                                if (entity.getTimeStamp() > Long.parseLong(time)) {
                                    filterList.add(entity);
                                }
                            }
                            LogUtils.e("========filterList========", String.valueOf(filterList));
                            //排序(sort)
                            Collections.sort(filterList);
                            mTransactionsChainData.clear();
                            mTransactionsChainData.addAll(filterList);
                            mTransactionsChainAdapter.notifyDataSetChanged();
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * @description: ListTransactions
     * @描述： 获取链上asset交易记录
     */
    private void listTransactions(String time) {
        mTransactionsAssetData.clear();
        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
        String txidListJson = txidSp.getString("txidListKey", "");
        if (!StringUtils.isEmpty(txidListJson)) {
            Gson gson = new Gson();
            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
            }.getType()); //将json字符串转换成List集合
            txidList.removeAll(Arrays.asList(""));
            removeDuplicate(txidList);
            LogUtils.e(TAG, "========txid=====" + txidList);
            for (int i = 0; i < txidList.size(); i++) {
                getOmniTransactions(txidList.get(i), time);
            }
//            oBListTransactions();
        }
//        else {
//            oBListTransactions();
//        }
    }

    private void getOmniTransactions(String txid, String time) {
        LightningOuterClass.GetOmniTransactionRequest getOmniTransactionRequest = LightningOuterClass.GetOmniTransactionRequest.newBuilder()
                .setTxid(txid)
                .build();
        Obdmobile.oB_GetOmniTransaction(getOmniTransactionRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------oB_GetOmniTransactionOnError------------------" + e.getMessage());
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
                            LightningOuterClass.AssetTx resp = LightningOuterClass.AssetTx.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------oB_GetOmniTransactionOnResponse-----------------" + resp);
                            List<LightningOuterClass.AssetTx> mData = new ArrayList<>();
                            mData.add(resp);
                            for (int i = 0; i < mData.size(); i++) {
                                TransactionAssetEntity entity = new TransactionAssetEntity();
                                entity.setBlockTime(mData.get(i).getBlocktime());
                                entity.setList(Collections.singletonList(mData.get(i)));
                                mTransactionsAssetData.add(entity);
                            }
                            List<TransactionAssetEntity> list = new ArrayList<>();
                            Map<String, TransactionAssetEntity> hashMap = new HashMap<>();
                            for (TransactionAssetEntity entity : mTransactionsAssetData) {
                                String key = DateUtils.MonthDay(entity.getBlockTime() + "");
                                if (hashMap.containsKey(key)) {
                                    List<LightningOuterClass.AssetTx> assetTxList = new ArrayList<>();
                                    assetTxList.addAll(hashMap.get(key).getList());
                                    assetTxList.addAll(entity.getList());
                                    entity.setList(assetTxList);
                                    hashMap.put(key, entity);
                                } else {
                                    hashMap.put(key, entity);
                                }
                            }
                            for (Map.Entry<String, TransactionAssetEntity> entry : hashMap.entrySet()) {
                                list.add(entry.getValue());
                            }
                            List<TransactionAssetEntity> filterList = new ArrayList<>();
                            for (TransactionAssetEntity entity : list) {
                                if (entity.getBlockTime() > Long.parseLong(time)) {
                                    filterList.add(entity);
                                }
                            }
                            LogUtils.e("========assetTxList========", String.valueOf(filterList));
                            //排序(sort)
                            Collections.sort(filterList);
                            mTransactionsAssetData.clear();
                            mTransactionsAssetData.addAll(filterList);
                            mTransactionsAssetAdapter.notifyDataSetChanged();
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void oBListTransactions() {
        LightningOuterClass.ListTranscationsRequest listTranscationsRequest = LightningOuterClass.ListTranscationsRequest.newBuilder()
                .addAddrs(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_ListTranscations(listTranscationsRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listTranscationsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListTranscationsResponse resp = LightningOuterClass.ListTranscationsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listTranscationsOnResponse-----------------" + resp);
//                    mTransactionsAssetData.addAll(resp.getListList());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTransactionsAssetAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This will fetch lightning Transactions from LND.
     * 请求交易列表各个状态的接口
     */
    public void fetchTransactionsFromLND(String time) {
        mTransactionsData.clear();
        LightningOuterClass.ListPaymentsRequest paymentsRequest;
        if (assetId == 0) {
            paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(false)
                    .setIncludeIncomplete(false)
                    .setStartTime(Long.parseLong(time))
                    .build();
        } else {
            paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(true)
                    .setIncludeIncomplete(false)
                    .setStartTime(Long.parseLong(time))
                    .build();
        }
        Obdmobile.oB_ListPayments(paymentsRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------paymentsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    fetchReceiveInvoicesFromLND(time, 100);
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.ListPaymentsResponse resp = LightningOuterClass.ListPaymentsResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------paymentsOnResponse-----------------" + resp);
                            mPayData.clear();
                            for (int i = 0; i < resp.getPaymentsList().size(); i++) {
                                PaymentEntity paymentEntity = new PaymentEntity();
                                paymentEntity.setDate(Long.parseLong(String.valueOf(resp.getPaymentsList().get(i).getHtlcs(0).getResolveTimeNs()).substring(0, 10)));
                                paymentEntity.setAssetId(resp.getPaymentsList().get(i).getAssetId());
                                paymentEntity.setAmount(resp.getPaymentsList().get(i).getValueMsat());
                                paymentEntity.setType(1);
                                mPayData.add(paymentEntity);
                            }
                            Collections.reverse(mPayData);
                            for (int i = 0; i < mPayData.size(); i++) {
                                TransactionLightingEntity entity = new TransactionLightingEntity();
                                entity.setCreationDate(mPayData.get(i).getDate());
                                entity.setList(Collections.singletonList(mPayData.get(i)));
                                mTransactionsData.add(entity);
                            }
                            List<TransactionLightingEntity> list = new ArrayList<>();
                            Map<String, TransactionLightingEntity> hashMap = new HashMap<>();
                            for (TransactionLightingEntity entity : mTransactionsData) {
                                String key = DateUtils.MonthDay(entity.getCreationDate() + "");
                                if (hashMap.containsKey(key)) {
                                    List<PaymentEntity> paymentList = new ArrayList<>();
                                    paymentList.addAll(hashMap.get(key).getList());
                                    paymentList.addAll(entity.getList());
                                    entity.setList(paymentList);
                                    hashMap.put(key, entity);
                                } else {
                                    hashMap.put(key, entity);
                                }
                            }
                            for (Map.Entry<String, TransactionLightingEntity> entry : hashMap.entrySet()) {
                                list.add(entry.getValue());
                            }
                            //排序(sort)
                            Collections.sort(list);
                            mTransactionsData.clear();
                            mTransactionsData.addAll(list);
                            mTransactionsAdapter.notifyDataSetChanged();
                            fetchReceiveInvoicesFromLND(time, 100);
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * This will fetch all lightning invoices from LND.
     * 请求发票列表各个状态的接口
     */
    private void fetchReceiveInvoicesFromLND(String time, long lastIndex) {
        LightningOuterClass.ListInvoiceRequest invoiceRequest;
        if (assetId == 0) {
            invoiceRequest = LightningOuterClass.ListInvoiceRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(false)
                    .setNumMaxInvoices(lastIndex)
                    .setStartTime(Long.parseLong(time))
                    .build();
        } else {
            invoiceRequest = LightningOuterClass.ListInvoiceRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(true)
                    .setNumMaxInvoices(lastIndex)
                    .setStartTime(Long.parseLong(time))
                    .build();
        }
        Obdmobile.oB_ListInvoices(invoiceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------ReceiveInvoiceOnError------------------" + e.getMessage());
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
                            LightningOuterClass.ListInvoiceResponse resp = LightningOuterClass.ListInvoiceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------ReceiveInvoiceOnResponse-----------------" + resp);
                            if (resp.getLastIndexOffset() < lastIndex) {
                                mReceiveData.clear();
                                for (int i = 0; i < resp.getInvoicesList().size(); i++) {
                                    if (resp.getInvoicesList().get(i).getAmtPaidMsat() != 0) {
                                        PaymentEntity paymentEntity = new PaymentEntity();
                                        paymentEntity.setDate(resp.getInvoicesList().get(i).getCreationDate());
                                        paymentEntity.setAssetId(resp.getInvoicesList().get(i).getAssetId());
                                        paymentEntity.setAmount(resp.getInvoicesList().get(i).getValueMsat());
                                        paymentEntity.setType(2);
                                        mReceiveData.add(paymentEntity);
                                    }
                                }
                                Collections.reverse(mReceiveData);
                                for (int i = 0; i < mReceiveData.size(); i++) {
                                    TransactionLightingEntity entity = new TransactionLightingEntity();
                                    entity.setCreationDate(mReceiveData.get(i).getDate());
                                    entity.setList(Collections.singletonList(mReceiveData.get(i)));
                                    mTransactionsData.add(entity);
                                }
                                List<TransactionLightingEntity> list = new ArrayList<>();
                                Map<String, TransactionLightingEntity> hashMap = new HashMap<>();
                                for (TransactionLightingEntity entity : mTransactionsData) {
                                    String key = DateUtils.MonthDay(entity.getCreationDate() + "");
                                    if (hashMap.containsKey(key)) {
                                        List<PaymentEntity> paymentList = new ArrayList<>();
                                        paymentList.addAll(hashMap.get(key).getList());
                                        paymentList.addAll(entity.getList());
                                        entity.setList(paymentList);
                                        hashMap.put(key, entity);
                                    } else {
                                        hashMap.put(key, entity);
                                    }
                                }
                                for (Map.Entry<String, TransactionLightingEntity> entry : hashMap.entrySet()) {
                                    list.add(entry.getValue());
                                }
                                //排序(sort)
                                Collections.sort(list);
                                mTransactionsData.clear();
                                mTransactionsData.addAll(list);
                                mTransactionsAdapter.notifyDataSetChanged();
                            } else {
                                fetchReceiveInvoicesFromLND(time, lastIndex + 100);
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * initialize the list of to be paid list
     * 初始化未支付列表
     */
    private void initToBePaidData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mToBePaidRecyclerView.setLayoutManager(layoutManager);
        if (network.equals("link")) {
            if (assetId == 0) {
                mPendingTxsChainAdapter = new PendingTxsChainAdapter(mContext, mPendingTxsChainData, R.layout.layout_item_to_be_paid_list);
                mToBePaidRecyclerView.setAdapter(mPendingTxsChainAdapter);
                getPendingTxsChain();
            } else {
                mPendingTxsAssetAdapter = new PendingTxsAssetAdapter(mContext, mPendingTxsAssetData, R.layout.layout_item_to_be_paid_list);
                mToBePaidRecyclerView.setAdapter(mPendingTxsAssetAdapter);
                getPendingTxsAsset();
            }
        } else if (network.equals("lightning")) {
            fetchPaymentsFromLND();
        }
    }

    /**
     * @description: getPendingTxsChain
     * @描述： 获取链上btc确认中交易记录
     */
    private void getPendingTxsChain() {
        mPendingTxsChainData.clear();
        Obdmobile.getTransactions(LightningOuterClass.GetTransactionsRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------getPendingTxsChainOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.TransactionDetails resp = LightningOuterClass.TransactionDetails.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------getPendingTxsChainOnResponse-----------------" + resp);
                    for (LightningOuterClass.Transaction transaction : resp.getTransactionsList()) {
                        if (StringUtils.isEmpty(String.valueOf(transaction.getNumConfirmations())) || transaction.getNumConfirmations() < 3) {
                            mPendingTxsChainData.add(transaction);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mToBePaidNumTv.setText(mPendingTxsChainData.size() + "");
                            mPendingTxsChainAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @description: getPendingTxsAsset
     * @描述： 获取链上asset确认中交易记录
     */
    private void getPendingTxsAsset() {
//        LightningOuterClass.ListTranscationsRequest listTranscationsRequest = LightningOuterClass.ListTranscationsRequest.newBuilder()
//                .addAddrs(User.getInstance().getWalletAddress(mContext))
//                .build();
//        Obdmobile.oB_ListTranscations(listTranscationsRequest.toByteArray(), new Callback() {
//            @Override
//            public void onError(Exception e) {
//                LogUtils.e(TAG, "------------------getPendingTxsAssetOnError------------------" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(byte[] bytes) {
//                if (bytes == null) {
//                    return;
//                }
//                try {
//                    LightningOuterClass.ListTranscationsResponse resp = LightningOuterClass.ListTranscationsResponse.parseFrom(bytes);
//                    LogUtils.e(TAG, "------------------getPendingTxsAssetOnResponse-----------------" + resp);
//                    mPendingTxsAssetData.clear();
//                    for (LightningOuterClass.AssetTx assetTx : resp.getListList()) {
//                        if (StringUtils.isEmpty(String.valueOf(assetTx.getConfirmations())) || assetTx.getConfirmations() < 3) {
//                            mPendingTxsAssetData.add(assetTx);
//                        }
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mToBePaidNumTv.setText(mPendingTxsAssetData.size() + "");
//                            mPendingTxsAssetAdapter.notifyDataSetChanged();
//                        }
//                    });
//                } catch (InvalidProtocolBufferException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        mPendingTxsAssetData.clear();
        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
        String txidListJson = txidSp.getString("txidListKey", "");
        if (!StringUtils.isEmpty(txidListJson)) {
            Gson gson = new Gson();
            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
            }.getType()); //将json字符串转换成List集合
            txidList.removeAll(Arrays.asList(""));
            removeDuplicate(txidList);
            LogUtils.e(TAG, "========txid=====" + txidList);
            for (int i = 0; i < txidList.size(); i++) {
                LightningOuterClass.GetOmniTransactionRequest getOmniTransactionRequest = LightningOuterClass.GetOmniTransactionRequest.newBuilder()
                        .setTxid(txidList.get(i))
                        .build();
                Obdmobile.oB_GetOmniTransaction(getOmniTransactionRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "------------------oB_GetOmniTransactionPendingOnError------------------" + e.getMessage());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        if (bytes == null) {
                            return;
                        }
                        try {
                            LightningOuterClass.AssetTx resp = LightningOuterClass.AssetTx.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------oB_GetOmniTransactionOnPendingResponse-----------------" + resp);
                            if (StringUtils.isEmpty(String.valueOf(resp.getConfirmations())) || resp.getConfirmations() < 3) {
                                mPendingTxsAssetData.add(resp);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mToBePaidNumTv.setText(mPendingTxsAssetData.size() + "");
                                    mPendingTxsAssetAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /**
     * This will fetch lightning payments from LND.
     * 请求支付列表各个状态的接口
     */
    public void fetchPaymentsFromLND() {
        mToBePaidData.clear();
        if (assetId == 0) {
            SharedPreferences sp = mContext.getSharedPreferences("SP_BTC_INVOICE_LIST", Activity.MODE_PRIVATE);
            String btcInvoiceListJson = sp.getString("btcInvoiceListKey", "");
            if (!StringUtils.isEmpty(btcInvoiceListJson)) {
                Gson gson = new Gson();
                mToBePaidData = gson.fromJson(btcInvoiceListJson, new TypeToken<List<InvoiceEntity>>() {
                }.getType()); //将json字符串转换成List集合
                removeDuplicateInvoice(mToBePaidData);
                Collections.reverse(mToBePaidData);
                LogUtils.e(TAG, "========btcInvoice=====" + btcInvoiceListJson);
                mToBePaidNumTv.setText(mToBePaidData.size() + "");
                mToBePaidAdapter = new ToBePaidAdapter(mContext, mToBePaidData, R.layout.layout_item_to_be_paid_list);
                mToBePaidRecyclerView.setAdapter(mToBePaidAdapter);
                mToBePaidAdapter.notifyDataSetChanged();
            }
        } else {
            SharedPreferences sp = mContext.getSharedPreferences("SP_INVOICE_LIST", Activity.MODE_PRIVATE);
            String invoiceListJson = sp.getString("invoiceListKey", "");
            if (!StringUtils.isEmpty(invoiceListJson)) {
                Gson gson = new Gson();
                mToBePaidData = gson.fromJson(invoiceListJson, new TypeToken<List<InvoiceEntity>>() {
                }.getType()); //将json字符串转换成List集合
                removeDuplicateInvoice(mToBePaidData);
                Collections.reverse(mToBePaidData);
                LogUtils.e(TAG, "========invoice=====" + invoiceListJson);
                mToBePaidNumTv.setText(mToBePaidData.size() + "");
                mToBePaidAdapter = new ToBePaidAdapter(mContext, mToBePaidData, R.layout.layout_item_to_be_paid_list);
                mToBePaidRecyclerView.setAdapter(mToBePaidAdapter);
                mToBePaidAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * initialize the list of my invoices list
     * 初始化我的发票列表
     */
    private void initMyInvoicesData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMyInvoicesRecyclerView.setLayoutManager(layoutManager);
        mMyInvoicesAdapter = new MyInvoicesAdapter(mContext, mMyInvoicesData, R.layout.layout_item_my_invoices_list);
        mMyInvoicesRecyclerView.setAdapter(mMyInvoicesAdapter);
        fetchInvoicesFromLND(100);
    }

    /**
     * This will fetch all lightning invoices from LND.
     * 请求发票列表各个状态的接口
     */
    private void fetchInvoicesFromLND(long lastIndex) {
        LightningOuterClass.ListInvoiceRequest invoiceRequest;
        if (assetId == 0) {
            invoiceRequest = LightningOuterClass.ListInvoiceRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(false)
                    .setNumMaxInvoices(lastIndex)
                    .build();
        } else {
            invoiceRequest = LightningOuterClass.ListInvoiceRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(true)
                    .setNumMaxInvoices(lastIndex)
                    .build();
        }
        Obdmobile.oB_ListInvoices(invoiceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------invoiceOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListInvoiceResponse resp = LightningOuterClass.ListInvoiceResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------invoiceOnResponse-----------------" + resp);
                    if (resp.getLastIndexOffset() < lastIndex) {
                        mMyInvoicesData.clear();
                        mMyInvoicesData.addAll(Lists.reverse(resp.getInvoicesList()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMyInvoicesNumTv.setText(resp.getInvoicesList().size() + "");
                                mMyInvoicesAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        fetchInvoicesFromLND(lastIndex + 100);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * the adapter of activity list
     * 交易列表适配器
     */
    private class TransactionsChainAdapter extends CommonRecyclerAdapter<TransactionChainEntity> {

        public TransactionsChainAdapter(Context context, List<TransactionChainEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final TransactionChainEntity item) {
            if ((DateUtils.dateFormat(item.getTimeStamp() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getTodayDate())) {
                holder.setText(R.id.tv_time, "Today");
            } else if ((DateUtils.dateFormat(item.getTimeStamp() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getYesterDate())) {
                holder.setText(R.id.tv_time, "Yesterday");
            } else {
                holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getTimeStamp() + ""));
            }
            if (position == 0) {
                holder.setViewVisibility(R.id.layout_amount, View.VISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.VISIBLE);
            } else {
                holder.setViewVisibility(R.id.layout_amount, View.INVISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.INVISIBLE);
            }
            TransactionsChainView mTransactionsChainView = holder.getView(R.id.view_transactions_chain);
            mTransactionsChainView.setViewShow(item.getList());
            mTransactionsChainView.setCallback(new TransactionsChainView.ChainItemCallback() {
                @Override
                public void onClickItem(LightningOuterClass.Transaction item) {
                    mTransactionsDetailsChainPopupWindow = new TransactionsDetailsChainPopupWindow(mContext);
                    mTransactionsDetailsChainPopupWindow.show(mParentLayout, item);
                }
            });
        }
    }

    /**
     * the adapter of activity list
     * 交易列表适配器
     */
    private class TransactionsAssetAdapter extends CommonRecyclerAdapter<TransactionAssetEntity> {

        public TransactionsAssetAdapter(Context context, List<TransactionAssetEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final TransactionAssetEntity item) {
            if ((DateUtils.dateFormat(item.getBlockTime() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getTodayDate())) {
                holder.setText(R.id.tv_time, "Today");
            } else if ((DateUtils.dateFormat(item.getBlockTime() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getYesterDate())) {
                holder.setText(R.id.tv_time, "Yesterday");
            } else {
                holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getBlockTime() + ""));
            }
            if (position == 0) {
                holder.setViewVisibility(R.id.layout_amount, View.VISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.VISIBLE);
            } else {
                holder.setViewVisibility(R.id.layout_amount, View.INVISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.INVISIBLE);
            }
            TransactionsAssetView mTransactionsAssetView = holder.getView(R.id.view_transactions_asset);
            mTransactionsAssetView.setViewShow(item.getList());
            mTransactionsAssetView.setCallback(new TransactionsAssetView.AssetItemCallback() {
                @Override
                public void onClickItem(LightningOuterClass.AssetTx item) {
                    mTransactionsDetailsAssetPopupWindow = new TransactionsDetailsAssetPopupWindow(mContext);
                    mTransactionsDetailsAssetPopupWindow.show(mParentLayout, item);
                }
            });
        }
    }

    /**
     * the adapter of activity list
     * 交易列表适配器
     */
    private class TransactionsAdapter extends CommonRecyclerAdapter<TransactionLightingEntity> {

        public TransactionsAdapter(Context context, List<TransactionLightingEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final TransactionLightingEntity item) {
            if ((DateUtils.dateFormat(item.getCreationDate() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getTodayDate())) {
                holder.setText(R.id.tv_time, "Today");
            } else if ((DateUtils.dateFormat(item.getCreationDate() * 1000L, DateUtils.YYYY_MM_DD)).equals(DateUtils.getYesterDate())) {
                holder.setText(R.id.tv_time, "Yesterday");
            } else {
                holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getCreationDate() + ""));
            }
            if (position == 0) {
                holder.setViewVisibility(R.id.layout_amount, View.VISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.VISIBLE);
            } else {
                holder.setViewVisibility(R.id.layout_amount, View.INVISIBLE);
                holder.setViewVisibility(R.id.layout_status, View.INVISIBLE);
            }
            TransactionsLightingView mTransactionsLightingView = holder.getView(R.id.view_transactions_lighting);
            mTransactionsLightingView.setViewShow(item.getList());
//            mTransactionsLightingView.setCallback(new TransactionsLightingView.LightingItemCallback() {
//                @Override
//                public void onClickItem(PaymentEntity item) {
//                    mTransactionsDetailsPopupWindow = new TransactionsDetailsPopupWindow(mContext);
//                    mTransactionsDetailsPopupWindow.show(mParentLayout, item);
//                }
//            });
        }
    }

    /**
     * the adapter of to be paid list
     * 未支付列表适配器
     */
    private class ToBePaidAdapter extends CommonRecyclerAdapter<InvoiceEntity> {

        public ToBePaidAdapter(Context context, List<InvoiceEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final InvoiceEntity item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getDate() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getAssetId() == 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount() / 1000)) / 100000000));
            } else {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
            }
            if (item.getInvoice() != null && !item.getInvoice().isEmpty()) {
                holder.setText(R.id.tv_receiver, PaymentRequestUtil.getMemo(item.getInvoice()));
            } else {
                holder.setText(R.id.tv_receiver, "unknown");
            }
            final SwipeMenuLayout menuLayout = holder.getView(R.id.layout_to_be_paid_list_swipe_menu);
            holder.getView(R.id.tv_to_be_paid_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Used to delete a payment probe.
                     * 删除付款
                     */
                    menuLayout.quickClose();
                    if (item.getAssetId() == 0) {
                        mToBePaidData.remove(position);
                        mToBePaidAdapter.notifyRemoveItem(position);
                        mToBePaidNumTv.setText(mToBePaidData.size() + "");
                        if (mToBePaidData.size() == 0) {
                            mToBePaidAdapter.notifyDataSetChanged();
                            mToBePaidNumTv.setText("0");
                        }
                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(mToBePaidData);
                        SharedPreferences sp = mContext.getSharedPreferences("SP_BTC_INVOICE_LIST", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("btcInvoiceListKey", jsonStr);
                        editor.commit();
                    } else {
                        mToBePaidData.remove(position);
                        mToBePaidAdapter.notifyRemoveItem(position);
                        mToBePaidNumTv.setText(mToBePaidData.size() + "");
                        if (mToBePaidData.size() == 0) {
                            mToBePaidAdapter.notifyDataSetChanged();
                            mToBePaidNumTv.setText("0");
                        }
                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(mToBePaidData);
                        SharedPreferences sp = mContext.getSharedPreferences("SP_INVOICE_LIST", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("invoiceListKey", jsonStr);
                        editor.commit();
                    }
                }
            });
            holder.getView(R.id.layout_to_be_paid_list).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreventContinuousClicksUtil.isNotFastClick()) {
                        mPayInvoiceDialog = new PayInvoiceDialog(mContext);
                        mPayInvoiceDialog.show(pubkey, assetId, UriUtil.generateLightningUri(item.getInvoice()));
                    }
                }
            });
        }
    }

    // ByteString values when using for example "paymentRequest.getDescriptionBytes()" can for some reason not directly be used as they are double in length
    private static ByteString byteStringFromHex(String hexString) {
        byte[] hexBytes = BaseEncoding.base16().decode(hexString.toUpperCase());
        return ByteString.copyFrom(hexBytes);
    }

    /**
     * the adapter of Pending Txs
     * 链上btc交易确认中列表适配器
     */
    private class PendingTxsChainAdapter extends CommonRecyclerAdapter<LightningOuterClass.Transaction> {

        public PendingTxsChainAdapter(Context context, List<LightningOuterClass.Transaction> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Transaction item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getTimeStamp() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getAmount() < 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000).replace("-", ""));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_receiver, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_receiver, "SENT");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_right_blue);
                }
            } else if (item.getAmount() > 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_receiver, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_receiver, "RECEIVED");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                }
            }
        }
    }

    /**
     * the adapter of Pending Txs
     * 链上asset交易确认中列表适配器
     */
    private class PendingTxsAssetAdapter extends CommonRecyclerAdapter<LightningOuterClass.AssetTx> {

        public PendingTxsAssetAdapter(Context context, List<LightningOuterClass.AssetTx> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.AssetTx item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getBlocktime() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getType().equals("Simple Send")) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getAmount())));
                if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                    holder.setText(R.id.tv_receiver, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_receiver, "RECEIVED");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                }
            } else if (item.getType().equals("Send To Many")) {
                if (item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getTotalamount())));
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        holder.setText(R.id.tv_receiver, "PENDING");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_receiver, "SENT");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_right_blue);
                    }
                } else if (!item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    if (item.getReceiversList() != null) {
                        if (item.getReceiversList().size() == 1) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                            }
                        } else if (item.getReceiversList().size() == 2) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & !item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                            } else if (!item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getReceivers(1).getAmount())));
                            }
                        }
                    }
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        holder.setText(R.id.tv_receiver, "PENDING");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_receiver, "RECEIVED");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                    }
                }
            }
        }
    }

    /**
     * the adapter of my invoices list
     * 我的发票列表适配器
     */
    private class MyInvoicesAdapter extends CommonRecyclerAdapter<LightningOuterClass.Invoice> {

        public MyInvoicesAdapter(Context context, List<LightningOuterClass.Invoice> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Invoice item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getCreationDate() + ""));
            holder.setText(R.id.tv_memo, StringUtils.cleanString(item.getMemo()));
            Long amt = item.getValueMsat();
            Long amtPayed = item.getAmtPaidMsat();

            final SwipeMenuLayout menuLayout = holder.getView(R.id.layout_my_invoices_list_swipe_menu);
            if (amt.equals(0L)) {
                // if no specific value was requested
                if (!amtPayed.equals(0L)) {
                    // The invoice has been payed
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                    menuLayout.setSwipeEnable(false);
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (assetId == 0) {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amtPayed / 1000)) / 100000000));
                    } else {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amtPayed)) / 100000000));
                    }
                } else {
                    // The invoice has not been payed yet
                    holder.setText(R.id.tv_amount, "0.00");
                    if (StringUtils.isEmpty(String.valueOf(item.getState()))) {
                        if (isInvoiceExpired(item)) {
                            // The invoice has expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_off_red);
                            menuLayout.setSwipeEnable(false);
                        } else {
                            // The invoice has not yet expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                            menuLayout.setSwipeEnable(true);
                        }
                    } else {
                        if (isInvoiceExpired(item) || item.getState() == LightningOuterClass.Invoice.InvoiceState.CANCELED) {
                            // The invoice has expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_off_red);
                            menuLayout.setSwipeEnable(false);
                        } else {
                            // The invoice has not yet expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                            menuLayout.setSwipeEnable(true);
                        }
                    }
                }
            } else {
                // if a specific value was requested
                if (isInvoicePayed(item)) {
                    // The invoice has been payed
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                    menuLayout.setSwipeEnable(false);
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (assetId == 0) {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amtPayed / 1000)) / 100000000));
                    } else {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amtPayed)) / 100000000));
                    }
                } else {
                    // The invoice has not been payed yet
                    DecimalFormat df = new DecimalFormat("0.00######");
                    if (assetId == 0) {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amt / 1000)) / 100000000));
                    } else {
                        holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(amt)) / 100000000));
                    }
                    if (StringUtils.isEmpty(String.valueOf(item.getState()))) {
                        if (isInvoiceExpired(item)) {
                            // The invoice has expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_off_red);
                            menuLayout.setSwipeEnable(false);
                        } else {
                            // The invoice has not yet expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                            menuLayout.setSwipeEnable(true);
                        }
                    } else {
                        if (isInvoiceExpired(item) || item.getState() == LightningOuterClass.Invoice.InvoiceState.CANCELED) {
                            // The invoice has expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_off_red);
                            menuLayout.setSwipeEnable(false);
                        } else {
                            // The invoice has not yet expired
                            holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                            menuLayout.setSwipeEnable(true);
                        }
                    }
                }
            }
            holder.getView(R.id.tv_my_invoices_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Used to delete a invoice.
                     * 删除发票
                     */
                    InvoicesOuterClass.CancelInvoiceMsg cancelInvoiceMsg = InvoicesOuterClass.CancelInvoiceMsg.newBuilder()
                            .setPaymentHash(item.getRHash())
                            .build();
                    Obdmobile.invoicesCancelInvoice(cancelInvoiceMsg.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------invoicesCancelInvoiceOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    menuLayout.quickClose();
                                    mMyInvoicesData.remove(position);
                                    mMyInvoicesAdapter.notifyRemoveItem(position);
                                    if (mMyInvoicesData.size() == 0) {
                                        mMyInvoicesAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            holder.getView(R.id.layout_invoice_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreventContinuousClicksUtil.isNotFastClick()) {
                        InvoiceDetailsPopupWindow mInvoiceDetailsPopupWindow = new InvoiceDetailsPopupWindow(mContext);
                        mInvoiceDetailsPopupWindow.show(mParentLayout, item);
                    }
                }
            });
        }
    }

    /**
     * Returns if the invoice has been payed already.
     *
     * @param invoice
     * @return
     */
    public boolean isInvoicePayed(LightningOuterClass.Invoice invoice) {
        boolean payed;
        if (invoice.getValueMsat() == 0) {
            payed = invoice.getAmtPaidMsat() != 0;
        } else {
            payed = invoice.getValueMsat() <= invoice.getAmtPaidMsat();
        }
        return payed;
    }

    /**
     * Returns if the invoice has been expired. This function just checks if the expiration date is in the past.
     * It will also return expired for already payed invoices.
     *
     * @param invoice
     * @return
     */
    public boolean isInvoiceExpired(LightningOuterClass.Invoice invoice) {
        return invoice.getCreationDate() + invoice.getExpiry() < System.currentTimeMillis() / 1000;
    }

    /**
     * click back button
     * 点击Back按钮
     */
    @OnClick(R.id.layout_back)
    public void clickBack() {
        finish();
    }

    /**
     * click three point button
     * 点击三个点按钮
     */
    @OnClick(R.id.layout_more)
    public void clickMore() {
        mMenuPopupWindow = new Menu1PopupWindow(mContext);
        mMenuPopupWindow.show(mMoreIv, balanceAmount, User.getInstance().getWalletAddress(mContext), pubkey);
    }

    /**
     * 点击copy图标按钮
     * click copy icon btn
     *
     * @description 中文：点击按钮复制当前address到粘贴板
     * EN：Click copy button,duplicate address to clipboard
     * @author Tong ChangHui
     * @Email tch081092@gmail.com
     */
    @OnClick(R.id.iv_copy)
    public void copyAddress() {
        //接收需要复制到粘贴板的地址
        //Get the address which will copy to clipboard
        String toCopyAddress = walletAddress;
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(BalanceDetailActivity.this, toCopyAddress, toastString);
    }

    /**
     * click qrcode
     * 点击qrcode按钮
     */
    @OnClick(R.id.iv_qrcode)
    public void copyQRCode() {
        FundPopupWindow mFundPopupWindow = new FundPopupWindow(mContext);
        mFundPopupWindow.show(mParentLayout, User.getInstance().getWalletAddress(mContext));
    }

    /**
     * click token info button
     * 点击token info按钮
     */
    @OnClick(R.id.iv_token_info)
    public void clickTokenInfo() {
        mTokenInfoPopupWindow = new TokenInfoPopupWindow(mContext);
        mTokenInfoPopupWindow.show(mParentLayout, pubkey, assetId, balanceAccount);
    }


    /**
     * click scan button
     * 点击扫描按钮
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        PermissionUtils.launchCamera(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                Bundle bundle = new Bundle();
                bundle.putInt(ScanActivity.KEY_SCAN_CODE, 2);
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
     * click channel list button
     * 点击channel List按钮
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        Bundle bundle = new Bundle();
        bundle.putLong(ChannelsActivity.KEY_BALANCE_AMOUNT, balanceAmount);
        bundle.putString(ChannelsActivity.KEY_WALLET_ADDRESS, walletAddress);
        bundle.putString(ChannelsActivity.KEY_PUBKEY, pubkey);
        switchActivity(ChannelsActivity.class, bundle);
    }

    /**
     * click pay invoice button
     * 点击Pay invoice按钮
     */
    @OnClick(R.id.layout_pay_invoice)
    public void clickPayInvoice() {
        mPayInvoiceDialog = new PayInvoiceDialog(mContext);
        mPayInvoiceDialog.show(pubkey, assetId, "");
//        mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
//        mPayInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, assetId, "");
    }

    /**
     * click create invoice button
     * 点击Create invoice按钮
     */
    @OnClick(R.id.layout_create_invoice)
    public void clickCreateInvoice() {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) assetId)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                            if (resp.getRemoteBalance().getMsat() == 0) {
                                mCreateChannelTipDialog = new CreateChannelTipDialog(mContext);
                                mCreateChannelTipDialog.show();
                            } else {
                                mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
                                mCreateInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, assetId, balanceAccount);
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * click lucky packet button
     * 点击Create lucky packet按钮
     */
    @OnClick(R.id.layout_lucky_packet)
    public void clickLuckyPacket() {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) assetId)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                            if (resp.getLocalBalance().getMsat() == 0) {
                                mCreateChannelTipDialog = new CreateChannelTipDialog(mContext);
                                mCreateChannelTipDialog.show();
                            } else {
                                mCreateLuckyPacketPopupWindow = new CreateLuckyPacketPopupWindow(mContext);
                                mCreateLuckyPacketPopupWindow.show(mParentLayout, pubkey, assetId, balanceAccount);
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * click send button
     * 点击Send按钮
     */
    @OnClick(R.id.layout_send_invoice)
    public void clickSendInvoice() {
        mSendDialog = new SendDialog(mContext);
        mSendDialog.show("");
    }

    /**
     * click channel activities text
     * 点击ChannelActivities文本
     */
    @OnClick(R.id.layout_root_channel_activities)
    public void clickChannelActivities() {
        mRootChannelActivitiesLayout.setVisibility(View.GONE);
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.VISIBLE);
        mChannelActivitiesLayout.startAnimation(animation);
    }

    /**
     * click close button in  channel activities model
     * 点击ChannelActivities Close按钮
     */
    @OnClick(R.id.iv_close_channel_activities)
    public void clickCloseChannelActivitiesIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.GONE);
        mChannelActivitiesLayout.startAnimation(animation);
        mRootChannelActivitiesLayout.setVisibility(View.VISIBLE);
    }

    /**
     * click to be paid text
     * 点击To be paid文本
     */
    @OnClick(R.id.layout_root_to_be_paid)
    public void clickToBePaid() {
        mRootChannelActivitiesLayout.setVisibility(View.GONE);
        if (mChannelActivitiesLayout.isShown()) {
            TranslateAnimation animation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            animation.setDuration(300l);//设置动画的过渡时间
            mChannelActivitiesLayout.setVisibility(View.GONE);
            mChannelActivitiesLayout.startAnimation(animation);
        }
        mRootBothParentLayout.setVisibility(View.GONE);
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300l);//设置动画的过渡时间
        mToBePaidLayout.setVisibility(View.VISIBLE);
        mToBePaidLayout.startAnimation(animation);
    }

    /**
     * click close button in to be paid model
     * 点击ToBePaid Close按钮
     */
    @OnClick(R.id.iv_close_to_be_paid)
    public void clickCloseToBePaidIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mToBePaidLayout.setVisibility(View.GONE);
        mToBePaidLayout.startAnimation(animation);
        mRootChannelActivitiesLayout.setVisibility(View.VISIBLE);
        mRootBothParentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * click my invoices text
     * 点击My invoices文本
     */
    @OnClick(R.id.layout_root_my_invoices)
    public void clickMyInvoices() {
        mRootChannelActivitiesLayout.setVisibility(View.GONE);
        if (mChannelActivitiesLayout.isShown()) {
            TranslateAnimation animation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            animation.setDuration(300l);//设置动画的过渡时间
            mChannelActivitiesLayout.setVisibility(View.GONE);
            mChannelActivitiesLayout.startAnimation(animation);
        }
        mRootBothParentLayout.setVisibility(View.GONE);
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300l);//设置动画的过渡时间
        mMyInvoicesLayout.setVisibility(View.VISIBLE);
        mMyInvoicesLayout.startAnimation(animation);
    }

    /**
     * click close button in my invoices model
     * 点击MyInvoices Close按钮
     */
    @OnClick(R.id.iv_close_my_invoices)
    public void clickCloseMyInvoicesIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mMyInvoicesLayout.setVisibility(View.GONE);
        mMyInvoicesLayout.startAnimation(animation);
        mRootChannelActivitiesLayout.setVisibility(View.VISIBLE);
        mRootBothParentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 点击时间筛选的按钮
     * Click the time filter button
     */
    @OnClick(R.id.layout_filter_time)
    public void clickFilterTimeLayout() {
        mTimePickerView.show();
    }

    // 显示时间
    public void showTimePicker() {
        //设置显示的日期
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2023, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 11, 31);
        mTimePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                filterTime = String.valueOf(DateUtils.getMonthBegin(date)).substring(0, 10);
                mFilterTimeTv.setText(DateUtils.YearMonth(filterTime));
                initTransactionsData(filterTime);
                LogUtils.e("==========filterTime==========", filterTime);
            }
        }).setSubmitText("OK")
                .setCancelText("Cancel")
                .setCancelColor(Color.BLACK)
                .setSubmitColor(Color.BLACK)
                .setSubCalSize(16)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                //.isDialog(true) //是否对话框样式显示（显示在页面中间）
                //.isCyclic(true) //是否循环滚动
                .setType(new boolean[]{true, true, false, false, false, false}) //显示“年月日时分秒”的哪几项
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false) //是否只显示选中的label文字，false则每项item全部都带有 label
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        LogUtils.e("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 扫码后的消息通知监听
     * Message notification monitoring after Scan qrcode
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResultEvent(ScanResultEvent event) {
        if (event.getCode() == 2) {
            if (event.getType().equals("payInvoice")) {
                mPayInvoiceDialog = new PayInvoiceDialog(mContext);
                mPayInvoiceDialog.show(pubkey, assetId, event.getData());
//                mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
//                mPayInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, assetId, event.getData());
            } else if (event.getType().equals("receiveLuckyPacket")) {
                LogUtils.e(TAG, "------------------decodePaymentOnResponse-----------------" + event.getData());
                ReceiveLuckyPacketDialog mReceiveLuckyPacketDialog = new ReceiveLuckyPacketDialog(mContext);
                mReceiveLuckyPacketDialog.show(event.getData());
            } else if (event.getType().equals("openChannel")) {
                mCreateChannelDialog = new CreateChannelDialog(mContext);
                mCreateChannelDialog.show(balanceAmount, walletAddress, event.getData());
//                mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//                mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, walletAddress, event.getData());
            } else if (event.getType().equals("send")) {
                mSendDialog = new SendDialog(mContext);
                mSendDialog.show(event.getData());
            }
        }
    }

    /**
     * 支付发票成功的消息通知监听
     * Message notification monitoring after pay invoice success
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayInvoiceSuccessEvent(PayInvoiceSuccessEvent event) {
        filterTime = String.valueOf(DateUtils.getMonthFirstdayDateZero()).substring(0, 10);
        mFilterTimeTv.setText(DateUtils.YearMonth(filterTime));
        fetchTransactionsFromLND(filterTime);
        fetchPaymentsFromLND();
    }

    /**
     * 支付发票失败的消息通知监听
     * Message notification monitoring after pay invoice failed
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayInvoiceFailedEvent(PayInvoiceFailedEvent event) {
        fetchPaymentsFromLND();
    }

    /**
     * 创建发票后的消息通知监听
     * Message notification monitoring after create invoice
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateInvoiceEvent(CreateInvoiceEvent event) {
        filterTime = String.valueOf(DateUtils.getMonthFirstdayDateZero()).substring(0, 10);
        mFilterTimeTv.setText(DateUtils.YearMonth(filterTime));
        fetchTransactionsFromLND(filterTime);
        fetchInvoicesFromLND(100);
    }

    /**
     * 支付成功后的消息通知监听
     * Message notification monitoring after successful payment
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSuccessEvent(SendSuccessEvent event) {
        filterTime = String.valueOf(DateUtils.getMonthFirstdayDateZero()).substring(0, 10);
        mFilterTimeTv.setText(DateUtils.YearMonth(filterTime));
        if (assetId == 0) {
            getTransactions(filterTime);
            getPendingTxsChain();
        } else {
            listTransactions(filterTime);
            getPendingTxsAsset();
        }
    }

    /**
     * btc和usdt变化后的消息通知监听
     * Message notification monitoring after Btc and Usdt change
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBtcAndUsdtEvent(BtcAndUsdtEvent event) {
        initBalanceAccount();
    }

    /**
     * 退出登录后的消息通知监听
     * Message notification monitoring after login out
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginOutEvent(LoginOutEvent event) {
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

    /**
     * @备注： 循环删除重复数据
     * @description: Circular deletion of duplicate data
     */
    public static void removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        System.out.println(list);
    }

    /**
     * @备注： 循环删除重复数据
     * @description: Circular deletion of duplicate data
     */
    public static void removeDuplicateInvoice(List<InvoiceEntity> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getInvoice().equals(list.get(i).getInvoice())) {
                    list.remove(j);
                }
            }
        }
        System.out.println(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.release();
        }
        if (mPayInvoiceStepOnePopupWindow != null) {
            mPayInvoiceStepOnePopupWindow.release();
        }
        if (mSendDialog != null) {
            mSendDialog.release();
        }
        if (mCreateInvoiceStepOnePopupWindow != null) {
            mCreateInvoiceStepOnePopupWindow.release();
        }
        if (mCreateLuckyPacketPopupWindow != null) {
            mCreateLuckyPacketPopupWindow.release();
        }
        if (mTransactionsDetailsPopupWindow != null) {
            mTransactionsDetailsPopupWindow.release();
        }
        if (mTokenInfoPopupWindow != null) {
            mTokenInfoPopupWindow.release();
        }
        if (mCreateChannelStepOnePopupWindow != null) {
            mCreateChannelStepOnePopupWindow.release();
        }
        if (mPayInvoiceDialog != null) {
            mPayInvoiceDialog.release();
        }
        if (mCreateChannelDialog != null) {
            mCreateChannelDialog.release();
        }
        if (mCreateChannelTipDialog != null) {
            mCreateChannelTipDialog.release();
        }
    }
}
