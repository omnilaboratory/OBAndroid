package com.omni.wallet.ui.activity.channel;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.View;

import lnrpc.LightningOuterClass;


public abstract class PendingChannelViewHolder extends ChannelViewHolder {

    PendingChannelViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract @ColorRes
    int getStatusColor();

    private void setState() {
        mStatusDot.setBackgroundResource(getStatusColor());
    }

    void bindPendingChannelItem(LightningOuterClass.PendingChannelsResponse.PendingChannel pendingChannel) {
        // Set state
        setState();

        // Set balances
        long availableCapacity = pendingChannel.getAssetCapacity();
        setBalances(pendingChannel.getLocalBalance(), pendingChannel.getRemoteBalance(), availableCapacity);

        // Set name
        setName(pendingChannel.getRemoteNodePub());

        // Set logo
        setLogo(pendingChannel.getAssetId());
    }
}
