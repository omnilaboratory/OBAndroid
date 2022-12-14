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
     * ?????????????????????????????????
     *
     * @param str1   ?????????1
     * @param str2   ?????????2
     * @param isCase ?????????????????????
     * @return ???????????????????????????
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
     * Map??? object ??? string
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
     * ??????URL???????????????
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
     * ??????URL???????????????
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
     * ????????????????????????????????????????????????
     */
    public static String formatString2DoubleStr(String str, int length) {
        if (!CheckoutUtils.checkIsNumber2(str)) {
            LogUtils.e(TAG, "?????????????????????");
            return str;
        }
        if (length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("0.");
            for (int i = 0; i < length; i++) {
                builder.append("0");
            }
            DecimalFormat df = new DecimalFormat(builder.toString());//?????????
            Double doubleValue = Double.parseDouble(nullString2Number(str));//?????????Double
            return df.format(doubleValue);
        }
        LogUtils.e(TAG, "????????????????????????1");
        return str;
    }

    /**
     * ????????????????????????????????????
     */
    public static String getRMBText(Context context, String showPrice) {
        if (StringUtils.isEmpty(showPrice)) {
            return "??????";
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
     * ????????????????????????????????????
     */
    public static String getRMBText(Context context, int showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "??????";
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
     * ????????????????????????????????????
     */
    public static String getRMBText(Context context, long showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "??????";
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
     * ????????????????????????????????????
     */
    public static String getRMBText(Context context, double showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "??????";
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
     * ??????????????????????????????????????????????????????????????????
     */
    public static String getPriceText(double showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return "??????";
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
     * ??????????????????????????????????????????
     */
    public static SpannableString getSmallRMBText(Context context, int smallSize, long showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return new SpannableString("??????");
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
     * ??????????????????????????????????????????
     */
    public static SpannableString getSmallRMBText(Context context, int smallSize, String showPrice) {
        if (StringUtils.isEmpty(String.valueOf(showPrice))) {
            return new SpannableString("??????");
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
     * ????????????????????????3???????????????
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
     * ??????????????????????????????????????????
     */
    public static String formatDouble2(float value) {
        return String.format("%.2f", value).toString();
    }


    /**
     * TextView???????????????????????????
     */
    public static void addMiddleLine(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //????????????
        textView.getPaint().setAntiAlias(true);// ?????????
    }

    /**
     * textView???????????????????????????
     */
    public static void removeMiddleLine(TextView textView) {
        textView.getPaint().setFlags(0); //?????????????????????
    }

    /**
     * ???????????????Double
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
     * ???????????????????????????
     **/
    public static String encodeMobileNumber(String mobile) {
        // userName = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        // ?????????????????????
        // idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})","$1*****$2");
        // 4304*****7733
        if (isEmpty(mobile) || mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length());
    }

    /**
     * ???????????????????????????3?????????4?????????????????????
     */
    public static String formatIDCardNumber(String IDCardNumber) {
        if (StringUtils.isEmpty(IDCardNumber) || IDCardNumber.length() < 8) {
            return IDCardNumber;
        }
        return IDCardNumber.substring(0, 3) + "********" + IDCardNumber.substring(IDCardNumber.length() - 4, IDCardNumber.length());
    }

    /**
     * ????????????????????????
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

    //key???????????????key?????????????????? UMENG_CHANNEL

    /**
     * ??????Key?????????????????????meta-data
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
     * ????????????????????????
     */
    public static void copyString(Context context, String text) {
        //???????????????????????????
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            // ?????????????????????ClipData
            ClipData mClipData = ClipData.newPlainText("Label", text);
            // ???ClipData?????????????????????????????????
            cm.setPrimaryClip(mClipData);
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    public static SpannableString changeTextSize(String text, int size, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * ??????????????????10???????????????0
     */
    public static String full0(int num) {
        if (num < 10 && num >= 0) {
            return "0" + num;
        }
        return String.valueOf(num);
    }

    /**
     * ??????????????????????????????????????????????????????.0
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
     * String???Double
     */
    public static double str2Double(String str) {
        return Double.parseDouble(nullString2Number(str));
    }

    /**
     * ????????????
     *
     * @param content  ????????????????????????
     * @param specific ???????????????
     * @return ??????????????????????????????
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
     * ????????????
     *
     * @param src      ?????????
     * @param specific ????????????
     * @return ????????????????????????????????????
     */
    public static <T> T[] arrayAdd(T[] src, T... specific) {
        //????????????????????????????????????????????????????????????????????????????????????????????????null???
        Class<?> type = src.getClass().getComponentType();
        T[] temp = (T[]) Array.newInstance(type, src.length + specific.length);
        System.arraycopy(src, 0, temp, 0, src.length);
        System.arraycopy(specific, 0, temp, temp.length - 1, specific.length);
        return temp;
    }
}
