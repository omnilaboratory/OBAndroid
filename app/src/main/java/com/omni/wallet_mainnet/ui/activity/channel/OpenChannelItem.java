package com.omni.wallet_mainnet.ui.activity.channel;

import com.google.protobuf.ByteString;

import lnrpc.LightningOuterClass;

public class OpenChannelItem extends ChannelListItem {
    private LightningOuterClass.Channel mChannel;

    public OpenChannelItem(LightningOuterClass.Channel channel) {
        mChannel = channel;
    }

    @Override
    public int getType() {
        return TYPE_OPEN_CHANNEL;
    }

    @Override
    public ByteString getChannelByteString() {
        return mChannel.toByteString();
    }

    public LightningOuterClass.Channel getChannel() {
        return mChannel;
    }
}
