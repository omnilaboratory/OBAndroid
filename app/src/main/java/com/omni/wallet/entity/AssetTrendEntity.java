package com.omni.wallet.entity;

/**
 * 汉: 资产走势图实体
 * En: AssetTrendEntity
 * author: guoyalei
 * date: 2023/1/12
 */
public class AssetTrendEntity {
    private String time;
    private String asset;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }
}
