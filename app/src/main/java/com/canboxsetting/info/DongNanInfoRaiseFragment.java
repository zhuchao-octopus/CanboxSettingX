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

public class DongNanInfoRaiseFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "DongNanInfoRaiseFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.dongnan_raise_info);
	}
	
	//初始化命令
	private final static int[] INIT_CMDS = {0x33,0x24};

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
				sendCanboxInfo0x90(msg.what & 0xff, 0);

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
	
	private void updateView(byte[] buf) {

		int index = 0;
		double findex = 0.0;
		String str;
		java.text.DecimalFormat myformat;
		String s = "";
		switch (buf[0]) {
			case (byte) 0x33: {
				//百公里耗油
				index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
				findex = index * 0.1;
			    myformat=new java.text.DecimalFormat("0.0");
			    str = myformat.format(findex);
				s = "" + str + "L/100Km";
				setPreference("consumption_hundredkilometer", s);
				
				//剩余油量
				index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
				s = "" + index + "%";
				setPreference("am_beoilmass", s);
				
				//行驶里程
				index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
				s = "" + index + "KM";
				setPreference("mileagex", s);
					
				//续航里程
				index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8) | ((buf[8] & 0xff) << 16));
				s = "" + index + "Km";
				setPreference("camry_distance", s);
			}
			break;
			case (byte) 0x24:
			{
				//主驾驶安全带
				index = buf[3] & 0x10;
				if(index == 0)
				{
					s = getActivity().getString(R.string.has_been_tied);		//已系上
					setPreference("safebelt_driver", s);
				}
				else					
				{		
					s = getActivity().getString(R.string.safebelt_off);			//未系上
					setPreference("safebelt_driver", s);
				}
				
				//副驾驶安全带
				index = buf[3] & 0x08;
				if(index == 0)
				{
					s = getActivity().getString(R.string.has_been_tied);		//已系上
					setPreference("safebelt_passenger", s);
				}
				else					
				{		
					s = getActivity().getString(R.string.safebelt_off);			//未系上
					setPreference("safebelt_passenger", s);
				}
				
				//手刹状态
				index = buf[3] & 0x02;
				if(index == 0)
				{
					s = getActivity().getString(R.string.brake_down);		//放下
					setPreference("brake_state", s);
				}
				else				
				{		
					s = getActivity().getString(R.string.brake_up);			//拉起
					setPreference("brake_state", s);
				}
				
				//
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
