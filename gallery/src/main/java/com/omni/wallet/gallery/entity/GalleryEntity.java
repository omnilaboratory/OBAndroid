package com.omni.wallet.gallery.entity;

import java.io.Serializable;

/**
 * 照片墙的实体
 */
public class GalleryEntity implements Serializable {


    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    private int type;// 图片类型
    private String fileName;// 文件名
    private String filePath;// 文件路径
    private long videoTime;// 视频时长
    private boolean selected;// 是否被选中


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(long videoTime) {
        this.videoTime = videoTime;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "GalleryEntity{" +
                "type=" + type +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", videoTime=" + videoTime +
                ", selected=" + selected +
                '}';
    }
}
