package com.omni.wallet.utils;


import android.content.Context;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.entity.ListAssetItemEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

public class Wallet {

    private static final String TAG = Wallet.class.getName();

    private static Wallet mInstance = null;
    private final Set<ChannelsUpdatedSubscriptionListener> mChannelsUpdatedSubscriptionListeners = new HashSet<>();

    public List<LightningOuterClass.Channel> mOpenChannelsList;
    public List<LightningOuterClass.PendingChannelsResponse.PendingOpenChannel> mPendingOpenChannelsList;
    public List<LightningOuterClass.PendingChannelsResponse.ClosedChannel> mPendingClosedChannelsList;
    public List<LightningOuterClass.PendingChannelsResponse.ForceClosedChannel> mPendingForceClosedChannelsList;
    public List<LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel> mPendingWaitingCloseChannelsList;
    public List<LightningOuterClass.ChannelCloseSummary> mClosedChannelsList;
    public List<LightningOuterClass.NodeInfo> mNodeInfos = new LinkedList<>();

    public List<ListAssetItemEntity> blockData = new ArrayList<>();
    public List<ListAssetItemEntity> lightningData = new ArrayList<>();
    public List<ListAssetItemEntity> allData = new ArrayList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static final String START_NODE = "  --trickledelay=5000 --alias=alice\n" +
            "--listen=0.0.0.0:9735\n" +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=bitcoind\n" +
            "--bitcoind.rpchost=16.162.119.13:18332 --bitcoind.rpcuser=polaruser\n" +
            "--bitcoind.rpcpass=polarpass\n" +
            "--bitcoind.zmqpubrawblock=tcp://16.162.119.13:28332\n" +
            "--bitcoind.zmqpubrawtx=tcp://16.162.119.13:28333";

    public static final String START_NODE_OMNI = " --noseedbackup --trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=omnicoreproxy " +
            "--omnicoreproxy.rpchost=43.138.107.248:18332 " +
            "--omnicoreproxy.zmqpubrawblock=tcp://43.138.107.248:28332 " +
            "--omnicoreproxy.zmqpubrawtx=tcp://43.138.107.248:28333";

    public static final String START_NODE_OMNI_WITH_SEED = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=omnicoreproxy " +
            "--omnicoreproxy.rpchost=43.138.107.248:18332 " +
            "--omnicoreproxy.zmqpubrawblock=tcp://43.138.107.248:28332 " +
            "--omnicoreproxy.zmqpubrawtx=tcp://43.138.107.248:28333";

    private Wallet() {
        ;
    }

    public static Wallet getInstance() {
        if (mInstance == null) {
            mInstance = new Wallet();
        }
        return mInstance;
    }

    public void closeChannel(String channelPoint, boolean force) {
        LightningOuterClass.ChannelPoint point = LightningOuterClass.ChannelPoint.newBuilder()
                .setFundingTxidStr(channelPoint.substring(0, channelPoint.indexOf(':')))
                .setOutputIndex(Character.getNumericValue(channelPoint.charAt(channelPoint.length() - 1)))
                .build();

        LightningOuterClass.CloseChannelRequest closeChannelRequest = LightningOuterClass.CloseChannelRequest.newBuilder()
                .setChannelPoint(point)
                .setForce(force)
                .build();
        Obdmobile.closeChannel(closeChannelRequest.toByteArray(), new RecvStream() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------closeChannelOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.CloseStatusUpdate resp = LightningOuterClass.CloseStatusUpdate.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------closeChannelOnResponse-----------------" + resp);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fetchChannelsFromLND() {
        Obdmobile.listChannels(LightningOuterClass.ListChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listChannelsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListChannelsResponse resp = LightningOuterClass.ListChannelsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listChannelsOnResponse-----------------" + resp.getChannelsList());
                    mOpenChannelsList.clear();
                    mOpenChannelsList = resp.getChannelsList();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        Obdmobile.pendingChannels(LightningOuterClass.PendingChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------pendingChannelsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.PendingChannelsResponse resp = LightningOuterClass.PendingChannelsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------pendingChannelsOnResponse1-----------------" + resp.getPendingOpenChannelsList());
                    LogUtils.e(TAG, "------------------pendingChannelsOnResponse2-----------------" + resp.getPendingClosingChannelsList());
                    LogUtils.e(TAG, "------------------pendingChannelsOnResponse3-----------------" + resp.getPendingForceClosingChannelsList());
                    LogUtils.e(TAG, "------------------pendingChannelsOnResponse4-----------------" + resp.getWaitingCloseChannelsList());
                    mPendingOpenChannelsList.clear();
                    mPendingOpenChannelsList = resp.getPendingOpenChannelsList();
                    mPendingClosedChannelsList.clear();
                    mPendingClosedChannelsList = resp.getPendingClosingChannelsList();
                    mPendingForceClosedChannelsList.clear();
                    mPendingForceClosedChannelsList = resp.getPendingForceClosingChannelsList();
                    mPendingWaitingCloseChannelsList.clear();
                    mPendingWaitingCloseChannelsList = resp.getWaitingCloseChannelsList();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        Obdmobile.closedChannels(LightningOuterClass.ClosedChannelsRequest.newBuilder().build().toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------closedChannelsOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ClosedChannelsResponse resp = LightningOuterClass.ClosedChannelsResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------closedChannelsOnResponse-----------------" + resp.getChannelsList());
                    mClosedChannelsList.clear();
                    mClosedChannelsList = resp.getChannelsList();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
        if (mOpenChannelsList != null) {
            // Load NodeInfos for all involved nodes. This allows us to display aliases later.
            Set<String> channelNodes = new HashSet<>();

            for (LightningOuterClass.Channel c : mOpenChannelsList) {
                boolean alreadyFetched = false;
                for (LightningOuterClass.NodeInfo i : mNodeInfos) {
                    if (i.getNode().getPubKey().equals(c.getRemotePubkey())) {
                        alreadyFetched = true;
                        break;
                    }
                }
                if (!alreadyFetched) {
                    channelNodes.add(c.getRemotePubkey());
                }
            }
            for (LightningOuterClass.PendingChannelsResponse.PendingOpenChannel c : mPendingOpenChannelsList) {
                boolean alreadyFetched = false;
                for (LightningOuterClass.NodeInfo i : mNodeInfos) {
                    if (i.getNode().getPubKey().equals(c.getChannel().getRemoteNodePub())) {
                        alreadyFetched = true;
                        break;
                    }
                }
                if (!alreadyFetched) {
                    channelNodes.add(c.getChannel().getRemoteNodePub());
                }
            }
            for (LightningOuterClass.PendingChannelsResponse.ClosedChannel c : mPendingClosedChannelsList) {
                boolean alreadyFetched = false;
                for (LightningOuterClass.NodeInfo i : mNodeInfos) {
                    if (i.getNode().getPubKey().equals(c.getChannel().getRemoteNodePub())) {
                        alreadyFetched = true;
                        break;
                    }
                }
                if (!alreadyFetched) {
                    channelNodes.add(c.getChannel().getRemoteNodePub());
                }
            }
            for (LightningOuterClass.PendingChannelsResponse.ForceClosedChannel c : mPendingForceClosedChannelsList) {
                boolean alreadyFetched = false;
                for (LightningOuterClass.NodeInfo i : mNodeInfos) {
                    if (i.getNode().getPubKey().equals(c.getChannel().getRemoteNodePub())) {
                        alreadyFetched = true;
                        break;
                    }
                }
                if (!alreadyFetched) {
                    channelNodes.add(c.getChannel().getRemoteNodePub());
                }
            }
            for (LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel c : mPendingWaitingCloseChannelsList) {
                boolean alreadyFetched = false;
                for (LightningOuterClass.NodeInfo i : mNodeInfos) {
                    if (i.getNode().getPubKey().equals(c.getChannel().getRemoteNodePub())) {
                        alreadyFetched = true;
                        break;
                    }
                }
                if (!alreadyFetched) {
                    channelNodes.add(c.getChannel().getRemoteNodePub());
                }
            }

            // Delay each NodeInfo request for 100ms to not stress LND
            ArrayList<String> channelNodesList = new ArrayList<>(channelNodes);

            compositeDisposable.add(Observable.range(0, channelNodesList.size())
                    .concatMap(i -> Observable.just(i).delay(100, TimeUnit.MILLISECONDS))
                    .doOnNext(integer -> fetchNodeInfoFromLND(channelNodesList.get(integer), integer == channelNodesList.size() - 1))
                    .subscribe());

            if (channelNodesList.size() == 0) {
                broadcastChannelsUpdated();
            }
        }
    }

    /**
     * This will fetch the NodeInfo according to the supplied pubkey.
     * The NodeInfo will then be added to the mNodeInfos list (no duplicates) which can then
     * be used for non async tasks, such as getting the aliases for channels.
     *
     * @param pubkey
     */
    public void fetchNodeInfoFromLND(String pubkey, boolean lastNode) {
        LightningOuterClass.NodeInfoRequest nodeInfoRequest = LightningOuterClass.NodeInfoRequest.newBuilder()
                .setPubKey(pubkey)
                .build();

        Obdmobile.getNodeInfo(nodeInfoRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------nodeInfoOnError-----------------" + e.getMessage());
                if (lastNode) {
                    broadcastChannelsUpdated();
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.NodeInfo nodeInfo = LightningOuterClass.NodeInfo.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------nodeInfoOnResponse-----------------" + nodeInfo);
                    mNodeInfos.add(nodeInfo);
                    if (lastNode) {
                        saveChannelInfoToCache();
                        broadcastChannelsUpdated();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * Returns the alias of the node based on the provided pubKey.
     * If no alias is found, `Unnamed` is returned.
     *
     * @param pubKey   the pubKey of the node
     * @param mContext context to get translation
     * @return alias
     */
    public String getNodeAliasFromPubKey(String pubKey, Context mContext) {
        String alias = "";
        for (LightningOuterClass.NodeInfo i : Wallet.getInstance().mNodeInfos) {
            if (i.getNode().getPubKey().equals(pubKey)) {
                if (i.getNode().getAlias().startsWith(i.getNode().getPubKey().substring(0, 8)) || i.getNode().getAlias().isEmpty()) {
                    String unnamed = mContext.getResources().getString(R.string.channel_no_alias);
                    alias = unnamed + " (" + i.getNode().getPubKey().substring(0, 5) + "...)";
                } else {
                    alias = i.getNode().getAlias();
                }
                break;
            }
        }

        if (alias.equals("")) {
            return mContext.getResources().getString(R.string.channel_no_alias);
        } else {
            return alias;
        }
    }

    /**
     * Used to save channel infos to the shared preferences.
     * The channel info is stored in a string.
     * StandardCharsets.ISO_8859_1 is used as it preserves the bytes correctly. (UTF8 would not work)
     * The byte length is stored in a 4 byte integer followed by the actual data.
     * This way on reading it can be split at the correct positions.
     */
    public void saveChannelInfoToCache() {
        StringBuilder cache = new StringBuilder();
        for (LightningOuterClass.NodeInfo i : mNodeInfos) {
            byte[] nodeInfoLength = UtilFunctions.intToByteArray(i.toByteArray().length);
            cache.append(new String(nodeInfoLength, StandardCharsets.ISO_8859_1));
            cache.append(new String(i.toByteArray(), StandardCharsets.ISO_8859_1));
        }
        PrefsUtil.editPrefs().putString(PrefsUtil.NODE_INFO_CACHE, cache.toString()).apply();
    }

    /**
     * Create a new wallet address first, and then request the interface of each asset balance list
     * 先创建新的钱包地址后再去请求各资产余额列表的接口
     */
    public void fetchWalletBalance() {
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
                    LightningOuterClass.NewAddressResponse addressResp = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------newAddressOnResponse-----------------" + addressResp.getAddress());
                    LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                            .setAddress(addressResp.getAddress())
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
                                blockData.clear();
                                ListAssetItemEntity entity = new ListAssetItemEntity();
                                entity.setAmount(resp.getTotalBalance());
                                entity.setPropertyid(0);
                                entity.setType(1);
                                blockData.add(entity);
                                allData.addAll(blockData);
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
     * request the interface of each asset balance list
     * 请求各资产余额列表的接口
     */
    public void fetchAssetsBalanceByAddress() {
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress("mtowceAw2yeftR1pPg15QcsDqsnSik7Spz")
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
                    lightningData.clear();
                    for (int i = 0; i < resp.getListList().size(); i++) {
                        ListAssetItemEntity entity = new ListAssetItemEntity();
                        entity.setAmount(resp.getListList().get(i).getBalance());
                        entity.setPropertyid(resp.getListList().get(i).getPropertyid());
                        entity.setType(2);
                        lightningData.add(entity);
                        allData.addAll(lightningData);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Notify all listeners that channels have been updated.
     */
    private void broadcastChannelsUpdated() {
        for (ChannelsUpdatedSubscriptionListener listener : mChannelsUpdatedSubscriptionListeners) {
            listener.onChannelsUpdated();
        }
    }

    public void registerChannelsUpdatedSubscriptionListener(ChannelsUpdatedSubscriptionListener listener) {
        mChannelsUpdatedSubscriptionListeners.add(listener);
    }

    public void unregisterChannelsUpdatedSubscriptionListener(ChannelsUpdatedSubscriptionListener listener) {
        mChannelsUpdatedSubscriptionListeners.remove(listener);
    }

    public interface ChannelsUpdatedSubscriptionListener {
        void onChannelsUpdated();
    }

}




