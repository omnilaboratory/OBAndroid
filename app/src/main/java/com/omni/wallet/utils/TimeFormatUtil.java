package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeFormatUtil {

    private static final String TAG = TimeFormatUtil.class.getSimpleName();

    /**
     * Returns a nicely formatted time.
     *
     * @param time    in seconds
     * @param context
     * @return
     */
    public static String formatTimeAndDateLong(long time, Context context) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        String formattedDate = df.format(new Date(time * 1000L));
        DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM, context.getResources().getConfiguration().locale);
        String formattedTime = tf.format(new Date(time * 1000L));
        return (formattedDate + ", " + formattedTime);
    }

    /**
     * Returns a nicely formatted duration.
     *
     * @param duration in seconds
     * @return
     */
    public static String formattedDuration(long duration, Context context) {
        String formattedString = "";

        int hours = (int) duration / 3600;
        String hoursString = context.getResources().getQuantityString(R.plurals.duration_hour, hours, hours);
        int days = (int) duration / 86400;
        String daysString = context.getResources().getQuantityString(R.plurals.duration_day, days, days);
        int minutes = (int) (duration % 3600) / 60;
        String minutesUnit = context.getResources().getString(R.string.duration_minute_short);
        String secondsUnit = context.getResources().getString(R.string.duration_second_short);

        if (duration < 3600) {
            formattedString = String.format("%02d %s, %02d %s", (duration % 3600) / 60, minutesUnit, (duration % 60), secondsUnit);
        } else if (duration < 86400) {
            formattedString = hoursString;
        } else {
            formattedString = daysString;
        }

        return formattedString;
    }

    /**
     * Returns a nicely formatted duration.
     * This always shows only one unit to keep it short.
     *
     * @param duration in seconds
     * @return
     */
    public static String formattedDurationShort(long duration, Context context) {
        String formattedString = "";

        int seconds = (int) duration;
        String secondsString = context.getResources().getQuantityString(R.plurals.duration_second, seconds, seconds);
        int minutes = (int) (duration % 3600) / 60;
        String minutesString = context.getResources().getQuantityString(R.plurals.duration_minute, minutes, minutes);
        int hours = (int) duration / 3600;
        String hoursString = context.getResources().getQuantityString(R.plurals.duration_hour, hours, hours);
        int days = (int) duration / 86400;
        String daysString = context.getResources().getQuantityString(R.plurals.duration_day, days, days);
        int years = (int) duration / (86400 * 365);
        String yearsString = context.getResources().getQuantityString(R.plurals.duration_year, years, years);

        if (duration < 60) {
            formattedString = secondsString;
        } else if (duration < 3600) {
            formattedString = minutesString;
        } else if (duration < 86400) {
            formattedString = hoursString;
        } else if (duration < 86400 * 365) {
            formattedString = daysString;
        } else {
            formattedString = yearsString;
        }

        return formattedString;
    }

    public static long getYearFirstMills() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        String yearDay = year + "-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
             d = sdf.parse(yearDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long mills = d.getTime();
        return mills;
    }

    public static String formatDateLong(long time) {
        SimpleDateFormat sdf = null;
        long yearDayMills = getYearFirstMills();
        if(time-yearDayMills<0){
            sdf = new SimpleDateFormat("yy-MM-dd");
        }else{
            sdf = new SimpleDateFormat("MM-dd");
        }
        String date = sdf.format(time);
        return date;
    }

    public static String getNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        return date;
    }

    public static long getCurrentDayMills() throws ParseException {
        String nowDate = getNowDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse(nowDate);
        long dateNum = d.getTime();
        return dateNum;
    }
}
