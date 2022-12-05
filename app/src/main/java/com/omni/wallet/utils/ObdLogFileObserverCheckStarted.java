package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class ObdLogFileObserverCheckStarted extends FileObserver {
    Context ctx;
    SharedPreferences blockDataSharedPreferences;
    SharedPreferences.Editor blockDataEditor;
    String filePath = "";
    int totalBlock = 0;
    
    public ObdLogFileObserverCheckStarted(String path) {
        super(path);
    }

    @SuppressLint("LongLogTag")
    public ObdLogFileObserverCheckStarted(String path, Context ctx,int totalBlock){
        super(path);
        this.ctx = ctx;
        this.blockDataSharedPreferences = ctx.getSharedPreferences("blockData",MODE_PRIVATE);
        this.blockDataEditor = blockDataSharedPreferences.edit();
        this.filePath = path;
        this.totalBlock = totalBlock;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onEvent(int event, @Nullable String path) {
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
                        if(checkString(oldLine,totalBlock)){
                            String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                            if(Integer.parseInt(stringHeight)>=totalBlock){
                                Boolean isOpen = blockDataSharedPreferences.getBoolean("isOpened",false);
                                if(!isOpen){
//                                    Log.e("--------------------isOpenChange-------------------","1");
                                    blockDataEditor.putBoolean("isOpened",true);
                                    blockDataEditor.commit();
                                    
                                }
                            }
                        }
                    }else{
                        if(checkString(oldLine,totalBlock)){
                            String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                            if(Integer.parseInt(stringHeight)>=totalBlock){
                                Boolean isOpen = blockDataSharedPreferences.getBoolean("isOpened",false);
                                if(!isOpen){
//                                    Log.e("--------------------isOpenChange-------------------","2");
                                    blockDataEditor.putBoolean("isOpened",true);
                                    blockDataEditor.commit();

                                }
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
    private Boolean checkString(String logLine,int block){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Chain backend is fully synced \\(end_height=\\d+\\)!.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
    }
}
