package com.canboxsetting.set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.common.utils.Node;
import com.common.util.Util;
import com.common.view.MyPreferenceSeekBar;


public class Set174 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "HondaSettingHiworldFragment";
    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {
            ////里程设置
            new Node("adjust_outside_temp", 0x6d01, 0x68010000, 0x0f00, 0, Node.TYPE_BUFF1),

            new Node("tripa_reset_time", 0x7a01, 0x75010000, 0x0300, 0, Node.TYPE_BUFF1),

            new Node("tripb_reset_time", 0x7a02, 0x75010000, 0x0c00, 0, Node.TYPE_BUFF1),
            //
            //////灯光设置
            new Node("il_dimming_time", 0x6C01, 0x67010000, 0x0300, 0, Node.TYPE_BUFF1),

            new Node("hl_auto_off_time", 0x6C02, 0x67010000, 0x0C00, 0, Node.TYPE_BUFF1),

            new Node("auto_light_sensitivity", 0x6C03, 0x67010000, 0x7000, 0, Node.TYPE_BUFF1),
            //
            //////车锁设置
            new Node("keyless_unlock_answer_back", 0x6a04, 0x66010000, 0x4000, 0, Node.TYPE_BUFF1),

            new Node("security_relock_timer", 0x6a03, 0x65010000, 0x3000, 0, Node.TYPE_BUFF1),

            new Node("auto_door_unlock_with", 0x6D05, 0x65010000, 0xC000, 0, Node.TYPE_BUFF1),

            new Node("auto_door_lock_with", 0x6D04, 0x65010000, 0x3000, 0, Node.TYPE_BUFF1),

            new Node("switch_lock", 0x6F0A, 0x69010000, 0x80, 0, Node.TYPE_BUFF1),
            //
            //////无线锁设置
            new Node("keyless_access_beep", 0x6B04, 0x66010000, 0x0800, 0, Node.TYPE_BUFF1),

            new Node("keyless_access_light_flash", 0x6B03, 0x66010000, 0x0400, 0, Node.TYPE_BUFF1),

            new Node("remote_start_system", 0x6B05, 0x66010000, 0x01000, 0, Node.TYPE_BUFF1),

            new Node("auto_interior_illumination_sensitivity", 0x6C04, 0x67010000, 0x07, 0, Node.TYPE_BUFF1),
            //////车身设置
            new Node("adjust_alarm_volume", 0x6F04, 0x69010000, 0x03, 0, Node.TYPE_BUFF1),

            new Node("fuel_efficiency_backlight", 0x6F05, 0x69010000, 0x04, 0, Node.TYPE_BUFF1),

            new Node("new_message_notifications", 0x6F07, 0x69010000, 0x10, 0, Node.TYPE_BUFF1),

            new Node("tachometer", 0x6F06, 0x69010000, 0x20, 0, Node.TYPE_BUFF1),

            new Node("walk_away_auto_lock", 0x6D03, 0x65010000, 0x0800, 0, Node.TYPE_BUFF1),

            new Node("headlight_wiper", 0x6C05, 0x67010000, 0x08, 0, Node.TYPE_BUFF1),
            ////驾驶辅助系统设置
            new Node("voice_alarm_system_volume", 0x6B02, 0x66010000, 0x0200, 0, Node.TYPE_BUFF1), new Node("engine_automatic_start_stop_tips", 0x6F08, 0x69010000, 0x08, 0, Node.TYPE_BUFF1), new Node("acc_detection_prompt_tone", 0x6E02, 0x68010000, 0x0400, 0, Node.TYPE_BUFF1), new Node("tone_of_pause_lkas", 0x7b03, 0x68010000, 0x0800, 0, Node.TYPE_BUFF1), new Node("set_risk_distance_head_of_car", 0x6E01, 0x68010000, 0x0300, 0, Node.TYPE_BUFF1), new Node("driver_attention_monitor", 0x6E07, 0x68010000, 0xC000, 0, Node.TYPE_BUFF1),

            new Node("static_boot_line", 0xF20D, 0xE8020000, 0x010000, 0, Node.TYPE_BUFF1), new Node("dynamic_boot_line", 0xF20D, 0xE8020000, 0x020000, 0, Node.TYPE_BUFF1), new Node("rear_camera", 0xF20D, 0xE8020000, 0x040000, 0, Node.TYPE_BUFF1), new Node("rear_view_dynamic_reminder_system_settings", 0xF20E, 0xE8020000, 0xFF00, 0, Node.TYPE_BUFF1), new Node("head_up_warning", 0x6E08, 0x68010000, 0x01, 0, Node.TYPE_BUFF1), new Node("turn_off_with_the_steering_light", 0xF20C, 0xE8010000, 0x01000000, 0, Node.TYPE_BUFF1),

            new Node("the_duration_is_displayed_after_the_turn_off", 0xF20C, 0xE8010000, 0x0C000000, 0, Node.TYPE_BUFF1), new Node("lane_offset_suppression_system", 0x6E09, 0x68010000, 0x0E, 0, Node.TYPE_BUFF1), new Node("minor_lane_departure_system_set", 0x6E04, 0x68010000, 0x3000, 0, Node.TYPE_BUFF1), new Node("parking_space_width", 0xF20D, 0xE8020000, 0x080000, 0, Node.TYPE_BUFF1),
            //系统设置
            new Node("reset_main_info", 0x6E0601, 0x00000000, 0x0000, 0, Node.TYPE_BUFF1), new Node("traffic_sign_recognition_system", 0x6F0D, 0x69010000, 0x8000, 0, Node.TYPE_BUFF1), new Node("electronic_preload_belt_set", 0x6F0C, 0x64010000, 0x010000, 0, Node.TYPE_BUFF1), new Node("deflation_warning_system", 0x4B0401, 0x00000000, 0x0000, 0, Node.TYPE_BUFF1), new Node("restore", 0x6E0501, 0x00000000, 0x0000, 0, Node.TYPE_BUFF1), new Node("reverse_tone", 0x6F09, 0x69010000, 0x40, 0, Node.TYPE_BUFF1), new Node("electric_tail_door_remote_opening_condition_setting", 0x7A01, 0x75010000, 0x0100, 0, Node.TYPE_BUFF1), new Node("electric_rear_door_outer_handle_for_electric_open", 0x7A02, 0x75010000, 0x0200, 0, Node.TYPE_BUFF1), new Node("driving_posture_set_personalized_memory_location_linkage", 0x6F0B, 0x64010000, 0x020000, 0, Node.TYPE_BUFF1),};

    private final static int[] INIT_CMDS = {
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.sprior_daojun_settings);
        findPreference("restore").setOnPreferenceClickListener(this);
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
                    if ("parking_space_width".equals(key)) {
                        int value = Integer.parseInt((String) newValue) + 6;
                        sendCanboxData(NODES[i].mCmd, value);
                    } else if ("the_duration_is_displayed_after_the_turn_off".equals(key)) {
                        int value = Integer.parseInt((String) newValue) + 4;
                        sendCanboxData(NODES[i].mCmd, value);
                    } else {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    }
                } else if (preference instanceof SwitchPreference) {
                    if ("dynamic_boot_line".equals(key)) {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 3 : 2);
                    } else if ("rear_camera".equals(key)) {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 5 : 4);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }
                } else if (preference instanceof MyPreferenceSeekBar) {
                    try {
                        if ("adjust_outside_temp".equals(key)) {
                            int value = Integer.parseInt((String) newValue) + 4;
                            sendCanboxData(NODES[i].mCmd, value);
                        } else {
                            int v = Integer.valueOf((String) newValue);
                            //进行相关的处理
                            Log.d("v", "!!" + (int) v);
                        }
                    } catch (Exception e) {

                    }
                    Log.d("ffck", "!!" + (String) newValue);
                } else if (preference instanceof PreferenceScreen) {
                    if ("restore".equals(key)) {
                        Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.confirmation_factory_settings).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendCanboxData(0x6e0300);
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();

                    } else if ("reset_main_info".equals(key)) {
                        Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.setreset).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendCanboxData(0x6E0601);
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
                    } else if ("deflation_warning_system".equals(key)) {
                        Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_deflation_warning_system).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendCanboxData(0x6e0100);
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
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
            if (p instanceof MyPreferenceSeekBar) {
                if ("adjust_outside_temp".equals(key)) {
                    index = index - 4;
                    p.setSummary(index + "");
                }

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
