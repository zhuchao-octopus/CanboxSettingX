package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.annotation.Nullable;

import android.provider.Settings;
import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.view.MyPreferenceSeekBar;

public class Set197 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "FocusSettingsFragment";


    private static final NodePreference[] NODES = {
            //group1
            new NodePreference("rain_sensor_wiper", 0xC9, 0x28, 0x60, 1), new NodePreference("repeat_wiper_once", 0xC9, 0x28, 0x61, 1), new NodePreference("rear_wiper", 0xC9, 0x28, 0x62, 1), new NodePreference("tire_pressure_unit", 0xC9, 0x28, 0x70, 1, R.array.tireunit_entries, R.array.brightness_control_value), new NodePreference("degree", 0xC9, 0x28, 0x71, 1, R.array.degree_entries, R.array.brightness_control_value), new NodePreference("temp_dis", 0xC9, 0x28, 0x72, 1, R.array.temp_dis_t, R.array.brightness_control_value),


            //group2
            new NodePreference("active_braking", 0xC9, 0x28, 0x11, 2), new NodePreference("security_alert_sensitivity", 0xC9, 0x28, 0x12, 2, R.array.trumpche_sensitivity_entries, R.array.brightness_control_value),

            new NodePreference("blind_spot_monitoring", 0xC9, 0x28, 0x13, 2), new NodePreference("fatigue_driving_warning", 0xC9, 0x28, 0x14, 2),

            //3
            new NodePreference("lane_keeping_mode", 0xC9, 0x28, 0x0, 3, R.array.lane_keeping_mode_entries, R.array.brightness_control_value), new NodePreference("warning_intensity", 0xC9, 0x28, 0x1, 3, R.array.gwm_early_warning_sensitivity_entries, R.array.brightness_control_value),

            new NodePreference("incoming_reverse_gear_warning", 0xC9, 0x28, 0x2, 3), new NodePreference("tcs_traction_control", 0xC9, 0x28, 0x3, 3), new NodePreference("automatic_engine_shutdown", 0xC9, 0x28, 0x5, 3),

            //4

            new NodePreference("switch_prohibited", 0xC9, 0x28, 0x50, 4), new NodePreference("voice_feedback", 0xC9, 0x28, 0x51, 4), new NodePreference("false_lock_warning", 0xC9, 0x28, 0x52, 4), new NodePreference("remote_unlock", 0xC9, 0x28, 0x53, 4, R.array.door_to_be_unlocked_entries, R.array.brightness_control_value),

            new NodePreference("str_auto_unlock", 0xC9, 0x28, 0x54, 4), new NodePreference("remote_control_on", 0xC9, 0x28, 0x55, 4), new NodePreference("remote_control_off", 0xC9, 0x28, 0x56, 4), new NodePreference("activate_remote_start", 0xC9, 0x28, 0x57, 4), new NodePreference("air_control", 0xC9, 0x28, 0x58, 4, R.array.air_control_entries, R.array.brightness_control_value),

            new NodePreference("key_car_lock_cycle", 0xC9, 0x28, 0x5a, 4, R.array.key_car_lock_cycle_entries, R.array.brightness_control_value),
            //5
            new NodePreference("headlight_delay", 0xC9, 0x28, 0x21, 5, R.array.ford_light_delay, R.array.ford_light_delay_value),

            new NodePreference("automatic_high_beam", 0xC9, 0x28, 0x22, 5), new NodePreference("electric_trunk", 0xC9, 0x28, 0x30, 5), new NodePreference("auto_fold_wing_mirror", 0xC9, 0x28, 0x40, 5),
            //6

            new NodePreference("korea_amp_treble", 0xC3, 0x0, 0x0, 0x06, -7, 7, 1), new NodePreference("korea_amp_middle", 0xC3, 0x1, 0x0, 0x06, -7, 7, 1), new NodePreference("korea_amp_bass", 0xC3, 0x2, 0x0, 0x06, -7, 7, 1), new NodePreference("attenuation", 0xC3, 0x3, 0x0, 0x06, -7, 7, 1), new NodePreference("blance", 0xC3, 0x4, 0x0, 0x06, -7, 7, 1),
            //		new NodePreference("korea_amp_volume", 0xC3, 0x4, 0x0, 0x106, 0, 30, 1),
            new NodePreference("speed_adjustment", 0xC3, 0, 0x5, 6, R.array.speed_compensated_vol_entries, R.array.brightness_control_value), new NodePreference("sound_mode", 0xC3, 0, 0x6, 6, R.array.sound_effect_entries, R.array.brightness_control_value), new NodePreference("allround", 0xC3, 0, 0x7, 6, R.array.orientation_selection, R.array.brightness_control_value),

    };

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {
                int index = NODES[i].mType & 0xff;
                if (index < getPreferenceScreen().getPreferenceCount()) {
                    Preference ps = getPreferenceScreen().getPreference(index);
                    if (ps instanceof PreferenceScreen) {
                        ((PreferenceScreen) ps).addPreference(p);

                        if ((p instanceof ListPreference) || (p instanceof SwitchPreference) || (p instanceof MyPreferenceSeekBar)) {
                            p.setOnPreferenceChangeListener(this);
                        } else {
                            p.setOnPreferenceClickListener(this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.focus_bnr_settings);

        //		init();

        findPreference("information_tone_volume").setOnPreferenceChangeListener(this);

        //		findPreference("warning_contorl").setOnPreferenceChangeListener(this);
        findPreference("warning_tone").setOnPreferenceChangeListener(this);

        //		findPreference("temp_dis_k").setOnPreferenceChangeListener(this);
        findPreference("turn_lights_set").setOnPreferenceChangeListener(this);
        findPreference("mileage_unit").setOnPreferenceChangeListener(this);
        //		findPreference("brightness").setOnPreferenceChangeListener(this);

        findPreference("control_system").setOnPreferenceChangeListener(this);

        //		findPreference("disc_indicator").setOnPreferenceChangeListener(this);
        //		findPreference("acoustic_key").setOnPreferenceChangeListener(this);
        //		findPreference("five_key").setOnPreferenceChangeListener(this);
        findPreference("off_Warning_info").setOnPreferenceChangeListener(this);

        //		findPreference("wipers_induction").setOnPreferenceChangeListener(this);
        //		findPreference("incar_light").setOnPreferenceChangeListener(this);
        //		findPreference("parking_mode").setOnPreferenceChangeListener(this);
        //		findPreference("ramp").setOnPreferenceChangeListener(this);
        //		findPreference("reverse_zoom").setOnPreferenceChangeListener(this);
        //		findPreference("enhanced_assistance").setOnPreferenceChangeListener(
        //				this);
        //		findPreference("tyre_monitor").setOnPreferenceClickListener(this);

        updateView(new byte[]{0x24, 0, 0, 0, 0, 0, 0, 0});

        //		updateView(new byte[] { 0x21, 0, 0, 0, 0, 0, 0, 0 });
        registerListener();

        updateLedSetting();
        udpateShowWarning();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        sendCanboxInfo(0x90, 0x24, 0);
        Util.doSleep(20);
        sendCanboxInfo(0x90, 0x21, 0);
        //		Util.doSleep(20);
        //		sendCanboxInfo(0x90, 0x62, 0);
    }

    private int mLedSeting = 0;
    private final String LED_SETTINGS = "canboxsetting_ledsetting";

    private void updateLedSetting() {
        //		mLedSeting = Settings.System.getInt(getActivity().getContentResolver(),
        //				LED_SETTINGS, 0);
        //		if ((mLedSeting & 0x1) != 0) {
        //			((SwitchPreference) findPreference("disc_indicator"))
        //					.setChecked(true);
        //		}
        //
        //		if ((mLedSeting & 0x2) != 0) {
        //			((SwitchPreference) findPreference("acoustic_key"))
        //					.setChecked(true);
        //		}
        //
        //		if ((mLedSeting & 0x4) != 0) {
        //			((SwitchPreference) findPreference("five_key")).setChecked(true);
        //		}
        //
        //		sendCanboxInfo(0xa2, mLedSeting);
    }

    private void setLedSetting(int i) {
        mLedSeting = i;
        sendCanboxInfo(0xa2, i);
        Settings.System.putInt(getActivity().getContentResolver(), LED_SETTINGS, i);
    }

    private void udpateShowWarning() {
        int show = Settings.System.getInt(getActivity().getContentResolver(), SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG, 0);

        if (show != 0) {
            ((SwitchPreference) findPreference("off_Warning_info")).setChecked(true);
        }

    }

    private void setWarningSetting(boolean b) {
        int i = b ? 1 : 0;

        Settings.System.putInt(getActivity().getContentResolver(), SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG, i);
        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD, SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG);
        this.getActivity().sendBroadcast(it);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        try {
            if ("information_tone_volume".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x6 : 0x5);
            } else if ("warning_tone".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x8 : 0x7);
            } else if ("control_system".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x1 : 0x2);
            } else if ("disc_indicator".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x1;
                } else {
                    mLedSeting &= ~0x1;
                }
                setLedSetting(mLedSeting);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("acoustic_key".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x2;
                } else {
                    mLedSeting &= ~0x2;
                }
                setLedSetting(mLedSeting);

                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("five_key".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x4;
                } else {
                    mLedSeting &= ~0x4;
                }
                setLedSetting(mLedSeting);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("off_Warning_info".equals(key)) {
                setWarningSetting((Boolean) newValue);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("warning_contorl".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x9);
            } else if ("temp_dis_k".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa0, i);
            } else if ("turn_lights_set".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x3);
            } else if ("mileage_unit".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0xe);
            } else if ("brightness".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x10);
            } else if ("wipers_induction".equals(key)) {
                sendCanboxInfo(0xa5, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("incar_light".equals(key)) {
                sendCanboxInfo(0xa6, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("parking_mode".equals(key)) {
                sendCanboxInfo(0xa7, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("ramp".equals(key)) {
                sendCanboxInfo(0xa8, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("reverse_zoom".equals(key)) {
                sendCanboxInfo(0xab, ((Boolean) newValue) ? 0x12 : 0x13);
            } else if ("enhanced_assistance".equals(key)) {
                sendCanboxInfo(0xad, ((Boolean) newValue) ? 0x1 : 0x0);
            } else {
                udpatePreferenceValue(preference, newValue);
            }

        } catch (Exception e) {

        }
        return false;
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, Integer.parseInt((String) newValue));

                } else if (preference instanceof SwitchPreference) {
                    sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, ((Boolean) newValue) ? 0x1 : 0x0);
                } else if (preference instanceof MyPreferenceSeekBar) {
                    try {
                        int v = Integer.valueOf((String) newValue);
                        v = v - NODES[i].mEntry;
                        sendCanboxInfo(NODES[i].mCmd, NODES[i].mStatus, v);
                    } catch (Exception e) {

                    }

                    Log.d("ffck", "!!" + (String) newValue);
                } else {

                }
                break;
            }
        }
    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("tyre_monitor".equals(key)) {
            sendCanboxInfo(0xa9, 0x1);
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) d0, (byte) d1};
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
                sp.setChecked(index == 1 ? true : false);
            } else {
                p.setSummary("" + index);
            }
        }
    }

    private void updateView(byte[] buf) {
        int index;
        int cmd;
        int mask;
        if (buf[0] == 0x62) {
            setPreference("korea_amp_treble", ((buf[2] & 0xf0) >> 4) - 7);
            setPreference("korea_amp_middle", ((buf[2] & 0xf) >> 0) - 7);
            setPreference("korea_amp_bass", ((buf[3] & 0xf0) >> 4) - 7);
            setPreference("attenuation", ((buf[3] & 0xf) >> 0) - 7);
            setPreference("blance", ((buf[4] & 0xf0) >> 4) - 7);


            setPreference("speed_adjustment", (buf[4] & 0xf));
            setPreference("sound_mode", (buf[5] & 0x80) >> 7);
            int t = (buf[5] & 0x60) >> 5;
            if (t == 1) {
                t = 2;
            } else if (t == 2) {
                t = 1;
            }
            setPreference("allround", t);
            return;
        }

        for (int i = 0; i < NODES.length; ++i) {
            cmd = NODES[i].mStatus;
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == mask) {
                setPreference(NODES[i].mKey, (buf[3] & 0xff));
            }

        }

        switch (buf[0]) {
            case 0x24:
                if ((buf[4] & 0x4) != 0) {
                    ((SwitchPreference) findPreference("information_tone_volume")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("information_tone_volume")).setChecked(false);
                }

                if ((buf[4] & 0x1) != 0) {
                    ((SwitchPreference) findPreference("control_system")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("control_system")).setChecked(false);
                }

                if ((buf[4] & 0x8) != 0) {
                    ((SwitchPreference) findPreference("warning_tone")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("warning_tone")).setChecked(false);
                }


                index = (int) ((buf[4] & 0x2) >> 1);
                ((ListPreference) findPreference("turn_lights_set")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("turn_lights_set")).setSummary(((ListPreference) findPreference("turn_lights_set")).getEntry());

                index = (int) ((buf[4] & 0x80) >> 7);
                ((ListPreference) findPreference("mileage_unit")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("mileage_unit")).setSummary(((ListPreference) findPreference("mileage_unit")).getEntry());

                //			index = (int) ((buf[5] & 0x80) >> 7);
                //			((ListPreference) findPreference("brightness")).setValue(String
                //					.valueOf(index));
                //			((ListPreference) findPreference("brightness"))
                //					.setSummary(((ListPreference) findPreference("brightness"))
                //							.getEntry());

                //			if ((buf[7] & 0x1) != 0) {
                //				((SwitchPreference) findPreference("wipers_induction"))
                //						.setChecked(true);
                //			} else {
                //				((SwitchPreference) findPreference("wipers_induction"))
                //						.setChecked(false);
                //			}
                //			if ((buf[7] & 0x2) != 0) {
                //				((SwitchPreference) findPreference("incar_light"))
                //						.setChecked(true);
                //			} else {
                //				((SwitchPreference) findPreference("incar_light"))
                //						.setChecked(false);
                //			}


                //			if ((buf[4] & 0x10) != 0) {
                //				((SwitchPreference) findPreference("enhanced_assistance"))
                //						.setChecked(true);
                //			} else {
                //				((SwitchPreference) findPreference("enhanced_assistance"))
                //						.setChecked(false);
                //			}


                break;

            case 0x21:

                index = (byte) ((buf[6] & 0x40) >> 6);
                ((ListPreference) findPreference("temp_dis_k")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("temp_dis_k")).setSummary(((ListPreference) findPreference("temp_dis_k")).getEntry());
                break;
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
