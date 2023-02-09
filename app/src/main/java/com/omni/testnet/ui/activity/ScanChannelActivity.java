package com.omni.testnet.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.omni.testnet.R;
import com.omni.testnet.base.AppBaseActivity;
import com.omni.testnet.baselibrary.utils.DisplayUtil;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StatusBarUtil;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.utils.ToastUtils;
import com.omni.testnet.lightning.LightningNodeUri;
import com.omni.testnet.lightning.LightningParser;
import com.omni.testnet.thirdsupport.zxing.CaptureHelper;
import com.omni.testnet.thirdsupport.zxing.OnCaptureCallback;
import com.omni.testnet.thirdsupport.zxing.ViewfinderView;
import com.omni.testnet.thirdsupport.zxing.camera.CameraConfig;
import com.omni.testnet.utils.Wallet;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 汉: 扫描通道二维码的页面
 * En: ScanChannelActivity
 * author: guoyalei
 * date: 2022/11/30
 */
public class ScanChannelActivity extends AppBaseActivity {
    private static final String TAG = ScanActivity.class.getSimpleName();

    @BindView(R.id.layout_parent)
    RelativeLayout mParentLayout;
    @BindView(R.id.view_scan_page_top)
    public View mTopView;
    @BindView(R.id.surface_view_scan)
    public SurfaceView mSurfaceView;
    @BindView(R.id.view_scan_finder)
    public ViewfinderView mFinderView;

    @BindView(R.id.layout_take_picture_light)
    public LinearLayout mLightLayout;// 手电筒的布局
    @BindView(R.id.icon_take_picture_light)
    public ImageView mLightIv;// 手电筒的图标

    private CaptureHelper mCaptureHelper;


    public static final String KEY_SCAN_CODE = "balanceAmountKey";
    int scanCode;

    @Override
    protected void getBundleData(Bundle bundle) {
        scanCode = bundle.getInt(KEY_SCAN_CODE);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initView() {
        mTopView.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(mContext);
        mCaptureHelper = new CaptureHelper(this, mSurfaceView, mFinderView);
        mCaptureHelper.setOnCaptureCallback(new MyCaptureCallback());
        mCaptureHelper.playBeep(true);
        mCaptureHelper.vibrate(true);
        mCaptureHelper.onCreate();
        // 设置手电筒的位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLightLayout.getLayoutParams();
        params.bottomMargin = DisplayUtil.getScreenHeight(mContext) / 2 - mFinderView.getFrameHeight() / 2 - DisplayUtil.getViewHeight(mLightLayout);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Reset light button style before show page
         * 展示之前重置闪关灯的按钮的样式
         */

        updateFlashIvSow();

        mCaptureHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    /**
     * click the cancel button at bottom
     * 点击底部CANCEL
     */
    @OnClick(R.id.layout_cancel)
    public void clickCancel(View view) {
        finish();
    }

    /**
     * click the light button for turn on or turn off the light
     * 点击打开关闭闪光灯
     */
    @OnClick(R.id.layout_take_picture_light)
    public void clickFlashChange(View view) {
        // 切换闪光灯模式
        flashChange();
        // 更新闪光灯图标样式
        updateFlashIvSow();
    }

    /**
     * light driver
     * 闪光灯
     */
    protected void flashChange() {
        if (mCaptureHelper.getFlashMode() == CameraConfig.FLASH_MODE_OFF
                || mCaptureHelper.getFlashMode() == CameraConfig.FLASH_MODE_AUTO) {
            mCaptureHelper.setFlashMode(CameraConfig.FLASH_MODE_TORCH);
        } else {
            mCaptureHelper.setFlashMode(CameraConfig.FLASH_MODE_OFF);
        }
    }

    /**
     * reset the button style for light
     * 更新闪光灯的按钮样式
     */
    private void updateFlashIvSow() {
        int flashMode = mCaptureHelper.getFlashMode();
        int icon;
        if (flashMode == CameraConfig.FLASH_MODE_TORCH) {
            icon = R.mipmap.icon_take_picture_light_on;
        } else {
            icon = R.mipmap.icon_take_picture_light_off;
        }
        mLightIv.setImageResource(icon);
    }

    /**
     * the callback function for scan qrcode
     * 扫码回调
     */
    private class MyCaptureCallback implements OnCaptureCallback {

        @Override
        public boolean onResultCallback(String result) {
            LogUtils.e(TAG, "========扫码获取的值为===========>" + result);
            if (!StringUtils.isEmpty(result)) {
                LightningNodeUri nodeUri = LightningParser.parseNodeUri(result);
                if (nodeUri != null) { //开通通道
                    Wallet.getInstance().broadcastScanChannelUpdated(result);
                    finish();
                } else {
                    ToastUtils.showToast(mContext, "Please scan the correct QR code");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCaptureHelper.restartPreviewAndDecode();
                        }
                    }, 2500);
                }
            } else {
                ToastUtils.showToast(mContext, "Please scan the correct QR code");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCaptureHelper.restartPreviewAndDecode();
                    }
                }, 2500);
            }
            return true;
        }

        @Override
        public void onLightChange(boolean dark) {
            LogUtils.d(TAG, "光线变化：" + dark);
            if (dark) {
                mLightLayout.setVisibility(View.VISIBLE);
            } else {
                // 闪光灯未打开才隐藏
                if (mCaptureHelper.getFlashMode() != CameraConfig.FLASH_MODE_TORCH) {
                    mLightLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        mCaptureHelper.onDestroy();
        super.onDestroy();
    }
}
