package com.omni.wallet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountAssetsData extends SQLiteOpenHelper {

    private static final String TAG = AccountAssetsData.class.getSimpleName();
    public static final int VERSION = 1;

    private AccountAssetsData(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private volatile static AccountAssetsData mInstance;

    public static AccountAssetsData getInstance(Context context){
        if(mInstance == null){
            synchronized (AccountAssetsData.class){
                if (mInstance ==null){
                    mInstance = new AccountAssetsData(context,"account_assets_data",null,1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlBTC = "create table btc_data(date text primary key,amount real,price real)";
        String sqlDollar = "create table dollar_data(date text primary key,amount real,price real)";
        Log.i(TAG, "create Database------------->");
        db.execSQL(sqlBTC);
        db.execSQL(sqlDollar);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG,"data update");
    }

    public List<Map<String,Object>> queryAmountForAll() throws ParseException {
        List<Map<String,Object>> queryList = new ArrayList<>();
        long endDate = TimeFormatUtil.getCurrentDayMills();
        long oneYearAgo = endDate - ConstantInOB.DAY_MILLIS*365;
        String oneYearAgoString = Long.toString(oneYearAgo);
        SQLiteDatabase db = mInstance.getWritableDatabase();
        String sql = "select dollar_data.date as save_data,dollar_data.price as dollar_price,dollar_data.amount as dollar_amount,btc_data.price as btc_price,btc_data.amount as btc_amount from dollar_data inner join btc_data on dollar_data.date = btc_data.date where dollar_data.date >= ?";
        Cursor cursor = db.rawQuery(sql, new String[]{oneYearAgoString});

        while (cursor.moveToNext()){
            Map<String,Object> usdtRow = new HashMap<>();
            double usdtValue = cursor.getDouble(cursor.getColumnIndex("dollar_price"))*cursor.getDouble(cursor.getColumnIndex("dollar_amount"));
            double btcValue = cursor.getDouble(cursor.getColumnIndex("btc_price"))*cursor.getDouble(cursor.getColumnIndex("btc_amount"));
            double value = usdtValue + btcValue;
            usdtRow.put("date",cursor.getString(cursor.getColumnIndex("save_data")));
            usdtRow.put("value",value);
            queryList.add(usdtRow);
        }
        return queryList;
    }

}
