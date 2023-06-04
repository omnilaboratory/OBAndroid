package com.omni.wallet_mainnet.baselibrary.view.editText;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 带有删除按钮的输入框
 */

public class CleanableEditText extends AppCompatEditText {
    private static final String TAG = CleanableEditText.class.getSimpleName();

    private Context mContext;
    private static final int DRAWABLE_LEFT = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_BOTTOM = 3;
    private Drawable mClearDrawable;
    private float mDrawablePadding = 5;
    private boolean mDelAlwaysVisible = false;// 有内容的时候，失去焦点之后删除按钮是否一直可见

    public CleanableEditText(Context context) {
        this(context, null);
    }

    public CleanableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
        initView();
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.CleanableEditText);
        // 获取指示器的选中Drawable
        mClearDrawable = array.getDrawable(R.styleable.CleanableEditText_delIcon);
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_delete_white);
        }
        mDrawablePadding = array.getDimension(R.styleable.CleanableEditText_delPadding, mDrawablePadding);
        mDelAlwaysVisible = array.getBoolean(R.styleable.CleanableEditText_delAlwaysShow, mDelAlwaysVisible);
        array.recycle();
    }


    private void initView() {
        setCompoundDrawablePadding((int) mDrawablePadding);
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (getGravity() == (Gravity.TOP | Gravity.START)) {
            setGravity(Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.CENTER_VERTICAL | getGravity());
        }
        // 屏蔽键盘上的回车按键
        this.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    return true;
                }
                return false;
            }
        });
    }

    public void setClearDrawable(Drawable drawable) {
        this.mClearDrawable = drawable;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mDelAlwaysVisible) {
            setClearIconVisible(text.length() > 0);
        } else {
            setClearIconVisible(hasFocus() && text.length() > 0);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!mDelAlwaysVisible) {
            setClearIconVisible(focused && length() > 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    setText("");
                    if (mCallback != null) {
                        mCallback.onClickClean();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    private void setClearIconVisible(boolean visible) {
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[DRAWABLE_LEFT],
                getCompoundDrawables()[DRAWABLE_TOP], visible ? mClearDrawable : null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
    }

    private ClickCallback mCallback;

    public void setCallback(ClickCallback callback) {
        this.mCallback = callback;
    }

    public interface ClickCallback {
        void onClickClean();
    }

}
