package com.omni.wallet_mainnet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.utils.FileUtils;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ThreadPoolUtils;
import com.omni.wallet_mainnet.framelibrary.entity.event.CompressEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 图片压缩工具类
 */

public class CompressImageUtils {
    private static final String TAG = CompressImageUtils.class.getSimpleName();

    private Context mContext;
    // 允许的图片大小，默认200K
    private long mMaxImageSize = MAX_IMAGE_LENGTH;
    // 压缩文件的时候生成的最终文件路径
    private String mCompressResultPath;

    // 压缩允许的图片的最大大小（1M）
    public static final long MAX_IMAGE_LENGTH = 200 * 1024;

    public CompressImageUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 设置文件大小上限
     */
    public void setmMaxImageSize(long maxImageSize) {
        this.mMaxImageSize = maxImageSize;
    }

    /**
     * 压缩图片
     */
    public void compressImage(final String imagePath) {
        // 判断源文件的大小
        long fileSize = FileUtils.getFileSize(imagePath);
        LogUtils.e(TAG, "原图（" + imagePath + "）大小是：" + FileUtils.getFileLength(imagePath));
        // 如果文件大小小于限制大小，则直接上传
        if (fileSize <= mMaxImageSize) {
            LogUtils.e(TAG, "原图大小符合限制标准，不压缩");
            // 回调回去
            postResult(true, imagePath);
            return;
        }
        // 子线程压缩
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                // 生成临时文件的路径
                mCompressResultPath = AppStorageUtils.getTempImageDir(mContext) + File.separator + System.currentTimeMillis() + ".jpg";
                File tempFile = new File(mCompressResultPath);
                if (!tempFile.getParentFile().exists()) {
                    boolean result = tempFile.getParentFile().mkdirs();
                    LogUtils.e(TAG, "压缩文件的生成结果：" + result);
                }
                // 压缩
//                NativeUtil.compressBitmap(imagePath, mCompressResultPath, mMaxImageSize);
                LogUtils.e(TAG, "压缩后图片(" + mCompressResultPath + ")大小是：" + FileUtils.getFileLength(mCompressResultPath));
                // 压缩完成判断文件是否存在（文件本身大小就满足的时候不会走压缩，所以要将原路径赋值给最终路径）
                if (new File(mCompressResultPath).exists()) {
                    postResult(true, mCompressResultPath);
                } else {
                    LogUtils.e(TAG, "==========压缩后的文件" + mCompressResultPath + "不存在==========>");
                    postResult(false, imagePath);
                }
            }
        });
    }


    /**
     * 发送处理结果
     */
    private void postResult(boolean result, String imagePath) {
        CompressEvent event = new CompressEvent();
        event.setResult(result);
        event.setImagePath(imagePath);
        EventBus.getDefault().post(event);
    }

    /**
     * 删除生成的临时图片文件
     */
    public void delTempImage() {
        boolean result = FileUtils.delFile(mCompressResultPath);
        LogUtils.e(TAG, "删除临时文件" + (result ? "成功" : "失败"));
    }

}
