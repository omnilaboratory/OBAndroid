package com.omni.testnet.baselibrary.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Activity生命周期监听回调的实现类，这里什么也不错，只是为了在使用的时候继承这个类，
 * 然后不用去实现那么多的方法而已
 */

public class DefaultActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
