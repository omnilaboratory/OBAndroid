package com.omni.wallet_mainnet.view.popupwindow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 汉: 选择块数的弹窗
 * En: SelectBlocksPopupWindow
 * author: guoyalei
 * date: 2023/4/17
 */
public class SelectBlocksPopupWindow {
    private static final String TAG = SelectBlocksPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;
    RecyclerView mRecyclerView;

    public List<Integer> mData = new ArrayList<>();
    private MyAdapter mAdapter;

    public SelectBlocksPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String type) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_blocks);
            mBasePopWindow.setWidth(view.getWidth());
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mBasePopWindow.setOutsideTouchable(true);
            mBasePopWindow.setFocusable(false);
            mRecyclerView = rootView.findViewById(R.id.recycler_blocks_list);
            if (type.equals("FAST")) {
                mData.add(1);
                mData.add(2);
                mData.add(3);
                mData.add(4);
                mData.add(5);
            } else if (type.equals("MEDIUM")) {
                mData.add(6);
                mData.add(7);
                mData.add(8);
                mData.add(9);
                mData.add(10);
            } else if (type.equals("SLOW")) {
                mData.add(11);
                mData.add(12);
                mData.add(13);
                mData.add(14);
                mData.add(15);
                mData.add(16);
                mData.add(17);
                mData.add(18);
                mData.add(19);
                mData.add(20);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_amount_unit_list);
            mRecyclerView.setAdapter(mAdapter);
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAsDropDown(view);
        }
    }

    /**
     * the adapter of block list
     * 块数列表列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<Integer> {

        public MyAdapter(Context context, List<Integer> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final Integer item) {
            holder.setText(R.id.tv_amount_unit, item + "");
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v, item);
                    }
                    mBasePopWindow.dismiss();
                }
            });
        }
    }

    public void setOnItemClickCallback(ItemCleckListener itemCleckListener) {
        this.mCallback = itemCleckListener;
    }

    public interface ItemCleckListener {
        void onItemClick(View view, Integer item);
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
