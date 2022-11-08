package com.omni.wallet.ui.activity.channel;

import android.view.View;

import com.omni.wallet.R;

public class PendingOpenChannelViewHolder extends PendingChannelViewHolder {

    public PendingOpenChannelViewHolder(View v) {
        super(v);
    }

    @Override
    int getStatusColor() {
        return R.color.lightningOrange;
    }

    void bindPendingOpenChannelItem(PendingOpenChannelItem pendingOpenChannelItem) {
        bindPendingChannelItem(pendingOpenChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingOpenChannelItem, ChannelListItem.TYPE_PENDING_OPEN_CHANNEL);
    }
}
