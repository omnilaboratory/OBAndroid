package com.omni.wallet.ui.activity.channel;

import android.annotation.SuppressLint;
import android.view.View;

import com.omni.wallet.R;

public class PendingForceClosingChannelViewHolder extends PendingChannelViewHolder {

    public PendingForceClosingChannelViewHolder(View v) {
        super(v);
    }

    @SuppressLint("ResourceType")
    @Override
    int getStatusColor() {
        return R.drawable.bg_btn_round_ff0000_25;
    }

    void bindPendingForceClosingChannelItem(PendingForceClosingChannelItem pendingForceClosedChannelItem) {
        bindPendingChannelItem(pendingForceClosedChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingForceClosedChannelItem, ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL);
    }
}
