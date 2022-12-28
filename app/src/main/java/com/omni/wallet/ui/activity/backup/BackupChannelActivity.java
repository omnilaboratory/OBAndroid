package com.omni.wallet.ui.activity.backup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.BackupFile;
import com.omni.wallet.ui.activity.channel.ChannelsActivity;
import com.omni.wallet.utils.FilesUtils;

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
    private List<String> pathList = new ArrayList();

    @BindView(R.id.tv_path_show)
    TextView pathShow;
    @BindView(R.id.recycler_file_list)
    public RecyclerView mRecyclerViewDirectory;
    private List<BackupFile> directoryData = new ArrayList();
    private MyAdapter myAdapter;
    String selectedFilePath = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_restore_channel;
    }

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected void initView() {
        String storagePath = Environment.getExternalStorageDirectory() + "";
        pathList.add(storagePath);
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
            Log.e("filename", fileName);
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
        Log.e("directoryList", pathList.toString());
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
        finish();
    }
    
    @OnClick(R.id.btn_next)
    public void clickBtnNext(){

        LightningOuterClass.ExportChannelBackupRequest exportChannelBackupRequest = LightningOuterClass.ExportChannelBackupRequest.newBuilder().build();

        Obdmobile.exportAllChannelBackups(exportChannelBackupRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(byte[] bytes) {
                if(bytes==null){
                    return;
                }
                try {
                    LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseFrom(bytes);

                    String filename = "channelBackupFile.OBBackupChannel";
//                    String path = Environment.getExternalStorageDirectory() + "/" + filename;
                    String newPath =  "";
                    
                    for (int i = 0; i < pathList.size(); i++) {
                        if (i == 0) {
                            newPath += pathList.get(i);
                        } else {
                            newPath = newPath + "/" + pathList.get(i);
                        }
                    };
                    String path = newPath + "/" + filename;
                    Log.e("channelBackUpPath",path);
                    File file = new File(path);
                    if(file.exists()){
                        file.delete();
                    }
                    OutputStream outputStream = new FileOutputStream(path);
                    chanBackupSnapshot.writeTo(outputStream);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String newPath =  "";

                            for (int i = 0; i < pathList.size(); i++) {
                                if (i == 0) {
                                    newPath += pathList.get(i);
                                } else {
                                    newPath = newPath + "/" + pathList.get(i);
                                }
                            };
                            ToastUtils.showToast(mContext,"Backup channels success,your backup file is already created in " + newPath);
                            switchActivity(ChannelsActivity.class);

                        }
                    });
                    

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

