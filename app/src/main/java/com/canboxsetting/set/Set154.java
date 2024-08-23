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
import com.common.utils.MyCmd;
import com.common.utils.NodePreference;
import com.common.utils.Util;
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceEdit.IButtonCallBack;
import com.common.view.MyPreferenceSeekBar;

public class Set154 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";


    private static final NodePreference[] NODES = {


            new NodePreference("ambient_light_color", 0x9761, 0x07020f, 0x0, 0, R.array.atmosphere_raise_light_color_entries, R.array.eninstrument_style3_value),


            new NodePreference("seat_massage"),


            new NodePreference("drive_adjustment_high", 0xc001, 0x4001ff, 0, 0, 10), new NodePreference("drive_adjustment_medium", 0xc002, 0x4002ff, 0, 0, 10), new NodePreference("drive_adjustment_low", 0xc003, 0x4003ff, 0, 0, 10),


            new NodePreference("passenger_adjustment_high", 0xc004, 0x4004ff, 0, 0, 10), new NodePreference("passenger_adjustment_medium", 0xc005, 0x4005ff, 0, 0, 10), new NodePreference("passenger_adjustment_low", 0xc006, 0x4006ff, 0, 0, 10),


            new NodePreference("drive_message_backrest", 0xc007, 0x40070c, 0x0, 0, R.array.seat_heat_values, R.array.three_values), new NodePreference("drive_message_cushion", 0xc008, 0x400703, 0x0, 0, R.array.seat_heat_values, R.array.three_values),

            new NodePreference("passenger_message_backrest", 0xc009, 0x40080c, 0x0, 0, R.array.seat_heat_values, R.array.three_values), new NodePreference("passenger_message_cushion", 0xc00a, 0x400803, 0x0, 0, R.array.seat_heat_values, R.array.three_values),};


    private final static int[] INIT_CMDS = {0x40};
    private IButtonCallBack mButtonCallBack = new IButtonCallBack() {
        public void callback(String key, boolean add) {
            for (int i = 0; i < NODES.length; ++i) {
                if (NODES[i].mKey.equals(key)) {
                    if (add) {
                        sendCmd(NODES[i].mCmd, 0x2);
                    } else {
                        sendCmd(NODES[i].mCmd, 0x1);
                    }
                    Util.doSleep(200);
                    query();
                    break;
                }
            }

        }

        ;
    };
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                byte[] buf = new byte[]{(byte) 0xf1, 0x2, (byte) (msg.what & 0xff)};
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
                    } else if ((p instanceof MyPreferenceEdit)) {
                        ((MyPreferenceEdit) p).setCallback(mButtonCallBack);
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
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }
    }

    private void query() {

        byte[] buf = new byte[]{(byte) 0xf1, 0x2, (byte) 0x40};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    ListPreference lp = (ListPreference) preference;
                    lp.setValue((String) newValue);
                    lp.setSummary("%s");
                    sendCmd(NODES[i].mCmd, Integer.parseInt((String) newValue));

                    Util.doSleep(200);
                    query();
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
                sendCmd(NODES[i].mCmd, 0x2);
                Util.doSleep(200);
                query();
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
            } else if (p instanceof Preference) {
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

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff0000) >> 16;
            index = (NODES[i].mStatus & 0xff00) >> 8;
            mask = NODES[i].mStatus & 0xff;

            if ((buf[0] & 0xff) == cmd) {
                if ((2 + index) < buf.length) {
                    int value = getStatusValue1(buf[2 + index], mask);
                    setPreference(NODES[i].mKey, value);
                }
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
