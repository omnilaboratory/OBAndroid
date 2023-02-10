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

public class AssetsDataDao {
    private static AssetsDB mInstance;
    private final static String TAG = AssetsDataDao.class.getSimpleName();

    public AssetsDataDao(Context context){
        this.mInstance = AssetsDB.getInstance(context);
    }

    public void insertAssetsData(String propertyId,double price,double amount,double channelAmount,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("property_id", propertyId);
        values.put("price", price);
        values.put("amount", amount);
        values.put("channel_amount", channelAmount);
        values.put("update_date", date);
        db.insert("assets", null, values);
        db.close();
    }

    public void insertAssetsData(String propertyId,double price,double amount,double channelAmount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            insertAssetsData(propertyId,price,amount,channelAmount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateAssetDataPrice(String propertyId,double price,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set price =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{price, propertyId,date});
        db.close();
    }

    public void updateAssetDataPrice(String propertyId,double price){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataPrice(propertyId,price,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateAssetDataAmount(String propertyId,double amount,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set amount =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{amount, propertyId,date});
        db.close();
    }

    public void updateAssetDataAmount(String propertyId,double amount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataAmount(propertyId,amount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateAssetDataChannelAmount(String propertyId,double channelAmount,long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set channel_amount =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{channelAmount, propertyId,date});
        db.close();
    }

    public void updateAssetDataChannelAmount(String propertyId,double channelAmount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataChannelAmount(propertyId,channelAmount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String,Object>> queryAllAssetsData(){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data ";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("update_date")));
            queryRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            queryRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryRow.put("channelAmount",cursor.getDouble(cursor.getColumnIndex("channel_amount")));
            queryList.add(queryRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public List<Map<String,Object>> queryAllAssetsDataByDate(long date){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data where update_date=?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(date)});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("update_date")));
            queryRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            queryRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryRow.put("channelAmount",cursor.getDouble(cursor.getColumnIndex("channel_amount")));
            queryList.add(queryRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }

    public List<Map<String,Object>> queryAssetLastDataByPropertyId(String propertyId){
        List<Map<String,Object>> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data where property_id=? order by update_date desc limit 1";
        Cursor cursor = db.rawQuery(sql,new String[]{propertyId});
        while (cursor.moveToNext()){
            Map<String,Object> queryRow = new HashMap<>();
            queryRow.put("date",cursor.getString(cursor.getColumnIndex("update_date")));
            queryRow.put("price",cursor.getDouble(cursor.getColumnIndex("price")));
            queryRow.put("amount",cursor.getDouble(cursor.getColumnIndex("amount")));
            queryRow.put("channelAmount",cursor.getDouble(cursor.getColumnIndex("channel_amount")));
            queryList.add(queryRow);
        }
        cursor.close();
        db.close();
        return queryList;
    }
}
