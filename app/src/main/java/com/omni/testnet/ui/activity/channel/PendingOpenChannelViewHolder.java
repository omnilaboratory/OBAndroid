package com.omni.testnet.ui.activity.channel;

import android.annotation.SuppressLint;
import android.view.View;

import com.omni.testnet.R;

public class PendingOpenChannelViewHolder extends PendingChannelViewHolder {

    public PendingOpenChannelViewHolder(View v) {
        super(v);
    }

    @SuppressLint("ResourceType")
    @Override
    int getStatusColor() {
        return R.drawable.bg_btn_round_ec9a1e_25;
    }

    void bindPendingOpenChannelItem(PendingOpenChannelItem pendingOpenChannelItem) {
        bindPendingChannelItem(pendingOpenChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingOpenChannelItem, ChannelListItem.TYPE_PENDING_OPEN_CHANNEL);
    }
}
