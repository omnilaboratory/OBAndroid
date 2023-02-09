package com.omni.testnet.utils;

import android.content.Context;

import com.omni.testnet.baselibrary.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;



public class AppUtil {

    private static final String LOG_TAG = AppUtil.class.getName();

    private static AppUtil mInstance = null;
    private static Context mContext = null;

    private AppUtil() {
        ;
    }

    public static AppUtil getInstance(Context ctx) {
        mContext = ctx;
        if (mInstance == null) {
            mInstance = new AppUtil();
        }
        return mInstance;
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("asset_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        LogUtils.e(LOG_TAG, "Error reading country_list JSON: " + json.toString());
        return json;
    }
}
