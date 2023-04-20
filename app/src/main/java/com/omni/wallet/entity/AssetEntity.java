package com.omni.wallet.entity;

/**
 * 汉: 资产实体
 * En: AssetEntity
 * author: guoyalei
 * date: 2023/4/19
 */
public class AssetEntity {
    private String assetId;
    private String name;
    private String imgUrl;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}