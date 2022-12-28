package com.omni.wallet.utils;

import android.content.Context;

import com.omni.wallet.listItems.BackupFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class FilesUtils {
    private String path;

    public static List<BackupFile> getDirectoryAndFile(String path, Context context) {
        List<BackupFile> fileInfoList = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
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
}
