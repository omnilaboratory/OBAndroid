package com.omni.testnet.framelibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.omni.testnet.baselibrary.dialog.AlertDialog;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.PermissionChecker;
import com.omni.testnet.baselibrary.utils.PermissionUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.framelibrary.R;

import java.util.List;


/**
 * 拨打电话的对话框，带有权限检查
 */

public class CallNumberDialog {
    private static final String TAG = CallNumberDialog.class.getSimpleName();

    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private String mPhoneNumber;

    public CallNumberDialog(Activity context) {
        this.mActivity = context;
    }

    public void show(String number) {
        this.mPhoneNumber = number;
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mActivity)
                    .setContentView(R.layout.layout_dialog_main)
                    .setOnClickListener(R.id.tv_dialog_right_btn, new MyClickListener())
                    .setOnClickListener(R.id.tv_dialog_left_btn, new MyClickListener())
                    .fullWidth()
                    .create();
        }
        mAlertDialog.setText(R.id.tv_dialog_content, "确认呼叫" + number + "?");
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_dialog_right_btn) {// 呼叫，做权限检查
                PermissionUtils.callPhone(mActivity, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        callPhoneNum();
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        deniedCallPhone();
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        deniedCallPhone();
                    }
                });
            }
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        }
    }

    /**
     * 打电话
     */
    private void callPhoneNum() {
        if (StringUtils.isEmpty(mPhoneNumber)) {
            LogUtils.e(TAG, "电话号码为空");
            return;
        }
        //用intent启动拨打电话
        if (!StringUtils.isEmpty(mPhoneNumber) && PermissionChecker.checkCallPhonePermission(mActivity)) {
            // 后台定义的格式，如果带有#，是需要转分机的，所以将#替换成 ,,  xxxx,,xx 格式
            if (mPhoneNumber.contains("#")) {
                mPhoneNumber = mPhoneNumber.replace("#", ",,");
            }
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber));
            mActivity.startActivity(intent);
        }
    }

    /**
     * 用户拒绝
     */
    private void deniedCallPhone() {
        LogUtils.e(TAG, "用户拒绝");
    }

    public void release() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
