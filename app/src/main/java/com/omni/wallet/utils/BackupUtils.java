package com.omni.wallet.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.framelibrary.entity.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lnrpc.LightningOuterClass;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

public class BackupUtils {
    private final static String TAG = BackupUtils.class.getSimpleName();
    private final static String basePath = Environment.getExternalStorageDirectory() + "";
    private final static String directoryName = "OBBackup";
    private final static String channelFileName = "channelBackupFile.OBBackupChannel";

    private BackupUtils() {

    }

    private static BackupUtils mInstance;

    public static BackupUtils getInstance() {
        if (mInstance == null) {
            synchronized (BackupUtils.class) {
                if (mInstance == null) {
                    mInstance = new BackupUtils();
                }
            }
        }
        return mInstance;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getChannelFileName() {
        return channelFileName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public String getUserSettingDirectory(Context context){
        String channelBackupPath = User.getInstance().getChannelBackupPathArray(context);
        return channelBackupPath;
    }

    public void backupChannelToFile(Context context) {
        Obdmobile.subscribeChannelBackups(null, new RecvStream() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    String directoryPath = "";
                    String userSettingDirectory =  getUserSettingDirectory(context);
                    Log.d(TAG,userSettingDirectory);
                    if(userSettingDirectory.isEmpty()){
                        directoryPath = basePath + "/" + directoryName;
                    }else {
                        directoryPath = userSettingDirectory;
                    }
                    String channelFilePath = directoryPath + "/" + channelFileName;
                    File directoryFile = new File(directoryPath);
                    if (!directoryFile.exists()) {
                        directoryFile.mkdir();
                    }

                    File channelFile = new File(channelFilePath);
                    if (channelFile.exists()) {
                        channelFile.delete();
                    }
                    channelFile.canWrite();
                    channelFile.canRead();
                    LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseFrom(bytes);
                    LightningOuterClass.ChanBackupSnapshot newChanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.newBuilder()
                            .setMultiChanBackup(chanBackupSnapshot.getMultiChanBackup())
                            .build();
                    OutputStream outputStream = new FileOutputStream(channelFile);
                    newChanBackupSnapshot.writeDelimitedTo(outputStream);

                } catch (InvalidProtocolBufferException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
