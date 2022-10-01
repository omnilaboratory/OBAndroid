package com.omni.wallet.thirdsupport.umeng.share.bean;

/**
 * 微信小程序分享的实体类
 */

public class WeChatAppletShareBean extends BaseShareBean {
    // 微信小程序的页面路径
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
