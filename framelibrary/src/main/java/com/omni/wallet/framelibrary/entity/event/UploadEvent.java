package com.omni.wallet.framelibrary.entity.event;

/**
 * 上传图片的事件实体
 */

public class UploadEvent {
    private String fileName;// 文件名
    private long totalSize;// 总大小
    private long currentSize;// 当前上传量
    private int progress;// 进度
    private int eventType;// 通知类型
    private String url;// 图片的Url
    private String errorCode;// 错误码
    private String errorMessage;// 错误信息

    public static final int TYPE_EVENT_SUCCESS = 0x00011;
    public static final int TYPE_EVENT_FAIL = 0x00022;
    public static final int TYPE_EVENT_PROGRESS = 0x00033;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
