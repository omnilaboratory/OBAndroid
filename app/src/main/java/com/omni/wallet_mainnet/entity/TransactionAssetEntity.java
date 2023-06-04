package com.omni.wallet_mainnet.entity;

import java.util.List;

import lnrpc.LightningOuterClass;

/**
 * 汉: Asset链上交易实体
 * En: TransactionAssetEntity
 * author: guoyalei
 * date: 2023/2/15
 */
public class TransactionAssetEntity implements Comparable<TransactionAssetEntity> {
    private long blockTime;
    private List<LightningOuterClass.AssetTx> list;

    public long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(long blockTime) {
        this.blockTime = blockTime;
    }

    public List<LightningOuterClass.AssetTx> getList() {
        return list;
    }

    public void setList(List<LightningOuterClass.AssetTx> list) {
        this.list = list;
    }

    @Override
    public int compareTo(TransactionAssetEntity o) {
        return (int) (o.getBlockTime() - this.getBlockTime());
    }
}