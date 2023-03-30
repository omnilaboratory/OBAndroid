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
    private AssetsDB mInstance;
    private final static String TAG = AssetsValueDataDao.class.getSimpleName();

    AssetsValueDataDao(Context context){
        this.mInstance = AssetsDB.getInstance(context);
    }

    void insertAssetValueData(double value, long date){
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

    void updateAssetValueData(double value, long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_value_data set value =? where update_date=?";
        db.execSQL(sql, new Object[]{value,date});
//        db.close();
    }

    private List<Map<String ,Object>> queryAssetValueDataLast(){
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

    void completeAssetData(){
        List<Map<String,Object>> lastDataList =  queryAssetValueDataLast();
        Log.d(TAG, "completeAssetData: "+ lastDataList.toString());
        if (lastDataList.size()>0){
            Map<String,Object> data =  lastDataList.get(0);
            Log.d(TAG, "completeAssetData: "+ data.toString());
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


    List<Map<String ,Object>> queryAssetValueDataOneYear(){
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
        cursor.close();
//        db.close();
        return queryList;
    }


    public void clearData(){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String deleteSql = "delete from assets_value_data";
        db.execSQL(deleteSql);
    }
}
