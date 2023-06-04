package com.omni.wallet_mainnet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.omni.wallet_mainnet.common.NetworkType;

import java.util.ArrayList;
import java.util.List;

public class NodeDao {
    private static NodeDB mInstance;
    public NodeDao(Context context){
        mInstance = NodeDB.getInstance(context);
    }

    public void insertNode(String alias, String spayUrl, String nodeUrl, NetworkType networkType){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put("alias", alias);
        values.put("spayUrl", spayUrl);
        values.put("nodeUrl", nodeUrl);
        values.put("netType", networkType.toString());
        db.insert("node_list", null, values);
    }

    public void clearTable(){
        SQLiteDatabase db = mInstance.getWritableDatabase();
        if (!db.isOpen()) {
            return;
        }
        db.execSQL("delete from node_list");
    }

    public List<Node> getNodeListByNetType(NetworkType networkType){
        List<Node> nodeList = new ArrayList<>();
        String sql = "select * from node_list where netType = ?" ;
        SQLiteDatabase db = mInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(networkType)});
        while (cursor.moveToNext()){
            String alias;
            int aliasIndex = cursor.getColumnIndex("alias");
            if (aliasIndex>=0){
                alias = cursor.getString(aliasIndex);
            }else{
                alias = "";
            }
            String spayUrl;
            int spayUrlIndex = cursor.getColumnIndex("spayUrl");
            if (spayUrlIndex>=0){
                spayUrl = cursor.getString(spayUrlIndex);
            }else{
                spayUrl = "";
            }
            String nodeUrl;
            int nodeUrlIndex = cursor.getColumnIndex("nodeUrl");
            if (nodeUrlIndex>=0){
                nodeUrl = cursor.getString(nodeUrlIndex);
            }else{
                nodeUrl = "";
            }
            Node node = new Node(alias,spayUrl,nodeUrl,networkType);
            nodeList.add(node);
        }
        cursor.close();
//        db.close();
        return nodeList;
    }


}
