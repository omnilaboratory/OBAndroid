package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.listItems.BackupFile;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FilesUtils {
    private String path;

    public static List<BackupFile> getDirectoryAndFile(String path, Context context) {
        List<BackupFile> fileInfoList = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if(tempList.length > 0){
            for (int i = 0; i < tempList.length; i++) {
                String filename = tempList[i].getName();
                long lastEdit = tempList[i].lastModified();
                String lastEditTime = TimeFormatUtil.formatTimeAndDateLong(lastEdit / 1000, context);
                BackupFile backupFile = null;
                if (!filename.equals("Android")) {
                    if (tempList[i].isDirectory()) {
                        backupFile = new BackupFile(false, "directory", filename, lastEditTime, file);
                    } else {
                        if (filename.endsWith(".db")) {
                            backupFile = new BackupFile(false, "db", filename, lastEditTime, file);
                        } else if (filename.endsWith(".OBBackupChannel")) {
                            backupFile = new BackupFile(false, "OBBackupChannel", filename, lastEditTime, file);
                        } else {
                            backupFile = new BackupFile(false, "else", filename, lastEditTime, file);
                        }
                    }
                    fileInfoList.add(backupFile);
                }
            }
        }else{
            fileInfoList.clear();
        }


        return fileInfoList;
    }

    public static List<BackupFile> getDirectory(String path, Context context) {
        List<BackupFile> fileInfoList = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            String filename = tempList[i].getName();
            long lastEdit = tempList[i].lastModified();
            String lastEditTime = TimeFormatUtil.formatTimeAndDateLong(lastEdit / 1000, context);
            BackupFile backupFile = null;
            int childCount = 0;
            if (tempList[i].listFiles() != null) {
                childCount = tempList[i].listFiles().length;
                if (!filename.equals("Android")) {
                    if (tempList[i].isDirectory()) {
                        backupFile = new BackupFile(false, "directory", filename, lastEditTime, file, childCount);
                    }
                    fileInfoList.add(backupFile);
                }
            }

        }
        return fileInfoList;
    }

    public static boolean fileIsExist(String path){
        try {
            File file = new File(path);
            if (!file.exists()){
                Log.e(path + "is exist","false");
                return false;
            }
        }catch (Exception e){
            Log.e(path + "is exist","false");
            return false;
        }
        Log.e(path + "is exist","true");
        return true;
    }

    public static long fileLastUpdate(String path){
        try {
            File file = new File(path);
            if (!file.exists()){
                Log.e(path + "is exist","false");
                return -1;
            }else{
                long lastModified = file.lastModified();
                return lastModified;
            }
        }catch (Exception e){
            Log.e(path + "is exist","false");
            return -1;
        }
    }

    public static boolean checkFileMd5Matched (String filePath,String fileMd5){
        boolean isMatched = false;
        String fileCheckMd5 = getFileMd5(filePath);
        if(fileCheckMd5.equals(fileMd5)){
            isMatched = true;
        }else{
            isMatched = false;
        }
        return isMatched;
    }

    public static String getFileMd5(String filePath){
        String fileMd5 = "";
        File file = new File(filePath);
        fileMd5 = getFileMD5(file);
        return fileMd5;
    };


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
