package com.omni.wallet.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.popupwindow.payinvoice.PayInvoiceStepOnePopupWindow;
import com.omni.wallet.popupwindow.send.SendStepOnePopupWindow;

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
    RelativeLayout mLinkgNetworkLayout;

    public static final String KEY_NETWORK = "networkKey";
    String network;

    PayInvoiceStepOnePopupWindow mPayInvoiceStepOnePopupWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;

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
            mLinkgNetworkLayout.setVisibility(View.GONE);
        } else if (network.equals("link")) {
            mNetworkIv.setImageResource(R.mipmap.icon_network_link_black);
            mNetworkTv.setText("Omnilayer Mainnet");
            mLightningNetworkLayout.setVisibility(View.GONE);
            mLinkgNetworkLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

    }

    /**
     * 点击Back按钮
     */
    @OnClick(R.id.layout_back)
    public void clickBack() {
        finish();
    }


    /**
     * 点击More按钮
     */
    @OnClick(R.id.layout_more)
    public void clickMore() {

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

    }

    /**
     * 点击Send按钮
     */
    @OnClick(R.id.layout_send_invoice)
    public void clickSendInvoice() {
        mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
        mSendStepOnePopupWindow.show(mParentLayout);
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
