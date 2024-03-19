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

public class Set100 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "Set275";

	private static final NodePreference[] NODES = {

			new NodePreference("gs4_energy_recovery", 0x9701, 0x2901, 0xff, 0,
					R.array.four_values, R.array.four_values),
					

			new NodePreference("head_up_display", 0x9702, 0x2902, 0xff, 0),
			new NodePreference("vps_switch", 0x9703, 0x2903, 0xff, 0),
			
			new NodePreference("power_level", 0x9704, 0x2904, 0xff, 0,
					R.array.four_values, R.array.four_values),	
					

					new NodePreference("power_lock", 0x9705, 0x2905, 0xff, 0),

					new NodePreference("headlight_off", 0x9707, 0x2907, 0xff, 0,
							R.array.enheadlights_off, R.array.four_values),	
							
					new NodePreference("car_in_light", 0x9708, 0x2908, 0xff, 0,
								R.array.dongfeng_atmosphere_lamp_entries, R.array.three_values),	

					new NodePreference("welcome_light", 0x9709, 0x2909, 0xff, 0),
					

	};

	private final static int[] INIT_CMDS = { 0x26 };


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

			if ((buf[0] & 0xff) == cmd && (buf[2] & 0xff) == index) {
				mask = (NODES[i].mMask);
				int value = getStatusValue1(buf[3], mask);
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
