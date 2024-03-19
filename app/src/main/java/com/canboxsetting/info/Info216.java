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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Info216 extends PreferenceFragment {
	private static final String TAG = "Golf7InfoSimpleFragment";

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.infiniti_vehicle_infos,
				container, false);
		return mMainView;
	}

	boolean mPaused = true;

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

	private final static int[] INIT_CMDS = { 0x16, 0x17 };

	private void requestInitData() {
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo(msg.what & 0xff);
			}
		}
	};

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private void setLocalText(int id, String s) {
		TextView v = (TextView)mMainView.findViewById(id);
		if (v != null) {
			v.setText(s);
		}
	}
	
	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] { 0x3, (byte) 0x6a, 0x5, 1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case 0x17:

			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_1_average_fuel_consumption, s);
			

			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_2_average_fuel_consumption, s);
			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_3_average_fuel_consumption, s);
			index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_4_average_fuel_consumption, s);
			index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_5_average_fuel_consumption, s);
			index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_6_average_fuel_consumption, s);
			index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_7_average_fuel_consumption, s);
			index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_8_average_fuel_consumption, s);
			index = ((buf[19] & 0xff) | ((buf[18] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_9_average_fuel_consumption, s);
			index = ((buf[21] & 0xff) | ((buf[20] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.historical_10_average_fuel_consumption, s);
			break;
		case 0x16:

			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.dynamical_fuel, s);

			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L/100km";
			setLocalText(R.id.average_fuel, s);
			
			index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8)| ((buf[6] & 0xff) << 16));
			s = index + " km";
			setLocalText(R.id.total_mileage, s);
			
			index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));
			s = index + " km";
			setLocalText(R.id.estimate_range, s);
			

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
