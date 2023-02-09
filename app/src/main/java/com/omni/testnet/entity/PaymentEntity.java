package com.omni.testnet.entity;

/**
 * 汉: 支付发票成功的实体
 * En: PaymentEntity
 * author: guoyalei
 * date: 2023/1/13
 */
public class PaymentEntity {
    private long assetId;
    private long date;
    private long amount;
    private int type;

    public long getAssetId() {
        return assetId;
    }

    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
