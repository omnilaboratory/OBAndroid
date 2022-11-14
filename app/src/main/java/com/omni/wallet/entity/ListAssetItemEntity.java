package com.omni.wallet.entity;

import com.omni.wallet.framelibrary.entity.BaseListEntity;

/**
 * 汉: 各资产余额列表单个资产实体类
 * En: ListAssetItemEntity
 * author: guoyalei
 * date: 2022/11/2
 */
public class ListAssetItemEntity extends BaseListEntity {
    private long amount;
    private long propertyid;
    private int type;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPropertyid() {
        return propertyid;
    }

    public void setPropertyid(long propertyid) {
        this.propertyid = propertyid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
