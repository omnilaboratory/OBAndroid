package com.omni.wallet_mainnet.ui.activity.channel;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet_mainnet.R;

import java.util.List;


public class ChannelItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SortedList<ChannelListItem> mSortedList = new SortedList<>(ChannelListItem.class, new SortedList.Callback<ChannelListItem>() {
        @Override
        public int compare(ChannelListItem i1, ChannelListItem i2) {
            return i1.compareTo(i2);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(ChannelListItem oldItem, ChannelListItem newItem) {
            return oldItem.equalsWithSameContent(newItem);
        }

        @Override
        public boolean areItemsTheSame(ChannelListItem item1, ChannelListItem item2) {
            return item1.equals(item2);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });


    private ChannelSelectListener mChannelSelectListener;

    // Construct the adapter with a data list
    public ChannelItemAdapter(ChannelSelectListener channelSelectListener) {
        mChannelSelectListener = channelSelectListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mSortedList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View channelView = inflater.inflate(R.layout.layout_item_channels_list, parent, false);

        switch (viewType) {
            case ChannelListItem.TYPE_OPEN_CHANNEL:
                return new OpenChannelViewHolder(channelView);
            case ChannelListItem.TYPE_PENDING_OPEN_CHANNEL:
                return new PendingOpenChannelViewHolder(channelView);
            case ChannelListItem.TYPE_PENDING_CLOSING_CHANNEL:
                return new PendingClosingChannelViewHolder(channelView);
            case ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL:
                return new PendingForceClosingChannelViewHolder(channelView);
            case ChannelListItem.TYPE_WAITING_CLOSE_CHANNEL:
                return new WaitingCloseChannelViewHolder(channelView);
            default:
                throw new IllegalStateException("Unknown channel type: " + viewType);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case ChannelListItem.TYPE_OPEN_CHANNEL:
                OpenChannelViewHolder openChannelHolder = (OpenChannelViewHolder) holder;
                OpenChannelItem openChannelItem = (OpenChannelItem) mSortedList.get(position);
                openChannelHolder.bindOpenChannelItem(openChannelItem);
                openChannelHolder.addOnChannelSelectListener(mChannelSelectListener);
                break;
            case ChannelListItem.TYPE_PENDING_OPEN_CHANNEL:
                PendingOpenChannelViewHolder pendingOpenChannelHolder = (PendingOpenChannelViewHolder) holder;
                PendingOpenChannelItem pendingOpenChannelItem = (PendingOpenChannelItem) mSortedList.get(position);
                pendingOpenChannelHolder.bindPendingOpenChannelItem(pendingOpenChannelItem);
                pendingOpenChannelHolder.addOnChannelSelectListener(mChannelSelectListener);
                break;
            case ChannelListItem.TYPE_PENDING_CLOSING_CHANNEL:
                PendingClosingChannelViewHolder pendingClosingChannelHolder = (PendingClosingChannelViewHolder) holder;
                PendingClosingChannelItem pendingClosingChannelItem = (PendingClosingChannelItem) mSortedList.get(position);
                pendingClosingChannelHolder.bindPendingClosingChannelItem(pendingClosingChannelItem);
                pendingClosingChannelHolder.addOnChannelSelectListener(mChannelSelectListener);
                break;
            case ChannelListItem.TYPE_PENDING_FORCE_CLOSING_CHANNEL:
                PendingForceClosingChannelViewHolder pendingForceClosingChannelHolder = (PendingForceClosingChannelViewHolder) holder;
                PendingForceClosingChannelItem pendingForceClosingChannelItem = (PendingForceClosingChannelItem) mSortedList.get(position);
                pendingForceClosingChannelHolder.bindPendingForceClosingChannelItem(pendingForceClosingChannelItem);
                pendingForceClosingChannelHolder.addOnChannelSelectListener(mChannelSelectListener);
                break;
            case ChannelListItem.TYPE_WAITING_CLOSE_CHANNEL:
                WaitingCloseChannelViewHolder waitingCloseChannelHolder = (WaitingCloseChannelViewHolder) holder;
                WaitingCloseChannelItem waitingCloseChannelItem = (WaitingCloseChannelItem) mSortedList.get(position);
                waitingCloseChannelHolder.bindWaitingCloseChannelItem(waitingCloseChannelItem);
                waitingCloseChannelHolder.addOnChannelSelectListener(mChannelSelectListener);
                break;
            default:
                throw new IllegalStateException("Unknown channel type: " + type);
        }
    }

    public void replaceAll(List<ChannelListItem> items) {
        mSortedList.replaceAll(items);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSortedList.size();
    }
}
