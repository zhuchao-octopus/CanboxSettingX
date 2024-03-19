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
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceSeekBar;
import com.common.view.MyPreferenceEdit.IButtonCallBack;

public class Set253 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

			new NodePreference("parking_assist_system_setting", 0x9701, 0x700, 0xc0, 0,
					R.array.kuwei_park_entries,
					R.array.three_values),
			new NodePreference("gac_settings_headlight_off_delay", 0x9711, 0x702, 0xc0, 0,
					R.array.fiat_headlight_off_delay, R.array.four_values),
			new NodePreference("gac_settings_illuminated_approach", 0x9712, 0x702, 0x30, 0,
					R.array.fiat_headlight_off_delay, R.array.four_values),
					
			new NodePreference("gac_settings_light_when_wiper", 0x9713, 0x702, 0x08, 0),	
			new NodePreference("gac_settings_flash_lights_w_lock", 0x9714, 0x702, 0x02, 0),	
			new NodePreference("gac_settings_auto_unlock_on_exit", 0x9722, 0x703, 0x40, 0),	
			
			new NodePreference("first_press_of_key_unlocks", 0x9724,
					0x703, 0x08, 0, R.array.nearly_unclock,
					R.array.radarrange_entries1),

			new NodePreference("gac_settings_passive_enter", 0x9725,
					0x703, 0x04, 0),
			new NodePreference("seat_auto_heat", 0x9727,
					0x701, 0x01, 0),

			new NodePreference("gac_settings_engine_off_power_delay", 0x9732,
					0x704, 0x60, 0, R.array.jeep_windlock_settings,
					R.array.four_values),

			new NodePreference("unit_setting", 0x9752,
					0x7007, 0x01, 0, R.array.gac_units_entries,
					R.array.two_values),

	};

	private final static int[] INIT_CMDS = { 0x7

	};

	private Preference[] mPreferences = new Preference[NODES.length];

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
					boolean add = true;

					if (add) {
						((PreferenceScreen) ps).addPreference(p);
					}

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

				byte[] buf = new byte[] { (byte) 0x1f, (byte) 1,
						(byte) (msg.what & 0xff) };
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);

			}
		}
	};

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					int value = Integer.parseInt((String) newValue);
					
					if ("gac_settings_headlight_off_delay"
							.equals(NODES[i].mKey)
							|| "gac_settings_illuminated_approach"
									.equals(NODES[i].mKey)) {
						String []ss = getResources().getStringArray(R.array.enheadlights_off_vaues);
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								Integer.parseInt(ss[value]));
						
					} else 	if ("gac_settings_engine_off_power_delay"
							.equals(NODES[i].mKey)) {
						String []ss = getResources().getStringArray(R.array.enpower_off_vaues);
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								Integer.parseInt(ss[value]));
						
					} else {
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								Integer.parseInt((String) newValue));
					}
					
					

				} else if (preference instanceof SwitchPreference) {
					// if (NODES[i].mType == Node.TYPE_CUSTOM) {
					// sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
					// } else {
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 2
									: 1);
					// }
				}
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
		String key = arg0.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
						NODES[i].mCmd & 0xff,0);

			}
		}
		return false;
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {

		byte[] buf = new byte[] { (byte) d0, 0x2, (byte) d1, (byte) d2 };
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
		// if (buf[0] == 0x57) {
		//
		//
		// } else {
		for (int i = 0; i < NODES.length; ++i) {

			if ((NODES[i].mType & NodePreference.PREFERENCE_MASK) == NodePreference.SCREENPREFERENCE) {
				cmd = (NODES[i].mStatus & 0xff00) >> 8;
				index = (NODES[i].mStatus & 0xff);
				mask = NODES[i].mType & 0xff;
			} else {
				cmd = (NODES[i].mStatus & 0xff00) >> 8;
				index = (NODES[i].mStatus & 0xff);
				mask = NODES[i].mMask;
			}

			if ((buf[0] & 0xff) == cmd) {
				value = getStatusValue1(buf[2 + index], mask);
				setPreference(NODES[i].mKey, value);
			}

		}

		// }
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
