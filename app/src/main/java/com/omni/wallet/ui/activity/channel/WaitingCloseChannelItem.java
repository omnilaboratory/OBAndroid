package com.omni.wallet.ui.activity.channel;

import com.google.protobuf.ByteString;

import lnrpc.LightningOuterClass;

public class WaitingCloseChannelItem extends ChannelListItem {
    private LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel mChannel;

    public WaitingCloseChannelItem(LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel channel) {
        mChannel = channel;
    }

    @Override
    public int getType() {
        return TYPE_WAITING_CLOSE_CHANNEL;
    }

    @Override
    public ByteString getChannelByteString() {
        return mChannel.toByteString();
    }

    public LightningOuterClass.PendingChannelsResponse.WaitingCloseChannel getChannel() {
        return mChannel;
    }
}
