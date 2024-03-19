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

public class DongFengFengShenAX7 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "DongFengFengShenAX7SettingsSimpleFragment";

	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {
//data0
		new Node("str_go_home", 0x8901, 0x29010000, 0xc0, 0,
				Node.TYPE_BUFF8),
				
		new Node("driving_auto", 0x8902, 0x29010000, 0x20, 0,
				Node.TYPE_BUFF8),
				
		new Node("automatic_emergency_braking", 0x8903, 0x29010000, 0x10, 0,
				Node.TYPE_BUFF8),
		
		new Node("forward_collision_waring", 0x8904, 0x29010000, 0x08, 0,
				Node.TYPE_BUFF8),
		
		new Node("slope_slow_start", 0x8905, 0x29010000, 0x04, 0,
				Node.TYPE_BUFF8),
		
		new Node("blind_spot_detector", 0x8906, 0x29010000, 0x02, 0,
				Node.TYPE_BUFF8),
				
		new Node("automatically_close_the_window_when_rains", 0x8907, 0x29010000, 0x01, 0,
				Node.TYPE_BUFF8), 

				
//data1
		new Node("automatically_lock_after_leaving_the_car", 0x8908, 0x29010000, 0x8000, 0,
				Node.TYPE_BUFF8), 
		
		new Node("pm2_5_purification_function", 0x8909, 0x29010000, 0x4000, 0,
				Node.TYPE_BUFF8), 
		
		new Node("side_view_image_assist", 0x890B, 0x29010000, 0x2000, 0,
				Node.TYPE_BUFF8), 
		
		new Node("voice_control_sunroof", 0x890A, 0x29010000, 0x1000, 0,
				Node.TYPE_BUFF8), 
				
		new Node("system_settings_language", 0x890D, 0x29010000, 0x0800, 0,
				Node.TYPE_BUFF8), 

				
//data2						
		new Node("fast_cooling_mode", 0x8910, 0x29010000, 0x800000, 0,
				Node.TYPE_BUFF8), 
						
		new Node("one_touch_heating", 0x8911, 0x29010000, 0x400000, 0,
				Node.TYPE_BUFF8), 
		
		new Node("rime_mode", 0x8912, 0x29010000, 0x200000, 0,
				Node.TYPE_BUFF8),
		
		new Node("preset_parking_mode", 0x8913, 0x29010000, 0x100000, 0,
				Node.TYPE_BUFF8),
	
		new Node("take_care_of_your_baby", 0x8914, 0x29010000, 0x080000, 0,
				Node.TYPE_BUFF8),
		
		new Node("smoking_mode", 0x8915, 0x29010000, 0x040000, 0,
				Node.TYPE_BUFF8),
		
		new Node("rain_and_snow_mode", 0x8916, 0x29010000, 0x020000, 0,
				Node.TYPE_BUFF8),

//data3
		new Node("personalized_meter_settings_left", 0x890E, 0x29010000, 0xF0000000, 0,
						Node.TYPE_BUFF8), 
						
		new Node("personalized_meter_settings_right", 0x890F, 0x29010000, 0x0F000000, 0,
						Node.TYPE_BUFF8), 		

						
//data4
		new Node("instrument_brightness", 0x8917, 0x29020000, 0xE0, 0,
				Node.TYPE_BUFF8),
		
		new Node("ambient_light_switch", 0x8918, 0x29020000, 0x10, 0,
				Node.TYPE_BUFF8),
	
		new Node("atmosphere_light_color", 0x8919, 0x29020000, 0x0F, 0,
				Node.TYPE_BUFF8),	

//data5
		new Node("ambient_light_brightness", 0x891A, 0x29020000, 0xF000, 0,
				Node.TYPE_BUFF8),	
	
				
//data6
		new Node("electric_tailgate_function_switch", 0x891B, 0x29020000, 0x800000, 0,
				Node.TYPE_BUFF8),	
								
		new Node("automatic_tail_lock_when_electric_tailgate_closed", 0x891C, 0x29020000, 0x400000, 0,
				Node.TYPE_BUFF8),
		
		new Node("kick_induction_electric_tailgate", 0x891D, 0x29020000, 0x200000, 0,
				Node.TYPE_BUFF8),
		
		new Node("delayed_closing_time_of_electric_tailgate", 0x891E, 0x29020000, 0x1C0000, 0,
				Node.TYPE_BUFF8),
		};

		private final static int[] INIT_CMDS = { 0x89,
	/*
	 * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
	 * 0x4080, 0x4090,
	 */
	};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.dongfengfengshenax7);

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
//		if (mType == 1) {
//			PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
//			if (p != null) {
//				setPreferenceScreen(p);
//			}
//		}
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
				sendCanboxInfo(0x90,msg.what & 0xff);
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

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					sendCanboxData(NODES[i].mCmd,
							Integer.parseInt((String) newValue));
				} else if (preference instanceof SwitchPreference) {
					sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x1 : 0x0);
				} else if (preference instanceof PreferenceScreen) {
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
		byte[] buf = new byte[] { (byte) d0, 0x01, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, int index) {
		Preference p = findPreference(key);
		if (p != null) {
			if (p instanceof ListPreference) {
				if (key.equals("personalized_meter_settings_left") || key.equals("personalized_meter_settings_right"))
				{
					ListPreference lp = (ListPreference) p;CharSequence []ss = lp.getEntries();
					if (ss != null && (ss.length > (index - 1))) {
						lp.setValue(String.valueOf(index - 1));
					}
					lp.setSummary("%s");
				}
				else 
				{
					ListPreference lp = (ListPreference) p;
					CharSequence []ss = lp.getEntries();
					if (ss != null && (ss.length > index)) {
						lp.setValue(String.valueOf(index));
					}
					lp.setSummary("%s");
				}
				// Log.d("aa", key+":"+((ListPreference)
				// findPreference(key)).getEntry());
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			} 
		}
	}

	private int getStatusValue(byte[] buf, int mask) {

		int value = 0;
		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}
		value = 0;
		if (buf.length > 3) {
			value = ((buf[2] & 0xff) << 0);
		}
		if (buf.length > 4) {
			value |= ((buf[3] & 0xff) << 8);
		}
		// } catch (Exception e) {
		// value = 0;
		// }

		return ((value & mask) >> start);
	}
	
	private int getStatusValue(byte[] buf, int mask,int index) {
		int value = 0;
		int value1 = 0;
		int start = 0;
		int i;
		for (i = 0; i < 32; i++) {
			if ((mask & (0x1 << i)) != 0) {
				start = i;
				break;
			}
		}
		value = 0;
		if (buf.length > 3) {
			value = ((buf[2] & 0xff) << 0);
		}
		if (buf.length > 4) {
			value |= ((buf[3] & 0xff) << 8);
		}
		
		if(buf.length > 5)
		{
			value |= ((buf[4] & 0xff) << 16);
		}
		
		if(buf.length > 6)
		{
			value |= ((buf[5] & 0xff) << 24);
		}
		
		if(buf.length > 7)
		{
			value1 =  ((buf[6] & 0xff) << 0);
		}
		
		if(buf.length > 8)
		{
			value1 |=  ((buf[7] & 0xff) << 8);
		}
		
		if(buf.length > 9)
		{
			value1 |=  ((buf[8] & 0xff) << 16);
		}
		
		if(buf.length > 10)
		{
			value1 |=  ((buf[9] & 0xff) << 24);
		}
		if(index == 1)
		{
			return ((value & mask) >> start);
		}
		else
		{
			return ((value1 & mask) >> start);
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
	
	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}
	private void updateView(byte[] buf) {
		int cmd;
		int Subparam;
		int mask;
		int value;

		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff000000) >> 24;
			Subparam = (NODES[i].mStatus & 0xff0000) >> 16;

			if(cmd == (buf[0] & 0xff))	
			{
				mask = (NODES[i].mMask);
				value = getStatusValue(buf, mask, Subparam);
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
