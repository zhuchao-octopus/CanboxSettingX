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
import com.common.utils.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.utils.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class Set152 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";


    private static final NodePreference[] NODES_152 = {


            new NodePreference("adjust_outside", 0x8200, 0x400, 0x0f, 0, R.array.mazda3_2020_settings_13, R.array.adjust_outside_values),


            new NodePreference("trip_a", 0x8202, 0x400, 0x30, 0, R.array.honda_trip_reset_entries, R.array.three_values),


            new NodePreference("trip_b", 0x8203, 0x400, 0xc0, 0, R.array.honda_trip_reset_entries, R.array.three_values),

            new NodePreference("fuel_efficiency_bl", 0x8201, 0x401, 0x80, 0),


            new NodePreference("il_dimming_time", 0x8204, 0x401, 0x3, 0, R.array.honda_il_dimming_entries, R.array.three_values),


            new NodePreference("hl_auto_off_time", 0x8205, 0x401, 0xc, 0, R.array.honda_hl_auto_off_entries, R.array.four_values),


            new NodePreference("auto_light_sensitivity", 0x8206, 0x401, 0x70, 0, R.array.accord_svc, R.array.four_values),


            new NodePreference("keyless", 0x820a, 0x402, 0x80, 0),

            new NodePreference("security_relock_timer", 0x820b, 0x402, 0x30, 0, R.array.honda_security_relock_timer_entries, R.array.three_values),

            new NodePreference("backlight", 0x8216, 0x403, 0x20, 0),

            new NodePreference("str_language", 0x8217, 0x403, 0x4, 0, R.array.chery_language_status_entries, R.array.two_values),

            new NodePreference("start_stop_dis", 0x8219, 0x403, 0x8, 0), new NodePreference("acc_detection_prompt_tone", 0x821c, 0x404, 0x40, 0), new NodePreference("tone_of_pause_lkas", 0x821e, 0x404, 0x08, 0),


            new NodePreference("tpms_calibration", 0x8211, 0), new NodePreference("restore_factory_settings", 0x820f, 0),

    };


    private static final NodePreference[] NODES_153 = {


            new NodePreference("adjust_outside", 0x8f00, 0xa00, 0x0f, 0, R.array.mazda3_2020_settings_13, R.array.adjust_outside_values),


            new NodePreference("trip_a", 0x8f02, 0xa00, 0x30, 0, R.array.honda_trip_reset_entries, R.array.three_values),


            new NodePreference("trip_b", 0x8f03, 0xa00, 0xc0, 0, R.array.honda_trip_reset_entries, R.array.three_values),

            //			new NodePreference("fuel_efficiency_bl", 0x8f01, 0xa01, 0x80, 0),


            new NodePreference("il_dimming_time", 0x8f04, 0xa01, 0x3, 0, R.array.honda_il_dimming_entries, R.array.three_values),


            new NodePreference("hl_auto_off_time", 0x8f05, 0xa01, 0xc, 0, R.array.honda_hl_auto_off_entries, R.array.four_values),


            new NodePreference("auto_light_sensitivity", 0x8f06, 0xa01, 0x70, 0, R.array.accord_svc, R.array.four_values),


            new NodePreference("keyless", 0x8f0a, 0xa02, 0x80, 0),

            new NodePreference("security_relock_timer", 0x8f0b, 0xa02, 0x30, 0, R.array.honda_security_relock_timer_entries, R.array.three_values),

            new NodePreference("backlight", 0x8f16, 0xa03, 0x20, 0),

            new NodePreference("str_language", 0x8f17, 0xa03, 0x4, 0, R.array.chery_language_status_entries, R.array.two_values),

            new NodePreference("start_stop_dis", 0x8f19, 0xa03, 0x8, 0), new NodePreference("acc_detection_prompt_tone", 0x8f1c, 0xa04, 0x40, 0), new NodePreference("tone_of_pause_lkas", 0x8f1e, 0xa04, 0x08, 0),


            new NodePreference("tpms_calibration", 0x8f11, 0), new NodePreference("restore_factory_settings", 0x8f0f, 0),

    };

    private static NodePreference[] NODES = NODES_152;
    private final static int[] INIT_CMDS_152 = {0x830400};
    private final static int[] INIT_CMDS_153 = {0x8d0a00};
    private static int[] INIT_CMDS = INIT_CMDS_152;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((GlobalDef.getProId() == 153)) {
            NODES = NODES_153;
            INIT_CMDS = INIT_CMDS_153;
        }


        addPreferencesFromResource(R.xml.empty_setting);

        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void sendCmd(int cmd, int value) {

        byte[] buf = new byte[]{(byte) ((cmd & 0xff00) >> 8), 0x2, (byte) ((cmd & 0xff)), (byte) value};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    boolean add = true;

                    if (add) {
                        ((PreferenceScreen) ps).addPreference(p);
                    }

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    } else if ((p instanceof Preference)) {
                        p.setOnPreferenceClickListener(this);

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
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo((msg.what & 0xff0000) >> 16, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCmd(NODES[i].mCmd, Integer.parseInt((String) newValue));

                } else if (preference instanceof SwitchPreference) {

                    sendCmd(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);

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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // TODO Auto-generated method stub
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                sendCmd(NODES[i].mCmd, 0x0);
                break;
            }
        }
        return false;
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0x83, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int cmd, int d0, int d1) {
        byte[] buf = new byte[]{(byte) cmd, 0x2, (byte) d0, (byte) d1};
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
            } else if (p instanceof MyPreferenceSeekBar) {
                if (key.equals("over_speed")) {
                    index = index * 10;
                }
                p.setSummary(index + "");
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
        // if (buf[0] == 0x78) {
        // if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4])
        // || (mVisible[3] != buf[5])) {
        // Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
        // removeAll();
        // init();
        // }
        // }
        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = NODES[i].mStatus & 0xff;
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd) {
                mask = (NODES[i].mMask);
                if ((2 + index) < buf.length) {
                    int value = getStatusValue1(buf[2 + index], mask);
                    setPreference(NODES[i].mKey, value);
                }
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
