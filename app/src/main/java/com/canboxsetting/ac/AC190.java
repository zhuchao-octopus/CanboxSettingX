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
import android.view.View;
import android.view.ViewGroup;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class AC190 extends MyFragment {
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

		mMainView = inflater.inflate(R.layout.ac_lufeng_od,
				container, false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		return mMainView;
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x95(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x84, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void showRear(boolean show) {
		if (show) {
			mMainView.findViewById(R.id.ac_layout_rear).setVisibility(
					View.VISIBLE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.GONE);
		} else {

			mMainView.findViewById(R.id.ac_layout_rear)
					.setVisibility(View.GONE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.VISIBLE);
		}
	}
	
	byte[] AirBuf = new byte[] { (byte) 0xc7, 0x6, 
		0, 
		0,
		0,
		0,
		0,
		0 };
	
	private void sendKeyEx(int key) {
		AirBuf[2] = (byte) ((key & 0xff) >> 0);
//		AirBuf[3] = (byte) ((key & 0xff00) >> 8);
		AirBuf[4] = (byte) ((key & 0xff0000) >> 16);
		AirBuf[5] = (byte) ((key & 0xff000000) >> 24);

		BroadcastUtil.sendCanboxInfo(getActivity(), AirBuf);

	}
	
	private void sendKey(int key){
		sendKeyEx(key);
		Util.doSleep(200);
		sendKeyEx(0);
	}

	private final static int[][] CMD_ID = new int[][] {

		{ R.id.off, 0x80 },
		{ R.id.ac, 0x2 },
		{ R.id.ac_auto, 0x20 },
		{ R.id.max, 0x10 },
		{ R.id.rear, 0x40000 },
		{ R.id.inner_loop_out, 0x8 },
		{ R.id.inner_loop_in, 0x4 },

		{ R.id.wind_horizontal1, 0x10000 },
		{ R.id.wind_horizontal_down, 0x20000 },
		{ R.id.wind_down1, 0x30000 },
		{ R.id.wind_up_down, 0x400000 },
		{ R.id.wind_up1, 0x500000 },
		
//		{ R.id.wind_minus, 0x1d },
//		{ R.id.wind_add, 0x1c },
		{ R.id.con_left_temp_up, 0x2000000 },
		{ R.id.con_left_temp_down, 0x1000000 },
	};

	private void sendCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				sendKey((CMD_ID[i][1]));
			}
		}
	}

	private int mWindStep = -1;
	private int mTempStep = -1;

	private void sendWind(boolean add) {
		mWindStep = (byte) ((mCommonUpdateView
				.getWind() & 0xF) << 4);
		if (add) {
			mWindStep = (mWindStep + (0x1 << 4));

		} else {
			mWindStep = (mWindStep - (0x1 << 4));

		}
		AirBuf[3] = (byte) mWindStep;
		BroadcastUtil.sendCanboxInfo(getActivity(), AirBuf);
	}

	private void sendTemp(boolean add) {
			
			int temp = mCommonUpdateView
					.getTempLeft() ;
			
			temp = temp - 32 + 0x10;
			if (temp <= 0x13) {
				temp = 0x13;
			} else if (temp >= 0x31) {
				temp = 0x31;
			}
			mTempStep = (byte) (temp);
		
		
		if (add) {
			mTempStep = (byte) (mTempStep + 1);
		} else {
			
			mTempStep = (byte) (mTempStep - 1);
			if (mTempStep <= 0x13) {
				mTempStep = 0xf;
			}
		}

		AirBuf[5] = (byte) (mTempStep<<2);
		BroadcastUtil.sendCanboxInfo(getActivity(), AirBuf);
	}
	public void onClick(View v) {
		int id = v.getId();
//		if (id == R.id.con_left_temp_up) {
//			sendTemp(true);
//		} else if (id == R.id.con_left_temp_down) {
//			sendTemp(false);
//		} else
			if (id == R.id.wind_add) {
			sendWind(true);
		} else if (id == R.id.wind_minus) {
			sendWind(false);
		} else {
			sendCmd(v.getId());
		}
	}

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
		mWindStep = -1;
		mTempStep = -1;
	}

	@Override
	public void onResume() {
		registerListener();
		super.onResume();

		sendCanboxInfo0x90(0x24);
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
															
									Log.d("ffck", "w:"+(buf[1]&0xf));
								} catch (Exception e) {
									Log.d("aa", "!!!!!!!!" + e);
								}
							}
						} else {
							
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
