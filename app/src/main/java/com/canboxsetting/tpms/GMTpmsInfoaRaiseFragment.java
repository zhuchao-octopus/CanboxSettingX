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
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

public class GMTpmsInfoaRaiseFragment extends PreferenceFragment implements
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

		byte[] buf = new byte[] { (byte) 0xe4, 0x01, 0x0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		
		unregisterListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();
		Bundle b = getArguments();
		String proIndex = b.getString(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX);
		if ("111".equals(proIndex)){
			byte[] buf = new byte[] { 0x3, (byte) 0x60, 0x05,  0x1, 0x68 };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		} else if ("244".equals(proIndex)){
			byte[] buf = new byte[] { (byte) 0x90, 0x01, (byte) 0x65 };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
			Util.doSleep(20);
			buf = new byte[] { (byte) 0xe4, 0x01, 0x5 };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		}  else {
			byte[] buf = new byte[] { (byte) 0x90, 0x01, (byte) 0x4a };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		}		

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

		text = value + " kPa";
		color = Color.WHITE;
		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		tv.setText(text);

	}
	
	private void setTpmsTextValueBNR(int id, int value, int unit) {

		String text;
		switch(unit){
		case 1:
			text = value + " PSI";
			break;
		case 2:
			text = (value/4) + " kpa";
			break;
		default:
			text = String.format("%d.%d bar", value/10, value%10);
			break;
		}
		
		TextView tv = ((TextView) mTpmsView.findViewById(id));
		tv.setText(text);

	}

	private void setTpmsText(int id, int text) {
		int color = Color.RED;
		if ((text & 0x1) != 0) {
			text = R.string.tpms_hi;
		} else if ((text & 0x2) != 0) {
			text = R.string.tpms_low;
		} else if ((text & 0x4) != 0) {
			text = R.string.vw_raise_warning_info_1;
		} else {
			text = R.string.am_normal;
			color = Color.WHITE;
		}

		TextView tv = ((TextView) mTpmsView.findViewById(id));

		tv.setTextColor(color);
		if (text != 0) {
			tv.setText(text);
		} else {
			if (tv.getText().length() == 0) {
				tv.setText(R.string.am_normal);
			}
		}
	}
	
	private void updateView(byte[] buf) {

		switch (buf[0]) {
		case 0x4a:
			if (buf[2] == 0x1) {
				setTpmsTextValue(R.id.type11_info, (buf[4] & 0xff)*4, 0);
				setTpmsTextValue(R.id.type12_info, (buf[5] & 0xff)*4, 0);
				setTpmsTextValue(R.id.type21_info, (buf[6] & 0xff)*4, 0);
				setTpmsTextValue(R.id.type22_info, (buf[7] & 0xff)*4, 0);
			}
			break;
		case 0x68: //hiworld
			setTpmsTextValue(R.id.type11_num,
					(((buf[3] & 0xff) << 8) | (buf[4] & 0xff)), 0);
			setTpmsTextValue(R.id.type12_num,
					(((buf[5] & 0xff) << 8) | (buf[6] & 0xff)), 0);
			setTpmsTextValue(R.id.type21_num,
					(((buf[7] & 0xff) << 8) | (buf[8] & 0xff)), 0);
			setTpmsTextValue(R.id.type22_num,
					(((buf[9] & 0xff) << 8) | (buf[10] & 0xff)), 0);

			setTpmsText(R.id.type11_info, buf[13]);
			setTpmsText(R.id.type12_info, buf[14]);
			setTpmsText(R.id.type21_info, buf[15]);
			setTpmsText(R.id.type22_info, buf[16]);
			break;
		case 0x65: //bnr

			setTpmsTextValueBNR(R.id.type11_info, buf[3]&0xff, buf[2]&0x3);
			setTpmsTextValueBNR(R.id.type12_info, buf[4]&0xff, buf[2]&0x3);
			setTpmsTextValueBNR(R.id.type21_info, buf[5]&0xff, buf[2]&0x3);
			setTpmsTextValueBNR(R.id.type22_info, buf[6]&0xff, buf[2]&0x3);
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
