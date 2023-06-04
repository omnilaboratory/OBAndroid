package com.omni.wallet_mainnet.baselibrary.view.recyclerView.pullToRefresh;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.wrap.WrapRecyclerView;


/**
 * 下拉刷新上拉加载的RecyclerView
 */

public class PullToRefreshRecyclerView extends WrapRecyclerView {
    private static final String TAG = PullToRefreshRecyclerView.class.getSimpleName();
    //************公用************//
    // 手指按下的Y位置
    private int mFingerDownY;
    // 手指按下的X位置
    private int mFingerDownX;
    // 手指拖拽的阻力指数
    private float mDragIndex = 0.35f;
    // 当前是否正在拖动
    private boolean mCurrentDrag = false;
    // 是否开启下拉刷新
    private boolean mIsRefresh = false;
    // 是否开启上上拉加载
    private boolean mIsCanLoad = false;
    // 手指按下的时候的顶部Margin
    private int mPressMarginTop;
    // 处理刷新和加载的回调监听
    private PullToRefreshListener mListener;
    // 滚动到底部的时候是否自动上拉加载
    private boolean mAutoLoad = true;
    // 是否上拉加载无数据了
    private boolean mIsLoadNoData = false;
    // 是否没有请求到数据 默认为true 不打开上拉加载，只有在请求到数据的时候才打开
    private boolean mIsRequestNoData = true;


    //************下拉************//
    // 下拉刷新的辅助类
    private RefreshViewCreator mRefreshCreator;
    // 下拉刷新头部的高度
    private int mRefreshViewHeight = 0;
    // 下拉刷新的头部View
    private View mRefreshView;
    // 当前下拉刷新的状态
    private int mCurrentRefreshStatus;
    // 默认下拉刷新状态
    public static int REFRESH_STATUS_NORMAL = 0x0011;
    // 下拉刷新状态
    public static int REFRESH_STATUS_PULL_DOWN_REFRESH = 0x00022;
    // 松开刷新状态
    public static int REFRESH_STATUS_LOOSEN_REFRESHING = 0x00033;
    // 正在刷新状态
    public static int REFRESH_STATUS_REFRESHING = 0x00044;

    //************上拉************//
    // 上拉加载更多的辅助类
    private LoadViewCreator mLoadCreator;
    // 上拉加载更多的底部的高度
    private int mLoadViewHeight = 0;
    // 上拉加载更多的头部View
    private View mLoadView;
    // 当前上拉加载的状态
    private int mCurrentLoadStatus = LOAD_STATUS_NORMAL;
    // 默认上拉加载状态
    public static int LOAD_STATUS_NORMAL = 0x0011;
    // 上拉加载更多状态
    public static int LOAD_STATUS_PULL_DOWN_REFRESH = 0x0022;
    // 松开加载更多状态
    public static int LOAD_STATUS_LOOSEN_LOADING = 0x0033;
    // 正在加载更多状态
    public static int LOAD_STATUS_LOADING = 0x0044;
    // 上拉加载没有跟多数据
    public static int LOAD_STATUS_LOAD_NO_DATA = 0x0055;

    /***************************************************************/
    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new RecyclerScrollViewListener());
    }

    // 设置是否自动完成上拉
    public void setAutoLoadEnable(boolean enable) {
        this.mAutoLoad = enable;
    }

    // 设置下拉刷新是否可用
    public void setRefreshEnable(boolean enable) {
        this.mIsRefresh = enable;
    }

    // 设置上拉加载是否可用
    public void setLoadEnable(boolean enable) {
        this.mIsCanLoad = enable;
    }

    // 先处理下拉刷新，同时考虑刷新列表的不同风格样式，确保这个项目还是下一个项目都能用
    // 所以我们不能直接添加View，需要利用辅助类去创建下拉刷新的View
    public void addRefreshViewCreator(RefreshViewCreator refreshCreator) {
        this.mRefreshCreator = refreshCreator;
        addRefreshView();
    }

    // 先处理上拉加载更多，同时考虑加载列表的不同风格样式，确保这个项目还是下一个项目都能用
    // 所以我们不能直接添加View，需要利用辅助类
    public void addLoadViewCreator(LoadViewCreator loadCreator) {
        this.mLoadCreator = loadCreator;
        addLoadView();
    }


    /**
     * 获取上拉加载的FooterView
     */
    public LoadViewCreator getLoadViewCreator() {
        return this.mLoadCreator;
    }

    /**
     * 添加头部的刷新View
     */
    private void addRefreshView() {
        if (!mIsRefresh) {
            return;
        }
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null && mRefreshCreator != null) {
            // 添加头部的刷新View
            View refreshView = mRefreshCreator.getRefreshView(getContext(), this);
            if (refreshView != null) {
                addHeaderView(refreshView);
                this.mRefreshView = refreshView;
            }
        }
    }

    /**
     * 添加底部加载更多View
     */
    private void addLoadView() {
        if (!mIsCanLoad) {
            return;
        }
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null && mLoadCreator != null) {
            // 添加底部加载更多View
            View loadView = mLoadCreator.getLoadView(getContext(), this);
            if (loadView != null) {
                addFooterView(loadView);
                this.mLoadView = loadView;
            }
        }
    }

    /**
     * 移除footer
     */
    public void removeLoadView() {
        if (mLoadView != null) {
            removeFooterView(mLoadView);
            mLoadView = null;
            mLoadCreator = null;
        }
    }


    /**
     * 自动刷新
     */
    public void autoRefresh() {
        autoRefresh(200);
    }

    /**
     * 自动刷新
     */
    public void autoRefresh(int duration) {
        postDelayed(new AutoRefreshRunnable(), duration);
    }

    /**
     * 自动刷新相关
     * 延时弹出RefreshHeader
     */
    private class AutoRefreshRunnable implements Runnable {

        @Override
        public void run() {
            if (mRefreshView == null) {
                return;
            }
            // 当前的marginTop（负的）
            int currentTopMargin = ((ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
            // 最终的顶部间距
            int finalTopMargin = 0;
            // 差值，作为动画时间
            int distance = finalTopMargin - currentTopMargin;
            if (distance < 0) {
                return;
            }
            // header自动滚出来
            ValueAnimator animator = ObjectAnimator.ofFloat(currentTopMargin, finalTopMargin).setDuration(distance);
            animator.addUpdateListener(new MyUpdateListener());
            animator.start();
        }
    }

    /**
     * 自动刷新动画监听
     */
    private class MyUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 这玩意儿必须调用，不然只是设置了顶边距不会自动滚动下去
            scrollToPosition(0);
            // 获取随机值
            float currentTopMargin = (float) animation.getAnimatedValue();
            // 状态置为正在下拉
            mCurrentRefreshStatus = REFRESH_STATUS_PULL_DOWN_REFRESH;
            //更改顶部边距
            setRefreshViewMarginTop((int) currentTopMargin);
            // header全部滚动显示出来了，就调用刷新方法
            if (currentTopMargin >= 0) {
                // 状态置为正在刷新
                mCurrentRefreshStatus = REFRESH_STATUS_REFRESHING;
                // 回调正在刷新进行数据请求
                if (mListener != null) {
                    mListener.onRefresh();
                }
                // 回调正在刷新，更新UI
                if (mRefreshCreator != null) {
                    mRefreshCreator.onRefreshing();
                }
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置 ,之所以写在dispatchTouchEvent那是因为如果我们处理了条目点击事件，
                // 那么就不会进入onTouchEvent里面，所以只能在这里获取
                mFingerDownY = (int) ev.getRawY();
                mFingerDownX = (int) ev.getRawX();
                if (mRefreshView != null) {
                    // 按下的时候获取当前的顶部margin
                    mPressMarginTop = ((ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentDrag) {
                    if (mIsRefresh) {
                        restoreRefreshView();
                    }
                    if (mIsCanLoad) {
                        restoreLoadView();
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 重置当前刷新状态状态
     */
    private void restoreRefreshView() {
        if (!mIsRefresh || mRefreshView == null) {
            return;
        }
        int currentTopMargin = ((ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
        int finalTopMargin = -mRefreshViewHeight + 1;
        if (mCurrentRefreshStatus == REFRESH_STATUS_LOOSEN_REFRESHING) {
            finalTopMargin = 0;
            mCurrentRefreshStatus = REFRESH_STATUS_REFRESHING;
            if (mRefreshCreator != null) {
                mRefreshCreator.onRefreshing();
            }
            if (mListener != null) {
                mListener.onRefresh();
            }
        } else if (mCurrentRefreshStatus == REFRESH_STATUS_PULL_DOWN_REFRESH) {
            if (mRefreshCreator != null) {
                mRefreshCreator.onCancelRefresh();
            }
        } else if (mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING) {
            if (mRefreshCreator != null) {
                mRefreshCreator.onRefreshing();
            }
            if (currentTopMargin >= 0) {
                finalTopMargin = 0;
            } else {
                finalTopMargin = -mRefreshViewHeight + 1;
            }
        }
        int distance = currentTopMargin - finalTopMargin;
        if (distance < 0) {
            return;
        }
        // 回弹到指定位置
        ValueAnimator animator = ObjectAnimator.ofFloat(currentTopMargin, finalTopMargin).setDuration(distance);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentTopMargin = (float) animation.getAnimatedValue();
                setRefreshViewMarginTop((int) currentTopMargin);
            }
        });
        animator.start();
        mCurrentDrag = false;
    }

    /**
     * 重置当前加载更多状态
     */
    private void restoreLoadView() {
        if (!mIsCanLoad || mLoadView == null) {
            LogUtils.e(TAG, "没有开启上拉加载或者loadView为null");
            return;
        }
        int currentBottomMargin = ((ViewGroup.MarginLayoutParams) mLoadView.getLayoutParams()).bottomMargin;
        int finalBottomMargin = 0;
        if (mCurrentLoadStatus == LOAD_STATUS_LOOSEN_LOADING) {
            LogUtils.e(TAG, "正在加载");
            mCurrentLoadStatus = LOAD_STATUS_LOADING;
            if (mLoadCreator != null) {
                mLoadCreator.onLoading();
            }
            if (mListener != null) {
                mListener.onLoad();
            }
        } else if (mCurrentLoadStatus == LOAD_STATUS_PULL_DOWN_REFRESH) {
            if (mLoadCreator != null) {
                mLoadCreator.onCancelLoad();
            }
        } else if (mCurrentLoadStatus == LOAD_STATUS_LOADING) {
            if (mLoadCreator != null) {
                mLoadCreator.onLoading();
            }
        }
        int distance = currentBottomMargin - finalBottomMargin;
        if (distance < 0) {
            return;
        }
        // 回弹到指定位置
        ValueAnimator animator = ObjectAnimator.ofFloat(currentBottomMargin, finalBottomMargin).setDuration(distance);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentTopMargin = (float) animation.getAnimatedValue();
                setLoadViewMarginBottom((int) currentTopMargin);
            }
        });
        animator.start();
        mCurrentDrag = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return super.onTouchEvent(e);
        }
//        LogUtils.e(TAG, "====mLoadView====》" + mLoadView + " || =====mAutoLoad======>" + mAutoLoad);
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 手指滑动的距离
                int distance = (int) (e.getRawY() - mFingerDownY);
//                LogUtils.e(TAG, "distance：" + distance + "|| mLoadView：" + mLoadView + "|| mAutoLoad：" + mAutoLoad);
                // 下拉刷新
                if (distance > 0 && mRefreshView != null) {
                    // 如果是在最顶部，并且状态不是正在刷新的状态才处理，否则不需要处理
                    if (!canScrollUp()) {
                        if (mRefreshCreator != null && mIsRefresh) {
                            mRefreshViewHeight = mRefreshView.getMeasuredHeight();
                        }
                        // 解决下拉刷新自动滚动问题
                        // 用这个方法使得RecyclerView可以自动滚动，不然光margin变化，控件不自动滚动
                        if (mCurrentDrag) {
                            scrollToPosition(0);
                        }
                        // 获取手指触摸拖拽的距离
                        int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
                        // 如果是已经到达头部，并且不断的向下拉，那么不断的改变refreshView的marginTop的值
                        if (distanceY > 0 && mIsRefresh) {
                            int marginTop = distanceY - mRefreshViewHeight;
                            // 当处在正在刷新的状态的时候，需要根据手指按下的时候的marginTop做判断
                            if (mPressMarginTop > -(mRefreshViewHeight - 1)) {
                                marginTop = distanceY + mPressMarginTop + 1;
                            }
                            setRefreshViewMarginTop(marginTop);
                            if (mCurrentRefreshStatus != REFRESH_STATUS_REFRESHING) {
                                updateRefreshStatus(marginTop);
                            }
                            mCurrentDrag = true;
                            return false;
                        }
                    }
                } else if (distance < 0 && mLoadView != null && !mAutoLoad) { // 上拉加载
                    // 如果是在最底部并且状态不是正在加载的状态才处理，否则不需要处理
                    if (!canScrollDown() && mCurrentLoadStatus != LOAD_STATUS_LOADING) {
                        if (mLoadCreator != null && mIsCanLoad) {
                            mLoadViewHeight = mLoadView.getMeasuredHeight();
                        }
                        // 解决上拉加载自动滚动问题
                        // 用这个方法使得RecyclerView可以自动滚动，不然光margin变化，控件不自动滚动
                        if (mCurrentDrag) {
                            scrollToPosition(getAdapter().getItemCount() - 1);
                        }
                        // 获取手指触摸拖拽的距离
                        int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
                        // 如果是已经到达底部，并且不断的向上拉，那么不断的改变loadView的marginBottom的值
                        if (distanceY < 0 && mIsCanLoad) {
                            setLoadViewMarginBottom(-distanceY);
                            if (mCurrentLoadStatus != LOAD_STATUS_LOADING) {
                                updateLoadStatus(-distanceY);
                            }
                            mCurrentDrag = true;
                            return false;
                        }
                    }
                } else if (mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING || mCurrentLoadStatus == LOAD_STATUS_LOADING) {// 正在刷新的时候上拉或者下拉
                    // 正在加载或者正在刷新的时候，设置拖拽状态未true，便于在手指离开的时候在相应的restore方法中重新开启转圈的动画
                    if (mIsRefresh || mIsCanLoad) {
                        mCurrentDrag = true;
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }


    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断是不是滚动到了最顶部，这个是从SwipeRefreshLayout里面copy过来的源代码
     */
    public boolean canScrollUp() {
        boolean result;
        if (android.os.Build.VERSION.SDK_INT < 14) {
            result = ViewCompat.canScrollVertically(this, -1) || this.getScrollY() > 0;
        } else {
            result = ViewCompat.canScrollVertically(this, -1);
        }
        return result;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断是不是滚动到了最顶部，这个是从SwipeRefreshLayout里面copy过来的源代码
     */
    public boolean canScrollDown() {
        boolean result = ViewCompat.canScrollVertically(this, 1);
        return result;
    }


    /**
     * 更新刷新的状态
     */
    private void updateRefreshStatus(int marginTop) {
        if (mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING) {
            return;
        }
        if (marginTop <= -mRefreshViewHeight) {
            mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
        } else if (marginTop < 0) {
            mCurrentRefreshStatus = REFRESH_STATUS_PULL_DOWN_REFRESH;
        } else {
            mCurrentRefreshStatus = REFRESH_STATUS_LOOSEN_REFRESHING;
        }
        if (mRefreshCreator != null) {
            mRefreshCreator.onPull(marginTop + mRefreshViewHeight, mRefreshViewHeight, mCurrentRefreshStatus);
        }
    }

    /**
     * 更新加载的状态
     */
    private void updateLoadStatus(int distanceY) {
        if (mCurrentRefreshStatus == LOAD_STATUS_LOADING) {
            return;
        }
        if (distanceY <= 0) {
            mCurrentLoadStatus = LOAD_STATUS_NORMAL;
        } else if (distanceY < mLoadViewHeight) {
            mCurrentLoadStatus = LOAD_STATUS_PULL_DOWN_REFRESH;
        } else {
            mCurrentLoadStatus = LOAD_STATUS_LOOSEN_LOADING;
        }

        if (mLoadCreator != null) {
            mLoadCreator.onPull(distanceY, mLoadViewHeight, mCurrentLoadStatus);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (mRefreshView != null && mRefreshViewHeight <= 0) {
                // 获取头部刷新View的高度
                mRefreshViewHeight = mRefreshView.getMeasuredHeight();
                if (mRefreshViewHeight > 0) {
                    // 隐藏头部刷新的View  marginTop  多留出1px防止无法判断是不是滚动到头部问题
                    setRefreshViewMarginTop(-mRefreshViewHeight + 1);
                }
            }

            // LoadView不用设置，因为设置了也没用，因为是最后一条，初始化一般都显示不出来
//            if (mLoadView != null && mLoadViewHeight <= 0) {
//                // 获取底部加载的View的高度
//                mLoadViewHeight = mLoadView.getMeasuredHeight();
//                if (mLoadViewHeight > 0) {
//                    // 隐藏底部加载的View  marginTop  多留出1px防止无法判断是不是滚动到头部问题
//                    setLoadViewMarginBottom(-mRefreshViewHeight + 1);
//                }
//            }
        }
    }

    /**
     * 设置刷新View的marginTop
     */
    public void setRefreshViewMarginTop(int marginTop) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRefreshView.getLayoutParams();
        if (marginTop < -mRefreshViewHeight + 1) {
            marginTop = -mRefreshViewHeight + 1;
        }
        params.topMargin = marginTop;
        mRefreshView.setLayoutParams(params);
    }

    /**
     * 设置加载View的marginBottom
     */
    public void setLoadViewMarginBottom(int marginBottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLoadView.getLayoutParams();
        if (marginBottom < 0) {
            marginBottom = 0;
        }
        params.bottomMargin = marginBottom;
        mLoadView.setLayoutParams(params);
    }

    /**
     * 停止刷新
     */
    public void stopRefresh() {
        // 重置上拉状态
        mCurrentLoadStatus = LOAD_STATUS_NORMAL;
        if (mCurrentRefreshStatus == REFRESH_STATUS_NORMAL) {
            return;
        }
        mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
        // 重置LoadView的状态为NORMAL
        restoreLoadStatus();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                restoreRefreshView();
            }
        }, 300);
        if (mRefreshCreator != null) {
            mRefreshCreator.onStopRefresh();
        }
    }

    /**
     * 停止加载更多
     */
    public void stopLoad() {
        if (mCurrentLoadStatus == LOAD_STATUS_NORMAL) {
            return;
        }
        // 根据是否已经无数据了设置状态
        if (mIsLoadNoData) {
            mCurrentLoadStatus = LOAD_STATUS_LOAD_NO_DATA;
        } else {
            mCurrentLoadStatus = LOAD_STATUS_NORMAL;
        }
        restoreLoadView();
        if (mLoadCreator != null) {
            mLoadCreator.onStopLoad();
        }
    }

    /**
     * 设置是否没有更多数据了
     */
    public void setLoadNoData(boolean isNoData) {
        this.mIsLoadNoData = isNoData;
        setLoadEnable(false);
        if (mLoadCreator != null) {
            mLoadCreator.setLoadNoData(isNoData);
        }
    }

    /**
     * 获取到数据，并且可以上拉加载的时候
     * 打开上拉加载
     */
    public void onLoadViewEnable(LoadViewCreator footer) {
        // 充值请求无数据的状态
        mIsRequestNoData = false;
        // 打开上拉加载
        setLoadEnable(true);
        // 添加footer
        if (getLoadViewCreator() == null) {
            addLoadViewCreator(footer);
        }
        // 设置footer默认显示的文字
        setLoadNoData(false);
    }

    /**
     * 上拉加载获取不到数据
     */
    public void onLoadNoData() {
        setLoadNoData(true);
        setLoadEnable(false);
    }

    /**
     * 第一次请求没有获取到数据
     */
    public void onRequestNoData() {
        mIsRequestNoData = true;
        // 关闭上拉加载
        setLoadEnable(false);
        // 移除footer
        removeLoadView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRefreshCreator != null) {
            mRefreshCreator.onRelease();
        }
    }

    private boolean needMoveLast = false;
    private int mScrollIndex = 0;

    /**
     * 滚动到指定位置（就算该条目已经显示出来了，也会滚动到顶部）
     */
    public void moveToPosition(int position) {
        this.mScrollIndex = position;
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = layoutManager.findFirstVisibleItemPosition();
        int lastItem = layoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = getChildAt(position - firstItem).getTop();
            scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            needMoveLast = true;
        }
    }

    class RecyclerScrollViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (needMoveLast) {
                needMoveLast = false;
                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n = mScrollIndex - layoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = getChildAt(n).getTop();
                    //最后的移动
                    scrollBy(0, top);
                }
            }
            // 底部自动上拉加载
            if (mAutoLoad && !canScrollDown() && dy > 0 && !mIsLoadNoData && !mIsRequestNoData) {
                mCurrentLoadStatus = LOAD_STATUS_LOADING;
                if (mLoadCreator != null) {
                    mLoadCreator.onLoading();
                }
                if (mListener != null) {
                    mListener.onLoad();
                }
            }
            if (mScrollChangeListener != null) {
                mScrollChangeListener.onScrolled(recyclerView, dx, dy);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                if (mAutoLoad && !canScrollDown() && !mIsLoadNoData && !mIsRequestNoData) {
//                    mCurrentLoadStatus = LOAD_STATUS_LOADING;
//                    if (mLoadCreator != null) {
//                        mLoadCreator.onLoading();
//                    }
//                    if (mListener != null) {
//                        mListener.onLoad();
//                    }
//                }
//            }
        }
    }

    /**
     * 下拉刷新完成的时候，重置LoadView的状态为NORMAL
     */
    private void restoreLoadStatus() {
        mCurrentLoadStatus = LOAD_STATUS_NORMAL;
    }

    /**
     * 设置监听
     */
    public void setOnPullToRefreshListener(PullToRefreshListener listener) {
        this.mListener = listener;
    }

    /**
     * 上拉加载下拉刷新的回调
     */
    public interface PullToRefreshListener {
        void onRefresh();

        void onLoad();
    }

    private ScrollChangeListener mScrollChangeListener;

    public void addScrollChangeListener(ScrollChangeListener listener) {
        this.mScrollChangeListener = listener;
    }

    public interface ScrollChangeListener {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }
}
