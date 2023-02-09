package com.omni.testnet.framelibrary.view.navigationBar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.testnet.baselibrary.utils.DisplayUtil;
import com.omni.testnet.baselibrary.utils.StatusBarUtil;
import com.omni.testnet.baselibrary.view.navigationbar.AbsNavigationBar;
import com.omni.testnet.framelibrary.R;


/**
 * 具体的自定义TitleBar
 */

public class DefaultNavigationBar<D extends DefaultNavigationBar.Builder.DefaultNavigationParams>
        extends AbsNavigationBar {
    private static final String TAG = DefaultNavigationBar.class.getSimpleName();

    /**
     * 构造方法
     */
    public DefaultNavigationBar(D params) {
        super(params);
    }

    @Override
    public int bindLayoutResId() {
        return R.layout.layout_title;
    }

    @Override
    public void applyView() {
        // 在这里去绑定具体的参数和点击效果什么的
        D params = (D) getParams();
        // 设置背景颜色
        setBackGroundResource(R.id.layout_title_parent, params.mTitleBgColorRes);
        // 设置状态栏占位条的背景颜色
        setBackgroundColor(R.id.view_top_bar, params.mTopViewBgColorRes);
        // 设置背景透明度
        setTitleBgAlpha(R.id.layout_title_parent, params.mTitleBgAlpha);// 最外层控件设置透明度
        // 设置左边返回按钮的图标
        setBackGroundResource(R.id.iv_back, params.mLeftIcon);
        // 设置标题文字
        setText(R.id.tv_title, params.mTitleText);
        // 设置标题文字颜色
        if (params.mTitleTextColor != 0) {
            setTextColor(R.id.tv_title, params.mTitleTextColor);
        }
        // 设置左边标题的文字
        setText(R.id.tv_left_title, params.mLeftTitleText);
        // 设置内容区域Layout的顶部也就是标题栏底部的阴影
        if (params.mShowContentTopShadow) {
            setContentTopShadow(params);
        }
        // 标题栏右边文字
        if (params.mAddRightText) {
            // 添加TextView控件到布局中
            addRightTextView(params);
            // 设置右边文字
            setText(R.id.tv_right, params.mRightText);
            // 设置文字颜色
            if (params.mRightTextColor != 0) {
                setTextColor(R.id.tv_right, params.mRightTextColor);
            }
        }
        // 标题栏右边其他控件
        if (params.mAddRightView) {
            // 添加View到控件右边的布局中
            addRightView(params.mRightView);
        }
        // 设置右边控件是否可见
        setVisibility(R.id.rl_right, params.mRightVisibility);

        // 设置右边的点击事件
        setClickListener(R.id.rl_right, params.mRightClickListener);

        // 设置左边返回键的点击事件
        setClickListener(R.id.iv_back, params.mLeftClickListener);

        // 设置左边返回图标是否可见
        setVisibility(R.id.iv_back, params.mLeftIconVisible);

        // 设置中间Tab是否可见
        setVisibility(R.id.rg_middle_tab, params.mMiddleTabVisible);

        // 设置底部的横线是否可见
        setVisibility(R.id.bottom_line, params.mBottomLineVisibility);

        // 设置标题位置的图片
        setImage(0, params.mTitleImage);
        // 中间Tab相关
        if (params.mMiddleTabVisible == View.VISIBLE) {// 如果中间Tab可见，根据数量填充相应的RadioButton
            initMiddleView(params);
        }
        // 沉浸式状态栏相关
        if (params.mIsImmersive) {
            setImmersive(params);
        }
    }

    /**
     * 设置内容布局区域顶部添加阴影
     */
    private void setContentTopShadow(D params) {
        ViewGroup parent = params.mParent;
        ViewGroup contentParent = params.mContentParent;
        Context context = params.mContext;
        if (contentParent == null) {
            // 这里直接获取最后一个Child。因为中间可能有其他Child(例如当前Style下中间有一个ViewStub)
            int contentLayoutIndex = parent.getChildCount() - 1;
            if (contentLayoutIndex >= 0) {
                contentParent = (ViewGroup) parent.getChildAt(contentLayoutIndex);
            }
        }
        if (contentParent == null) {
            return;
        }
        // 设置顶部阴影
        // 参数2：inflate的View的父布局
        // 参数3：inflate之后是否需要添加进父布局（如果未true，注意LayoutParams）
        // 这个参数为true，就不需要后面的AddView了  直接就添加进父布局里边了
        LayoutInflater.from(context).inflate(R.layout.layout_page_top_shadow_line, contentParent, true);
    }

    /**
     * 设置标题栏的背景色透明度
     */
    public void setTitleBgAlpha(@IntRange(from = 0, to = 255) int alpha) {
        setTitleBgAlpha(R.id.layout_title_parent, alpha);
        setTitleBgAlpha(R.id.view_top_bar, alpha);
    }


    /**
     * 设置左边返回控件的点击事件
     */
    public void setLeftClickListener(View.OnClickListener listener) {
        setClickListener(R.id.iv_back, listener);
    }

    /**
     * 设置左边返回图标
     */
    public void setLeftIcon(int iconId) {
        ((ImageView) getView(R.id.iv_back)).setImageResource(iconId);
    }

    /**
     * 获取标题栏控件的View
     */
    public View getTitleView() {
        return getView(R.id.layout_title_parent);
    }

    /**
     * 标题栏显示
     */
    public void setTitleViewShow() {
        getTitleView().setVisibility(View.VISIBLE);
    }

    /**
     * 标题栏隐藏
     */
    public void setTitleViewHidden() {
        getTitleView().setVisibility(View.GONE);
    }

    /**
     * 获取标题栏中间文字的TextView
     */
    public TextView getTitleTextView() {
        return (TextView) getView(R.id.tv_title);
    }

    /**
     * 添加View到标题栏右边
     */
    private void addRightView(View rightView) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rightView.setLayoutParams(layoutParams);
        RelativeLayout parentLayout = (RelativeLayout) getView(R.id.rl_right);
        parentLayout.removeAllViews();
        parentLayout.addView(rightView);
    }

    /**
     * 添加TextView控件到右边的布局中
     */
    private void addRightTextView(D params) {
        View rightTextView = LayoutInflater.from(params.mContext).inflate(R.layout.layout_title_right_text, null, false);
        addRightView(rightTextView);
        params.mAddRightView = true;
        params.mRightView = rightTextView;
    }

    /**
     * 初始化中间Tab的View
     */
    private void initMiddleView(D params) {
        // 获取父布局
        RadioGroup tabParent = (RadioGroup) getView(R.id.rg_middle_tab);
        // 填充子布局
        for (int i = 0; i < params.mMiddleTabSize; i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(params.mContext)
                    .inflate(R.layout.view_title_middle_child, null, false);
            // 这步必须设置，否则不能自动切换选中状态
            radioButton.setId(i);
            if (params.mTabClickListenerArray.size() > i) {
                // 获取点击事件
                View.OnClickListener clickListener = params.mTabClickListenerArray.get(i);
                // 设置点击事件
                radioButton.setOnClickListener(clickListener);
            }
            if (params.mTabTextArray.size() > i) {
                // 获取文字
                CharSequence text = params.mTabTextArray.get(i);
                // 设置文字
                radioButton.setText(text);
            }
            // 设置不同的背景，区分圆角或者中间非圆角
            if (i == 0) {// 头，左圆角
                radioButton.setBackgroundResource(R.drawable.selector_title_tab_left);
                // 设置一点左边距
                radioButton.setPadding(DisplayUtil.dp2px(params.mContext, 5), 0, 0, 0);
                // 默认第一个被选中
                radioButton.setChecked(true);
            } else if (i == params.mMiddleTabSize - 1) {// 尾，右圆角
                radioButton.setBackgroundResource(R.drawable.selector_title_tab_right);
                // 设置一点右边距
                radioButton.setPadding(0, 0, DisplayUtil.dp2px(params.mContext, 5), 0);
            } else {// 中间，非圆角
                // 单数的没有左右的竖线，双数的有
                if (i % 2 == 1) {
                    radioButton.setBackgroundResource(R.drawable.selector_title_tab_middle_1);
                } else {
                    radioButton.setBackgroundResource(R.drawable.selector_title_tab_middle);
                }
            }
            // 设置布局的宽高以及权重（直接等比例排版）
            RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            radioButton.setLayoutParams(p);
            // 添加到父控件
            tabParent.addView(radioButton);
        }
    }

    /**
     * 设置沉浸式相关
     * TODO 设置颜色和Activity透明放到Activity里边取统一处理，在这里写的话，如果创建title的是Fragment，
     * TODO 那么设置颜色的方法会重复调用，导致状态栏重复着色
     */
    private void setImmersive(D params) {
        // 沉浸式版本判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        // 设置隐藏条的高度（这里不能删除，添加title之后在这里去控制占位View的高度，在Activity统一控制的
        // 话，需要每个Activity都设置占位View，所以算了）
        StatusBarUtil.setStatusBarTopViewHeight(params.mContext, getView(R.id.view_top_bar));
    }

    /**
     * 设置顶部占位条的背景颜色
     */
    public void changeStatusBarViewBg(int bgRes) {
        D params = (D) getParams();
        setBackgroundColor(R.id.view_top_bar, ContextCompat.getColor(params.mContext, bgRes));
    }

    /**
     * 设置标题文字
     */
    public void setTitleText(CharSequence titleText) {
        setText(R.id.tv_title, String.valueOf(titleText));
    }

    /**
     * 设置标题文字颜色
     */
    public void setTitleTextColor(int colorRes) {
        D params = (D) getParams();
        setTextColor(R.id.tv_title, ContextCompat.getColor(params.mContext, colorRes));
    }

    /**
     * 设置左边标题文字
     */
    public void setLeftTitleText(CharSequence titleText) {
        setText(R.id.tv_left_title, String.valueOf(titleText));
    }

    /**
     * 设置左边返回图标隐藏
     */
    public void setLeftIconVisible(int visible) {
        setVisibility(R.id.iv_back, visible);
    }

    /**
     * 设置右边的文字
     */
    public void setRightText(CharSequence rightText) {
        D params = (D) getParams();
        addRightTextView(params);
        setVisibility(R.id.rl_right, View.VISIBLE);
        setText(R.id.tv_right, String.valueOf(rightText));
    }

    /**
     * 设置右边的文字
     */
    public void setRightTextColor(int colorRes) {
        D params = (D) getParams();
        setTextColor(R.id.tv_right, ContextCompat.getColor(params.mContext, colorRes));
    }

    /**
     * 设置右边的文字大小
     */
    public void setRightTextSize(int textSize) {
        TextView textView = (TextView) getView(R.id.tv_right);
        DisplayUtil.setTextSizeDP(textView, textSize);
    }

    /**
     * 设置右边的图标
     */
    public void setRightIcon(int rightIconRes) {
        D params = (D) getParams();
        addRightTextView(params);
        setVisibility(R.id.rl_right, View.VISIBLE);
        setBackGroundResource(R.id.tv_right, rightIconRes);
    }

    /**
     * 设置右边的View
     */
    public void setRightView(int layoutId) {
        D params = (D) getParams();
        View rightView = LayoutInflater.from(params.mContext).inflate(layoutId, null, false);
        setRightView(rightView);
    }

    /**
     * 设置右边的View
     */
    public void setRightView(View rightView) {
        D params = (D) getParams();
        params.mAddRightView = true;
        addRightView(rightView);
    }

    /**
     * 设置右边控件的可见状态
     */
    public void setRightVisibility(int visibility) {
        setVisibility(R.id.rl_right, visibility);
    }

    /**
     * 设置右边的点击事件
     */
    public void setRightClickListener(View.OnClickListener listener) {
        setClickListener(R.id.rl_right, listener);
    }

    /**
     * 获取右边的TextView控件
     */
    public TextView getRightTextView() {
        D params = (D) getParams();
        if (params.mAddRightText) {
            return (TextView) getView(R.id.tv_right);
        }
        return null;
    }

    /**
     * 获取添加的右边的控件
     */
    public View getRightView() {
        D params = (D) getParams();
        if (params.mAddRightView) {
            return params.mRightView;
        }
        return null;
    }

    /**
     * Builder建造类
     */
    public static class Builder extends AbsNavigationBar.Builder {
        // 参数集的对象
        private DefaultNavigationParams P;
        private Context mContext;

        /**
         * 构造方法，传递一个上下文，父布局在createAndBindView方法中再去获取
         * 这个方法目前只适用于继承自AppcompatActivity的Activity
         */
        public Builder(Context context) {
            super(context, null);
            this.mContext = context;
            P = new DefaultNavigationParams(context, null, null);
        }

        /**
         * 构造方法，需要传递一个上下文，一个承载自定义Title的父容器
         */
        public Builder(Context context, ViewGroup parent) {
            super(context, parent);
            this.mContext = context;
            P = new DefaultNavigationParams(context, parent, null);
        }

        /**
         * 构造方法，需要传递一个上下文，一个承载自定义Title的父容器，一个承载页面的父布局容器（目前必须是FrameLayout）
         */
        public Builder(Context context, ViewGroup parent, ViewGroup contentParent) {
            super(context, parent);
            this.mContext = context;
            P = new DefaultNavigationParams(context, parent, contentParent);
        }

        @Override
        public DefaultNavigationBar build() {
            return new DefaultNavigationBar(P);
        }

        /* 具体的所有自定义的Title的效果设置全部在这里*/

        /**
         * 设置底部的横线是否可见
         */
        public Builder setBottomLineVisibility(int visibility) {
            P.mBottomLineVisibility = visibility;
            return this;
        }

        /**
         * 设置标题栏底部也就是内容区域顶部的阴影是否可见
         */
        public Builder showContentTopShadow(boolean state) {
            P.mShowContentTopShadow = state;
            return this;
        }

        /**
         * 设置顶部占位条的背景颜色
         */
        public Builder setTopViewBg(int bgColorRes) {
            P.mTopViewBgColorRes = ContextCompat.getColor(P.mContext, bgColorRes);
            return this;
        }

        /**
         * 设置标题
         */
        public Builder setTitle(String title) {
            P.mTitleText = title;
            return this;
        }

        /**
         * 设置标题
         */
        public Builder setTitle(int id) {
            P.mTitleText = mContext.getResources().getString(id);
            return this;
        }

        /**
         * 设置标题文字颜色
         */
        public Builder setTitleTextColor(int res) {
            P.mTitleTextColor = ContextCompat.getColor(P.mContext, res);
            return this;
        }

        /**
         * 设置左边标题的文字
         */
        public Builder setLeftTitle(String leftTitle) {
            P.mLeftTitleText = leftTitle;
            return this;
        }

        /**
         * 设置左边标题的文字
         */
        public Builder setLeftTitle(int leftTitleId) {
            P.mLeftTitleText = mContext.getResources().getString(leftTitleId);
            return this;
        }

        /**
         * 设置Title图片
         */
        public Builder setTitleImage(int imageRes) {
            P.mTitleImage = imageRes;
            return this;
        }

        /**
         * 设置中间Tab是否显示
         */
        public Builder showMiddleTabView(int visible) {
            P.mMiddleTabVisible = visible;
            return this;
        }

        /**
         * 设置中间Tab的数量（最少是2，默认是2）
         */
        public Builder setMiddleTabSize(int TabSize) {
            P.mMiddleTabSize = TabSize;
            return this;
        }

        /**
         * 设置中间Tab的点击事件
         */
        public Builder setMiddleTabClickListener(int index, View.OnClickListener clickListener) {
            P.mTabClickListenerArray.put(index, clickListener);
            return this;
        }

        /**
         * 设置中间Tab的文字
         */
        public Builder setMiddleTabText(int index, CharSequence text) {
            P.mTabTextArray.put(index, text);
            return this;
        }

        /**
         * 设置中间Tab的文字
         */
        public Builder setMiddleTabText(int index, int textId) {
            CharSequence text = P.mContext.getResources().getString(textId);
            P.mTabTextArray.put(index, text);
            return this;
        }

        /**
         * 设置右边的文字
         */
        public Builder setRightText(String rightText) {
            P.mAddRightText = true;
            P.mRightText = rightText;
            return this;
        }

        /**
         * 设置右边的文字
         */
        public Builder setRightText(int rightTextId) {
            P.mAddRightText = true;
            P.mRightText = P.mContext.getString(rightTextId);
            return this;
        }

        /**
         * 设置标题右边文字颜色
         */
        public Builder setRightTextColor(int res) {
            P.mRightTextColor = ContextCompat.getColor(P.mContext, res);
            return this;
        }

        /**
         * 设置右边的View
         */
        public Builder setRightView(View view) {
            P.mAddRightView = true;
            P.mRightView = view;
            return this;
        }

        /**
         * 设置右边的View
         */
        public Builder setRightView(int layoutId) {
            P.mAddRightView = true;
            P.mRightView = LayoutInflater.from(P.mContext).inflate(layoutId, null, false);
            return this;
        }

        /**
         * 设置右边控件是否可见
         */
        public Builder showRightView(int visibility) {
            P.mRightVisibility = visibility;
            return this;
        }

        /**
         * 设置右边控件的点击事件
         */
        public Builder setRightClickListener(View.OnClickListener listener) {
            P.mRightClickListener = listener;
            return this;
        }

        /**
         * 设置左边返回的图标
         */
        public Builder setLeftIcon(int iconId) {
            P.mLeftIcon = iconId;
            return this;
        }

        /**
         * 隐藏左边的返回控件
         */
        public Builder hiddenLeftIcon() {
            P.mLeftIconVisible = View.INVISIBLE;
            return this;
        }

        /**
         * 设置左边控件的点击事件
         */
        public Builder setLeftClickListener(View.OnClickListener listener) {
            P.mLeftClickListener = listener;
            return this;
        }

        /**
         * 设置是否沉浸式（默认是沉浸式）
         */
        public Builder isImmersive(boolean isImmersive) {
            P.mIsImmersive = isImmersive;
            return this;
        }

        /**
         * 状态栏字体是否深色
         */
        public Builder isStatusBarDarkFont(boolean isDarkFont) {
            P.mIsStatusBarDarkFont = isDarkFont;
            return this;
        }


        /**
         * 设置标题栏的背景颜色
         */
        public Builder setTitleBgColorRes(int colorRes) {
            P.mTitleBgColorRes = colorRes;
            return this;
        }

        /**
         * 设置标题背景的透明度
         */
        public Builder setTitleBgAlpha(int alpha) {
            P.mTitleBgAlpha = alpha;
            return this;
        }

        /**
         * 参数保存类
         */
        public static class DefaultNavigationParams extends AbsNavigationBar.Builder.AbsNavigationParams {
            // 承载页面内容的父布局
            public ViewGroup mContentParent;
            // Title所有的参数全部存放在这里
            public Context mContext;
            // 是否沉浸式
            public boolean mIsImmersive = true;
            // 状态栏字体颜色是否深色
            public boolean mIsStatusBarDarkFont = false;
            // 底部横线是否可见(默认是可见的)
            public int mBottomLineVisibility = View.GONE;
            // 背景颜色
            public int mTitleBgColorRes;
            // 状态栏占位条的背景颜色
            public int mTopViewBgColorRes;
            // 标题背景的透明度(0（透明）-255（不透明）)
            public int mTitleBgAlpha = 255;
            // 左边返回按钮的图标
            public int mLeftIcon;
            // 左边的控件的显示状态
            public int mLeftIconVisible = View.VISIBLE;
            // 标题文字
            public String mTitleText;
            // 标题文字颜色
            public int mTitleTextColor = 0;
            // 左边标题文字
            public String mLeftTitleText;
            // 中间的控件是否显示
            public int mMiddleTabVisible = View.GONE;
            // 中间Tab的数量
            public int mMiddleTabSize = 2;
            // 中间控件各个Tab的点击事件以及索引关系
            public SparseArray<View.OnClickListener> mTabClickListenerArray = new SparseArray<>();
            // 中间控件各个Tab的文字以及相应索引的关系
            public SparseArray<CharSequence> mTabTextArray = new SparseArray<>();
            // 标题位置的图片
            public int mTitleImage;
            // 是否添加标题栏右边文字
            public boolean mAddRightText;
            // 标题栏右边的文字
            public String mRightText;
            // 标题栏右边的文字颜色
            public int mRightTextColor = 0;
            // 是否添加标题栏右边的其他View控件
            public boolean mAddRightView;
            // 标题栏右边天剑的控件View
            public View mRightView;
            // 标题栏右边控件的可见状态
            public int mRightVisibility = View.VISIBLE;
            // 右边控件的点击事件
            public View.OnClickListener mRightClickListener;
            // 左边控件的点击事件
            public View.OnClickListener mLeftClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) mContext).finish();
                }
            };
            // 内容区域顶部阴影(标题栏底部阴影)是否显示
            public boolean mShowContentTopShadow = false;

            public DefaultNavigationParams(Context context, ViewGroup parent, ViewGroup contentParent) {
                super(context, parent);
                this.mContext = context;
                this.mContentParent = contentParent;
            }
        }

    }
}
