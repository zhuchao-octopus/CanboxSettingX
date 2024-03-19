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

import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class JeepSettingsXinbasFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HondaSettingsSimpleFragment";

	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {

		new Node("for_warning", 0x8a01, 0x0c000000, 0x0001, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("for_outo_warning", 0x8a02, 0x0c000000, 0x0002, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("lane_warning", 0x8a03, 0x0c000000, 0x000c, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("deviation_correction", 0x8a04, 0x0c000000, 0x0030, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("parksense", 0x8a05, 0x0c000000, 0x00c0, 0x0, Node.TYPE_BUFF1_INDEX),
		
		new Node("busy_warning", 0x8a06, 0x0c000000, 0x0103, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("image_parkView", 0x8a07, 0x0c000000, 0x0104, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("parkView", 0x8a08, 0x0c000000, 0x0108, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("backview", 0x8a09, 0x0c000000, 0x0110, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("wipers_induction", 0x8a0a, 0x0c000000, 0x0120, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("ramp", 0x8a0b, 0x0c000000, 0x0140, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("parking_brake", 0x8a0c, 0x0c000000, 0x0180, 0x0, Node.TYPE_BUFF1_INDEX),
		
		new Node("b_parksense", 0x8a0d, 0x0c000000, 0x0203, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("f_parksense", 0x8a0e, 0x0c000000, 0x020c, 0x0, Node.TYPE_BUFF1_INDEX),
		
		
		new Node("rear_parkSense", 0x8a0f, 0x0c000000, 0x0210, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("brake_service", 0x8a10, 0x0c000000, 0x0220, 0x0, Node.TYPE_BUFF1_INDEX),
		
		new Node("parking_delays", 0x8a12, 0x0c000000, 0x0380, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("electronic_park", 0x8a13, 0x0c000000, 0x0340, 0x0, Node.TYPE_BUFF1_INDEX),
		
		
		
		
		
		
		

		new Node("wipers_start", 0x8a21, 0x0c000000, 0x0401, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("outhigh_beam", 0x8a22, 0x0c000000, 0x0402, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("rearview_dimming", 0x8a23, 0x0c000000, 0x0404, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("running_lights", 0x8a24, 0x0c000000, 0x0408, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("lights_flash", 0x8a25, 0x0c000000, 0x0410, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("headlights_off", 0x8a26, 0x0c000000, 0x0503, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("bright_headlights", 0x8a27, 0x0c000000, 0x050c, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("engine_power", 0x8a28, 0x0c000000, 0x0530, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("unlock_light", 0x8a29, 0x0c000000, 0x0480, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("turn_lights_set", 0x8a2a, 0x0c000000, 0x0440, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("car_in_light", 0x8a2b, 0x0c000000, 0x06f0, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("front_light", 0x8a2c, 0x0c000000, 0x060f, 0x0, Node.TYPE_BUFF1_INDEX),
		

		new Node("driving_auto", 0x8a31, 0x0c000000, 0x0701, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("unlock_driving", 0x8a32, 0x0c000000, 0x0702, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("key_unlock", 0x8a33, 0x0c000000, 0x0704, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("keyless_entry", 0x8a34, 0x0c000000, 0x0708, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("personalise", 0x8a35, 0x0c000000, 0x0710, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("door_alarm", 0x8a36, 0x0c000000, 0x0720, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("power_alarm", 0x8a37, 0x0c000000, 0x0740, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("lock_beep", 0x8a38, 0x0c000000, 0x0780, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("seat", 0x8a39, 0x0c000000, 0x0801, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("outseat_heating", 0x8a3a, 0x0c000000, 0x0802, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("door_lights_flash", 0x8a3b, 0x0c000000, 0x0810, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("beep_lock", 0x8a3c, 0x0c000000, 0x080c, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("remote_door", 0x8a3d, 0x0c000000, 0x0820, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("remote_beep", 0x8a3e, 0x0c000000, 0x0840, 0x0, Node.TYPE_BUFF1_INDEX),


		new Node("auto_adjustment", 0x8a41, 0x0c000000, 0x0901, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("dis_suspension", 0x8a42, 0x0c000000, 0x0902, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("tire_mode", 0x8a43, 0x0c000000, 0x0904, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("transport_mode", 0x8a44, 0x0c000000, 0x0908, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("wheel_mode", 0x8a45, 0x0c000000, 0x0910, 0x0, Node.TYPE_BUFF1_INDEX),

		new Node("unit_set", 0x8a51, 0x0c000000, 0x0a01, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("range", 0x8a52, 0x0c000000, 0x0a02, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("fulecons", 0x8a53, 0x0c000000, 0x0a04, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("tireunit", 0x8a54, 0x0c000000, 0x0a18, 0x0, Node.TYPE_BUFF1_INDEX),
		new Node("temperature", 0x8a55, 0x0c000000, 0x0a20, 0x0, Node.TYPE_BUFF1_INDEX),
		
	};

	private final static int[] INIT_CMDS = { 0x0c00 };

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.jeep_xinbas_setting);

		for (int i = 0; i < NODES.length; ++i) {
			mPreferences[i] = findPreference(NODES[i].mKey);
			if (mPreferences[i] != null) {
				if (mPreferences[i] instanceof PreferenceScreen) {
					mPreferences[i].setOnPreferenceClickListener(this);
				} else {
					mPreferences[i].setOnPreferenceChangeListener(this);
				}
			}
		}

		// findPreference("speeddata").setOnPreferenceClickListener(this);
		// for (Node node : NODES) {
		// String s = node.mKey;
		// if (findPreference(s) != null) {
		// findPreference(s).setOnPreferenceChangeListener(this);
		// }
		// }

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

		if (mType == 1) {
			PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
			if (p != null) {
				setPreferenceScreen(p);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mType = 0;
	}

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0xff(msg.what & 0xff00);
			}
		}
	};

	private void sendCanboxData(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

	}

	private void sendCanboxData(int cmd) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),
				((cmd & 0xff) >> 0));

	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					
					
					if (key.equals("car_type")) {
						((ListPreference)preference).setValue((String)newValue);
						((ListPreference)preference).setSummary(	((ListPreference)preference).getEntry());
						
						sendCanboxInfo(0xca, Integer.parseInt((String) newValue));
					} else {
						sendCanboxData(NODES[i].mCmd,
								Integer.parseInt((String) newValue));
					}
				} else if (preference instanceof SwitchPreference) {
					if (NODES[i].mType == Node.TYPE_CUSTOM) {
						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
					} else {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x1 : 0x0);
					}

					if (key.equals("ctm_system")) {
						mSetCTM = (((Boolean) newValue) ? 0x1 : 0x0);
					}
					
					
				} else if (preference instanceof PreferenceScreen) {
					sendCanboxData(NODES[i].mCmd);
				}
				break;
			}
		}
	}

	private int mSetCTM = -1;

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {
		if (arg0.getKey().equals("individual_reset")) {
			sendCanboxInfo(0xc6, 0xd4, 0x01);
		} else if (arg0.getKey().equals("speeddata")) {
			// sendCanboxInfo(0xc6, 0xd4, 0x01);
		} else {
			try {
				udpatePreferenceValue(arg0, null);
			} catch (Exception e) {

			}
		}
		return false;
	}

	private void sendCanboxInfo0xff(int d0) {
		byte[] buf = new byte[] { (byte) 0xff, 0x02, (byte) d0 ,0};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
//				if (key.equals("front_light")){
//					index++;
//				} 

				CharSequence []ss = lp.getEntries();
				if (ss != null && (ss.length > index)) {
					lp.setValue(String.valueOf(index));
				}
				lp.setSummary("%s");
				
				// Log.d("aa", key+":"+((ListPreference)
				// findPreference(key)).getEntry());
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			}
		}
	}

	private int getStatusValue(byte[] buf, int mask) {

		int value = 0;
		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}

		value = 0;
		if (buf.length > 3) {
			value = ((buf[3] & 0xff) << 0);
		}
		if (buf.length > 4) {
			value |= ((buf[4] & 0xff) << 8);
		}
		if (buf.length > 5) {
			value |= ((buf[5] & 0xff) << 16);
		}
		

		return ((value & mask) >> start);
	}

	private int getStatusValue1(int value, int mask) {

		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}

		// } catch (Exception e) {
		// value = 0;
		// }

		return ((value & mask) >> start);
	}

	private void updateView(byte[] buf) {

//		if ((buf[0] & 0xff) == 0xd0) {
//		} else 
			

			int cmd;
			int mask;
			int value;
			int param;

			for (int i = 0; i < NODES.length; ++i) {
				cmd = (NODES[i].mStatus & 0xff000000) >> 24;
				param = (NODES[i].mStatus & 0xff0000) >> 16;

				if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
					if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == param) {
						mask = (NODES[i].mMask);
						value = getStatusValue(buf, mask);
						setPreference(NODES[i].mKey, value);
						// break;
					}
				} else if (NODES[i].mType == Node.TYPE_BUFF1) {
					if ((buf[0] & 0xff) == cmd) {
						mask = (NODES[i].mMask);
						value = getStatusValue1(buf[6], mask);
						setPreference(NODES[i].mKey, value);
						// break;
					}
			} else if (NODES[i].mType == Node.TYPE_BUFF1_INDEX) {
				if ((buf[0] & 0xff) == cmd) {
					mask = (NODES[i].mMask & 0xff);
					int index = ((NODES[i].mMask & 0xff00) >> 8);

					if ((index + 2) < buf.length) {
						value = getStatusValue1(buf[2 + index], mask);
						setPreference(NODES[i].mKey, value);
					}
					// break;
				}
			} else if (NODES[i].mType == Node.TYPE_DEFINE1) {

			}

		}
		

	}

	private void showPreference(String id, int show, String parant) {
		Preference preference = null;

		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(id)) {
				preference = mPreferences[i];
				break;
			}
		}

		if (preference != null) {
			PreferenceScreen ps;
			if (parant != null) {
				ps = (PreferenceScreen) findPreference(parant);
			} else {
				ps = getPreferenceScreen();
			}
			if (ps != null) {
				if (show != 0) {
					if (ps.findPreference(id) == null) {
						ps.addPreference(preference);
					}
				} else {
					if (findPreference(id) != null) {
						boolean b = ps.removePreference(preference);
						// Log.d("dd", "" + b);
					}
				}
			}
		}

	}

	private void showPreference(String id, int show) {
		showPreference(id, show, "driving_mode");

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
								Log.d("aa", "!!!!!!!!" + e);
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
