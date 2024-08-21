package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.annotation.Nullable;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.widget.Toast;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.utils.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LandRoverHaoZhengSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "LandRoverHaoZhengSettingsFragment";

    private static final Node[] NODES = {

            new Node("over_speed", 0, 0, 0, 0x0), new Node("speed_alarms", 0, 0, 0, 0x0), new Node("oil_unit", 0, 0, 0, 0x0), new Node("instrument_style", 0, 0, 0, 0x0), new Node("reset1", 0, 0, 0, 0x0), new Node("reset2", 0, 0, 0, 0x0), new Node("reset3", 0, 0, 0, 0x0),

    };

    private final static int[] INIT_CMDS = {0x3800};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.landrover_haozheng_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {

                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }

            }
        }

        Preference p;
        p = findPreference("reset1");
        p.setTitle(getString(R.string.reset) + " " + getString(R.string.fulecons) + "1");
        p = findPreference("reset2");
        p.setTitle(getString(R.string.reset) + " " + getString(R.string.fulecons) + "2");
        p = findPreference("reset3");
        p.setTitle(getString(R.string.reset) + " " + getString(R.string.averageapeed));

        mEditTextPreferenceSpeedAlarm = (EditTextPreference) findPreference("speed_alarms");

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    EditTextPreference mEditTextPreferenceSpeedAlarm;
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
        // requestInitData();
        sendCanboxInfo(0x38);

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
                sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private byte mLockMode = 0;
    private byte mBackWiper = 0;

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();

        if ("over_speed".equals(key)) {
            sendCanboxInfo(0xc6, 0x1, ((Boolean) newValue) ? 1 : 0);
        } else if ("speed_alarms".equals(key)) {

            int v = -1;
            try {
                v = Integer.valueOf((String) newValue);
            } catch (Exception e) {

            }
            if (v == -1) {
                String s = "Error!The range is ";
                if ((mUnit & 0x8) == 0) {
                    s += "16~250 km/h";
                } else {
                    s = "10~156 mph";
                }
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            } else {
                sendCanboxInfo(0xc6, 0, v);
            }
        } else if ("instrument_style".equals(key)) {

            Set<String> s = (Set<String>) newValue;
            //			((MultiSelectListPreference) preference).setValues(s);
            //			((MultiSelectListPreference) preference).setSummary("");
            Iterator<String> it = s.iterator();
            int otherSettings = 0;
            while (it.hasNext()) {
                String str = it.next();
                try {
                    int i = Integer.valueOf(str);
                    if (i < 32) {
                        otherSettings |= (0x1 << i);
                    }
                } catch (Exception e) {

                }
            }

            sendCanboxInfo(0xc6, 0xa, otherSettings);

        } else if ("oil_unit".equals(key)) {

            int v = -1;
            try {
                v = Integer.valueOf((String) newValue);
            } catch (Exception e) {

            }
            if (v != -1) {
                sendCanboxInfo(0xc6, 0x3, v);
            }

        }

    }

    public boolean onPreferenceClick(Preference arg0) {
        if (arg0.getKey().equals("reset1")) {
            sendCanboxInfo(0xc6, 0x0d, 0x00);
        } else if (arg0.getKey().equals("reset2")) {
            sendCanboxInfo(0xc6, 0x0d, 0x01);
        } else if (arg0.getKey().equals("reset3")) {
            sendCanboxInfo(0xc6, 0x0d, 0x02);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception e) {

        }
        return false;
    }

    private void sendCanboxInfo(int d0) {

        byte[] buf = new byte[]{(byte) 0xff, 0x01, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 0 ? false : true);
            }
        }
    }

    private int mUnit = 0;

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case 0x38:

                mUnit = (buf[3] & 0xff);

                if ((buf[3] & 0x80) != 0) {
                    getPreferenceScreen().removePreference(mEditTextPreferenceSpeedAlarm);
                    ((SwitchPreference) findPreference("over_speed")).setChecked(true);
                } else {
                    getPreferenceScreen().addPreference(mEditTextPreferenceSpeedAlarm);
                    ((SwitchPreference) findPreference("over_speed")).setChecked(false);
                }

                String s;
                int index;
                index = ((buf[2] & 0xff));
                if ((mUnit & 0x8) == 0) {
                    s = index + " km";
                } else {
                    s = index + " mph";
                }
                mEditTextPreferenceSpeedAlarm.setSummary(s);

                s = "" + ((mUnit & 0x30) >> 4);
                ((ListPreference) findPreference("oil_unit")).setValue(s);
                ((ListPreference) findPreference("oil_unit")).setSummary("%s");

                HashSet<String> ss = new HashSet<String>();
                for (int i = 1; i <= 7; ++i) {
                    if ((buf[4] & (0x1 << i)) != 0) {
                        ss.add("" + i);
                    }
                }
                ((MultiSelectListPreference) findPreference("instrument_style")).setValues(ss);
                //			((MultiSelectListPreference)findPreference("instrument_style")).setSummary("%s");
                break;

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
                                Log.d("aa", "!!!!!!!!" + e);
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
