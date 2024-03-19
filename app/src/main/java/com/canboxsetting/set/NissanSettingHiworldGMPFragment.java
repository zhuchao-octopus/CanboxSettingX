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
import com.common.util.Node;
import com.common.util.Util;


public class NissanSettingHiworldGMPFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "NissanSettingHiworldGMPFragment";
	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}	
	private static final Node[] NODES = {
////空调系统设置
		new Node("volumeset", 0x3A01, 0x35010000, 0xC00000, 0,
				Node.TYPE_BUFF1),
		
		new Node("air_temperature_settings", 0x3A04, 0x35010000, 0x030000, 0,
				Node.TYPE_BUFF1),
						
		new Node("backwindowdefog", 0x3A0B, 0x35010000, 0x01000000, 0,
				Node.TYPE_BUFF1),
		
		new Node("frontwindowdefog", 0x3A0A, 0x35010000, 0x02000000, 0,
				Node.TYPE_BUFF1),
				
		new Node("seat_heating", 0x3A08, 0x35010000, 0x10000000, 0,
				Node.TYPE_BUFF1),
								
		new Node("air_model", 0x3A02, 0x35010000, 0x300000, 0,
				Node.TYPE_BUFF1),
				
		new Node("rear_air", 0x3A09, 0x35010000, 0x0C000000, 0,
				Node.TYPE_BUFF1),
		
		new Node("auto_heating_when_remote_start", 0x3A0E, 0x35020000, 0x18, 0,
				Node.TYPE_BUFF1),
				
		new Node("air_quality_sensor_1", 0x3A03, 0x35010000, 0x0C0000, 0,
				Node.TYPE_BUFF1),
								
		new Node("air_quality_sensor_2", 0x3A0D, 0x35020000, 0x60, 0,
				Node.TYPE_BUFF1),
				
		new Node("seat_auto_wind", 0x3A05, 0x35010000, 0x80000000, 0,
				Node.TYPE_BUFF1),
										
		new Node("seat_auto_heat", 0x3A06, 0x35010000, 0x40000000, 0,
				Node.TYPE_BUFF1),
				
		new Node("remote_control_start_seat_auto_wind", 0x3A07, 0x35010000, 0x20000000, 0,
				Node.TYPE_BUFF1),
												
		new Node("remote_control_start_air_condition_setting", 0x3A0C, 0x35020000, 0x80, 0,
				Node.TYPE_BUFF1),
//						
//车辆舒适与便利设置
		new Node("astern_wiper", 0x5A06, 0x55010000, 0x0200, 0,
				Node.TYPE_BUFF1),
								
		new Node("driver_personality_setting", 0x5A05, 0x55010000, 0x0400, 0,
				Node.TYPE_BUFF1),
//		
		new Node("blind_area_warning", 0x4A02, 0x45010000, 0x4000, 0,
				Node.TYPE_BUFF1),
				
		new Node("auto_wipers", 0x5A08, 0x56010000, 0x8000, 0,
				Node.TYPE_BUFF1),
										
		new Node("rearview_mirror_auto_tile", 0x5A02, 0x55010000, 0x1000, 0,
				Node.TYPE_BUFF1),
				
		new Node("rearview_auto_folding", 0x5A04, 0x55010000, 0x0800, 0,
				Node.TYPE_BUFF1),
		
		new Node("driver_seat_offset_setting_when_stop", 0x5A01, 0x55010000, 0x8000, 0,
				Node.TYPE_BUFF1),
												
		new Node("turnning_cylinder_away_vehicle_offset_setting", 0x5A03, 0x55010000, 0x6000, 0,
				Node.TYPE_BUFF1),
						
		new Node("turnning_cylinder_away_vehicle_tilt_setting", 0x5A07, 0x55010000, 0x0100, 0,
				Node.TYPE_BUFF1),
				
		new Node("adaptive_cruise_start_alert", 0x4A09, 0x46010000, 0x2000, 0,
				Node.TYPE_BUFF1),
						
		new Node("gm_movement_towards", 0x4A0B, 0x46010000, 0x0400, 0,
				Node.TYPE_BUFF1),
//
//碰撞/监测系统
		new Node("automatic_collision_avoidance", 0x4A06, 0x45010000, 0x0300, 0,
				Node.TYPE_BUFF1),
						
		new Node("car_status", 0x4A07, 0x46010000, 0x8000, 0,
				Node.TYPE_BUFF1),
		
		new Node("reversing_radar", 0x4A05, 0x45010000, 0x0400, 0,
					Node.TYPE_BUFF1),
					
		new Node("prompt_tone_type", 0x4A04, 0x45010000, 0x2000, 0,
					Node.TYPE_BUFF1),
				
		new Node("parking_assist_system_setting", 0x4A01, 0x45010000, 0x8000, 0,
				Node.TYPE_BUFF1),
		
		new Node("parking_assist_system_setting_with_trailer_compensation", 0x4A03, 0x45010000, 0x1800, 0,
						Node.TYPE_BUFF1),
						
		new Node("ramp_assist_system", 0x4A08, 0x46010000, 0x4000, 0,
						Node.TYPE_BUFF1),
					
		new Node("gm_preposition_pedestrian_detection", 0x4A0A, 0x46010000, 0x1800, 0,
					Node.TYPE_BUFF1),
//灯光设置
		new Node("find_headlights", 0x6C01, 0x67010000, 0x8000, 0,
				Node.TYPE_BUFF1),
						
		new Node("padlock_headlight_delay", 0x6C02, 0x67010000, 0x6000, 0,
				Node.TYPE_BUFF1),
		
		new Node("vehicle_positioning_lamp", 0x6C03, 0x67010000, 0x0400, 0,
					Node.TYPE_BUFF1),
//中控门锁设置
		new Node("prevent_locking", 0x6A01, 0x65010000, 0x8000, 0,
				Node.TYPE_BUFF1),
								
		new Node("start_locking", 0x6A02, 0x65010000, 0x4000, 0,
				Node.TYPE_BUFF1),
								
		new Node("locking_delay", 0x6A04, 0x65010000, 0x0800, 0,
				Node.TYPE_BUFF1),
				
		new Node("auto_unlocking_setting_with_automatic", 0x6A03, 0x65010000, 0x3000, 0,
				Node.TYPE_BUFF1),
						
		new Node("auto_unlocking_setting_with_manually", 0x6A05, 0x65010000, 0x0300, 0,
				Node.TYPE_BUFF1),
								
		new Node("prevent_the_reverse_lock", 0x6A06, 0x65010000, 0x0400, 0,
				Node.TYPE_BUFF1),
//智能遥控钥匙设置
		new Node("smartstart", 0x6B07, 0x66010000, 0x010000, 0,
				Node.TYPE_BUFF1),
				
		new Node("unlocklight", 0x6B02, 0x66010000, 0x200000, 0,
				Node.TYPE_BUFF1),
				
		new Node("lockdoor_prompt", 0x6B01, 0x66010000, 0xC00000, 0,
				Node.TYPE_BUFF1),
				
		new Node("smartunlock", 0x6B03, 0x66010000, 0x100000, 0,
				Node.TYPE_BUFF1),
				
		new Node("smartlock_again", 0x6B04, 0x66010000, 0x080000, 0,
				Node.TYPE_BUFF1),
				
		new Node("nearly_unclock", 0x6B08, 0x66010000, 0x80000000, 0,
				Node.TYPE_BUFF1),
				
		new Node("remindkey", 0x6B09, 0x66010000, 0x40000000, 0,
				Node.TYPE_BUFF1),
//			
		new Node("personalization", 0x5A05, 0x55010000, 0x0400, 0,
				Node.TYPE_BUFF1),
				
		new Node("autorelockdoors", 0x6B05, 0x66010000, 0x040000, 0,
				Node.TYPE_BUFF1),
				
		new Node("automatic_lock", 0x6B0A, 0x66010000, 0x30000000, 0,
				Node.TYPE_BUFF1),
				
		new Node("smartwindow", 0x6B0C, 0x66010000, 0x04000000, 0,
				Node.TYPE_BUFF1),
				
		new Node("driver_key_auto_recognize_setting", 0x6B06, 0x66010000, 0x020000, 0,
				Node.TYPE_BUFF1),
				
		new Node("remote_control_sliding_door_setting", 0x6B0B, 0x66010000, 0x08000000, 0,
				Node.TYPE_BUFF1),

//仪表显示设置 
		new Node("hybrid_power_eco_indicate_setting", 0x7A01, 0x75010000, 0x8000, 0,
				Node.TYPE_BUFF1),
				
		new Node("dashboard_navigation_info_indicate_setting", 0x7A02, 0x75010000, 0x4000, 0,
				Node.TYPE_BUFF1),
				
		new Node("speed_range_prompt_model_setting", 0x7A03, 0x75010000, 0x2000, 0,
				Node.TYPE_BUFF1),
//运动模式设置
		new Node("sport_model_engine_status_setting", 0x8A01, 0x85010000, 0x8000, 0,
				Node.TYPE_BUFF1),
				
		new Node("sport_model_backlight_setting", 0x8A02, 0x85010000, 0x4000, 0,
				Node.TYPE_BUFF1),		
		};

		private final static int[] INIT_CMDS = {
	0x35,0x47,0x55,0x65,0x66,0x75,0x85
	};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.nissan_hiworld_setting);
		for (int i = 0; i < NODES.length; ++i) {
			mPreferences[i] = findPreference(NODES[i].mKey);
			if (mPreferences[i] != null) {
				if (mPreferences[i] instanceof PreferenceScreen) {
					mPreferences[i].setOnPreferenceClickListener(this);
				} else {
					mPreferences[i].setOnPreferenceChangeListener(this);
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
		mType = 0;
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
				sendCanboxInfo(0x60, 5, 1, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxData(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

	}
	
	private void sendCanboxData(int cmd) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),((cmd & 0xff) >> 0));
	}
	
	private void sendCanboxData2(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),((cmd & 0xff) >> 0), value);
	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		int buf = 0;
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) 
				{
					sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
				} 
				else if (preference instanceof SwitchPreference) 
				{
						sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
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
			try {
				udpatePreferenceValue(arg0, null);
			} catch (Exception e) {

			}
		return false; 
	}
		
	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { 0x01, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x02, (byte) d0, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendCanboxInfo(int d0, int d1, int d2, int d3) {
		byte[] buf = new byte[] { 0x03, (byte) d0, (byte) d1, (byte) d2, (byte)d3};
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
				sp.setChecked(index == 0 ? false : true);
			} 
		}
	}

	private int getStatusValue(byte[] buf, int mask, int subpostion) {

		int value = 0;
		int value1 = 0;
		int value2 = 0;
		int value3 = 0;
		int value4 = 0;
		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}
		value1 = 0;
		if (buf.length > 3) {
			value1 = ((buf[2] & 0xff) << 0);
		}
		if (buf.length > 4) {
			value1 |= ((buf[3] & 0xff) << 8);
		}
		if (buf.length > 5) {
			value1 |= ((buf[4] & 0xff) << 16);
		}
		if (buf.length > 6) {
			value1 |= ((buf[5] & 0xff) << 24);
		}
		
		value2 = 0;
		if (buf.length > 7) {
			value2 = ((buf[6] & 0xff) << 0);
		}
		if (buf.length > 8) {
			value2 |= ((buf[7] & 0xff) << 8);
		}
		if (buf.length > 9) {
			value2 |= ((buf[8] & 0xff) << 16);
		}
		if (buf.length > 10) {
			value2 |= ((buf[9] & 0xff) << 24);
		}
		
		value3 = 0;
		if (buf.length > 11) {
			value3 = ((buf[10] & 0xff) << 0);
		}
		if (buf.length > 12) {
			value3 |= ((buf[11] & 0xff) << 8);
		}
		if (buf.length > 13) {
			value3 |= ((buf[12] & 0xff) << 16);
		}
		if (buf.length > 14) {
			value3 |= ((buf[13] & 0xff) << 24);
		}
		
		value4 = 0;
		if (buf.length > 15) {
			value4 = ((buf[14] & 0xff) << 0);
		}
		if (buf.length > 16) {
			value4 |= ((buf[15] & 0xff) << 8);
		}
		if (buf.length > 17) {
			value4 |= ((buf[16] & 0xff) << 16);
		}
		if (buf.length > 18) {
			value4 |= ((buf[17] & 0xff) << 24);
		}
		switch(subpostion)
		{
			case 0:
			{
				value = ((value1 & mask) >>> start);
				break;
			}
			case 1:
			{
				value = ((value1 & mask) >>> start);
				break;
			}
			case 2:
			{
				value = ((value2 & mask) >>> start);
				break;
			}
			case 3:
			{
				value = ((value3 & mask) >>> start);
				break;
			}
			case 4:
			{
				value = ((value4 & mask) >>> start);
				break;
			}
			
		}
		return value;
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
		return ((value & mask) >> start);
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
		int value;
		int subpostion ;
		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff000000) >>> 24;
			subpostion = (NODES[i].mStatus & 0xff0000) >>> 16;
			if(cmd == (buf[0] & 0xff))	
			{
				mask = (NODES[i].mMask);
				value = getStatusValue(buf, mask, subpostion);
				setPreference(NODES[i].mKey, value);
			}
		}

	}

	private void showPreference(String id, int show, String parant) {
		Preference preference = null;

		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(id)) {
				preference = mPreferences[i];
				break;
			}
		}

		if (preference != null) {
			PreferenceScreen ps;
			if (parant != null) {
				ps = (PreferenceScreen) findPreference(parant);
			} else {
				ps = getPreferenceScreen();
			}
			if (ps != null) {
				if (show != 0) {
					if (ps.findPreference(id) == null) {
						ps.addPreference(preference);
					}
				} else {
					if (findPreference(id) != null) {
						boolean b = ps.removePreference(preference);
						// Log.d("dd", "" + b);
					}
				}
			}
		}

	}

	private void showPreference(String id, int show) {
		showPreference(id, show, "driving_mode");

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
								Log.d("abcd", "!!!!!!!!" + Util.byte2HexStr(buf));
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
