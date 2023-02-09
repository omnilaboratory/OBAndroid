package com.omni.testnet.thirdsupport.zxing.camera;

import android.os.Environment;


@SuppressWarnings("deprecation")
public class CameraConfig {
    /**
     * 闪光灯关
     */
    public static final int FLASH_MODE_OFF = 0;
    /**
     * 闪光灯开
     */
    public static final int FLASH_MODE_TORCH = 1;
    /**
     * 闪光灯自动
     */
    public static final int FLASH_MODE_AUTO = 2;


    public static final String PICTURE_NAME = "testOCRPicture.jpg";

    public static final String PICTURE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
}
