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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class AC203 extends MyFragment {
	private static final String TAG = "VWMQBAirControlFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private CommonUpdateView mCommonUpdateView;
	private View invalidButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_byd_f6_daojun, container, false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
//		((TextView)mMainView.findViewById(R.id.coldest)).setText("");
		init();
		return mMainView;
	}

	private View mMainView;



	private void init(){
		for (int i = 0; i < CMD_ID.length; ++i) {
			View v = mMainView.findViewById(CMD_ID[i][0]);
			if (v != null) {				
				v.setOnTouchListener(mOnTouchListener);
			}
		}
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			sendCmd(msg.arg1, (byte)1);
			mHandler.removeMessages(0);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(0, msg.arg1, 0),
					400);

		}
	};
	
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				sendCmd(v.getId(), (byte)1);
				mHandler.removeMessages(0);
				mHandler.sendMessageDelayed(
						mHandler.obtainMessage(0, v.getId(), 0), 100);
				
				break;
			case MotionEvent.ACTION_UP:
				sendCmd(v.getId(), (byte)0);
				mHandler.removeMessages(0);
				break;
			
			}
			return false;
		}
	};
	


	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x71, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}


		private final static int[][] CMD_ID = new int[][] { 

			{ R.id.ac_auto, 0x1 },
			{ R.id.dual, 0x2 }, 
			{ R.id.inner_loop, 0x3 },
			{ R.id.max, 0x4 },
			{ R.id.rear, 0x5 },

			{ R.id.wind_minus, 0x7 },
			{ R.id.wind_add, 0x6 },
			{ R.id.ac, 0x8 }, 
			{ R.id.off, 0x9 },
			{ R.id.mode, 0xb }, 

				



				{ R.id.con_left_temp_up, 0xc },
				{ R.id.con_left_temp_down, 0xd },
				{ R.id.con_right_temp_up, 0xe },
				{ R.id.con_right_temp_down, 0xf },


//				{ R.id.wind_horizontal1, 0x21 },
//				{ R.id.wind_down1, 0x22 },
//				{ R.id.wind_horizontal_down, 0x23 },
				{ R.id.wind_up1, 0x23 },


				

				

		};




		private void sendCmd(int id, byte down) {
			for (int i = 0; i < CMD_ID.length; ++i) {
				if (CMD_ID[i][0] == id) {
					mMsgInterface.callBack(0);
					int cmd  = CMD_ID[i][1];				
					sendCanboxInfo(cmd & 0xff, down);					
				}
			}
		}
		


	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.air_rear) {
			showRear(true);
		} else if (id == R.id.air_front) {

			showRear(false);
		} else {
//			int cmd = getCmd(id);
//			if (cmd != 0) {
//				sendCanboxKey0x82(cmd);
//			}

		}
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

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();
		byte[] buf = new byte[] { (byte) 0x90, 1, 0x2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
