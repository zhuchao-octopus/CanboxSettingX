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
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.utils.Node;

public class Mazda3XinbasiSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Mazda3BinarytekFragment";

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

    private static final Node[] NODES = {

            new Node("headlight_off_timer", 0x840c, 0x3, 0x38, 0x0),

            new Node("default_all", 0x8418, 0x0, 0x0, 0x0),


            new Node("monitor_volume", 0x8415, 0x2, 0x3, 0x0),

            new Node("intelligent_brake", 0x8414, 0x2, 0x8, 0x0),

            new Node("auto_light_open", 0x840d, 0x3, 0x7, 0x0), new Node("car_light_off", 0x840b, 0x3, 0xc0, 0x0),

            new Node("adaptive", 0x840a, 0x2, 0x80, 0x0), new Node("turn_volume", 0x8407, 0x1, 0x10, 0x0), new Node("three_signal", 0x8406, 0x1, 0x20, 0x0),

            new Node("automatic_latch", 0x8405, 0x1, 0x40, 0x0), new Node("lock_mode", 0x8404, 0x1, 0x80, 0x0),

            new Node("security", 0x8403, 0x0, 0x3, 0x0),

            new Node("outo_lock_mode", 0x8402, 0x0, 0x70, 0x0),

            new Node("lock_beep", 0x8401, 0x0, 0x0c, 0x0),

            new Node("wipers_induction", 0x8400, 0x0, 0x80, 0x0),


            new Node("reset_avg", 0x8417, 0x2, 0x4, 0x0), new Node("sync_avg", 0x8416, 0x2, 0x4, 0x0),

            new Node("auto_lights_off2", 0x8409, 0x1, 0x3, 0x0),

            new Node("auto_lights_off", 0x8408, 0x1, 0xc, 0x0),

            new Node("calibration", 0x840f, 0x2, 0x40, 0x0),

            new Node("navigation", 0x8412, 0x2, 0x10, 0x0),

            new Node("active_display", 0x8411, 0x2, 0x20, 0x0),

            new Node("height", 0x840e, 0x4, 0xff, 0x0), new Node("brightness_control", 0x8410, 0x5, 0xff, 0x0), new Node("height2", 0x8413, 0x6, 0xff, 0x0),


            //settings mazda3 2020 v1.14
            new Node("homeinmode", 0x8419, 0x0, 0xe0, 0x1), new Node("homeoutmode", 0x841a, 0x0, 0x10, 0x1), new Node("backwindowdefog", 0x841b, 0x0, 0x08, 0x1), new Node("mazda3_2020_settings_1", 0x841c, 0x0, 0x07, 0x1), new Node("mazda3_2020_settings_2", 0x841d, 0x1, 0x80, 0x1), new Node("mazda3_2020_settings_3", 0x841e, 0x1, 0x70, 0x1), new Node("psa_simple_17", 0x8420, 0x1, 0x06, 0x1), new Node("mazda3_2020_settings_4", 0x8421, 0x2, 0xc0, 0x1), new Node("mazda3_2020_settings_5", 0x8422, 0x1, 0x08, 0x1), new Node("mazda3_2020_settings_6", 0x8423, 0x2, 0x20, 0x1),
            //v1.2
            new Node("leavehome", 0x8430, 0x7, 0x80, 0x0),};

    private final static int[] INIT_CMDS = {0x8309, 0x830d};

    private Preference[] mPreferences = new Preference[NODES.length];

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

        if (mType2 == 1) {
            addPreferencesFromResource(R.xml.mazda3_2020_xinbasi_setting);
        } else {
            addPreferencesFromResource(R.xml.mazda3_xinbasi_setting);
        }

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

        // findPreference("speeddata").setOnPreferenceClickListener(this);
        // for (Node node : NODES) {
        // String s = node.mKey;
        // if (findPreference(s) != null) {
        // findPreference(s).setOnPreferenceChangeListener(this);
        // }
        // }

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

        if (mType == 1) {
            PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
            if (p != null) {
                setPreferenceScreen(p);
            }
        }

        setPreference("reset_avg", 1);
        setPreference("default_all", 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mType = 0;
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
                byte[] buf = new byte[]{(byte) ((msg.what & 0xff00) >> 8), 0x02, (byte) (msg.what & 0xff), 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

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
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
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

        int mask;
        int value;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mShow == 0 && buf[0] == 0x09) {

                if ("reset_avg".equals(NODES[i].mKey)) {
                    setPreference(NODES[i].mKey, 1);
                    continue;
                }
                if ("default_all".equals(NODES[i].mKey)) {
                    setPreference(NODES[i].mKey, 1);
                    continue;
                }

                mask = (NODES[i].mMask);
                int result = buf[NODES[i].mStatus + 2];

                value = getStatusValue1(result, mask);// result & mask;
                setPreference(NODES[i].mKey, value);
            } else if (NODES[i].mShow == 1 && buf[0] == 0x0d) {
                mask = (NODES[i].mMask);
                int result = buf[NODES[i].mStatus + 2];

                value = getStatusValue1(result, mask);// result & mask;
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
