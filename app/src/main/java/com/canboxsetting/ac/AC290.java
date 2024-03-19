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

import com.canboxsetting.CanAirControlActivity;
import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.common.view.VerticalSeekBar;

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
import android.os.SystemClock;
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
public class AC290 extends MyFragment implements
		VerticalSeekBar.OnSeekBarChangeListener {
	private static final String TAG = "AC290";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private View mMainView;

	private CommonUpdateView mCommonUpdateView;

	VerticalSeekBar VSeekBarTemp;
	VerticalSeekBar VSeekBarWind;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMainView = inflater.inflate(R.layout.ac_beiqis3_other, container,
				false);

		mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

		View v;
		v = mMainView.findViewById(R.id.seekbar_wind);
		if (v != null) {
			VSeekBarWind = (VerticalSeekBar) v;
			VSeekBarWind.setOnSeekBarChangeListener(this);
		}

		v = mMainView.findViewById(R.id.seekbar_temp);
		if (v != null) {
			VSeekBarTemp = (VerticalSeekBar) v;
			VSeekBarTemp.setOnSeekBarChangeListener(this);
		}

		mMsgInterface.callBack(CanAirControlActivity.AC_ISNOT_FINISH);//
		return mMainView;
	}

	private long mLongSeekWind = 0;
	private long mLongSeekTemp = 0;
	public void onProgressChanged(VerticalSeekBar VerticalSeekBar,
			int progress, boolean fromUser) {
		if (fromUser) {
            int id = VerticalSeekBar.getId();
            if (id == R.id.seekbar_wind) {
                mLongSeekWind = SystemClock.uptimeMillis();
                sendCanboxInfo0x95(0x20, progress);
            } else if (id == R.id.seekbar_temp) {
                mLongSeekTemp = SystemClock.uptimeMillis();
                sendCanboxInfo0x95(0x21, progress);
            }
		}
	}

	public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {

	}

	public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {

	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x95(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xa8, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendKey(int key) {
		sendCanboxInfo0x95(key, 1);
		Util.doSleep(200);
		sendCanboxInfo0x95(key, 0);
	}

	private final static int[][] CMD_ID = new int[][] {

	{ R.id.off2, 0x00 }, { R.id.ac, 0x1 }, 
	{ R.id.ac_auto, 0x2 },
			{ R.id.dual, 0x3 },
			
			{ R.id.inner_loop_in, 0x4 },
			{ R.id.inner_loop_out, 0x5 },

			{ R.id.max, 0x12 }, { R.id.ac_max, 0x11 }, { R.id.front, 0x6 },

			{ R.id.wind_horizontal1, 0x7 }, { R.id.wind_down1, 0x9 },
			{ R.id.wind_horizontal_down, 0x8 }, { R.id.wind_up_down, 0xa },

			{ R.id.wind_minus, 0xc }, { R.id.wind_add, 0xb },

			{ R.id.con_left_temp_up, 0xd }, { R.id.con_left_temp_down, 0xe },
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
		if (v.getId() == R.id.inner_loop2){
			if ((mAirData0 & 0x20) != 0) {// inner loop,loop a
				sendKey(0x5);
			} else {
				sendKey(0x4);
			}
		} else {
			sendCmd(v.getId());
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

	private final static int MSG_DELAY_UPDATE_WIND = 1;
	private final static int MSG_DELAY_UPDATE_TEMP = 2;
	private final static int TIME_DELAY_UPDATE_SEEKBAR = 300;
	private final static int TIME_DELAY_UPDATE_SEEKBAR_CLICK = 500;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_DELAY_UPDATE_WIND:
				VSeekBarWind.setProgress(msg.arg1);
				break;
			case MSG_DELAY_UPDATE_TEMP:
				VSeekBarTemp.setProgress(msg.arg1);
				break;
			}
		}
	};
	
	private void prepareUpdateWind(int v) {
		if ((SystemClock.uptimeMillis() - mLongSeekWind) > TIME_DELAY_UPDATE_SEEKBAR_CLICK) {
			VSeekBarWind.setProgress(v);
		} else {
			mHandler.removeMessages(MSG_DELAY_UPDATE_WIND);
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MSG_DELAY_UPDATE_WIND, v, 0),
					TIME_DELAY_UPDATE_SEEKBAR);
		}
	}
	
	private void prepareUpdateTemp(int v){
		if ((SystemClock.uptimeMillis() - mLongSeekTemp) > TIME_DELAY_UPDATE_SEEKBAR_CLICK) {
			VSeekBarTemp.setProgress(v);
		} else {
			mHandler.removeMessages(MSG_DELAY_UPDATE_TEMP);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_DELAY_UPDATE_TEMP, v, 0), TIME_DELAY_UPDATE_SEEKBAR);
		}
	}
	
	private byte mAirData0 = 0;
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

									int temp = buf[2] & 0xff;
									int wind = buf[1] & 0x0f;

									if (wind >= 0 && wind <= 6) {
										prepareUpdateWind(wind);
										
									//	VSeekBarWind.setProgress(wind);
										if (wind == 0) {
											updateSelect(R.id.off2, 1);
										} else {
											updateSelect(R.id.off2, 0);
										}
									} else {
										updateSelect(R.id.off2, 1);
									}

									if (temp >= 0 && temp <= 6) {
										//VSeekBarTemp.setProgress(temp);
										prepareUpdateTemp(temp);
									} else if (temp >= 36 && temp <= 41) { //test
										temp -= 35;
										prepareUpdateTemp(temp);
									}
									
									mAirData0 = buf[0];
									if ((mAirData0 & 0x20) != 0) {// inner loop,loop a
										((ImageView) mMainView.findViewById(R.id.inner_loop2)).setImageResource(R.drawable.ac_290_inner);
									} else {
										((ImageView) mMainView.findViewById(R.id.inner_loop2)).setImageResource(R.drawable.ac_290_out);
									}
									
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
