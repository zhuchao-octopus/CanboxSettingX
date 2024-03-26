package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class PSASettingsBagooFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "GMSettingsSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.psa_bagoo_settings);

        findPreference("parking_assist").setOnPreferenceChangeListener(this);
        findPreference("bwiper").setOnPreferenceChangeListener(this);
        findPreference("daytime_lights").setOnPreferenceChangeListener(this);
        findPreference("am_backdoor").setOnPreferenceChangeListener(this);
        findPreference("auto_parking").setOnPreferenceChangeListener(this);


        ((SwitchPreference) findPreference("parking_assist")).setChecked(false);
        ((SwitchPreference) findPreference("bwiper")).setChecked(false);
        ((SwitchPreference) findPreference("daytime_lights")).setChecked(false);
        ((SwitchPreference) findPreference("am_backdoor")).setChecked(false);
        ((SwitchPreference) findPreference("auto_parking")).setChecked(false);

        findPreference("setdoor").setOnPreferenceChangeListener(this);
        findPreference("lamp_no").setOnPreferenceChangeListener(this);

        if (findPreference("temp_dis_k") != null) {
            findPreference("temp_dis_k").setOnPreferenceChangeListener(this);
        }

        findPreference("tpms_cal").setOnPreferenceClickListener(this);
        findPreference("tpms_calibration").setOnPreferenceClickListener(this);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        sendCanboxInfo(0xf1, 0x38);
        // Util.doSleep(100);
        // sendCanboxInfo(0x90, 0xd);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        try {
            if ("parking_assist".equals(key)) {
                sendCanboxInfo(0x80, 0x01, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("bwiper".equals(key)) {
                sendCanboxInfo(0x80, 0x02, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("daytime_lights".equals(key)) {
                sendCanboxInfo(0x80, 0x08, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("am_backdoor".equals(key)) {
                sendCanboxInfo(0x80, 0x03, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("auto_parking".equals(key)) {
                sendCanboxInfo(0x80, 0x0a, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("lamp_no".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0x80, 0x04, i);
            } else if ("setdoor".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0x80, 0x09, i);
            } else if ("temp_dis_k".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0x80, 0x07, i);
            }

        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("tpms_cal".equals(key)) {
            sendCanboxInfo(0xb0, 0x1);
        } else if ("tpms_calibration".equals(key)) {
            sendCanboxInfo(0x80, 0x22, 0x1);
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {

        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        int index;
        switch (buf[0]) {
            case 0x38:
                if ((buf[4] & 0x80) != 0) {
                    ((SwitchPreference) findPreference("parking_assist")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("parking_assist")).setChecked(false);
                }

                if ((buf[3] & 0x80) != 0) {
                    ((SwitchPreference) findPreference("bwiper")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("bwiper")).setChecked(false);
                }

                if ((buf[4] & 0x01) != 0) {
                    ((SwitchPreference) findPreference("daytime_lights")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("daytime_lights")).setChecked(false);
                }

                if ((buf[3] & 0x40) != 0) {
                    ((SwitchPreference) findPreference("am_backdoor")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("am_backdoor")).setChecked(false);
                }

                if ((buf[5] & 0x40) != 0) {
                    ((SwitchPreference) findPreference("auto_parking")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("auto_parking")).setChecked(false);
                }

                index = (int) ((buf[4] & 0x60) >> 5);
                ((ListPreference) findPreference("lamp_no")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("lamp_no")).setSummary(((ListPreference) findPreference("lamp_no")).getEntry());

                index = (int) ((buf[5] & 0x80) >> 7);
                ((ListPreference) findPreference("setdoor")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("setdoor")).setSummary(((ListPreference) findPreference("setdoor")).getEntry());

                if (findPreference("temp_dis_k") != null) {
                    index = (int) ((buf[4] & 0x02) >> 1);
                    ((ListPreference) findPreference("temp_dis_k")).setValue(String.valueOf(index));
                    ((ListPreference) findPreference("temp_dis_k")).setSummary(((ListPreference) findPreference("temp_dis_k")).getEntry());
                }
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

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}
