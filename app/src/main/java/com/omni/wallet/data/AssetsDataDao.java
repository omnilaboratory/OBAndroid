package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AssetsDataDao {
    private  AssetsDB mInstance;

    AssetsDataDao(Context context){
        this.mInstance = AssetsDB.getInstance(context);
    }

    void insertAssetsData(String propertyId, double price, double amount, double channelAmount, long date){
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
        db.insert("assets_data", null, values);
//        db.close();
    }

    private void insertAssetsData(String propertyId, double price, double amount, double channelAmount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            insertAssetsData(propertyId,price,amount,channelAmount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateAssetDataPrice(String propertyId, double price, long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set price =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{price, propertyId,date});
//        db.close();
    }



    void updateAssetDataPrice(String propertyId, double price){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataPrice(propertyId,price,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateAssetDataAmount(String propertyId, double amount, long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set amount =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{amount, propertyId,date});
//        db.close();
    }

    private void updateAssetDataAmount(String propertyId, double amount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataAmount(propertyId,amount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateAssetDataChannelAmount(String propertyId, double channelAmount, long date){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets_data set channel_amount =? where property_id=? and update_date=?";
        db.execSQL(sql, new Object[]{channelAmount, propertyId,date});
//        db.close();
    }

    void updateAssetDataChannelAmount(String propertyId, double channelAmount){
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            updateAssetDataChannelAmount(propertyId,channelAmount,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    List<AssetsDataItem> queryAllAssetsDataByDate(long date){
        List<AssetsDataItem> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data where update_date=?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(date)});
        while (cursor.moveToNext()){
            long update_date;
            if (cursor.getColumnIndex("update_date")>=0){
                update_date = cursor.getLong(cursor.getColumnIndex("update_date"));
            }else{
                update_date = -1;
            }
            double price;
            if (cursor.getColumnIndex("price")>=0){
                price = cursor.getDouble(cursor.getColumnIndex("price"));
            }else{
                price = -1;
            }
            double amount;
            if (cursor.getColumnIndex("amount")>=0){
                amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            }else{
                amount = -1;
            }
            double channel_amount;
            if (cursor.getColumnIndex("channel_amount")>=0){
                channel_amount = cursor.getDouble(cursor.getColumnIndex("channel_amount"));
            }else{
                channel_amount = -1;
            }
            String propertyId;
            if (cursor.getColumnIndex("property_id")>=0){
                propertyId = cursor.getString(cursor.getColumnIndex("property_id"));
            }else{
                propertyId = "";
            }
            AssetsDataItem row = new AssetsDataItem(propertyId,price,amount,channel_amount,update_date);
            queryList.add(row);
        }
        cursor.close();
//        db.close();
        return queryList;
    }

    List<AssetsDataItem> queryAssetLastDataByPropertyId(String propertyId){
        List<AssetsDataItem> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data where property_id=? order by update_date desc limit 1";
        Cursor cursor = db.rawQuery(sql,new String[]{propertyId});
        while (cursor.moveToNext()){
            long update_date;
            if (cursor.getColumnIndex("update_date")>=0){
                update_date = cursor.getLong(cursor.getColumnIndex("update_date"));
            }else{
                update_date = -1;
            }
            double price;
            if (cursor.getColumnIndex("price")>=0){
                price = cursor.getDouble(cursor.getColumnIndex("price"));
            }else{
                price = -1;
            }
            double amount;
            if (cursor.getColumnIndex("amount")>=0){
                amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            }else{
                amount = -1;
            }
            double channel_amount;
            if (cursor.getColumnIndex("channel_amount")>=0){
                channel_amount = cursor.getDouble(cursor.getColumnIndex("channel_amount"));
            }else{
                channel_amount = -1;
            }
            AssetsDataItem row = new AssetsDataItem(propertyId,price,amount,channel_amount,update_date);
            queryList.add(row);
        }
        cursor.close();
//        db.close();
        return queryList;
    }

    private boolean checkDataExist(String propertyId, long date){
        boolean dataExist =false;
        List<AssetsDataItem> queryList = new ArrayList<>();
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select * from assets_data where property_id=? and update_date = ? limit 1";
        Cursor cursor = db.rawQuery(sql,new String[]{propertyId, String.valueOf(date)});
        while (cursor.moveToNext()){
            long update_date;
            if (cursor.getColumnIndex("update_date")>=0){
                update_date = cursor.getLong(cursor.getColumnIndex("update_date"));
            }else{
                update_date = -1;
            }
            double price;
            if (cursor.getColumnIndex("price")>=0){
                price = cursor.getDouble(cursor.getColumnIndex("price"));
            }else{
                price = -1;
            }
            double amount;
            if (cursor.getColumnIndex("amount")>=0){
                amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            }else{
                amount = -1;
            }
            double channel_amount;
            if (cursor.getColumnIndex("channel_amount")>=0){
                channel_amount = cursor.getDouble(cursor.getColumnIndex("channel_amount"));
            }else{
                channel_amount = -1;
            }
            AssetsDataItem row = new AssetsDataItem(propertyId,price,amount,channel_amount,update_date);
            queryList.add(row);
        }
        cursor.close();
        if (queryList.size()>0) dataExist = true;
        return dataExist;
    }



    void insertOrUpdateAssetDataByAmount(String propertyId, double amount, long date){
        boolean dataExist = checkDataExist(propertyId,date);
        double price = 0;
        double channelAmount = 0;
        if (dataExist){
            updateAssetDataAmount(propertyId,amount);
        }else{
            List<AssetsDataItem> lastDataList = queryAssetLastDataByPropertyId(propertyId);
            if (lastDataList.size()>0){
                AssetsDataItem item = lastDataList.get(0);
                price = item.getPrice();
                channelAmount = item.getChannel_amount();
                insertAssetsData(propertyId,price,amount,channelAmount);
            }else {
                insertAssetsData(propertyId,0,amount,0);
            }
        }
    }

    public void clearData(){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String deleteSql = "delete from assets_data";
        db.execSQL(deleteSql);
    }


}
