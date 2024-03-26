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

public class MazdaTimeSetRaiseFragment extends MyFragment {

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.mazda_timeset_raise, container, false);

        mMainView.findViewById(R.id.time_clock).setOnTouchListener(mOnTouchListener);
        // mMainView.findViewById(R.id.time_set).setOnTouchListener(
        // mOnTouchListener);
        mMainView.findViewById(R.id.time_h).setOnTouchListener(mOnTouchListener);
        mMainView.findViewById(R.id.time_m).setOnTouchListener(mOnTouchListener);

        return mMainView;
    }

    private void sendCanboxInfo(int d0) {
        //		if (d0 != 0) {
        //			mHandler.sendMessageDelayed(mHandler.obtainMessage(0, d0, 0), 2000);
        //		} else {
        //			mHandler.removeMessages(0);
        //		}
        byte[] buf = new byte[]{0x4, 0x6, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }
    //
    //	public void onPause() {
    //		mHandler.removeMessages(0);
    //	};
    //
    //	private Handler mHandler = new Handler() {
    //		public void handleMessage(Message msg) {
    //			byte[] buf = new byte[] { 0x4, 0x6, (byte) msg.arg1 };
    //			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    //			mHandler.sendMessageDelayed(mHandler.obtainMessage(0, msg.arg1, 0), 200);
    //		};
    //	};

    private OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            if (arg1.getAction() == KeyEvent.ACTION_DOWN) {
                //				mHandler.removeMessages(0);
                int id = arg0.getId();
                if (id == R.id.time_clock) {
                    sendCanboxInfo(0x20);
                } else if (id == R.id.time_h) {
                    sendCanboxInfo(0x40);
                } else if (id == R.id.time_m) {
                    sendCanboxInfo(0x80);
                }
            } else if (arg1.getAction() == KeyEvent.ACTION_UP) {
                sendCanboxInfo(0);
            }
            return false;
        }
    };

}
