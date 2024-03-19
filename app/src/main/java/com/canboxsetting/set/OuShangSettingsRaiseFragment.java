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

public class OuShangSettingsRaiseFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HondaSettingsSimpleFragment";

	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {

	new Node("default_all", 0x83, 0x52, 0x00),
	new Node("rear_view", 0x83, 0x52, 0x01),
	new Node("wipers", 0x83, 0x52, 0x02),
	new Node("remote_unlock", 0x83, 0x52, 0x03),
	new Node("driving_auto", 0x83, 0x52, 0x04),
	new Node("oushang_1", 0x83, 0x52, 0x05),
	new Node("oushang_2", 0x83, 0x52, 0x06),
	new Node("oushang_3", 0x83, 0x52, 0x07),
	new Node("oushang_4", 0x83, 0x52, 0x08),
	new Node("oushang_5", 0x83, 0x52, 0x09),
	new Node("headlight_delay", 0x83, 0x52, 0x0a),
	new Node("turn_signal", 0x83, 0x52, 0x0b),
	new Node("oushang_6", 0x83, 0x52, 0x0d),
	new Node("information_tone", 0x83, 0x52, 0x0e),
	new Node("warning_tone", 0x83, 0x52, 0x0f),
	new Node("parkunlock", 0x83, 0x52, 0x10),
	new Node("oushang_7", 0x83, 0x52, 0x11),
	new Node("oushang_8", 0x83, 0x52, 0x12),
	new Node("oushang_9", 0x83, 0x52, 0x13),
	new Node("oushang_10", 0x83, 0x52, 0x14),
	new Node("oushang_11", 0x83, 0x52, 0x15),
	new Node("oushang_12", 0x83, 0x52, 0x16),
	

	new Node("oushang_17", 0x83, 0x52, 0x17),
	new Node("oushang_18", 0x83, 0x52, 0x18),
	new Node("oushang_19", 0x83, 0x52, 0x19),
	new Node("oushang_1a", 0x83, 0x52, 0x1a),
	new Node("oushang_1b", 0x83, 0x52, 0x1b),
	new Node("oushang_1c", 0x83, 0x52, 0x1c),
	new Node("oushang_1d", 0x83, 0x52, 0x1d),
	new Node("oushang_1e", 0x83, 0x52, 0x1e),
	new Node("oushang_1f", 0x83, 0x52, 0x1f),
	new Node("oushang_20", 0x83, 0x52, 0x20),
	new Node("oushang_21", 0x83, 0x52, 0x21),
	new Node("oushang_22", 0x83, 0x52, 0x22),
	new Node("oushang_23", 0x83, 0x52, 0x23),
	new Node("oushang_24", 0x83, 0x52, 0x24),
	new Node("oushang_25", 0x83, 0x52, 0x25),
	new Node("oushang_26", 0x83, 0x52, 0x26),
	new Node("oushang_27", 0x83, 0x52, 0x27),
	new Node("oushang_28", 0x83, 0x52, 0x28),
	new Node("oushang_29", 0x83, 0x52, 0x29),
	new Node("oushang_2a", 0x83, 0x52, 0x2a),
	new Node("oushang_2b", 0x83, 0x52, 0x2b),
	new Node("oushang_2c", 0x83, 0x52, 0x2c),
	new Node("oushang_2d", 0x83, 0x52, 0x2d),
	new Node("oushang_2e", 0x83, 0x52, 0x2e),
	new Node("oushang_2f", 0x83, 0x52, 0x2f),
	new Node("oushang_30", 0x83, 0x52, 0x30),
	new Node("oushang_31", 0x83, 0x52, 0x31),
	new Node("oushang_32", 0x83, 0x52, 0x32),
	new Node("oushang_33", 0x83, 0x52, 0x33),
	new Node("oushang_34", 0x83, 0x52, 0x34),
	new Node("oushang_35", 0x83, 0x52, 0x35),
	new Node("oushang_36", 0x83, 0x52, 0x36),
	new Node("oushang_37", 0x83, 0x52, 0x37),
	new Node("oushang_38", 0x83, 0x52, 0x38),
	new Node("oushang_39", 0x83, 0x52, 0x39),
	new Node("oushang_3a", 0x83, 0x52, 0x3a),
	new Node("oushang_3b", 0x83, 0x52, 0x3b),
	new Node("oushang_3c", 0x83, 0x52, 0x3c),
	new Node("oushang_3d", 0x83, 0x52, 0x3d),
	new Node("oushang_3e", 0x83, 0x52, 0x3e),
	new Node("oushang_3f", 0x83, 0x52, 0x3f),
	new Node("oushang_40", 0x83, 0x52, 0x40),
	new Node("oushang_41", 0x83, 0x52, 0x41),
	new Node("oushang_42", 0x83, 0x52, 0x42),
	new Node("oushang_43", 0x83, 0x52, 0x43),
	new Node("oushang_44", 0x83, 0x52, 0x44),
	new Node("oushang_45", 0x83, 0x52, 0x45),
	new Node("oushang_46", 0x83, 0x52, 0x46),
	new Node("oushang_47", 0x83, 0x52, 0x47),
	new Node("oushang_48", 0x83, 0x52, 0x48),
	new Node("oushang_49", 0x83, 0x52, 0x49),
	

	};

	private final static int[] INIT_CMDS = { 0x5201, 0x5202, 0x5203, 0x5204,
			0x5205, 0x5206, 0x5207, 0x5208, 0x5209, 0x520a, 0x5201, 0x5201,
			0x5201, 0x5201, };

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.oushang_raise_settings);

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
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 50));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90((msg.what & 0xff00)>>8, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxData(int cmd, int mask, int value) {
		sendCanboxInfo(((cmd & 0xff)), ((mask & 0xff)), value);

	}

	private void sendCanboxData(int cmd, int mask) {
		sendCanboxInfo(cmd, mask, 1);

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

					sendCanboxData(NODES[i].mCmd, NODES[i].mMask,
							Integer.parseInt((String) newValue));

				} else if (preference instanceof SwitchPreference) {

					sendCanboxData(NODES[i].mCmd, NODES[i].mMask,
							((Boolean) newValue) ? 0x1 : 0x2);

				} else if (preference instanceof PreferenceScreen) {
					sendCanboxData(NODES[i].mCmd, NODES[i].mMask);
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

	private void sendCanboxInfo0x90(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x90, 0x02, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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

			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 1 ? true : false);
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

		int value;
		for (int i = 0; i < NODES.length; ++i) {

			if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
				if ((buf[0] & 0xff) == NODES[i].mStatus
						&& (buf[2] & 0xff) == NODES[i].mMask) {
					value = (buf[3] & 0xff);
					setPreference(NODES[i].mKey, value);
				}
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
