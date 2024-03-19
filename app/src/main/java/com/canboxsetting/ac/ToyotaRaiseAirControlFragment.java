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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class ToyotaRaiseAirControlFragment extends MyFragment {
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
		mMainView = inflater
				.inflate(R.layout.ac_toyota_raise, container, false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		
		mMainView.findViewById(R.id.ac_auto_rear).setVisibility(View.GONE);
		return mMainView;
	}

	private void sendCanboxInfo0xc7(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xc7, 0x6, 0, 0, 0, 0, 0, 0 };
//		int index = (d1 & 0xff00) >> 8;
		buf[2 + d0] = (byte) (d1 & 0xff);
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		Util.doSleep(200);
		buf[2] = 0;
		buf[3] = 0;
		buf[4] = 0;
		buf[5] = 0;
		buf[6] = 0;
		buf[7] = 0;
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private final static int[][] CMD_ID = new int[][] {
		{ R.id.power, 0x0080 }, 
		{ R.id.ac, 0x0002 }, 
		{ R.id.ac_auto, 0x0020 }, 
		{ R.id.rear, 0x0104 }, 
		{ R.id.inner_loop, 0x0201 }, 
		{ R.id.ac_max, 0x0010 }, 
		{ R.id.max, 0x0010 }, 
		{ R.id.dual, 0x0108 }, 
		{ R.id.wind_minus, 0x0101 },
		{ R.id.wind_add, 0x0102 },
		{ R.id.mode, 0x0040 },
		

		{ R.id.con_left_temp_up, 0x302 },
		{ R.id.con_left_temp_down, 0x301 },
		{ R.id.con_right_temp_up, 0x402 },
		{ R.id.con_right_temp_down, 0x401 },

		{ R.id.swing, 0x0140 },
		{ R.id.pollen_clear, 0x0120 },
		{ R.id.windshield_deicing, 0x0180 },
			

		{ R.id.left_seat_heat, 0x210 }, 
		{ R.id.right_seat_heat, 0x204 },
		{ R.id.left_seat_refrigeration, 0x240 },
		{ R.id.right_seat_refrigeration, 0x220 },



		{ R.id.mode_rear, 0x508 },
		{ R.id.con_right_temp_rear_up, 0x420 },
		{ R.id.con_right_temp_rear_down, 0x410 },
		
		{ R.id.power_rear, 0x540 },


		{ R.id.wind_minus_rear, 0x510 }, 
		{ R.id.wind_add_rear, 0x520 },
		
//		{ R.id.ac_auto_rear, 0x504 },
		{ R.id.wind_auto_rear, 0x504 },
		
		{ R.id.con_left_temp_rear_up, 0x502 },
		{ R.id.con_left_temp_rear_down, 0x501 },
		
	};


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
		
		int id = v.getId();
		if (id == R.id.air_rear) {
			showRear(true);
		} else if (id == R.id.air_front) {
			showRear(false);
		} else {
			
		
		int cmd = getCmd(v.getId());


		sendCanboxInfo0xc7((cmd & 0xff00) >> 8, (cmd & 0xff));
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
		sendCanboxInfo0x90(0x28);
		Util.doSleep(200);
		sendCanboxInfo0x90(0x58);
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

								mCommonUpdateView.postChanged(
										CommonUpdateView.MESSAGE_AIR_CONDITION,
										0, 0, buf);

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
