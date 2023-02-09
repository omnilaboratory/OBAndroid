package com.omni.testnet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.omni.testnet.base.ConstantInOB;
import com.omni.testnet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class USDTData {
    private static AccountAssetsData mInstance;

    public USDTData(Context context) {
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
        db.insert("usdtData", null, values);
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

    public void updateAmount(double amount) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = TimeFormatUtil.getNowDate();
        String sql = "update usdtData set amount =? where date=?";
        db.execSQL(sql, new Object[]{amount, date});
        db.close();

    }

    public void updatePrice(double price) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = TimeFormatUtil.getNowDate();
        String sql = "update usdtData set price =? where date=?";
        db.execSQL(sql, new Object[]{price, date});
        db.close();
    }

    public void updatePriceAndAmount(double price, double amount) {
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String date = TimeFormatUtil.getNowDate();
        String sql = "update usdtData set price =?, amount =? where date=?";
        db.execSQL(sql, new Object[]{price, amount, date});
        db.close();
    }

    public List<Map<String,Object>> queryByDate(String endDate) throws ParseException {
        List<Map<String,Object>> queryList = new ArrayList<>();
        long oneYearAgo = Long.parseLong(endDate) - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from usdtData where date > ?";
        Cursor cursor = db.rawQuery(sql, (String[]) new Object[]{oneYearAgoString});
        while (cursor.moveToNext()){
            Map<String,Object> usdtRow = new HashMap<>();
            usdtRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            usdtRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            usdtRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryList.add(usdtRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public boolean checkDataIsEmpty(String endDate) throws ParseException {
        boolean isEmpty = true;
        List<Map<String,Object>> queryList = new ArrayList<>();
        long oneYearAgo = Long.parseLong(endDate) - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from usdtData where date = ? limit 1";
        Cursor cursor = db.rawQuery(sql, (String[]) new Object[]{oneYearAgoString});
        while (cursor.moveToNext()){
            Map<String,Object> usdtRow = new HashMap<>();
            usdtRow.put("date",cursor.getString(cursor.getColumnIndex("date")));
            usdtRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            usdtRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryList.add(usdtRow);
        }
        cursor.close();
        db.close();
        if (queryList.size() > 0){
            isEmpty = true;
        }else {
            isEmpty = false;
        }
        return isEmpty;
    }
}
