package com.omni.wallet_mainnet.entity;

/**
 * 汉: 版本更新实体
 * En: UpdateEntity
 * author: guoyalei
 * date: 2023/3/22
 */
public class UpdateEntity {
    private String version;
    private String newFeature;
    private String optimization;
    private String url;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNewFeature() {
        return newFeature;
    }

    public void setNewFeature(String newFeature) {
        this.newFeature = newFeature;
    }

    public String getOptimization() {
        return optimization;
    }

    public void setOptimization(String optimization) {
        this.optimization = optimization;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}