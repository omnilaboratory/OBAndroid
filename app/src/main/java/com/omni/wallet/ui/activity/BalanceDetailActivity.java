package com.omni.wallet.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;
import com.omni.wallet.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;
import com.omni.wallet.popupwindow.send.SendStepOnePopupWindow;
import com.omni.wallet.utils.CopyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BalanceDetailActivity extends AppBaseActivity {
    private static final String TAG = AccountLightningActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    RelativeLayout mParentLayout;
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.iv_network)
    ImageView mNetworkIv;
    @BindView(R.id.tv_network)
    TextView mNetworkTv;
    @BindView(R.id.layout_network_lightning)
    RelativeLayout mLightningNetworkLayout;
    @BindView(R.id.layout_network_link)
    RelativeLayout mLinkNetworkLayout;
    @BindView(R.id.layout_channel_activities)
    RelativeLayout mChannelActivitiesLayout;
    @BindView(R.id.recycler_transactions_list)
    RecyclerView mTransactionsListRecyclerView;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;

    public static final String KEY_NETWORK = "networkKey";
    String network;

    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow;

    @Override
    protected void getBundleData(Bundle bundle) {
        network = bundle.getString(KEY_NETWORK);
    }

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
        return R.layout.activity_balance_detail;
    }

    @Override
    protected void initView() {
        if (network.equals("lightning")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_vector);
            mNetworkTv.setText("USDT lightning network");
            mLightningNetworkLayout.setVisibility(View.VISIBLE);
            mLinkNetworkLayout.setVisibility(View.GONE);
        } else if (network.equals("link")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_link_black);
            mNetworkTv.setText("Omnilayer Mainnet");
            mLightningNetworkLayout.setVisibility(View.GONE);
            mLinkNetworkLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTransactionsListRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_transactions_list);
        for (int i = 0; i < 10; i++) {
            String str = new String();
            mData.add(str);
        }
        mTransactionsListRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 交易列表适配器
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
     * 点击Back按钮
     */
    @OnClick(R.id.layout_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击copy图标按钮
     * click copy icon btn
     *
     * @description 中文：点击按钮复制当前address到粘贴板
     * EN：Click copy button,duplicate address to clipboard
     * @author Tong ChangHui
     * @Email tch081092@gmail.com
     */
    @OnClick(R.id.iv_copy)
    public void copyAddress() {
        //接收需要复制到粘贴板的地址
        //Get the address which will copy to clipboard
        String toCopyAddress = "01234e*****bg453123";
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_copy_address);
        CopyUtil.SelfCopy(BalanceDetailActivity.this, toCopyAddress, toastString);
    }

    /**
     * 点击More按钮
     */
    @OnClick(R.id.layout_more)
    public void clickMore() {

    }

    /**
     * 点击扫描按钮
     */
    @OnClick(R.id.iv_scan)
    public void clickScan() {
        PermissionUtils.launchCamera(this, new PermissionUtils.PermissionCallback() {
            @Override
            public void onRequestPermissionSuccess() {
                switchActivity(ScanActivity.class);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
            }
        });
    }

    /**
     * 点击channel List按钮
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        switchActivity(ChannelsActivity.class);
    }

    /**
     * 点击Pay invoice按钮
     */
    @OnClick(R.id.layout_pay_invoice)
    public void clickPayInvoice() {
        mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
        mPayInvoiceStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * 点击Create invoice按钮
     */
    @OnClick(R.id.layout_create_invoice)
    public void clickCreateInvoice() {
        mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
        mCreateInvoiceStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * 点击Send按钮
     */
    @OnClick(R.id.layout_send_invoice)
    public void clickSendInvoice() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * 点击ChannelActivities文本
     */
    @OnClick(R.id.layout_root_channel_activities)
    public void clickChannelActivities() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.VISIBLE);
        mChannelActivitiesLayout.startAnimation(animation);
    }

    /**
     * 点击Close按钮
     */
    @OnClick(R.id.iv_close)
    public void clickCloseIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.GONE);
        mChannelActivitiesLayout.startAnimation(animation);
    }

    /**
     * 点击To be paid文本
     */
    @OnClick(R.id.layout_to_be_paid)
    public void clickToBePaid() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPayInvoiceStepOnePopupWindow != null) {
            mPayInvoiceStepOnePopupWindow.release();
        }
        if (mSendStepOnePopupWindow != null) {
            mSendStepOnePopupWindow.release();
        }
    }
}
