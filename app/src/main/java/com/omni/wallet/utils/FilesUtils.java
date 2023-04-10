package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.listItems.BackupFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;


public class FilesUtils {
    private static final String TAG = FilesUtils.class.getSimpleName();

    public FilesUtils() {
    }

    public static List<BackupFile> getDirectoryAndFile(String path, Context context) {
        List<BackupFile> fileInfoList = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if(tempList.length > 0){
            for (File value : tempList) {
                String filename = value.getName();
                long lastEdit = value.lastModified();
                String lastEditTime = TimeFormatUtil.formatTimeAndDateLong(lastEdit / 1000, context);
                BackupFile backupFile;
                if (!filename.equals("Android")) {
                    if (value.isDirectory()) {
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
        for (File value : tempList) {
            String filename = value.getName();
            long lastEdit = value.lastModified();
            String lastEditTime = TimeFormatUtil.formatTimeAndDateLong(lastEdit / 1000, context);
            BackupFile backupFile = null;
            int childCount;
            if (value.listFiles() != null) {
                childCount = value.listFiles().length;
                if (!filename.equals("Android")) {
                    if (value.isDirectory()) {
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
                return file.lastModified();
            }
        }catch (Exception e){
            Log.e(path + "is exist","false");
            return -1;
        }
    }

    static boolean checkFileCRC32Matched(String filePath, String fileCRC32){
        boolean isMatched;
        String fileCheckCRC32 = getFileCRC32(filePath);
        isMatched = fileCheckCRC32.equals(fileCRC32);
        Log.d(TAG, "checkFileCRC32Matched: " + " filePath " + filePath + " fileCRC32 " + fileCRC32 + " fileCheckCRC32 " + fileCheckCRC32);
        return isMatched;
    }

    private static String getFileCRC32(String filePath){
        String fileCRC32Str;
        File file = new File(filePath);
        fileCRC32Str = get8FileCRC32(file);
        return fileCRC32Str;
    }

    // TODO: 2023/4/6 change return string for check file secret
    private static String get8FileCRC32(File file){
        String file8CRC32Str;
        String fileCRC32Str = getFileCRC32(file);
        int stringLength = fileCRC32Str.length();
        if (stringLength<8){
            StringBuilder head = new StringBuilder();
            for (int i =0 ;i< 8 -stringLength;i++){
                head.append("0");
            }
            file8CRC32Str = head + fileCRC32Str;
        }else{
            file8CRC32Str = fileCRC32Str;
        }
        return file8CRC32Str;
    }

    private static String getFileCRC32(File file){
        long fileCRC32;
        String fileCRC32Str = "";
        CRC32 crc32 = new CRC32();
        if (!file.isFile()) {
            return fileCRC32Str;
        }
        FileInputStream in;
        byte[] buffer = new byte[1024];
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
