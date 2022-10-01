package com.omni.wallet.gallery.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.gallery.R;
import com.omni.wallet.gallery.util.CameraImageUtils;
import com.omni.wallet.gallery.util.GalleryImageUtils;

import java.util.List;


/**
 * 拍照或者从手机相册选择图片的弹窗
 */
public class SelectImageDialog {
    private static final String TAG = SelectImageDialog.class.getSimpleName();

    private Context mContext;
    // 选择图片的弹窗
    private AlertDialog mAlertDialog;
    // 图库中每次最多选择的图片数量
    private int mMaxSelectImageSize;
    // 拍照的图片文件名
    private String mImageFileName;
    // 图片选择完成之后是否裁剪
    private boolean mNeedCut = false;
    // 宽度
    private int mCutWidth = 0;
    // 宽度比例
    private int mAspectX = 1;
    // 高度
    private int mCutHeight = 0;
    // 高度比例
    private int mAspectY = 1;


    // 获取图片的方式
    private int mSelectImageType = -1;

    // 选择图片的Util类
    private GalleryImageUtils mGalleryImageUtils;
    // 拍摄图片的Util类
    private CameraImageUtils mCameraImageUtils;


    private static final int TYPE_SELECT_IMAGE_CAMERA = 1;// 拍照获取
    private static final int TYPE_SELECT_IMAGE_GALLERY = 2;// 图库获取

    public SelectImageDialog(Context context) {
        this.mContext = context;
        // 图库操作类
        mGalleryImageUtils = new GalleryImageUtils();
        // 相机拍照操作类
        mCameraImageUtils = new CameraImageUtils();
    }

    public SelectImageDialog maxSelectSize(int maxSize) {
        this.mMaxSelectImageSize = maxSize;
        return this;
    }

    public SelectImageDialog newFileName(String fileName) {
        this.mImageFileName = fileName;
        return this;
    }

    public SelectImageDialog needCutImage(boolean needCut) {
        this.mNeedCut = needCut;
        return this;
    }

    public SelectImageDialog setCutWidth(int cutWidth) {
        this.mCutWidth = cutWidth;
        mCameraImageUtils.setCutWidth(cutWidth);
        return this;
    }

    public SelectImageDialog setCutHeight(int cutHeight) {
        this.mCutHeight = cutHeight;
        mCameraImageUtils.setCutHeight(cutHeight);
        return this;
    }

    public SelectImageDialog setAspectX(int mAspectX) {
        this.mAspectX = mAspectX;
        mCameraImageUtils.setAspectX(mAspectX);
        return this;
    }

    public SelectImageDialog setAspectY(int mAspectY) {
        this.mAspectY = mAspectY;
        mCameraImageUtils.setAspectY(mAspectY);
        return this;
    }

    public boolean isShowing() {
        return mAlertDialog != null && mAlertDialog.isShowing();
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * 显示选择图片的Dialog
     * 注意调用之前的权限判断
     */
    public void showSelectImageDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.gallery_layout_dialog_select_image)
                    .setOnClickListener(R.id.staff_tv_select_image_camera, new MyDialogItemClickListener())
                    .setOnClickListener(R.id.staff_tv_select_image_gallery, new MyDialogItemClickListener())
                    .setOnClickListener(R.id.staff_tv_select_image_cancel, new MyDialogItemClickListener())
                    .fullWidth()
                    .fromBottom(true)
                    .create();
        }
        mAlertDialog.show();
    }

    /**
     * 条目点击事件
     */
    private class MyDialogItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.staff_tv_select_image_camera) {// 选择拍摄
                PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        mSelectImageType = TYPE_SELECT_IMAGE_CAMERA;
                        mCameraImageUtils.selectImageFromCamera(mContext, mImageFileName, mNeedCut);
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "相机权限拒绝");
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "相机权限拒绝，并勾选不再提示");
                    }
                });
            } else if (id == R.id.staff_tv_select_image_gallery) {// 点击从手机相册选择
                PermissionUtils.externalStorage((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        mSelectImageType = TYPE_SELECT_IMAGE_GALLERY;
                        if (mMaxSelectImageSize == 1) {
                            if (mNeedCut) {
                                mGalleryImageUtils.selectImageAndCut(mContext, mImageFileName, mAspectX, mAspectY, mCutWidth, mCutHeight);
                            } else {
                                mGalleryImageUtils.selectImage(mContext);
                            }
                        } else {
                            mGalleryImageUtils.selectImage(mContext, "选择图片", mMaxSelectImageSize);
                        }
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "外部存储权限拒绝");
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "外部存储权限拒绝，并勾选不再提示");
                    }
                });
            }
            mAlertDialog.dismiss();
        }
    }

    /**
     * 拍照回调
     */
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (mSelectImageType == TYPE_SELECT_IMAGE_CAMERA) {// 处理拍照的返回
            mCameraImageUtils.onActivityResult(requestCode, resultCode, data);
        } else if (mSelectImageType == TYPE_SELECT_IMAGE_GALLERY) {// 处理图库选择的返回
            mGalleryImageUtils.onActivityResult(mContext, requestCode, resultCode, data);
        }
    }

}
