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
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;
import com.common.utils.Node;
import com.common.view.MyPreferenceDialog;
import com.common.view.MyPreferenceSeekBar;

public class ChuanQiSetingsRaiseFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {

    private static final Node[] NODES = {

            new Node("auto_compressor_status", 0x83, 0x52, 0x2), new Node("auto_cycle_mode", 0x83, 0x52, 0x3), new Node("ac_comfort_setting", 0x83, 0x52, 0x4), new Node("anion_mode", 0x83, 0x52, 0x18), new Node("air_quality_sensor", 0x83, 0x52, 0x25), new Node("air_automatic_mode", 0x83, 0x52, 0x26), new Node("key_unlock_ventilation", 0x83, 0x52, 0x3b), new Node("air_conditioning_set_air_conditioning_self_drying", 0x83, 0x52, 0x3c),
            //

            new Node("seat_welcome", 0x83, 0x52, 0x19), new Node("auto_seat_recog", 0x83, 0x52, 0x1a), new Node("auto_heat", 0x83, 0x52, 0x5), new Node("fu_auto_heat", 0x83, 0x52, 0x6),

            //

            new Node("over_speed_alert", 0x83, 0x52, 0x7, 200, 10), new Node("alart_volume", 0x83, 0x52, 0x8), new Node("power_on_time", 0x83, 0x52, 0x9, 30, 1), new Node("start_time", 0x83, 0x52, 0xa, 30, 1), new Node("turn_mode", 0x83, 0x52, 0xb), new Node("right_side_line_assist", 0x83, 0x52, 0x22), new Node("left_side_line_assist", 0x83, 0x52, 0x23), new Node("luggage_sensor_open", 0x83, 0x52, 0x27), new Node("automatic_wiper", 0x83, 0x52, 0x28),
            //

            new Node("remote_unlock", 0x83, 0x52, 0xc), new Node("speed_lock", 0x83, 0x52, 0xd), new Node("auto_unlock", 0x83, 0x52, 0xe), new Node("remote_control_left_skylight", 0x83, 0x52, 0xf), new Node("front_wiper_maintain", 0x83, 0x52, 0x10), new Node("rear_wiper_auto", 0x83, 0x52, 0x11), new Node("outside_rearview", 0x83, 0x52, 0x1b), new Node("unlock_the_lock_tone", 0x83, 0x52, 0x1c), new Node("intelligent_active_locking", 0x83, 0x52, 0x1d), new Node("intelligent_active_unlocking", 0x83, 0x52, 0x1e), new Node("external_mirror_angle_automatic_adjustment", 0x83, 0x52, 0x1f), new Node("external_mirror_angle_manual_adjustment", 0x83, 0x52, 0x20), new Node("automatically_close_the_window", 0x83, 0x52, 0x35),
            //

            new Node("go_home", 0x83, 0x52, 0x12), new Node("foglamp_turn", 0x83, 0x52, 0x13), new Node("daytime_running_lamp", 0x83, 0x52, 0x14), new Node("intelligent_welcome_light", 0x83, 0x52, 0x21), new Node("ambient_light_control", 0x83, 0x52, 0x24), new Node("auto_light_sensitivity", 0x83, 0x52, 0x15), new Node("ambient_light_brightness", 0x83, 0x52, 0x29, 7, 1), new Node("atmosphere_light_color", 0x83, 0x52, 0x2a, 31, 1),
            //

            new Node("factory_reset", 0x83, 0x0, 0x0),};


    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, 0x52, msg.what & 0xff);
            }
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.chuanqi_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else if (mPreferences[i] instanceof MyPreferenceSeekBar) {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                    ((MyPreferenceSeekBar) mPreferences[i]).updateSeekBar(0, NODES[i].mShow, NODES[i].mType);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

        if ((GlobalDef.getModelId() != 28)) {
            ((PreferenceScreen) findPreference("airconditioning_setting")).removePreference(findPreference("key_unlock_ventilation"));
            ((PreferenceScreen) findPreference("airconditioning_setting")).removePreference(findPreference("air_conditioning_set_air_conditioning_self_drying"));
            ((PreferenceScreen) findPreference("accessory")).removePreference(findPreference("automatically_close_the_window"));
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
    }

    private void requestInitData() {
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mMask != 0) {
                mHandler.sendEmptyMessageDelayed(NODES[i].mMask, (i * 50));
            }
        }
    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    int value = Integer.parseInt((String) newValue);

                    if (NODES[i].mShow == 0) {
                        value++;
                    }

                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
                } else if (preference instanceof SwitchPreference) {
                    int value;
                    value = ((Boolean) newValue) ? 0x1 : 0x0;
                    if (NODES[i].mShow == 0) {
                        if (value == 0) {
                            value = 2;
                        }
                    }

                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
                } else if (preference instanceof MyPreferenceSeekBar) {
                    int cmd = Integer.parseInt((String) newValue);
                    if ("over_speed_alert".equals(key)) {
                        cmd = cmd / 10;
                    }
                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, cmd);

                } else if (preference instanceof MyPreferenceDialog) {
                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, 0xff);

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

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index, int show) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof ListPreference) {
                ListPreference lp = (ListPreference) p;

                if (show == 0) {
                    index--;
                }
                CharSequence[] ss = lp.getEntries();
                if (ss != null && (ss.length > index)) {
                    lp.setValue(String.valueOf(index));
                }
                lp.setSummary("%s");
                // Log.d("aa", key+":"+((ListPreference)
                // findPreference(key)).getEntry());
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;

                if (show == 0) {
                    if (index == 2) {
                        index = 0;
                    }
                }
                sp.setChecked(index == 0 ? false : true);
            } else if (p instanceof MyPreferenceSeekBar) {
                if ("over_speed_alert".equals(key)) {
                    index = index * 10;
                }
                p.setSummary(index + "");
            }
        }
    }

    private void updateView(byte[] buf) {

        int value;

        for (int i = 0; i < NODES.length; ++i) {

            if (NODES[i].mStatus == (buf[0] & 0xff) && NODES[i].mMask == (buf[2] & 0xff)) {
                value = buf[3] & 0xff;

                setPreference(NODES[i].mKey, value, NODES[i].mShow);
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
