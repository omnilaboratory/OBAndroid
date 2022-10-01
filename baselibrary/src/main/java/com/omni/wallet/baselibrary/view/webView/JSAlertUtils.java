package com.omni.wallet.baselibrary.view.webView;

import android.content.Context;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.EditText;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;


/**
 * WebView中弹窗工具类
 */
class JSAlertUtils {

    private Context mContext;

    JSAlertUtils(Context context) {
        this.mContext = context;
    }

    /**
     * 展示带输入框的对话框
     */
    void showPromptDialog(String message, String defaultValue, final JsPromptResult result) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setContentView(R.layout.view_prompt_dialog)
                .setText(R.id.tv_title, message)
                .setText(R.id.et_dialog_content, message)
                .fullWidth()
                .create();
        final EditText editText = dialog.getViewById(R.id.et_dialog_content);
        editText.setHint(defaultValue);
        dialog.getViewById(R.id.tv_dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.cancel();
                dialog.dismiss();
            }
        });
        dialog.getViewById(R.id.tv_dialog_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = editText.getText().toString().trim();
                result.confirm(value);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 展示普通对话框
     */
    void showAlertDialog(String message, final JsResult result) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setContentView(R.layout.view_alert_dialog)
                .setText(R.id.tv_dialog_content, message)
                .fullWidth()
                .create();
        dialog.setOnClickListener(R.id.tv_dialog_confirm_btn, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                result.confirm();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 展示确认的对话框
     */
    void showConfirmDialog(String message, final JsResult result) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setContentView(R.layout.view_confirm_dialog)
                .setText(R.id.tv_dialog_content, message)
                .fullWidth()
                .create();

        dialog.setOnClickListener(R.id.tv_dialog_cancel_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.cancel();
                dialog.dismiss();
            }
        });
        dialog.setOnClickListener(R.id.tv_dialog_confirm_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.confirm();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
