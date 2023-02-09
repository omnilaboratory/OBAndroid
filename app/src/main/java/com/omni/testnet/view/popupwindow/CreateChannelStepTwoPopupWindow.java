package com.omni.testnet.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.testnet.R;
import com.omni.testnet.baselibrary.view.BasePopWindow;
import com.omni.testnet.ui.activity.channel.ChannelsActivity;

/**
 * CreateChannelStepTwo的弹窗
 */
public class CreateChannelStepTwoPopupWindow {
    private static final String TAG = CreateChannelStepTwoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;

    public CreateChannelStepTwoPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_channel_steptwo);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
                    mCreateChannelStepOnePopupWindow.show(view, 0, "123", "123");
                }
            });
            // 点击create
            rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    mContext.startActivity(intent);
                }
            });
            // 点击cancel
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
