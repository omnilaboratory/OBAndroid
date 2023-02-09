package com.omni.testnet.baselibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.utils.DisplayUtil;


/**
 * banner指示器
 */

public class ViewPagerIndicator extends LinearLayout {

    private int mSum = 0;
    private int mSelected = 0;
    private Context mContext;
    private int mSelectedId = R.drawable.icon_banner_dot_over, mUnselectedId = R.drawable.icon_banner_dot;

    public ViewPagerIndicator(Context context) {
        this(context, null);
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setLength(int sum) {
        this.mSum = sum;
        this.mSelected = 0;
        draw();
    }

    public void setSelected(int selected) {
        removeAllViews();
        this.mSelected = mSum == 0 ? 0 : selected % mSum;
        draw();
    }

    public void setSelected(int selected, int selected_id, int unselected_id) {
        removeAllViews();
        this.mSelectedId = selected_id;
        this.mUnselectedId = unselected_id;
        this.mSelected = mSum == 0 ? 0 : selected % mSum;
        draw();
    }

    public void draw() {
        for (int i = 0; i < mSum; i++) {
            ImageView imageView = new ImageView(mContext);
            if (i == mSelected) {
                imageView.setImageDrawable(getResources().getDrawable(mSelectedId));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(mUnselectedId));
            }
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = DisplayUtil.dp2px(mContext, 6);
            params.rightMargin = DisplayUtil.dp2px(mContext, 6);
            imageView.setLayoutParams(params);
            addView(imageView);
        }
    }

    public float getDistance() {
        return getChildAt(1).getX() - getChildAt(0).getX();
    }

    public int getSelected() {
        return mSelected;
    }

    public int getSum() {
        return mSum;
    }
}
