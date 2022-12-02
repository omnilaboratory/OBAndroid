package com.omni.wallet.ui.activity.channel;

import android.view.View;

import com.omni.wallet.R;

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
        long availableCapacity = openChannelItem.getChannel().getAssetCapacity() - openChannelItem.getChannel().getCommitFee();
        setBalances(openChannelItem.getChannel().getLocalAssetBalance(), openChannelItem.getChannel().getRemoteAssetBalance(), availableCapacity);

        // Set name
        setName(openChannelItem.getChannel().getRemotePubkey());

        // Set Logo
        setLogo(openChannelItem.getChannel().getAssetId());

        setOnRootViewClickListener(openChannelItem, ChannelListItem.TYPE_OPEN_CHANNEL);
    }
}
