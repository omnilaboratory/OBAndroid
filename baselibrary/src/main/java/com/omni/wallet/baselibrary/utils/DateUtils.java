package com.omni.wallet.baselibrary.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * 日期工具类
 */

public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String MM_DD_HH_MM = "MM月dd日 HH:mm";

    /**
     * 时间戳转换成日期格式字符串
     */
    public static String dateFormat(long timeStamp, String format) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        if (timeStamp == 0) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(new Date(timeStamp));
    }


    /**
     * 十位时间戳字符串转年月
     */
    public static String YearMonth(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy.MM");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 十位时间戳字符串转月日
     */
    public static String MonthDay(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM/dd");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 十位时间戳字符串转时秒
     */
    public static String Hourmin(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 十位时间戳字符串转年月日
     */
    public static String yearMonthDay(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * 格式化当前时间
     */
    public static String formatCurrentDate() {
        return dateFormat(System.currentTimeMillis(), YYYY_MM_DD);
    }

    /**
     * 格式化当前时间
     */
    public static String formatCurrentTime() {
        return dateFormat(System.currentTimeMillis(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 把Long类型的毫秒值转换为X天X时X分X秒的格式
     *
     * @param countTime day天 HH时mm分ss秒
     */
    public synchronized static String longToStringTime(long countTime) {
        if (countTime <= 0) {
            return "0秒";
        }
        long[] tempLongTime = new long[4];
        formatLongTime(tempLongTime, countTime);
        long days = tempLongTime[0];
        long hours = tempLongTime[1];
        long minutes = tempLongTime[2];
        long second = tempLongTime[3];
        // 判断显示格式
        long oneDay = 1000 * 60 * 60 * 24;
        long oneHours = 1000 * 60 * 60;
        long oneMinute = 1000 * 60;
        if (countTime >= oneDay) {
            return days + "天" + hours + "时" + minutes + "分" + second + "秒";
        } else if (countTime >= oneHours && countTime < oneDay) {
            return hours + "时" + minutes + "分" + second + "秒";
        } else if (countTime >= oneMinute && countTime < oneHours) {
            return minutes + "分" + second + "秒";
        } else if (countTime < oneMinute) {
            return second + "秒";
        }
        return days + "天" + hours + "时" + minutes + "分" + second + "秒";
    }

    /**
     * 把Long类型的毫秒值转换为X天X时X分X秒的格式
     *
     * @param countTime day天 HH时mm分ss秒
     */
    public synchronized static void formatLongTime(long[] result, long countTime) {
        if (countTime <= 0) {
            return;
        }
        long days = countTime / (1000 * 60 * 60 * 24);
        long hours = (countTime - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (countTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = (countTime - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        days = days > 0 ? days : 0;
        hours = hours > 0 ? hours : 0;
        minutes = minutes > 0 ? minutes : 0;
        second = second > 0 ? second : 0;
        result[0] = days;
        result[1] = hours;
        result[2] = minutes;
        result[3] = second;
    }

    /**
     * 将传入的值 格式化成时间形式显示用于倒计时，单位s
     */
    public static String formatSecond(long secondTime) {
        String timeStr;
        long hour;// 时
        long minute;// 分
        long second;// 秒
        if (secondTime <= 0) {
            return "00:00:00";
        } else {
            minute = secondTime / 60;
            if (minute < 60) {
                second = secondTime % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = secondTime - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    /**
     * 将传入的值 格式化成时间形式显示
     * 用于倒计时，time格式是秒
     */
    public static String formatSecond2(int time) {
        String timeStr;
        int hour;// 时
        int minute;// 分
        int second;// 秒
        if (time <= 0) {
            return "0秒";
        } else if (time > 0 && time < 60) {
            return time + "秒";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = minute + "分钟" + second + "秒";
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "大于99小时";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = hour + "小时" + minute + "分钟" + second + "秒";
            }
        }
        return timeStr;
    }

    /**
     * 判断数字是不是小于10，小于10需要在前面加0
     */
    private static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


    /**
     * 根据时间字符串获取时间戳
     */
    public static String dateStr2TimeStr(String dateStr, String format) {
        return String.valueOf(dateStr2TimeStamp(dateStr, format));
    }

    /**
     * date转字符串
     */
    public static String date2Str(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    /**
     * 根据时间字符串获取时间戳
     */
    public static Date str2Date(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据时间字符串获取时间戳
     */
    public static long dateStr2TimeStamp(String dateStr, String format) {
        if (StringUtils.isEmpty(dateStr)) {
            LogUtils.e(TAG, "时间字符串为空");
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "时间字符串转时间戳异常：" + e.getMessage());
        }
        return 0;
    }

    /**
     * 根据系统设置的12或者24小时进制获取时间字符串
     * 时间在昨天的话显示昨天，在昨天之前统一显示日期加时间
     * flag 指的是当显示成日期加时间这种形式的时候，时间是否显示
     */
    public static String messageTimeFormat(Context context, String dateStr, String format, boolean flag) {
        Date date = str2Date(dateStr, format);
        if (date == null) {
            return "";
        }
        String chatTime;
        // 获取当前系统设置
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            chatTime = getMessageDateBy24(date, flag);
        } else {
            chatTime = getMessageDateBy12(date, flag);
        }
        return chatTime;
    }

    /**
     * 根据系统设置的12或者24小时进制获取时间字符串
     * 时间在昨天的话显示昨天，在昨天之前统一显示日期加时间
     * flag 指的是当显示成日期加时间这种形式的时候，时间是否显示
     */
    public static String messageTimeFormat(Context context, Date date, boolean flag) {
        String chatTime;
        // 获取当前系统设置
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            chatTime = getMessageDateBy24(date, flag);
        } else {
            chatTime = getMessageDateBy12(date, flag);
        }
        return chatTime;
    }


    /**
     * 获取时间，12小时制，带上午下午，时间大于昨天之后显示星期，大于上周之后显示日期
     */
    public static String getMessageDateBy12(Date chatDate, boolean flag) {
        String chatTime;
        Calendar cal = Calendar.getInstance();
        cal.setTime(chatDate);
        if (chatDate.getTime() < getYearDateZero().getTime()) {// 今年以前的时间只显示年
            SimpleDateFormat lastYearFormat = new SimpleDateFormat("yyyy年");
            chatTime = lastYearFormat.format(chatDate) + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        } else if (chatDate.getTime() < getYesterdayDateZero().getTime()) {// 昨天以前的时间显示日期加时间
            if (flag) {
                chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日 " + get12HoursByAP(chatDate);
            } else {
                chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
            }
        } else if (chatDate.getTime() < getTodayDateZero().getTime()) {// 昨天的时间显示昨天加时间
            if (flag) {
                chatTime = "昨天 " + get12HoursByAP(chatDate);
            } else {
                chatTime = "昨天";
            }
        } else {// 当天的直接显示时间
            chatTime = get12HoursByAP(chatDate);
        }
        return chatTime;
    }


    /**
     * 获取时间，大于昨天之后显示星期，大于上周之后显示日期
     */
    public static String getMessageDateBy24(Date chatDate, boolean flag) {
        String chatTime;
        Calendar cal = Calendar.getInstance();
        cal.setTime(chatDate);
        if (chatDate.getTime() < getYearDateZero().getTime()) {// 今年以前的时间只显示年
            SimpleDateFormat lastYearFormat = new SimpleDateFormat("yyyy年");
            chatTime = lastYearFormat.format(chatDate) + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        } else if (chatDate.getTime() < getYesterdayDateZero().getTime()) {// 昨天以前的时间显示日期加时间
            if (flag) {
                chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日 " + get24Hours(chatDate);
            } else {
                chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
            }
        } else if (chatDate.getTime() < getTodayDateZero().getTime()) {// 昨天的时间显示昨天加时间
            if (flag) {
                chatTime = "昨天 " + get24Hours(chatDate);
            } else {
                chatTime = "昨天";
            }
        } else {// 当天的直接显示时间
            chatTime = get24Hours(chatDate);
        }
        return chatTime;
    }

    /**
     * 聊天使用，时间大于昨天之后先显示星期才显示几月几日的
     */
    public static String chatTimeFormat(Context context, Date chatDate) {
        String chatTime;
        // 获取当前系统设置
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            chatTime = getChatDateBy24(chatDate);
        } else {
            chatTime = getChatDateByAP(chatDate);
        }
        return chatTime;
    }

    /**
     * 获取时间，12小时制，带上午下午，时间大于昨天之后显示星期，大于上周之后显示日期
     */
    public static String getChatDateByAP(Date chatDate) {
        String chatTime;
        Calendar cal = Calendar.getInstance();
        cal.setTime(chatDate);
        if (chatDate.getTime() < getYearDateZero().getTime()) {
            SimpleDateFormat lastYearFormat = new SimpleDateFormat("yyyy年");
            chatTime = lastYearFormat.format(chatDate) + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        } else if (chatDate.getTime() < getWeekDateZero().getTime()) {
            chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日" + get12HoursByAP(chatDate);
        } else if (chatDate.getTime() < getYesterdayDateZero().getTime()) {
            chatTime = getWeekOfDate(chatDate) + get12HoursByAP(chatDate);
        } else if (chatDate.getTime() < getTodayDateZero().getTime()) {
            chatTime = "昨天" + get12HoursByAP(chatDate);
        } else {
            chatTime = get12HoursByAP(chatDate);
        }
        return chatTime;
    }

    /**
     * 获取十二小时制时间，带有上下午
     */
    private static String get12HoursByAP(Date date) {
        String time;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour <= 12) {
            time = "上午" + hour + ":" + getMinute(cal.get(Calendar.MINUTE));
        } else {
            time = "下午" + (hour - 12) + ":" + getMinute(cal.get(Calendar.MINUTE));
        }
        return time;
    }

    /**
     * 获取时间，大于昨天之后显示星期，大于上周之后显示日期
     */
    public static String getChatDateBy24(Date chatDate) {
        String chatTime;
        Calendar cal = Calendar.getInstance();
        cal.setTime(chatDate);
        if (chatDate.getTime() < getYearDateZero().getTime()) {
            SimpleDateFormat lastYearFormat = new SimpleDateFormat("yyyy年");
            chatTime = lastYearFormat.format(chatDate) + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        } else if (chatDate.getTime() < getWeekDateZero().getTime()) {
            chatTime = (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日 " + get24Hours(chatDate);
        } else if (chatDate.getTime() < getYesterdayDateZero().getTime()) {
            chatTime = getWeekOfDate(chatDate) + get24Hours(chatDate);
        } else if (chatDate.getTime() < getTodayDateZero().getTime()) {
            chatTime = "昨天 " + get24Hours(chatDate);
        } else {
            chatTime = get24Hours(chatDate);
        }
        return chatTime;
    }


    /**
     * 24小时制
     */
    private static String get24Hours(Date date) {
        String time;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        time = hour + ":" + getMinute(cal.get(Calendar.MINUTE));
        return time;
    }

    /**
     * 判断时间分钟是否大于10，否时前面需加上“0”
     */
    private static String getMinute(int minute) {
        if (minute < 10) {
            return "0" + minute;
        } else {
            return String.valueOf(minute);
        }
    }

    /**
     * 获取当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取某月第一天是星期几（从星期日开始，对应的是1）
     */
    public static int getDayOfMonthInWeek(Date date) {
        // 获取日历实例
        Calendar cld = Calendar.getInstance();
        // 设置月份
        cld.setTime(date);
        // 设置日历成当月的第一天
        cld.set(Calendar.DAY_OF_MONTH, 1);
        //获取该月第一天对应的星期数字（星期日开始，对应的是1）
        return cld.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得当天0点时间
     */
    public static Date getTodayDateZero() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当月第一天时间戳
     */
    public static long getMonthFirstdayDateZero() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0); //获取当前月第一天
        c.set(Calendar.DAY_OF_MONTH, 1); //设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0); //将小时至0
        c.set(Calendar.MINUTE, 0); //将分钟至0
        c.set(Calendar.SECOND, 0); //将秒至0
        c.set(Calendar.MILLISECOND, 0); //将毫秒至0
        return c.getTimeInMillis();
    }

    /**
     * 获取当月最后一天时间戳
     */
    public static long getMonthLastdayDateZero() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH)); //获取当前月最后一天
        c.set(Calendar.HOUR_OF_DAY, 23); //将小时至23
        c.set(Calendar.MINUTE, 59); //将分钟至59
        c.set(Calendar.SECOND, 59); //将秒至59
        c.set(Calendar.MILLISECOND, 999); //将毫秒至999
        return c.getTimeInMillis();
    }

    /**
     * 获取指定日期所在月份开始的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static Long getMonthBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND, 0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获取指定日期所在月份结束的时间戳
     *
     * @param date 指定日期
     * @return
     */
    public static Long getMonthEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND, 59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        return c.getTimeInMillis();
    }

    /**
     * 获得昨天0点时间
     */
    public static Date getYesterdayDateZero() {
        return getYesterdayZeroCalendar().getTime();
    }

    /**
     * 获取今天0点的日历对象
     */
    private static Calendar getTodayZeroCalendar() {
        Calendar calendar = Calendar.getInstance(); //当前时间
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取昨天0点的日历对象
     */
    private static Calendar getYesterdayZeroCalendar() {
        Calendar calendar = Calendar.getInstance(); //当前时间
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获得本周日0点时间
     */
    public static Date getWeekDateZero() {
        Calendar cal = Calendar.getInstance();
        Date todayZero = getTodayDateZero();
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        Date sundayZero = new Date(todayZero.getTime() - w * 24 * 60 * 60 * 1000);
        return sundayZero;
    }

    /**
     * 获得本年0点时间
     */
    public static Date getYearDateZero() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, currentYear);
        return cal.getTime();
    }

    /**
     * 判断是否为今天(效率比较高)
     */
    public static boolean isToday(String dateStr, String formatStr) {
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = YYYY_MM_DD;
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(new Date());
            if (calendar.get(Calendar.YEAR) == (nowCalendar.get(Calendar.YEAR))) {
                int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 某个日期是否大于今天
     */
    public static boolean afterToday(String dateStr, String formatStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return false;
        }
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = YYYY_MM_DD;
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.setTime(new Date());
            if (calendar.get(Calendar.YEAR) == (todayCalendar.get(Calendar.YEAR))) {
                int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
                if (diffDay > 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
//
//        try {
//            Date date = format.parse(dateStr);
//            Date todayDate = new Date();
//            return date.after(todayDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    /**
     * 某个时间是否在今天之前
     */
    public static boolean beforeToday(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());
        if (calendar.get(Calendar.YEAR) == (todayCalendar.get(Calendar.YEAR))) {
            int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
            if (diffDay < 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 某个日期是否小于今天
     */
    public static boolean beforeToday(String dateStr, String formatStr) {
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = YYYY_MM_DD;
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.setTime(new Date());
            if (calendar.get(Calendar.YEAR) == (todayCalendar.get(Calendar.YEAR))) {
                int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - todayCalendar.get(Calendar.DAY_OF_YEAR);
                if (diffDay < 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
//        if (StringUtils.isEmpty(formatStr)) {
//            formatStr = YYYY_MM_DD;
//        }
//        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
//        try {
//            Date date = format.parse(dateStr);
//            Date todayDate = new Date();
//            return date.before(todayDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    /**
     * 某个日期距离今天多少天
     */
    public static int differentToday(String dateStr, String formatStr) {
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = YYYY_MM_DD;
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            long time1 = date.getTime();
            Date todayDate = format.parse(dateFormat(System.currentTimeMillis(), YYYY_MM_DD));
            long time2 = todayDate.getTime();
            long distance = time1 - time2;
            long oneDay = 1000 * 3600 * 24;
            float result = (float) distance / (float) oneDay;
            return (int) result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 某个时间距离当前时间差多少小时
     */
    public static int differentHour(String dateStr, String formatStr) {
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = "yyyy-MM-dd HH:mm";
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            long time1 = date.getTime();
            Date todayDate = format.parse(dateFormat(System.currentTimeMillis(), "yyyy-MM-dd HH:mm"));
            long time2 = todayDate.getTime();
            long distance = time1 - time2;
            long oneHour = 1000 * 3600;
            float result = (float) distance / (float) oneHour;
            return (int) result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 某个时间距离当前时间差多少分钟
     */
    public static int differentMinute(String dateStr, String formatStr) {
        try {
            if (StringUtils.isEmpty(formatStr)) {
                formatStr = "yyyy-MM-dd HH:mm";
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
            Date date = format.parse(dateStr);
            long time1 = date.getTime();
            Date todayDate = format.parse(dateFormat(System.currentTimeMillis(), "yyyy-MM-dd HH:mm"));
            long time2 = todayDate.getTime();
            long distance = time1 - time2;
            long oneMinute = 1000 * 60;
            float result = (float) distance / (float) oneMinute;
            return (int) result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取某个月份的天数
     */
    public static int getCurrentMonthSize(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取年
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentYear() {
        return getYear(new Date());
    }

    /**
     * 获取月
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonth() {
        return getMonth(new Date());
    }

    /**
     * 获取日
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.get(Calendar.DATE);
    }

    public static int getCurrentDay() {
        return getDay(new Date());
    }

    /**
     * 获取今天0点的时间戳
     */
    public static long getTodayZeroTime() {
        return getTodayZeroCalendar().getTimeInMillis();
    }

    /**
     * 获取昨天0点的时间戳
     */
    public static long getYesterdayZeroTime() {
        return getYesterdayZeroCalendar().getTimeInMillis();
    }

    /**
     * 获取今天的时间的字符串
     */
    public static String getTodayZeroTimeStr(String format) {
        return dateFormat(getTodayZeroCalendar().getTimeInMillis(), format);
    }

    /**
     * 获取昨天的时间的字符串
     */
    public static String getYesterdayZeroTimeStr(String format) {
        return dateFormat(getYesterdayZeroCalendar().getTimeInMillis(), format);
    }

    /**
     * String转Calendar
     */
    public static Calendar str2Calendar(String dateStr, String format) {
        Date date = str2Date(dateStr, format);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }

    /**
     * String转Calendar
     */
    public static Calendar long2Calendar(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 获得任意时区的时间
     */
    public static String getFormatDateString(float timeZoneOffset, String format) {
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }
        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }

    /**
     * 年月日时分秒转换为月日时分
     */
    public static String converToStandardTime(String time) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");


        String format = null;
        try {
            format = sdf2.format(sdf1.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return format;

    }

    /**
     * 年月日时分秒转换为年月日
     */
    public static String converToYearMonthDay(String time) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");


        String format = null;
        try {
            format = sdf2.format(sdf1.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return format;

    }

    /**
     * 得到昨天的日期
     *
     * @return
     */
    public static String getYesterDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yestoday = sdf.format(calendar.getTime());
        return yestoday;
    }

    /**
     * 得到今天的日期
     *
     * @return
     */
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }
}
