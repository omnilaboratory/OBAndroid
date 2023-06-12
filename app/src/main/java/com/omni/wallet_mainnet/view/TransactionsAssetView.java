package com.omni.wallet_mainnet.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.utils.DateUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.PreventContinuousClicksUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lnrpc.LightningOuterClass;

/**
 * 汉: Asset交易记录的自定义view
 * En: TransactionsAssetView
 * author: guoyalei
 * date: 2023/2/15
 */
public class TransactionsAssetView extends LinearLayout {
    private static final String TAG = TransactionsAssetView.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.recycler_transactions_list)
    public RecyclerView mRecyclerView;

    private List<LightningOuterClass.AssetTx> mShowData = new ArrayList<>();
    private MyAdapter mAdapter;
    private AssetItemCallback mCallback;

    public TransactionsAssetView(Context context) {
        this(context, null);
    }

    public TransactionsAssetView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionsAssetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_transactions, this);
        ButterKnife.bind(this, rootView);
        // 初始化隐藏
        setVisibility(GONE);
        // RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext, mShowData, R.layout.layout_item_transactions_list);
        mRecyclerView.setAdapter(mAdapter);
        // 解决滑动不流畅
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    public void setViewShow(List<LightningOuterClass.AssetTx> list) {
        if (list.size() == 0 || list == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        // 设置右边数据显示
        Collections.sort(list, new Comparator<LightningOuterClass.AssetTx>() {
            @Override
            public int compare(LightningOuterClass.AssetTx o1, LightningOuterClass.AssetTx o2) {
                return (int) (o2.getBlocktime() - o1.getBlocktime());
            }
        });
        mShowData.clear();
        mShowData.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends CommonRecyclerAdapter<LightningOuterClass.AssetTx> {

        public MyAdapter(Context context, List<LightningOuterClass.AssetTx> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, LightningOuterClass.AssetTx item) {
            holder.setText(R.id.tv_time, DateUtils.Hourmin(item.getBlocktime() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getType().equals("Simple Send")) {
                if (item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    holder.setText(R.id.tv_amount, "- " + df.format(Double.parseDouble(item.getAmount())));
                } else if (!item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(item.getAmount())));
                }
                if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                }
            } else if (item.getType().equals("Send To Many")) {
                if (item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    holder.setText(R.id.tv_amount, "- " + df.format(Double.parseDouble(item.getTotalamount())));
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        holder.setText(R.id.tv_state, "Unnamed");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_state, "Unnamed");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                    }
                } else if (!item.getSendingaddress().equals(User.getInstance().getWalletAddress(mContext))) {
                    if (item.getReceiversList() != null) {
                        if (item.getReceiversList().size() == 1) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                            }
                        } else if (item.getReceiversList().size() == 2) {
                            if (item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & !item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(item.getReceivers(0).getAmount())));
                            } else if (!item.getReceivers(0).getAddress().equals(User.getInstance().getWalletAddress(mContext))
                                    & item.getReceivers(1).getAddress().equals(User.getInstance().getWalletAddress(mContext))) {
                                holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(item.getReceivers(1).getAmount())));
                            }
                        }
                    }
                    if (StringUtils.isEmpty(String.valueOf(item.getConfirmations())) || item.getConfirmations() < 3) {
                        holder.setText(R.id.tv_state, "Unnamed");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                    } else {
                        holder.setText(R.id.tv_state, "Unnamed");
                        holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                    }
                }
            }
            holder.setOnItemClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreventContinuousClicksUtil.isNotFastClick()) {
                        if (mCallback != null) {
                            mCallback.onClickItem(item);
                        }
                    }
                }
            });
        }
    }

    public void setCallback(AssetItemCallback callback) {
        this.mCallback = callback;
    }

    public interface AssetItemCallback {
        void onClickItem(LightningOuterClass.AssetTx item);
    }
}