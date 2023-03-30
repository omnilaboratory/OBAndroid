package com.omni.wallet.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AssetsDB extends SQLiteOpenHelper {
    private final static String TAG = AssetsDB.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static AssetsDB mInstance;

    private AssetsDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static AssetsDB getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AssetsDB.class) {
                if (mInstance == null) {
                    mInstance = new AssetsDB(context, "asset_db", null, 1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateAssetsTable = "CREATE TABLE assets (_id INTEGER PRIMARY KEY AUTOINCREMENT,property_id TEXT NOT NULL,token_name TEXT NOT NULL,has_balance INTEGER NOT NULL)";
        String sqlCreateAssetsDataTable = "CREATE TABLE assets_data (_id INTEGER PRIMARY KEY AUTOINCREMENT,property_id TEXT NOT NULL,price REAL NOT NULL,amount REAL NOT NULL,channel_amount REAL NOT NULL,update_date NUMBER)";
        String sqlCreateAssetsValueDataTable = "CREATE TABLE assets_value_data (_id INTEGER PRIMARY KEY AUTOINCREMENT,value REAL NOT NULL,update_date NUMBER)";
        db.execSQL(sqlCreateAssetsTable);
        db.execSQL(sqlCreateAssetsDataTable);
        db.execSQL(sqlCreateAssetsValueDataTable);
        Log.i(TAG, "create Database------------->");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
