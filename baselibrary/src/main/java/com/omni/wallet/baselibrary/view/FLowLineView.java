package com.omni.wallet.baselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.entity.FlowChart;
import com.omni.wallet.baselibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 时间节点控件
 */

public class FLowLineView extends View {
    /**
     * 圆的直径
     */
    private int mRoundSize = 20;
    /**
     * 光晕透明度
     */
    private int haloAlpha = 50;
    /**
     * 光晕的宽度
     */
    private int haloWidth = 5;
    /**
     * 节点画笔
     */
    private Paint mPaint;
    /**
     * 文字描述的画笔
     */
    private TextPaint tp;
    /**
     * 流程线的画笔
     */
    private Paint mLinePaint;
    /**
     * 当前已完成的最新节点
     */
    private int tag;
    /**
     * 需要显示的流程节点集合
     */
    private List<FlowChart> mFlowCharts = new ArrayList<>();
    /**
     * 每一个节点item（包含文字）所要占用的最小宽度即最长文字的宽度
     */
    private int mItemMaxWidth;
    /**
     * 当前控件宽度
     */
    private int mCurrViewWidth;

    /**
     * 当前控件高度
     */
    private int mCurrViewHeight = 0;
    /**
     * 存放每个item最长的文字宽度
     */
    private List<Integer> mItemMaxTextViewWidthList = new ArrayList<>();
    /**
     * 布局是否超出屏幕
     */
    private boolean full;
    /**
     * 流程线的高度
     */
    private int lineHeight = 5;
    /**
     * 文字大小
     */
    private int textSize = 33;

    /**
     * 已完成节点颜色
     */
    private int doneColor;
    /**
     * 进行中节点颜色
     */
    private int doingColor;
    /**
     * 未开始节点颜色
     */
    private int todoColor;
    /**
     * 行间距
     */
    private int rowRpacing = 30;

    private boolean doubleBottom;


    public FLowLineView(Context context) {
        super(context, null, 0);
    }

    public FLowLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initAttr(context, attrs);
        initView();
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FLowLineView);//下面是在读取布局里面设置的属性值
        doneColor = typedArray.getColor(R.styleable.FLowLineView_doneColor, Color.parseColor("#5DBF19"));
        doingColor = typedArray.getColor(R.styleable.FLowLineView_doingColor, Color.parseColor("#FD6067"));
        todoColor = typedArray.getColor(R.styleable.FLowLineView_todongColor, Color.parseColor("#c1c1c1"));
        textSize = typedArray.getDimensionPixelSize(R.styleable.FLowLineView_mTextSize, textSize);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.FLowLineView_lineHeight, lineHeight);
        haloWidth = typedArray.getDimensionPixelSize(R.styleable.FLowLineView_haloWidth, haloWidth);
        haloAlpha = typedArray.getInt(R.styleable.FLowLineView_haloAlpha, haloAlpha);
        rowRpacing = typedArray.getDimensionPixelSize(R.styleable.FLowLineView_rowRpacing, rowRpacing);
    }


    private void initView() {
        mPaint = new Paint();
        mLinePaint = new Paint();
        tp = new TextPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //圆的半径
        int radius = mRoundSize / 2;
        int startY = rowRpacing + textSize / 2;
        for (int a = 0; a < mFlowCharts.size(); a++) {
            tp.setTextSize(textSize);
            tp.setTypeface(Typeface.SANS_SERIF);
            //文字字体加粗
            tp.setFakeBoldText(false);
            //笔宽5像素
            mLinePaint.setStrokeWidth(lineHeight);
            //下面开始设置画笔的颜色这些
            if (a <= tag) {
                mPaint.setColor(doneColor);
                tp.setColor(doneColor);
                mLinePaint.setColor(doneColor);
            } else if (a == tag + 1) {
                mPaint.setColor(doingColor);
                tp.setColor(doingColor);
                mLinePaint.setColor(todoColor);
            } else {
                mPaint.setColor(todoColor);
                tp.setColor(todoColor);
                mLinePaint.setColor(todoColor);
            } // 计算文字的宽度
            int topTextWidth = getTextWidth(tp, StringUtils.cleanString(mFlowCharts.get(a).getTopName()));
            int bottomTextWidth = getTextWidth(tp, StringUtils.cleanString(mFlowCharts.get(a).getBottomName()));
            int timeTextX = getTextWidth(tp, StringUtils.cleanString(mFlowCharts.get(a).getTime()));

            int maxTextWidth;
            int topX;
            int bottomX;
            int timeX;
            int roundCenterX;

            if (full) {
                maxTextWidth = mItemMaxWidth;
            } else {
                maxTextWidth = Math.max(topTextWidth, bottomTextWidth);
                if (doubleBottom) {
                    maxTextWidth = Math.max(maxTextWidth, timeTextX);
                }
                maxTextWidth += 20;

            }
            roundCenterX = mItemMaxWidth * a + maxTextWidth / 2;
            topX = roundCenterX - topTextWidth / 2;
            bottomX = roundCenterX - bottomTextWidth / 2;
            timeX = roundCenterX - timeTextX / 2;
            // 绘制文字
            canvas.drawText(StringUtils.cleanString(mFlowCharts.get(a).getTopName()), topX, startY, tp);
            canvas.drawText(StringUtils.cleanString(mFlowCharts.get(a).getBottomName()), bottomX, startY + rowRpacing * 2 + mRoundSize + haloWidth * 2, tp);
            if (doubleBottom) {//不显示最后一行文字
                canvas.drawText(StringUtils.cleanString(mFlowCharts.get(a).getTime()), timeX, startY + rowRpacing * 3 + textSize / 2 + mRoundSize + haloWidth * 2, tp);
            }
            if (a < mFlowCharts.size() - 1) { // 绘制线
                canvas.drawLine(roundCenterX, startY + rowRpacing, roundCenterX + mItemMaxWidth, startY + rowRpacing, mLinePaint);
            } // 画圆
            RectF rf2 = new RectF(roundCenterX - radius, startY + rowRpacing - radius, roundCenterX + 10, startY + rowRpacing + radius);
            canvas.drawOval(rf2, mPaint);
            if (a == tag + 1) {
                mPaint.setAlpha(haloAlpha);
                RectF haloRectF = new RectF(roundCenterX - (radius + haloWidth), startY + rowRpacing - radius - haloWidth, roundCenterX + (radius + haloWidth), startY + rowRpacing + radius + haloWidth);
                canvas.drawOval(haloRectF, mPaint);
            }
            mPaint.reset();
            tp.reset();
        }
    }

    /**
     * 计算文字宽度
     */
    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public void setFlowCharts(List<FlowChart> flowCharts) {
        if (flowCharts == null) {
            flowCharts = new ArrayList<>();
        }
        mFlowCharts = flowCharts;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        if (width < getViewMinWidth()) {
            width = getViewMinWidth() + 20 * (mFlowCharts.size() + 1);
            mItemMaxWidth = width / mFlowCharts.size();
            full = true;
        } else {
            mItemMaxWidth = (width - 20 - getFlowMinWidth(mFlowCharts.get(0)) / 2 - getFlowMinWidth(mFlowCharts.get(mFlowCharts.size() - 1)) / 2) / (mFlowCharts.size() - 1);
            full = false;
        }
        mCurrViewWidth = width;
        if (doubleBottom) {//不现实最后一行文字就不参与计算 mCurrViewHeight = rowRpacing + textSize / 2 + rowRpacing * 3 + textSize / 2 + mRoundSize + haloWidth * 2+rowRpacing;
        } else {
            mCurrViewHeight = rowRpacing + textSize / 2 + rowRpacing * 2 + mRoundSize + haloWidth * 2 + rowRpacing;
        }
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    /**
     * 获取所有文字占用的最小宽度
     *
     * @return
     */
    public int getViewMinWidth() {
        int minWidth;

        if (mFlowCharts != null && !mFlowCharts.isEmpty()) {
            for (FlowChart chart : mFlowCharts) { // 计算文字的宽度
                mItemMaxTextViewWidthList.add(getFlowMinWidth(chart));
            }
        } else {
        }
        int max = Collections.max(mItemMaxTextViewWidthList);
        minWidth = (max + 20) * mFlowCharts.size();
        return minWidth;
    }

    /**
     * 计算每个item的最长文字占用宽度作为item的最小宽度
     *
     * @param chart
     * @return
     */
    public int getFlowMinWidth(FlowChart chart) {
        tp = new TextPaint();
        tp.setTextSize(textSize);
        tp.setTypeface(Typeface.SANS_SERIF);
        //文字字体加粗
        tp.setFakeBoldText(false);
        int topTextWidth = getTextWidth(tp, StringUtils.cleanString(chart.getTopName()));
        int bottomTextWidth = getTextWidth(tp, StringUtils.cleanString(chart.getBottomName()));
        int timeTextX = getTextWidth(tp, StringUtils.cleanString(chart.getTime()));
        int maxTextWidth = Math.max(topTextWidth, bottomTextWidth);
        if (doubleBottom) {
            maxTextWidth = Math.max(maxTextWidth, timeTextX);
        }
        return maxTextWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCurrViewWidth, mCurrViewHeight);
    }

    public void setDoingColor(int doingColor) {
        this.doingColor = doingColor;
    }

    public void setDoneColor(int doneColor) {
        this.doneColor = doneColor;
    }

    public void setTodoColor(int todoColor) {
        this.todoColor = todoColor;
    }

    public void setHaloAlpha(int haloAlpha) {
        this.haloAlpha = haloAlpha;
    }

    public void setHaloWidth(int haloWidth) {
        this.haloWidth = haloWidth;
    }

    public void setRoundSize(int roundSize) {
        mRoundSize = roundSize;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setDoubleBottom(boolean doubleBottom) {
        this.doubleBottom = doubleBottom;
    }
}
