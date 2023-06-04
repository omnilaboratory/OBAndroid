package com.omni.wallet_mainnet.baselibrary.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 详情页的Banner
 */

public class BannerView extends RelativeLayout {
    private static final String TAG = BannerView.class.getSimpleName();
    private Context mContext;
    private BannerViewPager mBannerVp;
    private LinearLayout mIndicatorLayout;
    private BannerAdapter mAdapter;
    private int currentSelected;
    // 指示器选中的Drawable
    private Drawable mDotFocusDrawable;
    // 指示器默认的Drawable
    private Drawable mDotNormalDrawable;
    // 指示器的间距
    private int mDotDistance = 5;
    // 点指示器的位置
    private int mDotGravity = -1;
    private boolean mShowIndicator = false;// 是否显示指示器


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        // 把布局加载进来
        LayoutInflater.from(mContext).inflate(R.layout.view_banner, this);
        // 初始化属性
        initAttribute(attrs);
        // 初始化View
        initView();
    }

    // 初始化属性
    private void initAttribute(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.BannerView);
        // 获取指示器的选中Drawable
        mDotFocusDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorFocus);
        if (mDotFocusDrawable == null) {
            mDotFocusDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_indicator_normal);
        }
        // 获取指示器的默认Drawable
        mDotNormalDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorNormal);
        if (mDotNormalDrawable == null) {
            mDotNormalDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_indicator_default);
        }
        // 获取指示器的间距
        mDotDistance = (int) array.getDimension(R.styleable.BannerView_dotDistance, dip2px(mDotDistance));
        // 获取指示器的位置
        mDotGravity = array.getInt(R.styleable.BannerView_dotGravity, mDotGravity);
        // 使用完注意回收
        array.recycle();
    }

    /**
     * 初始化View
     */
    private void initView() {
        // 初始化布局中的控件
        mBannerVp = findViewById(R.id.banner_vp);
        mIndicatorLayout = findViewById(R.id.layout_banner_indicator);
        // 初始化指示器的点
        if (mShowIndicator) {
            if (mDotFocusDrawable == null) {
                mDotFocusDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_indicator_normal);
            }
            if (mDotNormalDrawable == null) {
                mDotNormalDrawable = ContextCompat.getDrawable(mContext, R.drawable.shape_indicator_default);
            }
        }
    }

    /**
     * 是否显示默认指示器
     */
    public void setShowIndicator(boolean show) {
        this.mShowIndicator = show;
    }

    /**
     * 数据刷新
     */
    public void notifyDataSetChanged() {
        mBannerVp.notifyDataSetChanged();
        // 重新初始化指示器
        initDotIndicator();
    }

    // 为BannerViewPager设置Adapter
    public void setAdapter(final BannerAdapter adapter) {
        this.mAdapter = adapter;
        mBannerVp.setAdapter(adapter);
        // 初始化的时候默认第一个被选中
        currentSelected = 0;
        // 初始化点的指示器
        initDotIndicator();
    }


    // 初始化点的指示器
    private void initDotIndicator() {
        if (!mShowIndicator) {
            return;
        }
        final int count = mAdapter.getPageCount();
        if (count <= 1) {
            mIndicatorLayout.setVisibility(GONE);
        } else {
            mIndicatorLayout.setVisibility(VISIBLE);
        }
        if (count == 0) {
            return;
        }
        mIndicatorLayout.removeAllViews();
        // 获取小点指示器的位置
        int dotGravity = getDotGravity();
        // 设置小点指示器的位置
        mIndicatorLayout.setGravity(dotGravity);
        // 根据数量遍历添加
        for (int i = 0; i < count; i++) {
            DotIndicatorView point = new DotIndicatorView(mContext);
            Drawable drawable;
            // 初始化的时候设置颜色
            if (i == 0) {
                drawable = mDotFocusDrawable;
            } else {
                drawable = mDotNormalDrawable;
            }
            // 初始化的时候设置颜色
            point.setDrawable(drawable);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // 给指示器点设置宽高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            // 设置点的间距距
            params.leftMargin = mDotDistance;
            point.setLayoutParams(params);
            // 添加到LinearLayout中
            mIndicatorLayout.addView(point);
        }
        // 设置滚动监听
        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 切换点的状态
                changeDotSelected(position, count);
            }
        });
    }

    // 切换点的状态
    private void changeDotSelected(int position, int count) {
        // 获取上一次选中的点
        DotIndicatorView currentSelectedView = (DotIndicatorView) mIndicatorLayout.getChildAt(currentSelected);
        if (currentSelectedView != null) {
            // 重新设置宽高
            int width = mDotNormalDrawable.getIntrinsicWidth();
            int height = mDotNormalDrawable.getIntrinsicHeight();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) currentSelectedView.getLayoutParams();
            params.width = width;
            params.height = height;
            // 设置图片
            currentSelectedView.setDrawable(mDotNormalDrawable);
        }
        // 将当前选中的点赋值到currentSelected
        currentSelected = position % count;
        // 获取当前选中的点
        DotIndicatorView changeView = (DotIndicatorView) mIndicatorLayout.getChildAt(currentSelected);
        if (changeView != null) {
            // 重新设置宽高
            int width = mDotFocusDrawable.getIntrinsicWidth();
            int height = mDotFocusDrawable.getIntrinsicHeight();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) changeView.getLayoutParams();
            params.width = width;
            params.height = height;
            // 设置图片
            changeView.setDrawable(mDotFocusDrawable);
        }
    }


    // 获取小点指示器的位置
    private int getDotGravity() {
        switch (mDotGravity) {
            case -1:
                return Gravity.LEFT;
            case 0:
                return Gravity.CENTER;
            case 1:
                return Gravity.RIGHT;
        }
        return Gravity.RIGHT;
    }

    // 把dip转换成px
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip, getResources().getDisplayMetrics());
    }

    /**
     * 添加滚动监听
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mBannerVp.addOnPageChangeListener(listener);
    }

    public void setOnItemClickListener(final BannerItemClickListener listener) {
        mBannerVp.setBannerItemClickListener(new BannerViewPager.BannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    public interface BannerItemClickListener {
        void onItemClick(int position);
    }
}
