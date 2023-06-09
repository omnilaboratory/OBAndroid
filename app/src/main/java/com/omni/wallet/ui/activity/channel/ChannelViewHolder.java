package com.omni.wallet.ui.activity.channel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.entity.AssetEntity;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.OnSingleClickListener;
import com.omni.wallet.utils.Wallet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    Context mContext;
    LinearLayout mRootView;
    ImageView mStatusDot;
    private TextView mRemoteName;
    private ImageView mAssetLogo;
    private TextView mAssetUnit;
    private TextView mRemotePubkey;
    private TextView mCreateTime;
    private TextView mLocalBalance;
    private TextView mRemoteBalance;
    private ProgressBar mProgressBar;
    private ChannelSelectListener mChannelSelectListener;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    ChannelViewHolder(@NonNull View itemView) {
        super(itemView);

        mRootView = itemView.findViewById(R.id.channelRootView);
        mStatusDot = itemView.findViewById(R.id.iv_channel_state);
        mRemoteName = itemView.findViewById(R.id.tv_node_name);
        mAssetLogo = itemView.findViewById(R.id.im_token_type);
        mAssetUnit = itemView.findViewById(R.id.tv_token_type);
        mRemotePubkey = itemView.findViewById(R.id.tv_pubkey_value);
        mCreateTime = itemView.findViewById(R.id.tv_create_time);
        mLocalBalance = itemView.findViewById(R.id.tv_local_amount);
        mRemoteBalance = itemView.findViewById(R.id.tv_remote_amount);
        mProgressBar = itemView.findViewById(R.id.pv_amount_percent);
        mContext = itemView.getContext();
    }

    public void setName(String channelRemotePubKey) {
        mRemoteName.setText(Wallet.getInstance().getNodeAliasFromPubKey(channelRemotePubKey, mContext));
        mRemotePubkey.setText(channelRemotePubKey);
    }

    public void setLogo(int assetId) {
        long mAssetId = assetId & 0xffffffffL;
        mAssetData.clear();
        Gson gson = new Gson();
        mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
        }.getType());
        for (AssetEntity entity : mAssetData) {
            if (Long.parseLong(entity.getAssetId()) == mAssetId) {
                ImageUtils.showImage(mContext, entity.getImgUrl(), mAssetLogo);
                mAssetUnit.setText(entity.getName());
            }
        }
    }

    public void setTime(long time) {
        mCreateTime.setText(DateUtils.dateFormat(time, DateUtils.YYYY_MM_DD_HH_MM_SS));
    }

    void setBalances(long local, long remote, long capacity) {
        float localBarValue = (float) ((double) local / (double) capacity);

        mProgressBar.setProgress((int) (localBarValue * 100f));

        DecimalFormat df = new DecimalFormat("0.00######");
        mLocalBalance.setText(df.format(Double.parseDouble(String.valueOf(local)) / 100000000));
        mRemoteBalance.setText(df.format(Double.parseDouble(String.valueOf(remote)) / 100000000));
    }

    void addOnChannelSelectListener(ChannelSelectListener channelSelectListener) {
        mChannelSelectListener = channelSelectListener;
    }

    void setOnRootViewClickListener(@NonNull ChannelListItem item, int type) {
        mRootView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mChannelSelectListener != null) {
                    mChannelSelectListener.onChannelSelect(item.getChannelByteString(), type);
                }
            }
        });
    }
}
