package com.omni.wallet.entity.event;

/**
 * 汉: 下载文件的通知实体
 * En: DownloadEvent
 * author: guoyalei
 * date: 2023/5/10
 */
public class DownloadEvent {
    private double total;
    private double current;
    private String fileName;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}