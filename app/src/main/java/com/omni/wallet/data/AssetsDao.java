package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omni.wallet.utils.TimeFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetsDao {
    private static AssetsDB mInstance;
    private final static String TAG = AssetsDao.class.getSimpleName();

    public AssetsDao(Context context){
        this.mInstance = AssetsDB.getInstance(context);
    }

    public void insertAsset(String propertyId,String tokenName){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("property_id", propertyId);
        values.put("token_name", tokenName);
        values.put("has_balance", 0);
        db.insert("assets", null, values);
        db.close();
    }

    public void changeAssetIsUse (String propertyId,int hasBalance){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets set has_balance =? where property_id=?";
        db.execSQL(sql, new Object[]{hasBalance, propertyId});
        db.close();
    }

    public boolean checkAssetsExist(String propertyId,String tokenName){
        boolean isExist = false;
        int count = 0;
        String sql = "select * from assets where property_id = ? and token_name = ?;";
        SQLiteDatabase db = mInstance.getWritableDatabase();;
        Cursor cursor = db.rawQuery(sql, new String[]{propertyId,tokenName});
        count = cursor.getCount();
        cursor.close();
        db.close();
        if (count>0) isExist = true;
        return isExist;
    }

    public void checkAndInsertAsset(String propertyId,String tokenName){
        boolean isExist = checkAssetsExist(propertyId,tokenName);
        if (!isExist){
            insertAsset(propertyId,tokenName);
        }
    }

    public List<Map<String,Object>> getAssetsList(){
        List<Map<String,Object>> assetsList = new ArrayList<Map<String, Object>>();
        String sql = "select * from assets";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> asset = new HashMap<>();
            asset.put("property_id",cursor.getString(cursor.getColumnIndex("property_id")));
            asset.put("token_name",cursor.getDouble(cursor.getColumnIndex("token_name")));
            assetsList.add(asset);
        }
        cursor.close();
        db.close();
        return assetsList;
    }

    public int getAssetsCount(){
        int count = 0;
        String sql = "select * from assets";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{});
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public int getUsingAssetCount(){
        int count = 0;
        String sql = "select * from assets where has_balance = 1";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{});
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public List<Map<String,Object>> getUsingAssetsList(){
        List<Map<String,Object>> assetsList = new ArrayList<Map<String, Object>>();
        String sql = "select * from assets where has_balance = 1";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            Map<String,Object> asset = new HashMap<>();
            asset.put("property_id",cursor.getString(cursor.getColumnIndex("property_id")));
            asset.put("token_name",cursor.getDouble(cursor.getColumnIndex("token_name")));
            assetsList.add(asset);
        }
        cursor.close();
        db.close();
        return assetsList;
    }
}
