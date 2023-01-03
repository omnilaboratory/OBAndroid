package com.omni.wallet.utils;

import android.content.Context;
import android.os.Environment;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.baselibrary.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupUtils {
    private final static String basePath = Environment.getExternalStorageDirectory() + "";
    private final static String directoryName = "OBBackupDirectory";
    private final static String channelFileName = "channelBackupFile.OBBackupChannel";
    
    private BackupUtils(){
        
    }
    
    private static BackupUtils mInstance;
    
    public static BackupUtils getInstance(){
        if (mInstance == null){
            synchronized (BackupUtils.class){
                if (mInstance == null){
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
    
    public Boolean BackupChannelToFile(Context context){
        final boolean[] isBackupChannelFileOver = {false};
        LightningOuterClass.ExportChannelBackupRequest exportChannelBackupRequest = LightningOuterClass.ExportChannelBackupRequest.newBuilder().build();
        Obdmobile.exportAllChannelBackups(exportChannelBackupRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                isBackupChannelFileOver[0] = false;
                ToastUtils.showToast(context,"Backup channel failed,please backup channel later.");
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    String directoryPath = basePath + "/" +directoryName;
                    String channelFilePath = directoryPath + "/" + channelFileName;
                    File directoryFile = new File(directoryPath);

                    if (!directoryFile.exists()){
                        directoryFile.mkdir();
                    }

                    File channelFile = new File(channelFilePath);
                    if (channelFile.exists()){
                        channelFile.delete();
                    }
                    LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseFrom(bytes);
                    OutputStream outputStream = new FileOutputStream(channelFilePath);
                    chanBackupSnapshot.writeTo(outputStream);
                    isBackupChannelFileOver[0] = true;
                } catch (InvalidProtocolBufferException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return isBackupChannelFileOver[0];
    }
}
