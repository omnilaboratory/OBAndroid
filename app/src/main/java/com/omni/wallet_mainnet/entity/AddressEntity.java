package com.omni.wallet_mainnet.entity;

/**
 * 汉: 地址列表的实体
 * En: AddressEntity
 * author: guoyalei
 * date: 2022/12/15
 */
public class AddressEntity {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
