package com.omni.wallet_mainnet.framelibrary.view.pullToRefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet_mainnet.baselibrary.view.recyclerView.pullToRefresh.RefreshViewCreator;
import com.omni.wallet_mainnet.framelibrary.R;


/**
 * RecyclerView的上拉加载更多
 */

public class DefaultRefreshView extends RefreshViewCreator {
    private static final String TAG = DefaultRefreshView.class.getSimpleName();

    public DefaultRefreshView() {
    }

    // 加载数据的ImageView
    private ImageView mRefreshIcon;
    private TextView mRefreshDesc;
    // 加载的动画
    private RotateAnimation mRefreshAnimation;


    protected int getLayoutId() {
        return R.layout.view_refresh_header;
    }

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        View refreshView = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
        mRefreshIcon = refreshView.findViewById(R.id.iv_refresh_icon);
        mRefreshDesc = refreshView.findViewById(R.id.tv_refresh_desc);
        mRefreshAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRefreshAnimation.setRepeatCount(-1);
        mRefreshAnimation.setInterpolator(new LinearInterpolator());
        mRefreshAnimation.setDuration(500);
        return refreshView;
    }

    /*@param currentDragHeight    当前拖动的高度
    * @param refreshViewHeight    总的刷新控件高度
    * @param currentRefreshStatus 当前状态*/
    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        float rotate = ((float) currentDragHeight) / refreshViewHeight;
        // 不断下拉的过程中不断的旋转图片
        mRefreshIcon.setRotation(rotate * 360);
        if (currentDragHeight >= refreshViewHeight) {
            mRefreshDesc.setText(R.string.text_load_release);
        } else {
            mRefreshDesc.setText(R.string.text_load_pull);
        }
    }

    @Override
    public void onCancelRefresh() {
        mRefreshDesc.setText(R.string.text_load_pull);
        mRefreshIcon.clearAnimation();
    }

    @Override
    public void onRefreshing() {
        mRefreshDesc.setText(R.string.text_refreshing);
        // 加载的时候不断旋转
        mRefreshIcon.startAnimation(mRefreshAnimation);
    }

    @Override
    public void onStopRefresh() {
        mRefreshDesc.setText(R.string.text_refresh_done);
        mRefreshIcon.clearAnimation();
    }

    @Override
    public void onRelease() {
        if (mRefreshIcon != null) {
            mRefreshIcon.clearAnimation();
        }
    }
}
