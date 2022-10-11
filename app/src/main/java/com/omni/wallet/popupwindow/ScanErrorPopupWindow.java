package com.omni.wallet.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 扫码错误的弹窗
 * En: ScanErrorPopupWindow
 * author: guoyalei
 * date: 2022/10/10
 */
public class ScanErrorPopupWindow {
    private static final String TAG = ScanErrorPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mFundPopupWindow;

    public ScanErrorPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view) {
        if (mFundPopupWindow == null) {
            mFundPopupWindow = new BasePopWindow(mContext);
            View rootView = mFundPopupWindow.setContentView(R.layout.layout_popupwindow_scan_error);
            mFundPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mFundPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mFundPopupWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mFundPopupWindow.setAnimationStyle(Gravity.CENTER);
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFundPopupWindow.dismiss();
                }
            });
            if (mFundPopupWindow.isShowing()) {
                return;
            }
            mFundPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void release() {
        if (mFundPopupWindow != null) {
            mFundPopupWindow.dismiss();
            mFundPopupWindow = null;
        }
    }
}
