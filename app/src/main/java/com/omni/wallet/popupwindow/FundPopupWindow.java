package com.omni.wallet.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * Fund的弹窗
 */
public class FundPopupWindow {
    private static final String TAG = FundPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mFundPopupWindow;

    public FundPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view) {
        if (mFundPopupWindow == null) {
            mFundPopupWindow = new BasePopWindow(mContext);
            View rootView = mFundPopupWindow.setContentView(R.layout.layout_popupwindow_fund);
            mFundPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mFundPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mFundPopupWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mFundPopupWindow.setAnimationStyle(Gravity.CENTER);
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
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
