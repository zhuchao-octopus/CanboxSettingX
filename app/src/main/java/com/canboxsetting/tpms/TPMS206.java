package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

public class TPMS206 extends PreferenceFragment {

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

		sendCanboxInfo(0x32);
		Util.doSleep(20);
		sendCanboxInfo(0x33);
	}

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x01, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private View mTpmsView;




	private void setTpmsTextInfo(int id, int value) {

		String text = "";
		if (value <= 0xffe) {
			text = String.format("%dKpa", value);
		} else {
			text = "--";
		}


		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setText(text);
	}


	
	private void setTpmsTextValue(int id, int value) {

		String text;

		
			value = value - 400;
			text = String.format("%d.%d °C", value/10, value%10);
		


		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setText(text);
	}

	private void updateView(byte[] buf) {
		switch (buf[0]) {
		case (byte)0x32:

			if (mTpmsView != null) {

				setTpmsTextInfo(R.id.type11_num, ((buf[2] & 0xff)|(((buf[3] & 0xff)<<8))) );
				setTpmsTextInfo(R.id.type12_num, ((buf[4] & 0xff)|(((buf[5] & 0xff)<<8))) );
				setTpmsTextInfo(R.id.type21_num, ((buf[6] & 0xff)|(((buf[7] & 0xff)<<8))) );
				setTpmsTextInfo(R.id.type22_num, ((buf[8] & 0xff)|(((buf[9] & 0xff)<<8))) );
				

			}

			break;
		case (byte)0x33:

			if (mTpmsView != null) {

				setTpmsTextValue(R.id.type11_info, ((buf[2] & 0xff)|(((buf[3] & 0xff)<<8))) );
				setTpmsTextValue(R.id.type12_info, ((buf[4] & 0xff)|(((buf[5] & 0xff)<<8))) );
				setTpmsTextValue(R.id.type21_info, ((buf[6] & 0xff)|(((buf[7] & 0xff)<<8))) );
				setTpmsTextValue(R.id.type22_info, ((buf[8] & 0xff)|(((buf[9] & 0xff)<<8))) );
				

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
