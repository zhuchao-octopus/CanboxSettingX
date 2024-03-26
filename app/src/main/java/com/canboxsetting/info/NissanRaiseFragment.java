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
import java.util.Locale;

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
import android.graphics.drawable.Drawable;
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
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class NissanRaiseFragment extends PreferenceFragment implements OnPreferenceClickListener {
    private static final String TAG = "KadjarRaiseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nissan_raise_info);

        if (GlobalDef.getProId() == 157) {
            getPreferenceScreen().removePreference(findPreference("mileage_sum"));

            INIT_CMDS = INIT_CMDS_XINCHI;
        }

    }


    private final static int[] INIT_CMDS_RAISE = {0x9027, 0x9068};
    private final static int[] INIT_CMDS_XINCHI = {0xf10b, 0xf133};

    private static int[] INIT_CMDS = INIT_CMDS_RAISE;


    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                if ((msg.what & 0xff00) == 0x9000) {
                    sendCanboxInfo0x90(msg.what & 0xff, 0);
                } else {
                    sendCanboxInfo0xf1(msg.what & 0xff);
                }
            }
        }
    };

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0xf1(int d0) {
        byte[] buf = new byte[]{(byte) 0xf1, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        return false;
    }

    private boolean mPaused = true;

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        mPaused = false;
        requestInitData();
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }


    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";

        switch (buf[0]) {
            case 0x27:
                index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                if ((buf[2] & 0x1) == 0) {
                    s = "km";
                } else {
                    s = "mile";
                }

                s = String.format("%d", index) + " " + s;

                setPreference("mileage_sum", s);

                index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                if ((buf[2] & 0x1) == 0) {
                    s = "km";
                } else {
                    s = "mile";
                }

                s = String.format("%d.%d", index / 10, index % 10) + " " + s;

                setPreference("driving_mileage", s);
                break;

            case 0x6a:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = index + " km/h";

                setPreference("speed", s);
                break;
            case 0x68:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d RPM", index);
                setPreference("enginespeed", s);

                break;
            case 0xb:
                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                s = index + " km/h";
                setPreference("speed", s);
                index = ((buf[4] & 0xff) * 100);
                s = String.format("%d RPM", index);
                setPreference("enginespeed", s);
                break;
            case 0x33:

                index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));

                s = "km";

                s = String.format("%d", index) + " " + s;

                setPreference("driving_mileage", s);
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
                        try {
                            if (buf != null) {

                                try {
                                    updateView(buf);
                                } catch (Exception e) {
                                    Log.d("aa", "!!!!!!!!" + buf);
                                }
                            }
                        } catch (Exception e) {

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
