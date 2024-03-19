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
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceEdit.IButtonCallBack;

public class Set228 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

	new NodePreference("automatic_lock_msg", 0xc600, 0x3800, 0x80, 0),
			new NodePreference("lock_signal", 0xc602, 0x3800, 0x08, 0),
			new NodePreference("remindkey", 0xc604, 0x3800, 0x04, 0),
			new NodePreference("deicer", 0xc607, 0x3801, 0x40, 0),
			new NodePreference("rear_window_defog", 0xc606, 0x3801, 0x80, 0),

			new NodePreference("autolock_period", 0xc601, 0x3800, 0x70),
			new NodePreference("lightingime", 0xc605, 0x3800, 0x03),

	};

	private final static int[] INIT_CMDS = { 0x38

	};

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.subaru_luzheng);

		init();

		((MyPreferenceEdit) findPreference("autolock_period"))
				.setCallback(mButtonCallBack);
		((MyPreferenceEdit) findPreference("lightingime"))
				.setCallback(mButtonCallBack);

	}

	private IButtonCallBack mButtonCallBack = new IButtonCallBack() {
		public void callback(String key, boolean add) {
			if ("autolock_period".equals(key)) {
				if (add) {
					sendCanboxInfo(0xc6, 0x1, 1);
				} else {
					sendCanboxInfo(0xc6, 0x1, 0);
				}
			} else if ("lightingime".equals(key)) {
				if (add) {
					sendCanboxInfo(0xc6, 0x5, 1);
				} else {
					sendCanboxInfo(0xc6, 0x5, 0);
				}
			}

		};
	};

	private void init() {

		for (int i = 0; i < NODES.length; ++i) {
			Preference p = NODES[i].createPreference(getActivity());
			if (p != null) {

				Preference ps = getPreferenceScreen();
				if (ps instanceof PreferenceScreen) {
					boolean add = true;
					// if (((NODES[i].mType & 0xff0000) >> 16) != 0) {
					// int index = ((NODES[i].mType & 0xff00) >> 8) + 2;
					// if ((mVisible[index] & NODES[i].mType) == 0) {
					// add = false;
					// }
					// }

					

					if ((p instanceof ListPreference)
							|| (p instanceof SwitchPreference)) {
						((PreferenceScreen) ps).addPreference(p);
						p.setOnPreferenceChangeListener(this);
					} else {
						// p.setOnPreferenceClickListener(this);
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
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {

				byte[] buf = new byte[] { (byte) 0x90, (byte) 2,
						(byte) (msg.what & 0xff), 0 };
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);

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
					// if (NODES[i].mType == Node.TYPE_CUSTOM) {
					// sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
					// } else {
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff, ((Boolean) newValue) ? 0
									: 0x0);
					// }
				}
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
		String key = arg0.getKey();
		for (int i = 0; i < NODES.length; ++i) {
			if (NODES[i].mKey.equals(key)) {
				int v = 1;
				if (key.equals("autolock_period")) {

				} else if (key.equals("lightingime")) {

				}
				sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
						NODES[i].mCmd & 0xff, v);

			}
		}
		return false;
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {

		byte[] buf = new byte[] { (byte) d0, 0x2, (byte) d1, (byte) d2 };
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
			} else {
				String sum = "";
				if (key.equals("autolock_period")) {
					switch (index) {
					case 0:
						sum = "20s";
						break;
					case 1:
						sum = "30s";
						break;
					case 2:
						sum = "40s";
						break;
					case 3:
						sum = "50s";
						break;
					case 4:
						sum = "60s";
						break;
					}
				} else if (key.equals("lightingime")) {
					switch (index) {
					case 0:
						sum = "0s";
						break;
					case 1:
						sum = getString(R.string.str_short);
						break;
					case 2:
						sum = getString(R.string.str_normal);
						break;
					case 3:
						sum = getString(R.string.str_long);
						break;
					}
				}
				p.setSummary(sum);
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
		int value;
		// if (buf[0] == 0x57) {
		//
		//
		// } else {
		for (int i = 0; i < NODES.length; ++i) {

			if ((NODES[i].mType & NodePreference.PREFERENCE_MASK) == NodePreference.SCREENPREFERENCE) {
				cmd = (NODES[i].mStatus & 0xff00) >> 8;
				index = (NODES[i].mStatus & 0xff);
				mask = NODES[i].mType & 0xff;
			} else {
				cmd = (NODES[i].mStatus & 0xff00) >> 8;
				index = (NODES[i].mStatus & 0xff);
				mask = NODES[i].mMask;
			}

			if ((buf[0] & 0xff) == cmd) {
				value = getStatusValue1(buf[2 + index], mask);
				setPreference(NODES[i].mKey, value);
			}

		}

		// }
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
