package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public class Info132 extends PreferenceFragment {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

		new NodePreference("am_runningspeed", 0x32),
		new NodePreference("engine_speed", 0x32),
		new NodePreference("battery_voltage", 0x32),
		
		
		
		new NodePreference("mileage_sum", 0x34),
		new NodePreference("average_fule_consumption_the_last_250km", 0x34),
		new NodePreference("average_power_consumption_the_last_250km", 0x34),
		new NodePreference("mileage", 0x34),
		new NodePreference("estimate_range_electricity", 0x34),


	};

	private final static int[] INIT_CMDS = { 0x32, 0x34 };

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
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
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
		byte[] buf = new byte[] { 0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0 };
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
		case 0x32:
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = String.format("%d RPM", index);
			setPreference(NODES[0].mKey, s);
			
			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = index + " km/h";
			setPreference(NODES[1].mKey, s);

			index = buf[8] & 0xff;
			index = index*1+30;
			s = String.format(Locale.ENGLISH, "%d.%d V", index/10, index%10);
			setPreference(NODES[2].mKey, s);	
			break;
		case 0x34:
			index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8) | ((buf[6] & 0xff) << 16));
			s = String.format(Locale.ENGLISH, "%d.%d KM", index/10, index%10);
			setPreference(NODES[3].mKey, s);
			
			index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8));
			s = String.format(Locale.ENGLISH, "%d.%d L/100KM", index/10, index%10);
			setPreference(NODES[4].mKey, s);
			
			index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));
			s = String.format(Locale.ENGLISH, "%d.%d kWh/100KM", index/10, index%10);
			setPreference(NODES[5].mKey, s);
			
			index = ((buf[23] & 0xff) | ((buf[22] & 0xff) << 8));
			s = String.format(Locale.ENGLISH, "%d KM", index);
			setPreference(NODES[6].mKey, s);
			
			index = ((buf[18] & 0xff) | ((buf[17] & 0xff) << 8));
			s = String.format(Locale.ENGLISH, "%d KM", index);
			setPreference(NODES[7].mKey, s);
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
