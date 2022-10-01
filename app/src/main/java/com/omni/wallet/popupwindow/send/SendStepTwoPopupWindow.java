package com.omni.wallet.popupwindow.send;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

public class SendStepTwoPopupWindow {
    private static final String TAG = SendStepTwoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    SendStepThreePopupWindow mSendStepThreePopupWindow;

    public SendStepTwoPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_steptwo);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(Gravity.CENTER);
            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
                    mSendStepOnePopupWindow.show(view);
                }
            });
            // 点击next
            rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepThreePopupWindow = new SendStepThreePopupWindow(mContext);
                    mSendStepThreePopupWindow.show(view);
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
