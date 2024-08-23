package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;

public class MitsubishiSettingsSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "MitsubishiSettingsSimpleFragment";
    private static final Node[] NODES = {

            new Node("car_type_cmd", 0x0, 0x0, 0x0, 0x0),

            new Node("mitsubishi_settings_1", 0xC601, 0x40000000, 0x00e0, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_2", 0xC602, 0x40000000, 0x000c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_3", 0xC610, 0x40000000, 0x01c0, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_4", 0xC611, 0x40000000, 0x0120, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_5", 0xC612, 0x40000000, 0x0110, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_6", 0xC613, 0x40000000, 0x010c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_7", 0xC614, 0x40000000, 0x0102, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_8", 0xC620, 0x40000000, 0x0280, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_9", 0xC621, 0x40000000, 0x0270, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_10", 0xC622, 0x40000000, 0x020e, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_11", 0xC623, 0x40000000, 0x03c0, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_12", 0xC624, 0x40000000, 0x0330, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_13", 0xC625, 0x40000000, 0x0307, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_14", 0xC630, 0x40000000, 0x0480, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_15", 0xC631, 0x40000000, 0x0440, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_16", 0xC632, 0x40000000, 0x0420, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_17", 0xC640, 0x40000000, 0x0418, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_18", 0xC641, 0x40000000, 0x0404, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_19", 0xC642, 0x40000000, 0x0403, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_20", 0xC650, 0x40000000, 0x0580, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_21", 0xC651, 0x40000000, 0x0540, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_22", 0xC652, 0x40000000, 0x0530, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_23", 0xC653, 0x40000000, 0x050c, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_24", 0xC654, 0x40000000, 0x0502, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_25", 0xC660, 0x40000000, 0x06c0, 0x0, Node.TYPE_BUFF1_INDEX), new Node("mitsubishi_settings_26", 0xC661, 0x40000000, 0x0630, 0x0, Node.TYPE_BUFF1_INDEX),

            new Node("mitsubishi_settings_0", 0xC600),

    };

    private final static int[] INIT_CMDS = {0x1A00, 0x1E00, 0x4101, 0x4102, 0x4203
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};

    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.mitsublishi_settings);

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
        sendCanboxInfo(0x90, 0x40, 0x0);
        mPaused = false;
        registerListener();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        if ("car_type_cmd".equals(key)) {
            sendCanboxInfo(0xca, Integer.parseInt((String) newValue));

            ((ListPreference) preference).setValue(String.valueOf((String) newValue));
            preference.setSummary("%s");
        } else {

            for (int i = 0; i < NODES.length; ++i) {
                if (NODES[i].mKey.equals(key)) {
                    if (preference instanceof ListPreference) {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    }
                    // else if (preference instanceof SwitchPreference) {
                    // if (NODES[i].mType == Node.TYPE_CUSTOM) {
                    // sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    // } else {
                    // sendCanboxData(NODES[i].mCmd,
                    // ((Boolean) newValue) ? 0x1 : 0x0);
                    // }
                    //
                    // if (key.equals("ctm_system")) {
                    // mSetCTM = (((Boolean) newValue) ? 0x1 : 0x0);
                    // }
                    // } else if (preference instanceof PreferenceScreen) {
                    // sendCanboxData(NODES[i].mCmd);
                    // }
                    // break;
                }
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
        if (arg0.getKey().equals("mitsubishi_settings_0")) {
            sendCanboxInfo(0xc6, 0x00, 0x01);
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, 0x0};
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
        if (buf.length > 2) {
            value = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 3) {
            value |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 4) {
            value |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 5) {
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
        if ((buf[0] & 0xff) == 0x40) {

            int cmd;
            int mask;
            int value;

            for (int i = 0; i < NODES.length; ++i) {
                cmd = (NODES[i].mStatus & 0xff000000) >> 24;

                if (NODES[i].mType == Node.TYPE_BUFF1_INDEX) {
                    if ((buf[0] & 0xff) == cmd) {
                        mask = (NODES[i].mMask & 0xff);
                        int index = ((NODES[i].mMask & 0xff00) >> 8);
                        value = getStatusValue1(buf[2 + index], mask);
                        setPreference(NODES[i].mKey, value);
                        // break;
                    }
                }

            }
        }

    }

    private void showPreference(String id, int show, String parant) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            PreferenceScreen ps;
            if (parant != null) {
                ps = (PreferenceScreen) findPreference(parant);
            } else {
                ps = getPreferenceScreen();
            }
            if (ps != null) {
                if (show != 0) {
                    if (ps.findPreference(id) == null) {
                        ps.addPreference(preference);
                    }
                } else {
                    if (findPreference(id) != null) {
                        boolean b = ps.removePreference(preference);
                        // Log.d("dd", "" + b);
                    }
                }
            }
        }

    }

    private void showPreference(String id, int show) {
        showPreference(id, show, "driving_mode");

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
