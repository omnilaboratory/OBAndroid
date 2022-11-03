package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;

/**
 * 汉: 选择资产类型的弹窗
 * En: SelectAssetUnitPopupWindow
 * author: guoyalei
 * date: 2022/11/3
 */
public class SelectAssetUnitPopupWindow {
    private static final String TAG = SelectAssetUnitPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;
    RecyclerView mRecyclerView;
    private List<LightningOuterClass.Asset> mData = new ArrayList<>();
    private MyAdapter mAdapter;

    public SelectAssetUnitPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, List<LightningOuterClass.Asset> list) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_asset);
            mBasePopWindow.setWidth(view.getWidth());
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mRecyclerView = rootView.findViewById(R.id.recycler_asset_list);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_amount_unit_list);
            mData.clear();
            mData.addAll(list);
            mRecyclerView.setAdapter(mAdapter);
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAsDropDown(view);
        }
    }


    /**
     * the adapter of asset list
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<LightningOuterClass.Asset> {

        public MyAdapter(Context context, List<LightningOuterClass.Asset> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final LightningOuterClass.Asset item) {
            holder.setText(R.id.tv_amount_unit, item.getName());
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
        void onItemClick(View view, LightningOuterClass.Asset item);
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
