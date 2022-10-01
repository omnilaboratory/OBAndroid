package com.omni.wallet.gallery.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.ThreadPoolUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.utils.image.BitmapUtils;
import com.omni.wallet.framelibrary.utils.AppStorageUtils;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.event.SelectImageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 调用相机拍照的工具类
 */

public class CameraImageUtils {
    private static final String TAG = CameraImageUtils.class.getSimpleName();
    private Context mContext;
    private File mFile;
    // 拍照之后得到的图片是否裁剪
    private boolean mIsCutImage;
    // 宽度
    private int mCutWidth;
    // 宽度比例
    private int mAspectX = 1;
    // 高度
    private int mCutHeight;
    // 高度比例
    private int mAspectY = 1;

    public CameraImageUtils() {
    }

    public void setCutWidth(int cutWidth) {
        this.mCutWidth = cutWidth;
    }

    public void setCutHeight(int cutHeight) {
        this.mCutHeight = cutHeight;
    }

    public void setAspectX(int mAspectX) {
        this.mAspectX = mAspectX;
    }

    public void setAspectY(int mAspectY) {
        this.mAspectY = mAspectY;
    }

    /**
     * 调用拍照
     */
    public void selectImageFromCamera(Context context, String fileName, boolean cutImage) {
        this.mContext = context;
        this.mIsCutImage = cutImage;
        LogUtils.e(TAG, "检测相机硬件结果：" + AppUtils.hasCameraSupport(mContext));
        // 检测是否有相机
        if (!AppUtils.hasCameraSupport(mContext)) {
            ToastUtils.showToast(mContext, "拍照设备异常");
            return;
        }
        // TODO 权限检查
        if (!PermissionUtils.hasSelfPermissions(context, Manifest.permission.CAMERA)) {
            LogUtils.e(TAG, "没有相机权限");
            return;
        }
        mFile = new File(AppStorageUtils.getTempCameraImageDir(mContext), fileName);
        if (!mFile.getParentFile().exists()) {
            boolean result = mFile.getParentFile().mkdirs();
            LogUtils.e(TAG, "创建拍照文件的文件夹结果：" + result);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 区分是否7.0做不同的处理
        /*获取当前系统的android版本号*/
        try {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion < Build.VERSION_CODES.N) {
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                ((Activity) mContext).startActivityForResult(intent, GalleryConfig.REQUEST_CODE_CAMERA);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, mFile.getAbsolutePath());
                Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                ((Activity) mContext).startActivityForResult(intent, GalleryConfig.REQUEST_CODE_CAMERA);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "相机开启异常" + e.getMessage());
            e.printStackTrace();
            ToastUtils.showToast(mContext, "相机开启异常");
        }
    }

    /**
     * 选择得回调
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case GalleryConfig.REQUEST_CODE_CAMERA:
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mFile.exists()) {
                            // 照片的路径
                            final String picturePath = mFile.getAbsolutePath();
                            // 根据图片转角旋转图片，然后保存到原来的路径
                            Bitmap bitmap = BitmapUtils.getBitmapFromFile(picturePath);
//                            // 检查裁剪宽高，如果没有设置，就根据宽高比和原图尺寸计算
//                            checkImageWidthAndHeight(bitmap);
                            // 将旋转之后的新图片保存到原路径
                            BitmapUtils.saveBitmapToFile(bitmap, picturePath);
                            // 裁剪或者回调
                            if (mIsCutImage) {// 需要裁剪就调用裁剪
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new CutImageUtils().cutImage(mContext, picturePath, picturePath, mAspectX, mAspectY, mCutWidth, mCutHeight);
                                    }
                                });
                            } else {// 否则将获取的图片添加到集合，然后传递出去
                                postSelectImageList(picturePath);
                            }
                        }
                    }
                });
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

//    /**
//     * 检查裁剪宽高，如果没有设置，就根据宽高比和原图尺寸计算
//     */
//    private void checkImageWidthAndHeight(Bitmap bitmap) {
//        // 裁剪宽度或者高度为0的话根据裁剪宽高比和图片原始宽高计算
//        if (mCutWidth == 0 || mCutHeight == 0) {
//            mCutWidth = bitmap.getWidth();
//            float resultHeight = mCutWidth / mAspectX * mAspectY;
//            mCutHeight = (int) resultHeight;
//        }
//    }

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
