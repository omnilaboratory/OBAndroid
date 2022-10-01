package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChannelsActivity extends AppBaseActivity {
    private static final String TAG = ChannelsActivity.class.getSimpleName();

    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.recycler_channels_list)
    public RecyclerView mRecyclerView;// 通道列表的RecyclerView
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;

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
        return R.layout.activity_channels;
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        for (int i = 0; i < 5; i++) {
            String str = new String();
            mData.add(str);
        }
        mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_channels_list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

    }

    /**
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<String> {

        public MyAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {

        }
    }

    /**
     * 点击右上角关闭按钮
     */
    @OnClick(R.id.iv_close)
    public void clickClose() {
        finish();
    }

    @OnClick(R.id.iv_fund)
    public void clickFund() {

    }

    /**
     * 点击钱包按钮
     */
    @OnClick(R.id.iv_wallet)
    public void clickWallet() {

    }
}
