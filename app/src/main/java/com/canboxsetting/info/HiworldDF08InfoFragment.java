package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.view.MyPreference;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class HiworldDF08InfoFragment extends PreferenceFragmentCompat {
    private static final String TAG = "KadjarRaiseFragment";
    private final static int[] INIT_CMDS = {0xc3};
    private boolean mPaused = true;
    private MyPreference averagefuel, total_mileage, averagespeed, battery_voltage, current, temp, sos, acc, rev, front_cover, tail_box, ribackdoor, lfbackdoor, driver_s_door, passenger_door, double_flashing_lights, left_turnning_light, right_turnning_light, instantaneous_speed, gear, steering_wheel_corners, enginespeed;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.hiworld_df08_info);
        initView();
    }

    private void initView() {
        averagefuel = findPreference("averagefuel");
        total_mileage = findPreference("total_mileage");
        averagespeed = findPreference("averagespeed");
        battery_voltage = findPreference("battery_voltage");
        current = findPreference("dongfeng_current");
        temp = findPreference("temp");
        sos = findPreference("sos");
        acc = findPreference("acc");
        rev = findPreference("rev");
        front_cover = findPreference("front_cover");
        tail_box = findPreference("tail_box");
        ribackdoor = findPreference("ribackdoor");
        lfbackdoor = findPreference("lfbackdoor");
        driver_s_door = findPreference("driver_s_door");
        passenger_door = findPreference("passenger_door");
        double_flashing_lights = findPreference("double_flashing_lights");
        left_turnning_light = findPreference("left_turnning_light");
        right_turnning_light = findPreference("right_turnning_light");
        instantaneous_speed = findPreference("instantaneous_speed");
        gear = findPreference("gear");
        steering_wheel_corners = findPreference("steering_wheel_corners");
        enginespeed = findPreference("enginespeed");
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        mPaused = false;
        requestInitData();
        updateInfo();
    }

    private void updateInfo() {
        byte[] buf13 = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0x13, (byte) 0x85
        };
        MMLog.d(TAG, "updateInfo13: buf = " + ByteUtils.BuffToHexStr(buf13));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf13);
        byte[] buf32 = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0x32, (byte) 0xA4
        };
        MMLog.d(TAG, "updateInfo32: buf = " + ByteUtils.BuffToHexStr(buf32));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf32);
        byte[] buf1A = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0x1A, (byte) 0x8C
        };
        MMLog.d(TAG, "updateInfo1A: buf = " + ByteUtils.BuffToHexStr(buf1A));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf1A);
    }

    private void updateView(byte[] buf) {
        MMLog.d(TAG, "updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf[2] == 0x13) {
            double carFuelValue = (((buf[3] & 0xFF) << 8) | (buf[4] & 0xFF)) / 10D;
            averagefuel.setSummary(carFuelValue + " L/100km");
            double totalMileageValue = (((buf[5] & 0xFF) << 16) | ((buf[6] & 0xFF) << 8) | (buf[7] & 0xFF)) / 10D;
            total_mileage.setSummary(totalMileageValue + "  km");
            int averageSpeedValue = ((buf[11] & 0xFF) << 8) | (buf[12] & 0xFF);
            averagespeed.setSummary(averageSpeedValue + " km/h");
        } else if (buf[2] == 0x1A) {
            acc.setSummary((buf[3] & 0x01) > 0 && (buf[4] & 0x01) > 0 ? "ON" : "OFF");
            rev.setSummary((buf[3] & 0x20) > 0 && (buf[4] & 0x02) > 0 ? "ON" : "OFF");
            front_cover.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x04) > 0 ? getString(R.string.open) : getString(R.string.close));
            tail_box.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x08) > 0 ? getString(R.string.open) : getString(R.string.close));
            ribackdoor.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x10) > 0 ? getString(R.string.open) : getString(R.string.close));
            lfbackdoor.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x20) > 0 ? getString(R.string.open) : getString(R.string.close));
            driver_s_door.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x80) > 0 ? getString(R.string.open) : getString(R.string.close));
            passenger_door.setSummary((buf[3] & 0x40) > 0 && (buf[4] & 0x40) > 0 ? getString(R.string.open) : getString(R.string.close));
            double_flashing_lights.setSummary((buf[3] & 0x02) > 0 && (buf[5] & 0x04) > 0 ? getString(R.string.open) : getString(R.string.close));
            left_turnning_light.setSummary((buf[3] & 0x02) > 0 && (buf[5] & 0x02) > 0 ? getString(R.string.open) : getString(R.string.close));
            right_turnning_light.setSummary((buf[3] & 0x02) > 0 && (buf[5] & 0x01) > 0 ? getString(R.string.open) : getString(R.string.close));
            instantaneous_speed.setSummary((buf[3] & 0x04) > 0 ? (buf[6] << 8 | (buf[7] & 0xFF)) + " km/h" : getString(R.string.device_info_default));
            gear.setSummary((buf[3] & 0x08) > 0 ? getGearName(buf[8]) : getString(R.string.close));
            short corners = (short) ((buf[9] << 8) | (buf[10] & 0xFF));
            steering_wheel_corners.setSummary((buf[3] & 0x10) > 0 ? corners == 0 ? getString(R.string.go_straight) : corners > 0 ? getString(R.string.turn_right) + corners + "°" : getString(R.string.turn_left) + Math.abs(corners) + "°" : getString(R.string.device_info_default));
            enginespeed.setSummary((buf[3] & 0x04) > 0 ? ((buf[12] << 8) | buf[13]) != 0xFFFF ? ((buf[12] << 8) | (buf[13] & 0xFF)) + " r/min" : getString(R.string.device_info_default) : getString(R.string.device_info_default));
        } else if (buf[2] == 0x32) {
            double batteryVoltageValue = (buf[9] & 0xFF) / 10D;
            battery_voltage.setSummary(batteryVoltageValue + " V");
            double currentValue = (buf[10] & 0xFF) / 10D;
            current.setSummary(currentValue + " A");
            double tempValue = (buf[11] & 0xFF) / 2D - 40;
            temp.setSummary(tempValue + " °C");
            sos.setSummary(buf[12] + " %");
        } else if (buf[2] == 0x0F) {

        }
    }

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

    private String getGearName(byte type) {
        String name = getString(R.string.bwm_invalid);
        switch (type) {
            case 0x01:
                name = getString(R.string.p_gear);
                break;
            case 0x02:
                name = getString(R.string.n_gear);
                break;
            case 0x03:
                name = getString(R.string.r_gear);
                break;
            case 0x04:
                name = getString(R.string.d_gear);
                break;
            case 0x05:
                name = getString(R.string.s_gear);
                break;
            case 0x06:
                name = getString(R.string.l_gear);
                break;
            case 0x11:
                name = getString(R.string.manual_1st_gear);
                break;
            case 0x12:
                name = getString(R.string.manual_2st_gear);
                break;
            case 0x13:
                name = getString(R.string.manual_3st_gear);
                break;
            case 0x14:
                name = getString(R.string.manual_4st_gear);
                break;
            case 0x15:
                name = getString(R.string.manual_5st_gear);
                break;
            case 0x16:
                name = getString(R.string.manual_6st_gear);
                break;
        }
        return name;
    }
}
