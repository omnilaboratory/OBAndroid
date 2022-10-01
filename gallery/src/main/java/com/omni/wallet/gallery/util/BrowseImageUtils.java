package com.omni.wallet.gallery.util;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omni.wallet.framelibrary.common.PageRouteConfig;
import com.omni.wallet.gallery.common.Constants;
import com.omni.wallet.gallery.entity.GalleryDataTransmit;
import com.omni.wallet.gallery.entity.GalleryEntity;

import java.util.List;

/**
 * 浏览大图的工具类
 */

public class BrowseImageUtils {
    private static final String TAG = BrowseImageUtils.class.getSimpleName();
    private Gson gson;

    public BrowseImageUtils() {
        gson = new GsonBuilder().serializeNulls().create();
    }

    /**
     * 单纯的图片浏览
     */
    public void browseImage(Context context, List<GalleryEntity> imageList, int position) {
        Bundle bundle = new Bundle();
        // 图片集合Json
        String listJson = gson.toJson(imageList);
        GalleryDataTransmit.getInstance().setGalleryListJson(listJson);
        // 页面类型(主要涉及到后面单张图片浏览的页面的显示方式)
        bundle.putInt(GalleryConfig.KEY_PAGE_TYPE, Constants.TYPE_BROWSE_IMAGE);
        bundle.putBoolean(GalleryConfig.KEY_CUT_IMAGE, false);
        bundle.putInt(GalleryConfig.KEY_CLICK_POSITION, position);// 当前图片在集合中的位置
        ARouter.getInstance().build(PageRouteConfig.PAGE_BROWSE_IMAGE).with(bundle).navigation();
//        startActivity(context, bundle, GalleryBrowseImageActivity.class);
    }

    /**
     * 浏览并编辑图片
     */
    public void browseAndEditImage(Context context, List<GalleryEntity> imageList, int position, int requestCode) {
        Bundle bundle = new Bundle();
        // 图片集合Json
        String listJson = gson.toJson(imageList);
        GalleryDataTransmit.getInstance().setGalleryListJson(listJson);
        // 页面类型(主要涉及到后面单张图片浏览的页面的显示方式)
        bundle.putInt(GalleryConfig.KEY_PAGE_TYPE, Constants.TYPE_EDIT_IMAGE);
        bundle.putBoolean(GalleryConfig.KEY_CUT_IMAGE, false);
        bundle.putInt(GalleryConfig.KEY_CLICK_POSITION, position);// 当前图片在集合中的位置
        bundle.putInt(GalleryConfig.KEY_BROWSE_IMAGE_REQUEST_CODE, requestCode);// 请求码
        ARouter.getInstance().build(PageRouteConfig.PAGE_BROWSE_IMAGE).with(bundle).navigation();
//        startActivity(context, bundle, GalleryBrowseImageActivity.class);
    }

}
