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
import android.util.Log;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

import java.util.Locale;

public class Info285 extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "ToyotaInfoHiworldFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.toyota_hiworld_info);
		

		getPreferenceScreen().removePreference(findPreference("dynamical"));
		getPreferenceScreen().removePreference(findPreference("engine_speed55"));
		getPreferenceScreen().removePreference(findPreference("averagespeed24"));
//		getPreferenceScreen().removePreference(findPreference("unit_of_fuel_consumption"));
//		getPreferenceScreen().removePreference(findPreference("mile_unit"));
		getPreferenceScreen().removePreference(findPreference("driving_mileage"));
		getPreferenceScreen().removePreference(findPreference("traveltime"));
	}
	
	//初始化命令
	private final static int[] INIT_CMDS = { 0x2200, 0x2300 };

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxInfo0x90(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public boolean onPreferenceClick(Preference arg0) {

		return false;
	}

	private boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
		mPaused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();
		mPaused = false;
		requestInitData();
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}
	
	private String getOilString(int index, int unit){
		String s;
		
		if (unit == 0) {
			s = index + "MPG";
		} else if (unit == 1) {
			s = index + "KM/L";
		} else {
			s = index + "L/100KM";
		}
//		setPreference("dynamical_fuel", s);
		return s;
	}
	private void updateView(byte[] buf) {

		int index = 0;
		String s = "";
		switch (buf[0]) {
		case 0x22:
			//瞬时油耗
			index = ((buf[3] & 0xff) << 8 | (buf[4] & 0xff));
		
//			if (buf[2] == 0) {
//				s = index + "MPG";
//			} else if (buf[2] == 1) {
//				s = index + "KM/L";
//			} else {
//				s = index + "L/100KM";
//			}
			s = getOilString(index, buf[2]);
			setPreference("dynamical_fuel", s);
//			setOil("dynamical_fuel",index,  buf[2]);
			break;
			case (byte) 0x32: {
				//发动机转速 
				index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
				s = "" + index + "RPM";
				setPreference("engine_speed55", s);
				
				//顺时车速
				index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
				s = "" + index + "Km/h";
				//
				setPreference("dynamical", s);
			}
			break;
			case (byte) 0x13: {
				//平均速度
				index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
				s = "" + index + "Km/h";
				setPreference("averagespeed24", s);
				
				index = (buf[12] & 0xff);
				switch(index)
				{
					case 0x00:
					{
						setPreference("unit_of_fuel_consumption", "MPG");
						break;
					}
					case 0x01:
					{
						setPreference("unit_of_fuel_consumption", "km/L");
						break;
					}
					case 0x02:
					{
						setPreference("unit_of_fuel_consumption", "L/100km");
						break;
					}
				}
				
				//行驶时间
				index = ((buf[8] & 0xff) << 8 | (buf[9] & 0xff));
				s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60,index % 60);
				setPreference("traveltime", s);
				
				//续航
				index = ((buf[4] & 0xff) << 8 | (buf[5] & 0xff));
				s = index + "km";
				setPreference("driving_mileage", s);
				
			
				
				//里程单位
				index = (buf[13] & 0xff);
				switch(index)
				{
					case 0x00:
					{
						setPreference("mile_unit", "km");
						break;
					}
					case 0x01:
					{
						setPreference("mile_unit", "mile");
						break;
					}
				}
			}
			break;
			case (byte)0x23:
			{
				//当前(现在)行程油耗
				index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("curfuel", s);
//				setOil("curfuel",index,  buf[2]);
				//Trip1
				index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("trip1", s);
				
				//Trip2
				index = ((buf[8] & 0xff) | ((buf[7] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("trip2", s);
				
				//Trip3
				index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("trip3", s);
				
				//Trip4
				index = ((buf[12] & 0xff) | ((buf[11] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("trip4", s);
				
				//Trip5
				index = ((buf[14] & 0xff) | ((buf[13] & 0xff) << 8));
				s = getOilString(index, buf[2]);
				setPreference("trip5", s);
				
				index = (buf[14] & 0xff);
				switch(index)
				{
					case 0x00:
					{
						break;
					}
					case 0x01:
					{
						break;
					}
					case 0x22:
					{
						break;
					}
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
