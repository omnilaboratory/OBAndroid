package com.omni.wallet.entity;

import com.google.gson.annotations.SerializedName;
import com.omni.wallet.framelibrary.entity.BaseListEntity;

/**
 * 汉:
 * En:
 * author: guoyalei
 * date: 2022/11/2
 */
public class ListAssetItemEntity extends BaseListEntity {
    @SerializedName("category")
    private String category;
    @SerializedName("creationtxid")
    private String creationtxid;
    @SerializedName("data")
    private String data;
    @SerializedName("divisible")
    private boolean divisible;
    @SerializedName("issuer")
    private String issuer;
    @SerializedName("name")
    private String name;
    @SerializedName("propertyid")
    private String propertyid;
    @SerializedName("subcategory")
    private String subcategory;
    @SerializedName("url")
    private String url;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreationtxid() {
        return creationtxid;
    }

    public void setCreationtxid(String creationtxid) {
        this.creationtxid = creationtxid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isDivisible() {
        return divisible;
    }

    public void setDivisible(boolean divisible) {
        this.divisible = divisible;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(String propertyid) {
        this.propertyid = propertyid;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
