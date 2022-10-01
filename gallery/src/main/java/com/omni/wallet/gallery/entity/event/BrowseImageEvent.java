package com.omni.wallet.gallery.entity.event;


import com.omni.wallet.gallery.entity.GalleryEntity;

import java.util.List;

/**
 * 图片浏览的时候发送通知的Event
 */

public class BrowseImageEvent {

    private int requestCode;// 区分调用方的请求码
    private int pageType;// 图库浏览的类型（主要用来区分资料上传详情页和图库两种）
    private int dataType;// 图片集合对应的分类
    private List<GalleryEntity> imageList;// 所有图片集合
    private List<GalleryEntity> selectImageList;// 选择的图片集合

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public List<GalleryEntity> getImageList() {
        return imageList;
    }

    public void setImageList(List<GalleryEntity> imageList) {
        this.imageList = imageList;
    }

    public List<GalleryEntity> getSelectImageList() {
        return selectImageList;
    }

    public void setSelectImageList(List<GalleryEntity> selectImageList) {
        this.selectImageList = selectImageList;
    }
}
