package com.canboxsetting.info;

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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.NodePreference;
import com.common.util.Util;
import com.common.view.MyPreferenceSeekBar;

public class ChangChengRaiseInfo extends PreferenceFragment {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

		new NodePreference("coolant_temp", 0),
		new NodePreference("transmission_oil_temperature", 0),
		new NodePreference("battery_voltage", 0),
		new NodePreference("battery_level", 0),
		new NodePreference("atmosphere_pressure", 0),
		
		new NodePreference("slope", 0),
		new NodePreference("front_wheel_torque_ratio", 0),		
		new NodePreference("dip_direction", 0),
		new NodePreference("trailer_status", 0),
		new NodePreference("altitude", 0),
		
		
		new NodePreference("amplifier_voltage", 0),
		new NodePreference("amplifier_temp", 0),


	};

	private final static int[] INIT_CMDS = { 0x29,0x36,0x37 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.empty_setting);

		init();

	}

	private void init() {

		for (int i = 0; i < NODES.length; ++i) {
			Preference p = NODES[i].createPreference(getActivity());
			if (p != null) {

				Preference ps = getPreferenceScreen();
				if (ps instanceof PreferenceScreen) {
					((PreferenceScreen) ps).addPreference(p);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

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

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 2, (byte) d0,0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, String s) {
		Preference ps = getPreferenceScreen();
		
		Preference p = ((PreferenceScreen)ps).findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}
	
	private void updateView(byte[] buf) {

		int index;
		String s;
		switch (buf[0]) {

		case 0x29:


			index = (buf[2] & 0xff);

			index = index * 75 - 4800;
			index /= 10;

			s = String.format("%d.%d", index / 100, (index<0)?(-index % 100):index % 100);
			s += getString(R.string.temp_unic);

			setPreference(NODES[0].mKey, s);

			index = (buf[3] & 0xff);
			
			index -= 40;
			s = index + getString(R.string.temp_unic);			

			setPreference(NODES[1].mKey, s);
			
			index = (buf[4] & 0xff);

			index = index * 25;

			s = String.format("%d.%d V", index / 100, index % 100);

			setPreference(NODES[2].mKey, s);
			
				s = String.format("%d", (buf[6] & 0xff));
				s += "%";
			

			setPreference(NODES[3].mKey, s);
			
			index = (buf[5] & 0xff);

			index = index * 59;

			s = String.format("%d.%d Kpa", index / 100, index % 100);

			setPreference(NODES[4].mKey, s);
			
			break;

		case 0x36:
			if ((buf[2] & 0x80) == 0) {
				s = getString(R.string.key_UP);
			} else {
				s = getString(R.string.key_DOWN);
			}
			
			s += (buf[2] & 0x7f) + getString(R.string.angle);

			setPreference(NODES[5].mKey, s);
			
			if ((buf[3] & 0xff) == 0xff) {
				s = "--";
			} else {
				s = String.format("%d", (buf[3] & 0xff));
				s += "%";
			}

			setPreference(NODES[6].mKey, s);
			
			if ((buf[4] & 0x80) == 0) {
				s = getString(R.string.key_LEFT);
			} else {
				s = getString(R.string.key_RIGHT);
			}
			
			s += (buf[4] & 0x7f) + getString(R.string.angle);

			setPreference(NODES[7].mKey, s);
			
			if ((buf[5] & 0x80) != 0) {
				s = getString(R.string.mount);
			} else {
				s = getString(R.string.unmounted);
			}
			
			setPreference(NODES[8].mKey, s);
			index = ((buf[6] & 0xff)<<8) | (buf[7] & 0xff);
			

			setPreference(NODES[9].mKey, index+"");
			
			break;
		case 0x37:

			index = (buf[10] & 0xff);
			if (index == 0xff) {
				s = "--";
			} else {
				index *= 25;
				s = String.format("%d.%d V", index / 100, index % 100);
			}

			setPreference(NODES[10].mKey, s);

			
			index = (buf[11] & 0xff);
			if (index == 0xff) {
				s = "--";
			} else {
				index -= 40;
				s = index + getString(R.string.temp_unic);
			}

			setPreference(NODES[11].mKey, s);
			
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
								Log.d(TAG, "updateView:Exception " + e);
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
