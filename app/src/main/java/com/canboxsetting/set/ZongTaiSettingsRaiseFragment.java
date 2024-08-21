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
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.annotation.Nullable;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.view.MyPreferenceSeekBar;

public class ZongTaiSettingsRaiseFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "ZongTaiSettingsRaiseFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private int flow_me_home_value = 0;
    private int flow_me_home_times_value = 1;

    private static final Node[] NODES = {
            //data0
            new Node("seat_courtesy", 0xc693, 0x41000000, 0x04, 0, Node.TYPE_BUFF1),

            new Node("ambient_light", 0xc694, 0x41000000, 0x08, 0, Node.TYPE_BUFF1),

            new Node("ambient_light_brightness", 0xc695, 0x41000000, 0xF0, 0, Node.TYPE_BUFF1),

            new Node("flow_me_home", 0xc696, 0x41000000, 0x0800, 0, Node.TYPE_BUFF1),

            new Node("flow_me_home_times", 0xc696, 0x41000000, 0xF000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_departure_waring", 0xc697, 0x41000000, 0x0400, 0, Node.TYPE_BUFF1),

            new Node("headlight_height", 0xc698, 0x41000000, 0x0300, 0, Node.TYPE_BUFF1),


            //data1
            new Node("ambient_lighting", 0xc699, 0x41000000, 0x800000, 0, Node.TYPE_BUFF1),

            new Node("ambient_lighting_brightness", 0xc69A, 0x41000000, 0x700000, 0, Node.TYPE_BUFF1),

            new Node("ambient_lighting_color", 0xc69B, 0x41000000, 0x0F0000, 0, Node.TYPE_BUFF1),

            new Node("original_car_cd_source_switch", 0xd000, 0x24000000, 0x80000000, 0, Node.TYPE_BUFF1),

            new Node("volume_plus", 0xd000, 0x41000000, 0x0F0000, 0),

            new Node("volume_min", 0xd000, 0x41000000, 0x0F0000, 0),

    };

    private final static int[] INIT_CMDS = {0x41, 0x24,
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.zongtai_raise_settings);

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
                sendCanboxInfo(0x90, msg.what & 0xff);
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
        int buf = 0;
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    if (preference.getKey().equals("original_car_cd_source_switch")) {
                        sendCanboxInfo(0xd0, Integer.parseInt((String) newValue), 0);
                    } else {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    }
                } else if (preference instanceof SwitchPreference) {
                    if (preference.getKey().equals("flow_me_home")) {
                        buf = ((Boolean) newValue) ? 0x1 : 0x0;
                        buf = (buf << 7) | (flow_me_home_times_value & 0x7F);
                        sendCanboxData(NODES[i].mCmd, buf);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }
                } else if (preference instanceof PreferenceScreen) {
                    if (preference.getKey().equals("volume_plus")) {
                        sendCanboxInfo(0xd0, 0, 1);
                    } else if (preference.getKey().equals("volume_min")) {
                        sendCanboxInfo(0xd0, 0, 2);
                    } else {

                        sendCanboxData(NODES[i].mCmd);
                    }
                } else if (preference instanceof MyPreferenceSeekBar) {
                    try {
                        int v = Integer.valueOf((String) newValue);
                        if (preference.getKey().equals("flow_me_home_times")) {
                            v = v / 20;
                            buf = (flow_me_home_value > 0) ? 0x1 : 0x0;
                            buf = (buf << 7) | (v & 0x7F);
                            sendCanboxData(NODES[i].mCmd, buf);
                        } else {
                            sendCanboxData(NODES[i].mCmd, v);
                        }
                    } catch (Exception e) {

                    }

                    Log.d("ffck", "!!" + (String) newValue);
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
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 0 ? false : true);
                if (p.getKey().equals("flow_me_home")) {
                    flow_me_home_value = index;
                }
            } else if (p instanceof MyPreferenceSeekBar) {
                if (p.getKey().equals("flow_me_home_times")) {
                    flow_me_home_times_value = index;
                    index = index * 20;
                }
                p.setSummary(index + "");
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
        return ((value & mask) >> start);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {
        int cmd;
        int mask;
        int value;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals("original_car_cd_source_switch")) {
                setPreference(NODES[i].mKey, (buf[5] & 0x80) >> 7);
            } else {
                cmd = (NODES[i].mStatus & 0xff000000) >> 24;
                if (cmd == (buf[0] & 0xff)) {
                    mask = (NODES[i].mMask);
                    value = getStatusValue(buf, mask);
                    setPreference(NODES[i].mKey, value);
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

    private void showPreferenceEnable(String id, boolean enabled) {
        Preference ps = (Preference) findPreference(id);
        if (ps != null) {
            ps.setEnabled(enabled);
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
