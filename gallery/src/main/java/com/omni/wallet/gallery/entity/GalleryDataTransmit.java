package com.omni.wallet.gallery.entity;

/**
 * 数据传递的单例，解决图片选择页面到图片浏览页面数据传递过大导致图片浏览页面无法打开的问题
 */
public class GalleryDataTransmit {
    private static final String TAG = GalleryDataTransmit.class.getSimpleName();

    private static GalleryDataTransmit ourInstance = new GalleryDataTransmit();

    public static GalleryDataTransmit getInstance() {
        return ourInstance;
    }

    private GalleryDataTransmit() {
    }

    // 传递的图片的Json
    private String galleryListJson;

    public String getGalleryListJson() {
        return galleryListJson;
    }

    public void setGalleryListJson(String galleryListJson) {
        this.galleryListJson = galleryListJson;
    }
}
