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
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

import java.util.Locale;

public class Mazda3SimpleFragment extends PreferenceFragment {
	private static final String TAG = "JeepInfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.mazda3_simple_info);

	}

	private boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		mPaused = true;
		mHandler.removeMessages(MSG_REQUEST_INIT);
		unregisterListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPaused = false;
		requestInitData();

		registerListener();
	}

	private final static int[] INIT_CMDS = { 0x4001, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			0xa };
	private final static int MSG_REQUEST_INIT = 1000;

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.removeMessages(INIT_CMDS[i]);
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 100));
		}

		mHandler.removeMessages(MSG_REQUEST_INIT);
		mHandler.sendEmptyMessageDelayed(MSG_REQUEST_INIT, 15000);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				if (msg.what == MSG_REQUEST_INIT) {
					requestInitData();
				} else {
					if (msg.what <= 0xff) {
						sendCanboxInfo(msg.what);
					} else {
						sendCanboxInfo2(msg.what);
					}
				}
			}
		}
	};

	private void sendCanboxInfo2(int cmd) {
		byte[] buf = new byte[] { (byte) 0x90, 0x02,
				(byte) ((cmd & 0xff00) >> 8), (byte) (cmd & 0xff) };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int cmd) {
		byte[] buf = new byte[] { (byte) 0x90, 0x02, 0x07, (byte) cmd };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	public String bcd2Str(byte b) {
		String c = "";
		if (b >= 0 && b < 10) {
			c = "" + b;
		} else if (b >= 0xa && b <= 0xf) {
			switch (b) {
			case 0xa:
				c = "A";
				break;
			case 0xb:
				c = "B";
				break;
			case 0xc:
				c = "C";
				break;
			case 0xd:
				c = "D";
				break;
			case 0xe:
				c = "E";
				break;
			case 0xf:
				c = "F";
				break;
			}
		}
		return c;
	}

	private byte mUnit = 0;

	private String getFuelUnit() {
		String s;
		switch ((mUnit & 0x6) >> 1) {
		case 1:
			s = "KM/L";
			break;
		case 2:
			s = "MPG(US)";
			break;
		case 3:
			s = "MPG(UK)";
			break;
		case 0:
		default:
			s = "L/100KM";
			break;
		}
		return s;
	}

	private String getDistanceUnit() {
		String s;
		switch ((mUnit & 0x8) >> 3) {
		case 1:
			s = "MI";
			break;
		default:
			s = "KM";
			break;
		}
		return s;
	}

	private String getSpeedUnit() {
		String s;
		switch ((mUnit & 0x80) >> 7) {
		case 1:
			s = "MPH";
			break;
		default:
			s = "KM/h";
			break;
		}
		return s;
	}

	private void updateView(byte[] buf) {

		int index = 0;
		String s = "";

		switch (buf[0]) {
		case 0x16: {
			index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));

			s = String.format(Locale.ENGLISH, "%d.%d", index/100, index%100);
//			s = index + getSpeedUnit();

			setPreference("speed", s);

			index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));

			s = index + "RPM";

			setPreference("enginespeed", s);

		
			break;

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
