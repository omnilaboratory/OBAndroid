package com.omni.wallet.baselibrary.view.filterView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.entity.BaseFilterMenuEntity;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.view.filterView.data.BaseDataHelper;
import com.omni.wallet.baselibrary.view.filterView.menu.BaseMenuCreator;
import com.omni.wallet.baselibrary.view.filterView.title.BaseTitleCreator;

import java.util.ArrayList;
import java.util.List;


public class FilterMenuView extends LinearLayout {
    private static final String TAG = FilterMenuView.class.getSimpleName();
    private Context mContext;
    // 标题的父布局
    private LinearLayout mTitleLayout;
    // 底部菜单的控件
    private MenuView mMenuView;
    // 标题生成器
    private BaseTitleCreator mTitleCreator;
    // 底部菜单生成器的集合
    private List<BaseMenuCreator> mMenuCreatorList = new ArrayList<>();
    // 底部菜单的数据的生成器
    protected BaseDataHelper mMenuDataHelper;
    // 条件点击的回调
    private FilterMenuViewCallback mCallback;
    // 添加ChildView的索引
    private int mChildIndex = 0;
    // 是否显示菜单下边分割线
    private boolean mShowLine = true;
    // 标题是否显示选择的信息
    private boolean mShowSelect = true;

    public FilterMenuView(Context context) {
        this(context, null);
    }

    public FilterMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    /**
     * 是否显示底部横线
     */
    public void setShowLine(boolean showLine) {
        this.mShowLine = showLine;
    }

    /**
     * 设置是否在标题栏显示选择的条目
     */
    public void setShowSelected(boolean showSelected) {
        mShowSelect = showSelected;
    }

    /**
     * 初始化
     */
    private void initView() {
        // 设置布局方向
        setOrientation(LinearLayout.VERTICAL);
        // 初始化标题父布局
        mTitleLayout = new LinearLayout(mContext);
        mTitleLayout.setOrientation(HORIZONTAL);
        mTitleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTitleLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_white));
        addView(mTitleLayout, mChildIndex);
        mChildIndex++;
        // 添加分割线
        if (mShowLine) {
            View lineView = new View(mContext);
            lineView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_eaeaea));
            lineView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(mContext, 0.5f)));
            // 添加进去
            addView(lineView, mChildIndex);
            mChildIndex++;
        }
        // 初始化菜单控件
        mMenuView = new MenuView(mContext);
        mMenuView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置回调
        mMenuView.setCallback(new MyMenuViewCallback());
        // 添加进去
        addView(mMenuView, mChildIndex);
    }


    /**
     * 底部菜单控件的回调
     */
    private class MyMenuViewCallback implements MenuView.MenuViewCallback {

        @Override
        public void onMenuShow(int index) {
            // 菜单显示事件回调给MenuCreator
            BaseMenuCreator filterMenuCreator = mMenuCreatorList.get(index);
            if (filterMenuCreator != null) {
                filterMenuCreator.onMenuShow();
            }
        }

        @Override
        public void onMenuClose(int index) {
            mTitleCreator.onMenuClose(index);
            // 菜单关闭事件回调给MenuCreator
            BaseMenuCreator filterMenuCreator = mMenuCreatorList.get(index);
            if (filterMenuCreator != null) {
                filterMenuCreator.onMenuClose();
            }
        }
    }

    /**
     * 设置数据源
     */
    public void setDataHelper(BaseDataHelper dataHelper) {
        this.mMenuDataHelper = dataHelper;
        mMenuDataHelper.setFilterMenuDataCallback(new BaseDataHelper.FilterMenuDataCallback() {
            @Override
            public void onDataSuccess(int index, List<BaseFilterMenuEntity> data) {
                BaseMenuCreator menuCreator = mMenuCreatorList.get(index);
                if (menuCreator != null) {
                    menuCreator.refreshViewShow(data);
                }
            }
        });
    }

    /**
     * 获取菜单数据
     */
    public void requestMenuData() {
        mMenuDataHelper.requestFilterData();
    }

    /**
     * 刷新数据
     */
    public void refreshMenuData() {
        mMenuDataHelper.requestFilterData();
    }


    /**
     * 设置标题生成器
     */
    public void setTitleCreator(BaseTitleCreator titleCreator) {
        this.mTitleCreator = titleCreator;
        titleCreator.setCallback(new MyTitleViewCallback());
        // 移除原来的标题View
        mTitleLayout.removeAllViews();
        // 获取标题的View
        View childView = mTitleCreator.getRootView();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        childView.setLayoutParams(params);
        // 将标题布局添加到菜单控件中
        mTitleLayout.addView(childView);
    }

    /**
     * 标题点击回调
     */
    private class MyTitleViewCallback implements BaseTitleCreator.MenuTitleCallback {

        @Override
        public void onClickTitle(int index, boolean showMenu, View titleView) {
            if (titleView == null) {
                return;
            }
            if (showMenu) {
                // 展示相应的Menu布局
                mMenuView.showMenuByIndex(index);
            }
            // 点击事件回调出去
            if (mCallback != null) {
                mCallback.onClickTitleItem(index, titleView);
            }
        }
    }

    /**
     * 通过设置底部菜单生成器，生成底部菜单
     */
    public void setMenuCreatorList(List<BaseMenuCreator> bottomCreatorList) {
        this.mMenuCreatorList = bottomCreatorList;
        if (bottomCreatorList == null) {
            return;
        }
        List<View> layoutList = new ArrayList<>();
        if (bottomCreatorList.size() > 0) {
            for (int i = 0; i < bottomCreatorList.size(); i++) {
                BaseMenuCreator creator = bottomCreatorList.get(i);
                layoutList.add(creator.getMenuLayout());
                creator.setMenuCallback(new MyMenuCallback(i));
                // 将最外层的父布局的引用设置进去，便于调用关闭菜单的方法
                creator.setFilterBottomMenu(mMenuView);
            }
            mMenuView.setMenuLayouts(layoutList);
        }
    }


    /**
     * 底部菜单的回调
     */
    private class MyMenuCallback implements BaseMenuCreator.MenuCallback {
        private int mIndex;

        MyMenuCallback(int index) {
            this.mIndex = index;
        }

        @Override
        public void onSelectedCondition(Object conditionId, String showCondition) {
            if (mShowSelect) {
                mTitleCreator.setShowCondition(mIndex, showCondition);
            }
            // 回调到整个菜单控件的引用者
            if (mCallback != null) {
                mCallback.onSelectCondition(mIndex, conditionId);
            }
        }
    }


    /**
     * 关闭显示的菜单
     */
    public void closeMenu(boolean animation) {
        if (isMenuShow()) {
            mMenuView.closeMenu(animation);
        }
    }

    /**
     * 菜单的选中状态和显示状态重置，并关闭菜单
     */
    public void resetMenu() {
        // 重置菜单
        for (BaseMenuCreator menuCreator : mMenuCreatorList) {
            menuCreator.resetMenu();
        }
        // 重置标题栏
        if (mShowSelect) {
            mTitleCreator.resetTitle();
        }
        // 关闭菜单
        closeMenu(false);
    }

    /**
     * 菜单是否显示
     */
    public boolean isMenuShow() {
        return mMenuView != null && mMenuView.isShowing();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMenuDataHelper.onViewDestroy();
    }

    /**
     * 设置标题条件条目点击的监听
     */
    public void setCallback(FilterMenuViewCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 条件标题条目点击监听
     */
    public interface FilterMenuViewCallback {
        // 点击筛选标题
        void onClickTitleItem(int index, View titleView);

        // 选择条件条目
        void onSelectCondition(int index, Object selectCondition);
    }
}
