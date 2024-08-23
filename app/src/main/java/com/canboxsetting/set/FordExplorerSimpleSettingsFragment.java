package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import android.provider.Settings;
import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;

public class FordExplorerSimpleSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "FordExplorerSimpleSettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ford_explorer_settings);

        findPreference("temp_dis_k").setOnPreferenceChangeListener(this);
        //		findPreference("mileage_unit").setOnPreferenceChangeListener(this);
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
        sendCanboxInfo(0x90, 0x24, 0);
        Util.doSleep(200);
        sendCanboxInfo(0x90, 0x29, 0);
    }

    private int mLedSeting = 0;
    private final String LED_SETTINGS = "canboxsetting_ledsetting";

    private void updateLedSetting() {
        mLedSeting = Settings.System.getInt(getActivity().getContentResolver(), LED_SETTINGS, 0);
        if ((mLedSeting & 0x1) != 0) {
            ((SwitchPreference) findPreference("disc_indicator")).setChecked(true);
        }

        if ((mLedSeting & 0x2) != 0) {
            ((SwitchPreference) findPreference("acoustic_key")).setChecked(true);
        }

        if ((mLedSeting & 0x4) != 0) {
            ((SwitchPreference) findPreference("five_key")).setChecked(true);
        }

        sendCanboxInfo(0xa2, mLedSeting);
    }

    private void setLedSetting(int i) {
        mLedSeting = i;
        sendCanboxInfo(0xa2, i);
        Settings.System.putInt(getActivity().getContentResolver(), LED_SETTINGS, i);
    }

    private void udpateShowWarning() {
        int show = Settings.System.getInt(getActivity().getContentResolver(), SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG, 0);

        if (show != 0) {
            ((SwitchPreference) findPreference("off_Warning_info")).setChecked(true);
        }

    }

    private void setWarningSetting(boolean b) {
        int i = b ? 1 : 0;

        Settings.System.putInt(getActivity().getContentResolver(), SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG, i);
        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD, SystemConfig.SHOW_FOCUS_CAR_WARNING_MSG);
        this.getActivity().sendBroadcast(it);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        try {
            if ("information_tone_volume".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x6 : 0x5);
            } else if ("warning_tone".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x8 : 0x7);
            } else if ("control_system".equals(key)) {
                sendCanboxInfo(0xa3, ((Boolean) newValue) ? 0x1 : 0x2);
            } else if ("disc_indicator".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x1;
                } else {
                    mLedSeting &= ~0x1;
                }
                setLedSetting(mLedSeting);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("acoustic_key".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x2;
                } else {
                    mLedSeting &= ~0x2;
                }
                setLedSetting(mLedSeting);

                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("five_key".equals(key)) {
                if (((Boolean) newValue)) {
                    mLedSeting |= 0x4;
                } else {
                    mLedSeting &= ~0x4;
                }
                setLedSetting(mLedSeting);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("off_Warning_info".equals(key)) {
                setWarningSetting((Boolean) newValue);
                ((SwitchPreference) preference).setChecked(((Boolean) newValue));
            } else if ("warning_contorl".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x9);
            } else if ("temp_dis_k".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa0, i);
            } else if ("turn_lights_set".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x3);
            } else if ("mileage_unit".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0xe);
            } else if ("brightness".equals(key)) {
                int i = Integer.parseInt((String) newValue);
                sendCanboxInfo(0xa3, i + 0x10);
            } else if ("wipers_induction".equals(key)) {
                sendCanboxInfo(0xa5, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("incar_light".equals(key)) {
                sendCanboxInfo(0xa6, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("parking_mode".equals(key)) {
                sendCanboxInfo(0xa7, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("ramp".equals(key)) {
                sendCanboxInfo(0xa8, ((Boolean) newValue) ? 0x1 : 0x0);
            } else if ("reverse_zoom".equals(key)) {
                sendCanboxInfo(0xab, ((Boolean) newValue) ? 0x12 : 0x13);
            } else if ("enhanced_assistance".equals(key)) {
                sendCanboxInfo(0xad, ((Boolean) newValue) ? 0x1 : 0x0);
            }

        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("tyre_monitor".equals(key)) {
            sendCanboxInfo(0xa9, 0x1);
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {

        byte[] buf = new byte[]{(byte) 0xc6, 0x02, (byte) d0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        int index;
        switch (buf[0]) {
            case 0x24:
                index = (int) ((buf[4] & 0x80) >> 7);
                ((ListPreference) findPreference("mileage_unit")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("mileage_unit")).setSummary(((ListPreference) findPreference("mileage_unit")).getEntry());


            case 0x29:

                index = (byte) ((buf[6] & 0x40) >> 6);
                ((ListPreference) findPreference("temp_dis_k")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("temp_dis_k")).setSummary(((ListPreference) findPreference("temp_dis_k")).getEntry());
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

}
