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
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.MyPreference2;

public class TPMS107 extends PreferenceFragment {

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

		byte[] buf = new byte[] { 3, (byte) 0x6a, 0x05, 1, (byte) 0x48 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}



	private View mTpmsView;




	private void setTpmsTextInfo(int id, int value, int color) {

		String text = "";
//		value = (int)(value*2.745f);
		
		if (value != 255) {
				text = String.format("%d kPa", value);
		} else {
			text = "--";
		}

		if (color == 0) {
			color = Color.WHITE;
		} else if (color == 3) {
			color = Color.YELLOW;
		} else {
			color = Color.RED;
		}

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}

	
	private void setTpmsTextValue(int id, int value, int color) {

		String text;

		if (value == 255) {
			text = "--";
		} else {
			value = value-60;
			text = String.format("%d °C", value);
		}

		color = Color.WHITE;

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);
	}
	
	int mColor;
	private void updateView(byte[] buf) {
		switch (buf[0]) {
		case (byte)0x48:

			if (mTpmsView != null) {
				
				setTpmsTextInfo(R.id.type11_num, (buf[4] & 0xff)
						+ (buf[9] & 0xff), mColor & 0xff);
				setTpmsTextInfo(R.id.type12_num, (buf[5] & 0xff)
						+ (buf[10] & 0xff), (mColor & 0xff00) >> 8);
				setTpmsTextInfo(R.id.type21_num, (buf[6] & 0xff)
						+ (buf[11] & 0xff), (mColor & 0xff0000) >> 16);
				setTpmsTextInfo(R.id.type22_num, (buf[7] & 0xff)
						+ (buf[12] & 0xff), (mColor & 0xff000000) >> 24);

				TextView tv = ((TextView) mTpmsView
						.findViewById(R.id.type30_info));

				if ((buf[2] & 0xc0) == 0xc0) {
					tv.setTextColor(0xffff0000);
					tv.setText(R.string.red_warn);
				} else {

					tv.setText("");
				}



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
