package com.omni.wallet_mainnet.framelibrary.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.omni.wallet_mainnet.baselibrary.dialog.AlertDialog;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.image.BitmapUtils;
import com.omni.wallet_mainnet.framelibrary.R;
import com.omni.wallet_mainnet.framelibrary.http.callback.DefaultHttpCallback;


/**
 * 图形验证码的对话框
 */

public class VerifyImageDialog {
    private static final String TAG = VerifyImageDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private VerifyImageDialogCallback mCallback;
    private boolean isClickable = true;
    private ImageView mShowImage;
    private EditText mEditText;

    private String mRandomStr;

    public VerifyImageDialog(Context context) {
        this.mContext = context;
    }

    public void setCallback(VerifyImageDialogCallback callback) {
        this.mCallback = callback;
    }

    public void show(String randomStr) {
        this.mRandomStr = randomStr;
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_image_verify)
                    .setCanceledOnTouchOutside(false)
                    .fullWidth()
                    .create();
            mShowImage = mAlertDialog.getViewById(R.id.iv_dialog_image_verify);
            mEditText = mAlertDialog.getViewById(R.id.et_dialog_image_verify);
        }
        mEditText.setText(null);
        mAlertDialog.setOnClickListener(R.id.tv_dialog_image_verify_confirm_btn, new MyClickListener());
        mAlertDialog.setOnClickListener(R.id.tv_dialog_image_verify_cancel_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
        mAlertDialog.show();
        // 接口请求图片
        requestData();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                mCallback.onClickConfirm(mEditText.getText().toString(), mRandomStr);
            }
            mAlertDialog.dismiss();
        }
    }

    private void requestData() {
        if (!isClickable) {
            LogUtils.e(TAG, "获取图片验证码重复点击");
            isClickable = true;
            return;
        }
//        HttpRequestUtils.getVerifyImage(mContext, mRandomStr, new ImageVerifyRequestCallback());
    }

    /**
     * 图形验证码接口回调
     */
    private class ImageVerifyRequestCallback extends DefaultHttpCallback {

        @Override
        public void onSuccess(final Context context, final byte[] result) {
            isClickable = true;
            if (result != null) {
                LogUtils.e(TAG, "====返回的图片数组长度是====>" + result.length);
                postMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap imageBitmap = BitmapUtils.arrayToBitmap(result);
                        mShowImage.setImageBitmap(imageBitmap);
                    }
                });
            }
        }

        @Override
        public void onError(Context context, String errorCode, String errorMsg) {
            super.onError(context, errorCode, errorMsg);
            isClickable = true;
            postMainThread(new Runnable() {
                @Override
                public void run() {
                    // 显示点击刷新
                    mShowImage.setImageResource(R.drawable.bg_dialog_verify_image_iv);
                }
            });
        }
    }

    public interface VerifyImageDialogCallback {
        void onClickConfirm(String verifyCode, String random);
    }
}
