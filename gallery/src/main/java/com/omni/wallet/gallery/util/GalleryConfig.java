package com.omni.wallet.gallery.util;

/**
 * GalleryModule的一些配置信息
 * 一些配置参数以及KEY
 */

public class GalleryConfig {

    // 页面标题的Key
    public static final String KEY_TITLE = "titleKey";
    // 图片页面展示方式的Key
    public static final String KEY_PAGE_TYPE = "pageTypeKey";
    // 图片选择数量的Key
    public static final String KEY_MAX_SELECT_IMAGE_SIZE = "maxImageSelectKey";
    // 是否裁剪照片的Key
    public static final String KEY_CUT_IMAGE = "cutImageKey";
        // 裁剪的图片的宽度的Key
    public static final String KEY_CUT_IMAGE_WIDTH = "cutImageWidthKey";
    // 裁剪图片的高度的Key
    public static final String KEY_CUT_IMAGE_HEIGHT = "cutImageHeightKey";
    // 裁剪的图片的宽高比X的Key
    public static final String KEY_CUT_IMAGE_ASPECT_X = "cutImageAspectXKey";
    // 裁剪的图片的宽高比Y的Key
    public static final String KEY_CUT_IMAGE_ASPECT_Y = "cutImageAspectYKey";
    // 传递详情页数据类型的Key
    public static final String KEY_DATA_TYPE = "dataTypeKey";
    // 传递点击的索引使用的KEY
    public static final String KEY_CLICK_POSITION = "clickPositionKey";
    // 图片浏览大图的请求码
    public static final String KEY_BROWSE_IMAGE_REQUEST_CODE = "requestCodeKey";
    // 请求相机裁剪的Action
    public static final String ACTION_CAMERA_CROP = "com.android.camera.action.CROP";

    // 选择照相机的请求码
    public static final int REQUEST_CODE_CAMERA = 99;
    // 裁剪图片的请求码
    public static final int REQUEST_CODE_CUT_IMAGE = 88;
    // 系统图库的请求码
    public static final int REQUEST_CODE_GALLERY = 77;
}
