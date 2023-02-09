package com.omni.testnet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WalletGetInfo {
    public static List<String> getAccountList(Context mctx){
        List<String> accountList = new ArrayList<>();
        SharedPreferences addressList = mctx.getSharedPreferences("Account", MODE_PRIVATE);
        String addressListStr = addressList.getString("accountList","");
        String [] addressArray= addressListStr.split(",");
        for (int i = 0; i< addressArray.length;i++){
            accountList.add(addressArray[i]);
        }
        return  accountList;
    }
}
