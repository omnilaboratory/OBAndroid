package com.libjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NativeUtil {
    // 默认的压缩比
    private static int DEFAULT_QUALITY = 95;

    /**
     * 加载lib下两个so文件
     */
    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
    }

    /**
     * JNI基本压缩
     *
     * @param bit      bitmap对象
     * @param fileName 指定保存目录名
     * @param optimize 是否采用哈弗曼表数据计算 品质相差5-10倍
     */
    public static void compressBitmap(Bitmap bit, String fileName, boolean optimize) {
        saveBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
    }

    /**
     * 通过JNI图片压缩把Bitmap保存到指定目录
     *
     * @param image    bitmap对象
     * @param filePath 要保存的指定目录
     */
    public static void compressBitmap(Bitmap image, String filePath) {
        // 最大图片大小 150KB
        int maxSize = 150;
        // 获取尺寸压缩倍数
        int ratio = NativeUtil.getRatioSize(image.getWidth(), image.getHeight());
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio);
        canvas.drawBitmap(image, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize) { // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        } // JNI保存图片到SD卡 这个关键
        NativeUtil.saveBitmap(result, options, filePath, true);
        // 释放Bitmap
        if (!result.isRecycled()) {
            result.recycle();
        }
    }

    /**
     * 通过JNI图片压缩把Bitmap保存到指定目录
     *
     * @param curFilePath    当前图片文件地址
     * @param targetFilePath 要保存的图片文件地址
     * @param maxSize        压缩的限制文件大小（K）
     */
    public static void compressBitmap(String curFilePath, String targetFilePath, long maxSize) {
        if (maxSize == 0) {
            // 默认的最大图片大小 200KB
            maxSize = 200 * 1024;
        }
        //根据地址获取bitmap
        Bitmap result = getBitmapFromFile(curFilePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到bos中
        int quality = 100;
        result.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        // 循环判断如果压缩后图片是否大于限定的图片大小,大于继续压缩
        while (bos.toByteArray().length > maxSize) {
            // 重置bos即清空bos
            bos.reset();
            // 每次都减少10
            quality -= 10;
            // 判断quality小于0（quality取值范围只能是0——100）
            if (quality < 0) {
                break;
            }
            // 这里压缩quality，把压缩后的数据存放到bos中
            result.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        }
        // JNI保存图片到SD卡 这个关键
        NativeUtil.saveBitmap(result, quality, targetFilePath, true);
        // 释放Bitmap
        if (!result.isRecycled()) {
            result.recycle();
        }
    }

    /**
     * 计算缩放比
     *
     * @param bitWidth  当前图片宽度
     * @param bitHeight 当前图片高度
     * @return int 缩放比
     */
    public static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0) ratio = 1;
        return ratio;
    }

    /**
     * 通过文件路径读获取Bitmap防止OOM以及解决图片旋转问题
     */
    public static Bitmap getBitmapFromFile(String filePath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        //只读边,不读内容
        BitmapFactory.decodeFile(filePath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 获取尺寸压缩倍数
        newOpts.inSampleSize = getRatioSize(w, h);
        newOpts.inJustDecodeBounds = false;
        //读取所有内容
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inTempStorage = new byte[32 * 1024];
        Bitmap bitmap = null;
        File file = new File(filePath);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
                //旋转图片
                int photoDegree = readPictureDegree(filePath);
                if (photoDegree != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(photoDegree);
                    // 创建新的图片
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 调用native方法
     *
     * @param bit
     * @param quality
     * @param fileName
     * @param optimize
     * @Description:函数描述
     */
    private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
        compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
    }


    /**
     * 1. 质量压缩 设置bitmap options属性，降低图片的质量，像素不会减少
     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
     * 设置options 属性0-100，来实现压缩
     *
     * @param bmp
     * @param file
     */
    public static void compressImageToFile(Bitmap bmp, File file) {
        // 0-100 100为不压缩
        int options = 20;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * * 2. 尺寸压缩 通过缩放图片像素来减少图片占用内存大小 * @param bmp * @param file
     */
    public static void compressBitmapToFile(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 8;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 3.设置图片的采样率，降低图片像素
     *
     * @param filePath
     * @param file
     */
    public static void compressBitmap(String filePath, File file) {
        // 数值越高，图片像素越低
        int inSampleSize = 8;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
        // 采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用底层 bitherlibjni.c中的方法
     *
     * @param bit
     * @param w
     * @param h
     * @param quality
     * @param fileNameBytes
     * @param optimize
     * @return
     * @Description: 函数描述
     */
    private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes, boolean optimize);
}
