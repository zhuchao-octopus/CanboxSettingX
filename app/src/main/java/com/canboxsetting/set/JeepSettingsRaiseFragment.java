package com.canboxsetting.set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

public class JeepSettingsRaiseFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HondaSettingsSimpleFragment";

	private int mType = 0;

	public void setType(int t) {
		mType = t;
	}

	private static final Node[] NODES = {



		new Node("parksense", 0x9701, 0x07000000, 0xc0, 0x0),
		new Node("f_parksense", 0x9702, 0x07000000, 0x130, 0x0),
		new Node("b_parksense", 0x9703, 0x07000000, 0x10c, 0x0),
		new Node("parkView", 0x9705, 0x07000000, 0x1, 0x0),
		new Node("ramp", 0x9707, 0x07010000, 0x10040, 0x0),
		new Node("image_parkView", 0x9708, 0x07070000, 0x80, 0x0),
		new Node("brake_service", 0x970a, 0x07070000, 0x20, 0x0),
		new Node("parking_brake", 0x970b, 0x07070001, 0x10, 0x0),
		new Node("lane_warning", 0x970c, 0x07090000, 0x18, 0x0),
		new Node("deviation_correction", 0x970d, 0x07070000, 0x07, 0x0),
		new Node("busy_warning", 0x970e, 0x07010000, 0x30, 0x0),
		new Node("for_warning", 0x9710, 0x07080001, 0x20, 0x0),

		new Node("for_warning2", 0x9781, 0x07060000, 0x0c, 0x0),
		new Node("warning_active_braking", 0x9783, 0x07060001, 0x10, 0x0),
		new Node("rear_parkSense", 0x9784, 0x07090001, 0x04, 0x0),
		
		

		new Node("headlights_off", 0x9711, 0x07020000, 0x2c0, 0x0),
		new Node("bright_headlights", 0x9712, 0x07090000, 0x260, 0x0),
		new Node("wipers_start", 0x9713, 0x07020001, 0x08, 0x0),
		new Node("running_lights", 0x9714, 0x07020001, 0x04, 0x0),
		new Node("lights_flash", 0x9715, 0x07020001, 0x02, 0x0),
		new Node("outhigh_beam", 0x9716, 0x07080001, 0x10, 0x0),
		new Node("welcome_light", 0x9790, 0x070a0001, 0x04, 0x0),
		new Node("front_light", 0x9793, 0x070b0000, 0x18, 0x0),
		new Node("turn_lights_set", 0x9795, 0x070c0001, 0x08, 0x0),
		
		

		new Node("outlock", 0x9720, 0x07080001, 0x08, 0x0),
		new Node("beep_lock", 0x9723, 0x07030001, 0x10, 0x0),
		new Node("key_unlock", 0x9724, 0x07030001, 0x08, 0x0),
		new Node("keyless_entry", 0x9725, 0x07030001, 0x04, 0x0),
		new Node("personalise", 0x9726, 0x07030001, 0x02, 0x0),
		new Node("door_alarm", 0x9728, 0x07080001, 0x04, 0x0),
		new Node("remote_beep", 0x9729, 0x070c0001, 0x04, 0x0),
		new Node("power_alarm", 0x9788, 0x07040001, 0x08, 0x0),
		

		new Node("seat", 0x9731, 0x07040001, 0x80, 0x0),
		new Node("power_off", 0x9732, 0x07040000, 0x260, 0x0),
		new Node("delayed_extinguishing_when_door_closes", 0x97a1, 0x07040000, 0x206, 0x0),
		

		new Node("unit_set", 0x9752, 0x070a0000, 0x03, 0x0),
		new Node("fulecons", 0x9774, 0x070c0000, 0x60, 0x0),
		new Node("tireunit", 0x9771, 0x070a0000, 0x60, 0x0),
		new Node("range", 0x9773, 0x070c0000, 0x80, 0x0),
		new Node("temperature", 0x9772, 0x070a0000, 0x10, 0x0),
		
		

		new Node("backview", 0x9704, 0x07000001, 0x02, 0x0),
		new Node("wipers_induction", 0x9706, 0x07010001, 0x80, 0x0),
		new Node("rearview_dimming", 0x9751, 0x07060001, 0x80, 0x0),
		

		new Node("outseat_heating", 0x9754, 0x07080001, 0x03, 0x0),
		new Node("buzzer", 0x9760, 0x07090000, 0x01, 0x0),
		new Node("tire_pressure_assist", 0x9785, 0x07090000, 0x02, 0x0),
		new Node("auto_parking", 0x9762, 0x070d0000, 0x80, 0x0),
		new Node("auto_adjustment", 0x9741, 0x07050001, 0x80, 0x0),
		new Node("tire_mode", 0x9743, 0x07050001, 0x20, 0x0),
		new Node("transport_mode", 0x9744, 0x07050001, 0x10, 0x0),
		new Node("wheel_mode", 0x9745, 0x07050001, 0x08, 0x0),
		new Node("dis_suspension", 0x9742, 0x07050001, 0x40, 0x0),
		
		new Node("vehicle_identification_settings", 0xee60, 0x00000002, 0x0, 0x0),

		new Node("restore", 0, 0, 0, 0x0),
	};

	private final static int[] INIT_CMDS = { 0x07, };

	private Preference[] mPreferences = new Preference[NODES.length];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.jeep_raise_setting);

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

		// findPreference("speeddata").setOnPreferenceClickListener(this);
		// for (Node node : NODES) {
		// String s = node.mKey;
		// if (findPreference(s) != null) {
		// findPreference(s).setOnPreferenceChangeListener(this);
		// }
		// }

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
				sendCanboxInfo(0xf1, msg.what & 0xff);
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
				int value;
				int mask2 = ((NODES[i].mStatus & 0xff) >> 0);
				if (preference instanceof ListPreference) {
					
					
					if (key.equals("vehicle_identification_settings")) {
						((ListPreference) preference)
								.setValue((String) newValue);
						((ListPreference) preference)
								.setSummary(((ListPreference) preference)
										.getEntry());
					}
					value = Integer.parseInt((String) newValue);

					if (mask2 == 1) {
						value++;
					}

					sendCanboxData(NODES[i].mCmd, value);
				} else if (preference instanceof SwitchPreference) {
					if (NODES[i].mType == Node.TYPE_CUSTOM) {
						sendCanboxData(NODES[i].mCmd, NODES[i].mStatus);
					} else {
						
						
						if (mask2 == 1) {
							sendCanboxData(NODES[i].mCmd,
									((Boolean) newValue) ? 0x2 : 0x1);
						} else {

							sendCanboxData(NODES[i].mCmd,
									((Boolean) newValue) ? 0x1 : 0x0);
						}
					}

					if (key.equals("ctm_system")) {
						mSetCTM = (((Boolean) newValue) ? 0x1 : 0x0);
					}
					
					
				} else if (preference instanceof PreferenceScreen) {
					sendCanboxData(NODES[i].mCmd);
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
		String key = arg0.getKey();
		if ("restore".equals(key)) {

			Dialog d = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.confirmation_factory_settings)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									sendCanboxInfo(0x97, 0x55, 0x01);
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.show();

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
			} else if (p instanceof SwitchPreference) {
				SwitchPreference sp = (SwitchPreference) p;
				sp.setChecked(index == 0 ? false : true);
			}
		}
	}
	
	private void setPreference(String key, int index, int mask1) {
		Preference p = findPreference(key);
		if (p != null) {
			if (p instanceof ListPreference) {
				ListPreference lp = (ListPreference) p;				
				if (mask1 == 2){					
					lp.setValueIndex(index);
				} else {
					CharSequence []ss = lp.getEntries();
					if (ss != null && (ss.length > index)) {
						lp.setValue(String.valueOf(index));
					}
				}				
				lp.setSummary("%s");				
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

//		if ((buf[0] & 0xff) == 0xd0) {
//		} else 
			
			if ((buf[0] & 0xff) == 0x07) {

			int cmd;
			int mask;
			int mask1;
			int value;
			int param;

			for (int i = 0; i < NODES.length; ++i) {
				cmd = (NODES[i].mStatus & 0xff000000) >> 24;
				param = (NODES[i].mStatus & 0xff0000) >> 16;
				
				if (NODES[i].mType == Node.TYPE_DEF_BUFF) {
					if ((buf[0] & 0xff) == cmd) {
						mask = (NODES[i].mMask & 0xff);
						mask1 = ((NODES[i].mMask & 0xff00) >> 8);
						value = getStatusValue1(buf[2 + param], mask);
						if (mask1 == 1) {
							value--;
						}
						setPreference(NODES[i].mKey, value, mask1);
						// break;
					}
				} else if (NODES[i].mType == Node.TYPE_BUFF1) {
					if ((buf[0] & 0xff) == cmd) {
						mask = (NODES[i].mMask);
						value = getStatusValue1(buf[6], mask);
						setPreference(NODES[i].mKey, value);
						// break;
					}
				} else if (NODES[i].mType == Node.TYPE_BUFF1_INDEX) {
					if ((buf[0] & 0xff) == cmd) {
						mask = (NODES[i].mMask & 0xff);
						int index = ((NODES[i].mMask & 0xff00) >> 8);
						value = getStatusValue1(buf[2 + index], mask);
						setPreference(NODES[i].mKey, value);
						// break;
					}
				} else if (NODES[i].mType == Node.TYPE_DEFINE1) {

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
