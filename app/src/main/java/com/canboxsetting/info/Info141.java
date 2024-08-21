package com.canboxsetting.info;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.common.view.LineGraphicView;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

public class Info141 extends MyFragment {
    private static final String TAG = "FiatFragment";

    View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.beiqi_electric_car_infos, container, false);
        initView();
        return mMainView;
    }

    private LineGraphicView mLineGraphicView;
    private ArrayList<Double> mYList;

    int[] mData = new int[50];

    private void updateLineGraphicView() {
        if (mLineGraphicView != null) {
            for (int i = 0; i < mData.length; ++i) {
                mYList.set(i, (double) (mData[i]));
            }
            mLineGraphicView.setData(mYList);
            mLineGraphicView.invalidate();
        }
    }

    private void initView() {

        showPage(R.id.page0);

        mLineGraphicView = (LineGraphicView) mMainView.findViewById(R.id.line_graphic);
        if (mLineGraphicView != null) {

            mYList = new ArrayList<Double>();

            for (int i = 0; i < mData.length; ++i) {
                mYList.add(0.0);
            }
            ArrayList<String> xRawDatas = new ArrayList<String>();

            //
            // xRawDatas.add(getString(R.string.eq_text_80));
            xRawDatas.add("0");
            xRawDatas.add("20");
            xRawDatas.add("40");
            xRawDatas.add("60");
            xRawDatas.add("80");
            xRawDatas.add("100");

            // int i = getResources().getInteger(R.integer.graphic_view_h2);
            mLineGraphicView.setData(mYList, xRawDatas, 60, 1);
            updateLineGraphicView();
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.charging_settings) {
            showPage(R.id.page0);
        } else if (id == R.id.energy_information) {
            showPage(R.id.page1);
        } else if (id == R.id.energy_statistics) {
            showPage(R.id.page2);
        } else if (id == R.id.gs4_energy_recovery1) {
            sendCanboxData(0x3, 0);
        } else if (id == R.id.gs4_energy_recovery2) {
            sendCanboxData(0x3, 1);
        } else if (id == R.id.gs4_energy_recovery3) {
            sendCanboxData(0x3, 2);
        } else if (id == R.id.energy_recovery_i_pedal1) {
            sendCanboxData(0x4, 1);
        } else if (id == R.id.energy_recovery_i_pedal2) {
            sendCanboxData(0x4, 2);
        } else if (id == R.id.cancel) {
            showPage(R.id.page1);
        } else if (id == R.id.time_of_appointment1) {
            (new TimePickerDialog(getActivity(), onTimeSetListener, 0, 0, true)).show();
        } else if (id == R.id.time_of_appointment2) {
            (new TimePickerDialog(getActivity(), onTimeSetListenerEnd, 0, 0, true)).show();
        } else if (id == R.id.ok) {
            setReserveTime();
        } else if (id == R.id.charging_mode1) {
            sendCanboxData(0x1, 1);
            v.setSelected(true);
        } else if (id == R.id.warning_cancel) {
            mMainView.findViewById(R.id.warning_layout).setVisibility(View.GONE);
        }
    }

    private void setReserveTime() {
        if (hStart >= hEnd || (hStart == hEnd && mStart >= mEnd)) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.set_charging_time_conflict).setNegativeButton(android.R.string.cancel, null).show();
        } else {

            byte[] buf = new byte[]{(byte) 0xa9, 0x6, 2, 0, 0, 0, 0, 0};

            buf[3] = (byte) ((hStart & 0x3f) | ((mStart & 0x03) << 6));
            buf[4] = (byte) ((mStart & 0xfc) >> 2);
            buf[5] = (byte) ((hEnd & 0x3f) | ((mEnd & 0x03) << 6));
            buf[6] = (byte) ((mEnd & 0xfc) >> 2);

            if (((RadioButton) mMainView.findViewById(R.id.cycle_mode1)).isChecked()) {
                buf[7] |= 0x1;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date1)).isChecked()) {
                buf[7] |= 0x2;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date2)).isChecked()) {
                buf[7] |= 0x4;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date3)).isChecked()) {
                buf[7] |= 0x8;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date4)).isChecked()) {
                buf[7] |= 0x10;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date5)).isChecked()) {
                buf[7] |= 0x20;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date6)).isChecked()) {
                buf[7] |= 0x40;
            }
            if (((CheckBox) mMainView.findViewById(R.id.cycle_date7)).isChecked()) {
                buf[7] |= 0x80;
            }
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            showPage(R.id.page1);
            setViewSel(R.id.charging_mode1, false);
        }
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hStart = hourOfDay;
            mStart = minute;
            String s = String.format("%02d:%02d", hStart, mStart, Locale.ENGLISH);

            ((TextView) mMainView.findViewById(R.id.time_of_appointment1)).setText(s);
        }

    };

    TimePickerDialog.OnTimeSetListener onTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hEnd = hourOfDay;
            mEnd = minute;

            String s = String.format("%02d:%02d", hEnd, mEnd, Locale.ENGLISH);

            ((TextView) mMainView.findViewById(R.id.time_of_appointment2)).setText(s);
        }

    };

    private void showPage(int page) {

        setViewVisible(R.id.page0, false);
        setViewVisible(R.id.page1, false);
        setViewVisible(R.id.page2, false);

        setViewVisible(page, true);

        setViewSel(R.id.charging_settings, false);
        setViewSel(R.id.energy_information, false);
        setViewSel(R.id.energy_statistics, false);
        if (page == id.page0) {
            setViewSel(id.charging_settings, true);
        } else if (page == id.page1) {
            setViewSel(id.energy_information, true);
        } else if (page == id.page2) {
            setViewSel(id.energy_statistics, true);
        }
    }

    private void setViewVisible(int id, boolean visible) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void setViewSel(int id, boolean s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s);
        }
    }

    int hStart;
    int mStart;
    int hEnd;
    int mEnd;
    private boolean mPause = false;

    @Override
    public void onPause() {
        super.onPause();
        mPause = true;
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPause = false;

        registerListener();

        byte[] buf = new byte[]{(byte) 0x90, 0x1, 0x40};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        Util.doSleep(10);
        buf[2] = 0x41;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        Util.doSleep(10);
        buf[2] = 0x39;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxData(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xa9, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private int gettree(int v) {
        int ret = 0;
        if (v < 50) {
            ret = 0;
        } else {
            ret = ((v - 50) / 100) + 1;
        }

        return ret;
    }

    private int getenvironmental_grade(int v) {
        int ret = 0;
        if (v >= 50000) {
            ret = 0x14;
        } else if (v >= 30000) {
            ret = 0x13;
        } else if (v >= 20000) {
            ret = 0x12;
        } else if (v >= 15000) {
            ret = 0x11;
        } else if (v >= 12000) {
            ret = 0x10;
        } else if (v >= 10000) {
            ret = 0xf;
        } else if (v >= 9000) {
            ret = 0xe;
        } else if (v >= 8000) {
            ret = 0xd;
        } else if (v >= 7000) {
            ret = 0xc;
        } else if (v >= 6000) {
            ret = 0xb;
        } else if (v >= 5000) {
            ret = 0xa;
        } else if (v >= 3800) {
            ret = 0x9;
        } else if (v >= 3000) {
            ret = 0x8;
        } else if (v >= 2400) {
            ret = 0x7;
        } else if (v >= 1800) {
            ret = 0x6;
        } else if (v >= 1200) {
            ret = 0x5;
        } else if (v >= 800) {
            ret = 0x4;
        } else if (v >= 400) {
            ret = 0x3;
        } else if (v >= 200) {
            ret = 0x2;
        } else if (v >= 100) {
            ret = 0x1;
        } else if (v >= 0) {
            ret = 0x0;
        }

        return ret;
    }

    private int getlevel(int v) {
        int ret = 0;
        if (v >= 0xee) {
            ret = 10;
        } else if (v >= 0xd5) {
            ret = 9;
        } else if (v >= 0xbc) {
            ret = 8;
        } else if (v >= 0xa3) {
            ret = 7;
        } else if (v >= 0x8a) {
            ret = 6;
        } else if (v >= 0x71) {
            ret = 5;
        } else if (v >= 0x58) {
            ret = 4;
        } else if (v >= 0x3f) {
            ret = 3;
        } else if (v >= 0x26) {
            ret = 2;
        } else if (v >= 0xd) {
            ret = 1;
        } else if (v >= 0) {
            ret = 0;
        }

        return ret;
    }

    private void updateView(byte[] buf) {
        String s;
        int index;
        switch (buf[0]) {
            case 0x41:

                for (int i = 0; i < mData.length; i += 2) {
                    mData[i] = ((buf[2 + i] & 0xff) << 8) | (buf[3 + i] & 0xff);
                }

                mMainView.findViewById(R.id.energy_statistics).setVisibility(View.VISIBLE);
                updateLineGraphicView();
                break;
            case 0x40: {

                index = ((buf[2] & 0xff) << 16) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff));
                s = String.format("%d", index);
                ((TextView) mMainView.findViewById(R.id.already_driving)).setText(s);

                int index2 = getenvironmental_grade(index);
                String[] ss = getResources().getStringArray(R.array.baic_environmental_grade);


                if (index2 < ss.length) {
                    ((TextView) mMainView.findViewById(R.id.environmental_grade)).setText(ss[index2]);
                }

                index2 = index / 6;

                ((TextView) mMainView.findViewById(R.id.co2)).setText(index2 + "");

                index2 = gettree(index);

                ((TextView) mMainView.findViewById(R.id.tree)).setText(index2 + "");

                //			index = ((buf[12] & 0xff));
                //			((TextView) mMainView.findViewById(R.id.gs4_energy_recovery))
                //					.setText(index + getString(R.string.level));

                index = ((buf[5] & 0x0f));

                if (index == 2) {
                    ((TextView) mMainView.findViewById(R.id.gs4_energy_recovery_title)).setText(R.string.drive_energy_level);
                } else {
                    ((TextView) mMainView.findViewById(R.id.gs4_energy_recovery_title)).setText(R.string.gs4_energy_recovery);
                }

                s = "";
                switch (index) {
                    case 0:
                        s = getString(R.string.shutdown);
                        break;
                    case 1:
                        s = getString(R.string.ready);
                        break;
                    case 2:
                        s = getString(R.string.driving);
                        break;
                    case 3:
                        s = getString(R.string.energy_recovery);
                        break;
                    case 4:
                        s = getString(R.string.fast_charge_mode);
                        break;
                    case 5:
                        s = getString(R.string.slow_charge_mode);
                        break;
                    case 6:
                        s = getString(R.string.slow_charge_mode_1);
                        break;
                    case 7:
                        s = getString(R.string.discharge_mode);
                        break;
                }

                ((TextView) mMainView.findViewById(R.id.vehicle_status)).setText(s);

                index = ((buf[5] & 0xf0) >> 4);
                if (index >= 1 && index <= 2) {
                    s = "";
                    switch (index) {
                        case 1:
                            s = getString(R.string.please_check);
                            mMainView.findViewById(R.id.warning_cancel).setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            s = getString(R.string.please_overhaul);

                            mMainView.findViewById(R.id.warning_cancel).setVisibility(View.GONE);
                            break;
                    }

                    mMainView.findViewById(R.id.warning_layout).setVisibility(View.VISIBLE);

                    ((TextView) mMainView.findViewById(R.id.warning_msg)).setText(s);
                } else {
                    mMainView.findViewById(R.id.warning_layout).setVisibility(View.GONE);

                }

                ((SeekBar) mMainView.findViewById(R.id.enag)).setProgress(getlevel(buf[6] & 0xff));

            }
            break;
            case 0x39: {

                index = ((buf[2] & 0xff) << 16) | ((buf[3] & 0xff) << 8) | ((buf[4] & 0xff));
                s = String.format("%d", index);
                ((TextView) mMainView.findViewById(R.id.already_driving)).setText(s);

                String[] ss = getResources().getStringArray(R.array.baic_environmental_grade);
                if ((buf[5] & 0xff) < ss.length) {
                    ((TextView) mMainView.findViewById(R.id.environmental_grade)).setText(ss[(buf[5] & 0xff)]);
                }

                index = ((buf[6] & 0xff) << 8) | ((buf[7] & 0xff));

                ((TextView) mMainView.findViewById(R.id.co2)).setText(index + "");

                index = ((buf[8] & 0xff) << 8) | ((buf[9] & 0xff));

                ((TextView) mMainView.findViewById(R.id.tree)).setText(index + "");

                index = ((buf[12] & 0xff));
                ((TextView) mMainView.findViewById(R.id.gs4_energy_recovery)).setText(index + getString(R.string.level));

                index = ((buf[10] & 0x0f));

                if (index == 2) {
                    ((TextView) mMainView.findViewById(R.id.gs4_energy_recovery_title)).setText(R.string.drive_energy_level);
                } else {
                    ((TextView) mMainView.findViewById(R.id.gs4_energy_recovery_title)).setText(R.string.gs4_energy_recovery);
                }

                s = "";
                switch (index) {
                    case 0:
                        s = getString(R.string.shutdown);
                        break;
                    case 1:
                        s = getString(R.string.ready);
                        break;
                    case 2:
                        s = getString(R.string.driving);
                        break;
                    case 3:
                        s = getString(R.string.energy_recovery);
                        break;
                    case 4:
                        s = getString(R.string.fast_charge_mode);
                        break;
                    case 5:
                        s = getString(R.string.slow_charge_mode);
                        break;
                    case 6:
                        s = getString(R.string.slow_charge_mode_1);
                        break;
                    case 7:
                        s = getString(R.string.discharge_mode);
                        break;
                }

                ((TextView) mMainView.findViewById(R.id.vehicle_status)).setText(s);

                index = ((buf[10] & 0xf0) >> 4);
                if (index >= 1 && index <= 2) {
                    s = "";
                    switch (index) {
                        case 1:
                            s = getString(R.string.please_check);
                            mMainView.findViewById(R.id.warning_cancel).setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            s = getString(R.string.please_overhaul);

                            mMainView.findViewById(R.id.warning_cancel).setVisibility(View.GONE);
                            break;
                    }

                    mMainView.findViewById(R.id.warning_layout).setVisibility(View.VISIBLE);

                    ((TextView) mMainView.findViewById(R.id.warning_msg)).setText(s);
                } else {
                    mMainView.findViewById(R.id.warning_layout).setVisibility(View.GONE);

                }

                ((SeekBar) mMainView.findViewById(R.id.enag)).setProgress(buf[11] & 0xff);

            }
            break;
        }
    }

    private boolean mSetSource = false;
    private boolean mPlayStatus = false;

    private BroadcastReceiver mReceiver;

    private void unregisterListener() {
        if (mReceiver != null) {
            this.getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(MyCmd.BROADCAST_SEND_FROM_CAN)) {

                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {

                            try {
                                updateView(buf);
                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + buf);
                            }
                        }
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

            this.getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

}
