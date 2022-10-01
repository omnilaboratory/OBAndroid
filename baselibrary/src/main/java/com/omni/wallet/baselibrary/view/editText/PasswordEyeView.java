package com.omni.wallet.baselibrary.view.editText;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.omni.wallet.baselibrary.R;


/**
 * 带有密码可见性切换功能的EditText
 */

public class PasswordEyeView extends AppCompatImageView implements View.OnClickListener {
    private static final String TAG = PasswordEyeView.class.getSimpleName();
    private Context mContext;
    private EditText mEditText;

    // 设置关联的EditText
    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    public PasswordEyeView(Context context) {
        this(context, null);
    }

    public PasswordEyeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEyeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        // 默认密码不可见
        setSelected(false);
        // 设置默认的背景
        setImageResource(R.drawable.selector_icon_password_eye);
        // 设置点击事件
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setSelected(!isSelected());
        boolean selectedState = isSelected();
        if (mEditText != null) {
            // 切换可见状态
            if (selectedState) {// 密码可见
                mEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {// 密码隐藏
                mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // 移动光标到最末尾
            mEditText.postInvalidate();
            Spannable spanText = mEditText.getText();
            Selection.setSelection(spanText, spanText.length());
        }
    }
}
