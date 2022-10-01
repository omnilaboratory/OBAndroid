package com.omni.wallet.popupwindow.send;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * SendStepOne的弹窗
 */
public class SendStepOnePopupWindow {
    private static final String TAG = SendStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;
    SendStepTwoPopupWindow mSendStepTwoPopupWindow;
    View mView;

    public SendStepOnePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mView = view;
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(Gravity.CENTER);
            // send list RecyclerView
            RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_send_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            for (int i = 0; i < 3; i++) {
                String str = new String();
                mData.add(str);
            }
            mAdapter = new MyAdapter(mContext, mData, R.layout.layout_item_send_list);
            mRecyclerView.setAdapter(mAdapter);
            // 点击底部cancel
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * send list列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<String> {

        public MyAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final String item) {
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepTwoPopupWindow = new SendStepTwoPopupWindow(mContext);
                    mSendStepTwoPopupWindow.show(mView);
                }
            });
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
