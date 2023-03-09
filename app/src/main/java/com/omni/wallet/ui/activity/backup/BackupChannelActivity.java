package com.omni.wallet.ui.activity.backup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.ActivityUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.listItems.BackupFile;
import com.omni.wallet.utils.BackupUtils;
import com.omni.wallet.utils.FilesUtils;
import com.omni.wallet.view.dialog.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class BackupChannelActivity extends AppBaseActivity {
    private static final String TAG = BackupChannelActivity.class.getSimpleName();
    private List<String> pathList = new ArrayList();
    @BindView(R.id.tv_path_show)
    TextView pathShow;
    @BindView(R.id.recycler_file_list)
    public RecyclerView mRecyclerViewDirectory;
    private List<BackupFile> directoryData = new ArrayList();
    private MyAdapter myAdapter;
    String selectedFilePath = "";
    LoadingDialog mLoadingDialog;
    String userSetBackupDirectory = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_backup_channel;
    }

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
        userSetBackupDirectory = User.getInstance().getChannelBackupPathArray(mContext);
        Log.d(TAG,userSetBackupDirectory);
        if(!userSetBackupDirectory.isEmpty()){
            String[] directoryArray =  userSetBackupDirectory.split(" ");
            for (int i = 0; i < directoryArray.length; i++) {
                pathList.add(directoryArray[i]);
                Log.d(TAG,directoryArray[i]);
            }
        }else{
            String storagePath = Environment.getExternalStorageDirectory() + "";
            pathList.add(storagePath);
        }
        String pathFull = "";
        for (int i = 0; i < pathList.size(); i++) {
            if (i == 0) {
                pathFull += pathList.get(i);
            } else {
                pathFull = pathFull + "/" + pathList.get(i);
            }
        }
        List<BackupFile> filesMap = FilesUtils.getDirectory(pathFull, mContext);
        for (BackupFile backupFile : filesMap) {
            String fileName = (String) backupFile.getFilename();
            Log.d("filename", fileName);
        }
        directoryData = filesMap;
        pathShow.setText(pathFull);
        initRecyclerView();
    }

    @Override
    protected void initData() {

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewDirectory.setLayoutManager(new LinearLayoutManager(mContext));
        myAdapter = new MyAdapter(mContext, directoryData, R.layout.layout_item_directory_file_list);
        mRecyclerViewDirectory.setAdapter(myAdapter);
    }

    public void updatePathView(String directory) {
        pathList.add(directory);
        Log.d("directoryList", pathList.toString());
        String pathFull = "";
        for (int i = 0; i < pathList.size(); i++) {
            if (i == 0) {
                pathFull += pathList.get(i);
            } else {
                pathFull = pathFull + "/" + pathList.get(i);
            }
        }
        List<BackupFile> filesMap = FilesUtils.getDirectory(pathFull, mContext);
        directoryData.clear();
        for (BackupFile backupFile : filesMap) {
            directoryData.add(backupFile);
        }
        pathShow.setText(pathFull);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });

    }

    @OnClick(R.id.btn_back_path)
    public void backPathView() {
        if (pathList.size() > 1) {
            pathList.remove(pathList.get(pathList.size() - 1));
            String pathFull = "";
            for (int i = 0; i < pathList.size(); i++) {
                if (i == 0) {
                    pathFull += pathList.get(i);
                } else {
                    pathFull = pathFull + "/" + pathList.get(i);
                }
            }
            List<BackupFile> filesMap = FilesUtils.getDirectory(pathFull, mContext);
            directoryData.clear();
            for (BackupFile backupFile : filesMap) {
                directoryData.add(backupFile);
            }
            pathShow.setText(pathFull);
            selectedFilePath = "";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            });
        } else {
            ToastUtils.showToast(mContext, "The directory is the root directory.");
        }

    }

    private class MyAdapter extends CommonRecyclerAdapter<BackupFile> {

        public MyAdapter(Context context, List<BackupFile> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, BackupFile item) {
            String filename = item.getFilename();
            String lastEdit = item.getLastEdit();
            Boolean isSelected = item.getSelected();
            holder.setText(R.id.tv_file_name, filename);
            holder.setText(R.id.tv_file_modify_time, lastEdit);
            if (isSelected) {
                holder.setViewVisibility(R.id.selected_iv, View.VISIBLE);
            } else {
                holder.setViewVisibility(R.id.selected_iv, View.INVISIBLE);
            }

            holder.setImageResource(R.id.iv_file_type, R.mipmap.icon_folder);
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isHasChildFile()) {
                        updatePathView(filename);
                    } else {
                        item.setSelected(true);
                        for (int i = 0; i < directoryData.size(); i++) {
                            if (i != position) {
                                directoryData.get(i).setSelected(false);
                            }
                        }
                        String path = "";
                        for (int j = 0; j < pathList.size(); j++) {
                            if (j == 0) {
                                path += pathList.get(j);
                            } else {
                                path = path + "/" + pathList.get(j);
                            }
                        }
                        selectedFilePath = path;
                        myAdapter.notifyDataSetChanged();
                    }

                }
            });

        }
    }
    
    @OnClick(R.id.btn_back)
    public void clickBtnBack(){
        ActivityUtils.getInstance().finishActivity(BackupChannelActivity.class);
    }
    
    @OnClick(R.id.btn_next)
    public void clickBtnNext(){
        mLoadingDialog.show();
        String arrayString = "";
        String path = "";
        for (int i = 0; i < pathList.size(); i++) {
            if(i>0){
                path = path + "/" + pathList.get(i);
                arrayString = arrayString + " " + pathList.get(i);
            }else{
                path = pathList.get(i);
                arrayString = pathList.get(i);
            }
        }

        User.getInstance().setChannelBackupPathArray(mContext,arrayString);
        LightningOuterClass.ChanBackupExportRequest chanBackupExportRequest = LightningOuterClass.ChanBackupExportRequest.newBuilder().build();
        Obdmobile.exportAllChannelBackups(chanBackupExportRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    String basePath = BackupUtils.getInstance().getBasePath();
                    String directoryName = BackupUtils.getInstance().getDirectoryName();
                    String channelFileName = BackupUtils.getInstance().getChannelFileName();
                    String directoryPath = "";
                    String userSettingDirectory =  BackupUtils.getInstance().getUserSettingDirectory(mContext);
                    if(userSettingDirectory.isEmpty()){
                        directoryPath = basePath + "/" + directoryName;
                    }else {
                        String[] userSettingDirectoryArray = userSettingDirectory.split(" ");
                        for (int i = 0; i < userSettingDirectoryArray.length; i++) {
                            if(i>0){
                                directoryPath = directoryPath + "/" + userSettingDirectoryArray[i];
                            }else{
                                directoryPath = userSettingDirectoryArray[i];
                            }
                        }
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
                    runOnUiThread(()->{
                        ToastUtils.showToast(mContext,"Set backup file save directory and backup file successful!");
                        if(mLoadingDialog.isShowing()){
                            mLoadingDialog.dismiss();
                        }
                    });
                } catch (InvalidProtocolBufferException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

