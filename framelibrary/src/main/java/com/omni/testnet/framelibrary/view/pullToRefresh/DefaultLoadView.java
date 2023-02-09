package com.omni.testnet.framelibrary.view.pullToRefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.testnet.baselibrary.view.recyclerView.pullToRefresh.LoadViewCreator;
import com.omni.testnet.framelibrary.R;


/**
 * RecyclerView的上拉加载更多
 */

public class DefaultLoadView extends LoadViewCreator {
    private static final String TAG = DefaultLoadView.class.getSimpleName();
    // 整体的父布局
    private View mRootView;
    // 加载的图标
    private ImageView mLoadIcon;
    // 文字描述
    private TextView mLoadText;
    // 是否没有更多数据了
    private boolean mShowNoData;
    // 加载的动画
    private RotateAnimation mLoadingAnimation;


    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_load_footer, parent, false);
        mLoadIcon = mRootView.findViewById(R.id.tv_load_icon);
        mLoadText = mRootView.findViewById(R.id.tv_load_text);
        mLoadingAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mLoadingAnimation.setRepeatCount(-1);
        mLoadingAnimation.setInterpolator(new LinearInterpolator());
        mLoadingAnimation.setDuration(500);
        return mRootView;
    }

    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        // 拉上去了就隐藏文字显示图标
        if (currentDragHeight > 0) {
            mLoadText.setVisibility(View.GONE);
            mLoadIcon.setVisibility(View.VISIBLE);
        }
        float rotate = ((float) currentDragHeight) / refreshViewHeight;
        // 不断下拉的过程中不断的旋转图片
        mLoadIcon.setRotation(rotate * 360);
    }

    @Override
    public void onCancelLoad() {
        if (mShowNoData) {
            mLoadText.setText(R.string.text_load_in_the_end);
        } else {
            mLoadText.setText(R.string.text_load_more);
        }
        mLoadText.setVisibility(View.VISIBLE);
        mLoadIcon.setVisibility(View.GONE);
    }

    @Override
    public void onLoading() {
        mLoadText.setVisibility(View.VISIBLE);
        mLoadText.setText(R.string.text_loading);
        mLoadIcon.setVisibility(View.VISIBLE);
        // 加载的时候不断旋转
        mLoadIcon.startAnimation(mLoadingAnimation);
    }

    @Override
    public void onStopLoad() {
        if (mShowNoData) {
            mLoadText.setText(R.string.text_load_in_the_end);
        } else {
            mLoadText.setText(R.string.text_load_more);
        }
        mLoadText.setVisibility(View.VISIBLE);
        mLoadIcon.setVisibility(View.GONE);
        // 停止加载的时候清除动画
        mLoadIcon.setRotation(0);
        mLoadIcon.clearAnimation();
    }

    /**
     * 上拉的时候是否还有数据
     */
    @Override
    public void setLoadNoData(boolean isNoData) {
        mShowNoData = isNoData;
        if (mLoadText == null) {
            return;
        }
        if (mShowNoData) {
            mLoadText.setText(R.string.text_load_in_the_end);
        } else {
            mLoadText.setText(R.string.text_load_more);
        }
    }
}
