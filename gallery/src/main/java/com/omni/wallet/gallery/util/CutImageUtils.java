package com.omni.wallet.gallery.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.omni.wallet.framelibrary.common.Constants;

import java.io.File;

/**
 * 图片裁剪工具类
 */

public class CutImageUtils {
    private static final String TAG = CutImageUtils.class.getSimpleName();

    public void cutImage(Context context, String filePath, String newFilePath, int aspectX, int aspectY, int cutWidth, int cutHeight) {
        // 宽高必须不能为0，裁剪宽高比可以为0
        if (cutWidth == 0 || cutHeight == 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //设置为true,表示解析Bitmap对象，该对象不占内存
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            cutWidth = options.outWidth;
            float resultHeight = options.outWidth / aspectX * aspectY;
            cutHeight = (int) resultHeight;
//            cutWidth = 480;
        }
//        if (cutHeight == 0) {
//            cutHeight = 640;
//        }
        File oldFile = new File(filePath);
        Uri oldFileUri;
        Intent intent = new Intent(GalleryConfig.ACTION_CAMERA_CROP);
        // 适配安卓7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            oldFileUri = FileProvider.getUriForFile(context, Constants.FILE_CONTENT_FILE_PROVIDER, oldFile);
        } else {
            oldFileUri = Uri.fromFile(oldFile);
        }
        intent.setDataAndType(oldFileUri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", cutWidth);
        intent.putExtra("outputY", cutHeight);
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true);
        // 图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 此处控制是否从intent中返回数据，避免内存占用过大此处返回false
        intent.putExtra("return-data", false);
        // 下面就是设置裁剪之后的图片的保存位置
        File newFile = new File(newFilePath);
        // 图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
//        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
//        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        intentBc.setData(Uri.fromFile(newFile));
//        context.sendBroadcast(intentBc);
        // 返回
        ((Activity) context).startActivityForResult(intent, GalleryConfig.REQUEST_CODE_CUT_IMAGE);
    }


    /**
     * 安卓7.0裁剪根据文件路径获取uri（这个方法也可以用，不要删除）
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
