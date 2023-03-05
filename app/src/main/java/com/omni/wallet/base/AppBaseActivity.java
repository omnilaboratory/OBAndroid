package com.omni.wallet.base;


import android.app.ActivityManager;
import android.content.Context;
import com.omni.wallet.framelibrary.base.FrameBaseActivity;
import com.omni.wallet.view.dialog.UnlockDialog;

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


    @Override
    protected void onStop() {
        long stopTime = System.currentTimeMillis();
        setStopTime(stopTime);
        setStopApp(true);
        super.onStop();
    }

    @Override
    protected void onResume() {
        long startTime = System.currentTimeMillis();
        long stopTime = getStopTime();
        long stopMills = startTime - stopTime;
        if (isStopApp()){
            setStopApp(false);
            if (stopMills >= ConstantInOB.MINUTE_MILLIS * 5){
                String runningActivityName = getRunningActivityName();
                String [] runningActivityNameArr = runningActivityName.split("\\.");
                String name =  runningActivityNameArr[5];
                switch (name){
                    case "UnlockActivity":
                    case "BackupBlockProcessActivity":
                    case "RestoreChannelActivity":
                    case "CreateWalletStepOneActivity":
                    case "CreateWalletStepTwoActivity":
                    case "CreateWalletStepThreeActivity":
                    case "RecoverWalletStepOneActivity":
                    case "RecoverWalletStepTwoActivity":
                    case "SplashActivity":
                    case "ForgetPwdActivity":
                    case "ForgetPwdNextActivity":
                        break;
                    default:
                        mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                        break;
                }
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mUnlockDialog != null){
            mUnlockDialog.release();
        }
        super.onDestroy();
    }
}
