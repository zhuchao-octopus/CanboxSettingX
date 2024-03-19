/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use getActivity() file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canboxsetting.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class AC141 extends MyFragment {
	private static final String TAG = "JeepAirControlFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private View mMainView;

	private CommonUpdateView mCommonUpdateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMainView = inflater.inflate(R.layout.ac_beiqiecc180_raise, container,
				false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface2);


		if (GlobalDef.getModelId() == 26) {
			( mMainView.findViewById(R.id.left_temp2))
					.setVisibility(View.VISIBLE);
			( mMainView.findViewById(R.id.left_temp_layout))
					.setVisibility(View.GONE);
		} else {
			( mMainView.findViewById(R.id.left_temp2))
					.setVisibility(View.GONE);
			( mMainView.findViewById(R.id.left_temp_layout))
					.setVisibility(View.VISIBLE);
		}
		init();
		return mMainView;
	}

	private void init(){
		for (int i = 0; i < CMD_ID.length; ++i) {
			View v = mMainView.findViewById(CMD_ID[i][0]);
			if (v != null) {				
				v.setOnTouchListener(mOnTouchListener);
			}
		}
	}	
	
	MsgInterface mMsgInterface2 = new MsgInterface() {
		@Override
		public void callBack(int msg) {
			mMsgInterface.callBack(msg);
			
			
			if (GlobalDef.getModelId() == 26){
				String s = "";
				switch(mCommonUpdateView.getAirData(2)){
				case 1:
					s = getString(R.string.cold_1);
					break;
				case 2:
					s = getString(R.string.cold_2);
					break;
				case 3:
					s = getString(R.string.cold_3);
					break;
				case 4:
					s = getString(R.string.cold_4);
					break;
				case 5:
					s = getString(R.string.natural_wind);
					break;
				case 6:
					s = getString(R.string.heat_2);
					break;
				case 7:
					s = getString(R.string.heat_3);
					break;
				case 8:
					s = getString(R.string.heat_4);
					break;
				case 9:
					s = getString(R.string.heat_1);
					break;
				}
				
				((TextView)mMainView.findViewById(R.id.left_temp2)).setText(s);
			}
			

			int index = ((mCommonUpdateView.getAirData(9) & 0xff) << 8)
					| (mCommonUpdateView.getAirData(10) & 0xff);
			if (index != 0) {
				((TextView) mMainView
						.findViewById(R.id.energy_statistics))
						.setText(index + "W");
			}
			index = (mCommonUpdateView.getAirData(11) & 0xff);
			if (index != 0) {
				((TextView) mMainView
						.findViewById(R.id.str_air_quality_sensor))
						.setText(getString(R.string.str_air_quality_sensor)
								+ ":" + index);
			}
		}
	};
	
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				sendCmd(v.getId(), (byte)1);
				break;
			case MotionEvent.ACTION_UP:
				sendCmd(v.getId(), (byte)0);
				break;
			
			}
			return false;
		}
	};
	
	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}





	private final static int[][] CMD_ID = new int[][] {

	{ R.id.power, 0x80 }, 
	{ R.id.mode, 0x40 }, 
	{ R.id.ac_auto, 0x20 }, 
	{ R.id.rear, 0x8 }, 
	{ R.id.ac, 0x2 }, 

	{ R.id.air_fwindow_heat, 0x380 },
	{ R.id.max, 0x4 },
	
	{ R.id.inner_loop, 0x201 },

	{ R.id.wind_minus, 0x104 }, 
	{ R.id.wind_add, 0x108 },

	{ R.id.con_left_temp_up, 0x102 }, 
	{ R.id.con_left_temp_down, 0x101 },



	};
	
	private void sendCanboxInf(int d0, int d1){
		byte[] buf = new byte[] { (byte) 0xa8, 0x2,
				 (byte)d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCmd(int id, byte down) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				mMsgInterface.callBack(0);

				byte[] buf = new byte[] { (byte) 0xc7, 05, 0, 0, 0, 0, 0 };
				if (down == 1) {
					int index = (CMD_ID[i][1] & 0xff00) >> 8;
					buf[index + 2] = (byte) (CMD_ID[i][1] & 0xff);
				}
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);
			}
		}
	}

	public void onClick(View v) {
	}

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();
		super.onResume();

		sendCanboxInfo0x90(0x23);
	}

	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
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
						String cmd = intent
								.getStringExtra(MyCmd.EXTRA_COMMON_CMD);
						if ("ac".equals(cmd)) {
							byte[] buf = intent.getByteArrayExtra("buf");
							if (buf != null) {
								try {
									// updateView(buf);
									mCommonUpdateView
											.postChanged(
													CommonUpdateView.MESSAGE_AIR_CONDITION,
													0, 0, buf);
									
								

								} catch (Exception e) {
									Log.d("aa", "!!!!!!!!" + e);
								}
							}
						}
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

			getActivity().registerReceiver(mReceiver, iFilter);
		}
	}
}
