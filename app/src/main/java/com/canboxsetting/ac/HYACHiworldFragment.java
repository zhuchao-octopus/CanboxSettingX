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
public class HYACHiworldFragment extends MyFragment {
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

		mMainView = inflater.inflate(R.layout.ac_hy_hiworld, container,
				false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		return mMainView;
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x95(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x02, 0x3d, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendKey(int key) {
		sendCanboxInfo0x95(key, 1);
		Util.doSleep(200);
		sendCanboxInfo0x95(key, 0);
	}

	private final static int[][] CMD_ID = new int[][] {

	{ R.id.power, 0x1 }, 
	{ R.id.ac, 0x2 }, 
	{ R.id.sync, 0x3 },
	{ R.id.ac_auto, 0x4 },
	{ R.id.front, 0x5 },

	{ R.id.inner_loop, 0x7 },


	{ R.id.aq, 0x19 },



			{ R.id.wind_minus, 0xc }, 
			{ R.id.wind_add, 0xb },


			{ R.id.wind_up1, 0x8 },
			{ R.id.wind_horizontal1, 0x1a },
			{ R.id.wind_horizontal_down, 0x1b },
			{ R.id.wind_down1, 0x1d },
			{ R.id.wind_up_down, 0x1c },

			
			{ R.id.con_left_temp_up, 0xd },
			{ R.id.con_left_temp_down, 0xe },
			{ R.id.con_right_temp_up, 0xf },
			{ R.id.con_right_temp_down, 0x10 },

	};

	private void sendCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				
					sendKey((CMD_ID[i][1] & 0xff));
				
			}
		}
	}

	public void onClick(View v) {
		sendCmd(v.getId());
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
