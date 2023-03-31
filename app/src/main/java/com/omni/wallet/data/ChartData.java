package com.omni.wallet.data;

import java.util.List;

public class ChartData {
    private List<AssetsValueDataItem> chartDataList;
    private ChangeData changeData;

    public ChartData(List<AssetsValueDataItem> chartDataList, ChangeData changeData) {
        this.chartDataList = chartDataList;
        this.changeData = changeData;
    }

    public List<AssetsValueDataItem> getChartDataList() {
        return chartDataList;
    }

    public void setChartDataList(List<AssetsValueDataItem> chartDataList) {
        this.chartDataList = chartDataList;
    }

    public ChangeData getChangeData() {
        return changeData;
    }

    public void setChangeData(ChangeData changeData) {
        this.changeData = changeData;
    }
}
