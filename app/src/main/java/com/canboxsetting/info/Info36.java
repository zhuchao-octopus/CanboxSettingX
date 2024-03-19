package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class Info36 extends PreferenceFragment {
	private static final String TAG = "Golf7InfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.renault_baogu_info);

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();

		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) 0x38, (byte) 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}



	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}


	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case 0x38:
			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8)| ((buf[5] & 0xff) << 16));
			if ((buf[2]&0x08) == 0) {
				s = index / 10 + "." + index % 10 + " km";
			} else {
				s = index / 10 + "." + index % 10 + " mils";
			}
			setPreference("mileage", s);
			
			
			index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " km/h";
			setPreference("averagespeed", s);		
			
			index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));

			if ((buf[2]&0x3) == 0) {
				s = index/10 + "." + index%10 + " l/100km";
			} else if ((buf[2]&0x3) == 2) {
				s = index/10 + "." + index%10 + " mpg(US)";
			} else if ((buf[2]&0x3) == 3) {
				s = index/10 + "." + index%10 + " mpg(UK)";
			} else if ((buf[2]&0x3) == 1) {
				s = index/10 + "." + index%10 + " km/l";
			}
			setPreference("averagefuel", s);
			
			index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));
			s = index / 10 + "." + index % 10 + " L";
			setPreference("fuelinfo", s);
			
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
