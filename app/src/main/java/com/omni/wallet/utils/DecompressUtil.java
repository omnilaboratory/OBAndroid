package com.omni.wallet.utils;

import com.omni.wallet.baselibrary.utils.LogUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 汉: 解压工具类
 * En: DecompressUtil
 * author: guoyalei
 * date: 2023/5/10
 */
public class DecompressUtil {

    /**
     * tar.gz解压缩
     */
    public static void doUnTarGz(File srcfile, String destpath)
            throws IOException {
        byte[] buf = new byte[1024];
        FileInputStream fis = new FileInputStream(srcfile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        GzipCompressorInputStream cis = new GzipCompressorInputStream(bis);
        TarArchiveInputStream tais = new TarArchiveInputStream(cis);
        TarArchiveEntry tae = null;
        int pro = 0;
        while ((tae = tais.getNextTarEntry()) != null) {
            File f = new File(destpath + "/" + tae.getName());
            if (tae.isDirectory()) {
                f.mkdirs();
            } else {
                /*
                 * 父目录不存在则创建
                 */
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int len;
                while ((len = tais.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                bos.flush();
                bos.close();
            }
        }
        tais.close();
    }

    /**
     * GZip解压，tar解包
     *
     * @param srcFile 待压缩的文件或文件夹
     * @param dstDir  压缩至该目录，保持原文件名，后缀改为zip
     */
    public static void untarGZip(File srcFile, String dstDir) {
        File file = new File(dstDir);
        //需要判断该文件存在，且是文件夹
        if (!file.exists() || !file.isDirectory()) file.mkdirs();
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        GzipCompressorInputStream gcis = null;
        TarArchiveInputStream tais = null;
        try {
            fis = new FileInputStream(srcFile);
            gcis = new GzipCompressorInputStream(fis);
            tais = new TarArchiveInputStream(gcis);
            TarArchiveEntry tarArchiveEntry;
            int len = 0;
            while ((tarArchiveEntry = tais.getNextTarEntry()) != null) {
                File f = new File(dstDir + File.separator + tarArchiveEntry.getName());
                if (tarArchiveEntry.isDirectory()) f.mkdirs();
                else {
                    File parent = f.getParentFile();
                    if (!parent.exists()) parent.mkdirs();
                    FileOutputStream fos = new FileOutputStream(f);
                    while ((len = tais.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                //关闭数据流的时候要先关闭外层，否则会报Stream Closed的错误
                if (tais != null) tais.close();
                if (gcis != null) gcis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ZipFolder(String srcFileString, String zipFileString) {
        //创建ZIP
        try {
            ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(new File(zipFileString)));
            //创建文件
            File file = new File(srcFileString);
            //压缩
            ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
            //完成和关闭
            outZip.finish();
            outZip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) {
        try {
            if (zipOutputSteam == null)
                return;
            File file = new File(folderString + fileString);
            if (file.isFile()) {
                ZipEntry zipEntry = new ZipEntry(fileString);
                FileInputStream inputStream = new FileInputStream(file);
                zipOutputSteam.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
                zipOutputSteam.closeEntry();
            } else {
                //文件夹
                String fileList[] = file.list();
                //没有子文件和压缩
                if (fileList.length <= 0) {
                    ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                    zipOutputSteam.putNextEntry(zipEntry);
                    zipOutputSteam.closeEntry();
                }
                //子文件和递归
                for (int i = 0; i < fileList.length; i++) {
                    ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压zip压缩文件到指定目录
     *
     * @param zipPath
     */
    public static boolean unzipFile(String zipPath, String tempFileName) {
        try {
            LogUtils.e("DecompressUtil", "开始解压的文件：" + zipPath + "," + "解压的目标路径：" + tempFileName);
            File file = new File(tempFileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            InputStream inputStream = new FileInputStream(zipPath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) { //如果是一个文件
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1); //截取文件的名字 去掉原文件夹名字
                    file = new File(tempFileName + File.separator + fileName); //放到新的解压的文件路径
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            File zipFile = new File(zipPath);
            zipFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}