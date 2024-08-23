package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.util.Util;

public class Set125 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {


            new NodePreference("rear_view_mirror_folding_mode", 0x6d00, 0x6800, 0x02, 0, R.array.automatic_air_conditioning_mode_entries, R.array.twelve_values),

            new NodePreference("gwm_intelligent_start_and_stop", 0x6d01, 0x6800, 0x01, 0),

            //			new NodePreference("left_front_windows", 0x6d02, 0x6800, 0x01, 0),
            //			new NodePreference("left_rear_windows", 0x6d03, 0x6800, 0x01, 0),
            //			new NodePreference("right_front_windows", 0x6d04, 0x6800, 0x01, 0),
            //			new NodePreference("right_rear_windows", 0x6d05, 0x6800, 0x01, 0),
            new NodePreference("fatigue_driving_warning", 0x6d08, 0x6800, 0x04, 0), new NodePreference("collision_safety_assistance", 0x6d09, 0x6801, 0x80, 0), new NodePreference("pedestrian_safety_assistance", 0x6d0a, 0x6801, 0x40, 0), new NodePreference("gwm_early_warning_sensitivity", 0x6d0b, 0x6801, 0x30, 0, R.array.volumeset, R.array.twelve_values), new NodePreference("gac_settings_departure_waring", 0x6d0c, 0x6800, 0x10, 0), new NodePreference("traffic_sign_information", 0x6d0d, 0x6800, 0x08, 0), new NodePreference("parallel_auxiliary", 0x6d0e, 0x6801, 0x08, 0), new NodePreference("gwm_back_side_aid", 0x6d0f, 0x6801, 0x04, 0), new NodePreference("opening_early_warning", 0x6d10, 0x6801, 0x02, 0),

            new NodePreference("instrument_color_setting", 0x6d11, 0x6802, 0x03, 0, R.array.blue_red_gold, R.array.twelve_values), new NodePreference("rain_light_sensor_settings", 0x6d12, 0x6803, 0x02, 0, R.array.rain_light_sensor_settings_entries, R.array.twelve_values), new NodePreference("trailer_settings", 0x6d13, 0x6803, 0x01, 0),

    };

    private final static int[] INIT_CMDS = {0x68};


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
        byte[] buf = new byte[]{0x4, (byte) d0, (byte) d1, (byte) d2, (byte) 0xff, (byte) 0xff};
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
