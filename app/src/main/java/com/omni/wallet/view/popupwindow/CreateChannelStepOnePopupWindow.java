package com.omni.wallet.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.ui.activity.ChannelsActivity;
import com.omni.wallet.ui.activity.ScanActivity;

import java.util.List;

/**
 * CreateChannelStepOne的弹窗
 */
public class CreateChannelStepOnePopupWindow {
    private static final String TAG = CreateChannelStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    Button speedButton;
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;

    public CreateChannelStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            final View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_create_channel_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            speedButton = rootView.findViewById(R.id.btn_speed);
            speedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectSpeedPopupWindow = new SelectSpeedPopupWindow(mContext);
                    mSelectSpeedPopupWindow.setOnItemClickCallback(new SelectSpeedPopupWindow.ItemCleckListener() {
                        @Override
                        public void onItemClick(View view) {
                            switch (view.getId()) {
                                case R.id.tv_slow:
                                    speedButton.setText(R.string.slow);
                                    break;
                                case R.id.tv_medium:
                                    speedButton.setText(R.string.medium);
                                    break;
                                case R.id.tv_fast:
                                    speedButton.setText(R.string.fast);
                                    break;
                            }
                        }
                    });
                    mSelectSpeedPopupWindow.show(v);
                }
            });
            /**
             * @描述： 扫描二维码
             * @desc: scan qrcode
             */
            rootView.findViewById(R.id.layout_scan_qrcode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onRequestPermissionSuccess() {
                            mBasePopWindow.dismiss();
                            Intent intent = new Intent(mContext, ScanActivity.class);
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onRequestPermissionFailure(List<String> permissions) {
                            LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
                        }

                        @Override
                        public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                            LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
                        }
                    });
                }
            });
            /**
             * @描述： 手动填写
             * @desc: fill in
             */
            rootView.findViewById(R.id.layout_fill_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.GONE);
                    rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.VISIBLE);
                }
            });
            /**
             * @描述： 点击back
             * @desc: click back button
             */
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.findViewById(R.id.lv_create_channel_step_one).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lv_create_channel_step_two).setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击create
             * @desc: click create button
             */
            rootView.findViewById(R.id.layout_create).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    Intent intent = new Intent(mContext, ChannelsActivity.class);
                    mContext.startActivity(intent);
                }
            });

            /**
             * @描述： 点击 cancel
             * @desc: click cancel button
             */
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
