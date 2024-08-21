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

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.Util;

public class ToyotaSetingHiworldFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "ToyotaSetingHiworldFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {
            //空调&灯光
            new Node("inner_outer_auto", 0x6A0107, 0x62010000, 0x0100, 0, Node.TYPE_BUFF1),

            new Node("air_auto", 0x6A0106, 0x62010000, 0x0200, 0, Node.TYPE_BUFF1),

            new Node("auto_adjust_head_light_sensity", 0x6A0301, 0x62010000, 0x07000000, 0, Node.TYPE_BUFF1),

            new Node("inner_lights_close_time", 0x6A0302, 0x62010000, 0x18000000, 0, Node.TYPE_BUFF1),

            new Node("outside_lights_close_time", 0x6A0303, 0x62010000, 0x60000000, 0, Node.TYPE_BUFF1),

            new Node("radar_display", 0x6A0108, 0x62010000, 0x80, 0, Node.TYPE_BUFF1),

            new Node("radar_distance", 0x6A010A, 0x62010000, 0x03, 0, Node.TYPE_BUFF1),

            new Node("alarm_volume", 0x6A0109, 0x62010000, 0x70, 0, Node.TYPE_BUFF1),

            new Node("front_radar_distance", 0x6A010A, 0x62010000, 0x0C, 0, Node.TYPE_BUFF1),
            //车锁设置
            new Node("auto_lock_by_speed", 0x6A0101, 0x62010000, 0x4000, 0, Node.TYPE_BUFF1),

            new Node("cardoorautomatic", 0x6A0105, 0x62010000, 0x0400, 0, Node.TYPE_BUFF1),

            new Node("plinkage", 0x6A0104, 0x62010000, 0x0800, 0, Node.TYPE_BUFF1),

            new Node("unlock_double_operate", 0x6A0204, 0x62010000, 0x400000, 0, Node.TYPE_BUFF1),

            new Node("lock_unlock_blink", 0x6A0201, 0x62010000, 0x800000, 0, Node.TYPE_BUFF1),

            new Node("auto_lock_and_one_key_start", 0x6A0202, 0x62010000, 0x200000, 0, Node.TYPE_BUFF1),

            new Node("smartdoorlock", 0x6A0102, 0x62010000, 0x2000, 0, Node.TYPE_BUFF1),

            new Node("unlock_driver_door_open", 0x6A0103, 0x62010000, 0x1000, 0, Node.TYPE_BUFF1),

            new Node("unlock_double_key", 0x6A0203, 0x62010000, 0x100000, 0, Node.TYPE_BUFF1),
            //原车设置
            //	new Node("rear_system_lock", 0x0000, 0x0000, 0, 0,
            //			Node.TYPE_BUFF1),
            //
            //	new Node("rear_row_picture", 0x0000, 0x0000, 0, 0,
            //			Node.TYPE_BUFF1),
            //
            //	new Node("language", 0x9a01, 0x0000, 0x0000, 0,
            //			Node.TYPE_BUFF1),
            //功放设置
            new Node("speed_linkage_volume", 0xAD07, 0xA6020000, 0x020000, 0, Node.TYPE_BUFF1),

            new Node("surround_volume", 0xAD08, 0xA6020000, 0x010000, 0, Node.TYPE_BUFF1),};

    private final static int[] INIT_CMDS = {
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.toyota_hiworld_settings);

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

    private void sendCanboxData2(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        int buf = 0;
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    if (preference.getKey().equals("language")) {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    } else {
                        if (preference.getKey().equals("radar_distance")) {
                            sendCanboxData2(NODES[i].mCmd, 0x02);

                        } else if (preference.getKey().equals("front_radar_distance")) {
                            sendCanboxData2(NODES[i].mCmd, 0x01);
                        } else {
                            sendCanboxData2(NODES[i].mCmd, Integer.parseInt((String) newValue));
                        }
                    }
                } else if (preference instanceof SwitchPreference) {
                    if (preference.getKey().equals("speed_linkage_volume") || preference.getKey().equals("surround_volume")) {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    } else {
                        sendCanboxData2(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
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
        try {
            udpatePreferenceValue(arg0, null);
        } catch (Exception e) {

        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{0x01, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x02, (byte) d0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2, int d3) {
        byte[] buf = new byte[]{0x03, (byte) d0, (byte) d1, (byte) d2, (byte) d3};
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
            }
        }
    }

    private int getStatusValue(byte[] buf, int mask, int subpostion) {

        int value = 0;
        int value1 = 0;
        int value2 = 0;
        int value3 = 0;
        int value4 = 0;
        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }
        value1 = 0;
        if (buf.length > 3) {
            value1 = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value1 |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value1 |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value1 |= ((buf[5] & 0xff) << 24);
        }

        value2 = 0;
        if (buf.length > 7) {
            value2 = ((buf[6] & 0xff) << 0);
        }
        if (buf.length > 8) {
            value2 |= ((buf[7] & 0xff) << 8);
        }
        if (buf.length > 9) {
            value2 |= ((buf[8] & 0xff) << 16);
        }
        if (buf.length > 10) {
            value2 |= ((buf[9] & 0xff) << 24);
        }

        value3 = 0;
        if (buf.length > 11) {
            value3 = ((buf[10] & 0xff) << 0);
        }
        if (buf.length > 12) {
            value3 |= ((buf[11] & 0xff) << 8);
        }
        if (buf.length > 13) {
            value3 |= ((buf[12] & 0xff) << 16);
        }
        if (buf.length > 14) {
            value3 |= ((buf[13] & 0xff) << 24);
        }

        value4 = 0;
        if (buf.length > 15) {
            value4 = ((buf[14] & 0xff) << 0);
        }
        if (buf.length > 16) {
            value4 |= ((buf[15] & 0xff) << 8);
        }
        if (buf.length > 17) {
            value4 |= ((buf[16] & 0xff) << 16);
        }
        if (buf.length > 18) {
            value4 |= ((buf[17] & 0xff) << 24);
        }
        switch (subpostion) {
            case 0: {
                value = ((value1 & mask) >>> start);
                break;
            }
            case 1: {
                value = ((value1 & mask) >>> start);
                break;
            }
            case 2: {
                value = ((value2 & mask) >>> start);
                break;
            }
            case 3: {
                value = ((value3 & mask) >>> start);
                break;
            }
            case 4: {
                value = ((value4 & mask) >>> start);
                break;
            }

        }
        return value;
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
        int subpostion;
        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff000000) >>> 24;
            subpostion = (NODES[i].mStatus & 0xff0000) >>> 16;
            Log.d("hhxg", "buf" + Util.byte2HexStr(buf));
            Log.d("hhxg", "cmd" + cmd);
            if (cmd == (buf[0] & 0xff)) {
                mask = (NODES[i].mMask);
                value = getStatusValue(buf, mask, subpostion);
                Log.d("hhxg", "aaaaaa" + Util.byte2HexStr(buf));
                setPreference(NODES[i].mKey, value);
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
