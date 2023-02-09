package com.omni.testnet.framelibrary.view.refreshlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.testnet.baselibrary.view.refreshView.RefreshLayoutHeaderCreator;
import com.omni.testnet.framelibrary.R;


public class LayoutRefreshView extends RefreshLayoutHeaderCreator {
    // 加载数据的ImageView
    private ImageView mRefreshIcon;
    private TextView mRefreshDesc;
    // 加载的动画
    private RotateAnimation mRefreshAnimation;
    // 是否显示最顶部的View
    private boolean mShowTopView = true;

    public LayoutRefreshView() {
    }

    public LayoutRefreshView(boolean showTopView) {
        this.mShowTopView = showTopView;
    }

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_refresh_header_mine_page, parent, false);
        mRefreshIcon = rootView.findViewById(R.id.iv_refresh_icon);
        mRefreshDesc = rootView.findViewById(R.id.tv_refresh_desc);
        mRefreshAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRefreshAnimation.setRepeatCount(-1);
        mRefreshAnimation.setInterpolator(new LinearInterpolator());
        mRefreshAnimation.setDuration(500);
        View topView = rootView.findViewById(R.id.view_refresh_top);
        if (mShowTopView) {
            topView.setVisibility(View.VISIBLE);
        } else {
            topView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onReset() {
        mRefreshDesc.setText(R.string.text_refresh_done);
        mRefreshIcon.clearAnimation();
    }

    @Override
    public void onShowHeader() {
        mRefreshDesc.setText(R.string.text_load_pull);
    }

    @Override
    public void onRefreshing() {
        mRefreshDesc.setText(R.string.text_refreshing);
        // 加载的时候不断旋转
        mRefreshIcon.startAnimation(mRefreshAnimation);
    }

    @Override
    public void onPulling(float currentPos, float lastPos, float refreshPos, boolean isTouch, int state) {
        float rotate = currentPos / refreshPos;
        // 不断下拉的过程中不断的旋转图片
        mRefreshIcon.setRotation(rotate * 360);
        if (currentPos >= refreshPos) {
            mRefreshDesc.setText(R.string.text_load_release);
        } else {
            mRefreshDesc.setText(R.string.text_load_pull);
        }
    }

    @Override
    public void onComplete() {
        mRefreshDesc.setText(R.string.text_refresh_done);
        mRefreshIcon.clearAnimation();
    }


    @Override
    public void onRelease() {
        mRefreshDesc.setText(R.string.text_load_pull);
        mRefreshIcon.clearAnimation();
    }
}
