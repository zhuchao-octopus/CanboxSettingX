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

public class Set150 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

	new NodePreference("parking_assist", 0x00c0, 0x3600, 0xc0, 0, R.array.kuwei_park_entries, R.array.twelve_values), 
			
	new NodePreference("headlight_off", 0x0030, 0x3600, 0x30, 0, R.array.enheadlights_off, R.array.twelve_values), 
	new NodePreference("unlock_open_lamp", 0x000c, 0x3600, 0x0c, 0, R.array.enheadlights_off, R.array.twelve_values), 
	

	new NodePreference("headlight_wiper", 0x0002, 0x3600, 0x02, 0),	
	
	new NodePreference("flash_lights_with_lock", 0x0001, 0x3600, 0x01, 0),	
	
	new NodePreference("zhonghua_xx", 0x0180, 0x3601, 0x80, 0),	
	new NodePreference("unlock_driving", 0x0140, 0x3601, 0x40, 0),	
	
	
	
	new NodePreference("first_press_of_key_unlocks", 0x0120, 0x3601, 0x20, 0, R.array.remote_unlock_entries, R.array.twelve_values), 

	new NodePreference("keyless_entry", 0x0110, 0x3601, 0x10, 0),	
	new NodePreference("outseat_heating", 0x0108, 0x3601, 0x08, 0),
	
	new NodePreference("engine_power", 0x0106, 0x3601, 0x06, 0, R.array.enpower_off, R.array.twelve_values), 
	new NodePreference("language", 0x02f0, 0x3602, 0xf0, 0, R.array.kuwei_language_entries, R.array.twelve_values), 
	new NodePreference("unit_setting", 0x020f, 0x3602, 0x0f, 0, R.array.kuwei_metric_entries, R.array.twelve_values), 

	};

	private final static int[] INIT_CMDS = { 0x36 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.empty_setting);

		init();

	}

	private byte[] mCmdSend = new byte[] { (byte) 0xc6, 0x8, 0, 0, 0, 0, 0, 0,
			0, 0 };

	private void sendCmd(int cmd, int value) {
		int mask = cmd & 0xff;
		int index = (cmd & 0xff00) >> 8;
		index += 2;
		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}

		mCmdSend[index] &= ~mask;
		mCmdSend[index] |= (value << start) & mask;

		BroadcastUtil.sendCanboxInfo(getActivity(), mCmdSend);
	}

	private void init() {

		for (int i = 0; i < NODES.length; ++i) {
			Preference p = NODES[i].createPreference(getActivity());
			if (p != null) {

				Preference ps = getPreferenceScreen();
				if (ps instanceof PreferenceScreen) {
					boolean add = true;
					if (((NODES[i].mType & 0xff0000) >> 16) != 0) {
						int index = ((NODES[i].mType & 0xff00) >> 8) + 2;
						if ((mVisible[index] & NODES[i].mType) == 0) {
							add = false;
						}
					}

					if (add) {
						((PreferenceScreen) ps).addPreference(p);
					}

					if ((p instanceof ListPreference)
							|| (p instanceof SwitchPreference)) {
						p.setOnPreferenceChangeListener(this);
					} else if ((p instanceof MyPreferenceSeekBar)) {
						p.setOnPreferenceChangeListener(this);
						if (NODES[i].mKey.equals("over_speed")) {
							((MyPreferenceSeekBar) p).setUnit("km/h");
						} else {
							((MyPreferenceSeekBar) p).setUnit("m");
						}
					}
				}

			}
		}
	}

	private byte[] mVisible = new byte[] { 0x78, 0, 0, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };

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

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {

					sendCmd(NODES[i].mCmd, Integer.parseInt((String) newValue));

				} else if (preference instanceof SwitchPreference) {

					sendCmd(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);

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

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] {(byte) 0x90, 0x1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x2, (byte) d0, (byte) d1, (byte) d2 };
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
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 1 ? true : false);
			} else if (p instanceof MyPreferenceSeekBar) {
				if (key.equals("over_speed")) {
					index = index * 10;
				}
				p.setSummary(index + "");
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
		// if (buf[0] == 0x78) {
		// if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4])
		// || (mVisible[3] != buf[5])) {
		// Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
		// removeAll();
		// init();
		// }
		// }
		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff00) >> 8;
			index = NODES[i].mStatus & 0xff;
			mask = NODES[i].mMask;

			if ((buf[0] & 0xff) == cmd) {
				mask = (NODES[i].mMask);
				int value = getStatusValue1(buf[2 + index], mask);
				setPreference(NODES[i].mKey, value);
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
