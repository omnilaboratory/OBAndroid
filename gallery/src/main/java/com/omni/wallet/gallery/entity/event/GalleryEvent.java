package com.omni.wallet.gallery.entity.event;


import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.GalleryFilesEntity;

import java.util.List;

/**
 * 图库图片展示的时候通知实体类
 */

public class GalleryEvent {
    private int type;// 类型
    private List<GalleryEntity> chooseList;// 选择的图片路径集合
    private List<GalleryFilesEntity> filesList;// 图片分类之后的目录的集合
    private String chooseItemFileName;// 选择的条目的文件夹的名字

    // 获取所有图片信息
    public static final int TYPE_ALL_IMAGE_LIST = 99;
    // 获取所有图片目录
    public static final int TYPE_ALL_IMAGE_DIR_LIST = 88;
    // 点击相关目录
    public static final int TYPE_CLICK_IMAGE_DIR = 77;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<GalleryEntity> getChooseList() {
        return chooseList;
    }

    public void setChooseList(List<GalleryEntity> chooseList) {
        this.chooseList = chooseList;
    }

    public List<GalleryFilesEntity> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<GalleryFilesEntity> filesList) {
        this.filesList = filesList;
    }

    public String getChooseItemFileName() {
        return chooseItemFileName;
    }

    public void setChooseItemFileName(String chooseItemFileName) {
        this.chooseItemFileName = chooseItemFileName;
    }
}
