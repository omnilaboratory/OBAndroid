package com.omni.testnet.baselibrary.utils.image.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

/**
 * Glide缓存配置
 * 内部默认存储的目录：data/data/包名/cache/image_manager_disk_cache
 */
public class GlideCacheModule implements GlideModule {
    private static final String TAG = GlideCacheModule.class.getSimpleName();

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //设置缓存在内部存储的目录（内部存储的目录：data/data/包名/cache/image_manager_disk_cache）
//        File storageDirectory = Environment.getExternalStorageDirectory();
//        String downloadDirectoryPath = storageDirectory + "/GlideCache";
        // 设置缓存大小为100M;
        int cacheSize = 1024 * 1024 * 100;
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheSize));
        // 调整内存缓存和缓存池大小
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        // 获取内存缓存大小
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        // 获取缓存池大小
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        // 自定义内存缓存大小
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        // 自定义缓存池大小
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
        // 设置
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}
