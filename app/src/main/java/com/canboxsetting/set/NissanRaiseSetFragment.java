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
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceEdit.IButtonCallBack;
import com.common.view.MyPreferenceSeekBar;

public class NissanRaiseSetFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {


            new NodePreference("setting_auto_light_sensitivity", 0x8355, 0x95, 0x6000, 0, R.array.duke_setting_auto_light_sensitivity, R.array.brightness_control_value),


            new NodePreference("car_light_auto", 0x8354, 0x95, 0x8000, 0),

            new NodePreference("smart_key_lock_fuction", 0x8358, 0x95, 0x800000, 0),

            new NodePreference("automatic_lighting_time_setting", 0x8357, 0x95, 0xf00, 0, R.array.automatic_lighting_time_setting, R.array.brightness_control_value),


            new NodePreference("vehicle_speed_interlocking_intermittent_wiper", 0x8356, 0x95, 0x1000, 0),


            new NodePreference("bose_center_point", 0x8327, 0x93, 8, 1),

            new NodePreference("driver_seat_sound_field", 0x8329, 0x93, 10, 1),

            new NodePreference("speed_linkage_volume", 0x8328, 0x93, 7, 1, 5),

            new NodePreference("surround_volume", 0x8323, 0x93, 9, 1, 11),

    };

    private final static int[] INIT_CMDS = {0x25};


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
                    ((PreferenceScreen) ps).addPreference(p);

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference) || (p instanceof MyPreferenceSeekBar)) {
                        p.setOnPreferenceChangeListener(this);
                    }
                }

            }
        }

        ((MyPreferenceEdit) findPreference("speed_linkage_volume")).setCallback(mButtonCallBack);
        ((MyPreferenceEdit) findPreference("surround_volume")).setCallback(mButtonCallBack);
    }

    private IButtonCallBack mButtonCallBack = new IButtonCallBack() {
        public void callback(String key, boolean add) {
            if ("speed_linkage_volume".equals(key)) {
                if (add) {
                    sendCanboxInfo(0x83, 0x26, 0x21);
                } else {
                    sendCanboxInfo(0x83, 0x26, 0x31);
                }
            } else if ("surround_volume".equals(key)) {
                if (add) {
                    sendCanboxInfo(0x83, 0x28, 0x21);
                } else {
                    sendCanboxInfo(0x83, 0x28, 0x31);
                }
            }
        }
    };
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
                sendCanboxInfo(0x90, msg.what & 0xff, 0);
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
                } else {
                    if (NODES[i].mKey.equals("speed_linkage_volume")) {


                    }
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
            } else {
                if ("surround_volume".equals(key)) {
                    if (index > 250) {
                        index = 255 - index;
                    }
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

    private int getStatusValue(byte[] buf, int mask) {

        int value = 0;
        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }

        value = 0;
        if (buf.length > 2) {
            value = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[4] & 0xff) << 16);
        }


        return ((value & mask) >> start);
    }


    private void updateView(byte[] buf) {

        int cmd;
        int mask;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = NODES[i].mStatus;
            mask = NODES[i].mMask;

            if ((buf[0] & 0xff) == cmd) {
                if ((NODES[i].mType & 0xff) == 1) {
                    mask = (NODES[i].mMask);
                    int value = buf[mask] & 0xff;
                    setPreference(NODES[i].mKey, value);
                } else {
                    mask = (NODES[i].mMask);
                    int value = getStatusValue(buf, mask);
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
