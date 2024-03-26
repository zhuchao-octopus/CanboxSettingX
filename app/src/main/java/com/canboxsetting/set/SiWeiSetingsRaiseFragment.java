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
import com.common.util.NodePreference;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreferenceDialog;
import com.common.view.MyPreferenceSeekBar;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class SiWeiSetingsRaiseFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {

    private static final NodePreference[] NODES = {


            new NodePreference("early_warning_moving_objects", 0xc603, 0x41, 0x0008, 0), new NodePreference("door_opening_warning", 0xc604, 0x41, 0x0004, 0),
            new NodePreference("blind_spot_monitoring", 0xc605, 0x41, 0x0010, 0),

            new NodePreference("forward_collision_sensitivity", 0xc606, 0x41, 0x0160, 0, R.array.jac_fortification_prompt_sound, R.array.honda_entryValues),

            new NodePreference("gac_settings_departure_waring", 0xc607, 0x41, 0x0118, 0, R.array.jac_fortification_prompt_sound, R.array.honda_entryValues),


            new NodePreference("theme_color", 0xc608, 0x41, 0x0003, 0, R.array.swee_color_theme_entries, R.array.honda_entryValues),

            new NodePreference("langauage5", 0xc609, 0x41, 0x0104, 0, R.array.launage_entries3, R.array.launage_entryValues3),

            new NodePreference("ambient_light_switch", 0xc60b, 0x41, 0x0102, 0),

            new NodePreference("flow_me_home", 0xc60c, 0x41, 0x00e0, 0, R.array.external_lighting_delay_off, R.array.honda_entryValues),

            new NodePreference("steering_effort", 0xc60d, 0x41, 0x0180, 1, R.array.electronic_power_mode_selection_entries, R.array.honda_entryValues),

            new NodePreference("topic_association", 0xc60e, 0x41, 0x0101, 0),

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_setting);

        init();

    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                //				Log.d("ffck", i+"i="+ps+":"+p);
                if (ps instanceof PreferenceScreen) {
                    ((PreferenceScreen) ps).addPreference(p);

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    } else {
                        p.setOnPreferenceClickListener(this);
                    }
                }

            }
        }
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestInitData() {
        sendCanboxInfo(0x90, 0x41, 0x0);

        //		for (int i = 0; i < NODES.length; ++i) {
        //			if (NODES[i].mMask != 0) {
        //				mHandler.sendEmptyMessageDelayed(NODES[i].mMask, (i * 10));
        //			}
        //		}
    }

    //	private Handler mHandler = new Handler() {
    //		@Override
    //		public void handleMessage(Message msg) {
    //			if (!mPaused) {
    //				sendCanboxInfo(0x90, 0x52, msg.what & 0xff);
    //			}
    //		}
    //	};

    private void sendCanboxData(int cmd) {
        sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0));

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    int value = Integer.parseInt((String) newValue);

                    // if ("ba
                    // }

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, value);
                } else if (preference instanceof SwitchPreference) {
                    int value;
                    value = ((Boolean) newValue) ? 0x1 : 0x0;
                    // if (NODES[i].mShow == 0) {
                    // value ++;
                    // }

                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, value);
                } else if (preference instanceof Preference) {
                    sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8, NODES[i].mCmd & 0xff, 1);
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
            } else if (p instanceof MyPreferenceSeekBar) {
                p.setSummary(index + "");
            }
        }
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

    private void updateView(byte[] buf) {

        int value;

        for (int i = 0; i < NODES.length; ++i) {

            if (NODES[i].mStatus == (buf[0] & 0xff)) {
                int index = (NODES[i].mMask & 0xff00) >> 8;
                int mask = (NODES[i].mMask & 0xff);
                value = getStatusValue1(buf[index + 2], mask);

                if ((NODES[i].mType & 0xff) == 1) {
                    if (value == 0) {
                        value = 1;
                    } else {
                        value = 0;
                    }
                }

                setPreference(NODES[i].mKey, value);
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
