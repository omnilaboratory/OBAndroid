package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        db.insert("assets", null, values);
        db.close();
    }

    public void updateAssetValueData(double value,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_value_data set value =? where update_date=?";
        db.execSQL(sql, new Object[]{value,date});
        db.close();
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
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            queryRow.put("value",cursor.getDouble(cursor.getColumnIndex("price")));
            queryList.add(queryRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public List<Map<String ,Object>> queryAssetValueDataLast(){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by date desc limit 1";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            queryRow.put("value",cursor.getDouble(cursor.getColumnIndex("price")));
            queryList.add(queryRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public void completeAssetData(){
        List<Map<String,Object>> lastDataList =  queryAssetValueDataLast();
        Map<String,Object> data =  lastDataList.get(0);
        long lastUpdateTime = (long) data.get("update_date");
        double lastValue = (double) data.get("value");
        try {
            long nowDate = TimeFormatUtil.getCurrentDayMills();
            int willCompleteNum = (int) (((nowDate - lastUpdateTime)/ ConstantInOB.DAY_MILLIS));
            for (int i = 0; i < willCompleteNum; i++) {
                long update_date = lastUpdateTime + ConstantInOB.DAY_MILLIS;
                insertAssetValueData(lastValue,update_date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
