package com.omni.wallet.base;


import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.omni.wallet.framelibrary.base.FrameBaseActivity;
import com.omni.wallet.view.dialog.UnlockDialog;

import java.util.List;

/**
 * App的Activity父类
 * Created by fa on 2018/8/2.
 */

public abstract class AppBaseActivity extends FrameBaseActivity {
    private static final String TAG = AppBaseActivity.class.getSimpleName();
    private static long STOP_TIME = 0;
    private static boolean stopApp = false;
    UnlockDialog mUnlockDialog;

    private String getRunningActivityName(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    public static long getStopTime() {
        return STOP_TIME;
    }

    public static void setStopTime(long stopTime) {
        STOP_TIME = stopTime;
    }

    public static boolean isStopApp() {
        return stopApp;
    }

    public static void setStopApp(boolean stopApp) {
        AppBaseActivity.stopApp = stopApp;
    }

    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }


        @Override
    protected void onStop() {
        super.onStop();
        boolean isRunningSelf = isRunningForeground();
        if (!isRunningSelf){
            long stopTime = System.currentTimeMillis();
            setStopTime(stopTime);
            setStopApp(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStopApp()){
            long startTime = System.currentTimeMillis();
            long stopTime = getStopTime();
            long stopMills = startTime - stopTime;
            setStopApp(false);
            if (stopMills >= ConstantInOB.MINUTE_MILLIS * 5){
                String runningActivityName = getRunningActivityName();
                String [] runningActivityNameArr = runningActivityName.split("\\.");
                String name =  runningActivityNameArr[5];
                Log.e(TAG+ "onResume: ", name);
                switch (name){
                    case "UnlockActivity":
                    case "backup":
                    case "recoverwallet":
                    case "SplashActivity":
                    case "ForgetPwdActivity":
                    case "ForgetPwdNextActivity":
                    case "createwallet":
                    case "InitWalletMenuActivity":
                        break;
                    default:
                        mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                        break;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStopApp(false);
        if (mUnlockDialog != null){
            mUnlockDialog.release();
        }

    }
}
