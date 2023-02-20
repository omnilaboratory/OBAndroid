package com.omni.wallet.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetsValueDataDao {
    private static AssetsDB mInstance;
    private final static String TAG = AssetsValueDataDao.class.getSimpleName();

    public AssetsValueDataDao(Context context){
        this.mInstance = AssetsDB.getInstance(context);
    }

    public void insertAssetValueData(double value,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("value", value);
        values.put("update_date", date);
        db.insert("assets_value_data", null, values);
//        db.close();
    }

    public void updateAssetValueData(double value,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_value_data set value =? where update_date=?";
        db.execSQL(sql, new Object[]{value,date});
//        db.close();
    }

    public void updateAssetValueData(double value){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String ,Object>> queryAssetValueDataAll(){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data ";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("update_date")));
            queryRow.put("value",cursor.getDouble(cursor.getColumnIndex("value")));
            queryList.add(queryRow);
        }
        cursor.close();
//        db.close();
        return queryList;
    }

    public List<Map<String ,Object>> queryAssetValueDataLast(){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by update_date desc limit 1";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getLong(cursor.getColumnIndex("update_date")));
            queryRow.put("value",cursor.getDouble(cursor.getColumnIndex("value")));
            queryList.add(queryRow);
        }
        cursor.close();
//        db.close();
        return queryList;
    }

    public void completeAssetData(){
        List<Map<String,Object>> lastDataList =  queryAssetValueDataLast();
        Log.e(TAG, "completeAssetData: "+ lastDataList.toString());
        if (lastDataList.size()>0){
            Map<String,Object> data =  lastDataList.get(0);
            Log.e(TAG, "completeAssetData: "+ data.toString());
            long lastUpdateTime = (long) data.get("date");
            double lastValue = (double) data.get("value");
            try {
                long nowDate = TimeFormatUtil.getCurrentDayMills();
                int willCompleteNum = (int) (((nowDate - lastUpdateTime)/ ConstantInOB.DAY_MILLIS));
                for (int i = 0; i < willCompleteNum; i++) {
                    long update_date = lastUpdateTime + ConstantInOB.DAY_MILLIS * (i+1);
                    insertAssetValueData(lastValue,update_date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            try {
                double lastValue = 0.0;
                long update_date = TimeFormatUtil.getCurrentDayMills();
                insertAssetValueData(lastValue,update_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }



    public Map<String,Double> queryChangeOfValue(){
        List<Double> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by update_date desc limit 1";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            queryList.add(cursor.getDouble(cursor.getColumnIndex("value")));
        }
        double value = queryList.get(0);
        double changePercent = Math.floor((queryList.get(0) - queryList.get(1))/queryList.get(1)*10000)/100.0;
        Map<String, Double> changeMap = new HashMap<>();
        changeMap.put("value",value);
        changeMap.put("percent",changePercent);
        cursor.close();
//        db.close();
        return changeMap;
    }

    @SuppressLint("LongLogTag")
    public List<Map<String ,Object>> queryAssetValueDataOneYear(){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by update_date desc limit 365";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getLong(cursor.getColumnIndex("update_date")));
            queryRow.put("value",cursor.getDouble(cursor.getColumnIndex("value")));
            queryList.add(queryRow);
        }
        Log.e(TAG+"queryAssetValueDataOneYear: ", queryList.toString());
        cursor.close();
//        db.close();
        return queryList;
    }



    public void deleteLastDataByNum(int count){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String queryLastString = "select * from assets_value_data order by _id desc limit ?";
        Cursor cursor = db.rawQuery(queryLastString, new String[]{String.valueOf(count)});
        List<Integer> queryList = new ArrayList<>();
        while (cursor.moveToNext()){
            queryList.add(cursor.getInt(cursor.getColumnIndex("_id")));
        }
        for (int i = 0; i<queryList.size();i++){
            String deleteSql = "delete from assets_value_data where _id =" + queryList.get(i);
            db.execSQL(deleteSql);
        }
    }
}
