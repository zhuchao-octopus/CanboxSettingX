package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Set178 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES_CPN005 = {


            new NodePreference("remote_unlock", 0x8c01, 0x8703, 0x1, 0, R.array.door_to_be_unlocked_entries, R.array.brightness_control_value),


            new NodePreference("driving_auto", 0x8c02, 0x8703, 0x2, 0),

            new NodePreference("flameout_automatic_latch", 0x8c03, 0x8703, 0x4, 0),

            new NodePreference("headlight_delay1", 0x8c04, 0x8703, 0x38, 0, R.array.headlight_delay_oushange, R.array.five_values),

            new NodePreference("light_one_key_turn", 0x8c05, 0x8703, 0xc0, 0, R.array.turn_signal_oushange, R.array.four_values),

            new NodePreference("astern_wiper", 0x8c06, 0x8704, 0x1, 0),


            new NodePreference("automatic_folding_of_rearview_mirror", 0x8c07, 0x8704, 0x2, 0),

            new NodePreference("air_conditioning_settings_unlock_active_ventilation", 0x8c08, 0x8704, 0x4, 0), new NodePreference("air_conditioning_set_air_conditioning_self_drying", 0x8c09, 0x8704, 0x8, 0),

            new NodePreference("changan01", 0x8c0b, 0x8705, 0x1c, 0, R.array.headlight_delay_oushange, R.array.five_values),


            new NodePreference("electric_horn_sound", 0x8c0d, 0x8705, 0xc0, 0, R.array.cs_sound, R.array.four_values),


            new NodePreference("long_press_ventilation_setting", 0x8c0e, 0x8706, 0x1, 0),


            new NodePreference("window_remote_control", 0x8c0f, 0x8706, 0x2, 0), new NodePreference("air_conditioning_settings_unlock_windows_for_ventilation", 0x8c10, 0x8704, 0x10, 0), new NodePreference("str_daytime_running_lamp", 0x8c17, 0x8706, 0x8, 0),

            new NodePreference("oushang_2f", 0x8c12, 0x8706, 0x40, 0),

            new NodePreference("oushang_30", 0x8c13, 0x8706, 0x20, 0),

            new NodePreference("oushang_32", 0x8c15, 0x8707, 0xf0, 0, R.array.volbackdoor_entryValues, R.array.alarm_vol_value),

            new NodePreference("oushang_27", 0x8c16, 0x8707, 0xf, 0, R.array.volbackdoor_entryValues, R.array.alarm_vol_value),


            new NodePreference("oushang_3d", 0x8c1a, 0x7804, 0x80, 0),

            new NodePreference("oushang_3c", 0x8c1b, 0x7804, 0x40, 0),


            new NodePreference("oushang_3f", 0x8c1c, 0x7804, 0x30, 0, R.array.oushang_3f, R.array.three_values), new NodePreference("oushang_40", 0x8c1d, 0x7804, 0xc, 0, R.array.oushang_40, R.array.three_values),


            new NodePreference("oushang_29", 0x8c1e, 0x7803, 0x80, 0),

            new NodePreference("oushang_22", 0x8c1f, 0x7803, 0x60, 0, R.array.oushang_22, R.array.three_values), new NodePreference("oushang_28", 0x8c20, 0x7803, 0xc, 0, R.array.oushang_1e, R.array.three_high_values),

            new NodePreference("factory_reset_settings", 0x8c11, 0, 0),

    };

    private static final NodePreference[] NODES_CNP003 = {


            new NodePreference("auto_unlock63", 0x8c02, 0x8703, 0x2, 0),

            new NodePreference("flameout_automatic_latch", 0x8c03, 0x8703, 0x4, 0),

            new NodePreference("headlight_delay1", 0x8c04, 0x8703, 0x38, 0, R.array.headlight_delay_oushange, R.array.five_values),

            new NodePreference("light_one_key_turn", 0x8c05, 0x8703, 0xc0, 0, R.array.turn_signal_oushange, R.array.four_values),

            new NodePreference("key_unlock_ventilation", 0x8c08, 0x8704, 0x4, 0), new NodePreference("key_lock_ventilation", 0x8c09, 0x8704, 0x8, 0),

            new NodePreference("long_press_ventilation_setting", 0x8c0e, 0x8706, 0x1, 0),

            new NodePreference("changan01", 0x8c0b, 0x8705, 0x1c, 0, R.array.headlight_delay_oushange, R.array.five_values),


            new NodePreference("electric_horn_sound", 0x8c0d, 0x8705, 0xc0, 0, R.array.horn_sound, R.array.three_values),


            new NodePreference("side_window_light_function", 0x8c0c, 0x8705, 0x20, 0),


    };
    NodePreference[] NODES = NODES_CPN005;

    private final static int[] INIT_CMDS = {0x87, 0x78};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_setting);

        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private final static int[] PRO218_MASK = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 0x10, 0x11,};

    private boolean isMaskSet(int id) {
        if (GlobalDef.getProId() == 218) {
            for (int i : PRO218_MASK) {
                if (id == i) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void init() {

        if (GlobalDef.getModelId() == 4) {
            NODES = NODES_CNP003;
        }


        int len = NODES.length;

        for (int i = 0; i < len; ++i) {
            if (!isMaskSet(NODES[i].mCmd & 0xff)) {
                continue;
            }

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
    public boolean onPreferenceClick(Preference preference) {
        // TODO Auto-generated method stub
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, 0x1);
                break;
            }
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
        byte[] buf = new byte[]{0x2, (byte) d0, (byte) d1, (byte) d2};
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
