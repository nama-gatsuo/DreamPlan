package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by nagamatsuayumu on 15/02/28.
 */
public class DateView extends TextView implements CalendarDatePickerDialog.OnDateSetListener {
    private DateTime dt;

    public DateView(Context context) {
        super(context);
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
        dt = new DateTime().withDate(year, month + 1, day).withTimeAtStartOfDay();
        this.setText(dt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));
    }

    public long getDate() {
        return dt.getMillis();
    }

    public void setDate(long date) {
        dt = new DateTime().withMillis(date).withTimeAtStartOfDay();
        this.setText(dt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));
    }
}