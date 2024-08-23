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
import com.common.util.NodePreference;
import com.common.view.MyPreferenceDialog;
import com.common.view.MyPreferenceSeekBar;

public class QiRuiSetingsRaiseFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {

    private static final NodePreference[] NODES = {


            new NodePreference("fortification_prompt", 0xc6, 0x40, 0x1, 0, R.array.chery_notification_entries, R.array.three_values),

            new NodePreference("str_daytime_running_lamp", 0xc6, 0x40, 0x5, 0), new NodePreference("brake_alarm", 0xc6, 0x40, 0x2, 0), new NodePreference("zhonghua_xx", 0xc6, 0x40, 0x3, 0),


            new NodePreference("headlight_delay1", 0xc6, 0x40, 0x4, 0),

            new NodePreference("over_speed", 0xc6, 0x40, 0x9, 0, R.array.over_speed, R.array.thirty_values),


            new NodePreference("backlighting", 0xc6, 0x40, 0xa, 0, R.array.dashboard_brightness_value, R.array.dashboard_brightness_value),

            new NodePreference("steering_auxiliary_lighting", 0xc6, 0x40, 0xb, 0), new NodePreference("str_auto_unlock", 0xc6, 0x40, 0xc, 0), new NodePreference("remote_open_trunk", 0xc6, 0x40, 0xd, 0), new NodePreference("blind_spot_monitoring", 0xc6, 0x40, 0xf, 0), new NodePreference("lane_offset_warning", 0xc6, 0x40, 0x10, 0), new NodePreference("rear_view", 0xc6, 0x40, 0x11, 0), new NodePreference("gesture_enable", 0xc6, 0x40, 0x12, 0), new NodePreference("gesture_skylight", 0xc6, 0x40, 0x13, 0),


            new NodePreference("accompany_me_home", 0xc6, 0x40, 0x14, 0, R.array.qirui1, R.array.eleven_values),

            new NodePreference("strobe_flashing_light", 0xc6, 0x40, 0x15, 0, R.array.turn_signal_oushange, R.array.eleven_values),

            new NodePreference("str_welcome", 0xc6, 0x40, 0x16, 0),


            new NodePreference("electronic_power_mode_selection", 0xc6, 0x40, 0x17, 0, R.array.electronic_power_mode_selection_entries, R.array.eleven_values),

            new NodePreference("blower_opens_ahead_of_schedule", 0xc6, 0x40, 0x19, 0),


            new NodePreference("ambient_light_brightness", 0xc6, 0x40, 0x20, 0, R.array.chery_ambient_light_brightness_entries, R.array.eleven_values),


            new NodePreference("delayed_closure_of_blower", 0xc6, 0x40, 0x1a, 0), new NodePreference("greeting_lights", 0xc6, 0x40, 0x1c, 0), new NodePreference("intelligent_key_sensor_unlock_unlock_lock", 0xc6, 0x40, 0x1d, 0), new NodePreference("ambient_related_driving_modes", 0xc6, 0x40, 0x1e, 0), new NodePreference("ambient_light_music_rhythm", 0xc6, 0x40, 0x1f, 0), new NodePreference("steering_force_mode_associated_driving_mode", 0xc6, 0x40, 0x22, 0), new NodePreference("display_distance_waring", 0xc6, 0x40, 0x24, 0), new NodePreference("gwm_automatic_emergency_braking_system", 0xc6, 0x40, 0x25, 0),


            new NodePreference("adaptive_cruise_system", 0xc6, 0x40, 0x27, 0, R.array.adaptive_cruise_system_entries, R.array.eleven_values),

            new NodePreference("adaptive_cruise_system_last_distance_selected", 0xc6, 0x40, 0x28, 0), new NodePreference("smart_key_sensor_tailgate_opens", 0xc6, 0x40, 0x29, 0), new NodePreference("air_purification", 0xc6, 0x40, 0x2a, 0),


            new NodePreference("language", 0xc6, 0x40, 0x0, 0, R.array.chery_language_status_entries, R.array.two_values),};


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
                Log.d("ffck", i + "i=" + ps + ":" + p);
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    }
                }

            }
        }
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
    }

    private void requestInitData() {
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mMask != 0) {
                mHandler.sendEmptyMessageDelayed(NODES[i].mMask, (i * 10));
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, 0x52, msg.what & 0xff);
            }
        }
    };

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    int value = Integer.parseInt((String) newValue);


                    if ("backlighting".equals(key)) {
                        value++;
                    }
                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
                } else if (preference instanceof SwitchPreference) {
                    int value;
                    value = ((Boolean) newValue) ? 0x1 : 0x0;
                    // if (NODES[i].mShow == 0) {
                    // value ++;
                    // }

                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
                } else if (preference instanceof MyPreferenceSeekBar) {
                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, Integer.parseInt((String) newValue));

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
            } else if (p instanceof MyPreferenceSeekBar) {
                p.setSummary(index + "");
            }
        }
    }

    private void updateView(byte[] buf) {

        int value;

        for (int i = 0; i < NODES.length; ++i) {

            if (NODES[i].mStatus == (buf[0] & 0xff) && NODES[i].mMask == (buf[2] & 0xff)) {
                value = buf[3] & 0xff;
                // if (NODES[i].mShow == 0) {
                // value --;
                // }
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
