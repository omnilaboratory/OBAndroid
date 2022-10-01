package com.omni.wallet.baselibrary.http.callback;

import android.content.Context;

import com.omni.wallet.baselibrary.http.progress.entity.Progress;

import java.util.Map;

/**
 * 网络请求的回调
 */

public interface EngineCallback {

    // 执行之前会回调的方法
    void onPreExecute(Context context, Map<String, Object> params);

    void onCancel(Context context);

    void onError(Context context, String errorCode, String errorMsg);

    void onSuccess(Context context, String result);

    void onSuccess(Context context, byte[] result);

    void onProgressInThread(Context context, Progress progress);

    void onFileSuccess(Context context, String filePath);

//    ILoadingDialog getDialog();

//    boolean showLoadingDialog();
//
//    boolean loadingCancelable();

    // 设置默认的CallBack
    EngineCallback DEFAULT_CALLBACK = new EngineCallback() {
        @Override
        public void onPreExecute(Context context, Map<String, Object> params) {

        }

        @Override
        public void onCancel(Context context) {

        }

        @Override
        public void onError(Context context, String errorCode, String errorMsg) {

        }

        @Override
        public void onSuccess(Context context, String result) {

        }

        @Override
        public void onSuccess(Context context, byte[] result) {
        }


        @Override
        public void onProgressInThread(Context context, Progress progress) {

        }

        @Override
        public void onFileSuccess(Context context, String result) {

        }
//
//        @Override
//        public ILoadingDialog getDialog() {
//            return null;
//        }

//        @Override
//        public boolean showLoadingDialog() {
//            return false;
//        }
//
//        @Override
//        public boolean loadingCancelable() {
//            return false;
//        }
    };
}
