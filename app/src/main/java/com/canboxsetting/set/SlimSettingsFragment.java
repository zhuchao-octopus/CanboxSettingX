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
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.SettingProperties;
import com.common.utils.SystemProperties;
import com.common.utils.Util;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class SlimSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "FocusSettingsFragment";
    private SwitchPreference lane_departure_warning, power_down_auto_unlock, driving_auto_lock, approaching_lock, welcome_light, wireless_charging, mobile_forgotten_tips;
    private ListPreference avm_quitting_speed, backlight_level, over_speed_alarm, fatigue_driving, security_tips, clock_format, theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_settings);
        initView();
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


    private void initView() {
        lane_departure_warning = findPreference("lane_departure_warning");
        lane_departure_warning.setOnPreferenceChangeListener(this);
        avm_quitting_speed = findPreference("avm_quitting_speed");
        avm_quitting_speed.setOnPreferenceChangeListener(this);
        power_down_auto_unlock = findPreference("power_down_auto_unlock");
        power_down_auto_unlock.setOnPreferenceChangeListener(this);
        driving_auto_lock = findPreference("driving_auto_lock");
        driving_auto_lock.setOnPreferenceChangeListener(this);
        approaching_lock = findPreference("approaching_lock");
        approaching_lock.setOnPreferenceChangeListener(this);
        welcome_light = findPreference("welcome_light");
        welcome_light.setOnPreferenceChangeListener(this);
        backlight_level = findPreference("backlight_level");
        backlight_level.setOnPreferenceChangeListener(this);
        over_speed_alarm = findPreference("over_speed_alarm");
        over_speed_alarm.setOnPreferenceChangeListener(this);
        fatigue_driving = findPreference("fatigue_driving");
        fatigue_driving.setOnPreferenceChangeListener(this);
        security_tips = findPreference("security_tips");
        security_tips.setOnPreferenceChangeListener(this);
        wireless_charging = findPreference("wireless_charging");
        wireless_charging.setOnPreferenceChangeListener(this);
        mobile_forgotten_tips = findPreference("mobile_forgotten_tips");
        mobile_forgotten_tips.setOnPreferenceChangeListener(this);
        clock_format = findPreference("clock_format");
        clock_format.setOnPreferenceChangeListener(this);
        theme = findPreference("theme");
        theme.setOnPreferenceChangeListener(this);
        avm_quitting_speed.setSummary(avm_quitting_speed.getEntry());
        backlight_level.setSummary(backlight_level.getEntry());
        over_speed_alarm.setSummary(over_speed_alarm.getEntry());
        fatigue_driving.setSummary(fatigue_driving.getEntry());
        security_tips.setSummary(security_tips.getEntry());
        clock_format.setSummary(clock_format.getEntry());
        theme.setSummary(theme.getEntry());
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

    private void setLedSetting(byte i) {
        mLedSeting = i;
        sendCanboxInfo((byte) 0xa2, i);
        Settings.System.putInt(getActivity().getContentResolver(), LED_SETTINGS, i);
    }

    private void updateShowWarning() {
        int show = Settings.System.getInt(getActivity().getContentResolver(), SettingProperties.SHOW_FOCUS_CAR_WARNING_MSG, 0);

        if (show != 0) {
//            ((SwitchPreference) findPreference("off_Warning_info")).setChecked(true);
        }

    }

    private void setWarningSetting(boolean b) {
        int i = b ? 1 : 0;

        Settings.System.putInt(getActivity().getContentResolver(), SettingProperties.SHOW_FOCUS_CAR_WARNING_MSG, i);
        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD, SettingProperties.SHOW_FOCUS_CAR_WARNING_MSG);
        this.getActivity().sendBroadcast(it);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        MMLog.d(TAG, "onPreferenceChange: key = " + key + "   newValue = " + newValue);
        switch (key) {
            case "lane_departure_warning":
                if (newValue instanceof Boolean) {
                    boolean settingValue = (boolean) newValue;
                    sendCanboxInfo((byte) 0x90, (byte) (settingValue ? 0x01 : 0x00));
                    lane_departure_warning.setChecked(settingValue);
                }
                break;
            case "avm_quitting_speed":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x91, (byte) value);
                    avm_quitting_speed.setValue(String.valueOf(value));
                    avm_quitting_speed.setSummary(avm_quitting_speed.getEntry());
                }
                break;
            case "power_down_auto_unlock":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x92, (byte) (value ? 0x01 : 0x00));
                    power_down_auto_unlock.setChecked(value);
                }
                break;
            case "driving_auto_lock":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x93, (byte) (value ? 0x01 : 0x00));
                    driving_auto_lock.setChecked(value);
                }
                break;
            case "approaching_lock":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x94, (byte) (value ? 0x01 : 0x00));
                    approaching_lock.setChecked(value);
                }
                break;
            case "welcome_light":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x95, (byte) (value ? 0x01 : 0x00));
                    welcome_light.setChecked(value);
                }
                break;
            case "backlight_level":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x96, (byte) value);
                    backlight_level.setValue(String.valueOf(value));
                    backlight_level.setSummary(backlight_level.getEntry());
                }
                break;
            case "over_speed_alarm":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x97, (byte) value);
                    over_speed_alarm.setValue(String.valueOf(value));
                    over_speed_alarm.setSummary(over_speed_alarm.getEntry());
                }
                break;
            case "fatigue_driving":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x98, (byte) value);
                    fatigue_driving.setValue(String.valueOf(value));
                    fatigue_driving.setSummary(fatigue_driving.getEntry());
                }
                break;
            case "security_tips":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x99, (byte) value);
                    security_tips.setValue(String.valueOf(value));
                    security_tips.setSummary(security_tips.getEntry());
                }
                break;
            case "wireless_charging":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x9A, (byte) (value ? 0x01 : 0x00));
                    wireless_charging.setChecked(value);
                }
                break;
            case "mobile_forgotten_tips":
                if (newValue instanceof Boolean) {
                    boolean value = (boolean) newValue;
                    sendCanboxInfo((byte) 0x9B, (byte) (value ? 0x01 : 0x00));
                    mobile_forgotten_tips.setChecked(value);
                }
                break;
            case "clock_format":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x9C, (byte) value);
                    clock_format.setValue(String.valueOf(value));
                    clock_format.setSummary(clock_format.getEntry());
                }
                break;
            case "theme":
                if (newValue instanceof String) {
                    int value = Integer.parseInt((String) newValue);
                    sendCanboxInfo((byte) 0x9E, (byte) value);
                    theme.setValue(String.valueOf(value));
                    theme.setSummary(theme.getEntry());
                }
                break;
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        String key = arg0.getKey();
        if ("tyre_monitor".equals(key)) {
            sendCanboxInfo((byte) 0xa9, (byte) 0x1);
        }
        return false;
    }

    private void sendCanboxInfo(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)
        };
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {

        byte[] buf = new byte[]{
                (byte) 0xc6, 0x02, (byte) d0, (byte) d1, (byte) d2
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateView(byte[] buf) {
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x10:
                    lane_departure_warning.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x11:
                    avm_quitting_speed.setValue(String.valueOf(buf[4]));
                    avm_quitting_speed.setSummary(avm_quitting_speed.getEntry());
                    break;
                case 0x12:
                    power_down_auto_unlock.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x13:
                    driving_auto_lock.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x14:
                    approaching_lock.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x15:
                    welcome_light.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x16:
                    backlight_level.setValue(String.valueOf(buf[4]));
                    backlight_level.setSummary(backlight_level.getEntry());
                    break;
                case 0x17:
                    over_speed_alarm.setValue(String.valueOf(buf[4]));
                    over_speed_alarm.setSummary(over_speed_alarm.getEntry());
                    break;
                case 0x18:
                    fatigue_driving.setValue(String.valueOf(buf[4]));
                    fatigue_driving.setSummary(fatigue_driving.getEntry());
                    break;
                case 0x19:
                    security_tips.setValue(String.valueOf(buf[4]));
                    security_tips.setSummary(security_tips.getEntry());
                    break;
                case 0x1A:
                    wireless_charging.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x1B:
                    mobile_forgotten_tips.setChecked(buf[4] == 0x01 ? true : false);
                    break;
                case 0x1C:
                    clock_format.setValue(String.valueOf(buf[4]));
                    clock_format.setSummary(clock_format.getEntry());
                    break;
                case 0x1E:
                    theme.setValue(String.valueOf(buf[4]));
                    theme.setSummary(theme.getEntry());
                    break;
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
