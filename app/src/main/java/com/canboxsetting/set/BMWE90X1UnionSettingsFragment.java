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
import com.canboxsetting.R.string;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class BMWE90X1UnionSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "BMWE90X1UnionSettingsFragment";

    private static final Node[] NODES = {

            new Node("range", 0x8200, 0x0401), new Node("lang", 0x8201, 0x0401), new Node("age_full", 0x8202, 0x0401), new Node("temp", 0x8203, 0x0401),

            new Node("lef_hot", 0x8501, 0x0), new Node("rif_hot", 0x8502, 0x0), new Node("redar", 0x8503, 0x0), new Node("curtain", 0x8504, 0x0),

    };

    private final static int[] INIT_CMDS = {};

    private Preference[] mPreferences = new Preference[NODES.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.bmw_e90x1_union_settings);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            // Log.d("aa", mPreferences[i]+":"+NODES[i].mKey);
            if (mPreferences[i] != null) {
                if (mPreferences[i] instanceof PreferenceScreen) {
                    mPreferences[i].setOnPreferenceClickListener(this);
                } else {
                    mPreferences[i].setOnPreferenceChangeListener(this);
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

    private void requestInitData() {
        // for (int i = 0; i < INIT_CMDS.length; ++i) {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], i * 100);
        // }
    }

    // private Handler mHandler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // if (!mPaused) {
    // sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
    // }
    // }
    // };

    private void sendCanboxData(int cmd, int value) {
        int index = ((cmd & 0xff) >> 0);
        mBufUnit[index] = (byte) value;

        String strTimeFormat = Settings.System.getString(getActivity().getContentResolver(), android.provider.Settings.System.TIME_12_24);

        byte t24 = 1;
        if ("12".equals(strTimeFormat)) {
            t24 = 0;
        }

        byte[] buf = new byte[]{
                (byte) ((cmd & 0xff00) >> 8), 0x05, mBufUnit[0], mBufUnit[1], mBufUnit[2], mBufUnit[3], t24
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private void sendCanboxData(int cmd) {

        byte[] buf = new byte[]{
                (byte) ((cmd & 0xff00) >> 8), 0x2, (byte) ((cmd & 0xff) >> 0), 0x1
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

        Util.doSleep(300);
        buf[3] = 0x0;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private byte[] mBufUnit = new byte[4];

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {
                    sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
                } else if (preference instanceof PreferenceScreen) {
                    sendCanboxData(NODES[i].mCmd);
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

        try {
            udpatePreferenceValue(arg0, null);
        } catch (Exception e) {

        }

        return false;
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
            }
        }
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        try {
            switch (buf[0]) {
                case 0x4:
                    Util.byteArrayCopy(mBufUnit, buf, 0, 2, mBufUnit.length);

                    setPreference(NODES[0].mKey, buf[2]);
                    setPreference(NODES[1].mKey, buf[3]);
                    setPreference(NODES[2].mKey, buf[4]);
                    setPreference(NODES[3].mKey, buf[5]);

                    break;
                case 0x7:
                    String s = null;
                    int index;

                    index = ((buf[2] & 0xf0) >> 4);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = "" + index;
                    }
                    setPreference(NODES[4].mKey, s);

                    index = ((buf[2] & 0xf) >> 0);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = "" + index;
                    }
                    setPreference(NODES[5].mKey, s);

                    index = ((buf[3] & 0x1) >> 0);
                    if (index == 0) {
                        s = getString(R.string.close);
                    } else {
                        s = getString(R.string.open);
                    }
                    setPreference(NODES[6].mKey, s);

                    //				index = ((buf[2] & 0xf0) >> 4);
                    //				if (index == 0) {
                    //					s = getString(R.string.close);
                    //				} else {
                    //					s = "" + index;
                    //				}
                    //				setPreference(NODES[7].mKey, s);

                    break;
            }
        } catch (Exception e) {
            Log.d("allen", "err" + e);
        }
    }

    private void showPreference(String id, int show) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            if (show != 0) {
                if (findPreference(id) == null) {
                    getPreferenceScreen().addPreference(preference);
                }
            } else {
                if (findPreference(id) != null) {
                    getPreferenceScreen().removePreference(preference);
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
