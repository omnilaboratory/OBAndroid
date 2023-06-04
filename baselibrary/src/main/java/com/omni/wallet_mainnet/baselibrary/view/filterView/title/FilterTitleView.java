package com.omni.wallet_mainnet.baselibrary.view.filterView.title;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 标题栏控件基类
 */

public class FilterTitleView extends LinearLayout {
    private static final String TAG = FilterTitleView.class.getSimpleName();
    protected Context mContext;
    protected View mRootView;
    private TextView mTextView;
    private ImageView mRightIv;

    public FilterTitleView(Context context) {
        this(context, null);
    }

    public FilterTitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_filter_menu_title_default, null, false);
        mTextView = mRootView.findViewById(R.id.tv_filter_title);
        mRightIv = mRootView.findViewById(R.id.iv_filter_right);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);
    }

    /**
     * 选择的条件回显到标题栏
     */
    public void setTitleText(String condition) {
        mTextView.setMaxLines(1);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTextView.setText(condition);
    }

    /**
     * 设置菜单展开收起状态
     */
    public void setMenuOpen(boolean state) {
        mRightIv.setSelected(state);
    }

    /**
     * 设置是否选择了条件的状态
     */
    public void setSelectedCondition(boolean state) {
        mTextView.setSelected(state);
    }


}
