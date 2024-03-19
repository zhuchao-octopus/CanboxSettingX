package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodePreference;

public class Info100 extends PreferenceFragment {
	private static final String TAG = "HYSettingsRaiseFragment";

	private static final NodePreference[] NODES = {

		new NodePreference("drive_motor_controller_temperature", 0),
		new NodePreference("drive_motor_temperature", 0),
		

		new NodePreference("lowest_voltage_unit", 0),
		new NodePreference("highest_voltage_unit", 0),
		
		new NodePreference("single_minimum_voltage_group_number", 0),
		new NodePreference("highest_voltage_group_number", 0),
		
		new NodePreference("battery_pack_minimum_temperature", 0),
		new NodePreference("battery_pack_maximum_temperature", 0),
		
		new NodePreference("battery_group_minimum_temperature_group", 0),
		new NodePreference("battery_group_maximum_temperature_group", 0),
		
		new NodePreference("remaining_power", 0),


		new NodePreference("battery_pack_charge_discharge_status", 0),
		new NodePreference("bms_charge_discharge_status", 0),

	};

	private final static int[] INIT_CMDS = { 0x27 };

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
					((PreferenceScreen) ps).addPreference(p);
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

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 2, (byte) d0,0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, String s) {
		Preference ps = getPreferenceScreen();
		
		Preference p = ((PreferenceScreen)ps).findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}
	
	private void updateView(byte[] buf) {

		int index;
		String s;
		switch (buf[0]) {
		case 0x27:
			switch(buf[2]){
			case 1:
				index = (buf[3] & 0xff);				
				index -= 40;
				s = index + getString(R.string.temp_unic);		

				setPreference(NODES[0].mKey, s);
				

				index = (buf[4] & 0xff);				
				index -= 40;
				s = index + getString(R.string.temp_unic);		

				setPreference(NODES[1].mKey, s);
				
				break;
			case 2:

				index = ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);

				s = String.format("%d.%d V", index / 1000, index % 1000);

				setPreference(NODES[2].mKey, s);
				
				index = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);

				s = String.format("%d.%d V", index / 1000, index % 1000);

				setPreference(NODES[3].mKey, s);
				

				setPreference(NODES[4].mKey, (buf[7] & 0xff)+"");
				setPreference(NODES[5].mKey, (buf[8] & 0xff)+"");
				
				
				index = (buf[9] & 0xff);				
				index -= 50;
				s = index + getString(R.string.temp_unic);		

				setPreference(NODES[6].mKey, s);
				
				index = (buf[10] & 0xff);				
				index -= 50;
				s = index + getString(R.string.temp_unic);		

				setPreference(NODES[7].mKey, s);
				

				setPreference(NODES[8].mKey, (buf[11] & 0xff)+"");
				setPreference(NODES[9].mKey, (buf[12] & 0xff)+"");
				
				
				s = String.format("%d", (buf[13] & 0xff));
				s += "%";			

				setPreference(NODES[10].mKey, s);
			
				break;
			case 3:
				s = "";
				switch (buf[3] & 0xff) {
				case 1:
					s = getString(R.string.discharged);
					break;
				case 2:
					s = getString(R.string.fast_charge);
					break;
				case 3:
					s = getString(R.string.slow_charge);
					break;
				}

				setPreference(NODES[11].mKey, s);


				s = "";
				switch (buf[4] & 0xff) {
				case 1:
					s = getString(R.string.parking_charging);
					break;
				case 2:
					s = getString(R.string.travel_charging);
					break;
				case 3:
					s = getString(R.string.not_charged);
					break;
				case 4:
					s = getString(R.string.charge_completed);
					break;
				}

				setPreference(NODES[12].mKey, s);
				
				
				break;
			}
			break;
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
