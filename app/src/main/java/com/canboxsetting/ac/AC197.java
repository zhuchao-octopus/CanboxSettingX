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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class AC197 extends MyFragment {
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
		mMainView = inflater.inflate(R.layout.ac_ford_bnr, container, false);
		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
		invalidButton(R.id.ac_auto_rear);
		invalidButton(R.id.wind_horizontal1_rear);
		invalidButton(R.id.wind_down1_rear);
		invalidButton(R.id.wind_horizontal_down_rear);
		invalidButton(R.id.wind_auto_rear);
		init();
		return mMainView;
	}

	private View mMainView;

	private void invalidButton(int id) {
		ImageView iv = (ImageView) mMainView.findViewById(id);
		if (iv != null) {
			iv.setImageDrawable(null);
			iv.setClickable(false);
		}
	}

	private void init(){
		for (int i = 0; i < CMD_ID.length; ++i) {
			View v = mMainView.findViewById(CMD_ID[i][0]);
			if (v != null) {				
				v.setOnTouchListener(mOnTouchListener);
			}
		}
	}
	
	View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				sendCmd(v.getId(), (byte)1);
				break;
			case MotionEvent.ACTION_UP:
				sendCmd(v.getId(), (byte)0);
				break;
			
			}
			return false;
		}
	};
	
	private void sendCanboxKey0x82(int cmd) {
		sendCanboxInfo((cmd & 0xff00) >> 8, cmd & 0xff);
		Util.doSleep(200);
		sendCanboxInfo((cmd & 0xff00) >> 8, 0);
	}

	private void sendCanboxInfo(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xc6, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}


		private final static int[][] CMD_ID = new int[][] { 
			{ R.id.power, 0xac01 },
				{ R.id.ac, 0xac02 }, 
				{ R.id.inner_loop, 0x0ac03 },
				{ R.id.ac_max, 0xac04 },
				
				{ R.id.max, 0xac3e },
				
				{ R.id.front, 0xac05 },
				{ R.id.rear, 0xac06 },
				
				{ R.id.dual, 0xac18 }, 
				{ R.id.wind_minus, 0xac36 },
				{ R.id.wind_add, 0xac35 },

				{ R.id.ac_auto, 0xac17 },


				{ R.id.con_left_temp_up, 0xac31 },
				{ R.id.con_left_temp_down, 0xac32 },
				{ R.id.con_right_temp_up, 0xac33 },
				{ R.id.con_right_temp_down, 0xac34 },


				{ R.id.wind_horizontal1, 0xac38 },
				{ R.id.wind_down1, 0xac39 },
				{ R.id.wind_up1, 0xac37 },


				

				{ R.id.left_seat_heat, 0xac3a },
				{ R.id.right_seat_heat, 0xac3b },
				{ R.id.left_seat_refrigeration, 0xac3c },
				{ R.id.right_seat_refrigeration, 0xac3d },
				
				

				{ R.id.off_rear, 0xac11 },

				{ R.id.rear_lock, 0xac12 },


				{ R.id.wind_minus_rear, 0xac15 },
				{ R.id.wind_add_rear, 0xac16 },

				{ R.id.con_left_temp_rear_up, 0xac14 },
				{ R.id.con_left_temp_rear_down, 0xac13 },

		};




		private void sendCmd(int id, byte down) {
			for (int i = 0; i < CMD_ID.length; ++i) {
				if (CMD_ID[i][0] == id) {
					mMsgInterface.callBack(0);
					int cmd = CMD_ID[i][1];
					if (down == 1){
						sendCanboxInfo((cmd & 0xff00) >> 8, cmd & 0xff);
					} else {
						sendCanboxInfo((cmd & 0xff00) >> 8, 0);
					}
				}
			}
		}
		
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
		sendCanboxInfo0x90(0x21);
		Util.doSleep(10);
		sendCanboxInfo0x90(0x6b);
		super.onResume();
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	private void updateRearTemp(byte b) {
		String s = "";
		switch ((b & 0xc0) >> 6) {
		case 0:
			s = getString(R.string.air_manual_normal);
			break;
		case 1:
			s = getString(R.string.air_manual_cryogen);
			s += " " + (b&0xf);
			break;
		case 2:
			s = getString(R.string.air_manual_heat);
			s += " " + (b&0xf);
			break;
		}
		
		
		((TextView) mMainView.findViewById(R.id.reartemp2)).setText(s);
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
							
							if (buf.length > 10) {
								updateRearTemp(buf[10]);
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
