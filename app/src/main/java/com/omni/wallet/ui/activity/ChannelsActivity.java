package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.popupwindow.CreateChannelStepOnePopupWindow;
import com.omni.wallet.utils.CopyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChannelsActivity extends AppBaseActivity {
    private static final String TAG = ChannelsActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    LinearLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.recycler_channels_list)
    public RecyclerView mRecyclerView;// 通道列表的RecyclerView
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;

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

    @OnClick(R.id.iv_create_channel)
    public void clickcCreateChannel() {
        mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
        mCreateChannelStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * 点击copy图标按钮
     * click copy icon btn
     * @description
     * 中文：点击按钮复制当前address到粘贴板
     * EN：Click copy button,duplicate address to clipboard
     * @author Tong ChangHui
     * @Email tch081092@gmail.com
     */
    @OnClick(R.id.iv_copy)
    public void copyAddress(){
        //接收需要复制到粘贴板的地址
        //Get the address which will copy to clipboard
        String toCopyAddress = "01234e*****bg453123";
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(ChannelsActivity.this,toCopyAddress,toastString);
    }
}
