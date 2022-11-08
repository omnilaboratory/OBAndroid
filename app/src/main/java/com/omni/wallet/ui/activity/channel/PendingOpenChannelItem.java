package com.omni.wallet.ui.activity.channel;

import com.google.protobuf.ByteString;

import lnrpc.LightningOuterClass;

public class PendingOpenChannelItem extends ChannelListItem {
    private LightningOuterClass.PendingChannelsResponse.PendingOpenChannel mChannel;

    public PendingOpenChannelItem(LightningOuterClass.PendingChannelsResponse.PendingOpenChannel channel) {
        mChannel = channel;
    }

    @Override
    public int getType() {
        return TYPE_PENDING_OPEN_CHANNEL;
    }

    @Override
    public ByteString getChannelByteString() {
        return mChannel.toByteString();
    }

    public LightningOuterClass.PendingChannelsResponse.PendingOpenChannel getChannel() {
        return mChannel;
    }
}
