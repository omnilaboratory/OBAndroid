package com.omni.testnet.ui.activity.channel;

import com.google.protobuf.ByteString;

import lnrpc.LightningOuterClass;

public class PendingForceClosingChannelItem extends ChannelListItem {
    private LightningOuterClass.PendingChannelsResponse.ForceClosedChannel mChannel;

    public PendingForceClosingChannelItem(LightningOuterClass.PendingChannelsResponse.ForceClosedChannel channel) {
        mChannel = channel;
    }

    @Override
    public int getType() {
        return TYPE_PENDING_FORCE_CLOSING_CHANNEL;
    }

    @Override
    public ByteString getChannelByteString() {
        return mChannel.toByteString();
    }

    public LightningOuterClass.PendingChannelsResponse.ForceClosedChannel getChannel() {
        return mChannel;
    }

}
