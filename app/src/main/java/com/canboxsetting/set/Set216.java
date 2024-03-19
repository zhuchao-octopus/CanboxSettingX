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
import com.common.view.MyPreferenceSeekBar;

public class Set216 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {
			//group1
		
		

		
		new NodePreference("side_angle_radar_sensitivity", 0x6f02, 0x6100, 0x70, 0,
				R.array.four_high_values, R.array.four_high_values),

		new NodePreference("center_radar_sensitivity", 0x6f03, 0x6100, 0xe, 0,
				R.array.four_high_values, R.array.four_high_values),
		

		new NodePreference("car_light_auto", 0x6f04, 0x6101, 0x80, 0),

		new NodePreference("autolight", 0x6f05, 0x6101, 0x70, 0,
				R.array.four_high_values, R.array.four_high_values),				

		new NodePreference("timeswitchlight", 0x6f06, 0x6101, 0xe, 0,
				R.array.automatic_lighting_time_setting, R.array.eight_values),

		new NodePreference("select_unlock", 0x6f07, 0x6101, 0x1, 0),

		new NodePreference("smart_key_lock_fuction", 0x6f08, 0x6102, 0x80, 0),

		new NodePreference("speed_sensitive_wiper", 0x6f15, 0x6104, 0x80, 0),

		new NodePreference("radarvol", 0x6f1f, 0x6105, 0x0c, 0,
				R.array.three_high_values, R.array.three_high_values),				

		new NodePreference("redar", 0x6f20, 0x6100, 0x1, 0),
				
		//group2
		

		new NodePreference("speed_compensated_vol", 0xad07, 0xa606, 0x7, 1, 0, 5, 1),
		new NodePreference("surround_volume", 0xad08, 0xa607, 0xff, 1, -5, 5, 1),
		

		new NodePreference("bose_center_point", 0xad09, 0xa606, 0x8, 1),
		new NodePreference("driver_seat_sound_field", 0xad0a, 0xa606, 0x10, 1),

		//group3
		new NodePreference("outside_odor_sensor", 0x3b03, 0x3505, 0xf0, 2,
				R.array.five_high_values, R.array.five_high_values),		

		new NodePreference("adjusting_the_automatic_defrost", 0x3b0b, 0x3505, 0x0f, 2,
				R.array.five_high_values, R.array.five_high_values),	
				

				new NodePreference("natural_wind", 0x3b15, 0x3506, 0x80, 2),
				new NodePreference("wind_intensity", 0x3b16, 0x3506, 0x40, 2),
				new NodePreference("aromatic", 0x3b17, 0x3506, 0x20, 2),
				
	};

	private final static int[] INIT_CMDS = { 0x61, 0x35 , (byte)0xa6

	};
	
	private byte []mNewEnergy = new byte[]{0,0,0};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.infiniti_hiworld_setting_header);

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
						} else if ((p instanceof MyPreferenceSeekBar)) {
							p.setOnPreferenceChangeListener(this);							
						}else {
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
				}  else if (preference instanceof MyPreferenceSeekBar) {
					int index = Integer.parseInt((String) newValue);
					
					int cur = 0;

					try {
						cur = Integer.parseInt(preference.getSummary()
								.toString());
					} catch (Exception e) {

					}
					
					if (index >= -5 && index <= 5 && cur >= -5 && cur <= 5) {
						index = index - cur;

						sendEQCmd((byte) (NODES[i].mCmd & 0xff), index);
					}
					
//					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
//							NODES[i].mCmd & 0xff,
//							index);	
					
					
				} 
				break;
			}
		}
	}
	
	private void sendEQCmd(byte id, int step) {
		byte data;
		if (step == 0) {
			return;
		} else if (step > 0) {
			data = 1;
			step--;
		} else {
			data = -1;
			step++;
		}
		byte[] buf = new byte[] { 0x2, (byte) 0xad, id, data };
//		sendDataToCanbox(buf, buf.length);
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		mHandlerEQ.removeMessages(SET_EQ_STEP);
		if (step != 0) {
			mHandlerEQ.sendMessageDelayed(
					mHandlerEQ.obtainMessage(SET_EQ_STEP, id, step), 200);
		}
	}
	
	private final static int SET_EQ_STEP = 1;
	private Handler mHandlerEQ = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_EQ_STEP:
				sendEQCmd((byte)msg.arg1, msg.arg2);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
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
		
		if (d0 == 0x6f){
			byte[] buf = new byte[] { 0x4, (byte) d0, (byte) d1, (byte) d2, (byte) 0xff, (byte) 0xff };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		} else {
			byte[] buf = new byte[] { 0x2, (byte) d0, (byte) d1, (byte) d2 };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		}

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
			} else if (p instanceof MyPreferenceSeekBar) {
				byte b = (byte) index;
				if (b < -5 || b > 5) {
					b = 0;
				}
				p.setSummary(b + "");
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
