package com.omni.testnet.baselibrary.view.datePicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.omni.testnet.baselibrary.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 自定义的上下滚动的时间选择器控件
 */
public class DatePicker extends LinearLayout implements NumberPicker.OnValueChangeListener {
    private static final String TAG = DatePicker.class.getSimpleName();

    private Context mContext;
    private NumberPicker mYearPicker;// 代表年的数字滚轮选择器
    private NumberPicker mMonthPicker;// 代表月的数字滚轮选择器
    private NumberPicker mDayOfMonthPicker;// 代表日的数字滚轮选择器
    private NumberPicker mHourPicker;// 代表小时的数字滚轮选择器
    private NumberPicker mMinutePicker;// 代表分钟的数字滚轮选择器
    private Calendar mCalendar;   // 日历
    private OnDateChangedListener mOnDateChangedListener; // 变动监听
    private LayoutInflater mLayoutInflater;

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mLayoutInflater.inflate(R.layout.layout_date_picker, this, true);
        mYearPicker = findViewById(R.id.year_picker);
        mMonthPicker = findViewById(R.id.month_picker);
        mDayOfMonthPicker = findViewById(R.id.day_picker);
        mHourPicker = findViewById(R.id.hour_picker);
        mMinutePicker = findViewById(R.id.minute_picker);
        //
        mYearPicker.setHovered(false);
        mYearPicker.setOnValueChangeListener(this);
        mMonthPicker.setOnValueChangeListener(this);
        mDayOfMonthPicker.setOnValueChangeListener(this);
        mHourPicker.setOnValueChangeListener(this);
        mMinutePicker.setOnValueChangeListener(this);
        mCalendar = Calendar.getInstance();
        setDate(mCalendar.getTime());
    }

    public DatePicker setDate(Date date) {
        mCalendar.setTime(date);
        mDayOfMonthPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mYearPicker.setCurrentNumber(mCalendar.get(Calendar.YEAR));
        mMonthPicker.setCurrentNumber(mCalendar.get(Calendar.MONTH) + 1);
        mDayOfMonthPicker.setCurrentNumber(mCalendar.get(Calendar.DAY_OF_MONTH));
        mHourPicker.setCurrentNumber(mCalendar.get(Calendar.HOUR_OF_DAY));
        mMinutePicker.setCurrentNumber(mCalendar.get(Calendar.MINUTE));
        // 设置了日期之后刷新数据，通知外边数据已经更新
        notifyDateChanged();
        return this;
    }

    // 年的起始值
    public void setStartYear(int startYear) {
        mYearPicker.setStartNumber(startYear);
    }

    // 年的当前值
    public void setCurrentYear(int currentYear) {
        mYearPicker.setCurrentNumber(currentYear);
    }

    // 年的结束值
    public void setEndYear(int endYear) {
        mYearPicker.setEndNumber(endYear);
    }

    @Override
    public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
        if (picker == mYearPicker) {
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.set(newVal, mCalendar.get(Calendar.MONTH), 1);
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mDayOfMonthPicker.setEndNumber(lastDayOfMonth);
        } else if (picker == mMonthPicker) {
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.set(mCalendar.get(Calendar.YEAR), newVal - 1, 1);
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mDayOfMonthPicker.setEndNumber(lastDayOfMonth);
        } else if (picker == mDayOfMonthPicker) {
            mCalendar.set(Calendar.DAY_OF_MONTH, newVal);
        } else if (picker == mHourPicker) {
            mCalendar.set(Calendar.HOUR_OF_DAY, newVal);
        } else if (picker == mMinutePicker) {
            mCalendar.set(Calendar.MINUTE, newVal);
        }
        notifyDateChanged();
    }

    /**
     * The callback used to indicate the user changes\d the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view        The view associated with this listener.
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *                    with {@link Calendar}.
         * @param dayOfMonth  The day of the month that was set.
         */
        void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth);

        void onDateTimeChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth, int hour, int minute);
    }

    public DatePicker setOnDateChangedListener(OnDateChangedListener listener) {
        mOnDateChangedListener = listener;
        return this;
    }

    private void notifyDateChanged() {
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(), getDayOfMonth());
            mOnDateChangedListener.onDateTimeChanged(this, getYear(), getMonth(), getDayOfMonth(), getHour(), getMinute());
        }
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public DatePicker setSoundEffect(Sound sound) {
        mYearPicker.setSoundEffect(sound);
        mMonthPicker.setSoundEffect(sound);
        mDayOfMonthPicker.setSoundEffect(sound);
        return this;
    }

    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        super.setSoundEffectsEnabled(soundEffectsEnabled);
        mYearPicker.setSoundEffectsEnabled(soundEffectsEnabled);
        mMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
        mDayOfMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
    }

    public DatePicker setRowNumber(int rowNumber) {
        mYearPicker.setRowNumber(rowNumber);
        mMonthPicker.setRowNumber(rowNumber);
        mDayOfMonthPicker.setRowNumber(rowNumber);
        mHourPicker.setRowNumber(rowNumber);
        mMinutePicker.setRowNumber(rowNumber);
        return this;
    }

    public DatePicker setTextSize(float textSize) {
        mYearPicker.setTextSize(textSize);
        mMonthPicker.setTextSize(textSize);
        mDayOfMonthPicker.setTextSize(textSize);
        mHourPicker.setTextSize(textSize);
        mMinutePicker.setTextSize(textSize);
        return this;
    }

    public DatePicker setFlagTextSize(float textSize) {
        mYearPicker.setFlagTextSize(textSize);
        mMonthPicker.setFlagTextSize(textSize);
        mDayOfMonthPicker.setFlagTextSize(textSize);
        mHourPicker.setFlagTextSize(textSize);
        mMinutePicker.setFlagTextSize(textSize);
        return this;
    }

    public DatePicker setTextColor(int color) {
        mYearPicker.setTextColor(color);
        mMonthPicker.setTextColor(color);
        mDayOfMonthPicker.setTextColor(color);
        mHourPicker.setTextColor(color);
        mMinutePicker.setTextColor(color);
        return this;
    }

    public DatePicker setFlagTextColor(int color) {
        mYearPicker.setFlagTextColor(color);
        mMonthPicker.setFlagTextColor(color);
        mDayOfMonthPicker.setFlagTextColor(color);
        mHourPicker.setFlagTextColor(color);
        mMinutePicker.setFlagTextColor(color);
        return this;
    }

    public DatePicker setLineColor(int color) {
        mYearPicker.setLineColor(color);
        mMonthPicker.setLineColor(color);
        mDayOfMonthPicker.setLineColor(color);
        mHourPicker.setLineColor(color);
        mMinutePicker.setLineColor(color);
        return this;
    }

    public DatePicker setBackground(int color) {
        super.setBackgroundColor(color);
        mYearPicker.setBackground(color);
        mMonthPicker.setBackground(color);
        mDayOfMonthPicker.setBackground(color);
        mHourPicker.setBackground(color);
        mMinutePicker.setBackground(color);
        return this;
    }

    public void showTimePicker(boolean showTime) {
        if (showTime) {
            mHourPicker.setVisibility(VISIBLE);
            mMinutePicker.setVisibility(VISIBLE);
        } else {
            mHourPicker.setVisibility(GONE);
            mMinutePicker.setVisibility(GONE);
        }
    }

}
