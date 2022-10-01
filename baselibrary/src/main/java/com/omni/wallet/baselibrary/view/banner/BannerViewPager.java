package com.omni.wallet.baselibrary.view.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet.baselibrary.base.DefaultActivityLifecycleCallbacks;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 自定义BannerViewPager
 * 这里边注意Handler的销毁还有与Activity的生命周期的绑定
 */

public class BannerViewPager extends ViewPager {
    private static final String TAG = BannerViewPager.class.getSimpleName();

    // ViewPager开始自动滚动的MsgWhat
    private static final int MSG_SCROLL = 0x0011;
    // 页面切换的时间间隔
    private static final long TIME_PAGE_CUT_DOWN = 3500;
    // 用来获取自定义BannerItemView的Adapter
    private BannerAdapter mAdapter;
    // 自定义的改变页面切换速度的Scroller类
    private BannerScroller mScroller;

    // ViewPager的Adapter
    private PagerAdapter mPagerAdapter;

    // 用来存放被销毁的Item的集合
    private ArrayList<View> mConvertViews;
    // Activity
    private Activity mActivity;
    // 条目点击的监听
    private BannerItemClickListener mBannerItemClickListener;
    // 点击事件判断相关，手指按下的坐标（X）
    public float mPressX = 0;

    public void setBannerItemClickListener(BannerItemClickListener bannerItemClickListener) {
        this.mBannerItemClickListener = bannerItemClickListener;
    }

    public interface BannerItemClickListener {
        void onItemClick(int position);
    }


    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mActivity = (Activity) context;
        // 处理滚动时的动画
        // 1、先获取mScroller
        try {
            // 注意：这里要用ViewPager的class。否则不起作用
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            mScroller = new BannerScroller(context);
            field.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 初始化缓存销毁的Item的集合
        mConvertViews = new ArrayList<>();
    }


    /**
     * 设置Adapter
     *
     * @param adapter 管理View的Adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
        // 为这个ViewPager来设置Adapter
        mPagerAdapter = new BannerPagerAdapter();
        setAdapter(mPagerAdapter);
        // 与Activity的生命周期绑定
        mActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    /**
     * 数据刷新
     */
    public void notifyDataSetChanged() {
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 用来实现ViewPager轮播的Handler
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter == null) {
                return;
            }
            // 判断数量，避免更新之后数据变化，导致这里崩溃
            if (mAdapter.getPageCount() > 1) {
                // 接收到滚动的消息之后，只需要将currentItem设置为下一个即可
                setCurrentItem(getCurrentItem() + 1);
            }
            // 然后继续调用开始滚动方法去发送延时消息
            startRollDelayed();
        }
    };

    /**
     * ViewPager开始自动滚动
     */
    public void startRollDelayed() {
        if (mHandler != null) {
            // 先要将已经存在的消息移除
            mHandler.removeMessages(MSG_SCROLL);
            // 发送消息，通知ViewPager开始滚动
            mHandler.sendEmptyMessageDelayed(MSG_SCROLL, TIME_PAGE_CUT_DOWN);
        }
    }

    /**
     * ViewPager开始自动滚动
     */
    public void startRoll() {
        if (mHandler != null) {
            // 先要将已经存在的消息移除
            mHandler.removeMessages(MSG_SCROLL);
            // 发送消息，通知ViewPager开始滚动
            mHandler.sendEmptyMessage(MSG_SCROLL);
        }
    }

    /**
     * Viewpager停止滚动
     */
    public void stopRoll() {
        if (mHandler != null) {
            // 将已经存在的消息移除
            mHandler.removeMessages(MSG_SCROLL);
        }
    }


    /**
     * 设置切换动画的时间
     *
     * @param scrollerDuration 动画切换时间（ms）
     */
    public void setScrollerDuration(int scrollerDuration) {
        mScroller.setScrollerDuration(scrollerDuration);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下之后取消自动滚动
                stopRoll();
                mPressX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                // 使控件可以响应正常点击事件
                performClick();
                // 手指抬起之后重新开始自动滚动
                startRollDelayed();
                // 手指移动距离小于25就算是点击
                if (Math.abs(ev.getX() - mPressX) < 25) {
                    // banner数量大于0才能有点击事件
                    if (mAdapter.getPageCount() > 0) {
                        int position = getCurrentItem() % mAdapter.getPageCount();
                        if (mBannerItemClickListener != null) {
                            mBannerItemClickListener.onItemClick(position);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 当从窗口中移除的时候会调用这个方法，这时候停止滚动
     */
    @Override
    protected void onDetachedFromWindow() {
        stopRoll();
        super.onDetachedFromWindow();
    }


    /**
     * 被添加到窗口的时候会调用这个方法，这个时候开始滚动
     */
    @Override
    protected void onAttachedToWindow() {
        startRoll();
        super.onAttachedToWindow();
    }

    /**
     * BannerViewPager真正显示和管理View使用的Adapter，用这个Adapter来辅助ViewPager
     */
    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mAdapter.getPageCount() > 1) {
                // 为了实现ViewPager能无限滚动
                return Integer.MAX_VALUE;
            } else {
                return 1;
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
            // 无限轮播的时候获取正确的索引
            if (mAdapter.getPageCount() != 0 && position >= mAdapter.getPageCount()) {
                position = position % mAdapter.getPageCount();
            }
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


    // 在这里把BannerViewPager与Activity的生命周期绑定，以便于控制内存消耗
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks =
            new DefaultActivityLifecycleCallbacks() {

                @Override
                public void onActivityResumed(Activity activity) {
                    // 页面在onResume的时候重新启动Handler发消息开启轮播
                    // 由于这个方法会监听所有的Activity，所以这里要做判断
                    if (activity == mActivity) {
                        startRollDelayed();
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    // 页面在OnPause的时候停止handler发消息停止轮播
                    // 由于这个方法会监听所有的Activity，所以这里要做判断
                    if (activity == mActivity) {
                        stopRoll();
                    }
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    // 页面在OnDestroy的时候清空Handler的消息
                    // 由于这个方法会监听所以的Activity，所以这里要做判断
                    if (activity == mActivity) {
                        stopRoll();
                        mHandler = null;
                        mActivity.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
                    }
                }
            };
}
