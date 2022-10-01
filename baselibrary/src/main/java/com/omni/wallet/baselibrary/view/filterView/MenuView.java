package com.omni.wallet.baselibrary.view.filterView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.omni.wallet.baselibrary.R;

import java.util.List;

/**
 * 筛选框下方菜单
 */

public class MenuView extends FrameLayout {
    private static final String TAG = MenuView.class.getSimpleName();

    private Context mContext;
    //弹出菜单父布局
    private FrameLayout mMenuParentView;
    //遮罩背景的半透明View，点击可关闭DropDownMenu
    private View mMaskView;
    // 当前显示的Position
    private int mCurrentShowIndex = -1;
    // 临时存储需要显示的菜单View的集合
    private SparseArray<View> mMenuViewArrays = new SparseArray<>();


    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        // 初始化遮罩的View
        mMaskView = new View(mContext);
        mMaskView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mMaskView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_half_transparent));
        mMaskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(true);
            }
        });
        mMaskView.setVisibility(GONE);
        addView(mMaskView);
        // 初始化Menu的父布局
        mMenuParentView = new FrameLayout(getContext());
        mMenuParentView.setVisibility(View.GONE);
        mMenuParentView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mMenuParentView);
    }


    /**
     * 设置Menu需要显示的几种布局（与标题的索引对应）
     */
    public void setMenuLayouts(List<View> menuItems) {
        int size = menuItems.size();
        mMenuViewArrays.clear();
        for (int i = 0; i < size; i++) {
            mMenuViewArrays.put(i, menuItems.get(i));
        }
    }


    /**
     * 是否处于可见状态
     */
    public boolean isShowing() {
        return mCurrentShowIndex != -1;
    }

    /**
     * 根据索引显示相应菜单
     */
    public void showMenuByIndex(int index) {
        if (mCurrentShowIndex == index) {// 点击的是当前显示的，就直接关闭
            closeMenu(true);
        } else {// 点击的是其他的，关闭菜单再显示（不能直接切换，关闭菜单会触发标题栏状态的刷新）
            closeMenu(true);
            // 如果当前菜单没有显示，弹出菜单；如果显示了直接切换
            mMenuParentView.setVisibility(View.VISIBLE);
            mMenuParentView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.filter_menu_anim_in));
            mMaskView.setVisibility(VISIBLE);
            mMaskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.filter_menu_mask_anim_in));
            // 将子布局添加进父布局中
            addChildView(index);
            mCurrentShowIndex = index;
            // 菜单展开的事件回调回去
            if (mCallback != null) {
                mCallback.onMenuShow(index);
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(boolean animation) {
        if (mCurrentShowIndex != -1) {
            mMenuParentView.setVisibility(View.GONE);
            mMaskView.setVisibility(GONE);
            if (animation) {
                mMenuParentView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.filter_menu_anim_out));
                mMaskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.filter_menu_mask_anim_out));
            }
            // TODO 这里关闭的时候就不移除ChildView了不然动画看不见了
            // 关闭的时候也回调
            if (mCallback != null) {
                mCallback.onMenuClose(mCurrentShowIndex);
            }
            mCurrentShowIndex = -1;
        }
    }

    /**
     * 添加ChildView
     */
    private void addChildView(int index) {
        mMenuParentView.removeAllViews();
        View childView = mMenuViewArrays.get(index);
        if (childView != null) {
            childView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mMenuParentView.addView(childView);
        }
    }


    // 菜单容器控件的回调
    private MenuViewCallback mCallback;

    public void setCallback(MenuViewCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 菜单容器控件的回调接口
     */
    public interface MenuViewCallback {

        // 这里的index是对应上方菜单的索引
        void onMenuShow(int index);

        // 这里的index是对应上方菜单的索引
        void onMenuClose(int index);
    }
}
