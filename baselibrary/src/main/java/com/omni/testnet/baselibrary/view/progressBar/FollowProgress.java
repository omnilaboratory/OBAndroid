package com.omni.testnet.baselibrary.view.progressBar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.utils.LogUtils;


/**
 * 文字跟随进度移动的ProgressBar
 */

public class FollowProgress extends LinearLayout {
    private static final String TAG = FollowProgress.class.getSimpleName();
    private Context mContext;

    private ProgressBar mProgressBar;
    private TextView mValueTv;

    public FollowProgress(Context context) {
        this(context, null);
    }

    public FollowProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FollowProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_follow_progressbar, this);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mValueTv = rootView.findViewById(R.id.progress_bar_value);
    }

    public void setProgress(int progress, String showText) {
        mProgressBar.setProgress(progress);
        mValueTv.setText(showText);
        setPosWay1();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setPos();
        }
    }

    private void setPosWay1() {
        mValueTv.post(new Runnable() {
            @Override
            public void run() {
                setPos();
            }
        });
    }

    /**
     * 设置进度显示在对应的位置
     */
    private void setPos() {
        int w = getMeasuredWidth();
        LogUtils.e(TAG, "ProgressBar的宽度是：" + w);
        MarginLayoutParams params = (MarginLayoutParams) mValueTv.getLayoutParams();
        int pro = mProgressBar.getProgress();
        int tW = mValueTv.getWidth();
        if (w * pro / 100 + tW * 0.3 > w) {
            params.leftMargin = (int) (w - tW * 1.1);
        } else if (w * pro / 100 < tW * 0.7) {
            params.leftMargin = 0;
        } else {
            params.leftMargin = (int) (w * pro / 100 - tW * 0.7);
        }
        mValueTv.setLayoutParams(params);
    }
}
