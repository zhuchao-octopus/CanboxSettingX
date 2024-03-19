package com.canboxsetting.set;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.NodePreference;
import com.common.util.Util;
import com.common.view.MyPreferenceSeekBar;

public class Set147 extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {
										

	new NodePreference("ambient_light_color", 0x6f01, 0x6100, 0xe0, 0,
			R.array.atmosphere_light_color_entries2, R.array.eight_values),
	
							

	new NodePreference("ambient_light_brightness", 0x6f02, 0x6100, 0x1f, 0,
		R.array.instrument_light_value, R.array.thirty_values),

		new NodePreference("ambient_light_meter", 0x6f03, 0x6101, 0x80, 0),
		new NodePreference("renual01", 0x6f04, 0x6101, 0x40, 0),
		new NodePreference("renual02", 0x6f05, 0x6101, 0x20, 0),
		new NodePreference("ambient_light_switch", 0x6f06, 0x6101, 0x10, 0),

			new NodePreference("renual03", 0x6f07, 0x6102,
					0xe0, 0, R.array.multi_sense_entries,
					R.array.five_values),
			new NodePreference("renual04", 0x6f08, 0x6102,
					0x18, 0, R.array.multi_sense_display_style_entries,
					R.array.four_values),
			new NodePreference("renual05", 0x6f09, 0x6102,
					0x7, 0, R.array.multi_sense_steering_entries,
					R.array.three_values),
			new NodePreference("renual06", 0x6f0a, 0x6103,
					0xe0, 0, R.array.multi_sense_powertrain_entries,
					R.array.three_values),
			new NodePreference("renual07", 0x6f0b, 0x6103,
					0x1c, 0, R.array.multi_sense_climate_entries,
					R.array.enautomatic_latch_vaues_5),
		
					

			new NodePreference("seat_massage", 0x6f0c, 0x6105, 0x80, 0),
			new NodePreference("seat_massage_mode", 0x6f0d, 0x6104, 0xc0, 0,
					R.array.hiworld_renault_seat_message_mode,
					R.array.three_values),
			new NodePreference("seat_massage_strength", 0x6f0e, 0x6104, 0x38, 0,
					R.array.five_high_values,
					R.array.five_high_values),
			new NodePreference("seat_massage_speed", 0x6f0f, 0x6104, 0x7, 0,
					R.array.five_high_values,
					R.array.five_high_values),
			new NodePreference("fatigue_detection_system", 0x6f10, 0x6106, 0x80, 0),
			
			

			new NodePreference("internal_welcome_voice", 0x6f11, 0x6106, 0x40, 0),
			new NodePreference("gac_settings_auto_door_locks", 0x6f12, 0x6106, 0x20, 0),

			new NodePreference("renual0e", 0x6f13, 0x6106, 0x10, 0),
			
			new NodePreference("adjust_the_prompt_volume", 0x6f14, 0x6106, 0xc, 0,
					R.array.new_tourui_three_values, R.array.three_values),

			new NodePreference("external_welcome", 0x6f15, 0x6106,
					0x2, 0),
			new NodePreference("auto_lights_off", 0x6f16, 0x6106,
					0x1, 0),
//			
			new NodePreference("driving_style_tips", 0x6f17, 0x6107, 0x80, 0),

			new NodePreference("dashboard_brightness", 0x6f18, 0x6107, 0x1f, 0,
					R.array.instrument_light_value, R.array.thirty_values),
									
			new NodePreference("reduction_of_fetal_pressure", 0x6f19, 0x6101, 0x80, 0),
			new NodePreference("renual00", 0x6f1a, 0x6108, 0x80, 0),

			new NodePreference("parking_sound_type", 0x6f1b, 0x6108,
					0x60, 0, R.array.parking_sound_type_entries,
					R.array.three_values),
//
			new NodePreference("parking_volume2", 0x6f1c, 0x6108,
					0x1f, 0, R.array.renual_1c,
					R.array.renual_1c),

			new NodePreference("rear_parking_sensor", 0x6f1d, 0x6109, 0x80, 0),
			new NodePreference("rear_view_image_switch", 0x6f1e, 0x6109, 0x40, 0),
			new NodePreference("reset_factory", 0x6f1f, 0x6101, 0x80, 0),

			new NodePreference("renual08", 0x6f20, 0x6109,
					0x30, 0, R.array.trumpche_level_entries,
					R.array.three_values),
			new NodePreference("renual09", 0x6f21, 0x6109,
					0xc, 0, R.array.departure_waring_entries,
					R.array.three_values),

					new NodePreference("renual0a", 0x6f22, 0x6109, 0x2, 0),
					new NodePreference("renual0b", 0x6f23, 0x6109, 0x1, 0),
					
			new NodePreference("renual0c", 0x6f24, 0x610a,
					0xc0, 0, R.array.trumpche_level_entries,
					R.array.three_values),

			new NodePreference("keyless_unlock", 0x6f25, 0x610a, 0x20, 0),
			new NodePreference("car_unlocked", 0x6f26, 0x610a, 0x10, 0),
			new NodePreference("mute_switch", 0x6f27, 0x610a, 0x8, 0),
			new NodePreference("automatic_latch", 0x6f28, 0x610a, 0x4, 0),
			new NodePreference("wipers", 0x6f29, 0x610a, 0x2, 0),
			new NodePreference("auto_wipers", 0x6f2a, 0x610a, 0x1, 0),
			new NodePreference("renual0d", 0x6f2b, 0x610b, 0x80, 0),
			new NodePreference("view_mirror_automatically_folded", 0x6f2c, 0x610b, 0x40, 0),
			new NodePreference("auto_air", 0x6f2d, 0x610b, 0x20, 0),
			new NodePreference("fresh_air_qualit_cycle", 0x6f2e, 0x610b, 0x10, 0),
			

			new NodePreference("renual0f", 0x6f30, 0x6200, 0x0, 0),
			new NodePreference("renual0g", 0x6f31, 0x6200, 0x0, 0),
			new NodePreference("renual0h", 0x6f32, 0x6200, 0x0, 0),
			new NodePreference("renual0i", 0x6f33, 0x6200, 0x0, 0),
			new NodePreference("renual0j", 0x6f34, 0x6200, 0x0, 0),

			new NodePreference("startlatch", 0x6f35, 0x6200, 0x80, 0),
			new NodePreference("parkunlock", 0x6f36, 0x6200, 0x40, 0),
			new NodePreference("outo_security", 0x6f37, 0x6200, 0x20, 0),
			new NodePreference("unlock_the_door", 0x6f38, 0x6200, 0x10, 0),
			new NodePreference("latch_flashing", 0x6f39, 0x6200, 0x8, 0),
			new NodePreference("unlock_flicker", 0x6f3a, 0x6200, 0x4, 0),

			
			new NodePreference("str_over_speed_alert", 0x6f3b, 0x6201,
					0xff, 0, 30, 220, 5),
//					

			new NodePreference("reset_main_info", 0x6f3c, 0x6200, 0x0, 0),
			new NodePreference("str_daytime_running_lamp", 0x6f3d, 0x6204, 0x80, 0),
//			
//			
			new NodePreference("str_go_home", 0x6f3e, 0x6204,
					0x70, 0, R.array.jac_time,
					R.array.five_values),
					
			new NodePreference("over_speed", 0x6f3f, 0x6200, 0x1, 0),
//					

	};

	private final static int[] INIT_CMDS = { 0x61,0x62 };


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.empty_setting);

		init();

	}

	private void init() {
		
		for (int i = 0; i < NODES.length; ++i) {
			Preference p = NODES[i].createPreference(getActivity());
			if (p != null) {
				
				Preference ps = getPreferenceScreen();
				if (ps instanceof PreferenceScreen) {
					boolean add = true;
					if (((NODES[i].mType & 0xff0000) >> 16) != 0) {
						int index = ((NODES[i].mType & 0xff00) >> 8) + 2;
						if ((mVisible[index] & NODES[i].mType) == 0) {
							add = false;
						}
					}

					if (add) {
						((PreferenceScreen) ps).addPreference(p);
					}

					if ((p instanceof ListPreference)
							|| (p instanceof SwitchPreference)) {
						p.setOnPreferenceChangeListener(this);
					} else if (p instanceof MyPreferenceSeekBar) {
						p.setOnPreferenceChangeListener(this);
					} else {
						p.setOnPreferenceClickListener(this);
					}
				}

			}
		}
	}
	
	private void removeAll() {
		getPreferenceScreen().removeAll();
	}
	
	private byte[] mVisible = new byte[] { 0x78, 0, 0, (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };

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
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo(msg.what & 0xff);
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
					
						sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
								NODES[i].mCmd & 0xff,
								((Boolean) newValue) ? 0x1 : 0x0);	
					
				} else if (preference instanceof MyPreferenceSeekBar) {
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff,
							Integer.parseInt((String) newValue));
			
				}  else {
					
					sendCanboxInfo((NODES[i].mCmd & 0xff00) >> 8,
							NODES[i].mCmd & 0xff, 0x1);	
				
			} 
				break;
			}
		}
	}

	public boolean onPreferenceClick(Preference arg0) {
		try {
			udpatePreferenceValue(arg0, null);
		} catch (Exception e) {

		}
	return false; 
}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] {  0x3, (byte) 0x6a, 0x5, 1,(byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { 0x4, (byte) d0,  (byte) d1, (byte) d2, 0,0 };
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
			} else if (p instanceof MyPreferenceSeekBar) {
				p.setSummary(index+"");
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
		if (buf[0] == 0x78) {
			if ((mVisible[3] != buf[3]) || (mVisible[3] != buf[4])
					|| (mVisible[3] != buf[5])) {
				Util.byteArrayCopy(mVisible, buf, 3, 3, mVisible.length - 3);
				removeAll();
				init();
			}
		}
		for (int i = 0; i < NODES.length; ++i) {
			cmd = (NODES[i].mStatus & 0xff00) >> 8;
			index = NODES[i].mStatus & 0xff;
			mask = NODES[i].mMask;

			if ((buf[0] & 0xff) == cmd) {
				mask = (NODES[i].mMask);
				int value = getStatusValue1(buf[2 + index], mask);
				setPreference(NODES[i].mKey, value);
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
