package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AssetsValueDataDao {
    private AssetsDB mInstance;
    private final static String TAG = AssetsValueDataDao.class.getSimpleName();

    AssetsValueDataDao(Context context) {
        this.mInstance = AssetsDB.getInstance(context);
    }

    void insertAssetValueData(double value, long date) {
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

    void updateAssetValueData(double value, long date) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_value_data set value =? where update_date=?";
        db.execSQL(sql, new Object[]{value, date});
//        db.close();
    }

    private List<AssetsValueDataItem> queryAssetValueDataLast() {
        List<AssetsValueDataItem> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by update_date desc limit 1";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()) {
            long date;
            int update_date_index = cursor.getColumnIndex("update_date");
            if (update_date_index>=0){
                date = cursor.getLong(update_date_index);
            }else{
                date = 0;
            }
            double value;
            int value_index = cursor.getColumnIndex("value");
            if (value_index>=0){
                value = cursor.getDouble(value_index);
            }else{
                value = 0;
            }
            AssetsValueDataItem queryRow = new AssetsValueDataItem(value,date);
            queryList.add(queryRow);
        }
        cursor.close();
//        db.close();
        return queryList;
    }

    void completeAssetData() {
        List<AssetsValueDataItem> lastDataList = queryAssetValueDataLast();
        Log.d(TAG, "completeAssetData: " + lastDataList.toString());
        if (lastDataList.size() > 0) {
            AssetsValueDataItem data = lastDataList.get(0);
            Log.d(TAG, "completeAssetData: " + data.toString());
            long lastUpdateTime = data.getUpdate_date();
            double lastValue =  data.getValue();
            try {
                long nowDate = TimeFormatUtil.getCurrentDayMills();
                int willCompleteNum = (int) (((nowDate - lastUpdateTime) / ConstantInOB.DAY_MILLIS));
                for (int i = 0; i < willCompleteNum; i++) {
                    long update_date = lastUpdateTime + ConstantInOB.DAY_MILLIS * (i + 1);
                    insertAssetValueData(lastValue, update_date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                double lastValue = 0.0;
                long update_date = TimeFormatUtil.getCurrentDayMills();
                insertAssetValueData(lastValue, update_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }


    List<AssetsValueDataItem> queryAssetValueDataOneYear() {
        List<AssetsValueDataItem> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_value_data order by update_date desc limit 365";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()) {
            long date;
            int update_date_index = cursor.getColumnIndex("update_date");
            if (update_date_index>=0){
                date = cursor.getLong(update_date_index);
            }else{
                date = 0;
            }
            double value;
            int value_index = cursor.getColumnIndex("value");
            if (value_index>=0){
                value = cursor.getDouble(value_index);
            }else{
                value = 0;
            }
            AssetsValueDataItem queryRow = new AssetsValueDataItem(value, date);
            queryList.add(queryRow);
        }
        cursor.close();
//        db.close();
        return queryList;
    }


    public void clearData() {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String deleteSql = "delete from assets_value_data";
        db.execSQL(deleteSql);
    }
}
