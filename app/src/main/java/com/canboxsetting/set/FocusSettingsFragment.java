package com.canboxsetting.set;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.SwitchPreference;

import android.util.Log;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;

import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.preference.PreferenceFragmentCompat;

public class FocusSettingsFragment extends PreferenceFragmentCompat implements androidx.preference.Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "FocusSettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.focus_settings);

        findPreference("information_tone_volume").setOnPreferenceChangeListener(this);

        findPreference("warning_contorl").setOnPreferenceChangeListener(this);
        findPreference("warning_tone").setOnPreferenceChangeListener(this);

        findPreference("temp_dis_k").setOnPreferenceChangeListener(this);
        findPreference("turn_lights_set").setOnPreferenceChangeListener(this);
        findPreference("mileage_unit").setOnPreferenceChangeListener(this);
        findPreference("brightness").setOnPreferenceChangeListener(this);

        findPreference("control_system").setOnPreferenceChangeListener(this);

        //		findPreference("disc_indicator").setOnPreferenceChangeListener(this);
        //		findPreference("acoustic_key").setOnPreferenceChangeListener(this);
        //		findPreference("five_key").setOnPreferenceChangeListener(this);
        findPreference("off_Warning_info").setOnPreferenceChangeListener(this);

        findPreference("wipers_induction").setOnPreferenceChangeListener(this);
        findPreference("incar_light").setOnPreferenceChangeListener(this);
        findPreference("parking_mode").setOnPreferenceChangeListener(this);
        findPreference("ramp").setOnPreferenceChangeListener(this);
        findPreference("reverse_zoom").setOnPreferenceChangeListener(this);
        findPreference("enhanced_assistance").setOnPreferenceChangeListener(this);
        findPreference("tyre_monitor").setOnPreferenceClickListener(this);

        updateView(new byte[]{
                0x24, 0, 0, 0, 0, 0, 0, 0
        });

        updateView(new byte[]{
                0x21, 0, 0, 0, 0, 0, 0, 0
        });
        registerListener();

        updateLedSetting();
        updateShowWarning();
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
        sendCanboxInfo(0x90, 0x21, 0);
    }

    private int mLedSeting = 0;
    private final String LED_SETTINGS = "canboxsetting_ledsetting";

    private void updateLedSetting() {
        //		mLedSeting = Settings.System.getInt(getActivity().getContentResolver(),
        //				LED_SETTINGS, 0);
        //		if ((mLedSeting & 0x1) != 0) {
        //			((SwitchPreference) findPreference("disc_indicator"))
        //					.setChecked(true);
        //		}
        //
        //		if ((mLedSeting & 0x2) != 0) {
        //			((SwitchPreference) findPreference("acoustic_key"))
        //					.setChecked(true);
        //		}
        //
        //		if ((mLedSeting & 0x4) != 0) {
        //			((SwitchPreference) findPreference("five_key")).setChecked(true);
        //		}
        //
        //		sendCanboxInfo(0xa2, mLedSeting);
    }

    private void setLedSetting(int i) {
        mLedSeting = i;
        sendCanboxInfo(0xa2, i);
        Settings.System.putInt(getActivity().getContentResolver(), LED_SETTINGS, i);
    }

    private void updateShowWarning() {
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

        } catch (Exception e) {

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

        byte[] buf = new byte[]{
                (byte) 0xc6, 0x02, (byte) d0, (byte) d1
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {

        byte[] buf = new byte[]{
                (byte) 0xc6, 0x02, (byte) d0, (byte) d1, (byte) d2
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        int index;
        switch (buf[0]) {
            case 0x24:
                if ((buf[4] & 0x4) != 0) {
                    ((SwitchPreference) findPreference("information_tone_volume")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("information_tone_volume")).setChecked(false);
                }

                if ((buf[4] & 0x1) != 0) {
                    ((SwitchPreference) findPreference("control_system")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("control_system")).setChecked(false);
                }

                if ((buf[4] & 0x8) != 0) {
                    ((SwitchPreference) findPreference("warning_tone")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("warning_tone")).setChecked(false);
                }

                index = (int) ((buf[4] & 0x70) >> 4);
                ((ListPreference) findPreference("warning_contorl")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("warning_contorl")).setSummary(((ListPreference) findPreference("warning_contorl")).getEntry());

                // index = (int) ((buf[4] & 0x1) >> 0);
                // ((ListPreference) findPreference("temp_dis_k"))
                // .setValue(String.valueOf(index));
                // ((ListPreference) findPreference("temp_dis_k"))
                // .setSummary(((ListPreference) findPreference("temp_dis_k"))
                // .getEntry());

                index = (int) ((buf[4] & 0x2) >> 1);
                ((ListPreference) findPreference("turn_lights_set")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("turn_lights_set")).setSummary(((ListPreference) findPreference("turn_lights_set")).getEntry());

                index = (int) ((buf[4] & 0x80) >> 7);
                ((ListPreference) findPreference("mileage_unit")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("mileage_unit")).setSummary(((ListPreference) findPreference("mileage_unit")).getEntry());

                index = (int) ((buf[5] & 0x80) >> 7);
                ((ListPreference) findPreference("brightness")).setValue(String.valueOf(index));
                ((ListPreference) findPreference("brightness")).setSummary(((ListPreference) findPreference("brightness")).getEntry());

                if ((buf[7] & 0x1) != 0) {
                    ((SwitchPreference) findPreference("wipers_induction")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("wipers_induction")).setChecked(false);
                }
                if ((buf[7] & 0x2) != 0) {
                    ((SwitchPreference) findPreference("incar_light")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("incar_light")).setChecked(false);
                }
                if ((buf[7] & 0x4) != 0) {
                    ((SwitchPreference) findPreference("parking_mode")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("parking_mode")).setChecked(false);
                }

                if ((buf[7] & 0x8) != 0) {
                    ((SwitchPreference) findPreference("ramp")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("ramp")).setChecked(false);
                }

                if ((buf[7] & 0x40) != 0) {
                    ((SwitchPreference) findPreference("reverse_zoom")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("reverse_zoom")).setChecked(false);
                }

                if ((buf[7] & 0x20) != 0) {
                    ((SwitchPreference) findPreference("enhanced_assistance")).setChecked(true);
                } else {
                    ((SwitchPreference) findPreference("enhanced_assistance")).setChecked(false);
                }

                if ((buf[5] & 0x40) != 0) {
                    findPreference("tyre_monitor").setSummary("OK");
                } else {

                    findPreference("tyre_monitor").setSummary("");
                }
                break;

            case 0x21:

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

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (MyCmd.BROADCAST_SEND_FROM_CAN.equals(action)) {
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
