package com.omni.wallet_mainnet.entity.event;

/**
 * 汉: 选择钱包地址的实体
 * En: SelectAccountEvent
 * author: guoyalei
 * date: 2022/11/11
 */
public class SelectAccountEvent {
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
