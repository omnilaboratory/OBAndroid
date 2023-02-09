package com.omni.testnet.baselibrary.dialog;

import android.content.Context;
import android.widget.SeekBar;

import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.utils.ToastUtils;
import com.omni.testnet.baselibrary.view.SlideVerifyImageView;


/**
 * 滑动验证的Dialog
 */

public class SlideVerifyDialog {
    private static final String TAG = SlideVerifyDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;

    public SlideVerifyDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_slide_verify)
                    .fullWidth()
                    .create();
        }
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
        final SlideVerifyImageView imageView = mAlertDialog.getViewById(R.id.view_slide);
        final SeekBar seekBar = mAlertDialog.getViewById(R.id.sb_dy);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                imageView.setUnitMoveDistance(imageView.getAverageDistance(seekBar.getMax()) * i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                imageView.testPuzzle();
            }
        });
        imageView.setListener(new SlideVerifyImageView.ImageVerifyListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast(mContext, "验证成功");
                imageView.reSet();
                seekBar.setProgress(0);
                mAlertDialog.dismiss();
            }

            @Override
            public void onFail() {
                ToastUtils.showToast(mContext, "验证失败");
                seekBar.setProgress(0);
                imageView.reSet();
            }
        });
    }
}
