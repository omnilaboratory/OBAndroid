package com.omni.testnet.baselibrary.view.filterView.menu;

import android.content.Context;
import android.view.View;

import com.omni.testnet.baselibrary.entity.BaseFilterMenuEntity;
import com.omni.testnet.baselibrary.view.filterView.MenuView;

import java.util.List;

/**
 * 底部菜单生成器
 */

public abstract class BaseMenuCreator {
    private static final String TAG = BaseMenuCreator.class.getSimpleName();

    protected Context mContext;
    // 底部菜单的回调
    protected MenuCallback mCallback;
    // 底部菜单列表的父布局
    protected MenuView mMenuView;

    public BaseMenuCreator(Context context) {
        this.mContext = context;
    }

    /**
     * 底部菜单列表的父布局
     */
    public void setFilterBottomMenu(MenuView menuView) {
        this.mMenuView = menuView;
    }

    /**
     * 获取自定义的MenuView
     */
    public abstract View getMenuLayout();

    /**
     * 刷新数据显示
     */
    public abstract void refreshViewShow(List<BaseFilterMenuEntity> data);

    /**
     * 重置菜单
     */
    public void resetMenu() {
    }

    /**
     * 菜单关闭的时候回调
     */
    public void onMenuClose() {

    }

    /**
     * 菜单显示的时候回调
     */
    public void onMenuShow() {

    }

    /**
     * 设置回调
     */
    public void setMenuCallback(MenuCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 设置菜单的回显数据
     */
    public void setMenuDisplayData(int index, String... id) {

    }

    /**
     * 底部菜单条目点击的回调
     */
    public interface MenuCallback {

        void onSelectedCondition(Object conditionId, String showCondition);
    }
}
