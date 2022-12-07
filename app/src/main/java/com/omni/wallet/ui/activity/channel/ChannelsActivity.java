package com.omni.wallet.ui.activity.channel;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.entity.event.CloseChannelEvent;
import com.omni.wallet.entity.event.ScanResultEvent;
import com.omni.wallet.ui.activity.ScanActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.UriUtil;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.view.dialog.CreateChannelDialog;
import com.omni.wallet.view.dialog.PayInvoiceDialog;
import com.omni.wallet.view.popupwindow.ChannelDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.MenuPopupWindow;
import com.omni.wallet.view.popupwindow.SelectNodePopupWindow;
import com.omni.wallet.view.popupwindow.send.SendStepOnePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class ChannelsActivity extends AppBaseActivity implements ChannelSelectListener, Wallet.ChannelsUpdatedSubscriptionListener {
    private static final String TAG = ChannelsActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_menu)
    ImageView mMenuIv;
    @BindView(R.id.tv_balance_amount)
    TextView mBalanceAmountTv;
    @BindView(R.id.tv_wallet_address)
    TextView mWalletAddressTv;
    @BindView(R.id.edit_search)
    EditText mSearchEdit;

    @BindView(R.id.recycler_channels_list)
    public RecyclerView mRecyclerView;// 通道列表的RecyclerView

    private ChannelItemAdapter mAdapter;
    private List<ChannelListItem> mChannelItems = new ArrayList<>();

    MenuPopupWindow mMenuPopupWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;
    ChannelDetailsPopupWindow mChannelDetailsPopupWindow;
    SelectNodePopupWindow mSelectNodePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;

    public static final String KEY_BALANCE_AMOUNT = "balanceAmountKey";
    public static final String KEY_WALLET_ADDRESS = "walletAddressKey";
    public static final String KEY_PUBKEY = "pubkeyKey";
    long balanceAmount;
    String walletAddress;
    private String pubkey;
    private String mCurrentSearchString = "";

    PayInvoiceDialog mPayInvoiceDialog;
    CreateChannelDialog mCreateChannelDialog;

    @Override
    protected void getBundleData(Bundle bundle) {
        balanceAmount = bundle.getLong(KEY_BALANCE_AMOUNT);
        walletAddress = bundle.getString(KEY_WALLET_ADDRESS);
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
        return R.layout.activity_channels;
    }

    @Override
    protected void initView() {
        if (balanceAmount == 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            mBalanceAmountTv.setText("My account " + df.format(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000));
        } else {
            DecimalFormat df = new DecimalFormat("0.00000000");
            mBalanceAmountTv.setText("My account " + df.format(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000));
        }
        mWalletAddressTv.setText(walletAddress);
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCurrentSearchString = s.toString();
                final List<ChannelListItem> filteredChannelList = filter(mChannelItems, s.toString());
                mAdapter.replaceAll(filteredChannelList);
                mRecyclerView.scrollToPosition(0);
            }
        });
        Wallet.getInstance().registerChannelsUpdatedSubscriptionListener(this);
        initRecyclerView();
        Wallet.getInstance().fetchChannelsFromLND();
        updateChannelsDisplayList();
    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChannelItemAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateChannelsDisplayList() {
        mChannelItems.clear();
        List<ChannelListItem> offlineChannels = new ArrayList<>();
        // Add all open channel items
        if (Wallet.getInstance().mOpenChannelsList != null) {
            for (LightningOuterClass.Channel c : Wallet.getInstance().mOpenChannelsList) {
                OpenChannelItem openChannelItem = new OpenChannelItem(c);
                if (c.getActive()) {
                    mChannelItems.add(openChannelItem);
                } else {
                    offlineChannels.add(openChannelItem);
                }
            }
        }
        // Add all pending channel items
        // Add open pending
        if (Wallet.getInstance().mPendingOpenChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.PendingOpenChannel c : Wallet.getInstance().mPendingOpenChannelsList) {
                PendingOpenChannelItem pendingOpenChannelItem = new PendingOpenChannelItem(c);
                mChannelItems.add(pendingOpenChannelItem);
            }
        }
        // Add closing pending
        if (Wallet.getInstance().mPendingClosedChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.ClosedChannel c : Wallet.getInstance().mPendingClosedChannelsList) {
                PendingClosingChannelItem pendingClosingChannelItem = new PendingClosingChannelItem(c);
                mChannelItems.add(pendingClosingChannelItem);
            }
        }
        // Add force closing pending
        if (Wallet.getInstance().mPendingForceClosedChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.ForceClosedChannel c : Wallet.getInstance().mPendingForceClosedChannelsList) {
                PendingForceClosingChannelItem pendingForceClosingChannelItem = new PendingForceClosingChannelItem(c);
                mChannelItems.add(pendingForceClosingChannelItem);
            }
        }
        // Add waiting for close
        if (Wallet.getInstance().mPendingWaitingCloseChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel c : Wallet.getInstance().mPendingWaitingCloseChannelsList) {
                WaitingCloseChannelItem waitingCloseChannelItem = new WaitingCloseChannelItem(c);
                mChannelItems.add(waitingCloseChannelItem);
            }
        }
        // Show offline channels at the bottom
        mChannelItems.addAll(offlineChannels);
        // Update the view
        if (mCurrentSearchString.isEmpty()) {
            mAdapter.replaceAll(mChannelItems);
        } else {
            final List<ChannelListItem> filteredChannelList = filter(mChannelItems, mCurrentSearchString);
            mAdapter.replaceAll(filteredChannelList);
        }
    }


    @Override
    public void onChannelSelect(ByteString channel, int type) {
        if (channel != null) {
            mChannelDetailsPopupWindow = new ChannelDetailsPopupWindow(mContext);
            mChannelDetailsPopupWindow.show(mParentLayout, channel, type, balanceAmount, walletAddress, pubkey);
        }
    }

    @Override
    public void onChannelsUpdated() {
        runOnUiThread(this::updateChannelsDisplayList);
    }

    private List<ChannelListItem> filter(List<ChannelListItem> items, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ChannelListItem> filteredItemList = new ArrayList<>();
        for (ChannelListItem item : items) {
            String text;
            String pubkey;

            switch (item.getType()) {
                case ChannelListItem.TYPE_OPEN_CHANNEL:
                    pubkey = ((OpenChannelItem) item).getChannel().getRemotePubkey();
                    text = pubkey + Wallet.getInstance().getNodeAliasFromPubKey(pubkey, ChannelsActivity.this);
                    break;
                case ChannelListItem.TYPE_PENDING_OPEN_CHANNEL:
                    pubkey = ((PendingOpenChannelItem) item).getChannel().getChannel().getRemoteNodePub();
                    text = pubkey + Wallet.getInstance().getNodeAliasFromPubKey(pubkey, ChannelsActivity.this);
                    break;
                case ChannelListItem.TYPE_PENDING_CLOSING_CHANNEL:
                    pubkey = ((PendingClosingChannelItem) item).getChannel().getChannel().getRemoteNodePub();
                    text = pubkey + Wallet.getInstance().getNodeAliasFromPubKey(pubkey, ChannelsActivity.this);
                    break;
                case ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL:
                    pubkey = ((PendingForceClosingChannelItem) item).getChannel().getChannel().getRemoteNodePub();
                    text = pubkey + Wallet.getInstance().getNodeAliasFromPubKey(pubkey, ChannelsActivity.this);
                    break;
                case ChannelListItem.TYPE_WAITING_CLOSE_CHANNEL:
                    pubkey = ((WaitingCloseChannelItem) item).getChannel().getChannel().getRemoteNodePub();
                    text = pubkey + Wallet.getInstance().getNodeAliasFromPubKey(pubkey, ChannelsActivity.this);
                    break;
                default:
                    text = "";
            }

            if (text.toLowerCase().contains(lowerCaseQuery)) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }

    /**
     * click scan button at top-right in page
     * 点击右上角扫码按钮
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        PermissionUtils.launchCamera(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                Bundle bundle = new Bundle();
                bundle.putInt(ScanActivity.KEY_SCAN_CODE, 3);
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
     * click the menu button at top-right in page
     * 点击右上角菜单按钮
     */
    @OnClick(R.id.iv_menu)
    public void clickMemu() {
        mMenuPopupWindow = new MenuPopupWindow(mContext);
        mMenuPopupWindow.show(mMenuIv, balanceAmount, walletAddress, pubkey);
    }

    /**
     * click the close button at top-right in page
     * 点击右上角关闭按钮
     */
    @OnClick(R.id.iv_close)
    public void clickClose() {
        finish();
    }

    @OnClick(R.id.iv_create_channel)
    public void clickcCreateChannel() {
        mCreateChannelDialog = new CreateChannelDialog(mContext);
        mCreateChannelDialog.show(balanceAmount, walletAddress, "");
//        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//        mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, walletAddress, "");
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
        CopyUtil.SelfCopy(ChannelsActivity.this, toCopyAddress, toastString);
    }

    @OnClick(R.id.lv_network_title_content)
    public void clickSelectNode() {
        mSelectNodePopupWindow = new SelectNodePopupWindow(mContext);
        mSelectNodePopupWindow.show(mParentLayout);
    }

    /**
     * 扫码后的消息通知监听
     * Message notification monitoring after Scan qrcode
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResultEvent(ScanResultEvent event) {
        if (event.getCode() == 3) {
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
                mCreateChannelDialog.show(balanceAmount, walletAddress, event.getData());
//                mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
//                mCreateChannelStepOnePopupWindow.show(mParentLayout, balanceAmount, walletAddress, event.getData());
            } else if (event.getType().equals("send")) {
                mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
                mSendStepOnePopupWindow.show(mParentLayout, event.getData());
            }
        }
    }

    /**
     * 关闭通道后的消息通知监听
     * Message notification monitoring after open channel
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseChannelEvent(CloseChannelEvent event) {
        Wallet.getInstance().fetchChannelsFromLND();
        updateChannelsDisplayList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wallet.getInstance().unregisterChannelsUpdatedSubscriptionListener(this);
        EventBus.getDefault().unregister(this);
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.release();
        }
        if (mCreateChannelStepOnePopupWindow != null) {
            mCreateChannelStepOnePopupWindow.release();
        }
        if (mChannelDetailsPopupWindow != null) {
            mChannelDetailsPopupWindow.release();
        }
        if (mSendStepOnePopupWindow != null) {
            mSendStepOnePopupWindow.release();
        }
        if (mPayInvoiceDialog != null) {
            mPayInvoiceDialog.release();
        }
        if (mCreateChannelDialog != null) {
            mCreateChannelDialog.release();
        }
    }
}
