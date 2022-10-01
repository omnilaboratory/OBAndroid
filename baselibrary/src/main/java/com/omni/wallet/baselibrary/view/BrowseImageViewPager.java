package com.omni.wallet.baselibrary.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.banner.BannerAdapter;

import java.util.ArrayList;

/**
 * 浏览图片的Viewpager，可以无限滚动
 */

public class BrowseImageViewPager extends ViewPager {

    private static final String TAG = BrowseImageViewPager.class.getSimpleName();
    private BannerAdapter mAdapter;
    private BrowseImageAdapter mPageAdapter;
    // 用来存放被销毁的Item的集合
    private ArrayList<View> mConvertViews;
    // 是否无限滚动
    private boolean mScrollingState = true;
    // 宽度比例
    private double mWidthProportion = 0;
    // 高度比例
    private double mHeightProportion = 0;


    public BrowseImageViewPager(Context context) {
        this(context, null);
    }

    public BrowseImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化缓存销毁的Item的集合
        mConvertViews = new ArrayList<>();
    }

    public void setWidthProportion(double widthProportion) {
        this.mWidthProportion = widthProportion;
    }

    public void setHeightProportion(double heightProportion) {
        this.mHeightProportion = heightProportion;
    }

    /**
     * 此方法用来捕获图片缩放过程中的索引越界异常
     * java.lang.IllegalArgumentException: pointerIndex out of range
     * 不重写此方法的话，图片缩放的时候会崩溃
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置是否可以无限滚动
     */
    public void setUnlimitedScrolling(boolean state) {
        this.mScrollingState = state;
    }

    /**
     * 设置Adapter
     *
     * @param adapter 管理View的Adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
        // 为这个ViewPager来设置Adapter
        mPageAdapter = new BrowseImageAdapter();
        setAdapter(mPageAdapter);
        post(new Runnable() {
            @Override
            public void run() {
                if (mWidthProportion != 0 && mHeightProportion != 0) {
                    // 重置高度
                    int width = getMeasuredWidth();
                    double height = width / mWidthProportion * mHeightProportion;
                    getLayoutParams().height = (int) height;
                    LogUtils.e(TAG, "===============》宽度：" + width + "高度：" + height);
                }
                if (mCallback != null) {
                    mCallback.onPagerDrawComplete(getLayoutParams().height);
                }
            }
        });
    }

    /***
     * 通知更新
     */
    public void notifyDataSetChanged() {
        mPageAdapter.notifyDataSetChanged();
    }

    /**
     * BannerViewPager真正显示和管理View使用的Adapter，用这个Adapter来辅助ViewPager
     */
    private class BrowseImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mScrollingState) {
                // 为了实现ViewPager能无限滚动
                return Integer.MAX_VALUE;
            } else {
                return mAdapter.getPageCount();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // 谷歌推荐这么写
            return view == object;
        }

        /**
         * ViewPager的Adapter在创建View的时候会回调这个方法，所以我们需要在Adapter中覆盖这个方法，
         * 返回我们自定义的View
         *
         * @param container 上下文
         * @param position  创建View的索引
         * @return 返回我们创建的View
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View bannerItemView = mAdapter.getView(position, getConvertView());// 利用Adapter获取BannerItemView
            // 将获取到的View添加到ViewPager中
            container.addView(bannerItemView);
            return bannerItemView;
        }

        /**
         * Adapter销毁里面View的方法，我们需要在这里去将我们的View从Adapter中移除并销毁
         *
         * @param container 就是ViewPager
         * @param position  需要销毁的View的索引
         * @param object    需要销毁的View
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 将当前位置的View移除
            container.removeView((View) object);
            // 将销毁的Item放到缓存的集合中缓存起来，以便于重复利用，减小系统压力
            mConvertViews.add((View) object);
        }
    }

    // 在缓存的销毁的Item的集合中获取可以使用的ItemView
    // 注意：这里由于快速滑动的时候会出现有的Item已经被添加到复用的缓存中了，但是这时候还需要
    // 缓存的itemView的情况，所以这个方法返回的时候要判断获取到的这个View是否有parent，如果没有才可以返回
    private View getConvertView() {
        for (int i = 0; i < mConvertViews.size(); i++) {
            View convertView = mConvertViews.get(i);
            // 判断是否已经被使用
            if (convertView.getParent() == null) {
                return convertView;
            }
        }
        return null;
    }

    private DrawViewCallback mCallback;

    public void setCallback(DrawViewCallback callback) {
        this.mCallback = callback;
    }

    public interface DrawViewCallback {
        // ViewPager绘制完成的时候回调，把ViewPager的高度回调回去
        void onPagerDrawComplete(int viewPagerHeight);
    }
}
