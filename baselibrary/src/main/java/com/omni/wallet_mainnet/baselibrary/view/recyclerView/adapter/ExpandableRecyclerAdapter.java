package com.omni.wallet_mainnet.baselibrary.view.recyclerView.adapter;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.view.recyclerView.ItemClickListener;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.List;



public abstract class ExpandableRecyclerAdapter<T> extends CommonRecyclerAdapter<T> {

    private OnScrollListener mOnScrollListener;

    public ExpandableRecyclerAdapter(Context context, List<T> data, int layoutId) {
        super(context, data, layoutId);
    }

    public ExpandableRecyclerAdapter(Context context, List<T> data, MultiTypeSupport<T> multiTypeSupport) {
        super(context, data, multiTypeSupport);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, position, mData.get(position), itemClickListener);
    }

    public abstract void convert(ViewHolder holder, int position, T item, ItemClickListener itemClickListener);

    private ItemClickListener itemClickListener = new ItemClickListener<T>() {
        @Override
        public void onExpandChildren(T bean) {
//            int position = getCurrentPosition(bean.getID());//确定当前点击的item位置
//            DataBean children = getChildDataBean(bean);//获取要展示的子布局数据对象，注意区分onHideChildren方法中的getChildBean()。
//            if (children == null) {
//                return;
//            }
//            add(children, position + 1);//在当前的item下方插入
//            if (position == dataBeanList.size() - 2 && mOnScrollListener != null) { //如果点击的item为最后一个
//                mOnScrollListener.scrollTo(position + 1);//向下滚动，使子布局能够完全展示
//            }
        }

        @Override
        public void onHideChildren(T bean) {
//            int position = getCurrentPosition(bean.getID());//确定当前点击的item位置
//            DataBean children = bean.getChildBean();//获取子布局对象
//            if (children == null) {
//                return;
//            }
//            remove(position + 1);//删除
//            if (mOnScrollListener != null) {
//                mOnScrollListener.scrollTo(position);
//            }
        }
    };

//    /**
//     * 在父布局下方插入一条数据
//     *
//     * @param bean
//     * @param position
//     */
//    public void add(T bean, int position) {
//        dataBeanList.add(position, bean);
//        notifyItemInserted(position);
//    }
//
//    /**
//     * 移除子布局数据
//     *
//     * @param position
//     */
//    protected void remove(int position) {
//        dataBeanList.remove(position);
//        notifyItemRemoved(position);
//    }

    /**
     * 自定义的滚动监听接口
     */
    public interface OnScrollListener {
        void scrollTo(int pos);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }
}
