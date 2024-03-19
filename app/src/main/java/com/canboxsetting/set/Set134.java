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
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class Set134 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

					
			new NodePreference("langauage5", 0x9b01, 0x9600, 0x03, 0,
							R.array.chery_language_status_entries, R.array.twelve_values),

			new NodePreference("str_auto_compressor_status", 0x9b02, 0x9601, 0x80, 0),	

			new NodePreference("str_auto_cycle_mode", 0x9b03, 0x9601, 0x40, 0,
					R.array.automatic_air_conditioning_mode_entries, R.array.twelve_values),

			new NodePreference("str_ac_comfort_setting", 0x9b04, 0x9601, 0x30, 0,
							R.array.trumpche_speed_level_entries, R.array.twelve_values),

			new NodePreference("auto_heat", 0x9b05, 0x9602, 0x80, 0),	
			new NodePreference("fu_auto_heat", 0x9b06, 0x9602, 0x40, 0),	


			new NodePreference("over_speed", 0x9b07, 0x9603, 0xff, 0, 0, 200, 10),
			
			
			new NodePreference("meter_vol", 0x9b08, 0x9604, 0xff, 0,
					R.array.parksense_volume_entries, R.array.twelve_values),

			new NodePreference("str_power_on_time", 0x9b09, 0x9605, 0xff, 0, 0, 30,
					1),
			new NodePreference("str_start_time", 0x9b0a, 0x9606, 0xff, 0, 0, 30,
					1),
					
			new NodePreference("str_turn_mode", 0x9b0b, 0x9607, 0xff,
					0, R.array.trumpche_turn_mode_entries,
					R.array.twelve_values),
	
			new NodePreference("remoteunlocksettings", 0x9b0c, 0x9608, 0x80, 0,
					R.array.door_to_be_unlocked_entries, R.array.twelve_values),
					

			new NodePreference("str_speed_lock", 0x9b0d, 0x9608, 0x40, 0),
			new NodePreference("auto_unlock63", 0x9b0e, 0x9608, 0x20, 0),
			new NodePreference("remote_window", 0x9b0f, 0x9608, 0x10, 0),
			new NodePreference("front_wiper", 0x9b10, 0x9608, 0x8, 0),
			new NodePreference("back_wiper", 0x9b11, 0x9608, 0x4, 0),
			
			
			new NodePreference("myhome", 0x9b12, 0x9609, 0xc0, 0,
					R.array.myhome_light, R.array.twelve_values),

			new NodePreference("lamp_steering", 0x9b13, 0x9609, 0x20, 0),
			new NodePreference("running_lights", 0x9b14, 0x9609, 0x10, 0),

			new NodePreference("str_auto_light_sensitivity", 0x9b15, 0x9609,
					0xc, 0, R.array.trumpche_level_entries,
					R.array.twelve_values),

			new NodePreference("str_anion_mode", 0x9b16, 0x9601, 0x8, 0),
			new NodePreference("str_seat_welcome", 0x9b17, 0x9602, 0x20, 0),
			new NodePreference("str_auto_seat_recog", 0x9b18, 0x9602, 0x10, 0),
			new NodePreference("str_outside_rearview", 0x9b19, 0x9608, 0x2, 0),
			new NodePreference("str_unlock_the_lock_tone", 0x9b1a, 0x960a, 0x1, 0),
			new NodePreference("str_ambient_light_control", 0x9b1b, 0x9609, 0x2, 0), };

	private final static int[] INIT_CMDS = { 0x68 };


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
					} else if ((p instanceof MyPreferenceSeekBar)) {
						p.setOnPreferenceChangeListener(this);
						if (NODES[i].mKey.equals("over_speed")){
							((MyPreferenceSeekBar)p).setUnit("km/h");
						}else{
							((MyPreferenceSeekBar)p).setUnit("m");
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

					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							Integer.parseInt((String) newValue));					
				} else if (preference instanceof SwitchPreference) {
					
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								((Boolean) newValue) ? 0x1 : 0x0);	
					
				}  else if (preference instanceof MyPreferenceSeekBar) {
					int index = Integer.parseInt((String) newValue);
					if (key.equals("over_speed")){
						index = index/10;
					}
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							index);	
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
		byte[] buf = new byte[] {  0x3,(byte) 0x6a, 0x5, 0x1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x2, (byte) d0, (byte) d1, (byte) d2};
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
				if (key.equals("over_speed")){
					index = index*10;
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
