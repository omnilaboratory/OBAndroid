package com.omni.wallet_mainnet.ui.activity.channel;

import android.view.View;

import com.omni.wallet_mainnet.R;

public class OpenChannelViewHolder extends ChannelViewHolder {

    OpenChannelViewHolder(View v) {
        super(v);
    }

    void setState(boolean isActive) {
        if (isActive) {
            mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_009b19_25);
        } else {
            mStatusDot.setBackgroundResource(R.drawable.bg_btn_round_99000000_25);
        }
    }

    void bindOpenChannelItem(final OpenChannelItem openChannelItem) {
        // Set state
        setState(openChannelItem.getChannel().getActive());

        // Set balances
        if (openChannelItem.getChannel().getAssetId() == 0) {
            long availableCapacity = openChannelItem.getChannel().getBtcCapacity() - openChannelItem.getChannel().getCommitFee();
            setBalances(openChannelItem.getChannel().getLocalBalance(), openChannelItem.getChannel().getRemoteBalance(), availableCapacity);
        } else {
            long availableCapacity = openChannelItem.getChannel().getAssetCapacity() - openChannelItem.getChannel().getCommitFee();
            setBalances(openChannelItem.getChannel().getLocalAssetBalance(), openChannelItem.getChannel().getRemoteAssetBalance(), availableCapacity);
        }

        // Set name
        setName(openChannelItem.getChannel().getRemotePubkey());

        // Set Logo
        setLogo(openChannelItem.getChannel().getAssetId());

        setOnRootViewClickListener(openChannelItem, ChannelListItem.TYPE_OPEN_CHANNEL);
    }
}
