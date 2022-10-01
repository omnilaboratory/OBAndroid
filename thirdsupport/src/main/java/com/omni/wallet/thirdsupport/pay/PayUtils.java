package com.omni.wallet.thirdsupport.pay;

import android.app.Activity;
import android.content.Context;

import com.kuaiqian.fusedpay.sdk.FusedPayApiFactory;
import com.kuaiqian.fusedpay.sdk.IFusedPayApi;
import com.kuaiqian.fusedpay.sdk.IFusedPayEventHandler;
import com.omni.wallet.thirdsupport.pay.kuaiqian.FusedPayUtils;

/**
 * 支付工具类
 */

public class PayUtils {

    public void pusedALiPay(Context context, String payInfo, String merchantId, String icBiz) {
        // 调用快钱阿里支付
        pusedPay(context, FusedPayUtils.PAY_TYPE_ALI_PAY, payInfo, merchantId, icBiz);
    }

    public static void onNewIntent(Activity activity, IFusedPayEventHandler handler) {
        IFusedPayApi api = FusedPayApiFactory.createPayApi(activity);
        api.handleIntent(activity.getIntent(), handler);
    }

    public void pusedWeChatPay(Context context, String payInfo, String merchantId, String icBiz) {
        // 调用快钱阿里支付
        pusedPay(context, FusedPayUtils.PAY_TYPE_WE_CHAT, payInfo, merchantId, icBiz);
}

    private void pusedPay(Context context, String payType, String payInfo, String merchantId, String icBiz) {
        // 调用快钱阿里支付
        new FusedPayUtils.Builder()
                .with(context)
                .payType(payType)
                .payInfo(payInfo)
                .merchantId(merchantId)
                .icBiz(icBiz)
                .build()
                .pay();
    }
}
