package com.omni.wallet_mainnet.thirdsupport.zxing;

import android.graphics.Bitmap;

import com.google.zxing.Result;


public interface OnCaptureListener {


    /**
     * 接收解码后的扫码结果
     */
    void onHandleDecode(Result result, Bitmap barcode, float scaleFactor);

    /**
     * 光线变化
     */
    void onLightChange(boolean isDark);

}
