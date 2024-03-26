package com.canboxsetting.time;

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
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.MyFragment;
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
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class BentengB70TimeSetHiworldFragment extends MyFragment {

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.bentengb70_timeset_hiworld, container, false);


        mMainView.findViewById(R.id.hour_a).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.hour_m).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.min_a).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.min_m).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.set24).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.set12).setOnTouchListener(mOnTouchListener);

        return mMainView;
    }

    private void sendCanboxInfo(int d0, int d1) {

        byte[] buf = new byte[]{0x2, (byte) 0x6e, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }


    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (arg1.getAction() == KeyEvent.ACTION_DOWN) {
                //				mHandler.removeMessages(0);
                int id = arg0.getId();
                if (id == R.id.hour_a) {
                    sendCanboxInfo(0x6, 0x1);
                } else if (id == R.id.hour_m) {
                    sendCanboxInfo(0x6, 0x2);
                } else if (id == R.id.min_a) {
                    sendCanboxInfo(0x7, 0x1);
                } else if (id == R.id.min_m) {
                    sendCanboxInfo(0x7, 0x2);
                } else if (id == R.id.set24) {
                    sendCanboxInfo(0x8, 0x1);
                } else if (id == R.id.set12) {
                    sendCanboxInfo(0x8, 0x2);
                }
            }
            return false;
        }
    };

}
