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
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Set230 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {
            //group1


            new NodePreference("redar", 0x9201, 0x2900, 0x1, 0), new NodePreference("remote_lift_window", 0x9203, 0x2900, 0x10, 0), new NodePreference("remote_drop_window", 0x9204, 0x2900, 0x20, 0), new NodePreference("long_press_micro_switch_unlocking_lifting_window", 0x9205, 0x2900, 0x40, 0), new NodePreference("lower_window_unlocked_by_long_press_micro_switch", 0x9206, 0x2900, 0x80, 0), new NodePreference("outside_automatic_folding_of_rearview_mirror", 0x9207, 0x2902, 0x1, 0), new NodePreference("zhonghua_xx", 0x9208, 0x2902, 0x2, 0), new NodePreference("driver_seat_auto_return", 0x9209, 0x2902, 0x4, 0), new NodePreference("str_turn_mode", 0x920a, 0x2902, 0x8, 0),

            new NodePreference("str_remote_unlock", 0x920b, 0x2902, 0x10, 0, R.array.auto_unlock_setting_for_near_car_entries, R.array.two_values),

            new NodePreference("go_home_lighting_delay", 0x920c, 0x2902, 0xe0, 0, R.array.byd_go_home, R.array.eight_values), new NodePreference("leave_home_lighting_delay", 0x920d, 0x2903, 0x07, 0, R.array.byd_go_home, R.array.eight_values),
            //group2


            new NodePreference("in_car_inspection", 0x9301, 0x2a05, 0x40, 1), new NodePreference("outside_inspection", 0x9302, 0x2a05, 0x80, 1), new NodePreference("power_on_detection", 0x9303, 0x2a00, 0x80, 1), new NodePreference("door_detection", 0x9304, 0x2a00, 0x20, 1), new NodePreference("thirty_minute_timing_test", 0x9305, 0x2a00, 0x10, 1),


    };

    private final static int[] INIT_CMDS = {0x29, 0x2a, 0x2b

    };

    private byte[] mNewEnergy = new byte[]{0, 0, 0};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.byd_cyt_setting_header);

        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {
                int index = NODES[i].mType & 0xff;
                if (index < getPreferenceScreen().getPreferenceCount()) {
                    Preference ps = getPreferenceScreen().getPreference(index);
                    if (ps instanceof PreferenceScreen) {
                        ((PreferenceScreen) ps).addPreference(p);

                        if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                            p.setOnPreferenceChangeListener(this);
                        } else {
                            p.setOnPreferenceClickListener(this);
                        }
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
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {

                byte[] buf = new byte[]{(byte) 0x90, (byte) 4, (byte) (msg.what & 0xff), 0, 0, 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private final static byte[] SPEED = new byte[]{(byte) 0xFE, (byte) 0x5a, (byte) 0x64, (byte) 0x6e, (byte) 0x78, (byte) 0x82};

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {


                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, Integer.parseInt((String) newValue));

                } else if (preference instanceof SwitchPreference) {
                    //					if (NODES[i].mType == Node.TYPE_CUSTOM) {
                    //						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    //					} else {
                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 1 : 0x0);
                    //					}
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
                } else {

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
        if (arg0.getKey().equals("reset_driver_mode")) {
            sendCanboxInfo(0xa9, 0xb, 0x01);
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

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x2, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendNewEnergyCanboxInfo(NodePreference node, int index) {

        if (node.mStatus == 2) {
            mNewEnergy[2] = SPEED[index];
        } else {
            mNewEnergy[node.mStatus] = (byte) ((index & 0xff) << node.mMask);
        }

        //		Log.d("ffck", Util.byte2HexStr(mNewEnergy));
        byte[] buf = new byte[]{0x8, (byte) 0xa9, (byte) 0x0a, mNewEnergy[0], mNewEnergy[1], mNewEnergy[2]};
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
        int value;
        //		if (buf[0] == 0x57) {
        //
        //
        //		} else {
        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff00) >> 8;
            index = (NODES[i].mStatus & 0xff);
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd) {
                value = getStatusValue1(buf[2 + index], mask);
                setPreference(NODES[i].mKey, value);
            }

        }

        if (buf[0] == 0x2a) {
            String s;
            Preference p;
            index = (buf[3] & 0xff) | ((buf[4] & 0xff) << 8);
            if (index >= 0 && index <= 0xBB8) {
                s = index + "ug/m3";

            } else {
                s = "---";
            }

            p = findPreference("_pm2_5");
            p.setSummary(s);

            index = (buf[5] & 0xff) | ((buf[6] & 0xff) << 8);
            if (index >= 0 && index <= 0xBB8) {
                s = index + "ug/m3";
                ;

            } else {
                s = "---";
            }

            p = findPreference("outside_pm2_5");
            p.setSummary(s);

        }

        //		}
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
