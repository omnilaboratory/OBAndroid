package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 通道详情的弹窗
 * En: ChannelDetailsPopupWindow
 * author: guoyalei
 * date: 2022/10/11
 */
public class ChannelDetailsPopupWindow {
    private static final String TAG = ChannelDetailsPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    CreateChannelStepOnePopupWindow mCreateChannelStepOnePopupWindow;

    public ChannelDetailsPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_channel_details);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            // 设置进度条
            ProgressBar mProgressBar = rootView.findViewById(R.id.progressbar);
            float barValue = (float) ((double) 100 / (double) 700);
            mProgressBar.setProgress((int) (barValue * 100f));
            // 点击create按钮
            rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mCreateChannelStepOnePopupWindow = new CreateChannelStepOnePopupWindow(mContext);
                    mCreateChannelStepOnePopupWindow.show(view);
                }
            });
            // 点击closeChannel按钮
            rootView.findViewById(R.id.layout_close_channel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击底部close
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
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
