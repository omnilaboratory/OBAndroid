package com.omni.wallet_mainnet.ui.activity.channel;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.api.services.drive.model.FileList;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.base.AppBaseActivity;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.PermissionUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.common.ConstantWithNetwork;
import com.omni.wallet_mainnet.common.NetworkType;
import com.omni.wallet_mainnet.entity.event.BackUpEvent;
import com.omni.wallet_mainnet.entity.event.CloseChannelEvent;
import com.omni.wallet_mainnet.entity.event.PayInvoiceSuccessEvent;
import com.omni.wallet_mainnet.entity.event.RebootEvent;
import com.omni.wallet_mainnet.entity.event.ScanResultEvent;
import com.omni.wallet_mainnet.entity.event.SubscribeChannelChangeEvent;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.ui.activity.ScanActivity;
import com.omni.wallet_mainnet.ui.activity.UnlockActivity;
import com.omni.wallet_mainnet.utils.CopyUtil;
import com.omni.wallet_mainnet.utils.DriveServiceHelper;
import com.omni.wallet_mainnet.utils.MoveCacheFileToFileObd;
import com.omni.wallet_mainnet.utils.UriUtil;
import com.omni.wallet_mainnet.utils.Wallet;
import com.omni.wallet_mainnet.view.dialog.CreateChannelDialog;
import com.omni.wallet_mainnet.view.dialog.LoadingDialog;
import com.omni.wallet_mainnet.view.dialog.PayInvoiceDialog;
import com.omni.wallet_mainnet.view.dialog.SendDialog;
import com.omni.wallet_mainnet.view.popupwindow.ChannelDetailsPopupWindow;
import com.omni.wallet_mainnet.view.popupwindow.Menu2PopupWindow;
import com.omni.wallet_mainnet.view.popupwindow.SelectNodePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.tv_network_type)
    TextView mNetworkTypeTv;
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

    Menu2PopupWindow mMenuPopupWindow;
    ChannelDetailsPopupWindow mChannelDetailsPopupWindow;
    SelectNodePopupWindow mSelectNodePopupWindow;
    SendDialog mSendDialog;

    public static final String KEY_BALANCE_AMOUNT = "balanceAmountKey";
    public static final String KEY_WALLET_ADDRESS = "walletAddressKey";
    public static final String KEY_PUBKEY = "pubkeyKey";
    public static final String KEY_CHANNEL = "channelKey";
    public static final String KEY_ASSET_ID = "assetIdKey";
    long balanceAmount;
    String walletAddress;
    private String pubkey;
    private String channelKey;
    long assetIdFilter;
    private String mCurrentSearchString = "";

    PayInvoiceDialog mPayInvoiceDialog;
    CreateChannelDialog mCreateChannelDialog;

    private static final int REQUEST_CODE_SIGN_IN = 3;
    private DriveServiceHelper mDriveServiceHelper;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void getBundleData(Bundle bundle) {
        balanceAmount = bundle.getLong(KEY_BALANCE_AMOUNT);
//        walletAddress = bundle.getString(KEY_WALLET_ADDRESS);
        walletAddress = User.getInstance().getWalletAddress(mContext);
        pubkey = bundle.getString(KEY_PUBKEY);
        channelKey = bundle.getString(KEY_CHANNEL);
        assetIdFilter = bundle.getLong(KEY_ASSET_ID);
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
        mLoadingDialog = new LoadingDialog(mContext);
        if (ConstantInOB.networkType == NetworkType.TEST) {
            mNetworkTypeTv.setText("testnet");
        } else if (ConstantInOB.networkType == NetworkType.REG) {
            mNetworkTypeTv.setText("regtest");
        } else if (ConstantInOB.networkType == NetworkType.MAIN) {
            mNetworkTypeTv.setText("mainnet");
        }
        if (balanceAmount == 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            mBalanceAmountTv.setText("My account " + df.format(Double.parseDouble(String.valueOf(balanceAmount)) / 100000000));
        } else {
            DecimalFormat df = new DecimalFormat("0.00######");
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
                if (channelKey.equals("all")) {
                    OpenChannelItem openChannelItem = new OpenChannelItem(c);
                    if (c.getActive()) {
                        mChannelItems.add(openChannelItem);
                    } else {
                        offlineChannels.add(openChannelItem);
                    }
                } else if (channelKey.equals("asset")) {
                    int assetId = c.getAssetId();
                    long mAssetId = assetId & 0xffffffffL;
                    if (mAssetId == assetIdFilter) {
                        OpenChannelItem openChannelItem = new OpenChannelItem(c);
                        if (c.getActive()) {
                            mChannelItems.add(openChannelItem);
                        } else {
                            offlineChannels.add(openChannelItem);
                        }
                    }
                }
            }
        }
        // Add all pending channel items
        // Add open pending
        if (Wallet.getInstance().mPendingOpenChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.PendingOpenChannel c : Wallet.getInstance().mPendingOpenChannelsList) {
                if (channelKey.equals("all")) {
                    PendingOpenChannelItem pendingOpenChannelItem = new PendingOpenChannelItem(c);
                    mChannelItems.add(pendingOpenChannelItem);
                } else if (channelKey.equals("asset")) {
                    int assetId = c.getChannel().getAssetId();
                    long mAssetId = assetId & 0xffffffffL;
                    if (mAssetId == assetIdFilter) {
                        PendingOpenChannelItem pendingOpenChannelItem = new PendingOpenChannelItem(c);
                        mChannelItems.add(pendingOpenChannelItem);
                    }
                }
            }
        }
        // Add closing pending
        if (Wallet.getInstance().mPendingClosedChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.ClosedChannel c : Wallet.getInstance().mPendingClosedChannelsList) {
                if (channelKey.equals("all")) {
                    PendingClosingChannelItem pendingClosingChannelItem = new PendingClosingChannelItem(c);
                    mChannelItems.add(pendingClosingChannelItem);
                } else if (channelKey.equals("asset")) {
                    int assetId = c.getChannel().getAssetId();
                    long mAssetId = assetId & 0xffffffffL;
                    if (mAssetId == assetIdFilter) {
                        PendingClosingChannelItem pendingClosingChannelItem = new PendingClosingChannelItem(c);
                        mChannelItems.add(pendingClosingChannelItem);
                    }
                }
            }
        }
        // Add force closing pending
        if (Wallet.getInstance().mPendingForceClosedChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.ForceClosedChannel c : Wallet.getInstance().mPendingForceClosedChannelsList) {
                if (channelKey.equals("all")) {
                    PendingForceClosingChannelItem pendingForceClosingChannelItem = new PendingForceClosingChannelItem(c);
                    mChannelItems.add(pendingForceClosingChannelItem);
                } else if (channelKey.equals("asset")) {
                    int assetId = c.getChannel().getAssetId();
                    long mAssetId = assetId & 0xffffffffL;
                    if (mAssetId == assetIdFilter) {
                        PendingForceClosingChannelItem pendingForceClosingChannelItem = new PendingForceClosingChannelItem(c);
                        mChannelItems.add(pendingForceClosingChannelItem);
                    }
                }
            }
        }
        // Add waiting for close
        if (Wallet.getInstance().mPendingWaitingCloseChannelsList != null) {
            for (LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel c : Wallet.getInstance().mPendingWaitingCloseChannelsList) {
                if (channelKey.equals("all")) {
                    WaitingCloseChannelItem waitingCloseChannelItem = new WaitingCloseChannelItem(c);
                    mChannelItems.add(waitingCloseChannelItem);
                } else if (channelKey.equals("asset")) {
                    int assetId = c.getChannel().getAssetId();
                    long mAssetId = assetId & 0xffffffffL;
                    if (mAssetId == assetIdFilter) {
                        WaitingCloseChannelItem waitingCloseChannelItem = new WaitingCloseChannelItem(c);
                        mChannelItems.add(waitingCloseChannelItem);
                    }
                }
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
    @OnClick(R.id.layout_scan)
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
    @OnClick(R.id.layout_menu)
    public void clickMemu() {
        mMenuPopupWindow = new Menu2PopupWindow(mContext);
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

    /**
     * click filter button
     * 点击Filter按钮
     */
    @OnClick(R.id.iv_filter)
    public void clickFilter() {
        ToastUtils.showToast(mContext, "Not yet open, please wait");
    }

    @OnClick(R.id.iv_create_channel)
    public void clickcCreateChannel() {
        mCreateChannelDialog = new CreateChannelDialog(mContext);
        mCreateChannelDialog.show(balanceAmount, walletAddress, "");
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
        if (StringUtils.isEmpty(walletAddress)) {
            ToastUtils.showToast(mContext, "Please waiting for a while");
            return;
        }
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
                                    mPayInvoiceDialog.show(pubkey, resp.getAssetId(), event.getData(), 3);
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (event.getTag() == 3) {
                    autoBackupFiles();
                }
            }
        });
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

    /**
     * 重启节点后的消息通知监听
     * Message notification monitoring after reboot
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRebootEvent(RebootEvent event) {
        switchActivityFinish(UnlockActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribeChannelChangeEvent(SubscribeChannelChangeEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String runningActivityName = getRunningActivityName();
                String[] runningActivityNameArr = runningActivityName.split("\\.");
                String name = runningActivityNameArr[5];
                switch (name) {
                    case "channel":
                        if (User.getInstance().isAutoBackUp(mContext) == false) {
                            autoBackupFiles();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackUpEventEvent(BackUpEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (event.getCode() == 3) {
                    backupFiles();
                }
            }
        });
    }

    private void backupFiles() {
        File walletPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db");
        File channelPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db");
        String storagePath = Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles";
        File toWalletPath = new File(Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles/wallet.db");
        File toChannelPath = new File(Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles/channel.db");
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
     * Handles the {@code result} of a completed sign-in activity initiated from requestSignIn.
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
                    User.getInstance().setGoogleAccountName(mContext, googleAccount.getAccount().name);
                    User.getInstance().setGoogleAccountType(mContext, googleAccount.getAccount().type);
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
                    query();
                })
                .addOnFailureListener(exception -> LogUtils.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void query() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles().addOnSuccessListener(new OnSuccessListener<FileList>() {
                @Override
                public void onSuccess(FileList fileList) {
                    if (fileList.getFiles().size() == 0) {
                        createAddressFile();
                    } else {
                        List<com.google.api.services.drive.model.File> list = new ArrayList<>();
                        for (int i = 0; i < fileList.getFiles().size(); i++) {
                            if (fileList.getFiles().get(i).getName().contains("_mainnet")) {
                                list.add(fileList.getFiles().get(i));
                            }
                        }
                        if (list.size() == 0) {
                            createAddressFile();
                        } else {
                            saveAddressFile(list.get(1).getId(), list.get(0).getId(), list.get(2).getId());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Unable to query files.", e);
                }
            });
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createAddressFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating a address file.");
            mLoadingDialog.show();
            mDriveServiceHelper.createFile(User.getInstance().getWalletAddress(mContext) + "_mainnet")
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
            mDriveServiceHelper.createFile(filePath, "wallet_mainnet.db").addOnSuccessListener(new OnSuccessListener<String>() {
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
            mDriveServiceHelper.createFile(filePath, "channel_mainnet.db").addOnSuccessListener(new OnSuccessListener<String>() {
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

    private void saveAddressFile(String walletFileId, String channelFileId, String addressFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save address file " + addressFileId);
            mLoadingDialog.show();
            mDriveServiceHelper.saveAddressFile(addressFileId, User.getInstance().getWalletAddress(mContext) + "_mainnet")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            saveWalletFile(walletFileId, channelFileId);
                        }
                    })
                    .addOnFailureListener(exception -> {
                        mLoadingDialog.dismiss();
                        LogUtils.e(TAG, "Couldn't Save address file.", exception);
                    });
        }
    }

    private void saveWalletFile(String walletFileId, String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save wallet file " + walletFileId);

            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db";
            mDriveServiceHelper.saveDbFile(walletFileId, filePath, "wallet_mainnet.db")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            saveChannelFile(channelFileId);
                        }
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't Save wallet file.", exception));
        }
    }

    private void saveChannelFile(String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save channel file " + channelFileId);

            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db";
            mDriveServiceHelper.saveDbFile(channelFileId, filePath, "channel_mainnet.db")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            LogUtils.e(TAG, "Channel fileId" + s);
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, "Backup Successfully");
                        }
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't Save channel file.", exception));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wallet.getInstance().unregisterChannelsUpdatedSubscriptionListener(this);
        EventBus.getDefault().unregister(this);
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.release();
        }
        if (mChannelDetailsPopupWindow != null) {
            mChannelDetailsPopupWindow.release();
        }
        if (mSendDialog != null) {
            mSendDialog.release();
        }
        if (mPayInvoiceDialog != null) {
            mPayInvoiceDialog.release();
        }
        if (mCreateChannelDialog != null) {
            mCreateChannelDialog.release();
        }
    }
}
