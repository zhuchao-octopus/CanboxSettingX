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

public class FiatEGEARaiseSettingFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HondaSettingsSimpleFragment";

	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {

		new Node("fulecons", 0x80, 0x40, 0x1, 0x0),
		new Node("range", 0x80, 0x40, 0x2, 0x0),
		new Node("temperature", 0x80, 0x40, 0x3, 0x0),
		new Node("metric", 0x80, 0x40, 0x4, 0x0),
		new Node("running_lights", 0x80, 0x40, 0x10, 0x0),
		new Node("turn_lights_set", 0x80, 0x40, 0x11, 0x0),
		new Node("headlights_off", 0x80, 0x40, 0x12, 0x0),
		new Node("lights_flash", 0x80, 0x40, 0x13, 0x0),
		new Node("front_light", 0x80, 0x40, 0x14, 0x0),
		new Node("driving_auto", 0x80, 0x40, 0x20, 0x0),
		new Node("parksense", 0x80, 0x40, 0x30, 0x0),
		new Node("b_parksense", 0x80, 0x40, 0x31, 0x0),
		new Node("brake_control", 0x80, 0x40, 0x32, 0x0),
		new Node("brake_control_sensitivity", 0x80, 0x40, 0x33, 0x0),
		new Node("rear_view_camera_delay", 0x80, 0x40, 0x34, 0x0),
		new Node("parkView", 0x80, 0x40, 0x35, 0x0),
		new Node("wipers_induction", 0x80, 0x40, 0x36, 0x0),
		new Node("dis_trip_b", 0x80, 0x40, 0xF0, 0x0),
		new Node("trip_b", 0x80, 0x40, 0xF1, 0x0),
		new Node("trip_a", 0x80, 0x40, 0xF2, 0x0),
		new Node("buzzer", 0x80, 0x40, 0xF3, 0x0),
		new Node("car_type", 0x80, 0x40, 0xF4, 0x0),

	};

	private final static int[] INIT_CMDS = { 0x40ff };

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.fiat_egea_raise_setting);

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

		if (mType == 1) {
			PreferenceScreen p = (PreferenceScreen) findPreference("driving_mode");
			if (p != null) {
				setPreferenceScreen(p);
			}
		}
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
				sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxData(int cmd, int value) {
		sendCanboxInfo(0x80, ((cmd & 0xff)), ((value & 0xff)));

	}

	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {

					sendCanboxData(NODES[i].mMask,
							Integer.parseInt((String) newValue));

				} else if (preference instanceof SwitchPreference) {
((SwitchPreference)preference).setChecked((Boolean) newValue);
					sendCanboxData(NODES[i].mMask, ((Boolean) newValue) ? 0x1
							: 0x0);

				}
				break;
			}
		}
	}

	private int mSetCTM = -1;

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {
		if (arg0.getKey().equals("individual_reset")) {
			sendCanboxInfo(0xc6, 0xd4, 0x01);
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
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
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
			value = ((buf[3] & 0xff) << 0);
		}
		if (buf.length > 4) {
			value |= ((buf[4] & 0xff) << 8);
		}
		if (buf.length > 5) {
			value |= ((buf[5] & 0xff) << 16);
		}

		return ((value & mask) >> start);
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

		if ((buf[0] & 0xff) == 0x40) {

			int value;

			for (int i = 0; i < NODES.length; ++i) {

				if ((buf[2] & 0xff) == NODES[i].mMask) {
					value = buf[3];
					setPreference(NODES[i].mKey, value);
					// break;
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
