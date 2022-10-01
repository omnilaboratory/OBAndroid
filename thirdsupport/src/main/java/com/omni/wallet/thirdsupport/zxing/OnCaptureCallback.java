package com.omni.wallet.thirdsupport.zxing;


public interface OnCaptureCallback {

    /**
     * 接收扫码结果回调
     *
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截
     */
    boolean onResultCallback(String result);

    /**
     * 光线变化情况
     */
    void onLightChange(boolean dark);
}
