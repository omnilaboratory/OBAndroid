package com.omni.wallet.ui.activity.channel;

import com.google.protobuf.ByteString;

import lnrpc.LightningOuterClass;

public class PendingClosingChannelItem extends ChannelListItem {
    private LightningOuterClass.PendingChannelsResponse.ClosedChannel mChannel;

    public PendingClosingChannelItem(LightningOuterClass.PendingChannelsResponse.ClosedChannel channel) {
        mChannel = channel;
    }

    @Override
    public int getType() {
        return TYPE_PENDING_CLOSING_CHANNEL;
    }

    @Override
    public ByteString getChannelByteString() {
        return mChannel.toByteString();
    }

    public LightningOuterClass.PendingChannelsResponse.ClosedChannel getChannel() {
        return mChannel;
    }
}
