package com.canboxsetting.set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class BaoJunSetingsRaiseFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {

	private static final NodePreference[] NODES = {

		
		


		new NodePreference("folding", 0x83, 0x52, 0x1, 0),
		new NodePreference("rain_and_snow_mode", 0x83, 0x52, 0x3, 0),
		new NodePreference("smoking_mode", 0x83, 0x52, 0x4, 0),
		new NodePreference("cool_mode", 0x83, 0x52, 0x5, 0),
		new NodePreference("warm_mode", 0x83, 0x52, 0x6, 0),
		new NodePreference("remote_control_window", 0x83, 0x52, 0x7, 0),
		

		new NodePreference("unlock_the_door_when_actively_entering", 0x83, 0x52, 0x8, 0,
		R.array.smartunlock, R.array.three_values),
		new NodePreference("flameout_automatic_latch", 0x83, 0x52, 0x9, 0,
				R.array.parking_unlock, R.array.three_values),
				new NodePreference("car_lock_reminder", 0x83, 0x52, 0xa, 0,
				R.array.car_lock_reminder_entries, R.array.three_values),
				
				
		new NodePreference("gac_settings_auto_lock_when_drive", 0x83, 0x52, 0xb, 0),
		new NodePreference("lane_change_flash", 0x83, 0x52, 0xc, 0),
		new NodePreference("indoor_lamp_delay", 0x83, 0x52, 0xd, 0,
				R.array.new_tourui_timer_entries, R.array.three_values),
				
		new NodePreference("str_go_home", 0x83, 0x52, 0xe, 0),
		new NodePreference("front_wiper", 0x83, 0x52, 0x11, 0),
		new NodePreference("anti_collision_warning", 0x83, 0x52, 0x13, 0),
		new NodePreference("automatic_emergency_braking", 0x83, 0x52, 0x15, 0),
		
		
			
					
			new NodePreference("finding_car", 0x83, 0x52, 0x10, 0,
					R.array.car_lock_reminder_entries, R.array.three_values),	
					
					

					new NodePreference("reset_default_settings", 0x8300, 0),

					new NodePreference("tire_pressure_reset", 0x8302, 0),
		
	};
	
	

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
//				Log.d("ffck", i+"i="+ps+":"+p);
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
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mMask != 0) {
				mHandler.sendEmptyMessageDelayed(NODES[i].mMask, (i * 10));
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo(0x90, 0x52, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxData(int cmd) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),
				((cmd & 0xff) >> 0));

	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					int value = Integer.parseInt((String) newValue);

					// if ("backlighting".equals(key)){
					value++;
					// }
					sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
				} else if (preference instanceof SwitchPreference) {
					int value;
					value = ((Boolean) newValue) ? 0x2 : 0x1;
					// if (NODES[i].mShow == 0) {
					// value ++;
					// }

					sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask, value);
				} else if (preference instanceof Preference) {
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff, 1);
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
				lp.setSummary("%s");
				// Log.d("aa", key+":"+((ListPreference)
				// findPreference(key)).getEntry());
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			} else if (p instanceof MyPreferenceSeekBar) {
				p.setSummary(index + "");
			}
		}
	}

	private void updateView(byte[] buf) {

		int value;

		for (int i = 0; i < NODES.length; ++i) {

			if (NODES[i].mStatus == (buf[0] & 0xff)
					&& NODES[i].mMask == (buf[2] & 0xff)) {
				value = buf[3] & 0xff;
				// if (NODES[i].mShow == 0) {
				 value --;
				// }
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
