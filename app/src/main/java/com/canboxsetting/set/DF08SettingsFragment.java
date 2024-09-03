package com.canboxsetting.set;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.SettingProperties;
import com.common.utils.Util;
import com.common.view.MyPreferenceSeekBar;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class DF08SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "FocusSettingsFragment";
    private SwitchPreference speed_locking, flame_out_unlock, flow_me_home, steering_lighting, rearview_folding, wireless_charging_switch, glonass, horn_test_switch, precollision_warning, safety_distance_alarm, automatic_emergency_braking, aeb_switch;
    private ListPreference driving_mode, power_steering_mode, forward_impact_sensitivity, energy_recovery_control, range_mode, lka_mode_switching_signal, ldw_lka_sensitivity, fcw_aeb_sensitivity, remote_control_windows, welcome_lamp, car_type_setting;
    private CheckBoxPreference low_speed_buzzer_control;
    private MyPreferenceSeekBar compass_offset_setting;

    private Preference v2l_switch_status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.df08_settings);
        initView();
        registerListener();

    }


    private void initView() {
        speed_locking = initPreference("speed_locking");
        flame_out_unlock = initPreference("flame_out_unlock");
        flow_me_home = initPreference("flow_me_home");
        steering_lighting = initPreference("steering_lighting");
        rearview_folding = initPreference("rearview_folding");
        wireless_charging_switch = initPreference("wireless_charging_switch");
        glonass = initPreference("glonass");
        horn_test_switch = initPreference("horn_test_switch");
        precollision_warning = initPreference("precollision_warning");
        safety_distance_alarm = initPreference("safety_distance_alarm");
        automatic_emergency_braking = initPreference("automatic_emergency_braking");
        aeb_switch = initPreference("aeb_switch");
        driving_mode = initPreference("driving_mode");
        power_steering_mode = initPreference("power_steering_mode");
        forward_impact_sensitivity = initPreference("forward_impact_sensitivity");
        energy_recovery_control = initPreference("energy_recovery_control");
        range_mode = initPreference("range_mode");
        lka_mode_switching_signal = initPreference("lka_mode_switching_signal");
        ldw_lka_sensitivity = initPreference("ldw_lka_sensitivity");
        fcw_aeb_sensitivity = initPreference("fcw_aeb_sensitivity");
        remote_control_windows = initPreference("remote_control_windows");
        welcome_lamp = initPreference("welcome_lamp");
        car_type_setting = initPreference("car_type_setting");
        low_speed_buzzer_control = initPreference("low_speed_buzzer_control");
        compass_offset_setting = initPreference("compass_offset_setting");
        v2l_switch_status = findPreference("v2l_switch_status");

        remote_control_windows.setSummary(remote_control_windows.getEntry());
        driving_mode.setSummary(driving_mode.getEntry());
        power_steering_mode.setSummary(power_steering_mode.getEntry());
        range_mode.setSummary(range_mode.getEntry());
        welcome_lamp.setSummary(welcome_lamp.getEntry());
        energy_recovery_control.setSummary(energy_recovery_control.getEntry());
        v2l_switch_status.setSummary(v2l_switch_status.getSummary());
        forward_impact_sensitivity.setSummary(forward_impact_sensitivity.getEntry());
        lka_mode_switching_signal.setSummary(lka_mode_switching_signal.getEntry());
        ldw_lka_sensitivity.setSummary(ldw_lka_sensitivity.getEntry());
        fcw_aeb_sensitivity.setSummary(fcw_aeb_sensitivity.getEntry());
        car_type_setting.setSummary(car_type_setting.getEntry());
    }

    public <T extends Preference> T initPreference(@NonNull CharSequence key) {
        T preference = findPreference(key);
        preference.setOnPreferenceChangeListener(this);
        return preference;
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
        updateInfo();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        MMLog.d(TAG, "onPreferenceChange: key = " + preference.getKey() + "   newValue = " + newValue);
        String key = preference.getKey();
        switch (key) {
            case "speed_locking":
                sendCanboxInfo((byte) 0x6F, (byte) 0x01, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "flame_out_unlock":
                sendCanboxInfo((byte) 0x6F, (byte) 0x02, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "flow_me_home":
                sendCanboxInfo((byte) 0x6F, (byte) 0x03, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "steering_lighting":
                sendCanboxInfo((byte) 0x6F, (byte) 0x04, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "rearview_folding":
                sendCanboxInfo((byte) 0x6F, (byte) 0x05, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "wireless_charging_switch":
                sendCanboxInfo((byte) 0x6F, (byte) 0x06, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "glonass":
                sendCanboxInfo((byte) 0x6F, (byte) 0x09, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "horn_test_switch":
                sendCanboxInfo((byte) 0x6F, (byte) 0x0A, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "precollision_warning":
                sendCanboxInfo((byte) 0x6F, (byte) 0x0E, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "safety_distance_alarm":
                sendCanboxInfo((byte) 0x6F, (byte) 0x10, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "automatic_emergency_braking":
                sendCanboxInfo((byte) 0x6F, (byte) 0x11, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "aeb_switch":
                sendCanboxInfo((byte) 0x6F, (byte) 0x18, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "driving_mode":
                sendCanboxInfo((byte) 0x6F, (byte) 0x07, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "power_steering_mode":
                sendCanboxInfo((byte) 0x6F, (byte) 0x08, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "forward_impact_sensitivity":
                sendCanboxInfo((byte) 0x6F, (byte) 0x0F, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "energy_recovery_control":
                sendCanboxInfo((byte) 0x6F, (byte) 0x14, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "range_mode":
                sendCanboxInfo((byte) 0x6F, (byte) 0x15, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "lka_mode_switching_signal":
                sendCanboxInfo((byte) 0x6F, (byte) 0x16, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "ldw_lka_sensitivity":
                sendCanboxInfo((byte) 0x6F, (byte) 0x17, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "fcw_aeb_sensitivity":
                sendCanboxInfo((byte) 0x6F, (byte) 0x19, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "remote_control_windows":
                sendCanboxInfo((byte) 0x6F, (byte) 0x1A, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "welcome_lamp":
                sendCanboxInfo((byte) 0x6F, (byte) 0x1B, (byte) (Integer.parseInt((String) newValue)));
                break;
            case "car_type_setting":
                sendCarSetting((byte) (Integer.parseInt((String) newValue)), (byte) 0x26);
                car_type_setting.setValue((String) newValue);
                car_type_setting.setSummary(car_type_setting.getEntry());
                break;
            case "low_speed_buzzer_control":
                sendCanboxInfo((byte) 0x6F, (byte) 0x0C, (byte) ((boolean) newValue ? 0x01 : 0x00));
                break;
            case "compass_offset_setting":
                sendCanboxInfo((byte) 0x6F, (byte) 0x0D, (byte) (Integer.parseInt((String) newValue)));
                break;
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        MMLog.d(TAG, "onPreferenceClick: key = " + key);
        switch (key) {

        }
        return false;
    }

    private void updateInfo() {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0x61, (byte) 0xD3
        };
        MMLog.d(TAG, "updateInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(byte cmd, byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x04, cmd, d0, d1, (byte) 0xFF, (byte) 0xFF, 0x00
        };
        buf[buf.length - 1] = (byte) ((0x03 + cmd + d0 + d1 + 0xFF + 0xFF) & 0xFF);
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCarSetting(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x04, 0x24, d0, d1, 0x00
        };
        buf[buf.length - 1] = (byte) ((0x03 + 0x24 + d0 + d1 + 0xFF + 0xFF) & 0xFF);
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        MMLog.d(TAG, "updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        speed_locking.setChecked((buf[3] & 0x80) > 0 ? true : false);
        flame_out_unlock.setChecked((buf[3] & 0x40) > 0 ? true : false);
        flow_me_home.setChecked((buf[3] & 0x20) > 0 ? true : false);
        steering_lighting.setChecked((buf[3] & 0x10) > 0 ? true : false);
        rearview_folding.setChecked((buf[3] & 0x08) > 0 ? true : false);
        wireless_charging_switch.setChecked((buf[3] & 0x04) > 0 ? true : false);
        remote_control_windows.setValue(String.valueOf(buf[3] & 0x03));
        remote_control_windows.setSummary(remote_control_windows.getEntry());
        driving_mode.setValue(String.valueOf((buf[4] & 0x70) >> 4));
        driving_mode.setSummary(driving_mode.getEntry());
        power_steering_mode.setValue(String.valueOf((buf[4] & 0x0C) >> 2));
        power_steering_mode.setSummary(power_steering_mode.getEntry());
        range_mode.setValue(String.valueOf((buf[4] & 0x02) >> 1));
        range_mode.setSummary(range_mode.getEntry());
        glonass.setChecked((buf[5] & 0x80) > 0 ? true : false);
        welcome_lamp.setValue(String.valueOf((buf[5] & 0x1C) >> 3));
        welcome_lamp.setSummary(welcome_lamp.getEntry());
        horn_test_switch.setChecked((buf[6] & 0x80) > 0 ? true : false);
        energy_recovery_control.setValue(String.valueOf((buf[6] & 0x60) >> 5));
        energy_recovery_control.setSummary(energy_recovery_control.getEntry());
        low_speed_buzzer_control.setChecked((buf[6] & 0x10) > 0 ? true : false);
//        compass_offset_setting.setSummary(compass_offset_setting.getEntry());
        precollision_warning.setChecked((buf[8] & 0x80) > 0 ? true : false);
        v2l_switch_status.setSummary((buf[7] & 0x80) > 0 ? getString(R.string.abc_capital_on) : getString(R.string.abc_capital_off));
        forward_impact_sensitivity.setValue(String.valueOf((buf[8] & 0x60) >> 5));
        forward_impact_sensitivity.setSummary(forward_impact_sensitivity.getEntry());
        safety_distance_alarm.setChecked((buf[8] & 0x10) > 0 ? true : false);
        automatic_emergency_braking.setChecked((buf[8] & 0x08) > 0 ? true : false);
        lka_mode_switching_signal.setValue(String.valueOf((buf[10] & 0xC0) >> 6));
        lka_mode_switching_signal.setSummary(lka_mode_switching_signal.getEntry());
        ldw_lka_sensitivity.setValue(String.valueOf((buf[10] & 0x30) >> 4));
        ldw_lka_sensitivity.setSummary(ldw_lka_sensitivity.getEntry());
        aeb_switch.setChecked((buf[10] & 0x08) > 0 ? true : false);
        fcw_aeb_sensitivity.setValue(String.valueOf((buf[10] & 0x06) >> 1));
        fcw_aeb_sensitivity.setSummary(fcw_aeb_sensitivity.getEntry());
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
                            MMLog.d(TAG, "onReceive: SlimSettings buf = " + ByteUtils.BuffToHexStr(buf));
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView(buf);
                                    }
                                });
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
