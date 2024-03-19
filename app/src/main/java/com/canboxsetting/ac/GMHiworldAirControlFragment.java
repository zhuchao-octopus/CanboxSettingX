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
public class GMHiworldAirControlFragment extends MyFragment {
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
		mMainView = inflater.inflate(R.layout.ac_gm_hiworld, container, false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		mMainView.findViewById(R.id.power_rear).setVisibility(View.INVISIBLE);
		mMainView.findViewById(R.id.ac_auto_rear).setVisibility(View.INVISIBLE);
		return mMainView;
	}

	private void sendCanboxInfo0x82(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x82, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxKey0x82(int d0) {
		sendCanboxInfo0x82(0x7, d0);
	}

	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { 0x2, (byte) 0x3b, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { 0x3, (byte) 0x60, 0x5, 1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private final static int[][] CMD_ID = new int[][] { 
		{ R.id.power, 0x01 },
			{ R.id.ac, 0x02 }, 			
			{ R.id.ac_auto, 0x04 },
			{ R.id.inner_loop, 0x07 },

			{ R.id.sync, 0x0f }, 
//			{ R.id.max, 0xff05 }, 	
			
			{ R.id.wind_horizontal1, 0xff09 },
			{ R.id.wind_down1, 0xff0a },
			{ R.id.wind_up1, 0xff05 },
			{ R.id.wind_horizontal_down, 0xff21 },
			{ R.id.wind_up_down, 0xff22 },
			
			

			{ R.id.wind_minus, 0x020b },
			{ R.id.wind_add, 0x010b },

			{ R.id.con_left_temp_up, 0x010c },
			{ R.id.con_left_temp_down, 0x020c },
			{ R.id.con_right_temp_up, 0x010d },
			{ R.id.con_right_temp_down, 0x020d },
			
			

			{ R.id.con_left_temp_rear_up, 0x0116 },
			{ R.id.con_left_temp_rear_down, 0x0216 },
			
			{ R.id.wind_horizontal1_rear, 0xff14 },
			{ R.id.wind_down1_rear, 0xff12 },
			{ R.id.wind_horizontal_down_rear, 0xff13 },
			
			{ R.id.ac_auto_rear, 0xff11 },
			

			{ R.id.wind_minus_rear, 0x0215 },
			{ R.id.wind_add_rear, 0x0115 },
			

			{ R.id.inner_loop_auto, 0x0207 },

//			{ R.id.rear, 0x02 },
//			{ R.id.max, 0xc },
//			{ R.id.dual, 0xd }, 

//			{ R.id.left_seat_heat, 0x16 },
//			{ R.id.right_seat_heat, 0x18 },
//			{ R.id.left_seat_refrigeration, 0x17 },
//			{ R.id.right_seat_refrigeration, 0x19 },
	};

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
	}



	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.air_rear) {
			showRear(true);
		} else if (id == R.id.air_front) {

			showRear(false);
		} else {
			int cmd = getCmd(id);
			if ((cmd & 0xff00) == 0) {

				int s = v.isSelected()?0:1;
				if (R.id.inner_loop == id){
					if (mCommonUpdateView.getLoopInner() == 0){
						s = 0;
					} 
//					else if (mCommonUpdateView.getLoopInner() != 0){
//						s = 2;
//					} 
					else {
						s = 1;
					}
				}				

				sendCanboxInfo(cmd & 0xff, s);
			} else {
				sendCanboxInfo(cmd & 0xff, (cmd & 0xff00) >> 8);
			}
		}
	}

	private void showRear(boolean show) {
		if (show) {
			mMainView.findViewById(R.id.ac_layout_rear).setVisibility(
					View.VISIBLE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.GONE);
		} else {

			mMainView.findViewById(R.id.ac_layout_rear).setVisibility(
					View.GONE);
			mMainView.findViewById(R.id.ac_layout_front).setVisibility(
					View.VISIBLE);
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
		sendCanboxInfo0x90(0x31);
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
