package com.omni.testnet.baselibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.omni.testnet.baselibrary.R;
import com.omni.testnet.baselibrary.utils.StringUtils;


/**
 * 字母检索侧边栏
 */

public class SideIndexBar extends View {
    private static final String TAG = SideIndexBar.class.getSimpleName();

    // 字母列表颜色
    int mLetterColor = 0xff666666;
    // 被选中的字母颜色
    int mSelectLetterColor = 0xff666666;
    // 字母字体大小
    float mLetterSize = 30;
    // 字母间距
    float mLetterSpace = 10;
    //是否是粗体字母
    boolean mIsBoldface;
    //是否字母是居中显示
    boolean mIsLetterCenter;
    //背景
    Drawable mBackground;
    //选中时的背景
    Drawable mSelectBackground;

    private int mWidth;//宽度
    private int mHeight;//去除padding后的高度
    private int mChoose = -1;// 选中的字母是第几个
    private Paint mPaint;//画笔0
    private TextView mTextDialog;//可以设置一个显示当前索引字母的对话框
    private String mLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";//默认字符
    private OnLetterChangedListener mLetterChangedListener;// 触摸字母改变事件

    public SideIndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public SideIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SideIndexBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context != null && attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideIndexBar, 0, 0);
            mLetterSize = a.getDimension(R.styleable.SideIndexBar_letterSize, mLetterSize);
            mLetterColor = a.getColor(R.styleable.SideIndexBar_letterColor, mLetterColor);
            mSelectLetterColor = a.getColor(R.styleable.SideIndexBar_selectLetterColor, mSelectLetterColor);
            mSelectBackground = a.getDrawable(R.styleable.SideIndexBar_selectBackground);
            mIsBoldface = a.getBoolean(R.styleable.SideIndexBar_isBoldface, mIsBoldface);
            mIsLetterCenter = a.getBoolean(R.styleable.SideIndexBar_isLetterCenter, mIsLetterCenter);
            a.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTypeface(mIsBoldface ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        mPaint.setTextSize(mLetterSize);
        mPaint.setAntiAlias(true);
        setClickable(true);
        mBackground = getBackground();
    }

    public void setLetters(String letters) {
        this.mLetters = letters;
        requestLayout();
        invalidate();
    }

    public String getLetters() {
        return mLetters;
    }

    public void setTextDialog(TextView textDialog) {
        this.mTextDialog = textDialog;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        mHeight = h - getPaddingTop() - getPaddingBottom();
        mWidth = w;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        float letterWidth = mPaint.measureText("测");
        int height = (int) (mLetters.length() * (letterWidth + mLetterSpace) + mLetterSpace);
        setMeasuredDimension(mWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!StringUtils.isEmpty(mLetters)) {
            for (int i = 0, len = mLetters.length(); i < len; i++) {
                String letter = mLetters.substring(i, i + 1);
                float letterWidth = mPaint.measureText(letter);
                mPaint.setColor(i == mChoose ? mSelectLetterColor : mLetterColor);
                // 计算（x,y），默认是该字母的左下角坐标
                float xPos = mIsLetterCenter ? (mWidth - letterWidth) / 2 : getPaddingLeft() + (mLetterSize - letterWidth) / 2;
                float yPos = mHeight / mLetters.length() * (i + 1) + getPaddingTop();
                canvas.drawText(letter, xPos, yPos, mPaint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (StringUtils.isEmpty(mLetters)) {
            return super.dispatchTouchEvent(event);
        }
        float y = event.getY();// 点击y坐标
        int oldChoose = mChoose;
        if (y < getPaddingTop() || y > mHeight + getPaddingTop()) {
            mChoose = -1;
        } else {
            // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
            mChoose = (int) ((y - getPaddingTop()) / mHeight * mLetters.length());
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mChoose = -1;//
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                setBackground(mBackground);
            default:
                if (oldChoose != mChoose && mChoose != -1 && mChoose != mLetterSize && mChoose < mLetters.length()) {
                    if (mLetterChangedListener != null) {
                        mLetterChangedListener.onChanged(mLetters.substring(mChoose, mChoose + 1), mChoose);
                    }
                    if (mTextDialog != null) {
                        mTextDialog.setText(mLetters.substring(mChoose, mChoose + 1));
                        mTextDialog.setVisibility(View.VISIBLE);
                    }
                    setBackground(mSelectBackground);
                }
        }
        invalidate();
        return super.dispatchTouchEvent(event);
    }

    //设置接口
    public void setOnLetterChangedListener(OnLetterChangedListener letterChangedListener) {
        mLetterChangedListener = letterChangedListener;
    }

    /**
     * 触摸选中的字母发生改变的接口
     */
    public interface OnLetterChangedListener {
        void onChanged(String s, int position);
    }

}
