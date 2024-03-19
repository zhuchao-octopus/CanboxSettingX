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
import com.car.ui.GlobalDef;
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
public class AC253 extends MyFragment {
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
				.inflate(R.layout.ac_fiat_xinchi, container, false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		
		
		init();
		return mMainView;
	}

	private void init() {

//		mMainView.findViewById(R.id.ac_auto_rear).setVisibility(View.GONE);
//		mMainView.findViewById(R.id.wind_horizontal_down_rear).setVisibility(View.INVISIBLE);
//		mMainView.findViewById(R.id.mode).setVisibility(View.INVISIBLE);
		mMainView.findViewById(R.id.wind_auto_rear).setVisibility(View.INVISIBLE);
	}

	private void sendCanboxInfo0xc7(int d0, int d1) {
		byte[] buf;

		buf = new byte[] { (byte) d0, 0x2, (byte) d1, 1 };

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

		Util.doSleep(200);
		buf[3] = 0;

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {

		byte[] buf;
		buf = new byte[] {  (byte) 0x1f, 1, (byte) d0 };
		

		BroadcastUtil.sendCanboxInfo(getActivity(), buf);

	}

	private final static int[][] CMD_ID = new int[][] {

		{ R.id.power, 0x9500 }, 
		{ R.id.ac, 0x9501 }, 
		{ R.id.inner_loop, 0x9503 }, 
		{ R.id.ac_auto, 0x9504 }, 
		{ R.id.max, 0x9505 }, 
		{ R.id.rear, 0x9506 }, 
		{ R.id.sync, 0x9507 }, 
		

		{ R.id.wind_down1, 0x950a },
		{ R.id.wind_horizontal1, 0x9508 },
		{ R.id.wind_horizontal_down, 0x9509 },
		
		{ R.id.wind_up_down, 0x950b },

		{ R.id.wind_add, 0x950d},
		{ R.id.wind_minus, 0x950c },
		

		{ R.id.con_left_temp_up, 0x950f },
		{ R.id.con_left_temp_down, 0x950e },



		{ R.id.con_right_temp_up, 0x9511 },
		{ R.id.con_right_temp_down, 0x9510 },
		

		{ R.id.left_seat_heat, 0x9520 },
		{ R.id.right_seat_heat, 0x9522 },


		{ R.id.left_seat_refrigeration, 0x9521 },
		{ R.id.right_seat_refrigeration, 0x9523 },
		

		{ R.id.wheel, 0x9524 },

		{ R.id.rear_lock, 0x961f },
		
		{ R.id.power_rear, 0x9600 },
		{ R.id.ac_auto_rear, 0x9604 },
		{ R.id.con_left_temp_rear_up, 0x960e },
		{ R.id.con_left_temp_rear_down, 0x960f},

		{ R.id.wind_add_rear, 0x960d },
		{ R.id.wind_minus_rear, 0x960c },
		
		{ R.id.wind_horizontal1_rear, 0x9608 },
		{ R.id.wind_down1_rear, 0x960a },
		{ R.id.wind_horizontal_down_rear, 0x9609 },


		
		
		

		






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
			int idCmd = (cmd & 0xff00) >> 8;
			if (idCmd == 0) {
				return ;
			}
			sendCanboxInfo0xc7(idCmd, (cmd & 0xff));
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
		sendCanboxInfo0x90(0x5);
		Util.doSleep(20);
		sendCanboxInfo0x90(0x6);
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
