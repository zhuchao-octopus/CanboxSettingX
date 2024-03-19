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
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class VWMQBInfoRaiseFragmentListViewNoUsed extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "VWMQBInfoRaiseFragment";

	PreferenceScreen mTpms;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.vw_mqb_raisse_info);
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

		// sendCanboxInfo(0x50, 0x1A);

		mHandler.sendEmptyMessageDelayed(0x5010, 0);
		mHandler.sendEmptyMessageDelayed(0x5010, 100 * 1);
		mHandler.sendEmptyMessageDelayed(0x5020, 100 * 2);
		mHandler.sendEmptyMessageDelayed(0x5021, 100 * 3);
		mHandler.sendEmptyMessageDelayed(0x5022, 100 * 4);

		mHandler.sendEmptyMessageDelayed(0x5030, 100 * 5);
		mHandler.sendEmptyMessageDelayed(0x5031, 100 * 6);
		mHandler.sendEmptyMessageDelayed(0x5032, 100 * 7);

		mHandler.sendEmptyMessageDelayed(0x5040, 100 * 8);
		mHandler.sendEmptyMessageDelayed(0x5041, 100 * 9);
		mHandler.sendEmptyMessageDelayed(0x5042, 100 * 10);

		mHandler.sendEmptyMessageDelayed(0x5050, 100 * 11);
		mHandler.sendEmptyMessageDelayed(0x5051, 100 * 12);
		mHandler.sendEmptyMessageDelayed(0x5052, 100 * 13);

		mHandler.sendEmptyMessageDelayed(0x5060, 100 * 14);
		mHandler.sendEmptyMessageDelayed(0x5061, 100 * 15);

		mHandler.sendEmptyMessageDelayed(0x6310, 100 * 16);
		mHandler.sendEmptyMessageDelayed(0x6311, 100 * 17);

		mHandler.sendEmptyMessageDelayed(0x6320, 100 * 18);
		mHandler.sendEmptyMessageDelayed(0x6321, 100 * 19);

		mHandler.sendEmptyMessageDelayed(0x6300, 100 * 20);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mHandler.removeMessages(msg.what);
			// mHandler.sendEmptyMessageDelayed(msg.what, 700);
			sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
		}
	};

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private View mTpmsView;
	
	public boolean onPreferenceClick(Preference arg0) {

		try {
			String key = arg0.getKey();
			if ("tpms".equals(key)) {
				mTpmsView = null;
				sendCanboxInfo(0x90, 0x65, 0);
			} 
		} catch (Exception e) {

		}

		return false;
	}
	
	private void setTpmsText(int id, int text){
		switch(text){
		case 1:
			text = R.string.tpms_low;
			break;
		case 2:
			text = R.string.tpms_hi;
			break;
		default:
			text = R.string.am_normal;
			break;
		}
		
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		
		tv.setText(text);
	}
	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case 0x65:

			if (mTpmsView == null) {
				MyPreference2 p = (MyPreference2) findPreference("tpms_content");
				if (p != null) {
					mTpmsView = p.getMainView();
				}
			}
			
			setTpmsText(R.id.type11_num, buf[2]);
			setTpmsText(R.id.type12_num, buf[3]);
			setTpmsText(R.id.type21_num, buf[4]);
			setTpmsText(R.id.type22_num, buf[5]);
			
			break;
		case 0x16:
			index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
			index /= 16;
			if ((buf[4] & 0x1) == 0) {
				s = index + " km/h";
			} else {
				s = index + " mph";
			}
			setPreference("speed", s);
			break;
		case 0x50:
			switch (buf[2]) {
			case 0x10:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM";
				} else {
					s = index + " MI";
				}
				setPreference("mileage", s);
				break;
			case 0x20:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)
						| ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM";
				} else {
					s = index + " MI";
				}
				setPreference("since_start", s);
				break;
			case 0x21:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)
						| ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM";
				} else {
					s = index + " MI";
				}
				setPreference("since_refueling", s);
				break;
			case 0x22:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)
						| ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM";
				} else {
					s = index + " MI";
				}
				setPreference("long_term", s);
				break;

			case 0x30:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x3) == 0) {
					s = index + " L/100KM";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " KM/L";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " MPG(UK)";
				} else {
					s = index + " MPG(US)";
				}
				setPreference("avg_start", s);
				break;
			case 0x31:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x3) == 0) {
					s = index + " L/100KM";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " KM/L";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " MPG(UK)";
				} else {
					s = index + " MPG(US)";
				}
				setPreference("avrefueling", s);
				break;
			case 0x32:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x3) == 0) {
					s = index + " L/100KM";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " KM/L";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " MPG(UK)";
				} else {
					s = index + " MPG(US)";
				}
				setPreference("avlong_term", s);
				break;
			case 0x40:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM/H";
				} else {
					s = index + " MPH";
				}
				setPreference("avspeed_start", s);
				break;
			case 0x41:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM/H";
				} else {
					s = index + " MPH";
				}
				setPreference("speeds_refueling", s);
				break;
			case 0x42:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x1) == 0) {
					s = index + " KM/H";
				} else {
					s = index + " MPH";
				}
				setPreference("speed_long", s);
				break;
			case 0x50:
				index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
				s = index + " MIN";
				setPreference("travelling_time", s);
				break;
			case 0x51:
				index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
				s = index + " MIN";
				setPreference("ttsr", s);
				break;
			case 0x52:
				index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
				s = index + " MIN";
				setPreference("tt_long_term", s);
				break;

			case 0x60:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
				if ((buf[3] & 0x1) == 0) {
					s = index + " gal/h";
				} else {
					s = index + " l/h";
				}
				setPreference("conv_consumers", s);
				break;

			case 0x61:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
				if ((buf[3] & 0x3) == 0) {
					s = index + " L/100KM";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " KM/L";
				} else if ((buf[3] & 0x3) == 1) {
					s = index + " MPG(UK)";
				} else {
					s = index + " MPG(US)";
				}
				setPreference("instant", s);
				break;
			}

			break;

		case 0x63:

			switch (buf[2]) {
			case 0x0:
				byte[] name = new byte[buf.length - 4];
				Util.byteArrayCopy(name, buf, 0, 3, name.length);
				try {
					s = new String(name, "GBK");
				} catch (Exception e) {

				}
				setPreference("vehicle", s);
				break;
			case 0x10:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
				if ((buf[3] & 0xf) == 0) {
					s = "------";
				} else if ((buf[3] & 0xf) == 1) {
					s = index + getString(R.string.days);
				} else {
					s = getString(R.string.be_overdue) + index
							+ getString(R.string.days);
				}
				setPreference("vi_days", s);
				break;

			case 0x11:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

				if ((buf[3] & 0xf0) == 0) {
					s = " KM";
				} else {
					s = " MI";
				}

				if ((buf[3] & 0xf) == 0) {
					s = "------";
				} else if ((buf[3] & 0xf) == 1) {
					s = index + s;
				} else {
					s = getString(R.string.be_overdue) + index + s;
				}
				setPreference("vi_distance", s);
				break;

			case 0x20:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
				if ((buf[3] & 0xf) == 0) {
					s = "------";
				} else if ((buf[3] & 0xf) == 1) {
					s = index + getString(R.string.days);
				} else {
					s = getString(R.string.be_overdue) + index
							+ getString(R.string.days);
				}
				setPreference("oil_days", s);
				break;

			case 0x21:
				index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

				if ((buf[3] & 0xf0) == 0) {
					s = " KM";
				} else {
					s = " MI";
				}

				if ((buf[3] & 0xf) == 0) {
					s = "------";
				} else if ((buf[3] & 0xf) == 1) {
					s = index + s;
				} else {
					s = getString(R.string.be_overdue) + index + s;
				}
				setPreference("oil_change", s);
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
							try{
							updateView(buf);
							}catch(Exception e){
								
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
