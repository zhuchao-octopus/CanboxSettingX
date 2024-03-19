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
import com.common.util.Util;

public class Set208 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {
		
	new NodePreference("esp_system", 0x6f01, 0x6100, 0x10, 0),									

	new NodePreference("active_braking", 0x6f02, 0x6100, 0xf, 0,
			R.array.acc_assist_warn_tip_entries, R.array.four_values),
	
						
	new NodePreference("blind_spot_assist_system", 0x6f03, 0x6101, 0x10, 0),	

	new NodePreference("driver_monitor", 0x6f04, 0x6101, 0xf, 0,
		R.array.driver_attention_monitor, R.array.three_values),
		
			new NodePreference("lanesystem", 0x6f05, 0x6102, 0x10, 0),

			
			
			new NodePreference("winter_tyres", 0x6f11, 0x6103, 0xf0, 0,
					R.array.winter_tyres, R.array.eight_values),

	new NodePreference("standby_mode", 0x6f12, 0x6103, 0x1, 0),
	new NodePreference("central_door_lock_sound_feedback", 0x6f13, 0x6104, 0x10, 0),
	new NodePreference("gac_settings_auto_door_locks", 0x6f14, 0x6104, 0x1, 0),
	new NodePreference("ambient_lighting", 0x6f21, 0x6105, 0x10, 0),
					
	new NodePreference("external_lighting_delay_off", 0x6f22, 0x6105, 0xf, 0,
				R.array.swee_flow_me_home_times, R.array.five_values),

	new NodePreference("interior_lighting_delay_off", 0x6f23, 0x6106, 0x10, 0),
	new NodePreference("auxiliary_speedometer", 0x6f31, 0x6106, 0x1, 0),
							

		new NodePreference("mileage_unit", 0x6f32, 0x6107, 0x10, 0,
									R.array.mileage_unit_t, R.array.two_values),
									

		new NodePreference("factory_reset_settings", 0x6f33, 0, 0),
									

		new NodePreference("main_lane_assist", 0x6f34, 0x6100, 0x20, 0),
	};

	private final static int[] INIT_CMDS = { 0x61 };


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
					} else {
						p.setOnPreferenceClickListener(this);
					}
				}

			}
		}
	}
	
	private void removeAll() {
		getPreferenceScreen().removeAll();
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

					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							Integer.parseInt((String) newValue));					
				} else if (preference instanceof SwitchPreference) {
					
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								((Boolean) newValue) ? 0x1 : 0x0);	
					
				}  else {
					
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff, 0x1);	
				
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
		byte[] buf = new byte[] {  0x3, (byte) 0x6a, 0x5, 1,(byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x4, (byte) d0,  (byte) d1, (byte) d2, 0,0 };
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
		if (buf[0] == 0x78) {
			if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4])
					|| (mVisible[3] != buf[5])) {
				Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
				removeAll();
				init();
			}
		}
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
