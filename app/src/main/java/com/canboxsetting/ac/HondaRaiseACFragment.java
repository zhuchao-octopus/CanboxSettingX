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
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class HondaRaiseACFragment extends MyFragment {

	private View mMainView;

	private CommonUpdateView mCommonUpdateView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_honda_raise, container, false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		
		if(GlobalDef.getModelId() != 19){
			mMainView.findViewById(R.id.seat_hot_layout).setVisibility(View.GONE);
		}
		
		return mMainView;
	}

	private final static int[][] CMD_ID = new int[][] { { R.id.power, 0x1 },
			{ R.id.ac, 0x17 }, { R.id.ac_auto, 0x15 }, { R.id.rear, 0x14 },
			{ R.id.inner_loop, 0x19 }, { R.id.max, 0x13 }, { R.id.dual, 0x10 },
			{ R.id.wind_minus, 0x9 }, { R.id.wind_add, 0xa },
			{ R.id.mode, 0x0040 },

			{ R.id.con_left_temp_up, 0x3 }, { R.id.con_left_temp_down, 0x2 },
			{ R.id.con_right_temp_up, 0x5 }, { R.id.con_right_temp_down, 0x4 },


			{ R.id.left_seat_heat, 0xb }, 
			{ R.id.right_seat_heat, 0xd },
			{ R.id.left_seat_refrigeration, 0xc },
			{ R.id.right_seat_refrigeration, 0xe },

	};

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
	}

	private void sendCanboxInfo0xC6(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xC6, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0xE0(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xe0, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ac) {
			int i = mCommonUpdateView.getAirData(0);
			if ((i & 0x40) == 0) {
				i = 1;
			} else {
				i = 2;
			}
			sendCanboxInfo0xC6(0xac, i);
		} else if (id == R.id.wind_horizontal1) {
			sendCanboxInfo0xC6(0xac, 0x3);
		} else if (id == R.id.wind_horizontal_down) {
			sendCanboxInfo0xC6(0xac, 0x4);
		} else if (id == R.id.wind_down1) {
			sendCanboxInfo0xC6(0xac, 0x5);
		} else if (id == R.id.wind_up_down) {
			sendCanboxInfo0xC6(0xac, 0x6);
		} else if (id == R.id.wind_add) {
			int w = mCommonUpdateView.getWind();
			w = (w + 1);
			if (w < 0 || w > 7) {
				w = 7;
			}
			sendCanboxInfo0xC6(0xad, w);
		} else if (id == R.id.wind_minus) {
			int w = mCommonUpdateView.getWind();
			w = (w - 1);
			if (w < 0 || w > 7) {
				w = 0;
			}
			sendCanboxInfo0xC6(0xad, w);
		} else {
			int cmd = getCmd(v.getId());

			Util.doSleep(200);
			sendCanboxInfo0xE0((cmd & 0xff), 1);
			Util.doSleep(200);
			sendCanboxInfo0xE0((cmd & 0xff), 0);
		}
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
