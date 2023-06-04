package com.omni.wallet_mainnet.thirdsupport.zxing;

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


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;

/**
 * Finishes an activity after a period of inactivity if the device is on battery power.
 */
final class InactivityTimer {

    private static final String TAG = InactivityTimer.class.getSimpleName();

    private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

    private final Activity activity;
    private final BroadcastReceiver powerStatusReceiver;
    private boolean registered;
    private AsyncTask<Object,Object,Object> inactivityTask;

    InactivityTimer(Activity activity) {
        this.activity = activity;
        powerStatusReceiver = new PowerStatusReceiver(this);
        registered = false;
        onActivity();
    }

    synchronized void onActivity() {
        cancel();
        inactivityTask = new InactivityAsyncTask(activity);
        try {
            inactivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException ree) {
            Log.w(TAG, "Couldn't schedule inactivity task; ignoring");
        }
    }

    synchronized void onPause() {
        cancel();
        if (registered) {
            activity.unregisterReceiver(powerStatusReceiver);
            registered = false;
        } else {
            Log.w(TAG, "PowerStatusReceiver was never registered?");
        }
    }

    synchronized void onResume() {
        if (registered) {
            Log.w(TAG, "PowerStatusReceiver was already registered?");
        } else {
            activity.registerReceiver(powerStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            registered = true;
        }
        onActivity();
    }

    private synchronized void cancel() {
        AsyncTask<?,?,?> task = inactivityTask;
        if (task != null) {
            task.cancel(true);
            inactivityTask = null;
        }
    }

    void shutdown() {
        cancel();
    }

    private static class PowerStatusReceiver extends BroadcastReceiver {

        private WeakReference<InactivityTimer> weakReference;

        public PowerStatusReceiver(InactivityTimer inactivityTimer){
            weakReference = new WeakReference<>(inactivityTimer);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 0 indicates that we're on battery

                InactivityTimer inactivityTimer = weakReference.get();
                if(inactivityTimer!=null){
                    boolean onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0;
                    if (onBatteryNow) {
                        inactivityTimer.onActivity();
                    } else {
                        inactivityTimer.cancel();
                    }
                }

            }
        }
    }

    private static class InactivityAsyncTask extends AsyncTask<Object,Object,Object> {

        private WeakReference<Activity> weakReference;

        public InactivityAsyncTask(Activity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                Thread.sleep(INACTIVITY_DELAY_MS);
                Log.i(TAG, "Finishing activity due to inactivity");
                Activity activity = weakReference.get();
                if(activity!=null){
                    activity.finish();
                }
            } catch (InterruptedException e) {
                // continue without killing
            }
            return null;
        }
    }

}