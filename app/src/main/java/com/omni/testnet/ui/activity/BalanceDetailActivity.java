package com.omni.testnet.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.testnet.R;
import com.omni.testnet.base.AppBaseActivity;
import com.omni.testnet.baselibrary.utils.DateUtils;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.PermissionUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.testnet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.testnet.baselibrary.view.recyclerView.swipeMenu.SwipeMenuLayout;
import com.omni.testnet.entity.InvoiceEntity;
import com.omni.testnet.entity.PaymentEntity;
import com.omni.testnet.entity.event.BtcAndUsdtEvent;
import com.omni.testnet.entity.event.CreateInvoiceEvent;
import com.omni.testnet.entity.event.LoginOutEvent;
import com.omni.testnet.entity.event.PayInvoiceFailedEvent;
import com.omni.testnet.entity.event.PayInvoiceSuccessEvent;
import com.omni.testnet.entity.event.RebootEvent;
import com.omni.testnet.entity.event.ScanResultEvent;
import com.omni.testnet.entity.event.SendSuccessEvent;
import com.omni.testnet.framelibrary.entity.User;
import com.omni.testnet.ui.activity.channel.ChannelsActivity;
import com.omni.testnet.utils.CopyUtil;
import com.omni.testnet.utils.PaymentRequestUtil;
import com.omni.testnet.utils.UriUtil;
import com.omni.testnet.view.dialog.CreateChannelDialog;
import com.omni.testnet.view.dialog.PayInvoiceDialog;
import com.omni.testnet.view.dialog.SendDialog;
import com.omni.testnet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.testnet.view.popupwindow.FundPopupWindow;
import com.omni.testnet.view.popupwindow.Menu1PopupWindow;
import com.omni.testnet.view.popupwindow.TokenInfoPopupWindow;
import com.omni.testnet.view.popupwindow.TransactionsDetailsAssetPopupWindow;
import com.omni.testnet.view.popupwindow.TransactionsDetailsChainPopupWindow;
import com.omni.testnet.view.popupwindow.TransactionsDetailsPopupWindow;
import com.omni.testnet.view.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;
import com.omni.testnet.view.popupwindow.createinvoice.CreateLuckyPacketPopupWindow;
import com.omni.testnet.view.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    private List<PaymentEntity> mPayData = new ArrayList<>();
    private List<PaymentEntity> mReceiveData = new ArrayList<>();
    private List<PaymentEntity> mTransactionsData = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    private List<InvoiceEntity> mToBePaidData = new ArrayList<>();
    private ToBePaidAdapter mToBePaidAdapter;
    private List<LightningOuterClass.Invoice> mMyInvoicesData = new ArrayList<>();
    private MyInvoicesAdapter mMyInvoicesAdapter;
    private List<LightningOuterClass.Transaction> mTransactionsChainData = new ArrayList<>();
    private TransactionsChainAdapter mTransactionsChainAdapter;
    private List<LightningOuterClass.Transaction> mPendingTxsChainData = new ArrayList<>();
    private PendingTxsChainAdapter mPendingTxsChainAdapter;
    private List<LightningOuterClass.AssetTx> mTransactionsAssetData = new ArrayList<>();
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
            mNetworkTypeTv.setText(User.getInstance().getNetwork(mContext));
            if (assetId == 0) {
                mNetworkTv.setText("BTC lightning network");
                mNetwork1Tv.setText("BTC lightning network");
                mNetwork2Tv.setText("BTC lightning network");
                mNetwork3Tv.setText("BTC lightning network");
            } else {
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
            mNetworkTypeTv.setText(User.getInstance().getNetwork(mContext));
            if (assetId == 0) {
                mNetworkTv.setText("BTC Mainnet");
                mNetwork1Tv.setText("BTC Mainnet");
                mNetwork2Tv.setText("BTC Mainnet");
                mNetwork3Tv.setText("BTC Mainnet");
            } else {
                mNetworkTv.setText("Omnilayer Mainnet");
                mNetwork1Tv.setText("Omnilayer Mainnet");
                mNetwork2Tv.setText("Omnilayer Mainnet");
                mNetwork3Tv.setText("Omnilayer Mainnet");
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
            mAssetNameTv.setText("BTC");
            mBalanceUnitTv.setText("BTC");
            mBalanceUnit1Tv.setText("BTC");
            mBalanceUnit2Tv.setText("BTC");
            mBalanceUnit3Tv.setText("BTC");
            mTokenInfoTv.setVisibility(View.GONE);
        } else {
            mAssetLogoIv.setImageResource(R.mipmap.icon_usdt_logo_small);
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
            mBalanceAccountTv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount1Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount2Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            mBalanceAccount3Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000));
            if (assetId == 0) {
                mBalanceAccountExchangeTv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange1Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange2Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                mBalanceAccountExchange3Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            } else {
                mBalanceAccountExchangeTv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange1Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange2Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                mBalanceAccountExchange3Tv.setText(df.format(Double.parseDouble(String.valueOf(balanceAccount)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            }
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initTransactionsData();
        initToBePaidData();
        initMyInvoicesData();
    }

    /**
     * initialize the list of activity
     * 初始化交易列表
     */
    private void initTransactionsData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTransactionsRecyclerView.setLayoutManager(layoutManager);
        if (network.equals("link")) {
            if (assetId == 0) {
                mTransactionsChainAdapter = new TransactionsChainAdapter(mContext, mTransactionsChainData, R.layout.layout_item_transactions_list);
                mTransactionsRecyclerView.setAdapter(mTransactionsChainAdapter);
                getTransactions();
            } else {
                mTransactionsAssetAdapter = new TransactionsAssetAdapter(mContext, mTransactionsAssetData, R.layout.layout_item_transactions_list);
                mTransactionsRecyclerView.setAdapter(mTransactionsAssetAdapter);
                listTransactions();
            }
        } else if (network.equals("lightning")) {
            mTransactionsAdapter = new TransactionsAdapter(mContext, mTransactionsData, R.layout.layout_item_transactions_list);
            mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);
            fetchTransactionsFromLND();
        }
    }

    /**
     * @description: getTransactions
     * @描述： 获取链上btc交易记录
     */
    private void getTransactions() {
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
                try {
                    LightningOuterClass.TransactionDetails resp = LightningOuterClass.TransactionDetails.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------getTransactionsOnResponse-----------------" + resp);
                    mTransactionsChainData.addAll(resp.getTransactionsList());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTransactionsChainAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @description: ListTransactions
     * @描述： 获取链上asset交易记录
     */
    private void listTransactions() {
        mTransactionsAssetData.clear();
        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
        String txidListJson = txidSp.getString("txidListKey", "");
        if (!StringUtils.isEmpty(txidListJson)) {
            Gson gson = new Gson();
            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
            }.getType()); //将json字符串转换成List集合
            removeDuplicate(txidList);
            LogUtils.e(TAG, "========txid=====" + txidListJson);
            for (int i = 0; i < txidList.size(); i++) {
                getOmniTransactions(txidList.get(i));
            }
//            oBListTransactions();
        }
//        else {
//            oBListTransactions();
//        }
    }

    private void getOmniTransactions(String txid) {
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
                try {
                    LightningOuterClass.AssetTx resp = LightningOuterClass.AssetTx.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------oB_GetOmniTransactionOnResponse-----------------" + resp);
                    List<LightningOuterClass.AssetTx> mData = new ArrayList<>();
                    mData.add(resp);
                    mTransactionsAssetData.addAll(mData);
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
                    mTransactionsAssetData.addAll(resp.getListList());
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
    public void fetchTransactionsFromLND() {
        mTransactionsData.clear();
        LightningOuterClass.ListPaymentsRequest paymentsRequest;
        if (assetId == 0) {
            paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(false)
                    .setIncludeIncomplete(false)
                    .build();
        } else {
            paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setIsQueryAsset(true)
                    .setIncludeIncomplete(false)
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
                    fetchReceiveInvoicesFromLND(100);
                    return;
                }
                try {
                    LightningOuterClass.ListPaymentsResponse resp = LightningOuterClass.ListPaymentsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------paymentsOnResponse-----------------" + resp);
                    mPayData.clear();
                    for (int i = 0; i < resp.getPaymentsList().size(); i++) {
                        PaymentEntity paymentEntity = new PaymentEntity();
                        paymentEntity.setDate(resp.getPaymentsList().get(i).getCreationDate());
                        paymentEntity.setAssetId(resp.getPaymentsList().get(i).getAssetId());
                        paymentEntity.setAmount(resp.getPaymentsList().get(i).getValueMsat());
                        paymentEntity.setType(1);
                        mPayData.add(paymentEntity);
                    }
                    mTransactionsData.addAll(Lists.reverse(mPayData));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTransactionsAdapter.notifyDataSetChanged();
                        }
                    });
                    fetchReceiveInvoicesFromLND(100);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This will fetch all lightning invoices from LND.
     * 请求发票列表各个状态的接口
     */
    private void fetchReceiveInvoicesFromLND(long lastIndex) {
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
                LogUtils.e(TAG, "------------------ReceiveInvoiceOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
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
                        mTransactionsData.addAll(Lists.reverse(mReceiveData));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTransactionsAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        fetchReceiveInvoicesFromLND(lastIndex + 100);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
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
            removeDuplicate(txidList);
            LogUtils.e(TAG, "========txid=====" + txidListJson);
            for (int i = 0; i < txidList.size(); i++) {
                LightningOuterClass.GetOmniTransactionRequest getOmniTransactionRequest = LightningOuterClass.GetOmniTransactionRequest.newBuilder()
                        .setTxid(txidList.get(i))
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
                        try {
                            LightningOuterClass.AssetTx resp = LightningOuterClass.AssetTx.parseFrom(bytes);
                            LogUtils.e(TAG, "------------------oB_GetOmniTransactionOnResponse-----------------" + resp);
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
    private class TransactionsChainAdapter extends CommonRecyclerAdapter<LightningOuterClass.Transaction> {

        public TransactionsChainAdapter(Context context, List<LightningOuterClass.Transaction> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Transaction item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getTimeStamp() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getAmount() < 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000).replace("-", ""));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "SENT");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_right_blue);
                }
            } else if (item.getAmount() > 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "RECEIVED");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                }
            }
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    private class TransactionsAssetAdapter extends CommonRecyclerAdapter<LightningOuterClass.AssetTx> {

        public TransactionsAssetAdapter(Context context, List<LightningOuterClass.AssetTx> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.AssetTx item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getBlocktime() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getType().equals("Simple Send")) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getAmount())));
                if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "PENDING");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "RECEIVED");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                }
            } else if (item.getType().equals("Send To Many")) {
                if (item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    holder.setText(R.id.tv_amount, df.format(Double.parseDouble(item.getTotalamount())));
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        holder.setText(R.id.tv_state, "PENDING");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_state, "SENT");
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
                        holder.setText(R.id.tv_state, "PENDING");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_state, "RECEIVED");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                    }
                }
            }
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    private class TransactionsAdapter extends CommonRecyclerAdapter<PaymentEntity> {

        public TransactionsAdapter(Context context, List<PaymentEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final PaymentEntity item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getDate() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getAssetId() == 0) {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount() / 1000)) / 100000000));
            } else {
                holder.setText(R.id.tv_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
            }
            if (item.getType() == 1) {
                holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_right_blue);
                holder.setText(R.id.tv_state, "SENT");
            } else if (item.getType() == 2) {
                holder.setImageResource(R.id.iv_state, R.mipmap.icon_arrow_left_green_small);
                holder.setText(R.id.tv_state, "RECEIVED");
            }
//            holder.setOnItemClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
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
                    mPayInvoiceDialog = new PayInvoiceDialog(mContext);
                    mPayInvoiceDialog.show(pubkey, assetId, UriUtil.generateLightningUri(item.getInvoice()));
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
        mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
        mCreateInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, assetId, balanceAccount);
    }

    /**
     * click lucky packet button
     * 点击Create lucky packet按钮
     */
    @OnClick(R.id.layout_lucky_packet)
    public void clickLuckyPacket() {
//        ToastUtils.showToast(mContext, "Not yet open, please wait");
        mCreateLuckyPacketPopupWindow = new CreateLuckyPacketPopupWindow(mContext);
        mCreateLuckyPacketPopupWindow.show(mParentLayout, pubkey, assetId, balanceAccount);
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
        fetchTransactionsFromLND();
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
        fetchInvoicesFromLND(100);
    }

    /**
     * 支付成功后的消息通知监听
     * Message notification monitoring after successful payment
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendSuccessEvent(SendSuccessEvent event) {
        if (assetId == 0) {
            getTransactions();
            getPendingTxsChain();
        } else {
            listTransactions();
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
    }
}
