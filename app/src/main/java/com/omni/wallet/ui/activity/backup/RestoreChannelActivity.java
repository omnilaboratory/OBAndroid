package com.omni.wallet.ui.activity.backup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.BackupFile;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.utils.FilesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class RestoreChannelActivity extends AppBaseActivity {
    private List<String> pathList = new ArrayList();
    
    @BindView(R.id.tv_path_show)
    TextView pathShow;
    @BindView(R.id.recycler_file_list)
    public RecyclerView mRecyclerViewDirectory;
    private List<BackupFile> directoryData = new ArrayList();
    private MyAdapter myAdapter;
    String selectedFilePath = "";

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }
    
    @Override
    protected int getContentView() {
        return R.layout.activity_restore_channel;
    }

    @Override
    protected void initView() {
        String storagePath = Environment.getExternalStorageDirectory() + "";
        pathList.add(storagePath);
        String pathFull = "";
        for (int i = 0;i<pathList.size();i++){
            if(i==0){
                pathFull += pathList.get(i);
            }else{
                pathFull = pathFull + "/" + pathList.get(i);
            }
        }
        List<BackupFile> filesMap = FilesUtils.getDirectoryAndFile(pathFull,mContext);
        for (BackupFile backupFile : filesMap){
            String fileName = (String) backupFile.getFilename();
            Log.e("filename",fileName);
        }
        directoryData = filesMap;
        pathShow.setText(pathFull);
        
        initRecyclerView();
    }

    @Override
    protected void initData() {

    }
    
    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewDirectory.setLayoutManager(new LinearLayoutManager(mContext));
        myAdapter = new MyAdapter(mContext, directoryData, R.layout.layout_item_directory_file_list);
        mRecyclerViewDirectory.setAdapter(myAdapter);
    }
    
    public void updatePathView(String directory){
        pathList.add(directory);
        Log.e("directoryList",pathList.toString());
        String pathFull = "";
        for (int i = 0;i<pathList.size();i++){
            if(i==0){
                pathFull += pathList.get(i);
            }else{
                pathFull = pathFull + "/" + pathList.get(i);
            }
        }
        List<BackupFile> filesMap = FilesUtils.getDirectoryAndFile(pathFull,mContext);
        directoryData.clear();
        for (BackupFile backupFile : filesMap){
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
    public void backPathView(){
        if(pathList.size()>1){
            pathList.remove(pathList.get(pathList.size()-1));
            String pathFull = "";
            for (int i = 0;i<pathList.size();i++){
                if(i==0){
                    pathFull += pathList.get(i);
                }else{
                    pathFull = pathFull + "/" + pathList.get(i);
                }
            }
            List<BackupFile> filesMap = FilesUtils.getDirectoryAndFile(pathFull,mContext);
            directoryData.clear();
            for (BackupFile backupFile : filesMap){
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
        }else{
            ToastUtils.showToast(mContext,"The directory is the root directory.");
        }
        
    }
    
    private class MyAdapter extends CommonRecyclerAdapter<BackupFile>{

        public MyAdapter(Context context, List<BackupFile> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, BackupFile item) {
            String filename =  item.getFilename();
            String fileType =  item.getFileType();
            String lastEdit =  item.getLastEdit();
            Boolean isSelected = item.getSelected();
            holder.setText(R.id.tv_file_name,filename);
            holder.setText(R.id.tv_file_modify_time,lastEdit);
            if (isSelected){
                holder.setViewVisibility(R.id.selected_iv,View.VISIBLE);
            }else{
                holder.setViewVisibility(R.id.selected_iv,View.INVISIBLE);
            }
            if(fileType == "directory"){
                holder.setImageResource(R.id.iv_file_type,R.mipmap.icon_folder);
                holder.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePathView(filename);
                    }
                });

            }else{
                if(fileType == "db"){
                    holder.setImageResource(R.id.iv_file_type,R.mipmap.icon_database);
                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtils.showToast(mContext,"The file type is wrong,please select the channel backup type file.");
                        }
                    });
                }else if(fileType == "OBBackupChannel"){
                    holder.setImageResource(R.id.iv_file_type,R.mipmap.icon_file);
                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!directoryData.get(position).getSelected()){
                                directoryData.get(position).setSelected(true);
                                for (int i = 0; i<directoryData.size();i++){
                                    if(i != position){
                                        directoryData.get(i).setSelected(false);
                                    }
                                }
                                String path = "";
                                for (int j = 0;j < pathList.size();j++){
                                    if(j==0){
                                        path += pathList.get(j);
                                    }else{
                                        path = path + "/" + pathList.get(j);
                                    }
                                }
                                path = path + "/" + filename;
                                selectedFilePath = path;
                                Log.e("selectedFile",selectedFilePath);
                                myAdapter.notifyDataSetChanged();
                            }else{
                                directoryData.get(position).setSelected(false);
                                for (int i = 0; i<directoryData.size();i++){
                                    if(i != position){
                                        directoryData.get(i).setSelected(false);
                                    }
                                }
                                String path = "";
                                for (int j = 0;j < pathList.size();j++){
                                    if(j==0){
                                        path += pathList.get(j);
                                    }else{
                                        path = path + "/" + pathList.get(j);
                                    }
                                }
                                selectedFilePath = path;
                                Log.e("selectedFile",selectedFilePath);
                                myAdapter.notifyDataSetChanged();
                            }
                            
                        }
                    });
                }else {
                    holder.setImageResource(R.id.iv_file_type,R.mipmap.icon_file);
                    holder.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtils.showToast(mContext,"The file type is wrong,please select the channel backup type file.");
                        }
                    });
                }
            }
        }
    }
    
    @OnClick(R.id.btn_back)
    public void clickBackButton(){
        finish();
    }
    
    @OnClick(R.id.btn_next)
    public void clickNextButton(){
        switchActivity(AccountLightningActivity.class);
    }
    
    @SuppressLint("LongLogTag")
    @OnClick(R.id.btn_restore)
    public void clickRestoreBtn(){
        try {
            File file = new File(selectedFilePath);
            Boolean isDirectory = file.isDirectory();
            if(isDirectory){
                ToastUtils.showToast(mContext,"Your path is a directory,please choose the file end with .OBBackupChannel!");
            }else{
                InputStream inputStream = new FileInputStream(file);
                LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseFrom(inputStream);
                LightningOuterClass.MultiChanBackup multiChanBackup =  chanBackupSnapshot.getMultiChanBackup();
                LightningOuterClass.ChannelBackups channelBackups = chanBackupSnapshot.getSingleChanBackups();

                LightningOuterClass.RestoreChanBackupRequest restoreChanBackupRequest = LightningOuterClass.RestoreChanBackupRequest.newBuilder()
                        .setMultiChanBackup(multiChanBackup.getMultiChanBackup())
                        .setChanBackups(channelBackups)
                        .build();
                Log.e("multi Channel restoreChanBackupRequest Str", String.valueOf(restoreChanBackupRequest));
                Obdmobile.restoreChannelBackups(restoreChanBackupRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        Log.e("restore string","restore failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.e("restore string","restore success");
                        ToastUtils.showToast(mContext,"The channels are recover successfully!");
                        if(bytes==null){
                            return;
                        }

                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
