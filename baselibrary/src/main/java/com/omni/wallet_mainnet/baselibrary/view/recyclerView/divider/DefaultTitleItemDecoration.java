package com.omni.wallet_mainnet.baselibrary.view.recyclerView.divider;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

/**
 * 默认的悬浮标题的Decoration
 */

public abstract class DefaultTitleItemDecoration<T> extends TitleItemDecoration<T> {
    private static final String TAG = DefaultTitleItemDecoration.class.getSimpleName();
    private Context mContext;
    private SparseArray<View> tempTitleArray = new SparseArray<>();

    public DefaultTitleItemDecoration(Context context, List<T> data) {
        super(data);
        this.mContext = context;
    }


    @Override
    public View getTopTitleView(int position) {
        if (!isDrawTopTitle(position)) {
            return null;
        }
        View titleView = tempTitleArray.get(0);
        if (titleView == null) {
            titleView = LayoutInflater.from(mContext).inflate(getTitleLayoutId(), null, false);
            tempTitleArray.put(0, titleView);
        }
        // View中数据填充
        fillTitleView(titleView, position);
        return titleView;
    }

    /**
     * 填充Title中的数据
     */
    protected abstract void fillTitleView(View rootView, int position);

    /**
     * 获取TitleView的布局ID
     */
    protected abstract int getTitleLayoutId();

    /**
     * 判断当前Position的Item是否需要绘制悬浮标题（默认true）
     */
    protected boolean isDrawTopTitle(int position) {
        return true;
    }
}
