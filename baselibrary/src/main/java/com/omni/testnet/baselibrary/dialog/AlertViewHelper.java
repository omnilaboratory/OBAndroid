package com.omni.testnet.baselibrary.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * dialog的视图辅助处理类
 */

class AlertViewHelper {
    private View mContentView = null;
    // 用来存储获取过的控件的集合，避免频繁的调用findViewById
    // 同时使用软引用，便于在内存不足的时候释放
    private SparseArray<WeakReference<View>> mViews = null;

    public AlertViewHelper(Context context, int mViewLayoutResId) {
        this();
        mContentView = LayoutInflater.from(context).inflate(mViewLayoutResId, null);
    }

    public AlertViewHelper() {
        mViews = new SparseArray<>();
    }


    public View getContentView() {
        return mContentView;
    }

    public void setContentView(View mContentView) {
        this.mContentView = mContentView;
    }

    /**
     * 设置Dialog中控件的文本
     *
     * @param mViewId      控件ID
     * @param charSequence 设置的文本
     */
    public void setText(int mViewId, CharSequence charSequence) {
        // 由于Button的父类也是textView 所以这里统一用TextView接收
        TextView view = getViewById(mViewId);
        if (view != null) {
            view.setText(charSequence);
        }
    }

    /**
     * 设置控件的点击事件
     *
     * @param mViewId         控件ID
     * @param onClickListener 设置的点击事件
     */
    public void setOnClickListener(int mViewId, View.OnClickListener onClickListener) {
        View view = getViewById(mViewId);
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
    }

    /**
     * 根据Id获取相应控件
     *
     * @param mViewId 控件ID
     * @param <T>     获取到的控件
     * @return
     */
    public <T extends View> T getViewById(int mViewId) {
        View view = null;
        // 先到缓存的集合中获取
        WeakReference<View> weakView = mViews.get(mViewId);
        if (weakView != null) {
            view = weakView.get();
        }
        // 如缓存的集合中没有获取到，再使用findViewById获取
        if (view == null && mViewId != 0) {
            view = mContentView.findViewById(mViewId);
        }
        return (T) view;
    }
}
