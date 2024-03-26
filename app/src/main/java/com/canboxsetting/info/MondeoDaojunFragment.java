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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MondeoDaojunFragment extends PreferenceFragment {
    private static final String TAG = "ToyotaInfoSimpleFragment";

    private boolean mRudder = false;
    private int mFlashLight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.mondeo_daojun_info);
    }

    private int mFrontDoor = 0;
    private int mBackDoor = 0;

    private void getCanboxSetting() {
        mFrontDoor = 0;
        mBackDoor = 0;

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_FRONT_DOOR)) {
                        mFrontDoor = Integer.valueOf(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_REAR_DOOR)) {
                        mBackDoor = Integer.valueOf(ss[i].substring(1));
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private final static int[] INIT_CMDS = {0x65, 0x16};

    private void requestInitData() {
        if (mPaused) {
            return;
        }
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1, INIT_CMDS[i], 0), (i * 100));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // case 0:
                // requestInitData();
                // break;
                case 1:
                    sendCanboxInfo0x90(msg.arg1 & 0xff);
                    break;
            }
        }
    };

    private void sendCanboxInfo0xff(int d1) {// no canbox cmd.
        byte[] buf = new byte[]{(byte) 0xff, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) 0xff};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";
        String temp;

        switch (buf[0]) {
            case 0x65:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format(Locale.ENGLISH, "%d RPM", index);
                setPreference("engine_speed55", s);
                break;
            case 0x16:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                if ((buf[4] & 0xff) == 0) {
                    s = String.format(Locale.ENGLISH, "%d.%02d km/h", index / 100, index % 100);
                } else {
                    s = String.format(Locale.ENGLISH, "%d.%02d mph", index / 100, index % 100);
                }

                setPreference("speed47", s);
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
