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

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.utils.NodePreference;

public class JiLiRaiseFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES_TPMS = {new NodePreference("deflation_warning_system", 0x8020, 0, 0),

    };


    private static final NodePreference[] NODES_X1 = {new NodePreference("speed_lock1", 0x8000, 0x5000, 0x80, 0), new NodePreference("stop_unlock", 0x8001, 0x5000, 0x40, 0), new NodePreference("door_open_turn_signal_flashes", 0x8002, 0x5000, 0x40, 0), new NodePreference("remote_lock_feedback", 0x8003, 0x5000, 0x10, 0), new NodePreference("automatically_turns_off_position_light_after_locking", 0x8004, 0x5000, 0x8, 0),


    };

    private static final NodePreference[] NODES_X6 = {
            //
            new NodePreference("intelligent_bend_light", 0x8005, 0x5001, 0x80, 0), new NodePreference("view_mirror_automatically_folded", 0x8014, 0x5001, 0x40, 0),
            //

    };

    private static final NodePreference[] NODES_18BOYUE = {
            //

            new NodePreference("electronic_power_mode_selection", 0x8006, 0x5002, 0x80, 0, R.array.electronic_power_mode_selection_entries, R.array.three_values),

            new NodePreference("remote_lock_feedback", 0x8007, 0x5002, 0x40, 0), new NodePreference("automatically_close_the_window", 0x8008, 0x5002, 0x20, 0), new NodePreference("close_the_skylight_shade", 0x8009, 0x5002, 0x10, 0), new NodePreference("daytime_driving_lights", 0x800a, 0x5002, 0x8, 0), new NodePreference("breathable_mode", 0x8012, 0x5002, 0x4, 0),
            //

    };

    private static final NodePreference[] NODES_1819DIHAO = {new NodePreference("speed_lock1", 0x8000, 0x5006, 0x80, 0), new NodePreference("stop_unlock", 0x8001, 0x5006, 0x40, 0), new NodePreference("door_open_turn_signal_flashes", 0x8002, 0x5006, 0x40, 0),

            new NodePreference("view_mirror_automatically_folded", 0x8014, 0x5006, 0x40, 0),

    };
    private static final NodePreference[] NODES = {new NodePreference("speed_lock1", 0x8000, 0x5000, 0x80, 0), new NodePreference("stop_unlock", 0x8001, 0x5000, 0x40, 0), new NodePreference("door_open_turn_signal_flashes", 0x8002, 0x5000, 0x20, 0), new NodePreference("remote_lock_feedback", 0x8003, 0x5000, 0x10, 0), new NodePreference("automatically_turns_off_position_light_after_locking", 0x8004, 0x5000, 0x8, 0),


            new NodePreference("intelligent_bend_light", 0x8005, 0x5001, 0x80, 0), new NodePreference("view_mirror_automatically_folded", 0x8014, 0x5006, 0x40, 0),

            //

            new NodePreference("electronic_power_mode_selection", 0x8006, 0x5002, 0x80, 0, R.array.electronic_power_mode_selection_entries, R.array.three_values),

            new NodePreference("automatically_close_the_window", 0x8008, 0x5002, 0x20, 0), new NodePreference("close_the_skylight_shade", 0x8009, 0x5002, 0x10, 0), new NodePreference("daytime_driving_lights", 0x800a, 0x5002, 0x8, 0), new NodePreference("breathable_mode", 0x8012, 0x5002, 0x4, 0),
            //

            //
            new NodePreference("fort_tips", 0x8011, 0x5003, 0x80, 0, R.array.fortification_prompt_entries, R.array.three_values),

            new NodePreference("trunk_auto_unlock_distance", 0x8023, 0x5003, 0x20, 0, R.array.trunk_auto_unlock_distance_entries, R.array.three_values),

            new NodePreference("trunk_auto_on", 0x8022, 0x5003, 0x10, 0), new NodePreference("keyless_unlocking_of_the_trunk", 0x8024, 0x5003, 0x8, 0), new NodePreference("any_door_open_lock_lock_alarm_setting", 0x801e, 0x5003, 0x4, 0), new NodePreference("near_unlocked_configuration", 0x801f, 0x5003, 0x2, 0), new NodePreference("leave_locked_configuration", 0x8020, 0x5003, 0x1, 0),


            new NodePreference("language", 0x801a, 0x2700, 0x01, 0, R.array.chery_language_status_entries, R.array.three_values), new NodePreference("showdistancecontrol", 0x8019, 0x5005, 0xc, 0, R.array.alarm_distance_entries, R.array.three_values), new NodePreference("low_speed_warning_tone", 0x801b, 0x4f00, 0xc, 0, R.array.alarm_sensitivity_entries, R.array.three_values),
            //			new NodePreference("stay_with_me_home_duration", 0x8027,
            //					0x5002, 0x80, 0,
            //					R.array.timeswitchlight_entries,
            //					R.array.jili1_value),
            //			new NodePreference("brightness_adjustment_of_vehicle_backlight", 0x8029,
            //					0x5002, 0x80, 0,
            //					R.array.eleven_values,
            //					R.array.dashboard_brightness_value),
            //			new NodePreference("electronic_power_mode_selection", 0x8006,
            //					0x5002, 0x80, 0,
            //					R.array.electronic_power_mode_selection_entries,
            //					R.array.three_values),

    };

    private final static int[] INIT_CMDS = {0x50};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_setting);

        //init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {

        int mModelId = -1;
        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID)) {
                        String mProId = ss[i].substring(1);
                        if (mProId != null && mProId.length() >= 4) {
                            int start = 0;
                            int end = 0;
                            if (mProId.charAt(1) == '0' && mProId.charAt(2) != 0) {
                                end = 1;
                            } else if (mProId.charAt(2) == '0') {
                                end = 2;
                            }

                            start = end + 1;

                            if (mProId.contains("-")) {
                                String[] sss = mProId.substring(start).split("-");
                                mModelId = Integer.valueOf(sss[1]);
                            } else {
                                if ((mProId.length() - start) == 2) {
                                    mModelId = Integer.valueOf(mProId.substring(start + 1, start + 2));
                                } else if ((mProId.length() - start) == 4) {
                                    mModelId = Integer.valueOf(mProId.substring(start + 2, start + 4));
                                } else if ((mProId.length() - start) == 3) {
                                    mModelId = Integer.valueOf(mProId.substring(start + 2, start + 3));
                                }
                            }

                            Log.d("abcd", ":" + mModelId);

                        }
                    }

                }
            } catch (Exception e) {

            }
        }

        NodePreference[] nodes = NODES;
        switch (mModelId) {
            case 2:
            case 7:
                nodes = NODES_X1;
                break;
            case 6:
                nodes = NODES_X6;
                break;
            case 10:
                nodes = NODES_18BOYUE;
                break;
            case 16:
            case 17:
                nodes = NODES_1819DIHAO;
                break;
        }
        getPreferenceScreen().removeAll();
        for (int i = 0; i < nodes.length; ++i) {
            Preference p = nodes[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    }
                }

            }
        }

        for (int i = 0; i < NODES_TPMS.length; ++i) {
            Preference p = NODES_TPMS[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);

                    p.setOnPreferenceClickListener(this);

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
        init();
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
                sendCanboxInfo(0x90, msg.what & 0xff, 0);
            }
        }
    };

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("deflation_warning_system".equals(key)) {

            Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.confirm_tire_pressure_reset).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    sendCanboxInfo(0x80, 0x28, 0x00);
                }
            }).setNegativeButton(android.R.string.cancel, null).show();

        }
        return false;
    }


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
        byte[] buf = new byte[]{(byte) d0, 0x2, (byte) d1, (byte) d2};
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

    private void updateView(byte[] buf) {

        int cmd;
        int mask;
        int index;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = (NODES[i].mStatus & 0xff) >> 0;
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd) {
                mask = (NODES[i].mMask);
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
