package com.omni.wallet.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.util.regex.Pattern;

/**
 * 汉: EditText文本监测工具
 * En: DecimalInputTextWatcher
 * author: guoyalei
 * date: 2023/4/10
 */
public class DecimalInputTextWatcher implements TextWatcher {
    private Pattern mPattern;

    /**
     * 不限制整数位数和小数位数
     * Unlimited integer and decimal places
     */
    public DecimalInputTextWatcher() {
    }

    /**
     * 限制整数位数或着限制小数位数
     * Limit the number of integers or decimals
     *
     * @param type   限制类型(Limit type)
     * @param number 限制位数(Limit number of digits)
     */
    public DecimalInputTextWatcher(Type type, int number) {
        if (type == Type.decimal) {
            mPattern = Pattern.compile("^[0-9]+(\\.[0-9]{0," + number + "})?$");
        } else if (type == Type.integer) {
            mPattern = Pattern.compile("^[0-9]{0," + number + "}+(\\.[0-9]{0,})?$");
        }
    }

    /**
     * 既限制整数位数又限制小数位数
     * Limit both integer and decimal places
     *
     * @param integers 整数位数(Number of integer digits)
     * @param decimals 小数位数(Decimal Places)
     */

    public DecimalInputTextWatcher(int integers, int decimals) {
        mPattern = Pattern.compile("^[0-9]{0," + integers + "}+(\\.[0-9]{0," + decimals + "})?$");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        if (TextUtils.isEmpty(text)) {
            return;
        }
        // 删除首位无效的“0”(Remove the first invalid '0')
        if ((editable.length() > 1) && (editable.charAt(0) == '0') && editable.charAt(1) != '.') {
            editable.delete(0, 1);
            return;
        }

        // 首位是“.”自动补“0”(The first one is'. 'and automatically fills in' 0 ')
        if (text.equals(".")) {
            editable.insert(0, "0");
            return;
        }

        if (mPattern != null && !mPattern.matcher(text).matches() && editable.length() > 0) {
            editable.delete(editable.length() - 1, editable.length());
        }
    }

    public enum Type {
        integer, decimal
    }
}
