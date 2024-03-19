package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class TPMS182 extends PreferenceFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTpmsView = inflater.inflate(R.layout.type_info4, container, false);
		return mTpmsView;
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

		byte[] buf = new byte[] { (byte) 0x90, 0x02, (byte) 0xd2, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private View mTpmsView;




	private void setTpmsTextInfo(int id, int value, int color) {

		String text = "";

		if (value != 255) {
				text = String.format("%d Kpa", value);
		} else {
			text = "--";
		}

		// if (color != 0) {
		// color = Color.RED;
		// } else {
		color = Color.WHITE;
		// }

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}

	private void setTpmsTextInfo2(int id, int value) {

		String text = value + " V";
		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setText(text);
	}
	
	private void setTpmsTextValue(int id, int value, int color) {

		String text;

		if (value == 255) {
			text = "--";
		} else {
			value = (byte)value;
			text = String.format("%d °C", value);
		}

		// if (color != 0) {
		// color = Color.RED;
		// } else {
		color = Color.WHITE;
		// }

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}

	private void setTpmsWaring(int value) {
		String s = "";
		int id = 0;
		if ((value & 0xf) != 0) {
			switch ((value & 0xf)) {
			case 1:
				id = R.string.jac_front_left_4;
				break;
			case 2:
				id = R.string.jac_front_left_3;
				break;
			case 3:
				id = R.string.jac_front_left_2;
				break;
			case 4:
				id = R.string.jac_front_left_1;
				break;
			case 5:
				id = R.string.jac_front_left_5;
				break;
			case 6:
				id = R.string.jac_front_left_6;
				break;
			case 7:
				id = R.string.jac_front_left_7;
				break;
			}
		} else if ((value & 0xf0) != 0) {
			switch ((value & 0xf0) >> 4) {
			case 1:
				id = R.string.jac_front_right_4;
				break;
			case 2:
				id = R.string.jac_front_right_3;
				break;
			case 3:
				id = R.string.jac_front_right_2;
				break;
			case 4:
				id = R.string.jac_front_right_1;
				break;
			case 5:
				id = R.string.jac_front_right_5;
				break;
			case 6:
				id = R.string.jac_front_right_6;
				break;
			case 7:
				id = R.string.jac_front_right_7;
				break;
			}
		} else if ((value & 0xf000) != 0) {
			switch ((value & 0xf000) >> 12) {
			case 1:
				id = R.string.jac_rear_left_4;
				break;
			case 2:
				id = R.string.jac_rear_left_3;
				break;
			case 3:
				id = R.string.jac_rear_left_2;
				break;
			case 4:
				id = R.string.jac_rear_left_1;
				break;
			case 5:
				id = R.string.jac_rear_left_5;
				break;
			case 6:
				id = R.string.jac_rear_left_6;
				break;
			case 7:
				id = R.string.jac_rear_left_7;
				break;
			}
		} else if ((value & 0xf00) != 0) {
			switch ((value & 0xf00) >> 8) {
			case 1:
				id = R.string.jac_rear_right_4;
				break;
			case 2:
				id = R.string.jac_rear_right_3;
				break;
			case 3:
				id = R.string.jac_rear_right_2;
				break;
			case 4:
				id = R.string.jac_rear_right_1;
				break;
			case 5:
				id = R.string.jac_rear_right_5;
				break;
			case 6:
				id = R.string.jac_rear_right_6;
				break;
			case 7:
				id = R.string.jac_rear_right_7;
				break;
			}
		}
		TextView tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));
		if (id != 0) {
			s = getString(id);
		}
		tv.setText(s);
	}

	private void updateView(byte[] buf) {
		switch (buf[0]) {
		case (byte)0x48:

			if (mTpmsView != null) {

				setTpmsTextInfo(R.id.type11_num, buf[6] & 0xff, 0);
				setTpmsTextInfo(R.id.type12_num, buf[7] & 0xff, 0);
				setTpmsTextInfo(R.id.type21_num, buf[8] & 0xff, 0);
				setTpmsTextInfo(R.id.type22_num, buf[9] & 0xff, 0);
				

				setTpmsTextValue(R.id.type11_info, buf[2] & 0xff,0 );
				setTpmsTextValue(R.id.type12_info, buf[3] & 0xff,0 );
				setTpmsTextValue(R.id.type21_info, buf[4] & 0xff,0 );
				setTpmsTextValue(R.id.type22_info, buf[5] & 0xff,0 );

//				setTpmsTextInfo2(R.id.type11_info2, buf[5] & 0xf );
//				setTpmsTextInfo2(R.id.type12_info2, buf[9] & 0xf );
//				setTpmsTextInfo2(R.id.type21_info2, buf[13] & 0xf );
//				if (buf.length > 16){
//
//					setTpmsTextInfo2(R.id.type22_info2, buf[17] & 0xf );
//				}
				
			}

			break;
		case 0x39: {
			if (mTpmsView != null) {
				setTpmsWaring((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
			}
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
