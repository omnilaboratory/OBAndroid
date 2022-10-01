package com.omni.wallet.baselibrary.view.LabelLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.entity.SelectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据内容长度动态调整标签数量的布局
 */

public class CustomLabelLayout extends ViewGroup {
    private static final String TAG = CustomLabelLayout.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<SelectEntity> mLabelsList = new ArrayList<>();// 显示的数据集合
    private int mLabelSpace; //标签间距
    private int mLabelRowSpace;// 标签行距
    private int mChildLayout;// 子控件布局ID
    private LabelChildViewCreator mChildViewCreator;// 子控件的ViewCreator
    private Map<String, TextView> mChildViewMap = new HashMap<>();// 缓存子控件布局
    private boolean mIsMultiChecked = true;// 是否支持多选
    private int mLastSelectedIndex = -1;// 单选时使用的上次选中的索引
    private boolean mChildClickable = true;// 子条目是否可以点击

    public CustomLabelLayout(Context context) {
        this(context, null);
    }

    public CustomLabelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInflater = LayoutInflater.from(context);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLabelLayout);
        mLabelSpace = typedArray.getDimensionPixelSize(R.styleable.CustomLabelLayout_labelSpace, 9);
        mLabelRowSpace = typedArray.getDimensionPixelSize(R.styleable.CustomLabelLayout_rowSpace, 10);
        mChildLayout = typedArray.getResourceId(R.styleable.CustomLabelLayout_childLayout, R.layout.layout_item_label_child);
        mChildClickable = typedArray.getBoolean(R.styleable.CustomLabelLayout_childClickable, mChildClickable);
//        LogUtils.e(TAG, "mLabelRowSpace=" + mLabelRowSpace + "   mLabelSpace=" + mLabelSpace);
        typedArray.recycle(); //回收
    }

    public void setChildViewCreator(LabelChildViewCreator viewCreator) {
        this.mChildViewCreator = viewCreator;
    }

    /**
     * 获取选中标签
     */
    public List<SelectEntity> getSelectedLabels() {
        List<SelectEntity> resultList = new ArrayList<>();
        for (SelectEntity entity : mLabelsList) {
            if (entity.isSelected()) {
                resultList.add(entity);
            }
        }
        return resultList;
    }

    public List<String> getSelectedStrLabels() {
        List<String> resultList = new ArrayList<>();
        for (SelectEntity entity : mLabelsList) {
            if (entity.isSelected()) {
                resultList.add(entity.getTitle());
            }
        }
        return resultList;
    }

    /**
     * 是否支持多选
     */
    public void setMultiChecked(boolean isMultiChecked) {
        this.mIsMultiChecked = isMultiChecked;
    }

    /**
     * 添加标签
     */
    public void setLabelsStr(List<String> labels) {
        if (labels != null && labels.size() > 0) {
            List<SelectEntity> labelList = new ArrayList<>();
            for (String str : labels) {
                SelectEntity selectEntity = new SelectEntity();
                selectEntity.setId(str);
                selectEntity.setTitle(str);
                labelList.add(selectEntity);
            }
            setLabels(labelList);
        } else {
            removeAllViews();
        }
    }


    /**
     * 添加标签
     */
    public void setLabels(List<SelectEntity> labels) {
        this.mLabelsList = labels;
        removeAllViews();
        if (mLabelsList != null && mLabelsList.size() > 0) {
            for (int i = 0, length = mLabelsList.size(); i < length; i++) {
                SelectEntity entity = mLabelsList.get(i);
                View labelView = getChildView(i, entity); //创建标签布局
                if (labelView == null) {
                    continue;
                }
                labelView.setSelected(entity.isSelected());//设置选中效果
                if (mChildClickable) { //点击事件，不可点击的时候不设置
                    labelView.setOnClickListener(new MyClickListener(mLabelsList.indexOf(entity)));
                }
                addView(labelView);  //将标签添加到容器中
            }
        }
    }

    /**
     * 创建标签布局（缓存中没有才重新创建）
     */
    private View getChildView(int position, SelectEntity entity) {
        if (entity == null) {
            return null;
        }
        if (mChildViewCreator != null) {
            return mChildViewCreator.getView(position, entity);
        }
        if (mChildLayout != 0) {
            View childView = mInflater.inflate(mChildLayout, null, false);
            if (childView instanceof TextView) {
                ((TextView) childView).setText(entity.getTitle());// 设置标题
            }
            return childView;
        }
        return null;
    }

    /**
     * 标签点击监听
     */
    private class MyClickListener implements OnClickListener {
        private SelectEntity mEntity;
        private int mIndex;

        MyClickListener(int index) {
            this.mIndex = index;
            this.mEntity = mLabelsList.get(index);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onChange();
            }
            view.setSelected(!view.isSelected());// 切换选中状态
            mEntity.setSelected(view.isSelected());// 更新数据源
            // 点击事件回调回去，切换特殊的选中状态
            if (mChildViewCreator != null) {
                mChildViewCreator.onChildClick(view, mIndex, mEntity);
            }
            if (!mIsMultiChecked) {// 单选，更新上次选中的数据的状态
                if (mLastSelectedIndex == -1) {// 首次点击
                    mLastSelectedIndex = mIndex;
                } else {// 非首次点击
                    // 注意上边已经反转了该View的选中状态
                    if (view.isSelected()) {// 点击非选中条目
                        getChildAt(mLastSelectedIndex).setSelected(false);// 将上次选中的置为false
                        mLabelsList.get(mLastSelectedIndex).setSelected(false);// 数据源赋值
                        mLastSelectedIndex = mIndex;
                    } else {// 选中条目点击
                        mLastSelectedIndex = -1;
                    }
                }
            }
        }
    }

    /**
     * 刷新ChildView的选中状态
     */
    public void refreshSelectedState(List<SelectEntity> selectedList) {
        if (selectedList == null) {
            selectedList = new ArrayList<>();
        }
        if (!mIsMultiChecked) {// 单选的时候，需要更新上次选中的索引
            if (selectedList.size() == 0) {
                mLastSelectedIndex = -1;
            } else {
                mLastSelectedIndex = mLabelsList.indexOf(selectedList.get(0));
            }
        }
        // 更细控件选中状态
        for (SelectEntity entity : mLabelsList) {
            entity.setSelected(selectedList.contains(entity));
            getChildAt(mLabelsList.indexOf(entity)).setSelected(entity.isSelected());
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //为所有的标签childView计算宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //获取高的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //建议的高度
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //布局的宽度采用建议宽度（match_parent或者size），如果设置wrap_content也是match_parent的效果
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            //如果高度模式为EXACTLY（match_parent或者size），则使用建议高度
            height = heightSize;
        } else {
            //其他情况下（AT_MOST、UNSPECIFIED）需要计算计算高度
            int childCount = getChildCount();
            if (childCount <= 0) {
                height = 0;   //没有标签时，高度为0
            } else {
                int row = 1;  // 标签行数
                int widthSpace = width;// 当前行右侧剩余的宽度
                for (int i = 0; i < childCount; i++) {
                    View view = getChildAt(i);
                    //获取标签宽度
                    int childW = view.getMeasuredWidth();
//                    LogUtils.e(TAG, "\n\n");
//                    LogUtils.e(TAG, "标签宽度:" + childW + "第" + row + "行剩余宽度：" + widthSpace);
//                    LogUtils.e(TAG, "\n\n");
                    if (widthSpace - mLabelSpace >= childW) { //如果剩余的宽度减去一个间距大于此标签的宽度，那就将此标签放到本行
                        widthSpace -= childW;
                    } else { //如果剩余的宽度不能摆放此标签，那就将此标签放入下一行
                        row++;    //增加一行
                        widthSpace = width - childW;
                    }
                    //减去标签左右间距
                    widthSpace -= mLabelSpace;
//                    LogUtils.e(TAG, "标签" + (i + 1) + "放在第" + row + "行，放完之后减去边距剩余宽度" + widthSpace);
                }
                //由于每个标签的高度是相同的，所以直接获取第一个标签的高度即可
                int childH = getChildAt(0).getMeasuredHeight();
                //最终布局的高度=标签高度*行数+行距*(行数-1)
                height = (childH * row) + mLabelRowSpace * (row - 1);
//                LogUtils.e(TAG, "总高度:" + height + " 行数：" + row + "  标签高度：" + childH);
//                LogUtils.e(TAG, "\n\n\n");
            }
        }
        //设置测量宽度和测量高度
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int row = 0;
        int right = 0;   // 标签相对于布局的右侧位置
        int bottom;       // 标签相对于布局的底部位置
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            //右侧位置=本行已经占有的位置+当前标签的宽度
            right += childW;
            //底部位置=已经摆放的行数*（标签高度+行距）+当前标签高度
            bottom = row * (childH + mLabelRowSpace) + childH;
            // 如果右侧位置已经超出布局右边缘，跳到下一行
            // 注意这里不能直接用r判断，r是相对屏幕的，需要减去leftMargin就是l
            if (right > (r - l - mLabelSpace)) {
                row++;
                right = childW;
                bottom = row * (childH + mLabelRowSpace) + childH;
            }
//            LogUtils.e(TAG, "\n\n");
//            LogUtils.e(TAG, "left = " + (right - childW) + " top = " + (bottom - childH) + " right = " + right + " bottom = " + bottom);
//            LogUtils.e(TAG, "\n\n");
            childView.layout(right - childW, bottom - childH, right, bottom);
            right += mLabelSpace;
        }
    }

    private ContentChangeListener mListener;

    public void setListener(ContentChangeListener listener) {
        this.mListener = listener;
    }

    public interface ContentChangeListener {
        void onChange();
    }

    /**
     * 子控件建造器，向外提供建造规则
     */
    public static abstract class LabelChildViewCreator {
        public abstract View getView(int position, SelectEntity entity);

        protected void onChildClick(View view, int index, SelectEntity entity) {

        }
    }


}
