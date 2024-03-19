package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class TPMS114 extends PreferenceFragment {

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

		byte[] buf = new byte[] { (byte) 0x90, 1, (byte) 0x60};
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private View mTpmsView;




	private void setTpmsTextInfo(int id, int value, int color) {

		String text = "";

		value = value*137/100;
		if (value != 0xff) {
			text = String.format("%d kpa", value);
		} else {
			text = "--";
		}

		
		if (color == 0) {
			color = Color.WHITE;
		} else {
			color = Color.RED;
		}

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}

	
	private void setTpmsTextValue(int id, int value, int color) {

		String text;

		if (value >= 0xc3) {
			text = "--";
		} else {
			value = value - 40;
			text = String.format("%d °C", value);
		}

		color = Color.WHITE;

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}
	
	

	private void setTpmsWaring(int text_id, int value) {
		String s = "";
		int id = 0;
		switch ((value & 0xff)) {
		case 0xc:
			id = R.string.invalid_sensor;
			break;
		case 0xa:
			id = R.string.str_sensor_low_battery_alarm;
			break;
		case 0x8:
			id = R.string.str_warning_lost_sensor;
			break;
		case 0x6:
			id = R.string.red_flashs;
			break;
		case 0x4:
			id = R.string.str_low_pressure;
			break;
		case 0x2:
			id = R.string.str_high_pressure;
			break;
		}
		TextView tv = ((TextView) mTpmsView.findViewById(text_id));
		if (id != 0) {
			s = getString(id);
		}
		tv.setText(s);
	}
	int mColor;
	private void updateView(byte[] buf) {
		switch (buf[0]) {
		case (byte)0x60:

			if (mTpmsView != null) {
				

				setTpmsTextInfo(R.id.type11_num, (buf[5] & 0xff), 0);				
				
				setTpmsTextInfo(R.id.type12_num, (buf[6] & 0xff),  0);				
				setTpmsTextInfo(R.id.type21_num, (buf[7] & 0xff),  0);
				
				setTpmsTextInfo(R.id.type22_num, (buf[8] & 0xff),  0);

				setTpmsTextValue(R.id.type11_info, (buf[9] & 0xff),  0);
				setTpmsTextValue(R.id.type12_info, (buf[10] & 0xff),  0);
				setTpmsTextValue(R.id.type21_info, (buf[11] & 0xff),  0);
				setTpmsTextValue(R.id.type22_info, (buf[12] & 0xff),  0);

				setTpmsWaring(R.id.type12_info2, buf[3] & 0xf);
				setTpmsWaring(R.id.type11_info2, (buf[3] & 0xf0)>>4);
				setTpmsWaring(R.id.type22_info2, buf[4] & 0xf);
				setTpmsWaring(R.id.type21_info2, (buf[4] & 0xff)>>4);

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
