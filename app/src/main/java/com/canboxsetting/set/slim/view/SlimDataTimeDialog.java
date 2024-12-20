package com.canboxsetting.set.slim.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.canboxsetting.R;
import com.zhuchao.android.fbase.MMLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SlimDataTimeDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "SlimAVMQuittingSpeedDia";
    private ImageButton ok,cancel;
    private Calendar calendar;
    private WheelView selectYear;
    private WheelView selectMonth;
    private WheelView selectDay;
    private WheelView selectHour;
    private WheelView selectMinute;
    private List<String> yearList;
    private List<String> monthList;
    private List<String> dayList;
    private List<String> hourList;
    private List<String> minuteList;
    private OnDataTimeListener listener;
    public SlimDataTimeDialog(@NonNull Context context, OnDataTimeListener listener) {
        this(context,R.style.dialog,listener);
        this.listener = listener;
    }

    public SlimDataTimeDialog(@NonNull Context context, int themeResId, OnDataTimeListener listener) {
        super(context, themeResId);
        this.listener = listener;
        init();
    }
    private void init() {
        setContentView(R.layout.slim_data_time_dialog_layout);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            // 设置宽度和高度
            layoutParams.width = 1200; // 或者指定宽度值，例如 300
            layoutParams.height = 525; // 或者指定高度值
            window.setAttributes(layoutParams);
        }
        calendar = Calendar.getInstance();
        yearList = new ArrayList<>();
        for (int i = 2019;i<= 2078;i++) {
            yearList.add(i + "");
        }
        monthList = new ArrayList<>();
//        for (int i = 1;i<= 12;i++) {
//            String month = getContext().getString(getContext().getResources().getIdentifier("month_" + i,"id",getContext().getPackageName()));
//            MMLog.d(TAG, "init: month = " + month);
//            monthList.add(month);
//        }
        monthList.add(getContext().getString(R.string.month_1));
        monthList.add(getContext().getString(R.string.month_2));
        monthList.add(getContext().getString(R.string.month_3));
        monthList.add(getContext().getString(R.string.month_4));
        monthList.add(getContext().getString(R.string.month_5));
        monthList.add(getContext().getString(R.string.month_6));
        monthList.add(getContext().getString(R.string.month_7));
        monthList.add(getContext().getString(R.string.month_8));
        monthList.add(getContext().getString(R.string.month_9));
        monthList.add(getContext().getString(R.string.month_10));
        monthList.add(getContext().getString(R.string.month_11));
        monthList.add(getContext().getString(R.string.month_12));
        dayList = new ArrayList<>();
        for (int i = 1;i<=31;i++) {
            dayList.add(i + "");
        }
        hourList = new ArrayList<>();
        for (int i = 0;i < 24;i++) {
            hourList.add(i + "");
        }
        minuteList = new ArrayList<>();
        for (int i =0;i < 60;i++) {
            minuteList.add(i + "");
        }
        selectYear = findViewById(R.id.data_yeas);
        selectYear.setData(yearList);
        selectMonth = findViewById(R.id.data_month);
        selectMonth.setData(monthList);
        selectDay = findViewById(R.id.data_day);
        selectDay.setData(dayList);
        selectHour = findViewById(R.id.data_hour);
        selectHour.setData(hourList);
        selectMinute = findViewById(R.id.data_minute);
        selectMinute.setData(minuteList);
        updateDate(calendar);
        ok = findViewById(R.id.dialog_ok);
        ok.setOnClickListener(this);
        cancel = findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(this);
        selectMonth.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                Log.d(TAG, "endSelect: id = " + id + "   text" + text);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    private void updateDate(Calendar calendar) {
        printCalendar(calendar);
        Log.d(TAG, "updateDate: year = " + calendar.get(Calendar.YEAR));
        if (yearList.size() > calendar.get(Calendar.YEAR) - 2019 && calendar.get(Calendar.YEAR) - 2019 >= 0) selectYear.setDefault(calendar.get(Calendar.YEAR) - 2019);
        int currMonth = calendar.get(Calendar.MONTH);
        selectMonth.setDefault(currMonth);
//        Log.d(TAG, "updateDate: month = " + currMonth);
//        Log.d(TAG, "updateDate: countDay = " + countDay);
        selectDay.setData(dayList);
        selectDay.setDefault(calendar.get(Calendar.DAY_OF_MONTH)-1);
//        Log.d(TAG, "updateDate: day = " + calendar.get(Calendar.DAY_OF_MONTH));
        selectHour.setDefault(calendar.get(Calendar.HOUR_OF_DAY));
//        Log.d(TAG, "updateDate: hour = " + calendar.get(Calendar.HOUR_OF_DAY));
        selectMinute.setDefault(calendar.get(Calendar.MINUTE));
//        Log.d(TAG, "updateDate: Minute = " + calendar.get(Calendar.MINUTE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                if (listener != null) {
                    calendar.set(Calendar.YEAR,selectYear.getSelected() + 2019);
                    calendar.set(Calendar.MONTH,selectMonth.getSelected());
                    calendar.set(Calendar.DAY_OF_MONTH,selectDay.getSelected() + 1);
                    calendar.set(Calendar.HOUR_OF_DAY,selectHour.getSelected());
                    calendar.set(Calendar.MINUTE,selectMinute.getSelected());
                    listener.onDataTimeListener(false,calendar);
                }
                dismiss();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        updateDate(calendar);
    }

    public interface OnDataTimeListener{
        void onDataTimeListener(boolean isOpen, Calendar calendar);
    }

    public static void printCalendar(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());
        System.out.println("Current date and time: " + dateString);
    }
}
