package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.LogUtils;

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

public class BlockReaderUtil {
    private Context ctx;
    
    public BlockReaderUtil(Context ctx){
        this.ctx = ctx;
    }

    public void getTotalBlockHeight (){
        String jsonStr = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"omni_getinfo\", \"params\": []}";
        HttpUtils.with(ctx)
                .postString()
                .url("http://43.138.107.248:18332")
                .addContent(jsonStr)
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {

                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e("-----------TotalBlockHeightResult-----------------", result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                            String block = jsonObject1.getString("block");
                            SharedPreferences blockData = ctx.getSharedPreferences("BlockData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = blockData.edit();
                            editor.putString("totalBlockHeight",block);
                            editor.commit();
                            getCatchingBlock();
                            LogUtils.e("-----------TotalBlockHeight-----------------", block);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
    }

    @SuppressLint("LongLogTag")
    public void getCatchingBlock()  {
        SharedPreferences blockData = ctx.getSharedPreferences("BlockData", MODE_PRIVATE);
        SharedPreferences.Editor editor = blockData.edit();
        
        int catchingBlock = 0;
        String fileLocal = String.valueOf(ctx.getExternalCacheDir()) + "/logs/bitcoin/regtest/lnd.log";
        Log.e("----------------fileLocal-------------------",fileLocal);
        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader(fileLocal));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = null;
        try {
            line = bfr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while (line!=null){
            sb.append(line);
            sb.append("\n");
            try {
                line = bfr.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(line != null){
//                Get catching block height from log
                if(checkString(line)){
                    Log.e("-----------------logOutputContent-----------------",line);
                    String stringHeight = line.split("height")[1].split(",")[0];
                    catchingBlock = Integer.parseInt(stringHeight.trim());
                    Log.e("-----------------catchingHeight-----------------",stringHeight);
                }else if(checkSyncedOver(line)){
                    Log.e("-----------------logOutputContent-----------------",line);
                    Log.e("-----------------logOutputContent-----------------","synced over");
                    String stringHeight = line.split("height=")[1].split("\\)")[0];
                    catchingBlock = Integer.parseInt(stringHeight.trim());
                    Log.e("---------------usefulCatchingBlock--------------", String.valueOf(catchingBlock));
                    editor.putString("currentBlockHeight", String.valueOf(catchingBlock));
                    editor.commit();
                    try {
                        bfr.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                try {
                    Log.e("---------------usefulCatchingBlock--------------", String.valueOf(catchingBlock));
                    editor.putString("currentBlockHeight", String.valueOf(catchingBlock));
                    editor.commit();
                    bfr.close();
                    try {
                        Thread.sleep(5000);
                        getCatchingBlock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    @SuppressLint("LongLogTag")
    private Boolean checkString(String logLine){
        Boolean isMatch = false;
        Log.e("-----------------logOutputContent-----------------",logLine);
        String pattern;
        pattern = ".*Catching up block hashes to height \\d+, this might take a while.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        Log.e("-----------------logOutputContentIsMatch-----------------",isMatch.toString());
        return isMatch;
    }
    
    @SuppressLint("LongLogTag")
    private Boolean checkSyncedOver(String logLine){
        Boolean isMatch = false;
        Log.e("-----------------logOutputContent-----------------",logLine);
        String pattern;
        pattern = ".*Chain backend is fully synced \\(end_height=\\d+\\)!.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        Log.e("-----------------logOutputContentIsMatch-----------------",isMatch.toString());
        return isMatch;
    }
}

