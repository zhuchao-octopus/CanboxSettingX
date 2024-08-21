package com.canboxsetting.set;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.utils.Node;
import com.common.util.Util;
import com.common.view.MyPreferenceSeekBar;

import java.util.Objects;


public class VWMQBSettingsRaiseFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Golf7SettingsSimpleFragment";
    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private final Preference[] mPreferences = new Preference[NODES.length];
    private int mWarningUnit = 0;
    private String mWarningUnitText = "km/h";
    private boolean mPaused = true;

    private static final Node[] NODES = {

            new Node("speed_adjustment", 0xa806, 0x51000000, 0x06ff, 0, Node.TYPE_BUFF1_INDEX),

            new Node("all_settings_key", 0xc6c701, 0, 0), new Node("driver_assist_key", 0xc6c101, 0, 0), new Node("parking_and_manoeuvring_key", 0xc6c201, 0, 0), new Node("lights_key", 0xc6c301, 0, 0), new Node("mirrors_and_wipers_key", 0xc6c401, 0, 0), new Node("opening_and_closing_key", 0xc6c501, 0, 0), new Node("multifunction_display_key", 0xc6c601, 0, 0), new Node("ambient_lighting_reset", 0xc6c901, 0, 0), new Node("electric_driving_charging_hybrid", 0xc6ca01, 0, 0), new Node("seat_settings", 0xc6ce01, 0, 0),

            new Node("reset_long_term_key", 0xc68a, 0, 0), new Node("reset_since_start_key", 0xc68b, 0, 0),

            new Node("dynamic_light_assistant", 0xc65a, 0x40510000, 0x2000000),

            new Node("motorway_light", 0xc659, 0x40510000, 0x1000000),

            new Node("headlight_distance_adjustment_key", 0xc65f, 0x40510000, 0xe),

            new Node("car_type_cmd", 0xc64b, 0, 0),

            new Node("parking_assistance_key", 0xc63b, 0x40400000, 0x8),

            new Node("brightness_of_variable_channel_auxiliary_system", 0xc63f, 0x40310000, 0xf00000), new Node("preventive_member_protection_system_key", 0xc63e, 0x40300000, 0x200), new Node("lane_assist_activited_key", 0xc636, 0x40300000, 0x100),

            new Node("direct_tire_pressure_test_key", 0xc623, 0x40200000, 0xff0000), new Node("speed_warning_at_key", 0xc621, 0x40200000, 0xff00), new Node("driver_mode", 0xc6d0, 0x40a00000, 0xf),

            new Node("langauage", 0xc600, 0x400000ff, 0xff, 0x0), new Node("actionstate", 0xc610, 0x40100001, 0x3, 0x0),

            // new Node("car_key_activate", 0xc6CA, 0x40B00001, 0x1, 0x0),

            new Node("speedalarm", 0xc620, 0x40200001, 0x1, 0x0),

            new Node("speedunits1", 0x0, 0x40200000, 0x2, 0x0, Node.TYPE_DEFINE1), new Node("speeddata", 0x0, 0x40200000, 0xff00, 0x0, Node.TYPE_DEFINE1),

            new Node("lanesystem", 0xc630, 0x40300001, 0x1, 0x0), new Node("fatigueystem", 0xc631, 0x40300002, 0x2, 0x0), new Node("lastdistance", 0xc632, 0x40310001, 0x1, 0x0), new Node("frontasssystem", 0xc633, 0x40310002, 0x2, 0x0), new Node("warning", 0xc634, 0x40310004, 0x4, 0x0), new Node("showdistancecontrol", 0xc635, 0x40310000, 0x8, 0x0), new Node("accrdriveprogram", 0xc637, 0x40310000, 0xf00, 0x0), new Node("accvehicle", 0xc638, 0x40310000, 0xf000, 0x0),

            new Node("slow_down", 0xc639, 0x40310000, 0x10000, 0x0), new Node("blind_area_monitoring", 0xc63d, 0x40310000, 0x20000, 0x0), new Node("alert_prompt", 0xc63C, 0x40310000, 0xC0000, 0x0),

            new Node("autoaction", 0xc640, 0x40400000, 0x1), new Node("frontvol", 0xc641, 0x40400000, 0xf00), new Node("frontpitch", 0xc642, 0x40400000, 0xf000), new Node("rearvol", 0xc643, 0x40400000, 0xf0000), new Node("rearpitch", 0xc644, 0x40400000, 0xf00000),

            new Node("turnontime", 0xc650, 0x40500000, 0xf), new Node("autorunlights", 0xc651, 0x40500000, 0x10), new Node("turnlights", 0xc652, 0x40500000, 0x20), new Node("switchlight", 0xc653, 0x40500000, 0xff00), new Node("homeinmode", 0xc654, 0x40500000, 0xff0000), new Node("homeoutmode", 0xc655, 0x40500000, 0xff000000),

            new Node("travlemode", 0xc656, 0x40510000, 0x1), new Node("doorlight", 0xc657, 0x40510000, 0xff00), new Node("footlight", 0xc658, 0x40510000, 0xff0000),

            // new Node("background_lighting3", 0xc65B, 0x40520000, 0xff),
            // new Node("background_lighting4", 0xc65C, 0x40520000, 0xff00),
            // new Node("background_lighting2", 0xc65D, 0x40520000, 0xff000000),
            new Node("interior_space_light_color", 0xc65E, 0x40520000, 0x3f0000),

            new Node("mirrorsyncadj", 0xc660, 0x40600000, 0x1), new Node("mirrorlower", 0xc661, 0x40600000, 0x2), new Node("autorain", 0xc662, 0x40600000, 0x4), new Node("rearrain", 0xc663, 0x40600000, 0x8), new Node("wiper_maintenance_position_key", 0xc68c, 0x63220000, 0x1), new Node("parking", 0xc664, 0x40600000, 0x100),

            new Node("acousticconfirmation", 0xc675, 0x40700000, 0x4000), new Node("windowsopen", 0xc670, 0x40700000, 0xf), new Node("centrallock", 0xc671, 0x40700000, 0xf0), new Node("autolock", 0xc672, 0x40700000, 0x100),

            new Node("remote_control_key_memory_matching", 0xc6CB, 0x40B00000, 0x2), new Node("driver_seat_key", 0xc6C9, 0x40B00000, 0x4), new Node("front_window_key", 0xc666, 0x40700000, 0x400), new Node("rear_window_key", 0xc667, 0x40700000, 0x800), new Node("sliding_window_key", 0xc668, 0x40700000, 0x1000), new Node("induction_rear_rear_cover", 0xc674, 0x40700000, 0x200),

            new Node("miunit", 0xc690, 0x40900000, 0x1), new Node("speedunits", 0xc691, 0x40900000, 0x2), new Node("temperature", 0xc692, 0x40900000, 0x4), new Node("volume", 0xc693, 0x40900000, 0xf0), new Node("fulecons", 0xc694, 0x40900000, 0xf00), new Node("tireunit", 0xc695, 0x40900000, 0xf000),

            new Node("currentfuel", 0xc680, 0x40800000, 0x1), new Node("averagefuel", 0xc681, 0x40800000, 0x2), new Node("averageapeed", 0xc686, 0x40800000, 0x40), new Node("comfort", 0xc682, 0x40800000, 0x4), new Node("tipeconomy", 0xc683, 0x40800000, 0x8), new Node("digitalspeed", 0xc687, 0x40800000, 0x80), new Node("oil", 0xc689, 0x40800000, 0x200), new Node("mileage", 0xc685, 0x40800000, 0x20), new Node("traveltime", 0xc684, 0x40800000, 0x10), new Node("speedalarm1", 0xc688, 0x40800000, 0x100),


            new Node("vw_meter_mode", 0xc6e9, 0x40c10000, 0xff), new Node("vw_left_meter", 0xc6ea, 0x40c10000, 0xff00), new Node("vw_right_meter", 0xc6eb, 0x40c10000, 0xff0000),

            new Node("auto_supple_heater", 0xc6BD, 0x40c00000, 0x1),

            new Node("steer_heat", 0xc6AC, 0x21000000, 0x580, 0, Node.TYPE_BUFF1_INDEX),

            new Node("automatic_air", 0xc6b1, 0x21000000, 0x6ff, 0, Node.TYPE_BUFF1_INDEX), new Node("air_switch", 0xc6b2, 0x21000000, 0x0080, 0, Node.TYPE_BUFF1_INDEX), new Node("aqs_auto", 0xc6b0, 0x21000000, 0x420, 0, Node.TYPE_BUFF1_INDEX),

            new Node("radar_sound", 0xc6ab, 0x25000000, 0x1, 0, Node.TYPE_BUFF1), new Node("tpms", 0xc62201, 0x0, 0x0),


            new Node("ambient_lighting", 0xc64a, 0x40520000, 0xc00000),

            new Node("background_lighting1", 0xc65e, 0x40520000, 0xf0000), new Node("background_lighting2", 0xc65d, 0x40520000, 0xff000000), new Node("background_lighting3", 0xc65b, 0x40520000, 0xff), new Node("background_lighting4", 0xc65c, 0x40520000, 0xff00),

            new Node("background_lighting5", 0xc64d, 0x40530000, 0xff),

            new Node("reverse_video1", 0xc646, 0x40d00000, 0xff), new Node("reverse_video2", 0xc647, 0x40d00000, 0xff00), new Node("reverse_video3", 0xc648, 0x40d00000, 0xff0000), new Node("reverse_video4", 0xc649, 0x40d00000, 0xff000000),

            // drive

            // new Node("normal", 0xc6d0, 0x0, 0x0, 0x0, Node.TYPE_CUSTOM),
            // new Node("sport", 0xc6d0, 0x1, 0x0, 0x0, Node.TYPE_CUSTOM),
            // new Node("driv_eco", 0xc6d0, 0x2, 0x0, 0x0, Node.TYPE_CUSTOM),
            // new Node("individual", 0xc6d0, 0x3, 0x0, 0x0, Node.TYPE_CUSTOM),

            new Node("individual", 0xc6a0, 0x40b20000, 0x80), new Node("user_account_key", 0xc6a1, 0x40b20000, 0x70), new Node("key_distribution_key", 0xc6a2, 0x40b20000, 0x8), new Node("assign_the_key_the_current_user_account_key", 0xc6a3, 0x0, 0x0),

            // new Node("individual_steering", 0xc6d1, 0x40a00000, 0x100),
            // new Node("individual_engine", 0xc6d2, 0x40a00000, 0x1000),
            // new Node("individual_climate", 0xc6d3, 0x40a00000, 0x30000),

            new Node("individual_reset", 0xc6d4, 0x0, 0x0), new Node("reset_main_info", 0xc6d4, 0x0, 0x0),};

    //初始状态
    private final static int[] INIT_CMDS = {0x6310, 0x6311, 0x6320, 0x6321, 0x6322, 0x6300, 0x4000, 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4052, 0x4053, 0x4060, 0x4070, 0x4080, 0x4090, 0x40a0, 0x40b0, 0x40b1, 0x40b2, 0x40c0, 0x40c1, 0x51ff, 0x5080,
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};


    private void updateWarningAtUnit() {
        if (mWarningUnit == 0) {
            ((MyPreferenceSeekBar) findPreference("speed_warning_at_key")).updateSeekBar(30, 240, 10);
            mWarningUnitText = "km/h";
        } else {
            mWarningUnitText = "mph";
            ((MyPreferenceSeekBar) findPreference("speed_warning_at_key")).updateSeekBar(20, 150, 5);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.vw_mqb_raise_settings);
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
        updateWarningAtUnit();
        // findPreference("speeddata").setOnPreferenceClickListener(this);
        // for (Node node : NODES) {
        // String s = node.mKey;
        // if (findPreference(s) != null) {
        // findPreference(s).setOnPreferenceChangeListener(this);
        // }
        // }
        init();

        if (!(GlobalDef.getModelId() == 11 || GlobalDef.getModelId() == 42)) {
            ((PreferenceScreen) findPreference("service_settings")).removePreference(findPreference("oil_data"));
            //			((PreferenceScreen)findPreference("service_settings")).removePreference(findPreference("reset_main_info"));
        }

        if (!(GlobalDef.getModelId() == 11)) {
            //			((PreferenceScreen)findPreference("service_settings")).removePreference(findPreference("oil_data"));
            ((PreferenceScreen) findPreference("service_settings")).removePreference(findPreference("reset_main_info"));
        }
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {
        showPreferenceEnable("speed_warning_at_key", false);
        showPreferenceEnable("user_account_key", false);
        showPreferenceEnable("key_distribution_key", false);
        showPreferenceEnable("assign_the_key_the_current_user_account_key", false);
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

        if (mType == 1) {
            PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
            if (p != null) {
                setPreferenceScreen(p);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mType = 0;
    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 80));
        }
    }

    private final Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (Node node : NODES) {
            if (node.mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(node.mCmd, Integer.parseInt((String) newValue));

                    if (key.equals("car_type_cmd")) {
                        ((ListPreference) preference).setValue((String) newValue);
                        ((ListPreference) preference).setSummary("%s");
                    }
                } else if (preference instanceof SwitchPreference) {
                    if (node.mType == Node.TYPE_CUSTOM) {
                        sendCanboxData(node.mCmd, node.mStatus);
                    } else {
                        sendCanboxData(node.mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
                    }
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(node.mCmd);
                } else {
                    if (key.equals("speed_warning_at_key")) {
                        sendCanboxData(node.mCmd, Integer.parseInt((String) newValue));
                    } else if (key.equals("reset_long_term_key")) {
                        sendCanboxData(node.mCmd, 1);
                    } else if (key.equals("reset_since_start_key")) {
                        sendCanboxData(node.mCmd, 1);
                    }
                }
                break;
            }
        }
    }

    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        if (arg0.getKey().equals("individual_reset")) {
            sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("speeddata")) {
            // sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("reset_main_info")) {
            Dialog d = new AlertDialog.Builder(getActivity()).setTitle(R.string.reset_main_info).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    sendCanboxInfo(0xc6, 0x8d, 0x01);
                }
            }).setNegativeButton(android.R.string.cancel, null).show();
        } else {
            try {
                udpatePreferenceValue(arg0, null);
            } catch (Exception ignored) {
            }
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
            } else {
                if (key.equals("speed_warning_at_key")) {
                    p.setSummary(index + " " + mWarningUnitText);
                }
            }
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
        if (buf.length > 3) {
            value = ((buf[3] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[4] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[5] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value |= ((buf[6] & 0xff) << 24);
        }

        // } catch (Exception e) {
        // value = 0;
        // }

        return ((value & mask) >> start);
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

    private void updateVisible(byte[] buf) {
        switch (buf[2]) {
            case 0x10:
                showPreference("actionstate", buf[3] & 0x1, null);
                break;
            case 0x20:
                showPreference("speedalarm", buf[3] & 0x1, "tire");
                showPreference("speedunits1", buf[3] & 0x2, "tire");
                showPreference("speeddata", buf[3] & 0x4, "tire");
                break;
            case 0x30:
                showPreference("lanesystem", buf[3] & 0x4, "driverassistance");
                showPreference("fatigueystem", buf[3] & 0x2, "driverassistance");
                // showPreference("lanesystem", buf[3] & 0x4, "driverassistance");

                break;
            case 0x31:
                showPreference("lastdistance", buf[3] & 0x1, "driverassistance");
                showPreference("frontasssystem", buf[3] & 0x2, "driverassistance");
                showPreference("warning", buf[3] & 0x4, "driverassistance");
                showPreference("showdistancecontrol", buf[3] & 0x8, "driverassistance");
                showPreference("accrdriveprogram", buf[3] & 0x10, "driverassistance");
                showPreference("accvehicle", buf[3] & 0x20, "driverassistance");

                break;
            case 0x40:
                showPreference("autoaction", buf[3] & 0x1, "parkshunt");
                showPreference("frontvol", buf[3] & 0x2, "parkshunt");
                showPreference("frontpitch", buf[3] & 0x4, "parkshunt");
                showPreference("rearvol", buf[3] & 0x8, "parkshunt");
                showPreference("rearpitch", buf[3] & 0x10, "parkshunt");
                break;
            case 0x50:
                showPreference("turnontime", buf[3] & 0x1, "lights");
                showPreference("autorunlights", buf[3] & 0x2, "lights");
                showPreference("turnlights", buf[3] & 0x4, "lights");
                showPreference("switchlight", buf[3] & 0x8, "lights");
                showPreference("homeinmode", buf[3] & 0x10, "lights");
                showPreference("homeoutmode", buf[3] & 0x20, "lights");
                break;
            case 0x51:
                showPreference("travlemode", buf[3] & 0x1, "lights");
                showPreference("doorlight", buf[3] & 0x2, "lights");
                showPreference("footlight", buf[3] & 0x4, "lights");
                break;
            case 0x60:
                showPreference("mirrorsyncadj", buf[3] & 0x1, "mirrorswipers");
                showPreference("mirrorlower", buf[3] & 0x2, "mirrorswipers");
                showPreference("autorain", buf[3] & 0x4, "mirrorswipers");
                showPreference("rearrain", buf[3] & 0x8, "mirrorswipers");
                showPreference("parking", buf[3] & 0x10, "mirrorswipers");
                break;
            case 0x70:
                showPreference("windowsopen", buf[3] & 0x1, "onoff");
                showPreference("centrallock", buf[3] & 0x2, "onoff");
                showPreference("autolock", buf[3] & 0x4, "onoff");
                break;

            case (byte) 0x80:
                showPreference("currentfuel", buf[3] & 0x1, "multidisplay");
                showPreference("averagefuel", buf[3] & 0x2, "multidisplay");
                showPreference("comfort", buf[3] & 0x4, "multidisplay");
                showPreference("tipeconomy", buf[3] & 0x8, "multidisplay");
                showPreference("traveltime", buf[3] & 0x10, "multidisplay");
                showPreference("mileage", buf[3] & 0x20, "multidisplay");
                showPreference("averageapeed", buf[3] & 0x40, "multidisplay");
                showPreference("digitalspeed", buf[3] & 0x80, "multidisplay");

                showPreference("speedalarm1", buf[4] & 0x1, "multidisplay");
                showPreference("oil", buf[4] & 0x2, "multidisplay");
                break;
            case (byte) 0x90:
                showPreference("miunit", buf[3] & 0x1, "unit");
                showPreference("speedunits", buf[3] & 0x2, "unit");
                showPreference("temperature", buf[3] & 0x4, "unit");
                showPreference("volume", buf[3] & 0x8, "unit");
                showPreference("fulecons", buf[3] & 0x10, "unit");
                showPreference("tireunit", buf[3] & 0x20, "unit");
                break;

            case (byte) 0xb0:
                showPreference("car_key_activate", buf[3] & 0x1, null);
                break;
        }
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateService(byte[] buf) {
        String s = "";
        int index;
        switch (buf[2]) {
            case 0x0:
                byte[] name = new byte[buf.length - 4];
                Util.byteArrayCopy(name, buf, 0, 3, name.length);
                try {
                    s = new String(name, "GBK");
                } catch (Exception e) {

                }
                setPreference("vehicle", s);
                break;
            case 0x10:
                index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                if ((buf[3] & 0xf) == 0) {
                    s = "------";
                } else if ((buf[3] & 0xf) == 1) {
                    s = index + getString(R.string.days);
                } else {
                    s = getString(R.string.be_overdue) + index + getString(R.string.days);
                }
                setPreference("vi_days", s);
                break;

            case 0x11:
                index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                if ((buf[3] & 0xf0) == 0) {
                    s = " KM";
                } else {
                    s = " MI";
                }

                if ((buf[3] & 0xf) == 0) {
                    s = "------";
                } else if ((buf[3] & 0xf) == 1) {
                    s = index + s;
                } else {
                    s = getString(R.string.be_overdue) + index + s;
                }
                setPreference("vi_distance", s);
                break;

            case 0x20:
                index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                if ((buf[3] & 0xf) == 0) {
                    s = "------";
                } else if ((buf[3] & 0xf) == 1) {
                    s = index + getString(R.string.days);
                } else {
                    s = getString(R.string.be_overdue) + index + getString(R.string.days);
                }
                setPreference("oil_days", s);
                break;

            case 0x21:
                index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                if ((buf[3] & 0xf0) == 0) {
                    s = " KM";
                } else {
                    s = " MI";
                }

                if ((buf[3] & 0xf) == 0) {
                    s = "------";
                } else if ((buf[3] & 0xf) == 1) {
                    s = index + s;
                } else {
                    s = getString(R.string.be_overdue) + index + s;
                }
                setPreference("oil_change", s);
                break;

        }
    }

    private void updateOil(byte[] buf) {
        switch (buf[2] & 0xff) {
            case 0x80:
                setPreference("oil_data", (buf[3] & 0xff) + "");
                break;
        }
    }

    private void updateView(byte[] buf) {
        if (buf[0] == 0x41) {
            updateVisible(buf);
            return;
        }

        if (buf[0] == 0x63) {
            updateService(buf);
            return;
        }

        if (buf[0] == 0x50) {
            updateOil(buf);
        }

        if ((buf[0] & 0xff) == 0x40 && (buf[2] & 0xff) == 0xb2) {// driver
            // mode

            // ((SwitchPreference) findPreference("normal")).setChecked(false);
            // ((SwitchPreference) findPreference("sport")).setChecked(false);
            // ((SwitchPreference)
            // findPreference("driv_eco")).setChecked(false);
            // ((SwitchPreference)
            // findPreference("individual")).setChecked(false);
            if ((buf[3] & 0x80) != 0) {
                // ((SwitchPreference) findPreference("individual"))
                // .setChecked(true);

                showPreferenceEnable("user_account_key", true);
                showPreferenceEnable("key_distribution_key", true);
                showPreferenceEnable("assign_the_key_the_current_user_account_key", true);
                // showPreference("individual_steering", 1);
                // showPreference("individual_engine", 1);
                // showPreference("individual_climate", 1);
                // showPreference("individual_reset", 1);
            } else {
                // if ((buf[3] & 0x3) == 0) {
                // ((SwitchPreference) findPreference("normal"))
                // .setChecked(true);
                // } else if ((buf[3] & 0x3) == 1) {
                // ((SwitchPreference) findPreference("sport"))
                // .setChecked(true);
                // }
                // if ((buf[3] & 0x3) == 2) {
                // ((SwitchPreference) findPreference("driv_eco"))
                // .setChecked(true);
                // }
                // showPreference("individual_steering", 0);
                // showPreference("individual_engine", 0);
                // showPreference("individual_climate", 0);
                // showPreference("individual_reset", 0);

                showPreferenceEnable("user_account_key", false);
                showPreferenceEnable("key_distribution_key", false);
                showPreferenceEnable("assign_the_key_the_current_user_account_key", false);
            }

        } else if ((buf[0] & 0xff) == 0x40 && (buf[2] & 0xff) == 0x20) {
            if ((buf[3] & 0x2) != mWarningUnit) {
                mWarningUnit = (buf[3] & 0x20);
                updateWarningAtUnit();
            }
            if ((buf[3] & 0x1) != 0) {
                showPreferenceEnable("speed_warning_at_key", true);
            } else {
                showPreferenceEnable("speed_warning_at_key", false);
            }
        }

        int cmd;
        int param;
        int mask;
        int value;

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff000000) >> 24;
            param = (NODES[i].mStatus & 0xff0000) >> 16;

            if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
                if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == param) {
                    mask = (NODES[i].mMask);
                    value = getStatusValue(buf, mask);
                    if (NODES[i].mKey.equals("user_account_key")) {
                        value++;
                    }
                    setPreference(NODES[i].mKey, value);
                    // break;
                }
            } else if (NODES[i].mType == Node.TYPE_BUFF1) {
                if ((buf[0] & 0xff) == cmd) {
                    mask = (NODES[i].mMask);
                    value = getStatusValue1(buf[2], mask);
                    setPreference(NODES[i].mKey, value);
                    // break;
                }
            } else if (NODES[i].mType == Node.TYPE_BUFF1_INDEX) {
                if ((buf[0] & 0xff) == cmd) {
                    mask = (NODES[i].mMask & 0xff);
                    int index = ((NODES[i].mMask & 0xff00) >> 8);
                    value = getStatusValue1(buf[2 + index], mask);
                    setPreference(NODES[i].mKey, value);
                    // break;
                }
            } else if (NODES[i].mType == Node.TYPE_DEFINE1) {

                // new Node("speedunits1", 0x0, 0x40200000, 0x2,
                // 0x0,Node.TYPE_DEFINE1),
                // new Node("speeddata", 0x0, 0x40200000, 0xff00,
                // 0x0,Node.TYPE_DEFINE1),

                if (NODES[i].mKey.equals("speedunits1")) {
                    if (findPreference("speedunits1") != null) {
                        if ((buf[3] & 0x2) == 0) {
                            findPreference("speedunits1").setSummary("km/h");
                        } else {
                            findPreference("speedunits1").setSummary("mph");
                        }
                    }
                    // mSpeedUnit = (byte)(buf[3]&0x2);
                } else if (NODES[i].mKey.equals("speeddata")) {
                    String s;
                    if ((buf[3] & 0x2) == 0) {
                        s = " km/h";
                    } else {
                        s = " mph";
                    }
                    if (findPreference("speeddata") != null) {
                        findPreference("speeddata").setSummary(buf[4] + s);
                    }
                }

            }

        }

    }

    private void showPreference(String id, int show, String parant) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            PreferenceScreen ps;
            if (parant != null) {
                ps = (PreferenceScreen) findPreference(parant);
            } else {
                ps = getPreferenceScreen();
            }
            if (ps != null) {
                if (show != 0) {
                    if (ps.findPreference(id) == null) {
                        ps.addPreference(preference);
                    }
                } else {
                    if (findPreference(id) != null) {
                        boolean b = ps.removePreference(preference);
                        // Log.d("dd", "" + b);
                    }
                }
            }
        }

    }

    private void showPreference(String id, int show) {
        showPreference(id, show, "driving_mode");
    }

    private void showPreferenceEnable(String id, boolean enabled) {
        Preference ps = (Preference) findPreference(id);
        if (ps != null) {
            ps.setEnabled(enabled);
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
