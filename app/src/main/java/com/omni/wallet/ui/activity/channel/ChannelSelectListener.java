package com.omni.wallet.ui.activity.channel;

import com.google.protobuf.ByteString;

public interface ChannelSelectListener {

    void onChannelSelect(ByteString channel, int type);
}
