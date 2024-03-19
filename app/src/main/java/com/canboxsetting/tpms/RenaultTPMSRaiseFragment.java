package com.canboxsetting.tpms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

public class RenaultTPMSRaiseFragment extends PreferenceFragment implements
		OnPreferenceClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTpmsView = inflater.inflate(R.layout.type_info3, container, false);
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

		byte[] buf = new byte[] { (byte) 0x90, 0x01, (byte) 0x4a };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private void sendCanboxInfo(int d0, int d1, int d2) {
		byte[] buf = new byte[] { (byte) d0, 0x02, (byte) d1, (byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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

	private void setTpmsTextValue(int id, int value, int color) {
		String text;
		if (value == 0xff) {
			text = "";
		} else {
			value = value * 3;
			text = String.format("%d.%02d  kPa", value / 100,
					value % 100);
		}
	

//		text = value * 4 + " kPa";
		color = Color.WHITE;
		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);

	}

	private void updateView(byte[] buf) {

		switch (buf[0]) {
		case 0x61:

			setTpmsTextValue(R.id.type11_info, buf[3] & 0xff, 0);
			setTpmsTextValue(R.id.type12_info, buf[4] & 0xff, 0);
			setTpmsTextValue(R.id.type21_info, buf[5] & 0xff, 0);
			setTpmsTextValue(R.id.type22_info, buf[6] & 0xff, 0);

			String ss[] = getResources().getStringArray(
					R.array.renault_tire_tips_or_warnings_entries);

			TextView tv = ((TextView) mTpmsView.findViewById(R.id.type30_info));

			tv.setText(ss[buf[2]]);

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
