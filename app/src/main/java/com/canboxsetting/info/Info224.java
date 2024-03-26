package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
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
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.CircularRingBottomView;
import com.common.view.CircularRingPercentageView;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Info224 extends Fragment {
    private static final String TAG = "GMInfoSimpleFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    private View mMainView;

    private void setFullScreen() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void setTime() { // 24


        long time = System.currentTimeMillis();
        Date d1 = new Date(time);
        SimpleDateFormat format;
        boolean is24 = DateFormat.is24HourFormat(getActivity());
        if (is24) {
            format = new SimpleDateFormat("HH:mm");
        } else {
            format = new SimpleDateFormat("hh:mm");
        }
        String t2 = format.format(d1);
        ((TextView) mMainView.findViewById(R.id.time)).setText(t2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context context = inflater.getContext();
        mMainView = inflater.inflate(R.layout.tractor_vehicleinfos, container, false);

        mMainView.findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                getActivity().startActivity(new Intent(Intent.ACTION_MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).addCategory(Intent.CATEGORY_HOME));
            }
        });

        setFullScreen();

        mDashboard1 = (CircularRingPercentageView) mMainView.findViewById(R.id.dashboard_1);
        mDashboard2 = (CircularRingPercentageView) mMainView.findViewById(R.id.dashboard_2);
        mDashboard12 = (CircularRingBottomView) mMainView.findViewById(R.id.dashboard_12);
        mDashboard21 = (CircularRingBottomView) mMainView.findViewById(R.id.dashboard_22);
        setTime();
        return mMainView;
    }

    CircularRingPercentageView mDashboard1;
    CircularRingPercentageView mDashboard2;
    CircularRingBottomView mDashboard12;
    CircularRingBottomView mDashboard21;

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

    private void updateView(byte[] buf) {
        if (buf[0] == 0x20) {
            int canId = (((buf[2] & 0xff) << 24) | ((buf[3] & 0xff) << 16) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 0));

            int index;
            String s;
            switch (canId) {
                case 0x0cf00400:
                    index = (buf[9] & 0xff) - 125;
                    mDashboard12.setProgress(index);

                    index = (((buf[11] & 0xff) << 8) | ((buf[10] & 0xff) << 0));

                    index = index * 125 / 1000;
                    mDashboard1.setValue(index);
                    break;
                case 0x18feef00:

                    index = (buf[10] & 0xff) * 4;
                    index = index * 100 / 1000;
                    mDashboard21.setProgress(index);
                    break;
                case 0x18feee00:

                    index = (buf[7] & 0xff) - 40;

                    mDashboard2.setValue(index * 100);
                    break;
                case 0x18fee500:
                    index = (((buf[10] & 0xff) << 24) | ((buf[9] & 0xff) << 16) | ((buf[8] & 0xff) << 8) | ((buf[7] & 0xff) << 0));
                    index = index / 2;
                    s = index / 10 + "." + index % 10 + "h";
                    ((TextView) mMainView.findViewById(R.id.engine_working_time)).setText(s);
                    break;
            }
        }
    }

    private void setText(int id, String s) {

        if (mMainView != null) {
            ((TextView) mMainView.findViewById(id)).setText(s);
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
                    } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED) || intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                        setTime();
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
            iFilter.addAction(Intent.ACTION_TIME_TICK);
            iFilter.addAction(Intent.ACTION_TIME_CHANGED);

            this.getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

}
