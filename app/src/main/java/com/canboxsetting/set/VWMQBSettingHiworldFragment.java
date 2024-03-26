package com.canboxsetting.set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreferenceSeekBar;
import com.common.view.MyPreferenceDialog;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class VWMQBSettingHiworldFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "VWMQBSettingHiworldFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {
            //ESC系统
            new Node("esc_setting", 0x8a03, 0x85010000, 0x3000, 0, Node.TYPE_BUFF1_INDEX),
            //轮胎设置
            new Node("tpms", 0x4B01, 0, 0, 0, Node.TYPE_BUFF1_INDEX),

            new Node("speedalarm", 0x4B02, 0x46000000, 0x8000, 0, Node.TYPE_BUFF3),

            new Node("speed_warning_at_key", 0x4B03, 0x46000000, 0xFF0000, 0, Node.TYPE_BUFF3),

            new Node("direct_tire_pressure_test_key", 0x4B04, 0x46000000, 0x6000, 0, Node.TYPE_BUFF3),
            //驾驶员辅助系统
            new Node("accrdriveprogram", 0x4C09, 0x47020000, 0xc0000000, 0, Node.TYPE_BUFF14),

            new Node("lastdistance", 0x4C0B, 0x47020000, 0x20000000, 0, Node.TYPE_BUFF14),

            new Node("accvehicle", 0x4C08, 0x47020000, 0x1F000000, 0, Node.TYPE_BUFF14),

            new Node("frontasssystem", 0x4C01, 0x47030000, 0x80, 0, Node.TYPE_BUFF14),

            new Node("warning", 0x4C02, 0x47030000, 0x40, 0, Node.TYPE_BUFF14),

            new Node("showdistancecontrol", 0x4C03, 0x47030000, 0x20, 0, Node.TYPE_BUFF14),

            new Node("fatigueystem", 0x4C06, 0x47030000, 0x80000000, 0, Node.TYPE_BUFF14),

            new Node("adaptive_lane_orientation", 0x4C04, 0x47030000, 0x8000, 0, Node.TYPE_BUFF14),

            new Node("activited", 0x4C0A, 0x47040000, 0x8000, 0, Node.TYPE_BUFF14),

            new Node("traffice_sign_recognition", 0x4C05, 0x47030000, 0x800000, 0, Node.TYPE_BUFF14),
            ////灯管设置
            new Node("turnontime", 0x6D03, 0x68010000, 0x30000000, 0, Node.TYPE_BUFF9),

            new Node("autorunlights", 0x6D04, 0x68010000, 0x08000000, 0, Node.TYPE_BUFF9),

            new Node("turnlights", 0x6D05, 0x68010000, 0x04000000, 0, Node.TYPE_BUFF9),

            new Node("brightness_of_variable_channel_auxiliary_system", 0x6D12, 0x67020000, 0xFF00, 0, Node.TYPE_BUFF9),

            new Node("travlemode", 0x6D06, 0x68010000, 0x02000000, 0, Node.TYPE_BUFF9),

            new Node("dynamic_light_assistant", 0x6D01, 0x68010000, 0x80000000, 0, Node.TYPE_BUFF9),

            new Node("motorway_light", 0x6D02, 0x68010000, 0x40000000, 0, Node.TYPE_BUFF9),

            new Node("headlight_distance_adjustment", 0x6D11, 0x67010000, 0xFF00, 0, Node.TYPE_BUFF9),

            new Node("switchlight", 0x6D07, 0x68020000, 0xFF, 0, Node.TYPE_BUFF9),

            new Node("doorlight", 0x6D08, 0x68020000, 0xFF00, 0, Node.TYPE_BUFF9),

            new Node("footlight", 0x6D09, 0x68020000, 0xFF0000, 0, Node.TYPE_BUFF9),

            new Node("ambient_lighting_three_color", 0x6D0C, 0x67010000, 0xff0000, 0, Node.TYPE_BUFF9),

            new Node("ambient_lighting_ten_color", 0x6D0C, 0x67010000, 0xff0000, 0, Node.TYPE_BUFF9),

            new Node("ambient_lighting_thirty_color", 0x6D0C, 0x67010000, 0xff0000, 0, Node.TYPE_BUFF9),

            new Node("atmosphere_lighting_car", 0x6D0D, 0x67010000, 0xff000000, 0, Node.TYPE_BUFF9),

            new Node("right_front_adjustable_lighting", 0x6D0E, 0x67020000, 0xff, 0, Node.TYPE_BUFF9),

            new Node("str_daytime_running_lamp", 0x6D10, 0x68010000, 0x01000000, 0, Node.TYPE_BUFF9),
            //所有区域暂时未发现接受字符串
            new Node("all_area", 0x6D0F, 0x51000000, 0x06ff, 0, Node.TYPE_BUFF1_INDEX),
            //范围只有30秒
            new Node("homeinmode", 0x6D0A, 0x68020000, 0xFF000000, 0, Node.TYPE_BUFF9),

            new Node("homeoutmode", 0x6D0B, 0x68030000, 0xFF, 0, Node.TYPE_BUFF9),

            //			new Node("car_type_cmd", 0x6D0B, 0x51000000, 0x06ff, 0,
            //						Node.TYPE_BUFF1_INDEX),		车行内容
            ////
            //后视镜和雨刮
            new Node("mirrorsyncadj", 0x6E01, 0x69000000, 0x800000, 0, Node.TYPE_BUFF4),

            new Node("mirrorlower", 0x6E02, 0x69000000, 0x400000, 0, Node.TYPE_BUFF4),

            new Node("autorain", 0x6E04, 0x69000000, 0x80000000, 0, Node.TYPE_BUFF4),

            new Node("rearrain", 0x6E05, 0x69000000, 0x40000000, 0, Node.TYPE_BUFF4),

            new Node("parking", 0x6E03, 0x69000000, 0x200000, 0, Node.TYPE_BUFF4),
            //
            //开光/设置
            new Node("windowsopen", 0x6F01, 0x64000000, 0xc00000, 0, Node.TYPE_BUFF4),

            new Node("centrallock", 0x6F02, 0x64000000, 0xc0000000, 0, Node.TYPE_BUFF4),

            new Node("autolock", 0x6F03, 0x64000000, 0x20000000, 0, Node.TYPE_BUFF4),

            new Node("remote_control_key_memory_matching", 0x6F04, 0x64000000, 0x10000000, 0, Node.TYPE_BUFF4),

            new Node("inductive_trunk_lid", 0x6F05, 0x64000000, 0x08000000, 0, Node.TYPE_BUFF4),
            //多功能显示器
            new Node("currentfuel", 0x7B01, 0x76000000, 0x800000, Node.TYPE_BUFF4),

            new Node("averagefuel", 0x7B02, 0x76000000, 0x400000, Node.TYPE_BUFF4),

            new Node("comfort", 0x7B03, 0x76000000, 0x200000, 0, Node.TYPE_BUFF4),

            new Node("tipeconomy", 0x7B04, 0x76000000, 0x100000, 0, Node.TYPE_BUFF4),

            new Node("traveltime1", 0x7B05, 0x76000000, 0x080000, 0, Node.TYPE_BUFF4),

            new Node("mileage", 0x7B06, 0x76000000, 0x040000, 0, Node.TYPE_BUFF4),

            new Node("avg_speed", 0x7B07, 0x76000000, 0x020000, 0, Node.TYPE_BUFF4),

            new Node("data_dis", 0x7B08, 0x76000000, 0x010000, 0, Node.TYPE_BUFF4),

            new Node("alarmspeed", 0x7B09, 0x76000000, 0x80000000, 0, Node.TYPE_BUFF4),

            new Node("oil_temp", 0x7B0A, 0x76000000, 0x40000000, 0, Node.TYPE_BUFF4),

            new Node("reset_since_start_key", 0x7B0B, 0x0000000, 0x00, 0, Node.TYPE_BUFF1_INDEX),

            new Node("reset_long_term_key", 0x7B0C, 0x0000000, 0x00, 0, Node.TYPE_BUFF1_INDEX),
            //单位:
            new Node("miunit", 0xCA01, 0xc1000000, 0x8000, 0, Node.TYPE_BUFF3),

            new Node("speedunits", 0xCA02, 0xc1000000, 0x4000, 0, Node.TYPE_BUFF3),

            new Node("temperature", 0xCA03, 0xc1000000, 0x2000, 0, Node.TYPE_BUFF3),

            new Node("volume", 0xCA04, 0xc1000000, 0x1800, 0, Node.TYPE_BUFF3),

            new Node("fulecons", 0xCA05, 0xc1000000, 0x0600, 0, Node.TYPE_BUFF3),
            ////耗电量
            new Node("tireunit", 0xCA06, 0xc1000000, 0xc00000, 0, Node.TYPE_BUFF3),
            //保养信息
            ////工厂设置
            new Node("all_settings_key", 0x1A80, 0x00000000, 0x00, 0, 0),

            new Node("driver_assist_key", 0x1A40, 0x00000000, 0x00, 0, 0),

            new Node("parking_and_manoeuvring_key", 0x1A20, 0x00000000, 0x00, 0, 0),

            new Node("light", 0x1A10, 0x00000000, 0x00, 0, 0),

            new Node("mirrors_and_wipers_key", 0x1A08, 0x00000000, 0x00, 0, 0),

            new Node("opening_and_closing_key", 0x1A04, 0x00000000, 0x00, 0, 0),

            new Node("multifunction_display_key", 0x1A02, 0x00000000, 0x00, 0, 0),

            //			new Node("ambient_lighting", 0xCA07, 0x51000000, 0x06ff, 0,
            //					Node.TYPE_BUFF1_INDEX),
            //
            //			new Node("electric_driving_charging_hybrid", 0xCA07, 0x51000000, 0x06ff, 0,
            //				Node.TYPE_BUFF1_INDEX),
            //
            //			new Node("seat_settings", 0xCA07, 0x51000000, 0x06ff, 0,
            //					Node.TYPE_BUFF1_INDEX),
    };

    //初始状态
    private final static int[] INIT_CMDS = {
            0x35, 0x45, 0x67, 0x68, 0x69, 0x64, 0x76, 0x85, 0x86, 0x87, 0x88, 0xc1, 0x1f, 0x1e, 0x75, 0x77, 0x48
    };

    private Preference[] mPreferences = new Preference[NODES.length];

    private int mWarningUnit = 0;
    private String mWarningUnitText = "km/h";

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

        addPreferencesFromResource(R.xml.vw_mqb_hiworld_settings);

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
        init();

    }

    private void init() {
        //		showPreferenceEnable("speed_warning_at_key", false);
        //		showPreferenceEnable("user_account_key", false);
        //		showPreferenceEnable("key_distribution_key", false);
        //		showPreferenceEnable("assign_the_key_the_current_user_account_key",
        //				false);
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
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

            }
        }
    };

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{0x2, (byte) 0xa, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo((cmd & 0xff00) >> 8, (cmd & 0xff) >> 0);

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
                } else if (preference instanceof PreferenceScreen) {
                    if (preference.getKey().equals("tpms")) {
                        sendCanboxData(NODES[i].mCmd, 0x1);
                    } else {
                        sendCanboxData(NODES[i].mCmd);
                    }
                } else if (preference instanceof MyPreferenceSeekBar) {
                    try {
                        int v = Integer.valueOf((String) newValue);
                        sendCanboxData(NODES[i].mCmd, v);
                    } catch (Exception e) {

                    }
                    Log.d("ffck", "!!" + (String) newValue);
                } else if (preference instanceof MyPreferenceDialog) {
                    sendCanboxData(NODES[i].mCmd, 0x1);
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
        if (arg0.getKey().equals("individual_reset")) {
            sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else if (arg0.getKey().equals("speeddata")) {
            // sendCanboxInfo(0xc6, 0xd4, 0x01);
        } else {
            try {
                udpatePreferenceValue(arg0, null);
            } catch (Exception e) {

            }
        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{0x01, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) 0x02, (byte) d0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof ListPreference) {
                ListPreference lp = (ListPreference) p;
                int value = 0;
                value = GetValueListPreference(key, index);
                lp.setValue(String.valueOf(value));
                lp.setSummary("%s");
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 0 ? false : true);
                displaySwitchcablity(key, index);
            } else if (p instanceof MyPreferenceSeekBar) {
                if (key.equals("speed_warning_at_key")) {
                    p.setSummary(index + " " + mWarningUnitText);
                } else if (key.equals("atmosphere_lighting_car") || key.equals("right_front_adjustable_lighting")) {
                    p.setSummary(index + " " + "%");
                } else {
                    p.setSummary(index + "");
                }
            }
        }
    }

    private int GetValueListPreference(String key, int index) {
        int value = index;
        if (key == "accrdriveprogram") {
            switch (index) {
                case 0: {
                    value = 1;
                    break;
                }
                case 1: {
                    value = 2;
                    break;
                }
                case 2: {
                    value = 3;
                    break;
                }
            }
        }

        if (key == "miunit") {
            switch (index) {
                case 0: {
                    value = 2;
                    break;
                }
                case 1: {
                    value = 1;
                    break;
                }
            }
        }

        if (key == "speedunits") {
            switch (index) {
                case 0: {
                    value = 2;
                    break;
                }
                case 1: {
                    value = 1;
                    break;
                }
            }
        }

        if (key == "temperature") {
            switch (index) {
                case 0: {
                    value = 2;
                    break;
                }
                case 1: {
                    value = 1;
                    break;
                }
            }
        }

        if (key == "volume") {
            switch (index) {
                case 0: {
                    value = 1;
                    break;
                }
                case 2: {
                    value = 3;
                    break;
                }
                case 1: {
                    value = 2;
                    break;
                }
            }
        }

        if (key == "fulecons") {
            switch (index) {
                case 0: {
                    value = 1;
                    break;
                }
                case 2: {
                    value = 3;
                    break;
                }
                case 1: {
                    value = 2;
                    break;
                }
                case 3: {
                    value = 4;
                    break;
                }
            }
        }

        if (key == "tireunit") {
            switch (index) {
                case 0: {
                    value = 1;
                    break;
                }
                case 2: {
                    value = 3;
                    break;
                }
                case 1: {
                    value = 2;
                    break;
                }
            }
        }
        return value;
    }

    private void displaySwitchcablity(String key, int weather) {
        if (key == "speedalarm") {
            showPreferenceEnable("speed_warning_at_key", weather == 0 ? false : true);
        }
    }

    private int getStatusValue(byte[] buf, int mask, int subpostion) {

        int value = 0;
        int value1 = 0;
        int value2 = 0;
        int value3 = 0;
        int value4 = 0;
        int start = 0;
        int i;
        for (i = 0; i < 32; i++) {
            if ((mask & (0x1 << i)) != 0) {
                start = i;
                break;
            }
        }
        value1 = 0;
        if (buf.length > 3) {
            value1 = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value1 |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value1 |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value1 |= ((buf[5] & 0xff) << 24);
        }

        value2 = 0;
        if (buf.length > 7) {
            value2 = ((buf[6] & 0xff) << 0);
        }
        if (buf.length > 8) {
            value2 |= ((buf[7] & 0xff) << 8);
        }
        if (buf.length > 9) {
            value2 |= ((buf[8] & 0xff) << 16);
        }
        if (buf.length > 10) {
            value2 |= ((buf[9] & 0xff) << 24);
        }

        value3 = 0;
        if (buf.length > 11) {
            value3 = ((buf[10] & 0xff) << 0);
        }
        if (buf.length > 12) {
            value3 |= ((buf[11] & 0xff) << 8);
        }
        if (buf.length > 13) {
            value3 |= ((buf[12] & 0xff) << 16);
        }
        if (buf.length > 14) {
            value3 |= ((buf[13] & 0xff) << 24);
        }

        value4 = 0;
        if (buf.length > 15) {
            value4 = ((buf[14] & 0xff) << 0);
        }
        if (buf.length > 16) {
            value4 |= ((buf[15] & 0xff) << 8);
        }
        if (buf.length > 17) {
            value4 |= ((buf[16] & 0xff) << 16);
        }
        if (buf.length > 18) {
            value4 |= ((buf[17] & 0xff) << 24);
        }
        switch (subpostion) {
            case 0: {
                value = ((value1 & mask) >>> start);
                break;
            }
            case 1: {
                value = ((value1 & mask) >>> start);
                break;
            }
            case 2: {
                value = ((value2 & mask) >>> start);
                break;
            }
            case 3: {
                value = ((value3 & mask) >>> start);
                break;
            }
            case 4: {
                value = ((value4 & mask) >>> start);
                break;
            }

        }
        return value;
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

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {
        int cmd;
        int subpostion;
        int mask;
        int value;
        int index = 0;
        String str;
        String s = "";
        switch (buf[0]) {
            case (byte) 0x1E: {
                //车况检查天数
                index = buf[5] & 0xff;
                s = "" + index;
                setPreference("vi_days", s);

                //车况检查里程
                index = (buf[7] & 0xff);
                s = "" + index;
                setPreference("vi_distance", s);

                //更换机油保养天数
                index = (buf[9] & 0xff);
                s = "" + index;
                setPreference("oil_days", s);

                //更换机油保养里程
                index = (buf[11] & 0xff);
                s = "" + index;
                setPreference("oil_change", s);
            }
            break;
        }

        for (int i = 0; i < NODES.length; ++i) {
            cmd = (NODES[i].mStatus & 0xff000000) >>> 24;
            subpostion = (NODES[i].mStatus & 0xff0000) >>> 16;
            mask = NODES[i].mMask;
            if ((buf[0] & 0xff) == cmd) {
                value = getStatusValue(buf, mask, subpostion);
                setPreference(NODES[i].mKey, value);
                // break;
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
