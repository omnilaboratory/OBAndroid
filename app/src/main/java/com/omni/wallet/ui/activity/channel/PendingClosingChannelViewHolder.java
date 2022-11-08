package com.omni.wallet.ui.activity.channel;

import android.support.annotation.NonNull;
import android.view.View;

import com.omni.wallet.R;

public class PendingClosingChannelViewHolder extends PendingChannelViewHolder {

    public PendingClosingChannelViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    int getStatusColor() {
        return R.color.superRed;
    }

    void bindPendingClosingChannelItem(PendingClosingChannelItem pendingClosingChannelItem) {
        bindPendingChannelItem(pendingClosingChannelItem.getChannel().getChannel());

        setOnRootViewClickListener(pendingClosingChannelItem, ChannelListItem.TYPE_PENDING_CLOSING_CHANNEL);
    }

}
