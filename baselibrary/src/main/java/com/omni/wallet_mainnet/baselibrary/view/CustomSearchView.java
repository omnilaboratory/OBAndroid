package com.omni.wallet_mainnet.baselibrary.view;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.omni.wallet_mainnet.baselibrary.R;
import com.omni.wallet_mainnet.baselibrary.utils.ListUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.view.editText.DefaultTextWatcher;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天的搜索框
 */

public class CustomSearchView<T> extends RelativeLayout {
    private static final String TAG = CustomSearchView.class.getSimpleName();

    private Context mContext;
    private EditText mContentEt;
    private MyRunnable mRunnable;
    private ListUtils.ListUtilsHook<T> mListHook;
    private CommonRecyclerAdapter mAdapter;
    private List<T> mShowData = new ArrayList<>();// 显示的数据
    private List<T> mCopyData = new ArrayList<>();// 复制的数据
    protected InputMethodManager mInputMethodManager;

    public CustomSearchView(Context context) {
        this(context, null);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    public void setAdapter(CommonRecyclerAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void setData(List<T> data) {
        this.mShowData = data;
        mCopyData.clear();
        mCopyData.addAll(data);
    }

    public void setListHook(ListUtils.ListUtilsHook<T> listHook) {
        this.mListHook = listHook;
    }

    private void init() {
        mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_view, null);
        mContentEt = rootView.findViewById(R.id.et_search_view_content);
        mContentEt.addTextChangedListener(new MyTextWatcher());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(params);
        addView(rootView);
    }

    public void setHint(String hint) {
        mContentEt.setHint(hint);
    }

    /**
     * 文本变动监听
     */
    private class MyTextWatcher extends DefaultTextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            if (s == null) {
                return;
            }
            if (mRunnable == null) {
                mRunnable = new MyRunnable();
            }
            //输入完成后延迟100毫秒在请求
            if (getHandler() != null) {
                getHandler().removeCallbacks(mRunnable);
            }
            mRunnable.setSearchText(s.toString());
            postDelayed(mRunnable, 100);
        }
    }

    /**
     * 筛选
     */
    private class MyRunnable implements Runnable {
        private String mSearchText;

        public void setSearchText(String searchText) {
            this.mSearchText = searchText;
        }

        @Override
        public void run() {
            List<T> resultList;
            if (StringUtils.isEmpty(mSearchText)) {
                resultList = mCopyData;
            } else {
                resultList = ListUtils.filter(mCopyData, mSearchText, mListHook);
            }
            mShowData.clear();
            mShowData.addAll(resultList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void clearInput() {
        mContentEt.setText(null);
    }

    public void clearEtFocus() {
        mContentEt.clearFocus();
    }
}
