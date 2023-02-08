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
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.listItems.BackupFile;
import com.omni.wallet.ui.activity.AccountLightningActivity;
import com.omni.wallet.ui.activity.recoverwallet.RecoverWalletStepTwoActivity;
import com.omni.wallet.utils.FilesUtils;
import com.omni.wallet.utils.PublicUtils;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import lnrpc.Stateservice;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

public class RestoreChannelActivity extends AppBaseActivity {
    String TAG = RestoreChannelActivity.class.getSimpleName();
    private List<String> pathList = new ArrayList();
    
    @BindView(R.id.tv_path_show)
    TextView pathShow;
    @BindView(R.id.recycler_file_list)
    public RecyclerView mRecyclerViewDirectory;
    private List<BackupFile> directoryData = new ArrayList();
    private MyAdapter myAdapter;
    String selectedFilePath = "";
    String directoryName = "OBBackup";
    LoadingDialog mLoadingDialog;

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
        EventBus.getDefault().register(this);
        mLoadingDialog = new LoadingDialog(mContext);
        String storagePath = Environment.getExternalStorageDirectory() + "";

        File file = new File(storagePath + "/" + directoryName);
        pathList.add(storagePath);
        Log.e(TAG,storagePath);
        Log.e(TAG,directoryName);
        if(file.exists()){
            pathList.add(directoryName);
        }
        Log.e(TAG,pathList.toString());
        String pathFull = "";
        for (int i = 0;i<pathList.size();i++){
            if(i==0){
                pathFull += pathList.get(i);
            }else{
                pathFull = pathFull + "/" + pathList.get(i);
            }
        }
        Log.e(TAG,pathFull);
        pathShow.setText(pathFull);
        List<BackupFile> filesMap = FilesUtils.getDirectoryAndFile(pathFull,mContext);
        if (!filesMap.isEmpty()){
            directoryData = filesMap;
        }
        initRecyclerView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            selectedFilePath = pathFull;
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
        mLoadingDialog.show();
        File file = new File(selectedFilePath);
        Log.e(TAG,selectedFilePath);
        Boolean isDirectory = file.isDirectory();
        if(!isDirectory){
            InputStream inputStream = null;
            LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = null;
            if (file.exists()){
                try {
                    inputStream = new FileInputStream(file);
                    chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseDelimitedFrom(inputStream);
                    Obdmobile.verifyChanBackup(chanBackupSnapshot.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            if(e.getMessage().equals("rpc error: code = Unknown desc = invalid single channel backup: chacha20poly1305: message authentication failed")){
                                runOnUiThread(()->{
                                    PublicUtils.closeLoading(mLoadingDialog);
                                    ToastUtils.showToast(mContext,"The file authenticated failed,please make sure the file and your wallet is matched!");
                                });
                            }else if(e.getMessage().trim().equals("rpc error: code = Unknown desc = only one Single is accepted at a time")){
                                runOnUiThread(()->{
                                    PublicUtils.closeLoading(mLoadingDialog);
                                    ToastUtils.showToast(mContext,e.getMessage());
                                });
                            }else if(e.getMessage().equals("rpc error: code = Unknown desc = invalid multi channel backup: chacha20poly1305: message authentication failed")){
                                runOnUiThread(()->{
                                    PublicUtils.closeLoading(mLoadingDialog);
                                    ToastUtils.showToast(mContext,"The file authenticated failed,please make sure the file and your wallet is matched!");
                                });
                            }else{
                                runOnUiThread(()->{
                                    PublicUtils.closeLoading(mLoadingDialog);
                                    Log.e(TAG,e.getMessage());
                                });

                            }
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            runOnUiThread(()->{
                                PublicUtils.closeLoading(mLoadingDialog);
                                Log.e(TAG,"snapShot is verified");
                                restoreChannel();
                            });

                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                PublicUtils.closeLoading(mLoadingDialog);
                User.getInstance().setRestoredChannel(mContext,true);
                switchActivityFinish(AccountLightningActivity.class);
            }
        }else{
            PublicUtils.closeLoading(mLoadingDialog);
            User.getInstance().setRestoredChannel(mContext,true);
            switchActivityFinish(AccountLightningActivity.class);
        }
        
    }


    public void restoreChannel(){
        mLoadingDialog.show();
        File file = new File(selectedFilePath);
        Boolean isDirectory = file.isDirectory();
        if(isDirectory){
            PublicUtils.closeLoading(mLoadingDialog);
            ToastUtils.showToast(mContext,"Your path is a directory,please choose the file end with .OBBackupChannel!");
        }else{
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                LightningOuterClass.ChanBackupSnapshot chanBackupSnapshot = LightningOuterClass.ChanBackupSnapshot.parseDelimitedFrom(inputStream);
                Log.d(TAG,chanBackupSnapshot.toString());
                LightningOuterClass.MultiChanBackup multiChanBackup =  chanBackupSnapshot.getMultiChanBackup();

                LightningOuterClass.RestoreChanBackupRequest restoreChanBackupRequest = LightningOuterClass.RestoreChanBackupRequest.newBuilder()
                        .setMultiChanBackup(multiChanBackup.getMultiChanBackup())
                        .build();
                Log.e(TAG, "multi Channel restoreChanBackupRequest Str" + String.valueOf(restoreChanBackupRequest));
                Obdmobile.restoreChannelBackups(restoreChanBackupRequest.toByteArray(), new Callback() {
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(()->{
                            PublicUtils.closeLoading(mLoadingDialog);
                            ToastUtils.showToast(mContext,"Please use the right file!");
                            Log.e("restore string","restore failed");
                        });
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        Log.e("restore string","restore success");
                        runOnUiThread(()->{
                            PublicUtils.closeLoading(mLoadingDialog);
                            ToastUtils.showToast(mContext,"The channels are recover successfully!");
                            User.getInstance().setRestoredChannel(mContext,true);
                            User.getInstance().setInitWalletType(mContext,"initialed");
                            switchActivityFinish(AccountLightningActivity.class);
                        });
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
