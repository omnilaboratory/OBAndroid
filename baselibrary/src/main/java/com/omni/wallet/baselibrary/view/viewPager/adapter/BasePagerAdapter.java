package com.omni.wallet.baselibrary.view.viewPager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet.baselibrary.view.viewPager.holder.PagerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可以填充布局的ViewPager适配器
 */

public abstract class BasePagerAdapter<T> extends PagerAdapter {
    private static final String TAG = BasePagerAdapter.class.getSimpleName();

    private Context mContext;
    private List<T> mData;
    private int mLayoutId;
    private ArrayList<View> mConvertViews = new ArrayList<>();// 用来存放被销毁的Item的集合
    private Map<View, PagerViewHolder> mHolderMap = new HashMap<>();
    private boolean mUseCache;// 是否使用缓存

    public BasePagerAdapter(Context context, List<T> mData, int mLayoutId) {
        this.mContext = context;
        this.mData = mData;
        this.mLayoutId = mLayoutId;
    }

    public void setUseCache(boolean useCache) {
        this.mUseCache = useCache;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        if (mData.size() > 1) {
            return Integer.MAX_VALUE;
        }
        return mData.size();
    }

    /**
     * 强制使得NotifyDataSetChanged生效
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (mData == null || mData.size() == 0) {
            return super.instantiateItem(container, position);
        }
        // 无限轮播的时候获取正确的索引
        if (mData.size() != 0 && position >= mData.size()) {
            position = position % mData.size();
        }
        PagerViewHolder holder = getViewHolder();
        convertView(position, holder, mData.get(position));
        // 将获取到的View添加到ViewPager中
        container.addView(holder.getItemView());
        return holder.getItemView();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // 将当前位置的View移除
        container.removeView((View) object);
        // 将销毁的Item放到缓存的集合中缓存起来，以便于重复利用，减小系统压力
        if (mUseCache) {
            mConvertViews.add((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private View getConvertView() {
        for (int i = 0; i < mConvertViews.size(); i++) {
            // 判断是否已经被使用
            if (mConvertViews.get(i).getParent() == null) {
                return mConvertViews.get(i);
            }
        }
        return LayoutInflater.from(mContext).inflate(mLayoutId, null, false);
    }

    private PagerViewHolder getViewHolder() {
        View itemView = getConvertView();
        PagerViewHolder holder = mHolderMap.get(itemView);
        if (holder == null) {
            holder = new PagerViewHolder(itemView);
            mHolderMap.put(itemView, holder);
        }
        return holder;
    }

    protected abstract void convertView(int position, PagerViewHolder holder, T item);
}
