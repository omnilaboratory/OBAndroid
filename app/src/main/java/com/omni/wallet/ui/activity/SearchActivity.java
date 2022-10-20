package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.view.popupwindow.TransactionsDetailsPopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchActivity extends AppBaseActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.tv_channels)
    TextView mChannelsTv;
    @BindView(R.id.tv_activities)
    TextView mActivitiesTv;
    @BindView(R.id.layout_channels)
    LinearLayout mChannelsLayout;
    @BindView(R.id.layout_activities)
    LinearLayout mActivitiesLayout;
    @BindView(R.id.recycler_channels_list)
    RecyclerView mChannelsListRecyclerView;
    @BindView(R.id.recycler_transactions_list)
    RecyclerView mTransactionsListRecyclerView;

    private List<String> mChannelsData = new ArrayList<>();
    private ChannelsAdapter mChannelsAdapter;
    private List<String> mTransactionsData = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    TransactionsDetailsPopupWindow mTransactionsDetailsPopupWindow;

    @Override
    protected View getStatusBarTopView() {
        return mTopView;
    }

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        initChannelsData();
        initTransactionsData();
    }

    /**
     * 初始化通道交易列表
     */
    private void initChannelsData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChannelsListRecyclerView.setLayoutManager(layoutManager);
        mChannelsAdapter = new ChannelsAdapter(mContext, mChannelsData, R.layout.layout_item_search_channels_list);
        for (int i = 0; i < 10; i++) {
            String str = new String();
            mChannelsData.add(str);
        }
        mChannelsListRecyclerView.setAdapter(mChannelsAdapter);
    }

    /**
     * 初始化交易列表
     */
    private void initTransactionsData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTransactionsListRecyclerView.setLayoutManager(layoutManager);
        mTransactionsAdapter = new TransactionsAdapter(mContext, mTransactionsData, R.layout.layout_item_transactions_list);
        for (int i = 0; i < 10; i++) {
            String str = new String();
            mTransactionsData.add(str);
        }
        mTransactionsListRecyclerView.setAdapter(mTransactionsAdapter);
    }

    /**
     * 通道交易列表适配器
     */
    private class ChannelsAdapter extends CommonRecyclerAdapter<String> {

        public ChannelsAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {

        }
    }

    /**
     * 交易列表适配器
     */
    private class TransactionsAdapter extends CommonRecyclerAdapter<String> {

        public TransactionsAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTransactionsDetailsPopupWindow = new TransactionsDetailsPopupWindow(mContext);
                    mTransactionsDetailsPopupWindow.show(mParentLayout);
                }
            });
        }
    }

    /**
     * Click Back
     */
    @OnClick(R.id.layout_back)
    public void clickBack() {
        finish();
    }

    /**
     * Click CHANNELS
     */
    @OnClick(R.id.tv_channels)
    public void clickChannelsTv() {
        mChannelsTv.setBackgroundResource(R.drawable.bg_4a92ff_rectangle_round_5);
        mChannelsTv.setTextColor(Color.parseColor("#FFFFFF"));
        mActivitiesTv.setBackground(null);
        mActivitiesTv.setTextColor(Color.parseColor("#4A92FF"));
        mChannelsLayout.setVisibility(View.VISIBLE);
        mActivitiesLayout.setVisibility(View.GONE);
    }

    /**
     * Click ACTIVITIES
     */
    @OnClick(R.id.tv_activities)
    public void clickActivitiesTv() {
        mActivitiesTv.setBackgroundResource(R.drawable.bg_4a92ff_rectangle_round_5);
        mActivitiesTv.setTextColor(Color.parseColor("#FFFFFF"));
        mChannelsTv.setBackground(null);
        mChannelsTv.setTextColor(Color.parseColor("#4A92FF"));
        mChannelsLayout.setVisibility(View.GONE);
        mActivitiesLayout.setVisibility(View.VISIBLE);
    }
}
