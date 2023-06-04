package com.omni.wallet_mainnet.baselibrary.view.refreshView;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;

/**
 * 下拉加载的控件
 */
public class RefreshLayout extends ViewGroup {

    private static final String TAG = RefreshLayout.class.getSimpleName();
    private Context mContext;
    private static final float DRAG_RATE = 0.5f;
    private static final int INVALID_POINTER = -1;
    // scroll从正在刷新状态滚动到消失不见所需要的时间
    private static final int SCROLL_TO_TOP_DURATION = 500;
    // 自动刷新的时候从看不见滚动到刷新高度需要的时间
    private static final int SCROLL_TO_REFRESH_DURATION = 250;
    // 完成状态展示的时间
    private static final long SHOW_COMPLETED_TIME = 300;

    private RefreshLayoutHeaderCreator mRefreshLayoutHeaderCreator;
    private View mRefreshHeader;
    private View mTarget;
    private int mCurrentMarginTop; // mTarget/header偏移距离
    private int mLastMarginTop;

    private boolean mHasMeasureHeader;   // 是否已经计算头部高度
    private int mTouchSlop;
    private int mHeaderHeight;       // header高度
    private int mShowRefreshDistance;  // 需要下拉这个距离才进入松手刷新状态，默认和header高度一致
    private int mMaxDragDistance;
    private int mActivePointerId;
    private boolean mIsTouch;
    private boolean mHasSendCancelEvent;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitDownY;
    private float mInitDownX;
    private static final int START_POSITION = 0;
    private MotionEvent mLastEvent;
    private boolean mIsBeginDragged;
    private AutoScroll mAutoScroll;
    //    private State mState = State.RESET;
    private OnRefreshListener mRefreshListener;
    private boolean mIsAutoRefresh;

    private boolean mCanPull = true;
    private int mState = RESET;
    private static final int RESET = 99;
    private static final int PULL = 88;
    private static final int LOADING = 77;
    private static final int COMPLETE = 66;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mAutoScroll = new AutoScroll();
    }

    // 刷新成功，显示500ms成功状态再滚动回顶部
    private Runnable mDelayToScrollTopRunnable = new Runnable() {
        @Override
        public void run() {
            mAutoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
        }
    };

    private Runnable mAutoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            // 标记当前是自动刷新状态，finishScroll调用时需要判断
            // 在actionDown事件中重新标记为false
            mIsAutoRefresh = true;
            changeState(PULL);
            mAutoScroll.scrollTo(mShowRefreshDistance, SCROLL_TO_REFRESH_DURATION);
        }
    };

    /**
     * 添加自定义header
     */
    public void addRefreshHeader(RefreshLayoutHeaderCreator creator) {
        this.mRefreshLayoutHeaderCreator = creator;
        View headerView = creator.getRefreshView(mContext, this);
        if (headerView != null && headerView != mRefreshHeader) {
            removeView(mRefreshHeader);
            // 为header添加默认的layoutParams
            LayoutParams layoutParams = headerView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                headerView.setLayoutParams(layoutParams);
            }
            mRefreshHeader = headerView;
            addView(mRefreshHeader);
        }
    }

    public void stopRefresh() {
        if (mRefreshLayoutHeaderCreator == null) {
            return;
        }
        changeState(COMPLETE);
        // if refresh completed and the mTarget at top, change mState to onReset.
        if (mCurrentMarginTop == START_POSITION) {
            changeState(RESET);
        } else {
            // waiting for a time to show refreshView completed mState.
            // at next touch event, remove this runnable
            if (!mIsTouch) {
                postDelayed(mDelayToScrollTopRunnable, SHOW_COMPLETED_TIME);
            }
        }
    }

    // 延时100ms显示刷新的状态
    public void autoRefresh() {
        autoRefresh(500);
    }

    /**
     * 在onCreate中调用autoRefresh，此时View可能还没有初始化好，需要延长一段时间执行。
     *
     * @param duration 延时执行的毫秒值
     */
    public void autoRefresh(long duration) {
        if (mState != RESET) {
            return;
        }
        postDelayed(mAutoRefreshRunnable, duration);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }

        if (mTarget == null) {
            return;
        }

        // ----- measure mTarget -----
        // target占满整屏
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        // ----- measure refreshView-----
        if (mRefreshHeader != null) {
            measureChild(mRefreshHeader, widthMeasureSpec, heightMeasureSpec);
            if (!mHasMeasureHeader) { // 防止header重复测量
                mHasMeasureHeader = true;
                mHeaderHeight = mRefreshHeader.getMeasuredHeight(); // header高度
                // 设置可以更新为刷新状态的高度为header的高度
                mShowRefreshDistance = mHeaderHeight;   // 需要pull这个距离才进入松手刷新状态
                if (mMaxDragDistance == 0) {  // 默认最大下拉距离为控件高度的五分之四
                    mMaxDragDistance = mShowRefreshDistance * 3;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }

        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }

        // target铺满屏幕
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + mCurrentMarginTop;
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        if (mRefreshHeader == null) {
            return;
        }
        // header放到target的上方，水平居中
        int refreshViewWidth = mRefreshHeader.getMeasuredWidth();
        mRefreshHeader.layout((width / 2 - refreshViewWidth / 2),
                -mHeaderHeight + mCurrentMarginTop,
                (width / 2 + refreshViewWidth / 2),
                mCurrentMarginTop);
    }

    /**
     * 将第一个Child作为target
     */
    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mRefreshHeader)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled() || mTarget == null || mRefreshHeader == null || !mCanPull) {
            return super.dispatchTouchEvent(ev);
        }
        final int actionMasked = ev.getActionMasked(); // support Multi-touch
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsAutoRefresh = false;
                mIsTouch = true;
                mHasSendCancelEvent = false;
                mIsBeginDragged = false;
                mLastMarginTop = mCurrentMarginTop;
                mCurrentMarginTop = mTarget.getTop();
                mInitDownX = mLastMotionX = ev.getX(0);
                mInitDownY = mLastMotionY = ev.getY(0);
                mAutoScroll.stop();
                removeCallbacks(mDelayToScrollTopRunnable);
                removeCallbacks(mAutoRefreshRunnable);
                super.dispatchTouchEvent(ev);
                return true;    // return true，否则可能接受不到move和up事件

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return super.dispatchTouchEvent(ev);
                }
                mLastEvent = ev;
                float x = ev.getX(MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                float y = ev.getY(MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                float yDiff = y - mLastMotionY;
                float offsetY = yDiff * DRAG_RATE;
                mLastMotionX = x;
                mLastMotionY = y;

                if (!mIsBeginDragged && Math.abs(y - mInitDownY) > mTouchSlop) {
                    mIsBeginDragged = true;
                }

                if (mIsBeginDragged) {
                    boolean moveDown = offsetY > 0; // ↓
                    boolean canMoveDown = canChildScrollUp();
                    boolean moveUp = !moveDown;     // ↑
                    boolean canMoveUp = mCurrentMarginTop > START_POSITION;
                    // 判断是否拦截事件
                    if ((moveDown && !canMoveDown) || (moveUp && canMoveUp)) {
                        moveSpinner(offsetY);
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsTouch = false;
                if (mCurrentMarginTop > START_POSITION) {
                    finishSpinner();
                }
                mActivePointerId = INVALID_POINTER;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    return super.dispatchTouchEvent(ev);
                }
                mLastMotionX = ev.getX(pointerIndex);
                mLastMotionY = ev.getY(pointerIndex);
                mLastEvent = ev;
                mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId));
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    private void moveSpinner(float diff) {
        int offset = Math.round(diff);
        if (offset == 0) {
            return;
        }

        // 发送cancel事件给child
        if (!mHasSendCancelEvent && mIsTouch && mCurrentMarginTop > START_POSITION) {
            sendCancelEvent();
            mHasSendCancelEvent = true;
        }

        int targetY = Math.max(0, mCurrentMarginTop + offset); // target不能移动到小于0的位置……
        // y = x - (x/2)^2
        float extraOS = targetY - mShowRefreshDistance;
        float slingshotDist = mShowRefreshDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) (tensionSlingshotPercent - Math.pow(tensionSlingshotPercent / 2, 2));

        if (offset > 0) { // 下拉的时候才添加阻力
            offset = (int) (offset * (1f - tensionPercent));
            targetY = Math.max(0, mCurrentMarginTop + offset);
        }

        // 1. 在RESET状态时，第一次下拉出现header的时候，设置状态变成PULL
        if (mState == RESET && mCurrentMarginTop == START_POSITION && targetY > 0) {
            changeState(PULL);
        }

        // 2. 在PULL或者COMPLETE状态时，header回到顶部的时候，状态变回RESET
        if (mCurrentMarginTop > START_POSITION && targetY <= START_POSITION) {
            if (mState == PULL || mState == COMPLETE) {
                changeState(RESET);
            }
        }

        // 3. 如果是从底部回到顶部的过程(往上滚动)，并且手指是松开状态, 并且当前是PULL状态，状态变成LOADING，这时候我们需要强制停止autoScroll
        if (mState == PULL && !mIsTouch && mCurrentMarginTop > mShowRefreshDistance && targetY <= mShowRefreshDistance) {
            mAutoScroll.stop();
            changeState(LOADING);
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
            // 因为判断条件targetY <= mShowRefreshDistance，会导致不能回到正确的刷新高度（有那么一丁点偏差），调整change
            int adjustOffset = mShowRefreshDistance - targetY;
            offset += adjustOffset;
        }
        setTargetOffsetTopAndBottom(offset);


        // loading状态的时候不做处理,
        if (mState == LOADING) {
            return;
        }
        // 别忘了回调header的位置改变方法。
        if (mRefreshLayoutHeaderCreator != null && mIsTouch) {
            mRefreshLayoutHeaderCreator.onPulling(mCurrentMarginTop, mLastMarginTop, mShowRefreshDistance, mIsTouch, mState);
        }
    }

    private void finishSpinner() {
        if (mState == LOADING) {
            if (mCurrentMarginTop > mShowRefreshDistance) {
                mAutoScroll.scrollTo(mShowRefreshDistance, SCROLL_TO_REFRESH_DURATION);
            }
        } else {
            mAutoScroll.scrollTo(START_POSITION, SCROLL_TO_TOP_DURATION);
        }
    }


    private void changeState(int state) {
        this.mState = state;
        if (mRefreshHeader == null) {
            return;
        }
        if (mRefreshLayoutHeaderCreator != null) {
            switch (state) {
                case RESET:
                    mRefreshLayoutHeaderCreator.onReset();
                    break;
                case PULL:
                    mRefreshLayoutHeaderCreator.onShowHeader();
                    break;
                case LOADING:
                    mRefreshLayoutHeaderCreator.onRefreshing();
                    break;
                case COMPLETE:
                    mRefreshLayoutHeaderCreator.onComplete();
                    break;
            }
        }
    }

    // 状态是否重置了
    public boolean isReset() {
        return mState == RESET;
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        if (offset == 0 || mRefreshHeader == null) {
            return;
        }

        mTarget.offsetTopAndBottom(offset);
        mRefreshHeader.offsetTopAndBottom(offset);
        mLastMarginTop = mCurrentMarginTop;
        mCurrentMarginTop = mTarget.getTop();
        invalidate();
    }

    private void sendCancelEvent() {
        if (mLastEvent == null) {
            return;
        }
        MotionEvent ev = MotionEvent.obtain(mLastEvent);
        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = ev.getY(newPointerIndex);
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }


    private class AutoScroll implements Runnable {
        private Scroller scroller;
        private int lastY;

        public AutoScroll() {
            scroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finished = !scroller.computeScrollOffset() || scroller.isFinished();
            if (!finished) {
                int currY = scroller.getCurrY();
                int offset = currY - lastY;
                lastY = currY;
                moveSpinner(offset);
                post(this);
                onScrollFinish(false);
            } else {
                stop();
                onScrollFinish(true);
            }
        }

        public void scrollTo(int to, int duration) {
            int from = mCurrentMarginTop;
            int distance = to - from;
            stop();
            if (distance == 0) {
                return;
            }
            scroller.startScroll(0, 0, 0, distance, duration);
            post(this);
        }

        private void stop() {
            removeCallbacks(this);
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            lastY = 0;
        }
    }

    /**
     * 在scroll结束的时候会回调这个方法
     *
     * @param isForceFinish 是否是强制结束的
     */
    private void onScrollFinish(boolean isForceFinish) {
        if (mIsAutoRefresh && !isForceFinish) {
            mIsAutoRefresh = false;
            changeState(LOADING);
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
            finishSpinner();
        }
    }

    public void setCanPull(boolean canPull) {
        this.mCanPull = canPull;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRefreshLayoutHeaderCreator != null) {
            mRefreshLayoutHeaderCreator.onRelease();
        }
    }

    public void setRefreshListener(OnRefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
