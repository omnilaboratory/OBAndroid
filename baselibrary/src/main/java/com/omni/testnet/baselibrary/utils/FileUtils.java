package com.omni.testnet.baselibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class FileUtils {


    /**
     * 根据URI获取文件的绝对路径
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 文件转字节数组 base64 加密
     */
    public static byte[] getBufferByBase64(String path) {
        byte[] cover = getBuffer(path);
        return Base64.encodeToString(cover, Base64.DEFAULT).getBytes();
    }

    public static boolean exists(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File f = new File(path);
        return f.exists();
    }

    /**
     * 判断路径是否存在，如果不存在则创建
     */
    public static void mkdirs(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 文件转字符串 编码(做Base64加密)
     */
    public static String FileToStringBase64(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "";
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            String result = Base64.encodeToString(buffer, Base64.DEFAULT);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 文件转字符串编码(不做Base64加密)
     */
    public static String FileToString(String path) {
        byte[] fileByte = getBuffer(path);
        if (fileByte != null) {
            return new String(fileByte);
        }
        return "";
    }

    /**
     * 文件转字符串编码(不做Base64加密), 设置编码
     */
    public static String FileToString(String path, String charsetName) {
        byte[] fileByte = getBuffer(path);
        if (fileByte != null) {
            try {
                return new String(fileByte, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 文件转字节数组
     */
    public static byte[] getBuffer(String path) {
        File file;
        FileInputStream fis = null;
        byte[] cover = null;
        try {
            file = new File(path);
            if (file.exists()) {
                int length = (int) file.length();
                fis = new FileInputStream(file);
                cover = new byte[length];
                fis.read(cover, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCloseable(fis);
        }
        return cover;
    }

    /**
     * 字符串 保存到文件
     */
    public static void StringToFile(String s, String path) {
        ByteToFile(s.getBytes(), path);
    }

    /**
     * byte 保存到文件
     */
    public static void ByteToFile(byte[] b, String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        FileOutputStream fos = null;
        try {
            File f = new File(path);
            mkdirs(f.getParentFile().getPath());
            if (f.exists()) {
                boolean delResult = f.delete();
                LogUtils.e(TAG, "删除文件" + path + "：" + delResult);
            }
            // 在文件系统中根据路径创建一个新的空文件
            boolean createResult = f.createNewFile();
            LogUtils.e(TAG, "创建空文件" + path + "：" + createResult);
            fos = new FileOutputStream(f);
            fos.write(b);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeCloseable(fos);
        }
    }

    /**
     * String写入文件
     */
    public static void writeStrToFile(String str, String path) throws IOException {
        writeStrToFile(str, path, "UTF-8");
    }

    /**
     * String写入文件
     */
    public static void writeStrToFile(String str, String path, String charsetEncoder) throws IOException {
        File f = new File(path);
        mkdirs(f.getParentFile().getPath());
        if (f.exists()) {
            f.delete();
        }
        // 在文件系统中根据路径创建一个新的空文件
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        Writer os = new OutputStreamWriter(fos, charsetEncoder);
        os.write(str);
        os.flush();
        fos.close();
    }

    /**
     * 读取文件
     */
    public static String readText(String filePath) throws IOException {
        String line = "";
        String encoding = "UTF-8";
        File file = new File(filePath);
        String temp = "";
        if (file.exists() && file.isFile()) {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((line = bufferedReader.readLine()) != null) {
                temp += line;
            }
            bufferedReader.close();
        } else {
            LogUtils.e(TAG, "找不到文件");
        }
        return temp;
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(String oldPath, String newPath) {

        boolean result = false;
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return result;
        }

        File parentFile = newFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            newFile.getParentFile().mkdirs();
        }
        if (newFile.exists()) {
            newFile.delete();
        }
        FileInputStream fosFrom = null;
        FileOutputStream fosTo = null;
        try {
            fosFrom = new FileInputStream(oldFile);
            fosTo = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024 * 4];
            int length;
            while ((length = fosFrom.read(buffer)) != -1) {
                fosTo.write(buffer, 0, length);
            }
            fosTo.flush();
            result = true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeCloseable(fosTo);
            closeCloseable(fosFrom);
        }
        return result;

    }

    /**
     * 关闭stream or reader
     *
     * @param closeObj
     */
    public static void closeCloseable(Closeable closeObj) {
        try {
            if (null != closeObj) {
                closeObj.close();
                closeObj = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    /**
     * 删除目录及内部所有东西
     */
    public static void delDir(File file) {
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            for (File f : fs) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    delDir(f);
                    f.delete();
                }
            }
            file.delete();
        } else if (file.isFile()) {
            file.delete();
        }
    }

    /**
     * 删除目录及内部所有东西
     *
     * @param path 路径
     */
    public static void delDir(String path) {
        delDir(new File(path));
    }

    /**
     * 删除文件
     */
    public static boolean delFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除指定目录下以指定字符开头的文件
     */
    public static boolean delFileInDir(String startFileName, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.getName().startsWith(startFileName) && f.isFile()) {
                    f.delete();
                }
            }
            return true;
        } else {
            return false;
        }
    }

//    /**
//     * @param path   需要分隔的文件路径
//     * @param length 每个包的大小长度
//     * @return List<String> 分隔之后得到的字符串集合
//     * @throws IOException
//     * @Title: gZipAndcutFileLength
//     * @Description:将文件Gzip压缩之后分割为不大于1024的List<byte[]>
//     * @author eye_fa
//     */
//    public static List<byte[]> gZipAndcutFileLength(String path, int length) throws IOException {
//        List<byte[]> result = new ArrayList<byte[]>();
//        byte[] byteArray = getBuffer(path);
//        if (byteArray != null) {
//            byte[] gzipArray = GzipUtil.execGzip(byteArray);
//            int countSize = 0;
//            if (gzipArray.length > length) {
//                while (length < gzipArray.length) {
//                    byte[] tempArray = new byte[length];
//                    if (gzipArray.length - countSize > length) {
//                        tempArray = Arrays.copyOfRange(gzipArray, countSize, countSize + length);
//                    } else {
//                        tempArray = Arrays.copyOfRange(gzipArray, countSize, gzipArray.length);
//                        length = gzipArray.length;
//                    }
//                    countSize += tempArray.length;
//                    result.add(tempArray);
//                    tempArray = null;
//                }
//            } else {
//                result.add(gzipArray);
//            }
//            // String tempStr = Base64.encodeToString(gzipArray,
//            // Base64.DEFAULT);
//            // while (length < tempStr.length()) {
//            // result.add(tempStr.substring(0, length));
//            // tempStr = tempStr.substring(length, tempStr.length());
//            // }
//            // result.add(tempStr);
//            return result;
//        } else {
//            return null;
//        }
//    }

    /**
     * 根据指定文件名开头和文件路径判断该文件夹下是否存在这个文件
     */
    public static boolean isExistsFile(String path, String startFileName) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile() && f.length() > 0) {
                    if (f.getName().startsWith(startFileName)) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     */
    public static String readAssetsText(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 判断文件是否存在如果不存在则 拷贝
     */
    public static void CheckExistsAndCopy(Context c, File f) {
        if (!f.exists()) {
            copyFileFromAssets(c, f);
        }
    }

    /**
     * 拷贝assets下的文件到指定目录
     */
    public static void copyFileFromAssets(Context c, File f) {

        try {
            AssetManager am = c.getAssets();
            InputStream is = am.open(f.getName());
            FileOutputStream fos = new FileOutputStream(f.getParent() + File.separator + f.getName());

            byte[] buf = new byte[1024];
            int len = 0;
            len = is.read(buf);
            while (len != -1) {
                fos.write(buf, 0, len);
                len = is.read(buf);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(String fileAbs) {
        if (!StringUtils.isEmpty(fileAbs)) {
            File file = new File(fileAbs);
            if (file.exists()) {
                return file.length();
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 获取文件长度，并且格式化 ps:用于显示文件大小
     */
    public static String getFileLength(String fileAbs) {
        if (!StringUtils.isEmpty(fileAbs)) {
            File file = new File(fileAbs);
            if (file.exists()) {
                return getFormatSize(file.length());
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 格式化文件大小
     */
    public static String getFormatSize(double size) {
        if (size <= 0) {
            return 0 + "B";
        }
        double kiloByte = size / 1024;
        if (kiloByte <= 1) {
            // return size + "Byte(s)";
            return size + "B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }


//    /**
//     * 发送通知，通知系统文件变动了
//     */
//    public static void notifyFileChanged(Context context) {
//        Uri fileUri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
//        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, fileUri);
//        context.sendBroadcast(intent);
//    }

    /**
     * 重命名文件
     */
    public static boolean renameFile(File file, String newName) {
        if (file == null || StringUtils.isEmpty(newName)) {
            return false;
        }
        String path = file.getParentFile().getAbsolutePath();
        File newFile = new File(path, newName);
        return file.renameTo(newFile);
    }

    /**
     * 将资源目录的数据库copy到本地
     */
    public static void copyDatabaseToLocal(Context context, String dbPath, String dbName) {
        try {
            String outFileName = dbPath + File.separator + dbName;
            File file = new File(dbPath);
            if (!file.mkdirs()) {
                file.mkdirs();
            }
            File dataFile = new File(outFileName);
            if (dataFile.exists()) {
                dataFile.delete();
            }
            InputStream myInput;
            myInput = context.getAssets().open(dbName);
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.toString());
        }
    }
}
