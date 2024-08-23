package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.util.Util;

public class Set122 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {


            new NodePreference("auto_lock_mode", 0x7e01, 0x7807, 0xe0, 0x780280, R.array.auto_lock_mode_entries, R.array.twelve_values),

            new NodePreference("headlight_auto_off", 0x7e02, 0x7807, 0x18, 0x780240, R.array.enauto_lights_off, R.array.twelve_values),

            new NodePreference("off_car_lock", 0x7e03, 0x7807, 0x04, 0x780220),

            new NodePreference("keyless_access_beep_vol", 0x7e04, 0x7807, 0x03, 0x780210, R.array.volume_as_fast_entries, R.array.twelve_values), new NodePreference("security_relock_timer", 0x7e05, 0x7808, 0xc0, 0x780208, R.array.honda_security_relock_timer_entries, R.array.twelve_values), new NodePreference("unlock_mode", 0x7e06, 0x7808, 0x20, 0x780204, R.array.auto_unlock_entries, R.array.twelve_values),

            new NodePreference("three_signal", 0x7e07, 0x7808, 0x10, 0x780202), new NodePreference("turn_volume", 0x7e08, 0x7808, 0x08, 0x780201, R.array.steering_signal_volume_entries, R.array.twelve_values),

            new NodePreference("wipers_induction", 0x7e09, 0x7808, 0x04, 0x780380),

            new NodePreference("auto_lights_off3", 0x7e0a, 0x7808, 0x03, 0x780340, R.array.enauto_lights_off2, R.array.twelve_values),

            new NodePreference("automatic_headlight_sensitivity", 0x7e0b, 0x7809, 0xe0, 0x780320, R.array.automatic_headlight_opening_entries, R.array.twelve_values),


            new NodePreference("car_light_off", 0x7e0c, 0x7809, 0x1c, 0x780310, R.array.seat_heat_values, R.array.twelve_values),

            new NodePreference("adaptive_front_lightion_system", 0x7e0d, 0x7809, 0x02, 0x780308),


            new NodePreference("mazda3_2020_settings_d", 0x7e1b, 0x780a, 0x80, 0x780401),


            new NodePreference("blindSpot_monitor_volume", 0x7e13, 0x780a, 0x03, 0x780480, R.array.seat_heat_values, R.array.twelve_values),


            new NodePreference("hendlight_light_off_time", 0x7e14, 0x780b, 0xe0, 0x780440, R.array.enheadlight_off_timer, R.array.twelve_values),

            new NodePreference("leaving_home_light", 0x7e24, 0x780e, 0x80, 0), new NodePreference("daytime_running_light", 0x7e25, 0x780e, 0x40, 0),


            new NodePreference("distance_unit", 0x7e26, 0x780e, 0x20, 0, R.array.mileage_unit_t, R.array.twelve_values),

            new NodePreference("temperature_unit", 0x7e27, 0x780e, 0x10, 0, R.array.temperature_unit, R.array.twelve_values),};

    private final static int[] INIT_CMDS = {0x78};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_setting);

        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    boolean add = true;
                    if (((NODES[i].mType & 0xff0000) >> 16) != 0) {
                        int index = ((NODES[i].mType & 0xff00) >> 8) + 2;
                        if ((mVisible[index] & NODES[i].mType) == 0) {
                            add = false;
                        }
                    }

                    if (add) {
                        ((PreferenceScreen) ps).addPreference(p);
                    }

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    }
                }

            }
        }
    }

    private void removeAll() {
        getPreferenceScreen().removeAll();
    }

    private byte[] mVisible = new byte[]{0x78, 0, 0, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

    private boolean mPaused = true;

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPaused = false;
        registerListener();
        requestInitData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(msg.what & 0xff);
            }
        }
    };


    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, Integer.parseInt((String) newValue));
                } else if (preference instanceof SwitchPreference) {

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 0x1 : 0x0);

                }
                break;
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception e) {

        }
        return false;
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x4, (byte) d0, 0xa, 0x0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof ListPreference) {
                ListPreference lp = (ListPreference) p;
                CharSequence[] ss = lp.getEntries();
                if (ss != null && (ss.length > index)) {
                    lp.setValue(String.valueOf(index));
                }
                lp.setSummary("%s");
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 1 ? true : false);
            }
        }
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private int getStatusValue1(int value, int mask) {

        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }

        // } catch (Exception e) {
        // value = 0;
        // }

        return ((value & mask) >> start);
    }

    private void updateView(byte[] buf) {

        int cmd;
        int mask;
        int index;
        if (buf[0] == 0x78) {
            if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4]) || (mVisible[3] != buf[5])) {
                Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
                removeAll();
                init();
            }
        }
        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = NODES[i].mStatus & 0xff;
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd) {
                mask = (NODES[i].mMask);
                int value = getStatusValue1(buf[2 + index], mask);
                setPreference(NODES[i].mKey, value);
            }

        }

    }

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
                                Log.d(TAG, "updateView:Exception " + e);
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
