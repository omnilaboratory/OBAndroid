package com.omni.testnet.ui.activity.channel;

import android.annotation.SuppressLint;
import android.view.View;

import com.omni.testnet.R;

public class WaitingCloseChannelViewHolder extends PendingChannelViewHolder {

    public WaitingCloseChannelViewHolder(View v) {
        super(v);
    }

    @SuppressLint("ResourceType")
    @Override
    int getStatusColor() {
        return R.drawable.bg_btn_round_ff0000_25;
    }

    void bindWaitingCloseChannelItem(WaitingCloseChannelItem pendingWaitingCloseChannelItem) {
        bindPendingChannelItem(pendingWaitingCloseChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingWaitingCloseChannelItem, ChannelListItem.TYPE_WAITING_CLOSE_CHANNEL);
    }
}
