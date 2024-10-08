package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Node;

public class CheryODSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "CheryODSettingFragment";

    private static final Node[] NODES = {new Node("langauage5", 0xc600, 0x7100, 0x2), new Node("fort_tips", 0xc601, 0x7100, 0x2), new Node("brake_alarm", 0xc602, 0x7100, 0x2), new Node("driving_auto", 0xc603, 0x7100, 0x2), new Node("headlight_delay", 0xc604, 0x7100, 0x2), new Node("running_lights", 0xc605, 0x7100, 0x2), new Node("over_speed", 0xc609, 0x7100, 0x2),};

    private Preference[] mPreferences = new Preference[NODES.length];
    private boolean mPaused = true;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.chery_od_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            // Log.d("aa", mPreferences[i]+":"+NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

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

    }

    private void sendCanboxData(int cmd, int value) {

        byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) (cmd), (byte) value};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));


                } else if (preference instanceof SwitchPreference) {
                    if (NODES[i].mType == Node.TYPE_CUSTOM) {
                        sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
                    } else {
                        sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
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

    public boolean onPreferenceClick(Preference arg0) {

        try {
            udpatePreferenceValue(arg0, null);
        } catch (Exception e) {

        }

        return false;
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

    private void updateView(byte[] buf) {

        try {
            switch (buf[0]) {
                case 0x40:
                    switch (buf[2]) {
                        case 0x0:
                            setPreference("langauage5", buf[3]);
                            break;
                        case 0x1:
                            setPreference("fort_tips", buf[3]);
                            break;
                        case 0x2:
                            setPreference("brake_alarm", buf[3]);
                            break;
                        case 0x3:
                            setPreference("driving_auto", buf[3]);
                            break;
                        case 0x4:
                            setPreference("headlight_delay", buf[3]);
                            break;
                        case 0x5:
                            setPreference("running_lights", buf[3]);
                            break;
                        case 0x9:
                            setPreference("over_speed", buf[3]);
                            break;
                    }
                    break;
            }

        } catch (Exception e) {
            Log.d(TAG, "err" + e);
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
                                Log.d("aa", "!!!!!!!!" + buf);
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
