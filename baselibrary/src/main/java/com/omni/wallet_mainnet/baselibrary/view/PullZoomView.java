package com.omni.wallet_mainnet.baselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 拖动头部缩放的ScrollView
 */
public class PullZoomView extends ScrollView {
    private static final String TAG = PullZoomView.class.getSimpleName();
    private static final String TAG_HEADER = "header";        //头布局Tag
    private static final String TAG_ZOOM = "zoom";            //缩放布局Tag
    private static final String TAG_CONTENT = "content";      //内容布局Tag

    private float sensitive = 1.5f;         //放大的敏感系数
    private int zoomTime = 500;             //头部缩放时间，单位 毫秒
    private boolean isParallax = true;      //是否让头部具有视差动画
    private boolean isZoomEnable = true;    //是否允许头部放大

    private Scroller scroller;              //辅助缩放的对象
    private boolean isActionDown = false;   //第一次接收的事件是否是Down事件
    private boolean isZooming = false;      //是否正在被缩放
    private MarginLayoutParams headerParams;//头部的参数
    private int headerHeight;               //头部的原始高度
    private View headerView;                //头布局
    private View zoomView;                  //用于缩放的View
    private View contentView;               //主体内容View
    private float lastEventX;               //Move事件最后一次发生时的X坐标
    private float lastEventY;               //Move事件最后一次发生时的Y坐标
    private float downX;                    //Down事件的X坐标
    private float downY;                    //Down事件的Y坐标
    private int maxY;                       //允许的最大滑出距离
    private int touchSlop;

    private OnScrollListener scrollListener;  //滚动的监听

    public void setOnScrollListener(OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    /**
     * 滚动的监听，范围从 0 ~ maxY
     */
    public static abstract class OnScrollListener {
        public void onScroll(int l, int t, int oldl, int oldt) {
        }

        public void onHeaderScroll(int currentY, int maxY) {
        }

        public void onContentScroll(int l, int t, int oldl, int oldt) {
        }
    }

    private OnPullZoomListener pullZoomListener; //下拉放大的监听

    public void setOnPullZoomListener(OnPullZoomListener pullZoomListener) {
        this.pullZoomListener = pullZoomListener;
    }

    public static abstract class OnPullZoomListener {
        public void onPullZoom(int originHeight, int currentHeight) {
        }

        public void onZoomFinish() {
        }
    }

    public PullZoomView(Context context) {
        this(context, null);
    }

    public PullZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public PullZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PullZoomView);
        sensitive = a.getFloat(R.styleable.PullZoomView_pzv_sensitive, sensitive);
        isParallax = a.getBoolean(R.styleable.PullZoomView_pzv_isParallax, isParallax);
        isZoomEnable = a.getBoolean(R.styleable.PullZoomView_pzv_isZoomEnable, isZoomEnable);
        zoomTime = a.getInt(R.styleable.PullZoomView_pzv_zoomTime, zoomTime);
        a.recycle();

        scroller = new Scroller(getContext());
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                maxY = contentView.getTop();//只有布局完成后才能获取到正确的值
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        findTagViews(this);
        if (headerView == null || zoomView == null || contentView == null) {
            throw new IllegalStateException("content, header, zoom 都不允许为空,请在Xml布局中设置Tag，或者使用属性设置");
        }
        headerParams = (MarginLayoutParams) headerView.getLayoutParams();
        headerHeight = headerParams.height;
        smoothScrollTo(0, 0);//如果是滚动到最顶部，默认最顶部是ListView的顶部
    }

    /**
     * 递归遍历所有的View，查询Tag
     */
    private void findTagViews(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View childView = vg.getChildAt(i);
                Object tag = childView.getTag();
                if (tag != null) {
                    if (TAG_CONTENT.equals(String.valueOf(tag)) && contentView == null)
                        contentView = childView;
                    if (TAG_HEADER.equals(String.valueOf(tag)) && headerView == null)
                        headerView = childView;
                    if (TAG_ZOOM.equals(String.valueOf(tag)) && zoomView == null)
                        zoomView = childView;
                }
                if (childView instanceof ViewGroup) {
                    findTagViews(childView);
                }
            }
        } else {
            String tag = (String) v.getTag();
            if (tag != null) {
                if (TAG_CONTENT.equals(tag) && contentView == null) contentView = v;
                if (TAG_HEADER.equals(tag) && headerView == null) headerView = v;
                if (TAG_ZOOM.equals(tag) && zoomView == null) zoomView = v;
            }
        }
    }

    private boolean scrollFlag = false;  //该标记主要是为了防止快速滑动时，onScroll回调中可能拿不到最大和最小值

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollListener != null) scrollListener.onScroll(l, t, oldl, oldt);
        if (t >= 0 && t <= maxY) {
            scrollFlag = true;
            if (scrollListener != null) scrollListener.onHeaderScroll(t, maxY);
        } else if (scrollFlag) {
            scrollFlag = false;
            if (t < 0) t = 0;
            if (t > maxY) t = maxY;
            if (scrollListener != null) scrollListener.onHeaderScroll(t, maxY);
        }
        if (t >= maxY) {
            if (scrollListener != null)
                scrollListener.onContentScroll(l, t - maxY, oldl, oldt - maxY);
        }
        if (isParallax) {
            if (t >= 0 && t <= headerHeight) {
                headerView.scrollTo(0, -(int) (0.65 * t));
            } else {
                headerView.scrollTo(0, 0);
            }
        }
    }

    /**
     * 主要用于解决 RecyclerView嵌套，直接拦截事件，可能会出现其他问题
     * 如果不需要使用  RecyclerView，可以将这里代码注释掉
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = e.getY();
                if (Math.abs(moveY - downY) > touchSlop) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isZoomEnable) return super.onTouchEvent(ev);

        float currentX = ev.getX();
        float currentY = ev.getY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastEventX = currentX;
                downY = lastEventY = currentY;
                scroller.abortAnimation();
                isActionDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isActionDown) {
                    downX = lastEventX = currentX;
                    downY = lastEventY = currentY;
                    scroller.abortAnimation();
                    isActionDown = true;
                }
                float shiftX = Math.abs(currentX - downX);
                float shiftY = Math.abs(currentY - downY);
                float dx = currentX - lastEventX;
                float dy = currentY - lastEventY;
                lastEventY = currentY;
                if (isTop()) {
                    if (shiftY > shiftX && shiftY > touchSlop) {
                        int height = (int) (headerParams.height + dy / sensitive + 0.5);
                        if (height <= headerHeight) {
                            height = headerHeight;
                            isZooming = false;
                        } else {
                            isZooming = true;
                        }
                        headerParams.height = height;
                        headerView.setLayoutParams(headerParams);
                        if (pullZoomListener != null)
                            pullZoomListener.onPullZoom(headerHeight, headerParams.height);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionDown = false;
                if (isZooming) {
                    scroller.startScroll(0, headerParams.height, 0, -(headerParams.height - headerHeight), zoomTime);
                    isZooming = false;
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                break;
        }
        return isZooming || super.onTouchEvent(ev);
    }

    private boolean isStartScroll = false;          //当前是否下拉过

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            isStartScroll = true;
            headerParams.height = scroller.getCurrY();
            headerView.setLayoutParams(headerParams);
            if (pullZoomListener != null)
                pullZoomListener.onPullZoom(headerHeight, headerParams.height);
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (pullZoomListener != null && isStartScroll) {
                isStartScroll = false;
                pullZoomListener.onZoomFinish();
            }
        }
    }

    private boolean isTop() {
        return getScrollY() <= 0;
    }

    public void setSensitive(float sensitive) {
        this.sensitive = sensitive;
    }

    public void setIsParallax(boolean isParallax) {
        this.isParallax = isParallax;
    }

    public void setIsZoomEnable(boolean isZoomEnable) {
        this.isZoomEnable = isZoomEnable;
    }

    public void setZoomTime(int zoomTime) {
        this.zoomTime = zoomTime;
    }
}