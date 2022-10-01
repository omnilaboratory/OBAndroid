package com.omni.wallet.entity.js;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JS交互的父类
 * Created by fa on 2018/9/10.
 */

public class BaseJavaScriptInterface {
    private static final String TAG = BaseJavaScriptInterface.class.getSimpleName();

    protected Context mContext;
    protected Gson gson;

    public BaseJavaScriptInterface(Context context) {
        mContext = context;
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public void release() {
    }
}
