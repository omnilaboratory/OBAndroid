package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class ObdLogFileObserver extends FileObserver {
    Context ctx;
    SharedPreferences blockDataSharedPreferences;
    SharedPreferences.Editor blockDataEditor;
    String filePath = "";
    public ObdLogFileObserver(String path) {
        super(path);
    }
    
    @SuppressLint("LongLogTag")
    public ObdLogFileObserver(String path, Context ctx){
        super(path);
        this.ctx = ctx;
        this.blockDataSharedPreferences = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        this.blockDataEditor = blockDataSharedPreferences.edit();
        this.filePath = path;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onEvent(int event, @Nullable String path) {
        int currentHeight =  blockDataSharedPreferences.getInt("currentBlockHeight",0);
        if(event == FileObserver.MODIFY){
            BufferedReader bfr;
            try {
                bfr = new BufferedReader(new FileReader(filePath));
                String line;
                line = bfr.readLine();
                StringBuilder sb = new StringBuilder();
                while(line!=null){
                    String oldLine = line;
                    sb.append(line);
                    sb.append("\n");
                    line = bfr.readLine();
                    if(line!=null){
                        if(checkString(oldLine)){
                            Log.e("---------------Updated Current Block Height------------------",oldLine);
                            String stringHeight = oldLine.split("height")[1].split(",")[0];
                            int syncingHeight = Integer.parseInt(stringHeight.trim());
                            if(syncingHeight>currentHeight){
                                blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                blockDataEditor.commit();
                            }
                        }else if(checkSyncedOver(oldLine)){
                            Log.e("---------------Updated Current Block Height------------------",oldLine);
                            String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                            int syncingHeight = Integer.parseInt(stringHeight.trim());
                            if(syncingHeight>currentHeight){
                                blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                blockDataEditor.commit();
                            }
                        }
                    }else{
                        if(checkString(oldLine)){
                            Log.e("---------------Updated Current Block Height------------------",oldLine);
                            String stringHeight = oldLine.split("height")[1].split(",")[0];
                            int syncingHeight = Integer.parseInt(stringHeight.trim());
                            if(syncingHeight>currentHeight){
                                blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                blockDataEditor.commit();
                            }
                        }else if(checkSyncedOver(oldLine)){
                            Log.e("---------------Updated Current Block Height------------------",oldLine);
                            String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                            int syncingHeight = Integer.parseInt(stringHeight.trim());
                            if(syncingHeight>currentHeight){
                                blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                blockDataEditor.commit();
                            }
                        }
                        bfr.close();
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        
    }
    
    
    @SuppressLint("LongLogTag")
    private Boolean checkString(String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Catching up block hashes to height \\d+, this might take a while.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
    }

    @SuppressLint("LongLogTag")
    private Boolean checkSyncedOver(String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Chain backend is fully synced \\(end_height=\\d+\\)!.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
    }
}