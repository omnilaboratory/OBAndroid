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
import com.omni.wallet_mainnet.utils.PreventContinuousClicksUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lnrpc.LightningOuterClass;

/**
 * 汉: BTC交易记录的自定义view
 * En: TransactionsChainView
 * author: guoyalei
 * date: 2023/2/14
 */
public class TransactionsChainView extends LinearLayout {
    private static final String TAG = TransactionsChainView.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.recycler_transactions_list)
    public RecyclerView mRecyclerView;

    private List<LightningOuterClass.Transaction> mShowData = new ArrayList<>();
    private MyAdapter mAdapter;
    private ChainItemCallback mCallback;

    public TransactionsChainView(Context context) {
        this(context, null);
    }

    public TransactionsChainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionsChainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void setViewShow(List<LightningOuterClass.Transaction> list) {
        if (list.size() == 0 || list == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        // 设置右边数据显示
        mShowData.clear();
        mShowData.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends CommonRecyclerAdapter<LightningOuterClass.Transaction> {

        public MyAdapter(Context context, List<LightningOuterClass.Transaction> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, LightningOuterClass.Transaction item) {
            holder.setText(R.id.tv_time, DateUtils.Hourmin(item.getTimeStamp() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getAmount() < 0) {
                holder.setText(R.id.tv_amount, "- " + df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000).replace("-", ""));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                }
            } else if (item.getAmount() > 0) {
                holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                if (StringUtils.isEmpty(String.valueOf(item.getNumConfirmations())) || item.getNumConfirmations() < 3) {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_alarm_clock_blue);
                } else {
                    holder.setText(R.id.tv_state, "Unnamed");
                    holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
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

    public void setCallback(ChainItemCallback callback) {
        this.mCallback = callback;
    }

    public interface ChainItemCallback {
        void onClickItem(LightningOuterClass.Transaction item);
    }
}