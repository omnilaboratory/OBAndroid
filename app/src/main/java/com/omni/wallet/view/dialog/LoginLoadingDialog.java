package com.omni.wallet.view.dialog;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.ObdLogFileObserver;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.ListPopupWindow.MATCH_PARENT;

public class LoginLoadingDialog {
    private static final String TAG = LoginLoadingDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog mAlertDialog;
    private boolean mCancelable = false;
    private ObjectAnimator mRotationAnimator;
    private DialogInterface.OnDismissListener mDismissListener;
    private ConstantInOB constantInOB = null;
    private ObdLogFileObserver obdLogFileObserver = null;
    private SharedPreferences blockData = null;
    private long totalBlock = 0;

    public LoginLoadingDialog(Context context) {
        this.mContext = context;
        this.constantInOB = new ConstantInOB(context);
        String fileLocal = mContext.getExternalFilesDir(null) + "/obd/" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getLogPath();
        this.obdLogFileObserver = new ObdLogFileObserver(fileLocal, context);
        this.blockData = context.getSharedPreferences("blockData", MODE_PRIVATE);
        this.totalBlock = User.getInstance().getTotalBlock(context);
    }

    @SuppressLint("SetTextI18n")
    private void updateSyncDataView(AlertDialog alertDialog, long syncedHeight) {
        double totalHeight = totalBlock;
        double currentHeight = syncedHeight;
        if (syncedHeight > totalBlock) {
            syncedHeight = totalBlock;
            currentHeight = totalHeight;
        }
        double percent = (currentHeight / totalHeight * 100);

        LinearLayout rvProcessOuter = alertDialog.getViewById(R.id.progress_bar_outer);
        Display alertDisplay = Objects.requireNonNull(mAlertDialog.getWindow()).getWindowManager().getDefaultDisplay();
        int windowWidth = alertDisplay.getWidth();
        LinearLayout.LayoutParams rvOuterParam = new LinearLayout.LayoutParams(windowWidth - 120,12);
        rvProcessOuter.setLayoutParams(rvOuterParam);
        int innerWidth = (int) ((windowWidth - 120) * percent / 100)-1;
        LinearLayout.LayoutParams rlInnerParam = new LinearLayout.LayoutParams(innerWidth, 10);
        LinearLayout rvProcessInner = alertDialog.getViewById(R.id.process_inner);
        rvProcessInner.setLayoutParams(rlInnerParam);
        @SuppressLint("DefaultLocale") String percentString = String.format("%.2f", percent);
        TextView percentTextView = alertDialog.getViewById(R.id.sync_percent);
        percentTextView.setText(percentString + "%");


        TextView syncedBlockNumView = alertDialog.getViewById(R.id.block_num_synced);
        syncedBlockNumView.setText(Long.toString(syncedHeight));

        if (totalHeight == currentHeight) {
            User.getInstance().setSynced(mContext, true);
        }
    }

    @SuppressLint("LongLogTag")
    private final SharedPreferences.OnSharedPreferenceChangeListener currentBlockSharePreferenceChangeListener = (sharedPreferences, key) -> {
        if (key == "currentBlockHeight") {
            int currentHeight = sharedPreferences.getInt("currentBlockHeight", 0);
            updateSyncDataView(mAlertDialog,currentHeight);
        }
    };

    @SuppressLint("DefaultLocale")
    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme_loading)
                    .setWidthAndHeight(MATCH_PARENT, MATCH_PARENT)
                    .setContentView(com.omni.wallet.R.layout.layout_dialog_login_loading)
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(mCancelable)
                    .setOnDismissListener(mDismissListener)
                    .create();
        }

        long syncedHeight = blockData.getInt("currentBlockHeight",0);
        updateSyncDataView(mAlertDialog,syncedHeight);
        TextView blockNumSyncTextView = mAlertDialog.getViewById(R.id.block_num_sync);
        blockNumSyncTextView.setText(String.valueOf(totalBlock));
        new Thread(() -> {
            obdLogFileObserver.startWatching();
        }).run();
        new Thread(() -> {
            blockData.registerOnSharedPreferenceChangeListener(currentBlockSharePreferenceChangeListener);
        });
        ImageView waitingIcon = mAlertDialog.getViewById(R.id.iv_dialog_loading);
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
        // 旋转动画(rotate animate)
        waitingIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRotationAnimator = ObjectAnimator.ofFloat(waitingIcon, "rotation", 0, 359f);
        mRotationAnimator.setInterpolator(new LinearInterpolator());
        mRotationAnimator.setDuration(4000);
        mRotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mRotationAnimator.start();
    }

    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            obdLogFileObserver.stopWatching();
            mAlertDialog.dismiss();
            if (mRotationAnimator != null) {
                mRotationAnimator.cancel();
            }
        }
    }

    public boolean isShowing() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }
}
