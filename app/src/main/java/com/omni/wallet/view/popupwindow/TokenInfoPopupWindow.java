package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

/**
 * 汉: 代币详情的弹窗
 * En: TokenInfoPopupWindow
 * author: guoyalei
 * date: 2022/11/20
 */
public class TokenInfoPopupWindow {
    private static final String TAG = TokenInfoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public TokenInfoPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_token_info);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
            RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share);
            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击copy
            rootView.findViewById(R.id.layout_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            // 点击share to
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            // 点击facebook
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击twitter
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
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
