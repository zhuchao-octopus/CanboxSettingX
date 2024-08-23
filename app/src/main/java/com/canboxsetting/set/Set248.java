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
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.annotation.Nullable;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Set248 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "HYSettingsRaiseFragment";

    private static final NodePreference[] NODES = {

            new NodePreference("ambient_lighting", 0x8401, 0x1a00, 0xff, 0, R.array.yema_brightness_entries, R.array.three_values), new NodePreference("ambient_lighting_color", 0x8402, 0x1a01, 0xff, 0, R.array.yema_display_color_entries, R.array.five_values), new NodePreference("ambient_lighting_brightness", 0x8403, 0x1a02, 0xff, 0, R.array.four_values, R.array.four_values),


    };

    private final static int[] INIT_CMDS = {0x1a

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

                    if (add) {
                        ((PreferenceScreen) ps).addPreference(p);
                    }

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    } else {
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

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, Integer.parseInt((String) newValue));

                } else if (preference instanceof SwitchPreference) {
                    // if (NODES[i].mType == Node.TYPE_CUSTOM) {
                    // sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    // } else {
                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 0 : 0x0);
                    // }
                }
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
        String key = arg0.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, 0);

            }
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
                // Log.d("aa", key+":"+((ListPreference)
                // findPreference(key)).getEntry());
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
        // if (buf[0] == 0x57) {
        //
        //
        // } else {
        for (int i = 0; i < NODES.length; ++i) {

            if ((NODES[i].mType & NodePreference.PREFERENCE_MASK) == NodePreference.SCREENPREFERENCE) {
                cmd = (NODES[i].mStatus & 0xff00) >> 8;
                index = (NODES[i].mStatus & 0xff);
                mask = NODES[i].mType & 0xff;
            } else {
                cmd = (NODES[i].mStatus & 0xff00) >> 8;
                index = (NODES[i].mStatus & 0xff);
                mask = NODES[i].mMask;
            }

            if ((buf[0] & 0xff) == cmd) {
                value = getStatusValue1(buf[2 + index], mask);
                setPreference(NODES[i].mKey, value);
            }

        }

        // }
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
