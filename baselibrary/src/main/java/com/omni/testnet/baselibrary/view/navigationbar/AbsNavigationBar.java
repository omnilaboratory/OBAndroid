package com.omni.testnet.baselibrary.view.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntRange;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;


/**
 * 自定义Title的抽象基类
 * 由于是抽象类，所以接口中的方法不需要实现，直接在子类中再覆盖即可
 */
public abstract class AbsNavigationBar<P extends AbsNavigationBar.Builder.AbsNavigationParams> implements INavigationBar {
    private static final String TAG = AbsNavigationBar.class.getSimpleName();

    // 参数集对象
    private P mParams;

    private View navigationView;

    /**
     * 构造方法
     */
    public AbsNavigationBar(P params) {
        this.mParams = params;
        createAndBindView();
    }

    /**
     * 获取存储参数集合的Params对象
     */
    public P getParams() {
        return mParams;
    }


    /**
     * 创建和绑定View
     */
    private void createAndBindView() {
        // 1.创建View
        if (bindLayoutResId() == 0) {
            LogUtils.e(TAG, "没有获取到布局文件ID");
            return;
        }
        // 对parent做判空，因为前边Builder的构造中有不传parent的构造方法
        if (mParams.mParent == null) {
            // 关于这里的解释，去查看setContentView的源码解析.txt
            ViewGroup viewGroup = (ViewGroup) ((Activity) mParams.mContext).getWindow().getDecorView();
            mParams.mParent = (ViewGroup) viewGroup.getChildAt(0);
        }
        // 做最后的判断
        if (mParams.mParent == null) {
            LogUtils.e(TAG, "父布局为null或者没有获取到父布局");
            return;
        }
        //（注意，最后面的false一定要传）
        navigationView = LayoutInflater.from(mParams.mContext).inflate(bindLayoutResId(), null, false);
        // 2.添加到父布局中
        mParams.mParent.addView(navigationView, 0);
        // 3.添加到父控件中之后再实现里边的效果
        applyView();
    }

    // 对子类提供一些常用的方法，比如设置文字、设置点击事件什么的等等

    /**
     * 设置背景颜色
     */
    public void setBackGroundResource(int viewId, int colorRes) {
        if (colorRes != 0) {
            getView(viewId).setVisibility(View.VISIBLE);
            getView(viewId).setBackgroundResource(colorRes);
        }
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int viewId, int color) {
        if (color != 0) {
            getView(viewId).setVisibility(View.VISIBLE);
            getView(viewId).setBackgroundColor(color);
        }
    }

    /**
     * 设置背景颜色透明度(0-255)
     */
    public void setTitleBgAlpha(int viewId, @IntRange(from = 0, to = 255) int alpha) {
        if (alpha > 255) {
            alpha = 255;
        }
        if (alpha < 0) {
            alpha = 0;
        }
        View titleView = getView(viewId);
        // TODO 坑坑坑坑坑坑坑
        // TODO 坑坑坑坑坑坑坑
        // 使用mutate()方法使该控件状态不定，这样不定状态的控件就不会共享自己的状态了
        // .mutate()这个方法必须要加，将这个背景的Drawable标记为不稳定状态，使其不被共享
        // 否则会出现内存波动，频繁GC，画面绘制异常等等一系列的BUG，贼鸡儿恶心
        titleView.getBackground().mutate().setAlpha(alpha);
    }

    /**
     * 设置文字
     */
    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        if (textView != null) {
            if (StringUtils.isEmpty(text)) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
            textView.setText(text);
        }
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int viewId, int color) {
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    /**
     * 设置图片
     */
    public void setImage(int imageId, int imageRes) {
        ImageView imageView = getView(imageId);
        if (imageView != null) {
            imageView.setImageResource(imageRes);
        }
    }


    // 设置控件是否可见
    public void setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }

    /**
     * 设置点击事件
     */
    public void setClickListener(int viewId, View.OnClickListener clickListener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(clickListener);
        }
    }

    /**
     * 根据Id获取对应控件
     */
    public <T extends View> T getView(int viewId) {
        return navigationView.findViewById(viewId);
    }

    public <T extends ViewGroup> T getViewGroup(int viewId) {
        return navigationView.findViewById(viewId);
    }


    /**
     * 自定义Title的构建类
     */
    public abstract static class Builder {

        /**
         * 构造方法，需要传递一个上下文，一个承载自定义Title的父容器
         */
        public Builder(Context context, ViewGroup parent) {
        }

        /**
         * 构建Title的Build方法，具体构建方式由子类去实现
         */
        public abstract AbsNavigationBar build();

        /**
         * 自定义Title的参数保存类
         */
        public static class AbsNavigationParams {
            public Context mContext;
            public ViewGroup mParent;

            public AbsNavigationParams(Context context, ViewGroup parent) {
                this.mContext = context;
                this.mParent = parent;
            }
        }
    }

}
