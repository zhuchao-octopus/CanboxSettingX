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
import com.common.view.MyPreferenceSeekBar;

public class PsaSettingHiworldFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "PsaSettingHiworldFragment";
	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {
			// 灯光设置
			new Node("str_adaptive_front_lighting", 0x7D02, 0x79000000, 0x40,
					0, Node.TYPE_BUFF1),

			new Node("str_daytime_running_lamp", 0x7B05, 0x76000001, 0x04, 0,
					Node.TYPE_BUFF1),

			new Node("str_atmosphere_lighting", 0x7B0A, 0x76000000, 0x8, 0,
					Node.TYPE_BUFF1),

			new Node("theme_color", 0x7D09, 0x79000000, 0x1, 0, Node.TYPE_BUFF1),

			new Node("str_go_home", 0x7B06, 0x76000001, 0x3, 0, Node.TYPE_BUFF1),

			new Node("str_welcome_lighting", 0x7B09, 0x76000000, 0x30, 0,
					Node.TYPE_BUFF1),

			new Node("atmosphere_model", 0x7D0A, 0x79000001, 0xc0, 0,
					Node.TYPE_BUFF1),

			// 车锁设置
			new Node("str_auto_lock", 0x7B0C, 0x76000001, 0x20, 0,
					Node.TYPE_BUFF1),

			new Node("str_the_door_locked", 0x7B0D, 0x76000001, 0x10, 0,
					Node.TYPE_BUFF1),

			new Node("only_set_the_trunk", 0x7D01, 0x79000000, 0x80, 0,
					Node.TYPE_BUFF1),

			new Node("str_remote_unlock_style", 0x7B04, 0x76000001, 0x8,
					0, Node.TYPE_BUFF1),

			// 车声设置
			new Node("psa_parking_assistance", 0x7B02, 0x76000001, 0x40, 0,
					Node.TYPE_BUFF1),

			new Node("str_rear_wiper", 0x7B01, 0x76000001, 0x80, 0,
					Node.TYPE_BUFF1),

			new Node("str_radar_inactive", 0x7B0B, 0x76000000, 0x40, 0,
					Node.TYPE_BUFF1),

			new Node("oil_unit", 0xCA05, 0xc1000001, 0x6, 0, Node.TYPE_BUFF1),

			new Node("str_auto_start_stop", 0x8C01, 0x85000001, 0x80, 0,
					Node.TYPE_BUFF1),

			new Node("welcome_cmd", 0x7D05, 0x79000000, 0x10, 0,
					Node.TYPE_BUFF1),

			new Node("tpms_calibration", 0x7D03, 0x62010000, 0x300000, 0,
					Node.TYPE_BUFF1),

			new Node("str_auto_hold", 0x7B08, 0x76000000, 0x80, 0,
					Node.TYPE_BUFF1),

			new Node("str_lane_changing_assistant", 0x7D04, 0x79000000, 0x20,
					0, Node.TYPE_BUFF1),

			new Node("str_temperature_unit", 0xCA03, 0xc1000001, 0x20, 0,
					Node.TYPE_BUFF1),

			new Node("fatigue_detection_system", 0x7D07, 0x79000000, 0x04, 0,
					Node.TYPE_BUFF1),

			new Node("lane_keeping_auxiliary", 0x7D06, 0x79000000, 0x8, 0,
					Node.TYPE_BUFF1),

			new Node("speed_limit_prompt", 0x7D08, 0x79000000, 0x2, 0,
					Node.TYPE_BUFF1),

			new Node("psa_driving_mode", 0x7D0B, 0x79000001, 0x20, 0,
					Node.TYPE_BUFF1),

			new Node("ion_purifier", 0x7D0C, 0x79000001, 0x18, 0,
					Node.TYPE_BUFF1),

			new Node("smoked_type", 0x7D0D, 0x79000001, 0x6, 0,
					Node.TYPE_BUFF1),

			new Node("fumigating_concentration", 0x7D0E, 0x79000002, 0xC0, 0,
					Node.TYPE_BUFF1),

			new Node("nitialization_mode", 0x7D0F, 0x9C010000, 0xFF00, 0,
					Node.TYPE_BUFF1),
			// 功放设置
			new Node("sound_distribution", 0xAD0B, 0xffffffff, 0, 0,
					Node.TYPE_BUFF1),

			new Node("loudness", 0xAD0C, 0xffffffff, 0, 0,
					Node.TYPE_BUFF1),

			new Node("volume_as_fast", 0xAD0D, 0xffffffff, 0, 0,
					Node.TYPE_BUFF1),

			new Node("sound_selection", 0xAD0E, 0xffffffff, 0, 0,
					Node.TYPE_BUFF1), };
	private final static int[] INIT_CMDS = {
	/*
	 * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
	 * 0x4080, 0x4090,
	 */
	};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.psa_hiworld_setting);
		// findPreference("restore").setOnPreferenceClickListener(this);
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
				sendCanboxInfo(0x90, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxData3(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), value);
	}

	private void sendCanboxData(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), value);

	}

	private void sendCanboxData(int cmd) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),
				((cmd & 0xff) >> 0));
	}

	private void sendCanboxData2(int cmd, int value) {
		sendCanboxInfo(((cmd & 0xff0000) >> 16), ((cmd & 0xff00) >> 8),
				((cmd & 0xff) >> 0), value);
	}

	private void sendCanboxData(int cmd, int param1, int param2) {
		sendCanboxInfo(((cmd & 0xff00) >> 8), ((cmd & 0xff) >> 0), param1,
				param2);
	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		int buf = 0;
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					if ("oil_unit".equals(key)) {

						sendCanboxData(NODES[i].mCmd,
								Integer.parseInt((String) newValue) + 1);
					} else {
						sendCanboxData(NODES[i].mCmd,
								Integer.parseInt((String) newValue));
					}
					
					if (NODES[i].mStatus == 0xffffffff) {
						setPreference(NODES[i].mKey,
								Integer.parseInt((String) newValue));
					}  
				} else if (preference instanceof SwitchPreference) {
					if ("str_atmosphere_lighting".equals(key)) {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x80 : 0x0);
					} else {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x1 : 0x0);
					}
				} else if (preference instanceof PreferenceScreen) {

					sendCanboxData(NODES[i].mCmd, 0x1);

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
		byte[] buf = new byte[] { 0x03, (byte) d0, (byte) d1, (byte) d2,
				(byte) d3 };
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
			} else if (p instanceof MyPreferenceSeekBar) {
				p.setSummary(index + "");
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
		int subpostion;
		int subCmd;
		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff000000) >> 24;
			subCmd = (NODES[i].mStatus & 0xff0000) >> 16;
			subpostion = (NODES[i].mStatus & 0xff);
			if (subCmd != 0) {

			} else if ((cmd&0xff) == (buf[0] & 0xff)) {

				mask = (NODES[i].mMask);
				value = getStatusValue1(buf[subpostion + 2], mask);
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
								Log.d("abcd",
										"!!!!!!!!" + Util.byte2HexStr(buf));
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
