package com.omni.wallet.entity.event;

/**
 * 汉: 扫码成功的通知实体
 * En: ScanResultEvent
 * author: guoyalei
 * date: 2022/11/30
 */
public class ScanResultEvent {
    private int code;
    private String type;
    private String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
