package com.omni.testnet.baselibrary.view.datePicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.omni.testnet.baselibrary.R;


public class NumberPicker extends View {
    private static final String TAG = NumberPicker.class.getSimpleName();
    /**
     * View width
     */
    private int mWidth;

    /**
     * View height
     */
    private int mHeight;

    private TextPaint mTextPaint;
    private TextPaint mFlagTextPaint;

    /**
     * Paint used for drawing lines besides the number in the middle of the view
     */
    private Paint mDividerLinePaint;

    private Paint mShaderPaintTop;
    private Paint mShaderPaintBottom;

    private Rect mTextBounds;
    private NumberHolder[] mTextYAxisArray;
    private int mStartYPos;
    private int mEndYPos;
    private boolean mIsLoop = false;

    /**
     * The coefficient by which to adjust (divide) the max fling velocity.
     */
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;

    private static final int DEFAULT_TEXT_COLOR = Color.rgb(51, 51, 51);
    private static final int DEFAULT_FLAG_TEXT_COLOR = Color.rgb(51, 51, 51);
    private static final int DEFAULT_BACKGROUND_COLOR = Color.rgb(255, 255, 255);
    private static final int DEFAULT_LINE_COLOR = Color.rgb(245, 245, 245);
    private static final int DEFAULT_LINE_SIZE = 1;

    private static final float DEFAULT_TEXT_SIZE = 20;
    private static final float DEFAULT_FLAG_TEXT_SIZE = 20;

    private static final int DEFAULT_VERTICAL_SPACING = 10;

    private static final int DEFAULT_LINES = 3;

    private int mTextColor;
    private float mTextSize;
    private int mStartNumber;
    private int mEndNumber;
    private int mCurrentNumber;

    /**
     * Space between bottom of the number above and the top of the number below.
     */
    private int mVerticalSpacing;

    /**
     * Text to be drawn at the top-right of the highlight number
     */
    private String mFlagText;

    private int mLineColor;

    private float mLineSize;

    private int mFlagTextColor;

    private float mFlagTextSize;

    /**
     * Number array form {@link #mStartNumber} to {@link #mEndNumber}
     */
    private int[] mNumberArray;

    /**
     * The index of the highlight number in the {@link #mNumberArray}
     */
    private int mCurrNumIndex;

    private int mTouchSlop;

    private RectF mHighLightRect;

    private Rect mTextBoundsFlag;

    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private int mTouchAction = MotionEvent.ACTION_CANCEL;

    private Scroller mFlingScroller;
    private Scroller mAdjustScroller;

    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    private int mBackgroundColor;

    private int mStartY;
    private int mCurrY;
    private int mOffsetY;
    private int mSpacing;
    private boolean mCanScroll;
    private VelocityTracker mVelocityTracker;

    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;

    /**
     * How many number will be displayed in the view
     */
    private int mRowNumber;

    private Sound mSound;

    private boolean mSoundEffectEnable = true;

    private float mDensity;

    private String[] mTextArray;

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDensity = getResources().getDisplayMetrics().density;
        readAttrs(context, attrs, defStyleAttr);
        init();
    }

    private void readAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.NumberPicker_textColor, DEFAULT_TEXT_COLOR);
        mLineColor = a.getColor(R.styleable.NumberPicker_lineColor, DEFAULT_LINE_COLOR);
        mLineSize = a.getDimension(R.styleable.NumberPicker_lineSize, DEFAULT_LINE_SIZE * mDensity);
        mTextSize = a.getDimension(R.styleable.NumberPicker_textSize, DEFAULT_TEXT_SIZE * mDensity);
        mStartNumber = a.getInteger(R.styleable.NumberPicker_startNumber, 0);
        mEndNumber = a.getInteger(R.styleable.NumberPicker_endNumber, 0);
        mCurrentNumber = a.getInteger(R.styleable.NumberPicker_currentNumber, 0);
        mVerticalSpacing = (int) a.getDimension(R.styleable.NumberPicker_verticalSpacing, DEFAULT_VERTICAL_SPACING * mDensity);
        mFlagText = a.getString(R.styleable.NumberPicker_flagText);
        mFlagTextColor = a.getColor(R.styleable.NumberPicker_flagTextColor, DEFAULT_FLAG_TEXT_COLOR);
        mFlagTextSize = a.getDimension(R.styleable.NumberPicker_flagTextSize, DEFAULT_FLAG_TEXT_SIZE * mDensity);
        mBackgroundColor = a.getColor(R.styleable.NumberPicker_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        mRowNumber = a.getInteger(R.styleable.NumberPicker_rowNumber, DEFAULT_LINES);
        a.recycle();
    }

    private void init() {

        verifyNumber();
        initPaint();
        initRect();
        measureText();

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        mFlingScroller = new Scroller(getContext(), null);
        mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
    }

    private void verifyNumber() {
        if (mStartNumber < 0 || mEndNumber < 0) {
            throw new IllegalArgumentException("number can not be negative");
        }
        if (mStartNumber > mEndNumber) {
            mEndNumber = mStartNumber;
        }
        if (mCurrentNumber < mStartNumber) {
            mCurrentNumber = mStartNumber;
        }
        if (mCurrentNumber > mEndNumber) {
            mCurrentNumber = mEndNumber;
        }

        mNumberArray = new int[mEndNumber - mStartNumber + 1];
        for (int i = 0; i < mNumberArray.length; i++) {
            mNumberArray[i] = mStartNumber + i;
        }

        mCurrNumIndex = mCurrentNumber - mStartNumber;

        mTextYAxisArray = new NumberHolder[mRowNumber + 4];
    }

    private void initPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setFlags(TextPaint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);

        mFlagTextPaint = new TextPaint();
        mFlagTextPaint.setTextSize(mFlagTextSize);
        mFlagTextPaint.setColor(mFlagTextColor);
        mFlagTextPaint.setFlags(TextPaint.ANTI_ALIAS_FLAG);
        mFlagTextPaint.setTextAlign(Align.LEFT);

        mDividerLinePaint = new Paint();
        mDividerLinePaint.setColor(mLineColor);
        mDividerLinePaint.setStyle(Paint.Style.STROKE);
        mDividerLinePaint.setStrokeWidth(mLineSize);

        mShaderPaintTop = new Paint();
        mShaderPaintBottom = new Paint();
    }

    private void initRect() {
        mTextBounds = new Rect();
        mTextBoundsFlag = new Rect();
    }

    private void measureText() {

        /*
         * To make sure the text bounds of the different number are exactly the same,
         * we turn the number such as "2014" into "0000".
         */
        String text = String.valueOf(mEndNumber);
        int length = text.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("0");
        }
        text = builder.toString();

        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);

        if (mFlagText != null) {
            mFlagTextPaint.getTextBounds(mFlagText, 0, mFlagText.length(), mTextBoundsFlag);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // Background color
        canvas.drawColor(mBackgroundColor);

        // Lines
        canvas.drawLine(0, mHighLightRect.top, mWidth, mHighLightRect.top, mDividerLinePaint);
        canvas.drawLine(0, mHighLightRect.bottom, mWidth, mHighLightRect.bottom, mDividerLinePaint);

//        // Flag text
//        if (mFlagText != null) {
//            int x = (mWidth + mTextBounds.width() + 6) / 2;
//            canvas.drawText(mFlagText, x, mHeight / 2, mFlagTextPaint);
//        }

        // Draw numbers
        for (int i = 0; i < mTextYAxisArray.length; i++) {
            if (mTextYAxisArray[i].mmIndex >= 0 && mTextYAxisArray[i].mmIndex <= mEndNumber - mStartNumber) {
                String text;
                if (mTextArray != null) {
                    text = mTextArray[mTextYAxisArray[i].mmIndex];
                } else {
                    text = String.valueOf(mNumberArray[mTextYAxisArray[i].mmIndex]);
                }
//                LogUtils.e(TAG, "===========》" + text + mFlagText);
                canvas.drawText(
                        text + mFlagText,
                        mWidth / 2,
                        mTextYAxisArray[i].mmPos + mTextBounds.height() / 2,
                        mTextPaint);
            }
        }

        // Draw shader
        canvas.drawRect(0, 0, mWidth, mHighLightRect.top, mShaderPaintTop);
        canvas.drawRect(0, mHighLightRect.bottom, mWidth, mHeight, mShaderPaintBottom);

        // Scroll the number to the position where exactly they should be.
        // Only do this when the finger no longer touch the screen and the fling action is finished.
        if (MotionEvent.ACTION_UP == mTouchAction && mFlingScroller.isFinished()) {
            adjustYPosition();
        }
    }

    /**
     * Scroll the number to the position where exactly they should be
     */
    private void adjustYPosition() {
        if (mAdjustScroller.isFinished()) {
            mStartY = 0;
            int offsetIndex = Math.round((float) (mTextYAxisArray[0].mmPos - mStartYPos) / (float) mSpacing);
            int position = mStartYPos + offsetIndex * mSpacing;
            int dy = position - mTextYAxisArray[0].mmPos;
            if (dy != 0) {
                mAdjustScroller.startScroll(0, 0, 0, dy, 300);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.  
            mWidth = widthSize;
        } else {
            mWidth = mTextBounds.width() + getPaddingLeft() + getPaddingRight() + mTextBoundsFlag.width() + 6;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = mRowNumber * mTextBounds.height() + (mRowNumber - 1) * mVerticalSpacing + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(mWidth, mHeight);
        
        /*
         * Do some initialization work when the view got a size
         */
        if (null == mHighLightRect) {
            mHighLightRect = new RectF();
            mHighLightRect.left = 0;
            mHighLightRect.right = mWidth;
            mHighLightRect.top = (mHeight - mTextBounds.height() - mVerticalSpacing) / 2;
            mHighLightRect.bottom = (mHeight + mTextBounds.height() + mVerticalSpacing) / 2;

            Shader topShader = new LinearGradient(0, 0, 0, mHighLightRect.top, new int[]{
                    mBackgroundColor & 0xDFFFFFFF,
                    mBackgroundColor & 0xCFFFFFFF,
                    mBackgroundColor & 0x00FFFFFF},
                    null, Shader.TileMode.CLAMP);

            Shader bottomShader = new LinearGradient(0, mHighLightRect.bottom, 0, mHeight, new int[]{
                    mBackgroundColor & 0x00FFFFFF,
                    mBackgroundColor & 0xCFFFFFFF,
                    mBackgroundColor & 0xDFFFFFFF},
                    null, Shader.TileMode.CLAMP);
            mShaderPaintTop.setShader(topShader);
            mShaderPaintBottom.setShader(bottomShader);
            mSpacing = mVerticalSpacing + mTextBounds.height();
            mStartYPos = mHeight / 2 - 3 * mSpacing;
            mEndYPos = mHeight / 2 + 3 * mSpacing;

            initTextYAxisArray();

        }
    }

    private void initTextYAxisArray() {
        for (int i = 0; i < mTextYAxisArray.length; i++) {
            NumberHolder textYAxis = new NumberHolder(mCurrNumIndex - 3 + i, mStartYPos + i * mSpacing);
            if (textYAxis.mmIndex > mNumberArray.length - 1) {
                textYAxis.mmIndex -= mNumberArray.length;
            } else if (textYAxis.mmIndex < 0) {
                textYAxis.mmIndex += mNumberArray.length;
            }
//            for (int k = 0; k < mNumberArray.length; k++) {
//                LogUtils.e(TAG, "=====mNumberArray======>" + mNumberArray[k]);
//            }
//            LogUtils.e(TAG, "====textYAxis=====>" + textYAxis.mmIndex + "||" + textYAxis.mmPos);
            mTextYAxisArray[i] = textYAxis;
//            for (int k = 0; k < mTextYAxisArray.length; k++) {
//                LogUtils.e(TAG, "======mTextYAxisArray=========>" + mTextYAxisArray[k]);
//            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = MotionEventCompat.getActionMasked(event);
        mTouchAction = action;
        if (MotionEvent.ACTION_DOWN == action) {
            getParent().requestDisallowInterceptTouchEvent(true);
            mStartY = (int) event.getY();
            if (!mFlingScroller.isFinished() || !mAdjustScroller.isFinished()) {
                mFlingScroller.forceFinished(true);
                mAdjustScroller.forceFinished(true);
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            }
        } else if (MotionEvent.ACTION_MOVE == action) {
            mCurrY = (int) event.getY();
            mOffsetY = mCurrY - mStartY;

            if (!mCanScroll && Math.abs(mOffsetY) < mTouchSlop) {
                return false;
            } else {
                mCanScroll = true;
                if (mOffsetY > mTouchSlop) {
                    mOffsetY -= mTouchSlop;
                } else if (mOffsetY < -mTouchSlop) {
                    mOffsetY += mTouchSlop;
                }
            }

            mStartY = mCurrY;

            computeYPos(mOffsetY);

            onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            invalidate();
        } else if (MotionEvent.ACTION_UP == action) {
            mCanScroll = false;

            VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
            int initialVelocity = (int) velocityTracker.getYVelocity();

            if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                fling(initialVelocity);
                onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
            } else {
                adjustYPosition();
                invalidate();
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        return true;
    }

    @Override
    public void computeScroll() {

        Scroller scroller = mFlingScroller;
        if (scroller.isFinished()) {
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
            scroller = mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }

        scroller.computeScrollOffset();

        mCurrY = scroller.getCurrY();
        mOffsetY = mCurrY - mStartY;

        computeYPos(mOffsetY);

        invalidate();
        mStartY = mCurrY;
    }

    /**
     * Make {@link #mTextYAxisArray} to be a circular array
     */
    private void computeYPos(int offectY) {
        for (int i = 0; i < mTextYAxisArray.length; i++) {

            mTextYAxisArray[i].mmPos += offectY;
            if (mTextYAxisArray[i].mmPos >= mEndYPos + mSpacing) {
                mTextYAxisArray[i].mmPos -= (mRowNumber + 2) * mSpacing;
                mTextYAxisArray[i].mmIndex -= (mRowNumber + 2);
                while (mTextYAxisArray[i].mmIndex < 0) {
                    mTextYAxisArray[i].mmIndex += mNumberArray.length;
                }
            }
            if (mTextYAxisArray[i].mmPos <= mStartYPos - mSpacing) {
                mTextYAxisArray[i].mmPos += (mRowNumber + 2) * mSpacing;
                mTextYAxisArray[i].mmIndex += (mRowNumber + 2);
                while (mTextYAxisArray[i].mmIndex > mNumberArray.length - 1) {
                    mTextYAxisArray[i].mmIndex -= mNumberArray.length;
                }
            }

            if (Math.abs(mTextYAxisArray[i].mmPos - mHeight / 2) < mSpacing / 4) {
                mCurrNumIndex = mTextYAxisArray[i].mmIndex;
                int oldNumber = mCurrentNumber;
                mCurrentNumber = mNumberArray[mCurrNumIndex];
                if (oldNumber != mCurrentNumber) {
                    if (mOnValueChangeListener != null) {
                        mOnValueChangeListener.onValueChange(this, oldNumber, mCurrentNumber);
                    }
                    // Play a sound effect
                    if (mSound != null && mSoundEffectEnable) {
                        mSound.playSoundEffect();
                    }
                }
            }
        }
    }

    private void fling(int startYVelocity) {
        int maxY = 0;
        if (startYVelocity > 0) {
            maxY = (int) (mTextSize * 10);
            mStartY = 0;
            mFlingScroller.fling(0, 0, 0, startYVelocity, 0, 0, 0, maxY);
        } else if (startYVelocity < 0) {
            maxY = (int) (mTextSize * 10);
            mStartY = maxY;
            mFlingScroller.fling(0, maxY, 0, startYVelocity, 0, 0, 0, maxY);
        }
        invalidate();
    }


    class NumberHolder {

        /**
         * Array index of the number in {@link #mTextYAxisArray}
         */
        public int mmIndex;

        /**
         * Position of the number in the view
         */
        public int mmPos;

        public NumberHolder(int index, int position) {
            mmIndex = index;
            mmPos = position;
        }

        @Override
        public String toString() {
            return "{" +
                    "mmIndex=" + mmIndex +
                    ", mmPos=" + mmPos +
                    '}';
        }
    }

    public NumberPicker setStartNumber(int startNumber) {
        mStartNumber = startNumber;
        verifyNumber();
        initTextYAxisArray();
        invalidate();
        return this;
    }

    public NumberPicker setEndNumber(int endNumber) {
        mEndNumber = endNumber;
        verifyNumber();
        initTextYAxisArray();
        invalidate();
        return this;
    }

    public NumberPicker setCurrentNumber(int currentNumber) {
        mCurrentNumber = currentNumber;
        verifyNumber();
        initTextYAxisArray();
        invalidate();
        return this;
    }

    public int getCurrentNumber() {
        return mCurrentNumber;
    }

    public NumberPicker setRowNumber(int rowNumber) {
        mRowNumber = rowNumber;
        verifyNumber();
        initTextYAxisArray();
        invalidate();
        return this;
    }

    public NumberPicker setTextColor(int color) {
        mTextColor = color;
        initPaint();
        invalidate();
        return this;
    }

    public NumberPicker setFlagTextColor(int color) {
        mFlagTextColor = color;
        initPaint();
        invalidate();
        return this;
    }

    public NumberPicker setLineColor(int color) {
        this.mLineColor = color;
        initPaint();
        invalidate();
        return this;
    }

    public NumberPicker setLineColor(float size) {
        this.mLineSize = size;
        init();
        invalidate();
        return this;
    }

    public NumberPicker setTextSize(float textSize) {
        mTextSize = textSize;
        init();
        invalidate();
        return this;
    }

    public NumberPicker setFlagTextSize(float textSize) {
        mFlagTextSize = textSize;
        init();
        invalidate();
        return this;
    }

    public NumberPicker setBackground(int color) {
        mBackgroundColor = color;
        invalidate();
        return this;
    }

    /**
     * Interface to listen for changes of the current value.
     */
    public interface OnValueChangeListener {

        /**
         * Called upon a change of the current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        void onValueChange(NumberPicker picker, int oldVal, int newVal);
    }

    /**
     * Interface to listen for the picker scroll state.
     */
    public interface OnScrollListener {
        /**
         * The view is not scrolling.
         */
        int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen.
         */
        int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and performed a fling.
         */
        int SCROLL_STATE_FLING = 2;

        /**
         * Callback invoked while the number picker scroll state has changed.
         *
         * @param picker      The view whose scroll state is being reported.
         * @param scrollState The current scroll state. One of
         *                    {@link #SCROLL_STATE_IDLE},
         *                    {@link #SCROLL_STATE_TOUCH_SCROLL} or
         *                    {@link #SCROLL_STATE_IDLE}.
         */
        void onScrollStateChange(NumberPicker picker, int scrollState);
    }

    public NumberPicker setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
        return this;
    }

    public NumberPicker setOnValueChangeListener(OnValueChangeListener l) {
        mOnValueChangeListener = l;
        return this;
    }

    /**
     * Handles transition to a given <code>scrollState</code>
     */
    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    public NumberPicker setSoundEffect(Sound sound) {
        mSound = sound;
        return this;
    }

    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        super.setSoundEffectsEnabled(soundEffectsEnabled);
        mSoundEffectEnable = soundEffectsEnabled;
    }

    /**
     * Display custom text array instead of numbers
     *
     * @param textArray
     */
    public NumberPicker setCustomTextArray(String[] textArray) {
        mTextArray = textArray;
        invalidate();
        return this;
    }

}
