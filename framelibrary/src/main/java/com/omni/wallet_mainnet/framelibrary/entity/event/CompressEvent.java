package com.omni.wallet_mainnet.framelibrary.entity.event;

/**
 * 压缩图片事件实体
 */

public class CompressEvent {
    private boolean result;
    private String imagePath;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
