package com.omni.wallet.baselibrary.view.banner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;

import java.util.List;

/**
 * 默认的BannerAdapter
 */

public class DefaultBannerAdapter extends BannerAdapter {
    private static final String TAG = DefaultBannerAdapter.class.getSimpleName();
    private Context mContext;
    private List<Object> mBannerData;

    public DefaultBannerAdapter(Context context, List<Object> bannerData) {
        this.mBannerData = bannerData;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView) {
        // 必须对数据长度做判断，否则会出现被除数是0的异常
        ImageView imageView;
        // 初始化的时候banner为空，直接返回一个ImageView,不能返回null
        if (mBannerData == null || mBannerData.size() == 0) {
            imageView = new ImageView(mContext);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            ImageUtils.showImageFitCenter(mContext, R.drawable.icon_banner_default, imageView);
        } else {// 创建用于展示图片的ImageView
            if (mBannerData.size() > 0 && position >= mBannerData.size()) {
                position = position % mBannerData.size();
            }
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            ImageUtils.showImageFitCenter(mContext, imageUrl, imageView, R.drawable.icon_banner_default);
        }
        if (mBannerData.size() > 0) {
            convertImage(imageView, position, R.drawable.icon_banner_default);
        }
        return imageView;
    }

    @Override
    public int getPageCount() {
        return mBannerData == null ? 0 : mBannerData.size();
    }


    protected void convertImage(ImageView imageView, int position, int defaultImageUrl) {
        Object imageUrl = mBannerData.get(position);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (defaultImageUrl != 0) {
            ImageUtils.showImageFitCenter(mContext, imageUrl, imageView, defaultImageUrl);
        } else {
            ImageUtils.showImageFitCenter(mContext, imageUrl, imageView);
        }
    }
}
