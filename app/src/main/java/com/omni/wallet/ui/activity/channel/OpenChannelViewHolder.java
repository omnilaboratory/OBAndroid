package com.omni.wallet.ui.activity.channel;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.omni.wallet.R;

public class OpenChannelViewHolder extends ChannelViewHolder {

    OpenChannelViewHolder(View v) {
        super(v);
    }

    void setState(boolean isActive) {
        if (isActive) {
            mStatusDot.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.superGreen)));
        } else {
            mStatusDot.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.gray)));
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

        setOnRootViewClickListener(openChannelItem, ChannelListItem.TYPE_OPEN_CHANNEL);
    }
}
