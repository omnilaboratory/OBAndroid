package com.omni.testnet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omni.testnet.base.ConstantInOB;
import com.omni.testnet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTCData {
    private static AccountAssetsData mInstance;
    private String TAG = BTCData.class.getSimpleName();

    public BTCData(Context context) {
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
        db.insert("btc_data", null, values);
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
        String sql = "update btc_data set amount =? where date=?";
        db.execSQL(sql, new Object[]{amount, date});
        db.close();

    }

    public void updatePrice(double price) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update btc_data set price =? where date=?";
        db.execSQL(sql, new Object[]{price, date});
        db.close();
    }

    public void updateChannelAmount(double channel_amount) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update btc_data set channel_amount =? where date=?";
        db.execSQL(sql, new Object[]{channel_amount, date});
        db.close();
    }

    public void updatePriceAndAmount(double price, double amount, double channel_amount) throws ParseException {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = Long.toString(TimeFormatUtil.getCurrentDayMills());
        String sql = "update btc_data set price =?, amount =?,channel_amount =? where date=?";
        db.execSQL(sql, new Object[]{price, amount, channel_amount, date});
        db.close();
    }


    public List<Map<String,Object>> queryByDate(String endDate) throws ParseException {
        List<Map<String,Object>> queryList = new ArrayList<>();
        long oneYearAgo = Long.parseLong(endDate) - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from btc_data where date > ?";
        Cursor cursor = db.rawQuery(sql, (String[]) new Object[]{oneYearAgoString});
        while (cursor.moveToNext()){
            Map<String,Object> btcRow = new HashMap<>();
            btcRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            btcRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            btcRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryList.add(btcRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public boolean checkDataIsEmpty() throws ParseException {
        boolean isEmpty;
        List<Map<String,Object>> queryList = new ArrayList<>();
        long endDate = TimeFormatUtil.getCurrentDayMills();
        long oneYearAgo = endDate - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        SQLiteDatabase db = mInstance.getWritableDatabase();
        Cursor cursor = db.query("btc_data",new String[]{"date"},"date=?",new String[]{Long.toString(endDate)},null,null,"date");
        while (cursor.moveToNext()){
            Map<String,Object> btcRow = new HashMap<>();
            btcRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            queryList.add(btcRow);
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
    public double getLastPrice(){
        SQLiteDatabase db = mInstance.getReadableDatabase();
        String sql = "SELECT price FROM btc_data ORDER BY date desc LIMIT 1";
        Cursor cursor = db.rawQuery(sql,null);
        double price = 160000.00;
        while (cursor.moveToNext()){
            price = cursor.getDouble(cursor.getColumnIndex("price"));
        }
        cursor.close();
        return price;
    }
}
