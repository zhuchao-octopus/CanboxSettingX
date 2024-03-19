package com.canboxsetting.set;

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

public class GMSettingsSimpleFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "GMSettingsSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.gm_simple_settings);

		findPreference("radarswitch").setOnPreferenceChangeListener(this);

		findPreference("alarm_vol").setOnPreferenceChangeListener(this);
		findPreference("restore").setOnPreferenceClickListener(this);

		for (String s : KEYS) {
			if (findPreference(s) != null) {
				findPreference(s).setOnPreferenceChangeListener(this);
			}
		}

		// findPreference("automodewindlevel").setOnPreferenceChangeListener(this);
		// findPreference("airqualitysensor").setOnPreferenceChangeListener(this);
		// findPreference("airpartition").setOnPreferenceChangeListener(this);
		// findPreference("backwindowdefog").setOnPreferenceChangeListener(this);
		// findPreference("frontwindowdefog").setOnPreferenceChangeListener(this);
		// findPreference("remote_seat").setOnPreferenceChangeListener(this);
		// findPreference("ari_mode").setOnPreferenceChangeListener(this);

		for(int i =0;i<KEYS.length;++i){
			mPreferences[i] = findPreference(KEYS[i]);
		}
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

//		sendCanboxInfo(0x90, 0x1A);

		mHandler.sendEmptyMessageDelayed(0x0A, 100);
		mHandler.sendEmptyMessageDelayed(0x05, 200);
		mHandler.sendEmptyMessageDelayed(0x06, 300);
		mHandler.sendEmptyMessageDelayed(0x07, 400);
//		mHandler.sendEmptyMessageDelayed(0x0d, 500);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mHandler.removeMessages(msg.what);
			// mHandler.sendEmptyMessageDelayed(msg.what, 700);
			sendCanboxInfo(0x90, msg.what);
		}
	};


	private Preference []mPreferences = new Preference[KEYS.length];
	
	private final static String[] KEYS = {
			// air set
			"automodewindlevel",
			"airqualitysensor",
			"airpartition",
			"backwindowdefog",
			"frontwindowdefog",
			"remote_seat",
			"ari_mode",

			// control set

			"findlight", "headlightdelay", "dooropenlock", "startlatch",
			"parkunlock", "delaylock", "remoteunlock5", "remotelock5",
			"remoteunlocksettings", "car_unlocked", "automatic_latch",
			"wipers", "remote_start", "unkey", "by_drive", "auto_relock",
			"blind", "lfRight", "adaptive", "folding", "rtmirror", "easyeeat",
			"memory", "auto_wipers", "auto_collision", "status_notifi",
			"window_control", };

	private final static int[] CMDS = { 0x3008200, 0x3008201, 0x3008202,
			0x3008203, 0x3008204, 0x3008205, 0x3008206,

			0x3008300, 0x3008301, 0x3008302, 0x3008303, 0x3008304, 0x3008305,
			0x3008306, 0x3008307, 0x3008308, 0x300830c, 0x3008317, 0x3008309,
			0x300830b, 0x300830d, 0x300830e, 0x300830f, 0x3008316, 0x3008310,
			0x3008311, 0x3008312, 0x3008313, 0x3008314, 0x3008315, 0x3008318,
			0x3008319, 0x300831a, 0x300831b,

	};

	private void sendCanboxData(int cmd, int value) {
		if ((cmd & 0xff000000) == 0x3000000) {
			sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
		}
	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < KEYS.length; ++i) {
			if (KEYS[i].equals(key)) {
				if (preference instanceof ListPreference) {
					sendCanboxData(CMDS[i], Integer.parseInt((String) newValue));
				} else if (preference instanceof SwitchPreference) {
					sendCanboxData(CMDS[i], ((Boolean) newValue) ? 0x1 : 0x0);
				}
				break;
			}
		}
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		try {
			if ("radarswitch".equals(key)) {
				sendCanboxInfo(0x84, ((Boolean) newValue) ? 0x1 : 0x0);
			} else if ("alarm_vol".equals(key)) {
				int i = Integer.parseInt((String) newValue);
				sendCanboxInfo(0x88, i);
			} else {
				udpatePreferenceValue(preference, newValue);
			}

		} catch (Exception e) {

		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {
		String key = arg0.getKey();
		if ("restore".equals(key)) {

			Dialog d = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.confirmation_factory_settings)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									sendCanboxInfo(0x83, 0x80, 0x01);
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.show();

		}
		return false;
	}

	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { (byte) d0, 0x01, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, int index) {
		Preference p = findPreference(key);
		if (p != null) {
			if (p instanceof ListPreference) {
				ListPreference lp = (ListPreference) p;
				CharSequence []ss = lp.getEntries();
				if (ss != null && (ss.length > index)) {
					lp.setValue(String.valueOf(index));
				}
				lp.setSummary(((ListPreference) findPreference(key)).getEntry());
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			}
		}
	}

	private void updateView(byte[] buf) {
		int index;
		switch (buf[0]) {
		case 0x5:

			setPreference("automodewindlevel", (int) ((buf[2] & 0xC0) >> 6));
			setPreference("airqualitysensor", (int) ((buf[2] & 0x30) >> 4));
			setPreference("airpartition", (int) ((buf[2] & 0x0C) >> 2));
			setPreference("backwindowdefog", (int) ((buf[2] & 0x02) >> 1));
			setPreference("frontwindowdefog", (int) ((buf[2] & 0x01) >> 0));
			setPreference("remote_seat",
					(int) (((buf[3] & 0x40) == 0) ? ((buf[3] & 0x08) >> 3)
							: ((buf[3] & 0x30) >> 4)));
			setPreference("ari_mode", (int) ((buf[3] & 0x3) >> 0));

			break;
		case 0x6:
			// if ((buf[3] & 0x2) != 0) {
			// ((SwitchPreference) findPreference("remote_start55"))
			// .setChecked(true);
			// } else {
			// ((SwitchPreference) findPreference("remote_start55"))
			// .setChecked(false);
			// }

			setPreference("findlight", (int) ((buf[2] & 0x80) >> 7));
			setPreference("headlightdelay", (int) ((buf[2] & 0x60) >> 5));
			setPreference("dooropenlock", (int) ((buf[2] & 0x10) >> 4));
			setPreference("startlatch", (int) ((buf[2] & 0x8) >> 3));
			setPreference("parkunlock", (int) ((buf[2] & 0x6) >> 1));
			setPreference("delaylock", (int) ((buf[2] & 0x1) >> 0));

			setPreference("remoteunlock5", (int) ((buf[3] & 0x80) >> 7));
			setPreference("remotelock5", (int) ((buf[3] & 0x60) >> 5));
			setPreference("remoteunlocksettings", (int) ((buf[3] & 0x10) >> 4));
			setPreference("wipers", (int) ((buf[3] & 0x8) >> 3));
			setPreference("remote_start", (int) ((buf[3] & 0x2) >> 0));
			break;
		case 0x7:
			if ((buf[2] & 0x1) != 0) {
				((SwitchPreference) findPreference("radarswitch"))
						.setChecked(true);
			} else {
				((SwitchPreference) findPreference("radarswitch"))
						.setChecked(false);
			}

			break;

		case 0xa:

			setPreference("car_unlocked", (int) ((buf[2] & 0x80) >> 7));
			setPreference("automatic_latch", (int) ((buf[2] & 0x3) >> 0));

			setPreference("unkey", (int) ((buf[2] & 0x40) >> 6));
			setPreference("by_drive", (int) ((buf[2] & 0x20) >> 5));
			setPreference("auto_relock", (int) ((buf[2] & 0x10) >> 4));

			setPreference("blind", (int) ((buf[2] & 0x4) >> 2));

			setPreference("lfRight", (int) ((buf[3] & 0x80) >> 7));
			setPreference("adaptive", (int) ((buf[3] & 0x40) >> 6));
			setPreference("folding", (int) ((buf[3] & 0x20) >> 5));
			setPreference("rtmirror", (int) ((buf[3] & 0x10) >> 4));
			setPreference("easyeeat", (int) ((buf[3] & 0x8) >> 3));
			setPreference("memory", (int) ((buf[3] & 0x4) >> 2));

			setPreference("auto_wipers", (int) ((buf[2] & 0x8) >> 3));

			index = (int) ((buf[3] & 0x3) >> 0);
			if (index == 3) {
				index = 2;
			}
			setPreference("auto_collision", index);

			setPreference("status_notifi", (int) ((buf[4] & 0x1) >> 0));
			setPreference("window_control", (int) ((buf[4] & 0x4) >> 2));
			break;
		case 0xd:

			index = (int) ((buf[2] & 0x8f));
			((ListPreference) findPreference("alarm_vol")).setValue(String
					.valueOf(index));
			((ListPreference) findPreference("alarm_vol"))
					.setSummary(((ListPreference) findPreference("alarm_vol"))
							.getEntry());

			break;
		case 0x1a:
			switch (buf[2]){			
			case 0:
				showPreference("automodewindlevel", (int)(buf[3]&0x1));
				showPreference("airqualitysensor", (int)(buf[3]&0x2));
				showPreference("airpartition", (int)(buf[3]&0x20));
				showPreference("backwindowdefog", (int)(buf[3]&0x10));
				showPreference("frontwindowdefog", (int)(buf[3]&0x80));
				showPreference("remote_seat", (int)(buf[3]&0x40));
				showPreference("ari_mode", (int)(buf[3]&0x4));
				
				break;
				
			case 1:
				showPreference("blind", (int)(buf[3]&0x2));
				showPreference("auto_collision", (int)(buf[3]&0x4));
				showPreference("status_notifi", (int)(buf[3]&0x8));
				
				break;
				
			case 2:
				showPreference("wipers", (int)(buf[3]&0x1));
				showPreference("memory", (int)(buf[3]&0x4));
				showPreference("easyeeat", (int)(buf[3]&0x8));
				showPreference("rtmirror", (int)(buf[3]&0x10));				

				showPreference("folding", (int)(buf[3]&0x20));
				showPreference("auto_wipers", (int)(buf[3]&0x40));
				
				break;
				
			case 3:
				showPreference("findlight", (int)(buf[3]&0x1));
				showPreference("headlightdelay", (int)(buf[3]&0x2));
				
				break;
				
				
			case 4:
				showPreference("dooropenlock", (int)(buf[3]&0x1));
				showPreference("parkunlock", (int)(buf[3]&0x2));
				showPreference("startlatch", (int)(buf[3]&0x4));
				showPreference("delaylock", (int)(buf[3]&0x8));
				
				break;
				
			case 5:
				showPreference("remoteunlock5", (int)(buf[3]&0x1));
				showPreference("remotelock5", (int)(buf[3]&0x2));
				showPreference("remoteunlocksettings", (int)(buf[3]&0x4));
				
				showPreference("car_unlocked", (int)(buf[3]&0x10));
				
				showPreference("unkey", (int)(buf[3]&0x20));
				showPreference("automatic_latch", (int)(buf[3]&0x40));
				showPreference("window_control", (int)(buf[3]&0x80));
				break;
			
			}
		
			break;
		}
	}

	private void showPreference(String id, int show){
		Preference preference = null;
		
		for(int i =0;i<KEYS.length;++i){
			if(KEYS[i].equals(id)){
				preference = mPreferences[i];
				break;
			}
		}
		
		if (preference != null) {
			if (show != 0) {
				if (findPreference(id) == null) {
					getPreferenceScreen().addPreference(preference);
				}
			} else {
				if (findPreference(id) != null) {
					getPreferenceScreen().removePreference(preference);
				}
			}
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
