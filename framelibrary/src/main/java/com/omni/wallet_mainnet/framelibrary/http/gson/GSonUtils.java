package com.omni.wallet_mainnet.framelibrary.http.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GSonUtils {
    private static final String TAG = GSonUtils.class.getSimpleName();


    /**
     * 创建Gson实例
     */
    public static Gson buildGSon() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(ResponseEntityAdapter.FACTORY)
                .registerTypeAdapterFactory(ObjectAdapter.FACTORY)
                .create();
    }
}
