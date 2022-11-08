package com.omni.wallet.ui.activity.channel;

import android.view.View;

import com.omni.wallet.R;

public class PendingForceClosingChannelViewHolder extends PendingChannelViewHolder {

    public PendingForceClosingChannelViewHolder(View v) {
        super(v);
    }

    @Override
    int getStatusColor() {
        return R.color.superRed;
    }

    void bindPendingForceClosingChannelItem(PendingForceClosingChannelItem pendingForceClosedChannelItem) {
        bindPendingChannelItem(pendingForceClosedChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingForceClosedChannelItem, ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL);
    }
}
