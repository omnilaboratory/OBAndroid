package com.omni.wallet_mainnet.view.dialog;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.http.HttpUtils;
import com.omni.wallet_mainnet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet_mainnet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet_mainnet.baselibrary.utils.AppUtils;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.common.NetworkType;
import com.omni.wallet_mainnet.entity.UpdateEntity;
import com.omni.wallet_mainnet.utils.UpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 汉: 新版本提示的弹窗
 * En: NewVersionDialog
 * author: guoyalei
 * date: 2023/3/17
 */
public class NewVersionDialog {
    private static final String TAG = NewVersionDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private UpdateUtils mUpdateUtils;

    public NewVersionDialog(Context context) {
        this.mContext = context;
    }

    public void show(boolean force) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_new_version)
                    .setAnimation(R.style.popup_anim_style)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        mUpdateUtils = new UpdateUtils(mContext);
        TextView mVersionTv = mAlertDialog.findViewById(R.id.tv_version);
        TextView mNewFeatureTv = mAlertDialog.findViewById(R.id.tv_new_feature);
        TextView mOptimizationTv = mAlertDialog.findViewById(R.id.tv_optimization);
        HttpUtils.with(mContext)
                .get()
                .url("https://omnilaboratory.github.io/OBAndroid/app/src/main/assets/newVersion.json")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        LogUtils.e(TAG, "newVersionError:" + errorMsg);
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------newVersion---------------------" + result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            new Handler(Looper.getMainLooper()).post(() -> {
                                try {
                                    JSONObject netObject = null;
                                    if (ConstantInOB.networkType == NetworkType.TEST) {
                                        netObject = jsonObject.getJSONObject("testnet");
                                    } else if (ConstantInOB.networkType == NetworkType.REG) {
                                        netObject = jsonObject.getJSONObject("regtest");
                                    } else if (ConstantInOB.networkType == NetworkType.MAIN) {
                                        netObject = jsonObject.getJSONObject("mainnet");
                                    }
                                    mVersionTv.setText(netObject.getString("version"));
                                    mNewFeatureTv.setText(netObject.getString("newFeature"));
                                    mOptimizationTv.setText(netObject.getString("optimization"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
        /**
         * @描述： 点击 update
         * @desc: click update button
         */
        mAlertDialog.findViewById(R.id.tv_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateUtils.checkUpdate(new UpdateUtils.CheckUpdateCallback() {
                    @Override
                    public void onCheckSuccess(UpdateEntity result) {
                        if (result == null) {
                            ToastUtils.showToast(mContext, "Currently is the latest version");
                            mAlertDialog.dismiss();
                            return;
                        }
                        String version = result.getVersion();
                        if (AppUtils.getAppVersionName(mContext).compareTo(version) < 0) {
                            ToastUtils.showToast(mContext, "Download progress can be viewed in the notification bar.");
                            mAlertDialog.dismiss();
                            mUpdateUtils.updateNewVersion(result);
                        } else {
                            ToastUtils.showToast(mContext, "Currently is the latest version");
                            mAlertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCheckFail(String code, String message) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        /**
         * @描述： 点击 close
         * @desc: click close button
         */
        mAlertDialog.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (force) {
                    killAppProcess();
                } else {
                    mAlertDialog.dismiss();
                }
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    /**
     * 注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
     * Note: You cannot kill the main process first, otherwise the logic code cannot continue to execute. You need to kill the related process first and then kill the main process
     */
    public void killAppProcess() {
        LogUtils.e(TAG, "killAppProcess");
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        // 先杀掉相关进程，最后再杀掉主进程(Kill the related process first, and then the main process)
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : processInfos) {
            if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        // 正常退出程序，也就是结束当前正在运行的java虚拟机(Exit the program normally, that is, end the currently running java virtual machine)
        System.exit(0);
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
