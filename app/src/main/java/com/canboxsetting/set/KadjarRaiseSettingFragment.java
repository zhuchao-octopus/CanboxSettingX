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

public class KadjarRaiseSettingFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "KadjarRaiseSettingFragment";

	private static final Node[] NODES = {

		new Node("front_parking", 0x8301, 0x7100, 0x2),		
		new Node("back_parking", 0x8302, 0x7100, 0x1),

		new Node("air_loop", 0x8303, 0x7101, 0x8),
		new Node("car_start", 0x8305, 0x7101, 0x4),
		new Node("ion_generator", 0x8304, 0x7101, 0x3),
		
		

		new Node("indicator", 0x830c, 0x7102, 0x80),
		new Node("wiper_back", 0x830b, 0x7102, 0x40),
		new Node("auto_cabin", 0x830a, 0x7102, 0x20),
		new Node("external_welcome", 0x8309, 0x7102, 0x10),
		
		

		new Node("door_aut", 0x8306, 0x7102, 0x4),
		new Node("prompt_volume", 0x8307, 0x7102, 0x3),
		

		new Node("user_reset", 0x838002, 0, 0),

		new Node("langauage", 0x830d, 0x7103, 0xff),
		new Node("inside_light", 0x8308, 0x7102, 0x8),


		new Node("parking_system", 0x8313, 0x7100, 0x30),
		new Node("lateral_parking", 0x8312, 0x7100, 0x4),
		new Node("blind_zone", 0x8311, 0x7100, 0x8),
		


		new Node("instrument_color", 0x8310, 0x7104, 0xff),
		new Node("instrument_style", 0x830e, 0x7105, 0xff),
		new Node("instrument_light", 0x830f, 0x7106, 0xff),
	};

	private final static int[] INIT_CMDS = {0x9071};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.kadjar_raise_settings);

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
				sendCanboxData(msg.what, 0);
			}
		}
	};

	private void sendCanboxData(int cmd, int value) {		

		byte[] buf = new byte[] { (byte) ((cmd & 0xff00) >> 8), 0x02,
				(byte)(cmd & 0xff), (byte)value };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private void sendCanboxData(int cmd) {

		byte[] buf = new byte[] { (byte) ((cmd & 0xff0000) >> 16), 0x02,
				(byte) ((cmd & 0xff00) >> 8), (byte)(cmd & 0xff) };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);


	}


	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				if (preference instanceof ListPreference) {
					sendCanboxData(NODES[i].mCmd,
							Integer.parseInt((String) newValue));
						if("instrument_color".equals(key)
								|| "langauage".equals(key)){
							
							
							ListPreference lp = (ListPreference) preference;
							lp.setValue((String)newValue);
							lp.setSummary("%s");
							
						}
					
				} else if (preference instanceof SwitchPreference) {
					if (NODES[i].mType == Node.TYPE_CUSTOM) {
						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
					} else {
						sendCanboxData(NODES[i].mCmd,
								((Boolean) newValue) ? 0x1 : 0x0);
					}
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

				if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
					if ((buf[0] & 0xff) == cmd) {
						if (param < (buf.length - 2)) {
							mask = (NODES[i].mMask);
							value = getStatusValue1(buf[param + 2], mask);
							setPreference(NODES[i].mKey, value);
						}
						// break;
					}
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
