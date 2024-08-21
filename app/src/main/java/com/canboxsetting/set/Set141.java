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
import com.common.utils.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class Set141 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {
            //group1

            new NodePreference("daytime_driving_lights", 0x860101, 0x37, 0xff, 0),

            new NodePreference("myhome", 0x860102, 0x37, 0xff, 0, R.array.baic_go_home, R.array.six_values),

            new NodePreference("lane_change_flashes", 0x860103, 0x37, 0xff, 0, R.array.light_one_key_turn_entries2, R.array.three_values),

            //
            new NodePreference("zhonghua_xx", 0x860201, 0x37, 0xff, 0, R.array.baic_automatic_locking, R.array.five_values),

            new NodePreference("prompt_for_successful_lock", 0x860202, 0x37, 0xff, 0), new NodePreference("prompt_for_lock_failure", 0x860203, 0x37, 0xff, 0), new NodePreference("power_off_to_unlock", 0x860204, 0x37, 0xff, 0),

            new NodePreference("low_power_settings", 0x860301, 0x37, 0xff, 0), new NodePreference("energy_recovery_intensity", 0x860302, 0x37, 0xff, 0, R.array.energy_recovery_intensity_entries, R.array.three_values),

            new NodePreference("blind_spot_detector", 0x860401, 0x37, 0xff, 0), new NodePreference("chana_lane_departure_warning", 0x860402, 0x37, 0xff, 0),

            new NodePreference("alarm_mode", 0x860403, 0x37, 0xff, 0, R.array.baic_interface_alarm_and_sound_alarm, R.array.two_values),

            new NodePreference("lane_departure_sensitivity", 0x860404, 0x37, 0xff, 0, R.array.steering_signal_volume_entries, R.array.two_values),

            new NodePreference("signal_pedal", 0x860501, 0x37, 0xff, 0), new NodePreference("creep", 0x860502, 0x37, 0xff, 0),

    };

    private final static int[] INIT_CMDS = {0x27

    };

    private Preference[] mPreferences = new Preference[NODES.length];

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
                if (ps instanceof PreferenceScreen) {
                    boolean add = true;
                    //					if (((NODES[i].mType & 0xff0000) >> 16) != 0) {
                    //						int index = ((NODES[i].mType & 0xff00) >> 8) + 2;
                    //						if ((mVisible[index] & NODES[i].mType) == 0) {
                    //							add = false;
                    //						}
                    //					}

                    if (add) {
                        ((PreferenceScreen) ps).addPreference(p);
                    }

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
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {

                byte[] buf = new byte[]{(byte) 0x90, (byte) 1, (byte) (msg.what & 0xff)};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            }
        }
    };

    //	private void sendCanboxData(int cmd, int value) {
    //		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
    //
    //	}
    //
    //	private void sendCanboxData(int cmd) {
    //		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),
    //				((cmd & 0xff) >> 0));
    //
    //	}

    private final static byte[] SPEED = new byte[]{(byte) 0xFE, (byte) 0x5a, (byte) 0x64, (byte) 0x6e, (byte) 0x78, (byte) 0x82};

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {


                    sendCanboxInfo((NODES[i].mCmd & 0xff0000) >> 16, (NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, Integer.parseInt((String) newValue));

                } else if (preference instanceof SwitchPreference) {
                    //					if (NODES[i].mType == Node.TYPE_CUSTOM) {
                    //						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    //					} else {
                    sendCanboxInfo((NODES[i].mCmd & 0xff0000) >> 16, (NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 1 : 0x0);
                    //					}
                }
            }
        }
    }

    private void sendEQCmd(byte id, int step) {
        byte data;
        if (step == 0) {
            return;
        } else if (step > 0) {
            data = 1;
            step--;
        } else {
            data = -1;
            step++;
        }
        byte[] buf = new byte[]{0x2, (byte) 0xad, id, data};
        //		sendDataToCanbox(buf, buf.length);
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        mHandlerEQ.removeMessages(SET_EQ_STEP);
        if (step != 0) {
            mHandlerEQ.sendMessageDelayed(mHandlerEQ.obtainMessage(SET_EQ_STEP, id, step), 200);
        }
    }

    private final static int SET_EQ_STEP = 1;
    private Handler mHandlerEQ = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_EQ_STEP:
                    sendEQCmd((byte) msg.arg1, msg.arg2);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception e) {

        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int cmd, int d0, int d1, int d2) {

        byte[] buf = new byte[]{(byte) cmd, 0x3, (byte) d0, (byte) d1, (byte) d2};
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
                byte b = (byte) index;
                if (b < -5 || b > 5) {
                    b = 0;
                }
                p.setSummary(b + "");
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
        int index2;
        int value;
        //		if (buf[0] == 0x57) {
        //
        //
        //		} else {
        for (int i = 0; i < NODES.length; ++i) {
            index = (NODES[i].mCmd & 0xff00) >> 8;
            index2 = (NODES[i].mCmd & 0xff);
            cmd = (NODES[i].mStatus & 0xff);
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == index && (buf[3] & 0xff) == index2) {
                value = getStatusValue1(buf[4], mask);
                setPreference(NODES[i].mKey, value);
            } else {

            }

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
