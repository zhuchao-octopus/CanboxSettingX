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
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class NissanRaiseACFragment extends MyFragment {

	private View mMainView;

	private CommonUpdateView mCommonUpdateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_nissan_raise, container, false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		return mMainView;
	}

	private final static int[][] CMD_ID = new int[][] { { R.id.power, 0x1 },
			{ R.id.ac, 0x2 }, { R.id.inner_loop_out, 0x8 },
			{ R.id.inner_loop_in, 0x4 }, { R.id.max, 0x10 },
			{ R.id.ac_auto, 0x20 }, { R.id.mode, 0x40 }, { R.id.power, 0x80 },

			{ R.id.dual, 0x180 }, { R.id.rear, 0x140 },

			{ R.id.wind_minus, 0x101 }, { R.id.wind_add, 0x102 },

			{ R.id.wind_up1, 0x201 }, { R.id.wind_horizontal1, 0x202 },
			{ R.id.wind_down1, 0x208 },

			{ R.id.con_left_temp_up, 0x302 },
			{ R.id.con_left_temp_down, 0x301 },
			{ R.id.con_right_temp_up, 0x402 },
			{ R.id.con_right_temp_down, 0x401 },

	};

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
	}

	private void sendCanboxInfo0xE1(int d0, int index) {
		byte[] buf = new byte[] { (byte) 0xe1, 0x5, 0, 0, 0, 0, 0 };
		buf[index + 2] = (byte) d0;
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public void onClick(View v) {
		int cmd = getCmd(v.getId());
		sendCanboxInfo0xE1((cmd & 0xff), (cmd & 0xff00) >> 8);
		Util.doSleep(200);
		sendCanboxInfo0xE1(0, 0);
	}

	private void updateSelect(int id, int s) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setSelected(s != 0);
		}
	}

	private void updateView(byte[] buf) {

		// if ((buf[0] & 0xff) == 0x21) {
		// if ((buf[6] & 0x40) != 0) {
		// mShow = true;
		// } else if (mShow) {
		// getActivity().finish();
		// }
		// }
		if ((buf[11] & 0x10) != 0) {
			((TextView) mMainView.findViewById(R.id.rearstat))
					.setText(R.string.rear_seat_air_conditioning_control_open_auto);
		} else {
			((TextView) mMainView.findViewById(R.id.rearstat))
					.setText(R.string.rear_seat_air_conditioning_control_close_auto);
		}

		updateSelect(R.id.wind_up, (buf[11] & 0x80));
		updateSelect(R.id.wind_horizontal, (buf[11] & 0x40));
		updateSelect(R.id.wind_down, (buf[11] & 0x20));

		int speed = buf[11] & 0xf;

		((TextView) mMainView.findViewById(R.id.rearfanspeed))
				.setText(getString(R.string.rear_wind_speed) + " " + speed);
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
						byte[] buf = intent.getByteArrayExtra("buf");
						if (buf != null) {
							if ("ac".equals(cmd)) {

								mCommonUpdateView.postChanged(
										CommonUpdateView.MESSAGE_AIR_CONDITION,
										0, 0, buf);

								try {
									updateView(buf);
								} catch (Exception e) {

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
