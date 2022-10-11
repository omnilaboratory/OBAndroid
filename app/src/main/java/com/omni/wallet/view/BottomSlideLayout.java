package com.omni.wallet.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.omni.wallet.R;

public class BottomSlideLayout extends LinearLayout {
    public static final String TAG = "BottomSlideLayout";

    private float currY = 0;
    //总共的偏移量，当为0时，说明没有偏移
    private float totalOffsetY = 0;

    //滑动偏移量
    private float slipOffsetY = 0;

    //默认露出多少高度
    private float defaultHeight;

    //预计要露出多少高度
    private float realHeight;

    //拉伸距离顶部的间距
    private float realMarginTop;

    //收缩时候距离顶部的距离
    private float defaultMarginTop;

    //记录滑动的状态，上滑or下滑
    private float startY, moveState;

    //标识内部是否包含滚动条,默认没有
    private boolean noHaveScroll;

    private View touchView;

    private View headerView;

    private View[] titleViews;

    private OnScrollStateListener onScrollStateListener;

    public BottomSlideLayout(Context context) {
        this(context, null);
    }

    public BottomSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomSlideLayout);
        if (typedArray != null) {
            defaultHeight = typedArray.getDimension(R.styleable.BottomSlideLayout_defaultHeight, 0);
            realHeight = typedArray.getDimension(R.styleable.BottomSlideLayout_realHeight, 0);
            typedArray.recycle();
        }
        init();
    }

    private void init() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().height == realHeight) {
            return;
        }
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int windowsHeight = getWindowsHeight((Activity) getContext());
        //计算高度
        int height = measureHeight > windowsHeight ? measureHeight : windowsHeight;
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        Log.d(TAG, "真实高度：" + Utils.getRealDisplay(getContext()).y + "    高度：" + Utils.getDisplay(getContext()).heightPixels);
        totalOffsetY = height - defaultHeight;
        setY(totalOffsetY);
        //设置view收缩状态下距离顶部的间距
        defaultMarginTop = totalOffsetY;
        //设置view拉伸状态下距离顶部的间距
        realMarginTop = height - realHeight;
        getLayoutParams().height = (int) realHeight;
        setLayoutParams(getLayoutParams());
        //新增headerview
        if (headerView != null) {
            if (headerView.getParent() == null) {
                RelativeLayout parent = (RelativeLayout) getParent();
                parent.addView(headerView);
            }
            headerView.setY(totalOffsetY - headerView.getMeasuredHeight());
        }
        //新增titleView
        if (titleViews != null) {
            for (View titleView : titleViews) {
                //这里要判断有没有headerview，如果有，需要减去headerview的高度
                if (headerView != null) {
                    titleView.setY(totalOffsetY - titleView.getMeasuredHeight() - headerView.getMeasuredHeight());
                } else {
                    titleView.setY(totalOffsetY - titleView.getMeasuredHeight());
                }
            }
        }
//        if (onScrollStateListener != null) {
//            if (totalOffsetY == realMarginTop) {
//                onScrollStateListener.onState(1);
//            }
//            if (totalOffsetY == defaultMarginTop) {
//                onScrollStateListener.onState(-1);
//            }
//            Log.d("上滑还是下滑","测量");
//        }
        Log.d(TAG, "屏幕高：" + windowsHeight + "     测量高度：" + height + "   defaultHeight：" + defaultHeight + "   realHeight：" + realHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "高度：" + h);
    }

    //设置默认露出的高度，参数单位px
    public BottomSlideLayout setDefaultHeight(float defaultHeight) {
        this.defaultHeight = defaultHeight;
        return this;
    }

    //设置预计露出的高度，参数单位px
    public BottomSlideLayout setRealHeight(float realHeight) {
        this.realHeight = realHeight;
        return this;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent-按下:子view是否可以向上滚动" + canChildScrollUp());
                currY = ev.getRawY();
                startY = currY;
                Log.d(TAG, "onInterceptTouchEvent-按下:startY:" + startY + "     totalOffsetY:" + totalOffsetY + "    realMarginTop:" + realMarginTop);
                if (touchView != null && ev.getY() > touchView.getTop() && ev.getY() < touchView.getBottom()) {
                    return false;
                }
//                //如果偏移量已经到达拉伸目标值，则交给子view消费
                if (totalOffsetY <= realMarginTop) {
                    if (!noHaveScroll) {
                        return true;
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //如果偏移量已经到达拉伸目标值，则交给子view消费
                Log.d(TAG, "onInterceptTouchEvent-移动");
                float moveY = ev.getRawY();
                moveState = moveY - startY;
                if (moveState > slipOffsetY) {
                    //下滑
                    Log.d(TAG, "分发-下滑");
                    if (totalOffsetY <= realMarginTop && !canChildScrollUp()) {
                        //外层的view到达顶点&&子view不在顶点，此处不拦截，交给子view
                        Log.d(TAG, "分发-下滑-不拦截");
                        return false;
                    }
                    Log.d(TAG, "分发-下滑-拦截");
                    return true;
                } else if (moveState < -slipOffsetY) {
                    //上滑
                    Log.d(TAG, "分发-上滑");
                    if (totalOffsetY <= realMarginTop) {
                        //只要偏移量到达预计值，就不拦截事件，直接分发到下级
                        return false;
                    }
                } else if (moveState == 0) {
                    Log.d(TAG, "分发-点击====00000000");
                    return false;
                } else {
                    Log.d(TAG, "分发-else");
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent-弹起");
                //如果UP的Y跟currentY一样  则分发到下级，说明是点击
                if (ev.getRawY() == currY) {
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currY = event.getRawY();
                startY = currY;
                Log.d(TAG, "onTouchEvent-按下:" + currY + "     startY:" + startY);
                if (totalOffsetY <= realMarginTop) {
                    //内部没有滚动条，事件自己消费，不往下传递
                    if (!noHaveScroll) {
                        return true;
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getRawY();
                //计算偏移量，得到正负值，-上 +下
                moveState = moveY - startY;
                totalOffsetY = moveY - currY + totalOffsetY;
//                if (onScrollStateListener != null) {
//                    if (totalOffsetY == realMarginTop) {
//                        onScrollStateListener.onState(1);
//                    }
//                    if (totalOffsetY == defaultMarginTop) {
//                        onScrollStateListener.onState(-1);
//                    }
//                    Log.d("上滑还是下滑","移动");
//                }
                if (totalOffsetY <= realMarginTop && moveState < 0) {
                    //偏移量到达预计值&&是上滑状态，直接breank
                    totalOffsetY = realMarginTop;
                    break;
                }
                currY = moveY;
                setTranslationY(totalOffsetY);
                if (headerView != null) {
                    headerView.setTranslationY(totalOffsetY - headerView.getMeasuredHeight());
                }
                if (titleViews != null) {
                    for (View titleView : titleViews) {
                        if (headerView != null) {
                            titleView.setTranslationY(totalOffsetY - titleView.getMeasuredHeight() - headerView.getMeasuredHeight());
                        } else {
                            titleView.setTranslationY(totalOffsetY - titleView.getMeasuredHeight());
                        }
                    }
                }
                Log.d(TAG, "onTouchEvent-移动:" + moveY + "    currY:" + currY + "   偏移量：" + totalOffsetY + "  滑动状态：" + moveState);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent-弹起:getY:" + getY());
                currY = event.getRawY();
                Log.d(TAG, "currY:" + currY);
                if (moveState > slipOffsetY) {
                    //下滑
                    Log.d(TAG, "下滑");
                    startAnim(getY(), defaultMarginTop);
                } else if (moveState < -slipOffsetY) {
                    //上滑
                    Log.d(TAG, "上滑");
                    startAnim(getY(), realMarginTop);
                }
                moveState = 0;
                break;
        }
        return true;
    }


    //判断子view是否滑动到顶部
    public boolean canChildScrollUp() {
        noHaveScroll = false;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            Log.d(TAG, "子viewid：" + childView.getId() + "   是否可见：" + (childView.getVisibility() == View.VISIBLE));
            if (android.os.Build.VERSION.SDK_INT < 14) {
                if (childView instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) childView;
                    noHaveScroll = true;
                    return absListView.getChildCount() > 0
                            && (absListView.getFirstVisiblePosition() > 0 || childView.getTop() < absListView.getPaddingTop());
                } else {
                    noHaveScroll = true;
                    return ViewCompat.canScrollVertically(childView, -1) || childView.getScrollY() > 0;
                }
            } else {
                if ((childView instanceof ScrollView && childView.getVisibility() == VISIBLE)) {
                    noHaveScroll = true;
                    ScrollView svView = (ScrollView) childView;
                    if (svView.getScrollY() == 0) {
                        return true;
                    }
                    return false;
                }
                if ((childView instanceof NestedScrollView && childView.getVisibility() == VISIBLE)) {
                    noHaveScroll = true;
                    NestedScrollView svView = (NestedScrollView) childView;
                    if (svView.getScrollY() == 0) {
                        return true;
                    }
                    return false;
                }
                if ((childView instanceof RecyclerView && childView.getVisibility() == VISIBLE)) {
                    noHaveScroll = true;
                    RecyclerView recyclerView = (RecyclerView) childView;
                    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0) {
                            return true;
                        }
                    }
                    return false;
                }
                if ((childView instanceof ListView && childView.getVisibility() == VISIBLE)) {
                    noHaveScroll = true;
                    ListView listView = (ListView) childView;
                    View firstVisibleItemView = listView.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    //动画
    private void startAnim(float startHeight, final float endHeight) {
        //上滑
        ValueAnimator anim = ValueAnimator.ofFloat(startHeight, endHeight);
        anim.setDuration(200);
        // 设置动画运行的时长
        anim.setRepeatCount(0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                if (totalOffsetY == endHeight) {
                    return;
                }
                setTranslationY(currentValue);
                if (headerView != null) {
                    headerView.setTranslationY(currentValue - headerView.getMeasuredHeight());
                }
                if (titleViews != null) {
                    for (View titleView : titleViews) {
                        if (headerView != null) {
                            //这里是currentValue
                            titleView.setTranslationY(currentValue - titleView.getMeasuredHeight() - headerView.getMeasuredHeight());
                        } else {
                            titleView.setTranslationY(currentValue - titleView.getMeasuredHeight());
                        }
                    }
                }
                totalOffsetY = currentValue;
                Log.d(TAG, "totalOffsetY:" + totalOffsetY + "   realMarginTop:" + realMarginTop + "    defaultMarginTop: " + defaultMarginTop);
                if (onScrollStateListener != null) {
                    if (totalOffsetY == realMarginTop) {
                        onScrollStateListener.onState(1);
                    }
                    if (totalOffsetY == defaultMarginTop) {
                        onScrollStateListener.onState(-1);
                    }
                }
            }

        });
        anim.start();
    }

    //获取屏幕的高度
    public static int getWindowsHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    //弹起
    public void toTop() {
        startAnim(defaultMarginTop, realMarginTop);
    }

    //关闭
    public void toBottom() {
        startAnim(realMarginTop, defaultMarginTop);
    }

    //是否弹起
    public boolean isTop() {
        return totalOffsetY <= realMarginTop;
    }

    //headerview只能添加一个，headerview将自己消费touch事件
    public void addHeaderView(View view) {
        this.headerView = view;
    }

    //titleview可以添加多个，必须是在activity布局内声明的view，且不在hrlayout内部，titleview将自己消费touch事件
    public void addTitleView(View... views) {
        this.titleViews = views;
    }

    //touchView只能添加一个，必须是在activity布局内声明的view，且必须在hrlayout内部，touchview内部的子view将自己消费touch事件
    public BottomSlideLayout addTouchView(View touchView) {
        this.touchView = touchView;
        return this;
    }

    public BottomSlideLayout setOnScrollStateListener(OnScrollStateListener onScrollStateListener) {
        this.onScrollStateListener = onScrollStateListener;
        return this;
    }

    public interface OnScrollStateListener {

        /**
         * @param state>0 顶部  state<0底部
         */
        void onState(int state);
    }
}
