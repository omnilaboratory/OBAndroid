package com.omni.wallet_mainnet.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 汉:
 * En:
 * author: guoyalei
 * date: 2022/11/2
 */
public class ListAssetEntity {
    @SerializedName("list")
    private List<ListAssetItemEntity> list;
}
