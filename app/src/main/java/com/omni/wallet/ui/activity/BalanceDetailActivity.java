package com.omni.wallet.ui.activity;

import android.content.Context;
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

import com.google.common.io.BaseEncoding;
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
import com.omni.wallet.entity.event.CreateInvoiceEvent;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.popupwindow.TokenInfoPopupWindow;
import com.omni.wallet.view.popupwindow.TransactionsDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;
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

import static lnrpc.LightningOuterClass.Invoice.InvoiceState.ACCEPTED;
import static lnrpc.LightningOuterClass.Invoice.InvoiceState.CANCELED;
import static lnrpc.LightningOuterClass.Invoice.InvoiceState.OPEN;
import static lnrpc.LightningOuterClass.Invoice.InvoiceState.SETTLED;
import static lnrpc.LightningOuterClass.Invoice.InvoiceState.UNRECOGNIZED;
import static lnrpc.LightningOuterClass.Payment.PaymentStatus.SUCCEEDED;

public class BalanceDetailActivity extends AppBaseActivity {
    private static final String TAG = AccountLightningActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    RelativeLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_network)
    ImageView mNetworkIv;
    @BindView(R.id.tv_network_type)
    TextView mNetworkTypeTv;
    @BindView(R.id.tv_network)
    TextView mNetworkTv;
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
    @BindView(R.id.tv_balance_account_2)
    TextView mBalanceAccount2Tv;
    @BindView(R.id.tv_balance_unit_2)
    TextView mBalanceUnit2Tv;
    @BindView(R.id.tv_balance_account_exchange_2)
    TextView mBalanceAccountExchange2Tv;
    @BindView(R.id.tv_balance_unit_exchange_2)
    TextView mBalanceUnitExchange2Tv;
    @BindView(R.id.tv_balance_account_3)
    TextView mBalanceAccount3Tv;
    @BindView(R.id.tv_balance_unit_3)
    TextView mBalanceUnit3Tv;
    @BindView(R.id.tv_balance_account_exchange_3)
    TextView mBalanceAccountExchange3Tv;
    @BindView(R.id.tv_balance_unit_exchange_3)
    TextView mBalanceUnitExchange3Tv;
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
    private List<LightningOuterClass.Payment> mTransactionsData = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    private List<LightningOuterClass.Payment> mToBePaidData = new ArrayList<>();
    private ToBePaidAdapter mToBePaidAdapter;
    private List<LightningOuterClass.Invoice> mMyInvoicesData = new ArrayList<>();
    private MyInvoicesAdapter mMyInvoicesAdapter;

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

    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow;
    TransactionsDetailsPopupWindow mTransactionsDetailsPopupWindow;
    TokenInfoPopupWindow mTokenInfoPopupWindow;

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
            mNetworkTypeTv.setText(R.string.lightning);
            mNetworkTv.setText("USDT lightning network");
            mLightningNetworkLayout.setVisibility(View.VISIBLE);
            mLinkNetworkLayout.setVisibility(View.GONE);
            mChannelActivitiesTv.setText(R.string.channel_activities);
            mChannelActivitiesTv.setTextColor(Color.parseColor("#4A92FF"));
            mToBePaidTv.setText(R.string.to_be_paid);
            mToBePaidTitleTv.setText(R.string.to_be_paid);
            mToBePaidTv.setTextColor(Color.parseColor("#4A92FF"));
            mLineView.setVisibility(View.VISIBLE);
            mRootMyInvoicesLayout.setVisibility(View.VISIBLE);
        } else if (network.equals("link")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_link_black);
            mNetworkTypeTv.setText(R.string.mainnet);
            mNetworkTv.setText("Omnilayer Mainnet");
            mLightningNetworkLayout.setVisibility(View.GONE);
            mLinkNetworkLayout.setVisibility(View.VISIBLE);
            mChannelActivitiesTv.setText("My Account Activities");
            mChannelActivitiesTv.setTextColor(Color.parseColor("#000000"));
            mToBePaidTv.setText(R.string.pending_txs);
            mToBePaidTitleTv.setText(R.string.pending_txs);
            mToBePaidTv.setTextColor(Color.parseColor("#000000"));
            mLineView.setVisibility(View.GONE);
            mRootMyInvoicesLayout.setVisibility(View.GONE);
        }
        if (assetId == 0) {
            mAssetLogoIv.setImageResource(R.mipmap.icon_btc_logo_small);
            mAssetNameTv.setText("BTC");
        } else {
            mAssetLogoIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            mAssetNameTv.setText("USDT");
        }
        mBalanceAmountTv.setText("My account " + balanceAmount);
        mWalletAddressTv.setText(walletAddress);
        mBalanceAccountTv.setText(balanceAccount + "");
        mBalanceAccountExchangeTv.setText(balanceAccount + "");
        mBalanceAccount1Tv.setText(balanceAccount + "");
        mBalanceAccountExchange1Tv.setText(balanceAccount + "");
        mBalanceAccount2Tv.setText(balanceAccount + "");
        mBalanceAccountExchange2Tv.setText(balanceAccount + "");
        mBalanceAccount3Tv.setText(balanceAccount + "");
        mBalanceAccountExchange3Tv.setText(balanceAccount + "");
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
        mTransactionsAdapter = new TransactionsAdapter(mContext, mTransactionsData, R.layout.layout_item_transactions_list);
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);
        fetchTransactionsFromLND();
    }

    /**
     * This will fetch lightning Transactions from LND.
     * 请求交易列表各个状态的接口
     */
    public void fetchTransactionsFromLND() {
        LightningOuterClass.ListPaymentsRequest paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                .setAssetId((int) assetId)
                .setIsQueryAsset(true)
                .setIncludeIncomplete(false)
                .build();
        Obdmobile.listPayments(paymentsRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------paymentsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListPaymentsResponse resp = LightningOuterClass.ListPaymentsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------paymentsOnResponse-----------------" + resp);
                    mTransactionsData.clear();
                    mTransactionsData.addAll(resp.getPaymentsList());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTransactionsAdapter.notifyDataSetChanged();
                        }
                    });
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
        mToBePaidAdapter = new ToBePaidAdapter(mContext, mToBePaidData, R.layout.layout_item_to_be_paid_list);
        mToBePaidRecyclerView.setAdapter(mToBePaidAdapter);
        fetchPaymentsFromLND();
    }

    /**
     * This will fetch lightning payments from LND.
     * 请求支付列表各个状态的接口
     */
    public void fetchPaymentsFromLND() {
        LightningOuterClass.ListPaymentsRequest paymentsRequest = LightningOuterClass.ListPaymentsRequest.newBuilder()
                .setAssetId((int) assetId)
                .setIsQueryAsset(true)
                .setIncludeIncomplete(false)
                .build();
        Obdmobile.listPayments(paymentsRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------toBePaidPaymentsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListPaymentsResponse resp = LightningOuterClass.ListPaymentsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------toBePaidPaymentsOnResponse-----------------" + resp);
                    mToBePaidData.clear();
                    for (LightningOuterClass.Payment payment : resp.getPaymentsList()) {
                        if (payment.getStatus() != SUCCEEDED) {
                            mToBePaidData.add(payment);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mToBePaidNumTv.setText(mToBePaidData.size() + "");
                            mToBePaidAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
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
        LightningOuterClass.ListInvoiceRequest invoiceRequest = LightningOuterClass.ListInvoiceRequest.newBuilder()
                .setAssetId((int) assetId)
                .setIsQueryAsset(true)
                .setNumMaxInvoices(lastIndex)
                .build();
        Obdmobile.listInvoices(invoiceRequest.toByteArray(), new Callback() {
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
                        mMyInvoicesData.addAll(resp.getInvoicesList());
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
    private class TransactionsAdapter extends CommonRecyclerAdapter<LightningOuterClass.Payment> {

        public TransactionsAdapter(Context context, List<LightningOuterClass.Payment> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Payment item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getCreationDate() + ""));
            holder.setText(R.id.tv_amount, item.getValueMsat() + "");
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTransactionsDetailsPopupWindow = new TransactionsDetailsPopupWindow(mContext);
                    mTransactionsDetailsPopupWindow.show(mParentLayout);
                }
            });
        }
    }

    /**
     * the adapter of to be paid list
     * 未支付列表适配器
     */
    private class ToBePaidAdapter extends CommonRecyclerAdapter<LightningOuterClass.Payment> {

        public ToBePaidAdapter(Context context, List<LightningOuterClass.Payment> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Payment item) {
            holder.setText(R.id.tv_time, DateUtils.MonthDay(item.getCreationDate() + ""));
            holder.setText(R.id.tv_amount, item.getValueMsat() + "");
            holder.setText(R.id.tv_receiver, "unknown");
            final SwipeMenuLayout menuLayout = holder.getView(R.id.layout_to_be_paid_list_swipe_menu);
            holder.getView(R.id.tv_to_be_paid_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Used to delete a payment probe.
                     * 删除付款
                     */
                    LightningOuterClass.DeletePaymentRequest deletePaymentRequest = LightningOuterClass.DeletePaymentRequest.newBuilder()
                            .setPaymentHash(byteStringFromHex(""))
                            .setFailedHtlcsOnly(false)
                            .build();
                    Obdmobile.deletePayment(deletePaymentRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            LogUtils.e(TAG, "------------------deletePaymentOnError------------------" + e.getMessage());
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.DeletePaymentResponse resp = LightningOuterClass.DeletePaymentResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------deletePaymentOnResponse-----------------" + resp);
                                menuLayout.quickClose();
                                mToBePaidData.remove(position);
                                mToBePaidAdapter.notifyRemoveItem(position);
                                if (mToBePaidData.size() == 0) {
                                    mToBePaidAdapter.notifyDataSetChanged();
                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
            holder.setText(R.id.tv_amount, item.getValueMsat() + "");
            holder.setText(R.id.tv_memo, item.getMemo());
            if (StringUtils.isEmpty(String.valueOf(item.getState()))) {
                holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
            } else {
                if (item.getState() == OPEN) {
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                } else if (item.getState() == SETTLED) {
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                } else if (item.getState() == CANCELED) {
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_off_red);
                } else if (item.getState() == ACCEPTED) {
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else if (item.getState() == UNRECOGNIZED) {
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                }
            }
            final SwipeMenuLayout menuLayout = holder.getView(R.id.layout_my_invoices_list_swipe_menu);
            holder.getView(R.id.tv_my_invoices_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuLayout.quickClose();
                    mMyInvoicesData.remove(position);
                    mMyInvoicesAdapter.notifyRemoveItem(position);
                    if (mMyInvoicesData.size() == 0) {
                        mMyInvoicesAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
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
     * click more button
     * 点击More按钮
     */
    @OnClick(R.id.layout_more)
    public void clickMore() {

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
        mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
        mPayInvoiceStepOnePopupWindow.show(mParentLayout, pubkey, assetId);
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
     * click send button
     * 点击Send按钮
     */
    @OnClick(R.id.layout_send_invoice)
    public void clickSendInvoice() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
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
     * 创建发票后的消息通知监听
     * Message notification monitoring after create invoice
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateInvoiceEvent(CreateInvoiceEvent event) {
        fetchInvoicesFromLND(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPayInvoiceStepOnePopupWindow != null) {
            mPayInvoiceStepOnePopupWindow.release();
        }
        if (mSendStepOnePopupWindow != null) {
            mSendStepOnePopupWindow.release();
        }
    }
}
