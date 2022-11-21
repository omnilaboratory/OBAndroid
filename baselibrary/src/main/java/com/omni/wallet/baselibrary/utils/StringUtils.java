package com.omni.wallet.baselibrary.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.omni.wallet.baselibrary.R;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s) || "null".equals(s);
    }


    public static String cleanString(String str) {
        if (isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }

    public static String replaceNullString(String str) {
        return replaceNullString(str, "--");
    }

    public static String replaceNullString(String str, String nullStr) {
        if (isEmpty(str)) {
            return nullStr;
        } else {
            return str;
        }
    }

    public static String replaceNullString(String str, String nullStr, String unit) {
        if (isEmpty(str)) {
            return nullStr;
        } else {
            return str + unit;
        }
    }

    public static String replaceNumberString(String str, String nullStr) {
        if ("0".equals(nullString2Number(str))) {
            return nullStr;
        } else {
            return str;
        }
    }

    public static String replaceNumberString(String str, String nullStr, String unit) {
        if ("0".equals(nullString2Number(str))) {
            return nullStr;
        } else {
            return str + unit;
        }
    }

    public static String nullString2Number(String str) {
        if (isEmpty(str)) {
            return "0";
        } else {
            return str;
        }
    }

    public static void setTextReplaceNullString(TextView textView, String str, String nullStr, String unit) {
        if (textView == null) {
            return;
        }
        textView.setText(replaceNullString(str, nullStr, unit));
    }

    public static void setTextReplaceNullString(TextView textView, String str, String nullStr) {
        if (textView == null) {
            return;
        }
        textView.setText(replaceNullString(str, nullStr, ""));
    }

    public static void setTextReplaceNumberString(TextView textView, String str, String nullStr, String unit) {
        if (textView == null) {
            return;
        }
        textView.setText(replaceNumberString(str, nullStr, unit));
    }

    public static void setTextReplaceNumberString(TextView textView, String str, String nullStr) {
        if (textView == null) {
            return;
        }
        textView.setText(replaceNumberString(str, nullStr, ""));
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1   字符串1
     * @param str2   字符串2
     * @param isCase 是否大小写敏感
     * @return 两个字符串是否相等
     */
    public static Boolean StringEquals(String str1, String str2, Boolean isCase) {
        if (isCase) {
            return str1.equals(str2);
        }
        if (TextUtils.isEmpty(str1) && TextUtils.isEmpty(str2)) {
            return true;
        }
        if ("null".equals(str1) && "null".equals(str2)) {
            return true;
        }
        if (!isEmpty(str1) && !isEmpty(str2)) {
            return str1.toUpperCase().equals(str2.toUpperCase());
        } else {
            return false;
        }
    }

    /**
     * Map值 object 转 string
     */
    public static Map<String, String> obj2Str(Map<String, Object> data) {
        Map<String, String> result = new HashMap<String, String>();
        for (Entry<String, Object> set : data.entrySet()) {
            result.put(set.getKey(), obj2Str(set.getValue()));
        }
        return result;
    }

    public static String obj2Str(Object data) {
        return data == null ? "" : data.toString();
    }

    /**
     * 获取URL中的文件名
     */
    public static String getUrlFileName(String url) {
        String result = "";
        if (!StringUtils.isEmpty(url) && url.contains("/")) {
            int index = url.lastIndexOf("/");
            result = url.substring(index + 1, url.length());
        }
        return result;
    }

    /**
     * 获取URL中的文件名
     */
    public static String getUrlFileNameUnSuffix(String url) {
        String result = "";
        if (!StringUtils.isEmpty(url) && url.contains("/")) {
            int index = url.lastIndexOf("/");
            result = url.substring(index + 1, url.length());
            int pointIndex = result.lastIndexOf(".");
            result = result.substring(0, pointIndex);
        }
        return result;
    }

    private static String getRMBStr(Context context) {
        return context.getResources().getString(R.string.base_rmb);
    }

    /**
     * 格式化字符串为小数点后几位的数字
     */
    public static String formatString2DoubleStr(String str, int length) {
        if (!CheckoutUtils.checkIsNumber2(str)) {
            LogUtils.e(TAG, "字符串不是数字");
            return str;
        }
        if (length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("0.");
            for (int i = 0; i < length; i++) {
                builder.append("0");
            }
            DecimalFormat df = new DecimalFormat(builder.toString());//格式化
            Double doubleValue = Double.parseDouble(nullString2Number(str));//转换成Double
            return df.format(doubleValue);
        }
        LogUtils.e(TAG, "小数点后位数小于1");
        return str;
    }

    /**
     * 获取人民币符号开头的文字
     */
    public static String getRMBText(Context context, String showPrice) {
        if (StringUtils.isEmpty(showPrice)) {
            return "——";
        }
        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df;
            if (showPrice.contains(".") || showPrice.contains("E") || showPrice.contains("e")) {
                df = new DecimalFormat("#,##0.00");
                text = getRMBStr(context) + df.format(Double.parseDouble(showPrice));
            } else {
                df = new DecimalFormat("#,###");
                text = getRMBStr(context) + df.format(Long.parseLong(showPrice));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 获取人民币符号开头的文字
     */
    public static String getRMBText(Context context, int showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "——";
        }
        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            text = getRMBStr(context) + df.format(showPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 获取人民币符号开头的文字
     */
    public static String getRMBText(Context context, long showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "——";
        }
        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            text = getRMBStr(context) + df.format(showPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 获取人民币符号开头的文字
     */
    public static String getRMBText(Context context, double showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "——";
        }
        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            text = getRMBStr(context) + df.format(showPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 获取保留小数点后两位，并用逗号分隔的价格文字
     */
    public static String getPriceText(double showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "——";
        }
        String text = String.valueOf(showPrice);
        try {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            text = df.format(showPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * 获取小的人民币符号开头的文字
     */
    public static SpannableString getSmallRMBText(Context context, int smallSize, long showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return new SpannableString("——");
        }

        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            text = getRMBStr(context) + df.format(showPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpannableString spannableString = new SpannableString(text);
        int textSize = DisplayUtil.sp2px(context, smallSize);
        spannableString.setSpan(new AbsoluteSizeSpan(textSize), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 获取小的人民币符号开头的文字
     */
    public static SpannableString getSmallRMBText(Context context, int smallSize, String showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return new SpannableString("——");
        }
        String text = getRMBStr(context) + showPrice;
        try {
            DecimalFormat df;
            if (showPrice.contains(".") || showPrice.contains("E") || showPrice.contains("e")) {
                df = new DecimalFormat("#,##0.00");
                text = getRMBStr(context) + df.format(Double.parseDouble(showPrice));
            } else {
                df = new DecimalFormat("#,###");
                text = getRMBStr(context) + df.format(Long.parseLong(showPrice));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpannableString spannableString = new SpannableString(text);
        int textSize = DisplayUtil.sp2px(context, smallSize);
        spannableString.setSpan(new AbsoluteSizeSpan(textSize), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 格式化金额，每隔3位添加逗号
     */
    public static String formatMoney(long money) {
        String text = String.valueOf(money);
        try {
            DecimalFormat df = new DecimalFormat("#,###");
            text = df.format(money);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    /**
     * 格式化浮点数保留小数点后两位
     */
    public static String formatDouble2(float value) {
        return String.format("%.2f", value).toString();
    }


    /**
     * TextView的文字中间添加横线
     */
    public static void addMiddleLine(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中间横线
        textView.getPaint().setAntiAlias(true);// 抗锯齿
    }

    /**
     * textView去掉中间设置的横线
     */
    public static void removeMiddleLine(TextView textView) {
        textView.getPaint().setFlags(0); //去掉中间的横线
    }

    /**
     * 字符串是否Double
     */
    public static boolean isDoubleStr(String str) {
        if (isEmpty(str)) {
            return false;
        }
        if (CheckoutUtils.checkIsNumber2(str)) {
            return true;
        }
        if (str.contains("e")) {
            return CheckoutUtils.checkIsNumber(str.replace("e", ""));
        } else if (str.contains("E")) {
            return CheckoutUtils.checkIsNumber(str.replace("E", ""));
        }
        return false;
    }

    /**
     * 加密手机号中间四位
     **/
    public static String encodeMobileNumber(String mobile) {
        // userName = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        // 替换身份证号码
        // idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})","$1*****$2");
        // 4304*****7733
        if (isEmpty(mobile) || mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length());
    }

    /**
     * 格式化身份证号，前3位，后4位，中间加星号
     */
    public static String formatIDCardNumber(String IDCardNumber) {
        if (StringUtils.isEmpty(IDCardNumber) || IDCardNumber.length() < 8) {
            return IDCardNumber;
        }
        return IDCardNumber.substring(0, 3) + "********" + IDCardNumber.substring(IDCardNumber.length() - 4, IDCardNumber.length());
    }

    /**
     * 加密手机号中间四位
     **/
    public static String encodePubkey(String pubkey) {
        if (isEmpty(pubkey)) {
            return pubkey;
        }
        return pubkey.substring(0, 11) + ".........." + pubkey.substring(pubkey.length() - 30, pubkey.length());
    }

    /**
     * 去掉链接的下划线
     */
    public static void removeHtmlTextBottomLine(TextView textView) {
        Spannable s = new Spannable.Factory().newSpannable(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    @SuppressLint("ParcelCreator")
    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            super.onClick(widget);
        }
    }

    //key为渠道名的key，对应友盟的 UMENG_CHANNEL

    /**
     * 根据Key获取清单文件的meta-data
     */
    public static String getMetaData(Context context, String key) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    /**
     * 内容复制到剪贴板
     */
    public static void copyString(Context context, String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", text);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
    }

    /**
     * 设置一段文字中个别字体的大小
     */
    public static SpannableString changeTextSize(String text, int size, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 如果数字小于10，就自动补0
     */
    public static String full0(int num) {
        if (num < 10 && num >= 0) {
            return "0" + num;
        }
        return String.valueOf(num);
    }

    /**
     * 浮点类型数字转换成字符串并去掉后边的.0
     */
    public static String formatFloat(float value) {
        String temp = String.valueOf(value);
        if (temp.endsWith(".0")) {
            return temp.replace(".0", "");
        } else {
            return temp;
        }
    }

    /**
     * String转Double
     */
    public static double str2Double(String str) {
        return Double.parseDouble(nullString2Number(str));
    }

    /**
     * 数组删除
     *
     * @param content  要删除内容的数组
     * @param specific 删除的内容
     * @return 删除指定内容后的数组
     */
    public static <T> T[] arraySpeDel(T[] content, T specific) {
        int len = content.length;
        for (int i = 0; i < content.length; i++) {
            if (content[i].equals(specific)) {
                System.arraycopy(content, i + 1, content, i, len - 1 - i);
                break;
            }
        }
        return Arrays.copyOf(content, len - 1);
    }

    /**
     * 数组添加
     *
     * @param src      源数组
     * @param specific 动态参数
     * @return 返回源数组同等类型的数组
     */
    public static <T> T[] arrayAdd(T[] src, T... specific) {
        //返回类的组件类型的数组。如果这个类并不代表一个数组类，此方法返回null。
        Class<?> type = src.getClass().getComponentType();
        T[] temp = (T[]) Array.newInstance(type, src.length + specific.length);
        System.arraycopy(src, 0, temp, 0, src.length);
        System.arraycopy(specific, 0, temp, temp.length - 1, specific.length);
        return temp;
    }
}
