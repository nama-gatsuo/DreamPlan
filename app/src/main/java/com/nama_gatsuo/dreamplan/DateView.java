package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by nagamatsuayumu on 15/02/28.
 */
public class DateView extends TextView implements CalendarDatePickerDialog.OnDateSetListener {


    public DateView(Context context) {
        super(context);
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
        DateTime dt = new DateTime().withDate(year, month + 1, day);
        LocalDate ld = dt.toLocalDate();
        this.setText(ld.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));
    }
}