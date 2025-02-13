package com.canboxsetting.set.slim;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.set.slim.utils.SlimCanUtils;
import com.canboxsetting.set.slim.view.SlimDataTimeDialog;
import com.common.utils.BroadcastUtil;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.Calendar;

public class SlimClockSettingFragment extends MyFragment implements View.OnClickListener {
    private static final String TAG = "SlimClockSettingFragment";
    private TextView time12, time24;
    private RelativeLayout timeSelect;
    private TextClock settingTime;
    private SlimDataTimeDialog dataTimeDialog;
    private byte[] settingAllData;
    private ImageView backPage;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slim_clock_setting_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        time12 = view.findViewById(R.id.time_type_12);
        time12.setOnClickListener(this);
        time24 = view.findViewById(R.id.time_type_24);
        time24.setOnClickListener(this);
        time12.setSelected(getTimeFormat());
        time24.setSelected(!getTimeFormat());
        timeSelect = view.findViewById(R.id.manial_adjustment);
        timeSelect.setOnClickListener(this);
        settingTime = view.findViewById(R.id.setting_time);
        settingTime.setOnClickListener(this);
        backPage = view.findViewById(R.id.back_page);
        backPage.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        settingAllData = SlimCanUtils.getInstance().getSettingData();
        //        if (settingAllData != null) updateTimeFormat(settingAllData[12]);
        sendCanboxInfo((byte) 0x00, (byte) 0x1C);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.time_type_12:
                //                set24HourFormat(false);
                //                time12.setSelected(getTimeFormat());
                //                time24.setSelected(!getTimeFormat());
                sendCanboxInfo((byte) 0x9C, (byte) 12);
                break;
            case R.id.time_type_24:
                //                set24HourFormat(true);
                //                time12.setSelected(getTimeFormat());
                //                time24.setSelected(!getTimeFormat());
                sendCanboxInfo((byte) 0x9C, (byte) 24);
                break;
            case R.id.manial_adjustment:
                if (dataTimeDialog == null) dataTimeDialog = new SlimDataTimeDialog(getActivity(), dataTimeListener);
                dataTimeDialog.show();
                break;
            case R.id.back_page:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    public boolean getTimeFormat() {
        String timeFormat = Settings.System.getString(getActivity().getContentResolver(), Settings.System.TIME_12_24);
        boolean is12HourFormat = timeFormat.equals("12");
        MMLog.d(TAG, "getTimeFormat: is12HourFormat = " + is12HourFormat);
        return is12HourFormat;
    }

    private void sendCanboxInfo(byte d0, byte d1) {
        byte[] buf = new byte[]{0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)};
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateTimeFormat(byte value) {
        time12.setSelected(value == 12);
        time24.setSelected(value == 24);
        set24HourFormat(value == 24);
    }

    public void updateView(byte[] buf) {
        MMLog.d(TAG, "updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf != null && buf.length >= 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x1C:
                    //                    time12.setSelected(buf[4] == 12);
                    //                    time24.setSelected(buf[4] == 24);
                    //                    set24HourFormat(buf[4] == 24);
                    updateTimeFormat(buf[4]);
                    break;
                case 0x1D:
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, buf[4] + 2000);
                    calendar.set(Calendar.MONTH, buf[5] - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, buf[6]);
                    calendar.set(Calendar.HOUR_OF_DAY, buf[7]);
                    calendar.set(Calendar.MINUTE, buf[8]);
                    setTimeToCalendar(calendar);
                    break;
            }
        }
    }

    public void set24HourFormat(boolean is24Hour) {
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            Settings.System.putString(resolver, Settings.System.TIME_12_24, is24Hour ? "24" : "12");
        } catch (Exception e) {
            Log.e("TimeFormatChanger", "Error setting 24 hour format", e);
        }
    }

    private final SlimDataTimeDialog.OnDataTimeListener dataTimeListener = new SlimDataTimeDialog.OnDataTimeListener() {
        @Override
        public void onDataTimeListener(boolean isOpen, Calendar calendar) {
            MMLog.d(TAG, "onDataTimeListener: Year = " + calendar.get(Calendar.YEAR) + "   Month = " + calendar.get(Calendar.MONTH) + "   Day = " + calendar.get(Calendar.DAY_OF_MONTH) + "   Hour = " + calendar.get(Calendar.HOUR_OF_DAY) + "   Minute = " + calendar.get(Calendar.MINUTE));
            //            setTimeToCalendar(calendar);
            byte y = (byte) (calendar.get(Calendar.YEAR) % 100);
            Log.d(TAG, "onDataTimeListener: y  = " + y);
            byte mon = (byte) (calendar.get(Calendar.MONTH) + 1);
            byte d = (byte) calendar.get(Calendar.DAY_OF_MONTH);
            byte h = (byte) calendar.get(Calendar.HOUR_OF_DAY);
            byte m = (byte) calendar.get(Calendar.MINUTE);
            byte[] buf = new byte[]{0x00, (byte) 0xA4, 0x01, 0x00, 0x07, (byte) 0x9D, y, mon, d, h, m, 0, (byte) (0xAC + 0x9D + y + mon + d + h + m)};
            Log.d(TAG, "onDataTimeListener: buf = " + ByteUtils.BuffToHexStr(buf));
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        }
    };

    private void setTimeToCalendar(Calendar calendar) {
        long timeInMillis = calendar.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setTime(timeInMillis);
    }
}
