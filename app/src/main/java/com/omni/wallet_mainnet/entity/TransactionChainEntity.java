package com.omni.wallet_mainnet.entity;

import java.util.List;

import lnrpc.LightningOuterClass;

/**
 * 汉: BTC链上交易实体
 * En: TransactionChainEntity
 * author: guoyalei
 * date: 2023/2/14
 */
public class TransactionChainEntity implements Comparable<TransactionChainEntity> {
    private long timeStamp;
    private List<LightningOuterClass.Transaction> list;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<LightningOuterClass.Transaction> getList() {
        return list;
    }

    public void setList(List<LightningOuterClass.Transaction> list) {
        this.list = list;
    }

    @Override
    public int compareTo(TransactionChainEntity o) {
        return (int) (o.getTimeStamp() - this.getTimeStamp());
    }
}