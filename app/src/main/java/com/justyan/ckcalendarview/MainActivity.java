package com.justyan.ckcalendarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.justyan.library.utils.CalendarUtil;
import com.justyan.library.view.CKCalendarView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CKCalendarView calendarView = (CKCalendarView) findViewById(R.id.calendarView);
        calendarView.setRange("2015-01-01","2019-12-31");
        calendarView.setOnShrinkListener(new CKCalendarView.OnShrinkListener() {
            @Override
            public void onShrink(CKCalendarView ckCalendarView, boolean shrink) {
                Toast.makeText(MainActivity.this, "shrink is " + shrink, Toast.LENGTH_SHORT).show();
            }
        });
        calendarView.setOnDayClickListener(new CKCalendarView.OnDayClickListener() {
            @Override
            public void onDayClick(CKCalendarView ckCalendarView, Calendar calendar) {
                Toast.makeText(MainActivity.this, CalendarUtil.getDefaultFormat().format(calendar.getTime()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
