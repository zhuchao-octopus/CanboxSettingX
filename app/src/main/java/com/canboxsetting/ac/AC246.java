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
public class AC246 extends MyFragment {
	private static final String TAG = "VWMQBAirControlFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private CommonUpdateView mCommonUpdateView;
	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_jinbei_daojun, container,
				false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

		return mMainView;
	}

	private void sendCanboxInfo0xc7(int d0) {
		byte[] buf;

		buf = new byte[] { (byte) 0x71, 0x2, (byte) d0, 1 };

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

		Util.doSleep(200);
		buf[3] = 0;

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private void sendCanboxInfo0x90(int d0) {

		byte[] buf;
		buf = new byte[] { (byte) 0x90, 0x1, (byte) d0 };

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private final static int[][] CMD_ID = new int[][] {

	{ R.id.ac_auto, 0x1 }, { R.id.inner_loop, 0x3 }, { R.id.wind_up1, 0x4 },
			{ R.id.rear, 0x5 }, { R.id.wind_add, 0x6 },
			{ R.id.wind_minus, 0x7 },

			{ R.id.ac, 0x8 },

			{ R.id.mode, 0x9 },

			{ R.id.con_left_temp_up, 0xc }, { R.id.con_left_temp_down, 0xd },

			{ R.id.wind_up_down, 0x13 }, { R.id.rear_temp_hot, 0x29 },
			{ R.id.rear_temp_cold, 0x2a }, };

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
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

	public void onClick(View v) {

		int cmd = getCmd(v.getId());
		sendCanboxInfo0xc7((cmd & 0xff));

	}

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();
		sendCanboxInfo0x90(0x21);
		super.onResume();
	}

	private void updateSelect(int id, int s) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setSelected(s != 0);
		}
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

								mCommonUpdateView.postChanged(
										CommonUpdateView.MESSAGE_AIR_CONDITION,
										0, 0, buf);

								updateSelect(R.id.rear_temp_hot, buf[6] & 0x80);
								updateSelect(R.id.rear_temp_cold, buf[6] & 0x40);

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
