package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.utils.Util;

public class Set184 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "KadjarRaiseSettingFragment";

    private static final Node[] NODES = {

            new Node("top_light_delayed_time", 0x8304, 0x3100, 0xf0, 3), new Node("go_home_delayed_time", 0x8305, 0x3100, 0xf, 3), new Node("economize_on_electricity", 0x8306, 0x3101, 0xf0, 3), new Node("rain_light_sensor_settings", 0x8307, 0x3101, 0x8, 1), new Node("headlamp_brightness_setting", 0x8308, 0x3101, 0x4, 1), new Node("automatic_folding_of_rearview_mirror_key", 0x8309, 0x3101, 0x2),


            new Node("trailer_settings", 0x8319, 0x3103, 0x10), new Node("headlight_mode_key", 0x830c, 0x3101, 0x1), new Node("seat_memory_key", 0x830d, 0x3102, 0x80), new Node("parking_setting_key", 0x830f, 0x3102, 0x40), new Node("antitheft_prevention_key", 0x830e, 0x3102, 0x30), new Node("gate_setting_key", 0x8310, 0x3102, 0x8), new Node("electric_sidestepping_system_key", 0x8311, 0x3102, 0x4), new Node("roof_mode_key", 0x8312, 0x3102, 0x2), new Node("full_terrain_key", 0x8313, 0x3102, 0x1), new Node("precollision_warning_system_key", 0x8317, 0x3103, 0x02), new Node("automatic_emergency_braking_system_key", 0x8318, 0x3103, 0x01), new Node("intelligent_start_and_stop_key", 0x830b, 0x3103, 0x20),


            new Node("fatigue_driving_warning", 0x833c, 0x3105, 0x10), new Node("early_warning_sensitivity", 0x8332, 0x3104, 0x30),


            new Node("driving_buttocks_massage", 0x8426, 0x2308, 0xf0), new Node("copilot_buttocks_massage", 0x8427, 0x2308, 0x0f),


            new Node("driving_waist_massage", 0x8428, 0x2309, 0xf0), new Node("side_drive_massage", 0x8429, 0x2309, 0x0f),

    };

    private final static int[] INIT_CMDS = {0x9031};

    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxData2(msg.what, 0);
            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.changchengh9_settings_od);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            // Log.d("aa", mPreferences[i]+":"+NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

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

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], i * 100);
        }
    }

    private void sendCanboxData2(int cmd, int value) {

        byte[] buf = new byte[]{(byte) ((cmd & 0xff00) >> 8), 0x02, (byte) (cmd & 0xff), (byte) value};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxData(int cmd, int value) {

        byte[] buf = new byte[]{(byte) ((cmd & 0xff00) >> 8), 0x03, (byte) (cmd & 0xff), (byte) value, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxData(int cmd) {

        byte[] buf = new byte[]{(byte) ((cmd & 0xff0000) >> 16), 0x02, (byte) ((cmd & 0xff00) >> 8), (byte) (cmd & 0xff)};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    int value = Integer.parseInt((String) newValue);
                    if ((NODES[i].mShow & 1) != 0) {
                        value++;
                    }
                    sendCanboxData(NODES[i].mCmd, value);

                } else if (preference instanceof SwitchPreference) {
                    if ((NODES[i].mShow & 1) != 0) {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x2 : 0x1);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }
                } else if (preference instanceof PreferenceScreen) {
                    if ((NODES[i].mCmd & 0xff00) >> 8 == 0x84) {
                        byte[] buf = new byte[]{(byte) ((NODES[i].mCmd & 0xff00) >> 8), 0x02, (byte) (NODES[i].mCmd & 0xff), 1};
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                        Util.doSleep(200);
                        buf[3] = 0;
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                    } else {
                        sendCanboxData(NODES[i].mCmd);
                    }
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

    public boolean onPreferenceClick(Preference arg0) {

        try {
            udpatePreferenceValue(arg0, null);
        } catch (Exception e) {

        }

        return false;
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
                sp.setChecked(index == 0 ? false : true);
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

        try {
            int cmd;
            int param;
            int mask;
            int value;

            for (int i = 0; i < NODES.length; ++i) {
                cmd = (NODES[i].mStatus & 0xff00) >> 8;
                param = (NODES[i].mStatus & 0xff);

                if ((buf[0] & 0xff) == cmd) {
                    if (param < (buf.length - 2)) {
                        mask = (NODES[i].mMask);
                        value = getStatusValue1(buf[param + 2], mask);
                        if ((NODES[i].mShow & 0x2) != 0) {
                            value--;
                        }
                        setPreference(NODES[i].mKey, value);
                    }
                    // break;
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "err" + e);
        }
    }

    private void showPreference(String id, int show) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            if (show != 0) {
                if (findPreference(id) == null) {
                    getPreferenceScreen().addPreference(preference);
                }
            } else {
                if (findPreference(id) != null) {
                    getPreferenceScreen().removePreference(preference);
                }
            }
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

}
