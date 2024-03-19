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

public class Set275 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "Set275";

	private static final NodePreference[] NODES = {

			new NodePreference("remote_unlock", 0x8800, 0x6600, 0x80, 0,
					R.array.door_to_be_unlocked_entries, R.array.two_values),

			new NodePreference("psa_simple_19", 0x8801, 0x6600, 0x40, 0),
			new NodePreference("parkunlock", 0x8802, 0x6600, 0x20, 0),
			
			new NodePreference("headlight_delay", 0x8803, 0x6601, 0xe0, 0,
					R.array.headlight_delay_oushange, R.array.five_values),

			new NodePreference("light_one_key_turn", 0x8804, 0x6601, 0xe0, 0,
					R.array.light_one_key_turn_entries, R.array.four_values),		


			new NodePreference("auto_fold_wing_mirror", 0x8805, 0x6602, 0x80, 0),
			new NodePreference("reversing_rear_wiper_auxiliary", 0x8806, 0x6602,
					0x40, 0),					
					
					
			new NodePreference(
					"air_conditioning_settings_unlock_active_ventilation",
					0x8807, 0x6603, 0x80, 0),
			new NodePreference(
					"air_conditioning_settings_unlock_windows_for_ventilation",
					0x8808, 0x6603, 0x20, 0),
			new NodePreference(
					"air_conditioning_settings_air_purification_system_automatically_opens",
					0x8809, 0x6603, 0x10, 0),

			new NodePreference("reset_info", 0x880a, 0, 0),
			new NodePreference("factory_reset_settings", 0x88ff, 0, 0),

			new NodePreference("gwm_precollision_warning_system", 0x880c, 0x6606, 0x1, 0),
			new NodePreference("forward_collision_sensitivity", 0x880d, 0x6606, 0x2, 0),
			new NodePreference("oushang_2b", 0x880e,0x6607, 0x1, 0),
			new NodePreference("oushang_2c", 0x880f, 0x6607,0x2, 0),
			new NodePreference("oushang_2d", 0x8810, 0x6607,0x4, 0),
			new NodePreference("oushang_20", 0x8811,0x6608,0x1, 0),
			new NodePreference("oushang_23", 0x8812, 0x6608,0x2, 0),
			

			new NodePreference("oushang_19", 0x8813, 0x6606, 0x0c, 0,
					R.array.six_values, R.array.six_values),
					
					
			new NodePreference("high_beam_control", 0x8814, 0x6601,0x4, 0),

			new NodePreference("lane_assist_warning_method", 0x8815, 0x6608, 0x0c, 0,
					R.array.lane_assist_warning_method_entries, R.array.three_high_values),

			new NodePreference("renual08", 0x8816, 0x6608, 0x30, 0,
					R.array.alert_volume_level_entries, R.array.three_high_values),
					
					
			new NodePreference("rear_warning_rear_end_warning_sound", 0x8817, 0x6607,0x8, 0),
			new NodePreference("door_and_window_rain_setting_skylight_setting",
					0x8818, 0x6609,0x1, 0),
			new NodePreference(
					"air_conditioning_set_air_conditioning_self_drying",
					0x8819, 0x660a,0x1, 0),
			new NodePreference("oushang_1e", 0x8820, 0x6606, 0x0c, 0,
					R.array.alert_volume_level_entries, R.array.three_values),
					
			new NodePreference("rearview_mirror_tilt", 0x8821, 0x6602,0x20, 0),
			new NodePreference(
					"gac_settings_seat_pass_in_and_out_conveniently", 0x8822,
					0x6602,0x10, 0),

	};

	private final static int[] INIT_CMDS = { 0x66 };


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
					} else if (p instanceof MyPreferenceSeekBar) {
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

					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							Integer.parseInt((String) newValue));					
				} else if (preference instanceof SwitchPreference) {
					
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								((Boolean) newValue) ? 0x1 : 0x0);	
					
				} else {
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							 0x0);	
				}
				break;
			}
		}
	}

	public boolean onPreferenceClick(Preference arg0) {
		try {
			udpatePreferenceValue(arg0, null);
		} catch (Exception e) {

		}
	return false; 
}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] {  (byte) 0x90, 2,(byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x2, (byte) d1, (byte) d2};
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
				p.setSummary(index+"");
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
//		if (buf[0] == 0x78) {
//			if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4])
//					|| (mVisible[3] != buf[5])) {
//				Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
//				removeAll();
//				init();
//			}
//		}
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
