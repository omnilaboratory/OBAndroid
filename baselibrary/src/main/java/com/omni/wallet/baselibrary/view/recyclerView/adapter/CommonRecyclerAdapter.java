package com.omni.wallet.baselibrary.view.recyclerView.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.List;

/**
 * 通用的RecyclerView的Adapter
 */

public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = CommonRecyclerAdapter.class.getSimpleName();
    protected Context mContext;
    private LayoutInflater mInflater;
    //数据怎么办？利用泛型
    protected List<T> mData;
    // 布局怎么办？直接从构造里面传递
    private int mLayoutId;
    // 多布局支持
    private MultiTypeSupport mMultiTypeSupport;
    // 局部刷新的标记
    private static final int FLAG_PLAY_LOADS = 99999;

    // 数据怎么办？ 布局怎么办？ 绑定怎么办？
    public CommonRecyclerAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    /**
     * 多布局支持
     */
    public CommonRecyclerAdapter(Context context, List<T> data, MultiTypeSupport<T> multiTypeSupport) {
        this(context, data, -1);
        this.mMultiTypeSupport = multiTypeSupport;
    }

    /**
     * 根据当前位置获取不同的viewType
     */
    @Override
    public int getItemViewType(int position) {
        // 多布局支持
        if (mMultiTypeSupport != null) {
            return mMultiTypeSupport.getLayoutId(mData.get(position), position);
        }
        return super.getItemViewType(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 多布局支持
        if (mMultiTypeSupport != null) {
            mLayoutId = viewType;
        }
        // 先inflate数据
        View itemView = mInflater.inflate(mLayoutId, parent, false);
        // 返回ViewHolder
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        convert(holder, position, mData.get(position));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            convert(holder, position, mData.get(position));
        } else {
            onNotifyView(holder, position, mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * 利用抽象方法回传出去，每个不一样的Adapter去设置
     *
     * @param item 当前的数据
     */
    public abstract void convert(ViewHolder holder, int position, T item);


    /**
     * 统一处理的条目移除的局部刷新
     */
    public void notifyRemoveItem(int position) {
        notifyItemRemoved(position);
        if (position != mData.size()) {
            notifyItemRangeChanged(position, mData.size() - position);
        }
    }

    /**
     * 统一处理的条目插入的局部刷新
     */
    public void notifyInsertItem(int position) {
        notifyItemInserted(position);
        if (position != mData.size()) {
            notifyItemRangeChanged(position, mData.size() - position);
        }
    }


    /**
     * 更新单个的View
     */
    public void notifyView() {
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            notifyItemChanged(i, FLAG_PLAY_LOADS);
        }
    }

    /**
     * 更新单个的View
     */
    public void notifyView(int position) {
        notifyItemChanged(position, FLAG_PLAY_LOADS);
    }


    /**
     * 更新View的时候回调
     */
    public void onNotifyView(ViewHolder holder, int position, T item) {
    }


    /**
     * 是否是可见条目
     *
     * @param position: TODO 注意这里的position是需要加上header的数量的
     */
    public boolean isVisibleItem(int position, RecyclerView.LayoutManager layoutManager) {
        if (layoutManager != null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
            // 可见的时候再刷新（注意RecyclerView有一个RefreshHeader，所以索引减1）
            if (position >= firstPosition && position <= lastPosition) {
                return true;
            }
        }
        return false;
    }
}
