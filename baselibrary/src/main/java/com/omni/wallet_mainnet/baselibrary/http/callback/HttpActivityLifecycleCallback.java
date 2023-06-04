package com.omni.wallet_mainnet.baselibrary.http.callback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.omni.wallet_mainnet.baselibrary.base.BaseActivity;
import com.omni.wallet_mainnet.baselibrary.base.DefaultActivityLifecycleCallbacks;
import com.omni.wallet_mainnet.baselibrary.http.dialog.LoadingDialog;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求使用的Activity的生命周期回调
 */
public class HttpActivityLifecycleCallback extends DefaultActivityLifecycleCallbacks {
    private static final String TAG = HttpActivityLifecycleCallback.class.getSimpleName();
    private Map<Activity, HttpFragmentLifecycleCallback> mCallbackMap = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // 添加Fragment的监听
        if (activity instanceof BaseActivity) {
            if (((BaseActivity) activity).containFragment()) {
                HttpFragmentLifecycleCallback callback;
                if (mCallbackMap.containsKey(activity)) {
                    callback = mCallbackMap.get(activity);
                } else {
                    callback = new HttpFragmentLifecycleCallback();
                    mCallbackMap.put(activity, callback);
                }
                ((BaseActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(callback, true);
            }
        }
    }

    @Override
    public void onActivityDestroyed(final Activity activity) {
        LogUtils.e(TAG, "Activity======》" + activity.getClass().getSimpleName() + "   销毁");
        // 延时移除Fragment的监听，否则Fragment的生命周期回调不执行
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof BaseActivity && ((BaseActivity) activity).containFragment()) {
                    HttpFragmentLifecycleCallback callback = mCallbackMap.get(activity);
                    if (callback != null) {
                        ((BaseActivity) activity).getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(callback);
                        mCallbackMap.remove(activity);
                    }
                }
            }
        }, 50);
        // Activity销毁的时候在这里统一销毁LoadingDialog
        LoadingDialog.getInstance().onActivityDestroy(activity);
    }
}
