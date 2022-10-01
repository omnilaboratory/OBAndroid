package com.omni.wallet.thirdsupport.umeng.share.bean;

import android.graphics.Bitmap;

import com.omni.wallet.thirdsupport.R;

import java.io.File;

/**
 * 图片类型的分享内容实体
 */

public class ImageShareBean extends BaseShareBean {
    // 图片URl
    private String imageUrl;
    // 本地图片文件
    private File imageFile;
    // 图片资源ID
    private int imageRes;
    // 图片Bitmap
    private Bitmap imageBitmap;
    // 图片字节数组
    private byte[] imageByte;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }


    /**
     * 创建分享图片的实体
     */
    public static ImageShareBean makeImageShareBean(Object image) {
        ImageShareBean shareImage = new ImageShareBean();
        if (image == null) {
            shareImage.setImageRes(R.drawable.ic_launcher_share);
            return shareImage;
        }
        if (image instanceof Integer) {
            shareImage.setImageRes((Integer) image);
        } else if (image instanceof Bitmap) {
            shareImage.setImageBitmap((Bitmap) image);
        } else if (image instanceof byte[]) {
            shareImage.setImageByte((byte[]) image);
        } else if (image instanceof String) {
            shareImage.setImageUrl((String) image);
        } else if (image instanceof File) {
            shareImage.setImageFile((File) image);
        }
        return shareImage;
    }
}
