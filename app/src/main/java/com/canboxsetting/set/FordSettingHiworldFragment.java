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


public class FordSettingHiworldFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "FordSettingHiworldFragment";
	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}	
	
	private static final Node[] NODES = {
////车辆设置
		new Node("trip_settings", 0x6C05, 0x67010000, 0x0400, 0,
				Node.TYPE_BUFF1),
		
		new Node("camera_delay", 0xF206, 0xE8010000, 0xFF0000, 0,
				Node.TYPE_BUFF1),
						
		new Node("bzs", 0x6D06, 0x68010000, 0x0100, 0,
				Node.TYPE_BUFF1),
		
//		new Node("language_status", 0x3A0A, 0x35010000, 0x02000000, 0,
//				Node.TYPE_BUFF1),
				
		new Node("traction_control_system", 0x8A03, 0x85010000, 0x0100, 0,
				Node.TYPE_BUFF1),
								
		new Node("turn_light", 0x6C03, 0x67010000, 0x0100, 0,
				Node.TYPE_BUFF1),
				
		new Node("message_tone", 0x6C06, 0x67010000, 0x0800, 0,
				Node.TYPE_BUFF1),
		
		new Node("waring_tone", 0x6C07, 0x67010000, 0x1000, 0,
				Node.TYPE_BUFF1),
				
//		new Node("rain_sensing_wipers", 0x3A03, 0x35010000, 0x0C0000, 0,
//				Node.TYPE_BUFF1),
								
//		new Node("ambient_light_brightness", 0x3A0D, 0x35020000, 0x60, 0,
//				Node.TYPE_BUFF1),
				
//		new Node("reverse_video_zoom_status", 0x3A05, 0x35010000, 0x80000000, 0,
//				Node.TYPE_BUFF1),
										
//		new Node("compass_switch", 0x3A06, 0x35010000, 0x40000000, 0,
//				Node.TYPE_BUFF1),
				
//		new Node("degree", 0x3A07, 0x35010000, 0x20000000, 0,
//				Node.TYPE_BUFF1),
												
		new Node("atmosphere_light_color", 0x6F03, 0x61010000, 0xFF0000, 0,
				Node.TYPE_BUFF1),
				
		new Node("temp47", 0x6D04, 0x68010000, 0x1000, 0,
				Node.TYPE_BUFF1),
				
//		new Node("mfd_screen_brightness", 0x3A07, 0x35010000, 0x20000000, 0,
//				Node.TYPE_BUFF1),
												
		new Node("park_lock_ctrl", 0x6C04, 0x67010000, 0x0200, 0,
				Node.TYPE_BUFF1),
//功放设置
		new Node("speed_compensated_vol", 0xAD07, 0xA6020000, 0x0E0000, 0,
				Node.TYPE_BUFF1),
								
		new Node("surround_sound", 0xAD08, 0xA6020000, 0x010000, 0,
				Node.TYPE_BUFF1),
		
		new Node("reset_balance", 0xAD0A00, 0x00000000, 0x0000, 0,
				Node.TYPE_BUFF1),
				
		new Node("reset_all", 0xAD0B00, 0x00000000, 0x0000, 0,
				Node.TYPE_BUFF1),
										
//		new Node("orientation_selection", 0xAD0C, 0x55010000, 0x1000, 0,
//				Node.TYPE_BUFF1),
				
//		new Node("eq_mode", 0x5A04, 0x55010000, 0x0800, 0,
//				Node.TYPE_BUFF1),

//		new Node("wide_angle_view", 0xF208, 0xE8010000, 0x01, 0,
//			Node.TYPE_BUFF1),
//		
//		new Node("depression_view", 0xF209, 0xE8010000, 0x02, 0,
//			Node.TYPE_BUFF1),
//原车视频状态设置
		new Node("video_view_switching", 0xF208, 0xE8010000, 0x0800, 0,
						Node.TYPE_BUFF1),		
		};

		private final static int[] INIT_CMDS = {
	0x61,0x67,0xa6,0xe8,0x8a
	};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.ford_hiworld_settings);
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

				byte[] buf = new byte[] { 0x03, (byte) 0x6a, 0x5, 1,
						(byte) (msg.what & 0xff) };
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);
			}
		}
	};

	private void sendCanboxData(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
	}
	
	private void sendCanboxData4(int cmd, int value) {
		sendCanboxInfo4(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);
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
					if(key.equals("atmosphere_light_color"))
					{
						sendCanboxData4(NODES[i].mCmd, Integer.parseInt((String) newValue));
					}
					else
					{
						sendCanboxData(NODES[i].mCmd, Integer.parseInt((String) newValue));
					}
				} 
				else if (preference instanceof SwitchPreference) 
				{
					sendCanboxData(NODES[i].mCmd, ((Boolean) newValue) ? 0x1 : 0x0);
				} else if(preference instanceof PreferenceScreen) {
					sendCanboxData(NODES[i].mCmd);
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
	
	private void sendCanboxInfo4(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x04, (byte) d0, (byte) d1, (byte) d2, 0x00, 0x00 };
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
