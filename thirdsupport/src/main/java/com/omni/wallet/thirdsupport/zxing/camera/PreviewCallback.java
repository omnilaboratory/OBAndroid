package com.omni.wallet.thirdsupport.zxing.camera;

/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.thirdsupport.R;


@SuppressWarnings("deprecation") // camera APIs
final class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager configManager;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager) {
        this.configManager = configManager;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    //上次记录的时间戳
    long lastRecordTime = System.currentTimeMillis();

    //上次记录的索引
    int darkIndex = 0;
    //一个历史记录的数组，255是代表亮度最大值
    long[] darkList = new long[]{255, 255, 255, 255};
    //扫描间隔
    int waitScanTime = 300;

    //亮度低的阀值
    int darkValue = 60;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = configManager.getCameraResolution();
        Handler thePreviewHandler = previewHandler;
        if (cameraResolution != null && thePreviewHandler != null) {
            Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
            // TODO 处理光线过暗，光线正常
            // TODO 处理光线过暗，光线正常
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRecordTime >= waitScanTime) {
                lastRecordTime = currentTime;
                int width = camera.getParameters().getPreviewSize().width;
                int height = camera.getParameters().getPreviewSize().height;
                //像素点的总亮度
                long pixelLightCount = 0L;
                //像素点的总数
                long pixeCount = width * height;
                //采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
                int step = 10;
                //data.length - allCount * 1.5f的目的是判断图像格式是不是YUV420格式，只有是这种格式才相等
                //因为int整形与float浮点直接比较会出问题，所以这么比
                if (Math.abs(data.length - pixeCount * 1.5f) < 0.00001f) {
                    for (int i = 0; i < pixeCount; i += step) {
                        //如果直接加是不行的，因为data[i]记录的是色值并不是数值，byte的范围是+127到—128，
                        // 而亮度FFFFFF是11111111是-127，所以这里需要先转为无符号unsigned long参考Byte.toUnsignedLong()
                        pixelLightCount += ((long) data[i]) & 0xffL;
                    }
                    //平均亮度
                    long cameraLight = pixelLightCount / (pixeCount / step);
                    //更新历史记录
                    int lightSize = darkList.length;
                    darkList[darkIndex = darkIndex % lightSize] = cameraLight;
                    darkIndex++;
                    boolean isDarkEnv = true;
                    //判断在时间范围waitScanTime * lightSize内是不是亮度过暗
                    for (int i = 0; i < lightSize; i++) {
                        if (darkList[i] > darkValue) {
                            isDarkEnv = false;
                        }
                    }
                    //亮度过暗就提醒
                    LogUtils.d(TAG, "摄像头环境亮度为 ： " + cameraLight + "      计算出环境光线结果：" + (isDarkEnv ? "过暗" : "正常"));
                    Message lightMessage = thePreviewHandler.obtainMessage(R.id.light_change, isDarkEnv);
                    lightMessage.sendToTarget();
                }
            }
            // TODO 处理光线过暗，光线正常
            // TODO 处理光线过暗，光线正常
            previewHandler = null;
        } else {
            Log.d(TAG, "Got preview callback, but no handler or resolution available");
        }
    }

}