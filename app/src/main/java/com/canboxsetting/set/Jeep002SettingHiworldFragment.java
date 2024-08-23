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
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.utils.Util;
import com.common.view.MyPreferenceSeekBar;


public class Jeep002SettingHiworldFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Jeep002SettingHiworldFragment";
    private static final Node[] NODES = {
            //单位
            new Node("temp47", 0xCA03, 0xC1010000, 0xFF00, 0, Node.TYPE_BUFF1),

            new Node("consumption", 0xCA05, 0xC1010000, 0xFF0000, 0, Node.TYPE_BUFF1),

            new Node("units", 0xCA07, 0xC1010000, 0xFF000000, 0, Node.TYPE_BUFF1),

            new Node("tireunit", 0xCA06, 0xC1010000, 0xFF, 0, Node.TYPE_BUFF1),

            //安全和驾驶辅助
            new Node("gac_settings_parksense", 0x4405, 0x43010000, 0xC0000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_lane_departure_correction", 0x4404, 0x43010000, 0x30000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_departure_waring", 0x4403, 0x43010000, 0x0C000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_front_collision_alarm_auto_brake", 0x4402, 0x43010000, 0x02000000, 0, Node.TYPE_BUFF1),

            new Node("forward_collision_waring", 0x4401, 0x43010000, 0x01000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_ramp_start_auxiliary", 0x440B, 0x43020000, 0x40, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_rain_sensor_wipper", 0x440A, 0x43020000, 0x20, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_park_view_dynamic_guide_line", 0x4407, 0x43020000, 0x08, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_park_view_static_guide_line", 0x4408, 0x43020000, 0x04, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_busy_point_alarm", 0x4406, 0x43020000, 0x03, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_rear_parksense", 0x440F, 0x43020000, 0x1000, 0, Node.TYPE_BUFF1),

            new Node("b_parksense", 0x440E, 0x43020000, 0x0C00, 0, Node.TYPE_BUFF1),

            new Node("f_parksense", 0x440D, 0x43020000, 0x0300, 0, Node.TYPE_BUFF1),

            new Node("radar_sound_switch", 0x4412, 0x43010000, 0x200000, 0, Node.TYPE_BUFF1),

            new Node("forward_collision_warning_abroad", 0x4413, 0x45010000, 0xC0000000, 0, Node.TYPE_BUFF1),

            new Node("forward_collision_sensitivity_abroad", 0x4414, 0x45010000, 0x30000000, 0, Node.TYPE_BUFF1),

            new Node("side_distance_waring", 0x4415, 0x45010000, 0x0C000000, 0, Node.TYPE_BUFF1),

            new Node("side_distance_warning_volume", 0x4416, 0x45010000, 0x03000000, 0, Node.TYPE_BUFF1),

            new Node("active_parkview_backup", 0x4417, 0x45020000, 0x80, 0, Node.TYPE_BUFF1),

            new Node("traffic_sign_assist", 0x4418, 0x45020000, 0x40, 0, Node.TYPE_BUFF1),

            new Node("greeting_light", 0x4419, 0x45020000, 0x20, 0, Node.TYPE_BUFF1),
            //车灯
            new Node("cornering_lights", 0x630A, 0x62010000, 0x1000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_illuminated_approach", 0x6302, 0x62010000, 0x0c0000, 0, Node.TYPE_BUFF1),

            new Node("headlights_off", 0x6301, 0x62010000, 0x030000, 0, Node.TYPE_BUFF1),

            new Node("car_in_light", 0x630B, 0x62010000, 0xE0000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_flash_lights_w_lock", 0x6306, 0x62010000, 0x10000000, 0, Node.TYPE_BUFF1),

            new Node("day_light", 0x6305, 0x62010000, 0x08000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_anti_glare_auto_high_beam", 0x6304, 0x62010000, 0x02000000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_light_when_wiper", 0x6303, 0x62010000, 0x01000000, 0, Node.TYPE_BUFF1),

            new Node("unlock_open_lamp", 0x6309, 0x62010000, 0x04000000, 0, Node.TYPE_BUFF1),
            //车门和锁
            new Node("gac_settings_horn_when_lock", 0x6106, 0x60010000, 0x6000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_door_alarm", 0x6105, 0x60010000, 0x1000, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_passive_enter", 0x6104, 0x60010000, 0x0800, 0, Node.TYPE_BUFF1),

            new Node("first_press_of_key_unlocks", 0x6103, 0x60010000, 0x0400, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_auto_unlock_on_exit", 0x6102, 0x60010000, 0x0200, 0, Node.TYPE_BUFF1),

            new Node("gac_settings_auto_door_locks", 0x6101, 0x60010000, 0x0100, 0, Node.TYPE_BUFF1),
            //发动机关闭选项
            new Node("gac_settings_engine_off_power_delay", 0x6307, 0x62010000, 0x300000, 0, Node.TYPE_BUFF1),

            //制动
            new Node("retract_park_brake_to_allow_for_brake_system_service", 0x4410, 0x43020000, 0x2000, 0, Node.TYPE_BUFF1), new Node("gac_brake_auto_brake", 0x440C, 0x43020000, 0x80, 0, Node.TYPE_BUFF1),
            //其他设置
            new Node("auto_heat_driver_seat_when_car_start", 0x4411, 0x43020000, 0xC000, 0, Node.TYPE_BUFF1), new Node("rearview_mirror_adjust_lens", 0x3D16, 0x31010000, 0x0100, 0, Node.TYPE_BUFF1),
            //罗盘设置
            new Node("compass_offset_setting", 0x9B00, 0x9C010000, 0xFF00, 0, Node.TYPE_BUFF1),
            //DSP相关的信息
            new Node("speed_compensated_vol", 0xAD07, 0xA6020000, 0x060000, 0, Node.TYPE_BUFF1), new Node("surround_volume", 0xAD08, 0xA6020000, 0x010000, 0, Node.TYPE_BUFF1),};
    private final static int[] INIT_CMDS = {
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};
    private int mType = 0;
    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, msg.what & 0xff);
            }
        }
    };
    private BroadcastReceiver mReceiver;

    public void setType(int t) {
        mType = t;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.jeep002_hiworld_settings);
        //findPreference("restore").setOnPreferenceClickListener(this);
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

    private void sendCanboxData3(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), value);
    }

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));
    }

    private void sendCanboxData2(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
    }

    private void sendCanboxData(int cmd, int param1, int param2) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), param1, param2);
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        int buf = 0;
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                } else if (preference instanceof SwitchPreference) {
                    if (key.equals("rearview_mirror_adjust_lens")) {
                        sendCanboxData(NODES[i].mCmd, 0x1);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }
                } else if (key.equals("atmosphere_light_color")) {
                    sendCanboxData3(NODES[i].mCmd, Integer.parseInt((String) newValue));
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
            } else if (p instanceof MyPreferenceSeekBar) {
                p.setSummary(index + "");
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
            if (cmd == (buf[0] & 0xff)) {
                mask = (NODES[i].mMask);
                value = getStatusValue(buf, mask, subpostion);
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
                                Log.d("abcd", "!!!!!!!!" + Util.byte2HexStr(buf));
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
