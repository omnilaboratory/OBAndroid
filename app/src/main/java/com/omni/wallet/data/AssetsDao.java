package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AssetsDao {
    private static AssetsDB mInstance;

    public AssetsDao(Context context){
        mInstance = AssetsDB.getInstance(context);
    }

    private void insertAsset(String propertyId, String tokenName){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("property_id", propertyId);
        values.put("token_name", tokenName);
        values.put("has_balance", AssetsItem.ASSET_UNUSED);
        db.insert("assets", null, values);
//        db.close();
    }

    void changeAssetIsUse(String propertyId, int hasBalance){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        String sql = "update assets set has_balance =? where property_id=?";
        db.execSQL(sql, new Object[]{hasBalance, propertyId});
//        db.close();
    }

    private boolean checkAssetsExist(String propertyId, String tokenName){
        boolean isExist = false;
        int count;
        String sql = "select * from assets where property_id = ? and token_name = ?;";
        SQLiteDatabase db = mInstance.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{propertyId,tokenName});
        count = cursor.getCount();
        cursor.close();
        if (count>0) isExist = true;
        return isExist;
    }

    void checkAndInsertAsset(String propertyId, String tokenName){
        boolean isExist = checkAssetsExist(propertyId,tokenName);
        if (!isExist){
            insertAsset(propertyId,tokenName);
        }
    }

    public List<AssetsItem> getUsingAssetsList(){
        List<AssetsItem> assetsList = new ArrayList<>();
        String sql = "select * from assets where has_balance = 1";
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            String propertyId;
            int propertyIdIndex = cursor.getColumnIndex("property_id");
            if (propertyIdIndex>=0){
                propertyId = cursor.getString(propertyIdIndex);
            }else{
                propertyId = "";
            }
            String token_name;
            int tokenNameIndex = cursor.getColumnIndex("token_name");
            if (tokenNameIndex>=0){
                token_name = cursor.getString(tokenNameIndex);
            }else{
                token_name = "";
            }
            AssetsItem asset = new AssetsItem(propertyId,token_name,AssetsItem.ASSET_USING);
            assetsList.add(asset);
        }
        cursor.close();
//        db.close();
        return assetsList;
    }
}
