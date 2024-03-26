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

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MingjueRongweiSettingsRaiseFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "MingjueRongweiSettingsRaiseFragment";

    private int mType = 0;

    public void setType(int t) {
        mType = t;
    }

    private static final Node[] NODES = {

            new Node("key_driving_lock", 0xc60101, 0x39, 0x0, 0x0), new Node("key_unlock", 0xc60102, 0x39, 0x0, 0x0), new Node("key_unlock_mode", 0xc60103, 0x39, 0x0, 0x0),
            new Node("key_unlock_near_car", 0xc60104, 0x39, 0x0, 0x0), new Node("key_flow_me_home", 0xc60201, 0x39, 0x0, 0x0), new Node("key_flow_me_home_times", 0xc60202, 0x4001, 0xf, 0x0),
            new Node("key_search_car_indicator", 0xc60203, 0x39, 0x0, 0x0), new Node("key_search_car_indicator_times", 0xc60204, 0x4002, 0xf, 0x0),


            new Node("key_flow_me_home_switch", 0xc60301, 0x4100, 0x80, 0x0), new Node("key_search_car_indicator_type", 0xc60302, 0x4100, 0x40, 0x0),
            new Node("key_steering_handle", 0xc60303, 0x4100, 0x30, 0x0), new Node("greeting_lights", 0xc60304, 0x4108, 0x80, 0x0),

            new Node("reduction_of_fetal_pressure_key", 0xc60401, 0x4102, 0x80, 0x0), new Node("body_stability_control", 0xc60402, 0x4102, 0x40, 0x0),

            new Node("rear_window_defrost_defogging_linkage_key", 0xc60501, 0x39, 0x0, 0x0), new Node("volumeset_key", 0xc60502, 0x39, 0x0, 0x0),
            new Node("air_temperature_settings_key", 0xc60503, 0x4101, 0x18, 0x0),

            new Node("automatic_folding_of_rearview_mirror_key", 0xc60801, 0x39, 0x0, 0x0),
            // new Node("factory_reset_settings_key", 0xc60101, 0x39, 0x0, 0x0),
            new Node("warning_pedestrian_cues", 0xc60601, 0x4104, 0x80, 0x0),


            new Node("dashboard_brightness", 0xc60701, 0x39, 0x0, 0x0),


            new Node("warning_pedestrian_cues", 0xc60601, 0x39, 0x0, 0x0), new Node("warning_pedestrian_cues", 0xc60601, 0x39, 0x0, 0x0),


            new Node("sunroof", 0xc60901, 0x39, 0x0, 0x0), new Node("front_left_window", 0xc60902, 0x39, 0x0, 0x0), new Node("rear_left_window", 0xc60903, 0x39, 0x0, 0x0),
            new Node("front_right_window", 0xc60904, 0x39, 0x0, 0x0), new Node("rear_right_window", 0xc60905, 0x39, 0x0, 0x0),

            new Node("opening_degree_of_reserve_box", 0xc60906, 0x39, 0x0, 0x0), new Node("language1", 0xc60a01, 0x39, 0x0, 0x0),

            new Node("driving_mode", 0xc60b01, 0x5300, 0x0, 0x0),


            new Node("dynamic_response", 0xc60b04, 0x5301, 0x0, 0x0), new Node("steering_feel", 0xc60b04, 0x5301, 0x0, 0x0), new Node("comfortable_air_conditioning", 0xc60b04, 0x5301, 0x0, 0x0),


    };

    private final static int[] INIT_CMDS = {
            0x5300, 0x5100
    };

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rongwei_mingjue_settings_raise);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else if (mPreferences[i] instanceof MyPreferenceSeekBar) {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                    //					((MyPreferenceSeekBar) mPreferences[i])
                    //					.updateSeekBar(NODES[i].mShow, NODES[i].mType, 1);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
                }
            }
        }

        // findPreference("speeddata").setOnPreferenceClickListener(this);
        // for (Node node : NODES) {
        // String s = node.mKey;
        // if (findPreference(s) != null) {
        // findPreference(s).setOnPreferenceChangeListener(this);
        // }
        // }

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
        //		mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
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

    private Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
            }
        }
    };

    private void sendCanboxData(int cmd, int value) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

    }

    private void sendDriverMode() {
        byte[] buf = new byte[]{(byte) 0xc6, 0x03, (byte) 0xb, (byte) 0x4, (byte) mCustomDriverMode};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    int v = Integer.parseInt((String) newValue);
                    if ("driving_mode".equals(NODES[i].mKey)) {
                        byte[] buf = new byte[]{(byte) 0xc6, 0x03, (byte) 0xb, (byte) (v + 1), (byte) mCustomDriverMode};
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                        ((ListPreference) preference).setValue(String.valueOf(v));
                        ((ListPreference) preference).setSummary("%s");
                    } else if ("dynamic_response".equals(NODES[i].mKey)) {
                        mCustomDriverMode &= ~0xc0;
                        mCustomDriverMode |= (v << 6);
                        sendDriverMode();
                        ((ListPreference) preference).setValue(String.valueOf(v));
                        ((ListPreference) preference).setSummary("%s");
                    } else if ("steering_feel".equals(NODES[i].mKey)) {
                        mCustomDriverMode &= ~0x30;
                        mCustomDriverMode |= (v << 4);
                        sendDriverMode();
                        ((ListPreference) preference).setValue(String.valueOf(v));
                        ((ListPreference) preference).setSummary("%s");
                    } else if ("comfortable_air_conditioning".equals(NODES[i].mKey)) {
                        mCustomDriverMode &= ~0x08;
                        mCustomDriverMode |= (v << 3);
                        sendDriverMode();
                        ((ListPreference) preference).setValue(String.valueOf(v));
                        ((ListPreference) preference).setSummary("%s");
                    } else {
                        sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                    }

                } else if (preference instanceof SwitchPreference) {

                    sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);

                } else if (preference instanceof MyPreferenceSeekBar) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));

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


    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x03, (byte) d1, (byte) d2, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2, int d3) {
        byte[] buf = new byte[]{(byte) d0, 0x03, (byte) d1, (byte) d2, (byte) d3};
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
            } else if (p instanceof MyPreferenceSeekBar) {
                p.setSummary(index + "");
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
            value = ((buf[2] & 0xff) << 0);
        }
        if (buf.length > 4) {
            value |= ((buf[3] & 0xff) << 8);
        }
        if (buf.length > 5) {
            value |= ((buf[4] & 0xff) << 16);
        }
        if (buf.length > 6) {
            value |= ((buf[5] & 0xff) << 24);
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

    private byte mCustomDriverMode = 0;

    private void updateView(byte[] buf) {

        int mask;
        int cmd;
        int index;
        int value;
        if (buf[0] == 0x53) {
            mCustomDriverMode = buf[3];
            Preference p;
            int v;


            v = (buf[2] & 0xff);
            p = findPreference("driving_mode");
            ((ListPreference) p).setValue(v + "");
            ((ListPreference) p).setSummary("%s");


            v = (mCustomDriverMode & 0xc0) >> 6;
            p = findPreference("dynamic_response");
            ((ListPreference) p).setValue(v + "");
            ((ListPreference) p).setSummary("%s");

            v = (mCustomDriverMode & 0x30) >> 4;
            p = findPreference("steering_feel");
            ((ListPreference) p).setValue(v + "");
            ((ListPreference) p).setSummary("%s");

            v = (mCustomDriverMode & 0x08) >> 3;
            p = findPreference("comfortable_air_conditioning");
            ((ListPreference) p).setValue(v + "");
            ((ListPreference) p).setSummary("%s");

        } else {
            for (int i = 0; i < NODES.length; ++i) {

                if (NODES[i].mStatus <= 0xff) {
                    cmd = NODES[i].mStatus & 0xff;
                    int param1 = (NODES[i].mCmd & 0xff00) >> 8;
                    int param2 = (NODES[i].mCmd & 0xff) >> 0;
                    if (cmd == (buf[0] & 0xff) && param1 == (buf[2] & 0xff) && param2 == (buf[3] & 0xff)) {
                        value = buf[4];
                        setPreference(NODES[i].mKey, value);
                    }
                } else {
                    mask = (NODES[i].mMask);
                    cmd = (NODES[i].mStatus & 0xff00) >> 8;
                    index = (NODES[i].mStatus & 0xff) >> 0;
                    if (cmd == (buf[0] & 0xff)) {
                        value = getStatusValue1(buf[2 + index], mask);
                        setPreference(NODES[i].mKey, value);
                    }
                }

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
