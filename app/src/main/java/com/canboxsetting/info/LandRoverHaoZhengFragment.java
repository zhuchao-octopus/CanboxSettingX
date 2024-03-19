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
import com.canboxsetting.R.array;
import com.canboxsetting.R.string;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class LandRoverHaoZhengFragment extends PreferenceFragment {
	private static final String TAG = "Golf7InfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.landrover_haozheng_info);

		Preference p;
		p = findPreference("fulecons1");
		p.setTitle(getString(R.string.fulecons) + "1");
		p = findPreference("fulecons2");
		p.setTitle(getString(R.string.fulecons) + "2");
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

		sendCanboxInfo(0x38);
		sendCanboxInfo(0x35);
		sendCanboxInfo(0x37);
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private void sendCanboxInfo(int d0) {

		byte[] buf = new byte[] { (byte) 0xff, 0x01, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private int mUnit = 0;

	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case 0x38:
			mUnit = (buf[3] & 0xff);

			String[] ss;
			index = ((buf[3] & 0x4) >> 2);

			ss = getResources().getStringArray(R.array.mitsubishi_settings_4);

			setPreference("hdc", ss[index]);

			index = ((buf[3] & 0x3) >> 0);

			ss = getResources().getStringArray(R.array.high_low_gear);

			setPreference("high_low_gear", ss[index]);

			index = ((buf[6] & 0xf0) >> 4);

			ss = getResources().getStringArray(R.array.driving_mode_landrover);

			setPreference("driving_mode", ss[index]);

			index = ((buf[6] & 0xf) >> 0);

			ss = getResources().getStringArray(R.array.air_suspension_mode);

			setPreference("air_suspension_mode", ss[index]);
			index = ((buf[8] & 0xff));
			s = "";
			if (index >= 0 && index <= 99) {
				s = index + "";
			}
			setPreference("tire_distance_1", s);
			index = ((buf[9] & 0xff));
			s = "";
			if (index >= 0 && index <= 99) {
				s = index + "";
			}
			setPreference("tire_distance_2", s);
			index = ((buf[10] & 0xff));
			s = "";
			if (index >= 0 && index <= 99) {
				s = index + "";
			}
			setPreference("tire_distance_3", s);
			index = ((buf[11] & 0xff));
			s = "";
			if (index >= 0 && index <= 99) {
				s = index + "";
			}
			setPreference("tire_distance_4", s);
			break;
		case 0x35:
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			if ((mUnit & 0x8) == 0) {
				s = index + " km";
			} else {
				s = index + " mil";
			}
			setPreference("drivingmileage", s);

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = index + " rpm";
			setPreference("enginespeed", s);

			index = (buf[12] & 0xff) | ((buf[11] & 0xff) << 8)
					| ((buf[10] & 0xff) << 16) | ((buf[9] & 0xff) << 24);
			if ((mUnit & 0x8) == 0) {
				s = index + " km";
			} else {
				s = index + " mil";
			}
			setPreference("mileage_sum", s);
			break;
		case 0x37:
			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = index / 10 + "." + index % 10;
			if ((mUnit & 0x30) == 0) {
				s += " l/100km";
			} else if ((mUnit & 0x30) == 0x10) {
				s += " mpg";
			} else {
				s += " km/l";
			}
			setPreference("fulecons1", s);
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = index / 10 + "." + index % 10;
			if ((mUnit & 0x30) == 0) {
				s += " l/100km";
			} else if ((mUnit & 0x30) == 0x10) {
				s += " mpg";
			} else {
				s += " km/l";
			}
			setPreference("fulecons2", s);

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = index / 10 + "." + index % 10;
			if ((mUnit & 0x8) == 0) {
				s += " km/h";
			} else {
				s += " mil/h";
			}
			setPreference("averageapeed", s);

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = index / 10 + "." + index % 10;
			if ((mUnit & 0x8) == 0) {
				s += " km/h";
			} else {
				s += " mil/h";
			}
			setPreference("averageapeed", s);

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
