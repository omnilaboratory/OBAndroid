package com.omni.wallet.baselibrary.view;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * EditText小数点前后数字位数的过滤器
 */

public class DoubleInputFilter implements InputFilter {
    private static final String TAG = DoubleInputFilter.class.getSimpleName();
    // 输入框小数的位数
    private static final int DECIMAL_DIGITS = 1;
    // 小数前的位数
    private static final int DECIMAL_DIGITS_START = 6;

    private int mDotLength = DECIMAL_DIGITS;
    private int mStartLength = DECIMAL_DIGITS_START;

    public DoubleInputFilter(int mStartLength, int mDotLength) {
        this.mStartLength = mStartLength;
        this.mDotLength = mDotLength;
    }

    // source:当前输入的字符
    // start:输入字符的开始位置
    // end:输入字符的结束位置
    // dest：当前已显示的内容
    // dstart:当前光标开始位置
    // dent:当前光标结束位置
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (dest.length() == 0 && source.equals(".")) {
            return "0.";
        }
        String showStr = dest.toString();
        String[] splitArray = showStr.split("\\.");
        int dotIndex = showStr.indexOf(".");// 小数点在文字中的索引
        String startStr = splitArray[0];// 整数部分的文字
        String endStr = splitArray.length > 1 ? splitArray[1] : "";// 小数部分的文字
        //
        if (showStr.contains(".")) {// 有小数点
            // 获取小数点的位置
            if (dstart <= dotIndex && startStr.length() >= mStartLength) {// 光标在小数点之前,整数部分长度达标了
                return "";
            } else if (dstart > dotIndex && endStr.length() >= mDotLength) {// 光标在小数点之后
                return "";
            }
        } else {// 没有小数点，只判断整数
            if (startStr.length() >= mStartLength && !".".equals(source)) {// 长度达标了，并且输入的不是小数点
                return "";
            } else if (".".equals(source) && dstart < startStr.length() - 2) {// 输入的是小数点，并且位置小于整数长度-2，不允许输入
                return "";
            }
        }
        return source;
    }
}
