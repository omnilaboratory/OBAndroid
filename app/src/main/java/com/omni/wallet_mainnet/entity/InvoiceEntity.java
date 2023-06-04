package com.omni.wallet_mainnet.entity;

/**
 * 汉: 待支付发票的实体
 * En: InvoiceEntity
 * author: guoyalei
 * date: 2023/1/5
 */
public class InvoiceEntity {
    private long assetId;
    private long date;
    private long amount;
    private String invoice;
    private long expiry;

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

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }
}
