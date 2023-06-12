package com.omni.wallet.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.PaymentEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 汉: 闪电网络交易记录的自定义view
 * En: TransactionsLightingView
 * author: guoyalei
 * date: 2023/2/15
 */
public class TransactionsLightingView extends LinearLayout {
    private static final String TAG = TransactionsLightingView.class.getSimpleName();
    private Context mContext;

    @BindView(R.id.recycler_transactions_list)
    public RecyclerView mRecyclerView;

    private List<PaymentEntity> mShowData = new ArrayList<>();
    private MyAdapter mAdapter;
    private LightingItemCallback mCallback;

    public TransactionsLightingView(Context context) {
        this(context, null);
    }

    public TransactionsLightingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionsLightingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void setViewShow(List<PaymentEntity> list) {
        if (list.size() == 0 || list == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        // 设置右边数据显示
        Collections.sort(list);
        mShowData.clear();
        mShowData.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends CommonRecyclerAdapter<PaymentEntity> {

        public MyAdapter(Context context, List<PaymentEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, PaymentEntity item) {
            holder.setText(R.id.tv_time, DateUtils.Hourmin(item.getDate() + ""));
            DecimalFormat df = new DecimalFormat("0.00######");
            if (item.getType() == 1) {
                if (item.getAssetId() == 0) {
                    holder.setText(R.id.tv_amount, "- " + df.format(Double.parseDouble(String.valueOf(item.getAmount() / 1000)) / 100000000));
                } else {
                    holder.setText(R.id.tv_amount, "- " + df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                }
                holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                holder.setText(R.id.tv_state, "Unnamed");
            } else if (item.getType() == 2) {
                if (item.getAssetId() == 0) {
                    holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(String.valueOf(item.getAmount() / 1000)) / 100000000));
                } else {
                    holder.setText(R.id.tv_amount, "+ " + df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
                }
                holder.setImageResource(R.id.iv_state, R.mipmap.icon_vector_blue);
                holder.setText(R.id.tv_state, "Unnamed");
            }
            holder.setOnItemClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickItem(item);
                    }
                }
            });
        }
    }

    public void setCallback(LightingItemCallback callback) {
        this.mCallback = callback;
    }

    public interface LightingItemCallback {
        void onClickItem(PaymentEntity item);
    }
}