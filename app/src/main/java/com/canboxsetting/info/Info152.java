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
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class Info152 extends PreferenceFragment {
	private static final String TAG = "Golf7InfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.accord2013_info);
		
		getPreferenceScreen().removePreference(findPreference("fuelrange")); //why remove fail?
		getPreferenceScreen().removePreference(findPreference("hfuelrange"));
		
		if ((GlobalDef.getProId() == 153)){
			INIT_CMDS = INIT_CMDS_153;
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

	private final static int[] INIT_CMDS_152 = { 0x830501, 0x830502 };
	private final static int[] INIT_CMDS_153 = { 0x8d0801, 0x8d0802 };
	private static int[] INIT_CMDS = INIT_CMDS_152;

	private void requestInitData() {
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo((msg.what & 0xff0000) >> 16, (msg.what & 0xff00) >> 8, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxInfo(int cmd, int d0, int d1) {
		byte[] buf = new byte[] { (byte) cmd, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
		int index;
		String s = "";
		if ((GlobalDef.getProId() == 153)){
			if ((buf[0] != 8)){
				return ;
			}
		} else if ((GlobalDef.getProId() == 152)){
			if ((buf[0] != 5)){
				return ;
			}
		}
		switch (buf[0]) {
		case 0x5:
		case 0x8:
			switch (buf[2]) {
			case 1: {
				int unit = (buf[15] & 0xff);

				index = getFuelrange(buf[16]);
				s = "" + index;
				setPreference("fuelrange", s);

				index = (buf[3] & 0xff);
				if (index == 0xff) {
					s = "--";
				} else {
					if ((unit & 0x3) == 0) {
						s = "mpg";
					} else if ((unit & 0x3) == 1) {
						s = "km/l";
					} else {
						s = "l/l00km";
					}
					s = index + " " + s;
				}
				setPreference("instant", s);

				index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

				if ((unit & 0x3) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("average", s);

				index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

				if (((unit & 0xc) >> 2) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("historical", s);

				index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

				if (((unit & 0x30) >> 4) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("ages", s);

				index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8) | ((buf[10] & 0xff) << 16));

				if (((unit & 0x40)) == 0) {
					s = "km";
				} else {
					s = "mil";
				}
				if (index != 0xffffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("tripaoilwear", s);

				index = ((buf[14] & 0xff) | ((buf[13] & 0xff) << 8));

				if (((unit & 0x80)) == 0) {
					s = "km";
				} else {
					s = "mil";
				}
				if (index != 0xffff) {
					s = String.format("%d", index) + " " + s;
				} else {
					s = "-- " + s;
				}
				setPreference("drivingmileage", s);
				break;
			}
			case 0x2:
				int unit = (buf[18] & 0xff);

				index = getFuelrange(buf[19]);
				s = "" + index;
				setPreference("hfuelrange", s);

				index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));

				if (((unit & 0x40)) == 0) {
					s = "km";
				} else {
					s = "mil";
				}

				if (index != 0xffffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("hjourney1", s);

				index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8) | ((buf[8] & 0xff) << 16));

				if (((unit & 0x40)) == 0) {
					s = "km";
				} else {
					s = "mil";
				}
				if (index != 0xffffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("hjourney2", s);

				index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8) | ((buf[13] & 0xff) << 16));

				if (((unit & 0x40)) == 0) {
					s = "km";
				} else {
					s = "mil";
				}

				if (index != 0xffffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("hjourney3", s);

				index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

				if ((unit & 0x3) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("thistorical1", s);

				index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8));

				if ((unit & 0x3) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("thistorical2", s);

				index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));

				if (((unit & 0x30) >> 4) == 0) {
					s = "mpg";
				} else if ((unit & 0x3) == 1) {
					s = "km/l";
				} else {
					s = "l/l00km";
				}
				if (index != 0xffff) {
					s = String.format("%d.%d", index / 10, index % 10) + " "
							+ s;
				} else {
					s = "-- " + s;
				}
				setPreference("thistorical3", s);
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
