package com.omni.wallet.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * 汉: 输入框字符限制
 * En: EditInputFilter
 * author: guoyalei
 * date: 2023/3/6
 */
public class EditInputFilter implements InputFilter {
    public EditInputFilter(int max_count) {
        this.max_count = max_count;
    }

    private int max_count = 24;//设置最大长度为24个英文字符或者12个中文汉字(Set the maximum length to 24 English characters or 12 Chinese charact)

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (dest == null || source == null) {
            return null;
        }
        // 判断EditText输入内容+已有内容长度是否超过设定值，超过则做处理(Judge whether the length of EditText input content+existing content exceeds the set value, and handle it if it exceeds the set value)
        if (getTextLengthContainsChinese(dest.toString()) + getTextLengthContainsChinese(source.toString()) > max_count) {

            int destLength = getTextLengthContainsChinese(dest.toString());
            // 已有内容已经有20个字符则返回空字符(If the existing content has 20 characters, return null characters)
            if (destLength >= max_count) {
                return "";
            } else if (destLength == 0) {
                // 已有内容内有0个字符(There are 0 characters in the existing content)
                return source.toString().substring(0, max_count - destLength);
            } else {
                // 已有内容有若干字符但是不超过20也大于0个(The existing content has several characters but no more than 20 and no more than 0)
                int sourceLength = max_count - destLength;
                return source.toString().substring(0, getValidIndex(sourceLength, source.toString()));
            }

        }
        return null;
    }

    /**
     * 获取要截取到位置的索引
     * Get the index to intercept to the location
     *
     * @param sourceLength
     * @param source
     * @return
     */
    private int getValidIndex(int sourceLength, String source) {
        if (TextUtils.isEmpty(source)) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) > 255) {
                length += 2;
            } else {
                length++;
            }
            if (length == sourceLength) {
                return i + 1;
            }
            if (length > sourceLength) {
                return i;
            }
        }
        return 0;
    }


    /**
     * 获取文本的长度，中文自增2，其他符号自增1
     * Get the length of the text. The Chinese character will increase by 2, and other symbols will increase by 1
     *
     * @param text
     * @return
     */
    private int getTextLengthContainsChinese(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 255) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }
}
