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

/**
 * This activity plays a video from a specified URI.
 */
public class AC174 extends MyFragment {
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

		mMainView = inflater.inflate(R.layout.ac_spirior_daojun, container,
				false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

		return mMainView;
	}


	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x95(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xA8, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendKey(int key) {
		sendCanboxInfo0x95(key, 1);		
	}

	private final static int[][] CMD_ID = new int[][] {


		{ R.id.ac, 0x02 }, 		
		{ R.id.ac_auto, 0x04 },
		
		{ R.id.sync, 0x0F },
		{ R.id.inner_loop, 0x07 },	
		{ R.id.rear, 0x06 },

		{ R.id.wind_add, 0x0b01 },
		{ R.id.wind_minus, 0x0b02 }, 

		{ R.id.power, 0x710a },
		
		{ R.id.con_left_temp_up, 0x0c01 },
		{ R.id.con_left_temp_down, 0x0c02 },


		{ R.id.con_right_temp_up, 0x0d01 },
		{ R.id.con_right_temp_down, 0x0d02 },
		


		{ R.id.wind_up1, 0x05ff },
		{ R.id.wind_horizontal1, 0x7122 },

		{ R.id.wind_down1, 0x0Aff },




	};


	public void onClick(View v) {
		int id = v.getId();

		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				if ((CMD_ID[i][1] & 0xff00) == 0) {
					byte[] buf = new byte[] { 0x2, 0x3b, 
							(byte) (CMD_ID[i][1] & 0xff), 1 };
					if (v.isSelected()) {
						buf[3] = 0;
					}
					BroadcastUtil.sendCanboxInfo(getActivity(), buf);
				} else {
					byte[] buf = new byte[] { 0x2, 0x3b, 
							(byte) ((CMD_ID[i][1] & 0xff00) >> 8),
							(byte) (CMD_ID[i][1] & 0xff) };
					BroadcastUtil.sendCanboxInfo(getActivity(), buf);
				}
			}
		}
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

		sendCanboxInfo0x90(0x21);
	}

	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	private int power = 0;

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
									power = buf[0] & 0x80;
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
