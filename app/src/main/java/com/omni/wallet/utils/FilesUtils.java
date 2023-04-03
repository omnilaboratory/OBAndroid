package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.listItems.BackupFile;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;


public class FilesUtils {
    private String path;
    private static final String TAG = FilesUtils.class.getSimpleName();
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

    public static long fileLastUpdate(String path){
        try {
            File file = new File(path);
            if (!file.exists()){
                Log.d(path + "is exist","false");
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

    public static boolean checkFileCRC32Matched(String filePath,String fileCRC32){
        boolean isMatched;
        String fileCheckCRC32 = getFileCRC32(filePath);
        if (fileCheckCRC32.equals(fileCRC32)){
            isMatched = true;
        }else{
            isMatched = false;
        }
        Log.d(TAG, "checkFileCRC32Matched: " + "filePath " + filePath + "fileCRC32 " + fileCRC32 + "fileCheckCRC32 " + fileCheckCRC32);
        return isMatched;
    }

    public static String getFileCRC32(String filePath){
        String fileCRC32Str = "";
        File file = new File(filePath);
        fileCRC32Str = getFileCRC32(file);
        return fileCRC32Str;
    }

    public static String getFileCRC32(File file){
        long fileCRC32 = -1;
        String fileCRC32Str = "";
        CRC32 crc32 = new CRC32();
        if (!file.isFile()) {
            return fileCRC32Str;
        }
        FileInputStream in;
        byte buffer[] = new byte[1024];
        try {
            in = new FileInputStream(file);
            int len = in.read(buffer, 0, 1024);
            while (len != -1) {
                crc32.update(buffer, 0, len);
                len = in.read(buffer, 0, 1024);
            }
            in.close();
            fileCRC32 = crc32.getValue();
            fileCRC32Str = Long.toHexString(fileCRC32).toLowerCase();
            return fileCRC32Str;
        } catch (Exception e) {
            e.printStackTrace();
            return fileCRC32Str;
        }
    }
}
