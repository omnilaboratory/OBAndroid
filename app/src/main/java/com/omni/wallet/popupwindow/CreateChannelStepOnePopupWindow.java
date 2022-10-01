package com.omni.wallet.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * CreateChannelStepOne的弹窗
 */
public class CreateChannelStepOnePopupWindow {
    private static final String TAG = CreateChannelStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    CreateChannelStepTwoPopupWindow mCreateChannelStepTwoPopupWindow;

    public CreateChannelStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_channel_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(Gravity.CENTER);
            // 点击scan qrcode
            rootView.findViewById(R.id.layout_scan_qrcode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击fill in
            rootView.findViewById(R.id.layout_fill_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mCreateChannelStepTwoPopupWindow = new CreateChannelStepTwoPopupWindow(mContext);
                    mCreateChannelStepTwoPopupWindow.show(view);
                }
            });
            // 点击底部cancel
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
