package com.omni.wallet.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

public class NodeDB extends SQLiteOpenHelper {

    private static NodeDB mInstance;

    public NodeDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static NodeDB getInstance(Context context) {
        if (mInstance == null) {
            synchronized (NodeDB.class) {
                if (mInstance == null) {
                    mInstance = new NodeDB(context, "node_db", null, 1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateNodeListTable = "CREATE TABLE node_list (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "alias TEXT NOT NULL," +
                "spayUrl TEXT NOT NULL," +
                "nodeUrl TEXT NOT NULL," +
                "netType TEXT Not Null)";
        db.execSQL(sqlCreateNodeListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
