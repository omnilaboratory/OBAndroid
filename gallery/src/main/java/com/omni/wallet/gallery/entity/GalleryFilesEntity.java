package com.omni.wallet.gallery.entity;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 遍历系统图片文件的实体类
 */
@SuppressLint("ParcelCreator")
public class GalleryFilesEntity implements Parcelable {

    // 所属图片的文件名称
    public String fileName;
    // 该文件夹下面对应的文件实体集合
    public List<GalleryEntity> entityContent = new ArrayList<>();
    // 是否被选中
    public boolean selected;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeList(entityContent);
        dest.writeValue(selected);
    }

    public static final Creator<GalleryFilesEntity> CREATOR = new Creator<GalleryFilesEntity>() {

        @Override
        public GalleryFilesEntity[] newArray(int size) {
            return null;
        }

        @Override
        public GalleryFilesEntity createFromParcel(Parcel source) {
            GalleryFilesEntity ft = new GalleryFilesEntity();
            ft.fileName = source.readString();
            ft.entityContent = source.readArrayList(GalleryFilesEntity.class.getClassLoader());
            ft.selected = (boolean) source.readValue(Boolean.class.getClassLoader());
            return ft;
        }

    };
}
