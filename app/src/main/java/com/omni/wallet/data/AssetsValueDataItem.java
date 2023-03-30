package com.omni.wallet.data;

public class AssetsValueDataItem {
    private double value;
    private long update_date;

    public AssetsValueDataItem(double value, long update_date) {
        this.value = value;
        this.update_date = update_date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }
}
