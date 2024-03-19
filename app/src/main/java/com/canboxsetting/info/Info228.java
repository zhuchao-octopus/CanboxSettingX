package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class Info228 extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "KadjarRaiseFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.subaru_luzheng_info);
	}

	private final static int[] INIT_CMDS = {  0x35 };

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90(msg.what & 0xff);

			}
		}
	};

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public boolean onPreferenceClick(Preference arg0) {

		return false;
	}

	private boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
		mPaused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();
		mPaused = false;
		requestInitData();
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private int getFuelrange(byte b) {
		int ret = 0;
		switch (b) {
		case 0:
			ret = 60;
			break;
		case 1:
			ret = 10;
			break;
		case 2:
			ret = 12;
			break;
		case 3:
			ret = 20;
			break;
		case 4:
			ret = 30;
			break;
		case 5:
			ret = 40;
			break;
		case 6:
			ret = 50;
			break;
		case 7:
			ret = 60;
			break;
		case 8:
			ret = 70;
			break;
		case 9:
			ret = 80;
			break;
		case 10:
			ret = 90;
			break;
		case 11:
			ret = 100;
			break;
		}
		return ret;
	}

	private void updateView(byte[] buf) {

		int index = 0;
		String s = "";

		switch (buf[0]) {
		case 0x35: {
			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = "l/l00km";
			s = String.format("%d.%d", index / 10, index % 10) + " "
					+ s;
			setPreference("instant", s);
			
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
			s = "l/l00km";
			if (index != 0xffff) {
				s = String.format("%d.%d", index / 10, index % 10) + " "
						+ s;
			} else {
				s = "-- " + s;
			}
			setPreference("tripA_average_fuel_consumption", s);

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
			s = "l/l00km";
			if (index != 0xffff) {
				s = String.format("%d.%d", index / 10, index % 10) + " "
						+ s;
			} else {
				s = "-- " + s;
			}
			setPreference("tripB_average_fuel_consumption", s);
			
			
			index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
			s = "km";
			if (index != 0xffff) {
				s = index + " " + s;
			} else {
				s = "-- " + s;
			}
			setPreference("driving_range", s);
			
			
			index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
			s = "km";
			if (index != 0xffff) {
				s = String.format("%d", index) + " "
						+ s;
			} else {
				s = "-- " + s;
			}
			setPreference("tripA_cumulative_mileage", s);
			
			index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8));
			s = "km";
			if (index != 0xffff) {
				s = String.format("%d", index) + " "
						+ s;
			} else {
				s = "-- " + s;
			}
			setPreference("tripB_cumulative_mileage", s);
			
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
