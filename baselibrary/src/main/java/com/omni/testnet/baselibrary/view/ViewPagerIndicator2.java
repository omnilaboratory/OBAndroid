package com.omni.testnet.baselibrary.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.utils.DisplayUtil;


/**
 * 跟随ViewPager滑动的指示器
 */

public class ViewPagerIndicator2 extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private static final String TAG = ViewPagerIndicator2.class.getSimpleName();
    private ViewPager mViewPager;
    private Context mContext;
    private ViewPagerIndicator mViewPagerIndicator;
    private ImageView mCoverImageView;
    private int mSum;
    private OnPageChangeListener mPageChangeListener;

    public ViewPagerIndicator2(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setViewPager(ViewPager viewPager, int sum) {
        this.mViewPager = viewPager;
        this.mSum = sum;
        draw();
        this.mViewPager.addOnPageChangeListener(this);
    }

    public void draw() {
        if (mViewPager != null && mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() != 0) {
            mViewPagerIndicator = new ViewPagerIndicator(mContext);
            mViewPagerIndicator.setLength(mSum);
            mViewPagerIndicator.setSelected(0, R.drawable.icon_banner_dot, R.drawable.icon_banner_dot);
            addView(mViewPagerIndicator);

            mCoverImageView = new ImageView(mContext);
            mCoverImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_banner_dot_over));
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = DisplayUtil.dp2px(mContext, 6);
            params.rightMargin = DisplayUtil.dp2px(mContext, 6);
            mCoverImageView.setLayoutParams(params);
            addView(mCoverImageView);


        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mSum > 1) {
            int dis = (int) mViewPagerIndicator.getDistance();
            position = position % mSum;
            float leftMargin;
            if (mViewPagerIndicator.getSelected() == 0 && position == mViewPagerIndicator.getSum() - 1) {
                leftMargin = 0;
            } else if (mViewPagerIndicator.getSelected() == mViewPagerIndicator.getSum() - 1 && position == mViewPagerIndicator.getSum() - 1) {
                leftMargin = dis * (position);
            } else {
                leftMargin = dis * (position + positionOffset);
            }
            LayoutParams params = (LayoutParams) mCoverImageView.getLayoutParams();
            params.leftMargin = Math.round(leftMargin) + DisplayUtil.dp2px(mContext, 6);
            mCoverImageView.setLayoutParams(params);
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mViewPagerIndicator.setSelected(position);
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void addOnPageChangeListener(OnPageChangeListener onPageChangelistener) {
        this.mPageChangeListener = onPageChangelistener;
    }

    public interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
}
