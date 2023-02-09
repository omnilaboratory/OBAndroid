package com.omni.testnet.utils;

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
                long lastReadLineNum = blockDataSharedPreferences.getLong("lastReadLineNum",0);
                long readingLineNum = 0;
                bfr = new BufferedReader(new FileReader(filePath));
                String line;
                line = bfr.readLine();
                StringBuilder sb = new StringBuilder();
                while(line!=null){
                    String oldLine = line;
                    sb.append(line);
                    sb.append("\n");
                    line = bfr.readLine();
                    readingLineNum++;
                    if(readingLineNum>lastReadLineNum){
                        if(line!=null){
                            if(checkString(oldLine)){
                                String stringHeight = oldLine.split("height")[1].split(",")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkSyncedOver(oldLine)){
                                String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.putBoolean("isSynced",true);
                                    blockDataEditor.putBoolean("isOpened",true);
                                    blockDataEditor.commit();
                                }
                            }else if(checkRecoverString(oldLine)){
                                String stringHeight = oldLine.split("blocks")[1].split("-")[1];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkRecoveredStartString(oldLine)){
                                String stringHeight = oldLine.split("height=")[1].split("hash=")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkSyncedHeightNeutrinoString(oldLine)){
                                String stringHeight = oldLine.split("height=")[2].split(",")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }
                        }else{
                            if(checkString(oldLine)){
                                String stringHeight = oldLine.split("height")[1].split(",")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkSyncedOver(oldLine)){
                                String stringHeight = oldLine.split("height=")[1].split("\\)")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.putBoolean("isSynced",true);
                                    blockDataEditor.putBoolean("isOpened",true);
                                    blockDataEditor.commit();
                                }
                            }else if(checkRecoverString(oldLine)){
                                String stringHeight = oldLine.split("blocks")[1].split("-")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkRecoveredStartString(oldLine)){
                                String stringHeight = oldLine.split("height=")[1].split("hash=")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkSyncedHeightNeutrinoString(oldLine)){
                                String stringHeight = oldLine.split("height=")[2].split(",")[0];
                                int syncingHeight = Integer.parseInt(stringHeight.trim());
                                if(syncingHeight>currentHeight){
                                    blockDataEditor.putInt("currentBlockHeight",syncingHeight);
                                    blockDataEditor.commit();
                                }
                            }else if(checkNeutrinoSyncingString(oldLine)){
                                String stringHeight = oldLine.split("height")[1].split("from")[0];
                                Log.e("syncingHeight",stringHeight);
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
    private Boolean checkRecoverString(String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Recovered addresses from blocks \\d+-+\\d.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
    }
    private Boolean checkRecoveredStartString(String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Seed birthday surpassed, starting recovery of wallet from height=\\d+ hash=\\w+ with recovery-window=\\d.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
    }
    private Boolean checkSyncedHeightNeutrinoString(String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Got cfheaders from height=\\d+ to height=\\d+, prev_hash=\\w.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        return isMatch;
        
    }
    
    private Boolean checkNeutrinoSyncingString (String logLine){
        Boolean isMatch = false;
        String pattern;
        pattern = ".*Syncing to block height \\d+ from peer.*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(logLine);
        isMatch = m.matches();
        Log.e("syncingHeight",isMatch.toString());
        return isMatch;
    }
}
