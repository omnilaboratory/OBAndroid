package com.omni.wallet.data;

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

public class DollarData {
    private String TAG = DollarData.class.getSimpleName();
    private static AccountAssetsData mInstance;

    public DollarData(Context context) {
        this.mInstance = AccountAssetsData.getInstance(context);
    }

    public void insert(String date, double amount, double price) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("amount", amount);
        values.put("price", price);
        db.insert("dollar_data", null, values);
        Log.e(TAG,"insert success");
        db.close();
    }

    public void insert(double amount, double price) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        insert(date, amount, price);
    }

    public void updateAmount(double amount) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update dollar_data set amount =? where date=?";
        db.execSQL(sql, new Object[]{amount, date});
        db.close();

    }

    public void updatePrice(double price) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update dollar_data set price =? where date=?";
        db.execSQL(sql, new Object[]{price, date});
        db.close();
    }

    public void updatePriceAndAmount(double price, double amount) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update dollar_data set price =?, amount =? where date=?";
        db.execSQL(sql, new Object[]{price, amount, date});
        db.close();
    }


    public List<Map<String,Object>> queryByDate(String endDate) throws ParseException {
        List<Map<String,Object>> queryList = new ArrayList<>();
        long oneYearAgo = Long.parseLong(endDate) - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        String sql = "select * from dollar_data where date > ?";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, (String[]) new Object[]{oneYearAgoString});
        while (cursor.moveToNext()){
            Map<String,Object> dollarRow = new HashMap<>();
            dollarRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            dollarRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            dollarRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryList.add(dollarRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public boolean checkDataIsEmpty() throws ParseException {
        boolean isEmpty;
        List<Map<String,Object>> queryList = new ArrayList<>();
        long endDate = TimeFormatUtil.getCurrentDayMills();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        Cursor cursor = db.query("dollar_data",new String[]{"date"},"date=?",new String[]{Long.toString(endDate)},null,null,"date");
        while (cursor.moveToNext()){
            Map<String,Object> dollarRow = new HashMap<>();
            dollarRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            queryList.add(dollarRow);
        }
        cursor.close();
        db.close();
        if (queryList.size() > 0){
            isEmpty = false;
        }else {
            isEmpty = true;
        }
        return isEmpty;
    }

    public double getLastPrice() {
        SQLiteDatabase db = mInstance.getReadableDatabase();
        String sql = "SELECT price FROM dollar_data ORDER BY date desc LIMIT 1";
        Cursor cursor = db.rawQuery(sql,null);
        double price = 1.00;
        while (cursor.moveToNext()){
            price = cursor.getDouble(cursor.getColumnIndex("price"));
        }
        cursor.close();
        return price;
    }
}
