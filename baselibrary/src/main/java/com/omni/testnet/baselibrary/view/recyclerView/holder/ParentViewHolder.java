package com.omni.testnet.baselibrary.view.recyclerView.holder;

import android.view.View;

import com.omni.testnet.baselibrary.view.recyclerView.ItemClickListener;


public class ParentViewHolder extends ViewHolder {


    public ParentViewHolder(View itemView) {
        super(itemView);
    }


    public void setParentClickListener(ItemClickListener itemClickListener) {
        itemView.setOnClickListener(new MyClickListener(itemClickListener));
    }


    /**
     * 含有子项的父布局的点击事件
     */
    private class MyClickListener implements View.OnClickListener {
        private ItemClickListener itemClickListener;

        public MyClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
//                if (dataBean.isExpand()) {
//                    itemClickListener.onHideChildren(dataBean);
//                    parentDashedView.setVisibility(View.VISIBLE);
//                    dataBean.setExpand(false);
//                    rotationExpandIcon(90, 0);
//                } else {
//                    itemClickListener.onExpandChildren(dataBean);
//                    parentDashedView.setVisibility(View.INVISIBLE);
//                    dataBean.setExpand(true);
//                    rotationExpandIcon(0, 90);
//                }
            }
        }
    }

}
