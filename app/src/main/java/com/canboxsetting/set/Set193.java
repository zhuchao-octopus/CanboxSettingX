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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.NodePreference;
import com.common.util.Util;

public class Set193 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {
			//group1
		
		

		
		new NodePreference("two_unlock", 0x6c01, 0x6702, 0x1, 0, R.array.remote_unlock_entries, R.array.two_values),
		

		new NodePreference("the_15KMH_automatic_latch", 0x6c02, 0x6702, 0x2, 0),
		new NodePreference("str_outside_rearview", 0x6c03, 0x6703, 0x1, 0),
		
		new NodePreference("go_home_lighting_delay", 0x6c04, 0x6702, 0x1c, 0, R.array.byd_go_home, R.array.eight_values),
		new NodePreference("leave_home_lighting_delay", 0x6c05, 0x6702, 0xe0, 0, R.array.byd_go_home, R.array.eight_values),

	
		new NodePreference("outside_the_Logo_lamp_of_the_rear_view_mirror_is_lit", 0x6c06, 0x6703, 0x2, 0),
		new NodePreference("automatic_dome_lamp_lighting_time", 0x6c07, 0x6703, 0xc, 0, R.array.beiqi003_01, R.array.four_values),
		

		new NodePreference("remote_lift_window", 0x6c08, 0x6703, 0x10, 0),
		new NodePreference("remote_control_window_drop", 0x6c09, 0x6703, 0x20, 0),
		new NodePreference("the_headlights_flashing_when_disarming", 0x6c0a, 0x6703, 0x40, 0),
		new NodePreference("open_the_door_to_stop_the_wiper", 0x6c0b, 0x6703, 0x80, 0),
		new NodePreference("to_automatically_open_the_haze_lamp", 0x6c0c, 0x6704, 0x1, 0),
		new NodePreference("driver_seat_auto_return", 0x6c0d, 0x6704, 0x2, 0),
		
		new NodePreference("magnetic_relay_condition", 0x6c0e, 0x6704, 0x4, 0, R.array.magnetic_relay_condition_entries, R.array.four_values),
		
		//group2
		
		
		
		new NodePreference("tire_pressure_unit", 0x6d01, 0x6801, 0x3, 1, R.array.entireunit, R.array.three_values),
		

		new NodePreference("high_speed_reminder", 0x6d02, 0x6801, 0x4, 1),
		new NodePreference("speed_limit_in_winter", 0x6d03, 0x6801, 0x8, 1),
		new NodePreference("BSD_warning_tone_opens", 0x6d04, 0x6801, 0x10, 1),
		new NodePreference("LDW_warning_tone_opens", 0x6d05, 0x6801, 0x20, 1),


		//group3
		new NodePreference("auto_inner_loop", 0x6b01, 0x6601, 0x1, 2),
		new NodePreference("AC_automatic_mode", 0x6b02, 0x6601, 0x2, 2),
		new NodePreference("automatic_air_volume", 0x6b03, 0x6601, 0x4, 2),
		new NodePreference("air_conditioning_high_temperature_inner_loop", 0x6b04, 0x6601, 0x8, 2),
		new NodePreference("automatic_drop_wind_speed", 0x6b05, 0x6601, 0x70, 2, R.array.enwind_speed, R.array.eight_values),
		
		
		new NodePreference("remote_open_air_conditioning", 0x6b06, 0x6601, 0x80, 2),


		
				
	};

	private final static int[] INIT_CMDS = { 0x66, 0x67, 0x68

	};
	
	private byte []mNewEnergy = new byte[]{0,0,0};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.beiqi_s6_hiworld_setting_header);

		init();

	}

	private void init() {

		for (int i = 0; i < NODES.length; ++i) {
			Preference p = NODES[i].createPreference(getActivity());
			if (p != null) {
				int index = NODES[i].mType & 0xff;
				if (index < getPreferenceScreen().getPreferenceCount()) {
					Preference ps = getPreferenceScreen().getPreference(index);
					if (ps instanceof PreferenceScreen) {
						((PreferenceScreen) ps).addPreference(p);

						if ((p instanceof ListPreference)
								|| (p instanceof SwitchPreference)) {
							p.setOnPreferenceChangeListener(this);
						} else {
							p.setOnPreferenceClickListener(this);
						}
					}
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
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {

				byte[] buf = new byte[] { 0x3, (byte) 0x6a, (byte) 5, 1, (byte) (msg.what & 0xff)  };
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);
				
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

	private final static byte []SPEED = new byte[]{(byte)0xFE, (byte)0x5a, (byte)0x64, (byte)0x6e, (byte)0x78, (byte)0x82 };
	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					
					
						sendCanboxInfo((NODES[i].mCmd&0xff00) >> 8, NODES[i].mCmd&0xff,
								Integer.parseInt((String) newValue));
					
				} else if (preference instanceof SwitchPreference) {
//					if (NODES[i].mType == Node.TYPE_CUSTOM) {
//						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
//					} else {
						sendCanboxInfo((NODES[i].mCmd&0xff00) >> 8, NODES[i].mCmd&0xff,
								((Boolean) newValue) ? 1 : 0x0);
//					}
				} else if (preference instanceof PreferenceScreen) {
					sendCanboxData(NODES[i].mCmd);
				} else {

				}
				break;
			}
		}
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {
		if (arg0.getKey().equals("reset_driver_mode")) {
			sendCanboxInfo(0xa9, 0xb, 0x01);
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

	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { (byte) d0, 0x01, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x2, (byte) d0, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendNewEnergyCanboxInfo(NodePreference node, int index) {

		if (node.mStatus == 2) {
			mNewEnergy[2] = SPEED[index];
		} else {
			mNewEnergy[node.mStatus] = (byte) ((index & 0xff) << node.mMask);
		}

//		Log.d("ffck", Util.byte2HexStr(mNewEnergy));
		byte[] buf = new byte[] { 0x8, (byte) 0xa9, (byte) 0x0a, mNewEnergy[0],
				mNewEnergy[1], mNewEnergy[2] };
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
				lp.setSummary("%s");
				// Log.d("aa", key+":"+((ListPreference)
				// findPreference(key)).getEntry());
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			}
		}
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
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

		int cmd;
		int mask;
		int index;
		int value;
//		if (buf[0] == 0x57) {
//
//
//		} else {
		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff00) >> 8;
			index = (NODES[i].mStatus & 0xff);
			mask = NODES[i].mMask;

			if ((buf[0] & 0xff) == cmd) {
				value = getStatusValue1(buf[2 + index], mask);
				setPreference(NODES[i].mKey, value);
			}

		}

//		}
	}


	private void showPreferenceEnable(String id, boolean enabled) {
		Preference ps = (Preference) findPreference(id);
		if (ps != null) {
			ps.setEnabled(enabled);
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
