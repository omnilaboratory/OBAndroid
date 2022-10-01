package com.omni.wallet.gallery.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.GalleryFilesEntity;
import com.omni.wallet.gallery.view.ImgCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * @ClassName: GalleryUtils
 * @Description: 获取手机所有图片/视频信息
 */
public class GalleryUtils {

    private static final String TAG = GalleryUtils.class.getSimpleName();
    private Context mContext;

    public GalleryUtils(Context context) {
        this.mContext = context;
    }

    /**
     * @Title: listAllMediaDir
     * @Description: 获取所有视频图片的文件路径
     * @return: ArrayList<GalleryEntity>
     * @author: eye_fa
     */
    public List<GalleryEntity> listAllMediaDir() {
        List<GalleryEntity> allList = new ArrayList<>();
        List<GalleryEntity> imageList = listAllImageDirs();
        for (GalleryEntity image : imageList) {
            GalleryEntity entity = new GalleryEntity();
            entity.setType(GalleryEntity.TYPE_IMAGE);
            entity.setFilePath(image.getFilePath());
            entity.setFileName(StringUtils.getUrlFileName(image.getFilePath()));
            allList.add(entity);
        }
        List<String> videoList = listAllVideoDir();
        for (String video : videoList) {
            GalleryEntity entity = new GalleryEntity();
            entity.setType(GalleryEntity.TYPE_VIDEO);
            entity.setFilePath(video);
            entity.setFileName(StringUtils.getUrlFileNameUnSuffix(video));
            allList.add(entity);
        }
        listSortByFileTime(allList);
        return allList;
    }

    /**
     * @param allList
     * @Title: listSortByFileTime
     * @Description: 按照文件修改顺序排序
     * @return: void
     * @author: eye_fa
     */
    public static void listSortByFileTime(List<GalleryEntity> allList) {
        MyFileTimeComparator comparator = new MyFileTimeComparator();
        Collections.sort(allList, comparator);
    }

    /**
     * @ClassName: MyFileTimeComparator
     * @Description: 文件修改时间比较器
     * @author: eye_fa
     * @date: 2016-7-6 下午5:33:57
     */
    private static class MyFileTimeComparator implements Comparator<GalleryEntity> {
        // 关于Collator
        private Collator collator = Collator.getInstance(java.util.Locale.CHINA);

        /**
         * compare 实现排序。
         *
         * @param o1 Object
         * @param o2 Object
         * @return int
         */
        public int compare(GalleryEntity o1, GalleryEntity o2) {
            CollationKey key1 = null;
            CollationKey key2 = null;
            if (o1 != null && !StringUtils.isEmpty(o1.getFilePath())) {
                File file1 = new File(o1.getFilePath());
                // long lastModify1 = new File(o1.getFilePath()).lastModified();
                // 把字符串转换为一系列比特，它们可以以比特形式与 CollationKeys 相比较
                key1 = collator.getCollationKey(Long.toString(file1.lastModified()));// 要想不区分大小写进行比较用o1.toString().toLowerCase()
            }
            if (o2 != null && !StringUtils.isEmpty(o2.getFilePath())) {
                File file2 = new File(o2.getFilePath());
                // long lastModify2 = new File(o2.getFilePath()).lastModified();
                // 把字符串转换为一系列比特，它们可以以比特形式与 CollationKeys 相比较
                key2 = collator.getCollationKey(Long.toString(file2.lastModified()));// 要想不区分大小写进行比较用o1.toString().toLowerCase()
            }
            if (key1 != null && key2 != null) {
                return key1.compareTo(key2);// 返回的分别为1,0,-1分别代表大于，等于，小于。要想按照字母降序排序的话加个“-”号
            }
            return 0;
        }
    }

    /**
     * 获取全部图片地址
     */
    public ArrayList<GalleryEntity> listAllImageDirs() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        ArrayList<GalleryEntity> list = new ArrayList<>();
        if (uri != null) {
            String[] data = {MediaStore.Images.Media.DATA};
            Cursor cursor = mContext.getContentResolver().query(uri, data, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    // 不获取gif图
                    if (!path.endsWith(".gif")) {
                        GalleryEntity entity = new GalleryEntity();
                        entity.setFilePath(new File(path).getAbsolutePath());
                        list.add(entity);
                    }
                }
                cursor.close();
            }
            // 按照时间排序
            listSortByFileTime(list);
            // 反转集合，使得最新的图片在最上方展示
            Collections.reverse(list);
        }
        return list;
    }

    /**
     * 获取全部视频地址
     */
    public ArrayList<String> listAllVideoDir() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        ArrayList<String> list = new ArrayList<>();
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, data, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                list.add(new File(path).getAbsolutePath());
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 获取按照文件夹分类的图片文件信息
     */
    public List<GalleryFilesEntity> getImagesFileList(List<GalleryEntity> allPicDir) {
        // 图片所在文件夹的集合
        List<GalleryFilesEntity> resultList = new ArrayList<>();
        // 如果传递进来的所有图片信息为空，就重新获取
        if (allPicDir == null) {
            allPicDir = listAllImageDirs();
        }
        if (allPicDir != null) {
            // 主要利用TreeSet来去重复
            Set<String> treeSet = new TreeSet<>();
            // 遍历获取到不重复的文件夹信息
            for (int i = 0; i < allPicDir.size(); i++) {
                treeSet.add(getFileInfo(allPicDir.get(i).getFilePath()));
            }
            // 将去重之后的文件夹集合转换成实体集合
            String[] fileNames = treeSet.toArray(new String[0]);
            // 遍历所有文件信息，并将获取到的图片所在文件夹信息组成一个集合
            for (String fileName : fileNames) {
                GalleryFilesEntity ftl = new GalleryFilesEntity();
                ftl.fileName = fileName;
                resultList.add(ftl);
            }
            // 以文件夹集合为基准去与所有文件集合做对比，当图片所在文件夹的名字与文件夹集合相同时，
            // 就将该文件添加到实体的图片集合中
            for (GalleryFilesEntity parentFile : resultList) {
                for (GalleryEntity entity : allPicDir) {
                    // 如果文件夹的名字与图片文件路径截取出来的文件夹相同就将该图片添加到相应的集合中
                    if (parentFile.fileName.equals(getFileInfo(entity.getFilePath()))) {
                        parentFile.entityContent.add(entity);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 获取按照文件夹分类的媒体文件信息
     */
    public List<GalleryFilesEntity> localMediaFileList(List<GalleryEntity> allMediaDir) {
        List<GalleryFilesEntity> resultList = new ArrayList<>();
        if (allMediaDir == null) {
            allMediaDir = listAllMediaDir();
        }
        if (allMediaDir != null) {
            Set<String> treeSet = new TreeSet<>();
            for (int i = 0; i < allMediaDir.size(); i++) {
                treeSet.add(getFileInfo(allMediaDir.get(i)));
            }
            String[] fileNames = treeSet.toArray(new String[0]);
            for (String fileName : fileNames) {
                GalleryFilesEntity ftl = new GalleryFilesEntity();
                ftl.fileName = fileName;
                resultList.add(ftl);
            }
            for (GalleryFilesEntity data : resultList) {
                for (GalleryEntity entity : allMediaDir) {
                    if (data.fileName.equals(getFileInfo(entity))) {
                        data.entityContent.add(entity);
                    }
                }
            }
        }
        return resultList;
    }

    // 显示原生图片尺寸大小
    public Bitmap getPathBitmap(Uri imageFilePath, int dw, int dh) throws FileNotFoundException {
        // 获取屏幕的宽和高
        /**
         * 为了计算缩放的比例，我们需要获取整个图片的尺寸，而不是图片
         * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds，将其设置为true
         * 这样，我们获取到的就是图片的尺寸，而不用加载图片了。
         * 当我们设置这个值的时候，我们接着就可以从BitmapFactory.Options的outWidth和outHeight中获取到值
         */
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        // 由于使用了MediaStore存储，这里根据URI获取输入流的形式
        Bitmap pic = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageFilePath), null, op);

        int wRatio = (int) Math.ceil(op.outWidth / (float) dw); // 计算宽度比例
        int hRatio = (int) Math.ceil(op.outHeight / (float) dh); // 计算高度比例

        /**
         * 接下来，我们就需要判断是否需要缩放以及到底对宽还是高进行缩放。 如果高和宽不是全都超出了屏幕，那么无需缩放。
         * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》 这需要判断wRatio和hRatio的大小
         * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。 缩放使用的还是inSampleSize变量
         */
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false; // 注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
        pic = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageFilePath), null, op);

        return pic;
    }

    /**
     * 根据文件路径获取文件所在文件夹的名字
     */
    private String getFileInfo(String data) {
        String[] filename = data.split("/");
        if (filename != null) {
            return filename[filename.length - 2];
        }
        return null;
    }

    /**
     * 根据文件实体信息中的文件路径获取文件所在文件夹的名字
     */
    public String getFileInfo(GalleryEntity entity) {
        if (entity != null && !StringUtils.isEmpty(entity.getFilePath())) {
            String filename[] = entity.getFilePath().split("/");
            if (filename != null) {
                return filename[filename.length - 2];
            }
        }
        return null;
    }


    public void imgExecuteAsync(ImageView imageView, ImgCallBack icb, String... params) {
        LoadBitAsync loadBitAsync = new LoadBitAsync(imageView, icb);
        loadBitAsync.execute(params);
    }

    public class LoadBitAsync extends AsyncTask<String, Integer, Bitmap> {

        ImageView imageView;
        ImgCallBack icb;

        LoadBitAsync(ImageView imageView, ImgCallBack icb) {
            this.imageView = imageView;
            this.icb = icb;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        bitmap = getPathBitmap(Uri.fromFile(new File(params[i])), 200, 200);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                // imageView.setImageBitmap(result);
                icb.resultImgCall(imageView, result);
            }
        }

    }

    // /**
    // * @param mContext
    // * @param cr
    // * @param Videopath
    // * @return
    // */
    // public static Bitmap getVideoThumbnail(Context mContext, ContentResolver
    // cr, String Videopath) {
    // ContentResolver testcr = mContext.getContentResolver();
    // String[] projection = { MediaStore.Video.Media.DATA,
    // MediaStore.Video.Media._ID, };
    // String whereClause = MediaStore.Video.Media.DATA + " = '" + Videopath +
    // "'";
    // Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
    // projection, whereClause, null, null);
    // int _id = 0;
    // String videoPath = "";
    // if (cursor == null || cursor.getCount() == 0) {
    // return null;
    // }
    // if (cursor.moveToFirst()) {
    //
    // int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
    // int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
    // do {
    // _id = cursor.getInt(_idColumn);
    // videoPath = cursor.getString(_dataColumn);
    // } while (cursor.moveToNext());
    // }
    // cursor.close();
    // BitmapFactory.Options options = new BitmapFactory.Options();
    // options.inDither = false;
    // options.inPreferredConfig = Bitmap.Config.RGB_565;
    // Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id,
    // Images.Thumbnails.MINI_KIND, options);
    // return bitmap;
    // }

}
