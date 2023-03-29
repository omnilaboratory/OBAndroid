package com.omni.wallet.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.entity.UpdateEntity;
import com.omni.wallet.utils.UpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_new_version)
                    .setAnimation(R.style.popup_anim_style)
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
                                    mVersionTv.setText(jsonObject.getString("version"));
                                    mNewFeatureTv.setText(jsonObject.getString("newFeature"));
                                    mOptimizationTv.setText(jsonObject.getString("optimization"));
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
                        if (AppUtils.getAppVersionName(mContext).compareTo(version) <= 0) {
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
                mAlertDialog.dismiss();
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    public void release() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
