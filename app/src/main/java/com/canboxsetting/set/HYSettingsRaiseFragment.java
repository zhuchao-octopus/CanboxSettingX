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

public class HYSettingsRaiseFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {
			//group1
		new NodePreference("blind_spot_detector", 0x83, 0x52, 0x1, 0),
		new NodePreference("air_controll_system", 0x83, 0x52, 0x2, 0),
		new NodePreference("raise_third_row_seat_back_fold_left_side", 0x83, 0x52, 0x3, 0, R.array.back_seat_status_entries, R.array.three_high_values),
		new NodePreference("raise_third_row_seat_back_fold_right_side", 0x83, 0x52, 0x4, 0, R.array.back_seat_status_entries, R.array.three_high_values),

		new NodePreference("raise_steering_wheel_heating", 0x83, 0x52, 0x5, 0),
		new NodePreference("raise_seat_heating_or_ventilation", 0x83, 0x52, 0x6, 0),
		new NodePreference("seat_position_change_prompt", 0x83, 0x52, 0x7, 0),
		new NodePreference("open_rear_camera", 0x83, 0x52, 0x8, 0),
		new NodePreference("air_circulation_activated_according_to_external_dust_conditions", 0x83, 0x52, 0xa, 0),
		new NodePreference("langauage5", 0x3, 0x0, 0x1, 0, R.array.launage_entries3, R.array.launage_entryValues3),
		//group2
		new NodePreference("sound_atmosphere_light_atmosphere_light", 0x83, 0x52, 0xb, 1, R.array.korea_atmosphere_light_entries, R.array.three_high_values),
		new NodePreference("sound_atmosphere_light_theme_color", 0x83, 0x52, 0xc, 1, R.array.korea_sound_atmosphere_light_theme_color_entries, R.array.korea_sound_atmosphere_light_theme_color_values),
		new NodePreference("sound_atmosphere_light_monochrome_light", 0x83, 0x52, 0xd, 1, R.array.korea_sound_atmosphere_light_monochrome_light_entries, R.array.dashboard_brightness_value),
		new NodePreference("sound_atmosphere_light_sound", 0x83, 0x52, 0xe, 1),
		new NodePreference("sound_atmosphere_light_brightness", 0x83, 0x52, 0xf, 1, R.array.korea_sound_atmosphere_light_brightness_entries, R.array.korea_sound_atmosphere_light_brightness_values),
		//group3
		new NodePreference("appointment_charging_method", 0xa9, 0x0, 0x1, 2, R.array.appointment_charging_method_entries, R.array.three_high_values),
		//group4
		new NodePreference("high_speed_limit", 0xa90a, 0x2, 0x0, 3, R.array.korea_speed_limit, R.array.envol_vaues),
		new NodePreference("eco_taxiing_energy_regeneration", 0xa90a, 0x1, 0x4, 3, R.array.prompt_volume_entries, R.array.envol_vaues),
		new NodePreference("sport_taxiing_energy_regeneration", 0xa90a, 0x1, 0x2, 3, R.array.prompt_volume_entries, R.array.envol_vaues),
		new NodePreference("comfort_taxiing_energy_regeneration", 0xa90a, 0x1, 0x0, 3, R.array.prompt_volume_entries, R.array.envol_vaues),
		new NodePreference("eco_mode1", 0xa90a, 0x0, 0x6, 3, R.array.korea_air_mode, R.array.envol_vaues),
		new NodePreference("sport_mode", 0xa90a, 0x0, 0x4, 3, R.array.korea_air_mode, R.array.envol_vaues),
		new NodePreference("comfort_mode", 0xa90a, 0x0, 0x2, 3, R.array.korea_air_mode, R.array.envol_vaues),
		new NodePreference("reset_driver_mode", 0xa90b, 3),
				
	};

	private final static int[] INIT_CMDS = { 0x41ff, 0x40ff,

	};
	
	private byte []mNewEnergy = new byte[]{0,0,0};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.hy_raise_setting);

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
				sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
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
					
					if ((NODES[i].mType &0xff)== 3) {					
						
						sendNewEnergyCanboxInfo(NODES[i], Integer.parseInt((String) newValue));
						
					} else {
						sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask,
								Integer.parseInt((String) newValue));
						if (key.equals("langauage5")) {
							((ListPreference) preference)
									.setValue((String) newValue);
							preference.setSummary("%s");
						}
					}
				} else if (preference instanceof SwitchPreference) {
//					if (NODES[i].mType == Node.TYPE_CUSTOM) {
//						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
//					} else {
						sendCanboxInfo(NODES[i].mCmd, NODES[i].mMask,
								((Boolean) newValue) ? 0x2 : 0x1);
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
		byte[] buf = new byte[] { 0x6, (byte) d0, (byte) d1, (byte) d2 };
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
				sp.setChecked(index == 2 ? true : false);
			}
		}
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private void updateView(byte[] buf) {

		int cmd;
		int mask;
		if (buf[1] == 0x57) {
			byte i;
			for (i = 0; i < SPEED.length; ++i) {
				if (SPEED[i] == buf[5]) {
					break;
				}
			}
			
			if (i < SPEED.length) {
				setPreference("high_speed_limit", i);
			}
			setPreference("eco_taxiing_energy_regeneration", (buf[6]&0xc0)>>6);
			setPreference("comfort_taxiing_energy_regeneration", (buf[6]&0x30)>>4);
			setPreference("sport_taxiing_energy_regeneration", (buf[6]&0xc)>>2);
//			
//			
			setPreference("eco_mode1", (buf[7]&0x20)>>5);
			setPreference("sport_mode", (buf[7]&0x8)>>3);
			setPreference("comfort_mode", (buf[7]&0x2)>>1);

		} else {
			for (int i = 0; i < NODES.length; ++i) {
				cmd = NODES[i].mStatus;
				mask = NODES[i].mMask;

				if ((buf[1] & 0xff) == cmd && (buf[2] & 0xff) == mask) {
					setPreference(NODES[i].mKey, (buf[3] & 0xff));
				}

			}

		}
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
