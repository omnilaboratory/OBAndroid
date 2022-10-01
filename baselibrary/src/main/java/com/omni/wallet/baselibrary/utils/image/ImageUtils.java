package com.omni.wallet.baselibrary.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.utils.image.glide.GlideRoundTransform;

/**
 * 图片显示工具类
 */

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void showImage(Context context, Object imagePath, ImageView imageView) {
        showImage(context, imagePath, imageView, 0, 0, R.color.color_f7f7f7);
    }

    public static void showImage(Context context, Object imagePath, ImageView imageView, int placeHolder) {
        showImage(context, imagePath, imageView, 0, 0, placeHolder);
    }

    public static void showImage(Context context, Object imagePath, ImageView imageView, int imageWidth, int imageHeight) {
        showImage(context, imagePath, imageView, imageWidth, imageHeight, R.color.color_f7f7f7);
    }

    public static void showImage(Context context, Object imagePath, ImageView imageView, int imageWidth, int imageHeight, int placeHolder) {
        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
        if (imageWidth > 0 && imageHeight > 0) {
            builder.override(imageWidth, imageHeight);
        }
        builder.into(imageView);
    }

    public static void showImageCenterCrop(Context context, Object imagePath, ImageView imageView) {
        showImageCenterCrop(context, imagePath, imageView, R.color.color_f7f7f7);
    }

    public static void showImageCenterCrop(Context context, Object imagePath, ImageView imageView, int width, int height) {
        showImageCenterCrop(context, imagePath, imageView, R.color.color_f7f7f7, width, height);
    }

    public static void showImageCenterCrop(Context context, Object imagePath, ImageView imageView, int placeHolder) {
        showImageCenterCrop(context, imagePath, imageView, placeHolder, 0, 0);
    }

    public static void showImageCenterCrop(Context context, Object imagePath, ImageView imageView, int placeHolder, int width, int height) {
        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
        builder.centerCrop();
        if (width > 0 && height > 0) {
            builder.override(width, height);
        }
        builder.into(imageView);
    }

    public static void showImageFitCenter(Context context, Object imagePath, ImageView imageView) {
        showImageFitCenter(context, imagePath, imageView, R.color.color_f7f7f7);
    }

    public static void showImageFitCenter(Context context, Object imagePath, ImageView imageView, int placeHolder) {
        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
        builder.fitCenter();
        builder.into(imageView);
    }

    public static void showImageRound(Context context, Object imagePath, ImageView imageView, int roundDP) {
        showImageRound(context, imagePath, imageView, R.color.color_f7f7f7, roundDP);
    }

    public static void showImageRound(Context context, Object imagePath, ImageView imageView, int placeHolder, int roundDP) {
        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
        builder.transform(new CenterCrop(context), new GlideRoundTransform(context, roundDP));
        builder.into(imageView);
    }

//    public static void showImageCircle(Context context, Object imagePath, ImageView imageView) {
//        showImageCircle(context, imagePath, imageView, R.color.color_f7f7f7);
//    }
//
//    public static void showImageCircle(Context context, Object imagePath, ImageView imageView, int placeHolder) {
//        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
//        builder.centerCrop();
//        builder.transform(new GlideCircleTransform(context));
//        builder.into(imageView);
//    }

    private static DrawableRequestBuilder getBuilder(Context context, Object imagePath, int placeHolder) {
        DrawableRequestBuilder builder = Glide.with(context).load(imagePath);
        if (placeHolder != 0) {
            builder.error(placeHolder).placeholder(placeHolder);
        }
        return builder;
    }


    public static void showImageCenterCropOverride(Context context, Object imagePath, ImageView imageView,
                                                   int width, int height, int placeHolder) {
        DrawableRequestBuilder builder = getBuilder(context, imagePath, placeHolder);
        builder.centerCrop();
        builder.crossFade();
        if (width > 0 && height > 0) {
            builder.override(width, height);
        }
        builder.into(imageView);
    }

    public static void showGifCenterCrop(Context context, Object imagePath, ImageView imageView, int placeHolder) {
        Glide.with(context).load(imagePath)
                .asGif()
                .centerCrop()
                .error(placeHolder)
                .skipMemoryCache(true)// 禁止内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 设置不缓存
                .placeholder(placeHolder)
                .into(imageView);
    }

    /**
     * 给Layout设置背景
     */
    public static void showLayoutBg(final Context context, Object imagePath, final View layout) {
        showLayoutBg(context, imagePath, layout, R.color.color_f7f7f7);
    }

    public static void showLayoutRoundBg(final Context context, Object imagePath, final View layout, int roundDP) {
        showLayoutRoundBg(context, imagePath, layout, R.color.color_f7f7f7, roundDP);
    }

    /**
     * 给Layout设置背景
     */
    public static void showLayoutBg(final Context context, Object imagePath, final View layout, int placeHolder) {
        Glide.with(context)
                .load(imagePath)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(new ViewTarget<View, GlideDrawable>(layout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    public static void showLayoutRoundBg(final Context context, Object imagePath, final View layout, int placeHolder, int rounddp) {
        Glide.with(context)
                .load(imagePath)
                .transform(new CenterCrop(context), new GlideRoundTransform(context, rounddp))
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(new ViewTarget<View, GlideDrawable>(layout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    /**
     * 给Layout设置背景
     */
    public static void showLayoutBg(final Context context, Object imagePath, final ViewGroup layout, Drawable placeHolder) {
        Glide.with(context)
                .load(imagePath)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(new ViewTarget<View, GlideDrawable>(layout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        layout.setBackground(resource.getCurrent());
                    }
                });
    }

    /**
     * 加载Bitmap
     */
    public static void getBitmap(final Context context, Object imagePath, final LoadBitmapCallback callback) {
        Glide.with(context)
                .load(imagePath)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
                            return;
                        }
                        if (callback != null) {
                            callback.onLoadReady(bitmap, glideAnimation);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        if (callback != null) {
                            callback.onLoadFail(e);
                        }
                    }
                });
    }

    public static void LoadImageWithCallback(Context context, final ImageView imageView, Object obj, final LoadDrawableCallback callback) {
        Glide.with(context)
                .load(obj)
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadException(e);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onLoadReady(resource);
                        }
                        return false;
                    }
                }).into(imageView);

    }

    public static void getImageDrawable(Context context, Object path, final LoadDrawableCallback callback) {
        Glide.with(context)
                .load(path)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        if (callback != null) {
                            callback.onLoadReady(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        if (callback != null) {
                            callback.onLoadException(e);
                        }
                    }
                });
    }

    /**
     * 获取Bitmap的回调
     */
    public interface LoadBitmapCallback {
        void onLoadReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation);

        void onLoadFail(Exception e);
    }

    /**
     * 获取Bitmap的回调
     */
    public interface LoadDrawableCallback {
        void onLoadReady(Drawable drawable);

        void onLoadException(Exception e);
    }

    /**
     * 给Layout设置背景(可以设置背景图片宽高)
     * 这种方式会按照设置的宽高去截取图片，再去加载到控件背景上，有时会导致图片显示不全
     */
    public static void showLayoutBgWithSize(final Context context, Object imagePath, final ViewGroup layout, int width, int height) {
        showLayoutBgWithSize(context, imagePath, layout, width, height, R.color.color_f7f7f7);
    }

    /**
     * 给Layout设置背景(可以设置背景图片宽高)
     * 这种方式会按照设置的宽高去截取图片，再去加载到控件背景上，有时会导致图片显示不全
     */
    public static void showLayoutBgWithSize(final Context context, Object imagePath, final ViewGroup layout, int width, int height, final int placeHolder) {
        Glide.with(context)
                .load(imagePath)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(width, height) {//设置宽高
                          @Override
                          public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                              Drawable drawable;
                              if (resource != null) {
                                  drawable = new BitmapDrawable(context.getResources(), resource);

                              } else {
                                  drawable = ContextCompat.getDrawable(context, placeHolder);
                              }
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                  layout.setBackground(drawable);   //设置背景
                              } else {
                                  layout.setBackgroundDrawable(drawable);   //设置背景
                              }
                          }
                      }

                );
    }
}
