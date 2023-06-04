package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 选择收款或者支付类型的弹窗
 * En: SelectReceiveOrSendTypePopupWindow
 * author: guoyalei
 * date: 2023/6/2
 */
public class SelectReceiveOrSendTypePopupWindow {
    private static final String TAG = SelectReceiveOrSendTypePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;

    public SelectReceiveOrSendTypePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(View view, int type) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_receive_or_send_type);
            rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            mBasePopWindow.setTouchable(true);
            mBasePopWindow.setOutsideTouchable(true);
            mBasePopWindow.setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });

            TextView mTypeOne = rootView.findViewById(R.id.tv_type_one);
            TextView mTypeTwo = rootView.findViewById(R.id.tv_type_two);
            if (type == 1) {
                mTypeOne.setText("Via Invoice");
                mTypeTwo.setText("Via BTC address");
            } else if (type == 2) {
                mTypeOne.setText("Pay Invoice");
                mTypeTwo.setText("To BTC address");
            }
            mTypeOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            mTypeTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v);
                    }
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            int width = mBasePopWindow.getWidth();
            int[] xy = new int[2];
            view.getLocationInWindow(xy);
            mBasePopWindow.showAtLocation(view, Gravity.NO_GRAVITY,
                    xy[0] + (DisplayUtil.dp2px(mContext, width) - view.getWidth()) / 2, xy[1] - DisplayUtil.dp2px(mContext, 80));
        }
    }

    public void setOnItemClickCallback(ItemCleckListener callback) {
        this.mCallback = callback;
    }

    public interface ItemCleckListener {
        void onItemClick(View view);
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}