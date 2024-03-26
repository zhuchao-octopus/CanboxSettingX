package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
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
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Info151 extends PreferenceFragment {
    private static final String TAG = "Golf7InfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ford_quanxun_info);

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

    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case 0x42:

                index = ((buf[2] & 0xff));
                if (index == 0xff) {
                    s = " -- ";
                } else {
                    s = index / 10 + "." + index % 10 + " L";
                }
                setPreference("averagefuel", s);


                index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8));

                s = index + " km";

                setPreference("mileage", s);


                index = ((buf[5] & 0xff));
                s = index + " %";
                setPreference("beoilmass", s);

                index = ((buf[6] & 0xff) | ((buf[7] & 0xff) << 8) | ((buf[8] & 0xff) << 16));

                s = index / 10 + "." + index % 10 + " km";

                setPreference("mileage_sum", s);


                index = ((buf[9] & 0xff) | ((buf[10] & 0xff) << 8));
                s = index + " km";
                setPreference("maintenance_mileage_unit", s);


                index = ((buf[11] & 0xff));
                s = index + " %";
                setPreference("the_battery", s);


                index = ((buf[12] & 0xff));
                s = index / 10 + "." + index % 10 + " V";
                setPreference("am_cellvoltage", s);


                index = ((buf[13] & 0xff));
                s = index + " km/h";
                setPreference("averagespeed", s);

                index = ((buf[14] & 0xff));
                s = "";
                switch (index) {
                    case 0:
                        s = getString(R.string.str_info_4b);
                        break;
                    case 1:
                        s = getString(R.string.str_info_4d);
                        break;
                    case 2:
                        s = getString(R.string.air_mode_manual);
                        break;
                    case 3:
                        s = getString(R.string.sport_modes);
                        break;
                    case 4:
                        s = getString(R.string.driving_mode_eco_mode);
                        break;
                    case 5:
                        s = "4L" + getString(R.string.mode);
                        break;

                }
                //			s = index + ":" + (buf[9] & 0xff) + " H";
                setPreference("driving_mode", s);

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
