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

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Set132 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "Set132";

    private static final NodePreference[] NODES = {


            new NodePreference("steering_feel", 0x6f03, 0x6603, 0xc, 0, R.array.saic_steering_handle, R.array.three_values),

            new NodePreference("radar_warning_tone", 0x6f04, 0x6606, 0x10, 0), new NodePreference("flameout_unlock", 0x6f06, 0x6603, 0x20, 0), new NodePreference("greeting_lights", 0x6f08, 0x6603, 0x10, 0),

            new NodePreference("length_welcome_light", 0x6f09, 0x6603, 0x3, 0, R.array.honda_security_relock_timer_entries, R.array.three_values),

            new NodePreference("smartunlock", 0x6f12, 0x6602, 0x40, 0, R.array.auto_unlock_setting_for_near_car_entries, R.array.two_values),

            new NodePreference("instrument_brightness", 0x6f18, 0x660d, 0xff, 0, R.array.eleven_values, R.array.eleven_values),

            new NodePreference("nearly_unclock", 0x6f20, 0x6602, 0x80, 0, R.array.auto_unlock_setting_for_near_car_entries, R.array.two_values),

            new NodePreference("follow_me_home_reversing_lights", 0x6f23, 0x6604, 0x20, 0), new NodePreference("accompany_me_home_low_beam", 0x6f24, 0x6604, 0x10, 0), new NodePreference("follow_me_home_after_the_fog_lights", 0x6f25, 0x6604, 0x8, 0), new NodePreference("find_car_lights_indicate_reversing_lights", 0x6f26, 0x6604, 0x4, 0), new NodePreference("find_car_light_indicator_low_beam", 0x6f27, 0x6604, 0x2, 0), new NodePreference("find_car_lights_indicate_rear_fog_lights", 0x6f28, 0x6604, 0x1, 0),

            new NodePreference("home_time", 0x6f29, 0x6605, 0xf0, 0, R.array.enhome_time, R.array.enhome_time_vaues),

            new NodePreference("car_time", 0x6f2a, 0x6605, 0xf, 0, R.array.enhome_time, R.array.enhome_time_vaues),

            new NodePreference("myhome", 0x6f3a, 0x6602, 0x20, 0),

            new NodePreference("search_car_indicator", 0x6f3b, 0x6602, 0x10, 0, R.array.saic_search_car_indicator_type_entries, R.array.two_values),


            new NodePreference("folding", 0x6f3c, 0x6602, 0x4, 0), new NodePreference("driving_auto", 0x6f3d, 0x6602, 0x2, 0), new NodePreference("body_stability_control", 0x6f41, 0x6604, 0x80, 0), new NodePreference("economic_driving", 0x6f42, 0x6604, 0x40, 0),

            new NodePreference("battery_level", 0x6f43, 0x6606, 0x3, 0, R.array.power_management_entries, R.array.three_values),

            new NodePreference("warning_pedestrian_cues", 0x6f44, 0x6608, 0x80, 0),

            new NodePreference("psa_driving_mode", 0x6f51, 0x6607, 0xc0, 0, R.array.economic_model_entries, R.array.four_values),

            new NodePreference("lane_assist_function_selection", 0x6f61, 0x6608, 0x8, 0), new NodePreference("LaneDepartureWarningSystem", 0x6f63, 0x6608, 0x4, 0),

            new NodePreference("blind_detection", 0x6f6e, 0x660a, 0x10, 0), new NodePreference("str_lane_changing_assistant", 0x6f6f, 0x660a, 0x8, 0),


            new NodePreference("deflation_warning_system", 0, 0), new NodePreference("factory_reset_settings", 0, 0),

    };


    private final static int[] INIT_CMDS = {0x66};


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

        int len = NODES.length;

        for (int i = 0; i < len; ++i) {

            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    } else if ((p instanceof Preference)) {
                        p.setOnPreferenceClickListener(this);

                    }
                }

            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("deflation_warning_system".equals(key)) {

            Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_tire_pressure_reset).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    sendCanboxInfo(0x6f, 0xa9, 0x1);
                }
            }).setNegativeButton(android.R.string.cancel, null).show();

        } else if ("factory_reset_settings".equals(key)) {

            Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.confirmation_factory_settings).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    sendCanboxInfo(0x6f, 0xaa, 0x1);
                }
            }).setNegativeButton(android.R.string.cancel, null).show();

        }
        return false;
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
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {

                byte[] buf = new byte[]{0x3, (byte) 0x6a, 5, (byte) 1, (byte) (msg.what & 0xff)};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        }
    };

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, Integer.parseInt((String) newValue));
                } else if (preference instanceof SwitchPreference) {

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 0x1 : 0x0);

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


    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x4, (byte) d0, (byte) d1, (byte) d2, 0, 0};
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
                sp.setChecked(index == 1 ? true : false);
            }
        }
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
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
        int index;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = NODES[i].mStatus & 0xff;
            mask = NODES[i].mMask;
            if ((buf[0] & 0xff) == cmd) {
                int value = getStatusValue1(buf[2 + index], mask);
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
                                Log.d(TAG, "updateView:Exception " + e);
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
