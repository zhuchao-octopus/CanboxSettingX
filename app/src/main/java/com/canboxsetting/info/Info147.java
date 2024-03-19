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

public class Info147 extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "Golf7InfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.renault_hiworld_info);

		findPreference("fuelclear").setOnPreferenceClickListener(this);
	}
	
	public boolean onPreferenceClick(Preference arg0) {
		String key = arg0.getKey();
		if ("fuelclear".equals(key)) {
			byte[] buf = new byte[] { 0x4, (byte) 0x1b, 0x02, 0x02, 0x1, (byte)0xff };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		}
		return false;
	}
boolean mPaused = true;
	@Override
	public void onPause() {
		super.onPause();mPaused = true;
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

	private final static int[] INIT_CMDS = { 0x13, 0x14 };

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
	

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] {  0x3, (byte) 0x6a, 0x5, 1,(byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case 0x13:
			index = ((buf[10] & 0xff));
			if (index >= 1 && index <= 0x1e){

				s = getString(R.string.right_deviation) + index;
			} else if (index >= 0x80 && index <= 0x9e){
				index = index - 0x80;
				s = getString(R.string.left_leaning) + index;
			} else {
				s = "";
			}
			setPreference("left_and_right_inclination", s);
			
			
			index = ((buf[11] & 0xff));
			if (index >= 1 && index <= 0x1e){
				s = getString(R.string.backward_tilt) + index;
			} else if (index >= 0x80 && index <= 0x9e){
				index = index - 0x80;
				s = getString(R.string.anteversion) + index;
			} else {
				s = "";
			}
			setPreference("back_and_forth_tilt", s);
			
			break;
		case 0x14:

			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " l/100km";
			setPreference("averagefuel", s);
			
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " km/h";
			setPreference("averageapeed", s);
			
			
			index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8) | ((buf[6] & 0xff) << 16));

			s = index / 10 + "." + index % 10 + " km";

			setPreference("mileage1", s);

			index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
			s = index + ":" + (buf[9] & 0xff) + " H";
			setPreference("driving_time", s);
			

			index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L";
			setPreference("total_fuel_consumption", s);
			
			index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));

			s = index / 10 + "." + index % 10 + " km";
			setPreference("non_expendable_mileage", s);

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
