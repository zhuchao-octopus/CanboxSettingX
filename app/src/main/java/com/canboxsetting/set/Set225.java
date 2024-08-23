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
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;
import com.common.utils.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class Set225 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {
            //group1

            new NodePreference("set_tip_sound", 0xa701, 0x4000, 0xff, 0, R.array.low_speed_warning_tone_entries, R.array.three_values), new NodePreference("outside_lights_close_time", 0xa702, 0x4001, 0xff, 0, R.array.enheadlight_off_timer, R.array.five_values), new NodePreference("inner_lights_close_time", 0xa703, 0x4002, 0xff, 0, R.array.external_lighting_delay_off, R.array.five_values), new NodePreference("automatic_speed_lock", 0xa704, 0x4003, 0xff, 0, R.array.jac_automatic_speed_lock, R.array.three_values),

            new NodePreference("fortification_prompt_sound", 0xa705, 0x4004, 0xff, 0), new NodePreference("Positioning_lighting", 0xa706, 0x4005, 0xff, 0),


            new NodePreference("alert_volume", 0xa709, 0x4008, 0xff, 0, R.array.three_high_values, R.array.three_values),

            new NodePreference("automatic_window_lowering_by_remote_control", 0xa70a, 0x4009, 0xff, 0), new NodePreference("remote_mirror_fold", 0xa70c, 0x400b, 0xff, 0), new NodePreference("reverse_mirror_flip", 0xa70d, 0x400c, 0xff, 0), new NodePreference("function_seat", 0xa70e, 0x400d, 0xff, 0),


            new NodePreference("vol_radar", 0xa70f, 0x400e, 0xff, 0, R.array.five_high_values, R.array.five_high_values), new NodePreference("radar_warning_tone", 0xa710, 0x400f, 0xff, 0, R.array.five_high_values, R.array.five_high_values),


    };

    private final static int[] INIT_CMDS = {0x40

    };
    private final static byte[] SPEED = new byte[]{(byte) 0xFE, (byte) 0x5a, (byte) 0x64, (byte) 0x6e, (byte) 0x78, (byte) 0x82};
    private final static int SET_EQ_STEP = 1;
    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {

                byte[] buf = new byte[]{(byte) 0x90, (byte) 2, (byte) (msg.what & 0xff), 0};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            }
        }
    };
    private BroadcastReceiver mReceiver;

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
        int max = 6;
        if (GlobalDef.getModelId() == 13) {
            max = NODES.length;
        }
        for (int i = 0; i < max; ++i) {
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

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

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
    }    private Handler mHandlerEQ = new Handler() {
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

        //		}
    }


    private void showPreferenceEnable(String id, boolean enabled) {
        Preference ps = (Preference) findPreference(id);
        if (ps != null) {
            ps.setEnabled(enabled);
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
