package com.omni.wallet_mainnet.data;

public class AssetsDataItem {
    private String property_id;
    private double price;
    private double amount;
    private double channel_amount;
    private long update_date;

    AssetsDataItem(String property_id, double price, double amount, double channel_amount, long update_date) {
        this.property_id = property_id;
        this.price = price;
        this.amount = amount;
        this.channel_amount = channel_amount;
        this.update_date = update_date;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    double getChannel_amount() {
        return channel_amount;
    }

    public void setChannel_amount(double channel_amount) {
        this.channel_amount = channel_amount;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }
}
