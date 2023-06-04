package com.omni.wallet_mainnet.baselibrary.http.progress.entity;

import java.math.BigDecimal;

/**
 * 进度的实体类
 */

public class Progress {

    // 进度（0-100）
    private int progress;
    // 读取的长度、内容长度
    private long readLength, contentLength;
    // 是否已经完成
    private boolean isComplete;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setProgress() {
        this.progress = getProgress(readLength, contentLength);
    }

    /**
     * 获取进度
     */
    private int getProgress(long readLength, long contentLength) {
        // 计算当前进度
        float temp = (float) readLength / (float) contentLength * 100;
        return new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
    }
}
