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
import com.omni.wallet.baselibrary.view.recyclerView.swipeMenu.SwipeMenuLayout;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.popupwindow.TransactionsDetailsPopupWindow;
import com.omni.wallet.view.popupwindow.createinvoice.CreateInvoiceStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;
import com.omni.wallet.view.popupwindow.send.SendStepOnePopupWindow;

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
    @BindView(R.id.layout_root_channel_activities)
    RelativeLayout mRootChannelActivitiesLayout;
    @BindView(R.id.layout_channel_activities)
    RelativeLayout mChannelActivitiesLayout;
    @BindView(R.id.recycler_transactions_list)
    RecyclerView mTransactionsListRecyclerView;
    @BindView(R.id.layout_root_to_be_paid)
    RelativeLayout mRootToBePaidLayout;
    @BindView(R.id.layout_to_be_paid)
    RelativeLayout mToBePaidLayout;
    @BindView(R.id.recycler_pending_invoices_list)
    RecyclerView mPendingInvoicesListRecyclerView;
    private List<String> mTransactionsData = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    private List<String> mPendingInvoicesData = new ArrayList<>();
    private PendingInvoicesAdapter mPendingInvoicesAdapter;

    public static final String KEY_NETWORK = "networkKey";
    String network;

    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    CreateInvoiceStepOnePopupWindow mCreateInvoiceStepOnePopupWindow;
    TransactionsDetailsPopupWindow mTransactionsDetailsPopupWindow;

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
        initTransactionsData();
        initPendingInvoicesData();

    }

    /**
     * initialize the list of activity
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
     * initialize the list of to paid list
     * 初始化未支付列表
     */
    private void initPendingInvoicesData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPendingInvoicesListRecyclerView.setLayoutManager(layoutManager);
        mPendingInvoicesAdapter = new PendingInvoicesAdapter(mContext, mPendingInvoicesData, R.layout.layout_item_pending_invoices_list);
        for (int i = 0; i < 10; i++) {
            String str = new String();
            mPendingInvoicesData.add(str);
        }
        mPendingInvoicesListRecyclerView.setAdapter(mPendingInvoicesAdapter);
    }

    /**
     * the adapter of activity list
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
     * the adapter of to be paid list
     * 未支付列表适配器
     */
    private class PendingInvoicesAdapter extends CommonRecyclerAdapter<String> {

        public PendingInvoicesAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {
            final SwipeMenuLayout menuLayout = holder.getView(R.id.layout_pending_invoices_list_swipe_menu);
            // 取消收藏
            holder.getView(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuLayout.quickClose();
                    mPendingInvoicesData.remove(position);
                    mPendingInvoicesAdapter.notifyRemoveItem(position);
                    if (mPendingInvoicesData.size() == 0) {
                        mPendingInvoicesAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * click back button
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
     * click more button
     * 点击More按钮
     */
    @OnClick(R.id.layout_more)
    public void clickMore() {

    }

    /**
     * click scan button
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
     * click channel list button
     * 点击channel List按钮
     */
    @OnClick(R.id.iv_channel_list)
    public void clickChannelList() {
        switchActivity(ChannelsActivity.class);
    }

    /**
     * click pay invoice button
     * 点击Pay invoice按钮
     */
    @OnClick(R.id.layout_pay_invoice)
    public void clickPayInvoice() {
        mPayInvoiceStepOnePopupWindow = new PayInvoiceStepOnePopupWindow(mContext);
        mPayInvoiceStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * click create invoice button
     * 点击Create invoice按钮
     */
    @OnClick(R.id.layout_create_invoice)
    public void clickCreateInvoice() {
        mCreateInvoiceStepOnePopupWindow = new CreateInvoiceStepOnePopupWindow(mContext);
        mCreateInvoiceStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * click send button
     * 点击Send按钮
     */
    @OnClick(R.id.layout_send_invoice)
    public void clickSendInvoice() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
    }

    /**
     * click channel activities text
     * 点击ChannelActivities文本
     */
    @OnClick(R.id.layout_root_channel_activities)
    public void clickChannelActivities() {
//        TranslateAnimation animation = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
//        animation.setDuration(300l);//设置动画的过渡时间
        mRootChannelActivitiesLayout.setVisibility(View.GONE);
//        mRootChannelActivitiesLayout.startAnimation(animation);
        TranslateAnimation animation1 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation1.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.VISIBLE);
        mChannelActivitiesLayout.startAnimation(animation1);
    }

    /**
     * click close button in  channel activities model
     * 点击ChannelActivities Close按钮
     */
    @OnClick(R.id.iv_close_channel_activities)
    public void clickCloseChannelActivitiesIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mChannelActivitiesLayout.setVisibility(View.GONE);
        mChannelActivitiesLayout.startAnimation(animation);
//        TranslateAnimation animation1 = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
//        animation1.setDuration(300l);//设置动画的过渡时间
        mRootChannelActivitiesLayout.setVisibility(View.VISIBLE);
//        mRootChannelActivitiesLayout.startAnimation(animation1);
    }

    /**
     * click to be paid text
     * 点击To be paid文本
     */
    @OnClick(R.id.layout_root_to_be_paid)
    public void clickToBePaid() {
//        TranslateAnimation animation = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
//        animation.setDuration(300l);//设置动画的过渡时间
        mRootChannelActivitiesLayout.setVisibility(View.GONE);
//        mRootChannelActivitiesLayout.startAnimation(animation);
        if (mChannelActivitiesLayout.isShown()) {
            TranslateAnimation animation1 = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            animation1.setDuration(300l);//设置动画的过渡时间
            mChannelActivitiesLayout.setVisibility(View.GONE);
            mChannelActivitiesLayout.startAnimation(animation1);
        }
//        TranslateAnimation animation2 = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
//        animation2.setDuration(300l);//设置动画的过渡时间
        mRootToBePaidLayout.setVisibility(View.GONE);
//        mRootToBePaidLayout.startAnimation(animation2);
        TranslateAnimation animation3 = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        animation3.setDuration(300l);//设置动画的过渡时间
        mToBePaidLayout.setVisibility(View.VISIBLE);
        mToBePaidLayout.startAnimation(animation3);
    }

    /**
     * click close button in to be paid model
     * 点击ToBePaid Close按钮
     */
    @OnClick(R.id.iv_close_to_be_paid)
    public void clickCloseToBePaidIv() {
        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        animation.setDuration(300l);//设置动画的过渡时间
        mToBePaidLayout.setVisibility(View.GONE);
        mToBePaidLayout.startAnimation(animation);
//        TranslateAnimation animation1 = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
//        animation1.setDuration(300l);//设置动画的过渡时间
        mRootChannelActivitiesLayout.setVisibility(View.VISIBLE);
//        mRootChannelActivitiesLayout.startAnimation(animation1);
//        TranslateAnimation animation2 = new TranslateAnimation(
//                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
//                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
//        animation2.setDuration(300l);//设置动画的过渡时间
        mRootToBePaidLayout.setVisibility(View.VISIBLE);
//        mRootToBePaidLayout.startAnimation(animation2);
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
