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
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;

public class MazdaRaiseSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Mazda3BinarytekFragment";

    private static final Node[] NODES = {

            // // settings mazda3 2020 v1.14

            new Node("mazda3_2020_settings_c", 0xA40b01, 0x7408, 0x80, 0x1), new Node("mazda3_2020_settings_d", 0xA40b02, 0x7408, 0x60, 0x1), new Node("mazda3_2020_settings_e", 0xA40b03, 0x7408, 0x18, 0x1), new Node("safe_reset", 0xA40b04, 0, 0, 0),

            new Node("mazda3_2020_settings_10", 0xA40701, 0x7405, 0x80, 0x1), new Node("mazda3_2020_settings_11", 0xA40702, 0x7405, 0x60, 0x1), new Node("mazda3_2020_settings_12", 0xA40703, 0x7405, 0x18, 0x1), new Node("speed_warning_reset", 0xA40704, 0, 0, 0),

            new Node("homeinmode", 0x8381, 0x4107, 0x70, 0x0), new Node("homeoutmode", 0x8380, 0x4107, 0x80, 0x0), new Node("backwindowdefog", 0x8316, 0x4105, 0x08, 0x0), new Node("mazda3_2020_settings_1", 0x8317, 0x4105, 0x07, 0x0), new Node("mazda3_2020_settings_2", 0xA40801, 0x7407, 0x80, 0x1), new Node("mazda3_2020_settings_3", 0xA40902, 0x7407, 0x38, 0x1), new Node("psa_simple_17", 0x831a, 0x4106, 0x0c, 0x0), new Node("mazda3_2020_settings_4", 0x8319, 0x4106, 0x30, 0x0), new Node("mazda3_2020_settings_5", 0xA40901, 0x7407, 0x40, 0x1), new Node("mazda3_2020_settings_6", 0xA40a, 0x7407, 0x04, 0x1), new Node("mazda3_2020_settings_8", 0xA40c01, 0x7409, 0x80, 0x1), new Node("mazda3_2020_settings_9", 0xA40c02, 0x7409, 0x40, 0x1), new Node("mazda3_2020_settings_a", 0xA40c03, 0x7409, 0x20, 0x1),

            new Node("rear_view", 0x8318, 0x4106, 0x40, 0x0), new Node("daylight", 0x8385, 0x4107, 0x01, 0x0), new Node("range", 0x8382, 0x4107, 0x08, 0x0), new Node("temp", 0x8383, 0x4107, 0x04, 0x0), new Node("background_lighting", 0x8386, 0x4108, 0xc0, 0x0), new Node("mazda3_2020_settings_7", 0x8313, 0x4105, 0x30, 0x0),

            // // v1.2
            new Node("leavehome", 0x8380, 0x4107, 0x80, 0x0),

            new Node("headlight_off_timer", 0x830d, 0x4104, 0xe0, 0x0),
            //
            new Node("default_all", 0x831b00, 0, 0, 0),
            //
            new Node("auto_light_open", 0x830f, 0x4104, 0x1c, 0x0), new Node("car_light_off", 0x830c, 0x4103, 0x0c, 0x0),
            //
            new Node("adaptive", 0x830e, 0x4103, 0x02, 0x0), new Node("turn_volume", 0x8308, 0x4102, 0x60, 0x0), new Node("three_signal", 0x8307, 0x4102, 0x80, 0x0),
            //
            new Node("automatic_latch", 0x8305, 0x4101, 0x80, 0x0), new Node("lock_mode", 0x8303, 0x4100, 0x04, 0x0),
            //
            new Node("security", 0x8302, 0x4100, 0x18, 0x0),
            //
            new Node("outo_lock_mode", 0x8301, 0x4100, 0xe0, 0x0),
            //
            new Node("lock_beep", 0x8304, 0x4100, 0x03, 0x0),
            //
            new Node("wipers_induction", 0x8311, 0x4105, 0x80, 0x0),
            //
            new Node("reset_avg", 0x8315, 0, 0, 0x0), new Node("sync_avg", 0x8314, 0x4106, 0x80, 0x0),
            //
            // new Node("auto_lights_off2", 0x8409, 0x1, 0x3, 0x0),
            //
            new Node("auto_lights_off", 0x830a, 0x4103, 0xc0, 0x0),

            new Node("auto_lights_off2", 0x830b, 0x4103, 0x30, 0x0),

            new Node("calibration", 0x8402, 0x4003, 0x80, 0x0),

            new Node("active_display", 0x8406, 0x4004, 0x80, 0x0),

            new Node("height", 0x8401, 0x4000, 0xff, 0x0),

            new Node("brightness_control", 0x8404, 0x4001, 0xff, 0x0), new Node("height2", 0x8403, 0x4002, 0xff, 0x0),

            new Node("mazda3_2020_settings_13", 0x8405, 0x4003, 0x7f, 0x0), new Node("mazda3_2020_settings_14", 0x8408, 0x4004, 0x78, 0x0), new Node("display_reset", 0x8407, 0x0, 0x0, 0x0),
            //
            new Node("touch_switch", 0x0, 0x0, 0x0, 0x0),

    };

    private final static int[] INIT_CMDS = {0x9074, 0x9041};

    private Preference[] mPreferences = new Preference[NODES.length];

    private int mType = 0;
    private int mType2 = 0;

    public void setType(int t) {
        mType = t;
        // if (mType == 1) {
        // PreferenceScreen p = (PreferenceScreen)
        // findPreference("driving_mode");
        // if (p != null) {
        // setPreferenceScreen(p);
        // }
        // }
    }

    public void setType2(int t) {
        mType2 = t;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

        String[] ss = mCanboxType.split(",");
        for (int i = 1; i < ss.length; ++i) {
            if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE2)) {
                try {
                    mType2 = Integer.valueOf(ss[i].substring(1));
                } catch (Exception e) {

                }
            }
        }

        addPreferencesFromResource(R.xml.mazda_raise_setting);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

        if (mType2 != 1) {
            getPreferenceScreen().removePreference(findPreference("text_360"));
        }

        int i = SystemConfig.getIntProperty(getActivity(), SystemConfig.KEY_CANBOX_TOUCH_PANNEL);
        ((SwitchPreference) findPreference("touch_switch")).setChecked(i == 0);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

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

        setPreference("reset_avg", 1);
        setPreference("default_all", 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                // sendCanboxInfo((msg.what & 0xff00) >> 8, msg.what & 0xff);
                byte[] buf = new byte[]{(byte) ((msg.what & 0xff00) >> 8), 0x01, (byte) (msg.what & 0xff)};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        if ((cmd & 0xff0000) != 0) {
            sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
        } else {
            sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
        }

    }

    private void sendCanboxData(int cmd) {
        if ((cmd & 0xff0000) != 0) {
            sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));
        } else {
            sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));
        }
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                } else if (preference instanceof SwitchPreference) {
                    if (NODES[i].mType == Node.TYPE_CUSTOM) {
                        sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    } else if (key.equals("touch_switch")) {
                        SystemConfig.setIntProperty(getActivity(), SystemConfig.KEY_CANBOX_TOUCH_PANNEL, ((Boolean) newValue) ? 0x0 : 0x1);
                        ((SwitchPreference) preference).setChecked(((Boolean) newValue));
                    } else {
                        if ("mazda3_2020_settings_2".equals(key) || "mazda3_2020_settings_5".equals(key) || "mazda3_2020_settings_8".equals(key) || "mazda3_2020_settings_9".equals(key) || "mazda3_2020_settings_a".equals(key) || "mazda3_2020_settings_c".equals(key) || "mazda3_2020_settings_10".equals(key)) {
                            sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x2 : 0x1);
                        } else if ("mazda3_2020_settings_6".equals(key)) {
                            sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x2);
                        } else {
                            sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                        }
                    }
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
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
        if (arg0.getKey().equals("individual_reset")) {
            sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("speeddata")) {
            // sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("default_all")) {
            sendCanboxInfo(0x83, 0x1b, 0x00);
            Util.doSleep(50);
            sendCanboxInfo(0x83, 0x6, 0x00);
            Util.doSleep(50);
            sendCanboxInfo(0x83, 0x9, 0x00);
            Util.doSleep(50);
            sendCanboxInfo(0x83, 0x10, 0x00);
            // Util.doSleep(10);
            // sendCanboxInfo(0x83, 0x1b, 0x00);
        } else {
            try {
                udpatePreferenceValue(arg0, null);
            } catch (Exception e) {

            }
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2, int d3) {
        byte[] buf = new byte[]{(byte) d0, 0x03, (byte) d1, (byte) d2, (byte) d3};
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
                // Log.d("aa", key+":"+((ListPreference)
                // findPreference(key)).getEntry());
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 0 ? false : true);
            }
        }
    }

    private int getStatusValue(byte[] buf, int mask) {

        int value = 0;
        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }

        value = 0;
        if (buf.length > 3) {
            value = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value |= ((buf[5] & 0xff) << 24);
        }

        // } catch (Exception e) {
        // value = 0;
        // }

        return ((value & mask) >> start);
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
        int value;
        int index;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = (NODES[i].mStatus & 0xff) + 2;
            mask = (NODES[i].mMask & 0xff);

            if (cmd == buf[0]) {
                int result = buf[index];
                value = getStatusValue1(result, mask);// result & mask;
                if ("turn_volume".equals(NODES[i].mKey)) {
                    if (value == 1) {
                        value = 2;
                    } else if (value == 2) {
                        value = 1;
                    }
                } else if ("daylight".equals(NODES[i].mKey)) {
                    if (value == 1) {
                        value = 0;
                    } else if (value == 0) {
                        value = 1;
                    }
                }
                setPreference(NODES[i].mKey, value);
            }
            // else if (NODES[i].mShow == 1 && buf[0] == 0x0d) {
            //
            // mask = (NODES[i].mMask);
            // int result = buf[NODES[i].mStatus + 2];
            //
            // value = getStatusValue1(result, mask);// result & mask;
            // setPreference(NODES[i].mKey, value);
            // }

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
                                Log.d("aa", "!!!!!!!!" + e);
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
