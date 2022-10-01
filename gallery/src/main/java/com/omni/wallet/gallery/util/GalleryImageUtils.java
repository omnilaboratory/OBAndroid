package com.omni.wallet.gallery.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.alibaba.android.arouter.launcher.ARouter;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.framelibrary.common.PageRouteConfig;
import com.omni.wallet.framelibrary.utils.AppStorageUtils;
import com.omni.wallet.gallery.common.Constants;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.event.SelectImageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gallery选择图片的工具类
 */

public class GalleryImageUtils {
    private static final String TAG = GalleryImageUtils.class.getSimpleName();
    // 选择图片之后是否裁剪
    private boolean mIsCutImage;
    // 宽度
    private int mCutWidth = 0;
    // 宽度比例
    private int mAspectX = 1;
    // 高度
    private int mCutHeight = 0;
    // 高度比例
    private int mAspectY = 1;
    // 新文件路径
    private String mNewImageName;
    // 临时文件
    private File mFile;

    public GalleryImageUtils() {
    }

    /**
     * 跳转选择图片页面，可以设置最大选择数量
     */
    public void selectImage(Context context, String title, int maxSelectSize) {
        Bundle bundle = new Bundle();
        bundle.putInt(GalleryConfig.KEY_MAX_SELECT_IMAGE_SIZE, maxSelectSize);
        bundle.putInt(GalleryConfig.KEY_PAGE_TYPE, Constants.TYPE_SELECT_IMAGE);
        bundle.putString(GalleryConfig.KEY_TITLE, title);
        bundle.putBoolean(GalleryConfig.KEY_CUT_IMAGE, false);
        ARouter.getInstance().build(PageRouteConfig.PAGE_GALLERY).with(bundle).navigation();
//        startActivity(context, bundle, GalleryChooseImageActivity.class);
    }

    /**
     * 选择单张图片
     */
    public void selectImage(Context context) {
        this.mIsCutImage = false;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, GalleryConfig.REQUEST_CODE_GALLERY);
    }

    /**
     * 调用系统图库选择单张图片，选择之后裁剪
     */
    public void selectImageAndCut(Context context, String title) {
        selectImageAndCut(context, title, 2, 3, 320, 480);
    }

    /**
     * 选择单张图片并裁剪
     */
    public void selectImageAndCut(Context context, String newPath, int aspectX, int aspectY, int cutWidth, int cutHeight) {
        this.mIsCutImage = true;
        this.mAspectX = aspectX;
        this.mAspectY = aspectY;
        this.mCutWidth = cutWidth;
        this.mCutHeight = cutHeight;
        this.mNewImageName = newPath;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, GalleryConfig.REQUEST_CODE_GALLERY);
    }


    /**
     * 打开Activity
     */
    private void startActivity(Context context, Bundle bundle, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 图库选择图片、裁剪图片的回调
     */
    public void onActivityResult(final Context context, int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case GalleryConfig.REQUEST_CODE_GALLERY:
                    final String imagePath;
                    if (Build.VERSION.SDK_INT >= 19) {
                        imagePath = handleImageOnKitkat(context, data);
                    } else {
                        imagePath = handleImageBeforeKitkat(context, data);
                    }
                    // 裁剪或者回调
                    if (mIsCutImage) {// 需要裁剪就调用裁剪
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFile = new File(AppStorageUtils.getTempCameraImageDir(context), mNewImageName);
                                if (!mFile.getParentFile().exists()) {
                                    boolean result = mFile.getParentFile().mkdirs();
                                    LogUtils.e(TAG, "创建裁剪文件的文件夹结果：" + result);
                                }
                                // 调用裁剪
                                new CutImageUtils().cutImage(context, imagePath, mFile.getAbsolutePath(), mAspectX, mAspectY, mCutWidth, mCutHeight);
                            }
                        });
                    } else {// 否则将获取的图片添加到集合，然后传递出去
                        postSelectImageList(imagePath);
                    }
                    break;
                case GalleryConfig.REQUEST_CODE_CUT_IMAGE:
                    LogUtils.e(TAG, "=====picturePath=====>" + mFile.getAbsolutePath());
                    // 如果裁剪之后的图片存在就把路径添加到集合中传递出去
                    if (mFile.exists()) {
                        postSelectImageList(mFile.getAbsolutePath());
                    }
                    break;
            }
        }
    }

    private String handleImageOnKitkat(Context context, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (uri == null) {
            return null;
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" + "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;

    }

    private String handleImageBeforeKitkat(Context context, Intent data) {
        Uri uri = data.getData();
        return getImagePath(context, uri, null);
    }

    /**
     * 跟进Uri获取文件路径
     */
    private String getImagePath(Context context, Uri uri, String selection) {
        if (uri == null) {
            return null;
        }
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 将获取的图片集合传递出去
     */
    private void postSelectImageList(String imagePath) {
        List<GalleryEntity> selectImageList = new ArrayList<>();
        GalleryEntity entity = new GalleryEntity();
        entity.setFilePath(imagePath);
        selectImageList.add(entity);
        SelectImageEvent event = new SelectImageEvent();
        event.setSelectImageList(selectImageList);
        EventBus.getDefault().post(event);
    }
}
