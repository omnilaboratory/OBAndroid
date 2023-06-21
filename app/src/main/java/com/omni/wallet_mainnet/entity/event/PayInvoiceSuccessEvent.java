package com.omni.wallet_mainnet.entity.event;

/**
 * 汉: 支付发票成功的通知实体
 * En: PayInvoiceSuccessEvent
 * author: guoyalei
 * date: 2022/11/27
 */
public class PayInvoiceSuccessEvent {
    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
