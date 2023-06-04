package com.omni.wallet_mainnet.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.PermissionUtils;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;
import com.omni.wallet_mainnet.ui.activity.ScanActivity;

import java.util.List;

/**
 * 汉: 扫码错误的弹窗
 * En: ScanErrorPopupWindow
 * author: guoyalei
 * date: 2022/10/10
 */
public class ScanErrorPopupWindow {
    private static final String TAG = ScanErrorPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public ScanErrorPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_scan_error);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // click try again button
            // 点击Try Again
            rootView.findViewById(R.id.layout_scan).setOnClickListener(new View.OnClickListener() {
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
            // click close button at bottom
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
