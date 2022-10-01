package com.omni.wallet.thirdsupport.umeng.share.bean;

/**
 * 分享的实体基类
 */

public abstract class BaseShareBean {
    // 分享的内容的链接
    private String mUrl;
    // 标题
    private String mTitle;
    // 描述信息
    private String mDesc;
    // 缩略图
    private ImageShareBean mThumb;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public ImageShareBean getThumb() {
        return mThumb;
    }

    public void setThumb(ImageShareBean mThumb) {
        this.mThumb = mThumb;
    }
}
