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

public class Set213 extends PreferenceFragment
		implements Preference.OnPreferenceChangeListener,
		OnPreferenceClickListener {
	private static final String TAG = "KadjarRaiseSettingFragment";

	private static final Node[] NODES = {

		new Node("top_light_delayed_time", 0x8200, 0x400, 0xff, 3),
		new Node("go_home_delayed_time", 0x8201, 0x401, 0xff, 3),
		new Node("economize_on_electricity", 0x8202, 0x402, 0xff, 3),
		new Node("rain_light_sensor_settings", 0x8203, 0x403, 0x1, 1),
		new Node("headlamp_brightness_setting", 0x8204, 0x403, 0x2, 1),
		

		new Node("antitheft_prevention_key", 0x8205, 0x404, 0x3),
		new Node("parking_setting_key", 0x8206, 0x404, 0x4),
		new Node("automatic_folding_of_rearview_mirror_key", 0x8207, 0x404, 0x8),
		new Node("gate_setting_key", 0x8208, 0x404, 0x10),
		new Node("seat_memory_key", 0x8209, 0x404, 0x20),
		new Node("electric_sidestepping_system_key", 0x820b, 0x405, 0x1),
		new Node("roof_mode_key", 0x820c, 0x405, 0x2),
		new Node("full_terrain_key", 0x820d, 0x405, 0x4),
		


		
		

		new Node("driving_buttocks_massage", 0xe011, 0x1301, 0xf),
		new Node("copilot_buttocks_massage", 0xe012, 0x1301, 0xf0),


		new Node("driving_waist_massage", 0xe013, 0x1303, 0xf),
		new Node("side_drive_massage", 0xe014, 0x1303, 0xf0),

	};

	private final static int[] INIT_CMDS = { 0x8313,0x8304 };

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.changchengh9_settings_bnr);

		for (int i = 0; i < NODES.length; ++i) {
			mPreferences[i] = findPreference(NODES[i].mKey);
			// Log.d("aa", mPreferences[i]+":"+NODES[i].mKey);
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

	private void requestInitData() {
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], i * 100);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxData2(msg.what, 0);
			}
		}
	};

	private void sendCanboxData2(int cmd, int value) {

		byte[] buf = new byte[] { (byte) ((cmd & 0xff00) >> 8), 0x02,
				(byte) (cmd & 0xff), (byte) value};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}
	
	private void sendCanboxData(int cmd, int value) {

		byte[] buf = new byte[] { (byte) ((cmd & 0xff00) >> 8), 0x03,
				(byte) (cmd & 0xff), (byte) value, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private void sendCanboxData(int cmd) {

		byte[] buf = new byte[] { (byte) ((cmd & 0xff0000) >> 16), 0x02,
				(byte) ((cmd & 0xff00) >> 8), (byte) (cmd & 0xff) };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {

					int value = Integer.parseInt((String) newValue);
//					if ((NODES[i].mShow & 1) != 0) {
//						value++;
//					}
					sendCanboxData(NODES[i].mCmd, value);

				} else if (preference instanceof SwitchPreference) {
					if ((NODES[i].mShow & 1) != 0) {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x2 : 0x1);
					} else {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x1 : 0x0);
					}
				} else if (preference instanceof PreferenceScreen) {
					if ((NODES[i].mCmd & 0xff00)>>8 == 0xe0){
						byte[] buf = new byte[] { (byte) ((NODES[i].mCmd & 0xff00) >> 8), 0x02,
								 (byte) (NODES[i].mCmd & 0xff), 1 };
						BroadcastUtil.sendCanboxInfo(getActivity(), buf);
						Util.doSleep(200);
						buf[3] = 0;
						BroadcastUtil.sendCanboxInfo(getActivity(), buf);
					} else {
						sendCanboxData(NODES[i].mCmd);
					}
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
			}else if (p instanceof PreferenceScreen) {
				p.setSummary(""+index);
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

		try {
			int cmd;
			int param;
			int mask;
			int value;

			for (int i = 0; i < NODES.length; ++i) {
				cmd = (NODES[i].mStatus & 0xff00) >> 8;
				param = (NODES[i].mStatus & 0xff);

				if ((buf[0] & 0xff) == cmd) {
					if (param < (buf.length - 2)) {
						mask = (NODES[i].mMask);
						value = getStatusValue1(buf[param + 2], mask);
//						if ((NODES[i].mShow & 0x2) != 0) {
//							value--;
//						}
						setPreference(NODES[i].mKey, value);
					}
					// break;
				}

			}
		} catch (Exception e) {
			Log.d(TAG, "err" + e);
		}
	}

	private void showPreference(String id, int show) {
		Preference preference = null;

		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(id)) {
				preference = mPreferences[i];
				break;
			}
		}

		if (preference != null) {
			if (show != 0) {
				if (findPreference(id) == null) {
					getPreferenceScreen().addPreference(preference);
				}
			} else {
				if (findPreference(id) != null) {
					getPreferenceScreen().removePreference(preference);
				}
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
								Log.d("aa", "!!!!!!!!" + buf);
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
