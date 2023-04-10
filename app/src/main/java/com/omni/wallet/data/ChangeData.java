package com.omni.wallet.data;

public class ChangeData {
    private double value;
    private double percent;

    ChangeData(double value, double percent) {
        this.value = value;
        this.percent = percent;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
