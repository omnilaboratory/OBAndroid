package com.omni.wallet.entity;

import java.util.List;

/**
 * 汉: 闪电网络交易实体
 * En: TransactionLightingEntity
 * author: guoyalei
 * date: 2023/2/15
 */
public class TransactionLightingEntity implements Comparable<TransactionLightingEntity> {
    private long creationDate;
    private List<PaymentEntity> list;

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public List<PaymentEntity> getList() {
        return list;
    }

    public void setList(List<PaymentEntity> list) {
        this.list = list;
    }

    @Override
    public int compareTo(TransactionLightingEntity o) {
        return (int) (o.getCreationDate() - this.getCreationDate());
    }
}