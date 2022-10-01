package com.omni.wallet.gallery.view;

import android.content.Context;
import android.view.View;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.gallery.R;


/**
 * 图片删除的Dialog
 */

public class GalleryDeleteImageDialog {
    private static final String TAG = GalleryDeleteImageDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    private DeleteDialogCallBack mCallBack;

    public GalleryDeleteImageDialog(Context mContext, DeleteDialogCallBack callBack) {
        this.mContext = mContext;
        this.mCallBack = callBack;
    }

    /**
     * 显示删除的Dialog
     */
    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.gallery_layout_dialog_delete_image)
                    .fullWidth()
                    .fromBottom(true)
                    .create();
        }
        // 设置点击事件
        mAlertDialog.setOnClickListener(R.id.gallery_tv_delete_image_confirm, new MyDialogItemClickListener());
        mAlertDialog.setOnClickListener(R.id.gallery_tv_delete_image_cancel, new MyDialogItemClickListener());
        mAlertDialog.show();
    }

    /**
     * 条目点击事件
     */
    private class MyDialogItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.gallery_tv_delete_image_confirm) {// 删除
                if (mCallBack != null) {
                    mCallBack.onClickDelItem();
                }
            }
            mAlertDialog.dismiss();
        }
    }

    /**
     * 回调
     */
    public interface DeleteDialogCallBack {
        void onClickDelItem();
    }
}
