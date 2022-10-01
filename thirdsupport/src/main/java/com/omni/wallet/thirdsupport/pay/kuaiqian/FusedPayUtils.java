package com.omni.wallet.thirdsupport.pay.kuaiqian;

import android.content.Context;

import com.kuaiqian.fusedpay.entity.FusedPayRequest;
import com.kuaiqian.fusedpay.sdk.FusedPayApiFactory;
import com.kuaiqian.fusedpay.sdk.IFusedPayApi;

/**
 * 快钱支付的工具类
 */

public class FusedPayUtils {
    private static final String TAG = FusedPayUtils.class.getSimpleName();


    public static final String PAY_TYPE_FFAN = "1";// 飞凡通支付
    public static final String PAY_TYPE_ALI_PAY = "2";// 支付宝支付
    public static final String PAY_TYPE_WE_CHAT = "3";// 微信支付

    public static final String RESULT_FFAN_PAY_OK = "100";// 飞凡支付成功
    public static final String RESULT_WECHAT_PAY_OK = "0";// 微信支付成功

    private Context mContext;
    private String mPayType;
    private String mPayInfo;
    private String mMerchantId;
    private String mIcBiz;

    private FusedPayUtils(Builder builder) {
        this.mContext = builder.mContext;
        this.mPayType = builder.mPayType;
        this.mPayInfo = builder.mPayInfo;
        this.mMerchantId = builder.mMerchantId;
        this.mIcBiz = builder.mIcBiz;
    }

    /**
     * 支付
     */
    public void pay() {
        invokeFusedPaySDK(mContext, mPayType, mPayInfo, mMerchantId, mIcBiz);
    }

    /**
     * 调起聚合支付sdk
     *
     * @param context    上下文
     * @param platform   支付平台 :1 --飞凡通支付   2 --支付宝支付     3 --微信支付
     * @param payInfo    移动支付的信息
     * @param merchantId 商户编号
     * @param icBiz      快钱交易编号
     */
    private void invokeFusedPaySDK(Context context, String platform, String payInfo, String merchantId, String icBiz) {
        FusedPayRequest payRequest = new FusedPayRequest();
        payRequest.setPlatform(platform);
        payRequest.setMpayInfo(payInfo);
        payRequest.setMerchantId(merchantId);
        payRequest.setIdBiz(icBiz);

        // CallBackSchemeId可以自定义，自定义的结果页面需实现IKuaiqianEventHandler接口
        payRequest.setCallbackSchemeId("com.ydxf.user.ui.activity.PayResultActivity");

        IFusedPayApi payApi = FusedPayApiFactory.createPayApi(context);
        payApi.pay(payRequest);
    }

    public static class Builder {
        private Context mContext;
        private String mPayType;
        private String mPayInfo;
        private String mMerchantId;
        private String mIcBiz;

        public Builder with(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder payType(String payType) {
            this.mPayType = payType;
            return this;
        }

        public Builder payInfo(String payInfo) {
            this.mPayInfo = payInfo;
            return this;
        }

        public Builder merchantId(String merchantId) {
            this.mMerchantId = merchantId;
            return this;
        }

        public Builder icBiz(String icBiz) {
            this.mIcBiz = icBiz;
            return this;
        }

        public FusedPayUtils build() {
            return new FusedPayUtils(this);
        }
    }

}
