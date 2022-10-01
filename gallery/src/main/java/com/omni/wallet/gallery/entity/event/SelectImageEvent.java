package com.omni.wallet.gallery.entity.event;


import com.omni.wallet.gallery.entity.GalleryEntity;

import java.util.List;

/**
 * 图库和拍照获取图片之后的通知实体类
 */

public class SelectImageEvent {
    // 选择的图片的集合
    private List<GalleryEntity> selectImageList;

    public List<GalleryEntity> getSelectImageList() {
        return selectImageList;
    }

    public void setSelectImageList(List<GalleryEntity> selectImageList) {
        this.selectImageList = selectImageList;
    }
}
