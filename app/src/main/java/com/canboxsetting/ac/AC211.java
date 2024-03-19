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
public class AC211 extends MyFragment {
	private static final String TAG = "VWMQBAirControlFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.ac_vw_mqb_bnr, container,
				false);

		return mMainView;
	}

	private void sendCanboxInfo0xc6(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xc6, 0x2, (byte) d0, (byte) d1, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendCanboxInfo0xe8(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xe0, 0x2, (byte) d0, (byte) d1, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendKey(int key){
		sendCanboxInfo0xe8(key, 1);
		Util.doSleep(200);
		sendCanboxInfo0xe8(key, 0);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private final static int[][] CMD_ID = new int[][] {
		{ R.id.ac_profile, 0xb100 },
		{ R.id.icon_power, 0x1b2ff },
		

		{ R.id.con_left_temp_up, 0x3 },
		{ R.id.con_left_temp_down, 0x2 },
		{ R.id.con_right_temp_up, 0x5 },
		{ R.id.con_right_temp_down, 0x4 },

		{ R.id.mode, 0x7 }, 
		{ R.id.wind_minus, 0x9 }, 
		{ R.id.wind_add, 0xa },
		
		{ R.id.con_seathotleft, 0xb },
		{ R.id.con_seathotright, 0xd },
		

		{ R.id.air_title_sync, 0x10 }, 
		
			{ R.id.air_title_ce_max, 0x13 },
			{ R.id.rear, 0x14 }, 
			{ R.id.air_title_ce_rear_lock, 0x2a },
//			{ R.id.air_title_ce_aqs, 0x1b000 },
			{ R.id.air_title_ce_ac_1, 0x17 },
			{ R.id.air_title_ce_inner_loop, 0x19 },
			{ R.id.air_title_ce_auto_large, 0x15 },
			{ R.id.air_title_ce_ac_max, 0x18 }, 

			
//			{ R.id.canbus21_mode1, 0x1b4ff }, 
//			{ R.id.canbus21_mode3, 0x1b5ff },
//			{ R.id.canbus21_mode5, 0x1b6ff }, 

			


	};

	private int mWindStep = 0;
	private int mInner = 0;
	private int mSeatHeatLeft = 0;
	private int mSeatHeatRight = 0;

	private int getCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				return (CMD_ID[i][1] & 0xffffff);
			}
		}
		return 0;
	}

	public void onClick(View v) {
		int cmd = getCmd(v.getId());
		

		
//		else if (v.getId() == R.id.air_title_ce_inner_loop) {
//			if (mInner == 0) {
//				mInner = 1;
//			} else {
//				mInner = 0;
//			}
//			cmd |= mInner;
//		} else if (v.getId() == R.id.con_seathotright) {
//			mSeatHeatRight = (mSeatHeatRight + 1) % 4;
//			cmd |= mSeatHeatRight;
//		} else if (v.getId() == R.id.con_seathotleft) {
//			mSeatHeatLeft = (mSeatHeatLeft + 1) % 4;
//			cmd |= mSeatHeatLeft;
//		} else {
//
//			if ((cmd & 0x10000) != 0) {
//				cmd &= ~0xff;
//				if (!v.isSelected()) {
//					cmd |= 0x1;
//				}
//			} else if ((cmd & 0xff00) == 0xbb00) {
//				if (v.isSelected()) {
//					cmd &= ~0xff;
//				}
//			} else {
//
//			}
//		}
		
		if ((cmd & 0xff00) != 0) {
			if (v.getId() == R.id.ac_profile) {
				mACProfileLevel = (mACProfileLevel + 1) % 3;
				cmd |= mACProfileLevel;
			} else {
				if ((cmd & 0x10000) != 0) {
					cmd &= ~0xff;
					if (!v.isSelected()) {
						cmd |= 0x1;
					}
				} else if ((cmd & 0xff00) == 0xbb00) {
					if (v.isSelected()) {
						cmd &= ~0xff;
					}
				} else {

				}
			}
			
			sendCanboxInfo0xc6((cmd & 0xff00) >> 8, (cmd & 0xff));
		} else {

			sendKey((cmd & 0xff));
		}

	}

	private void updateSelect(int id, int s) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setSelected(s != 0);
		}
	}

	private int mACProfileLevel = 0;

	private void setACProfile(int level) {
		TextView tv = (TextView) mMainView.findViewById(R.id.ac_profile);
		if (tv != null) {
			String[] array = getResources().getStringArray(
					R.array.air_con_profile_entries);

			if (level >= 0 && level < array.length) {
				String s = getString(R.string.ac_profile) + ": " + array[level];
				tv.setText(s);
			}

		}
		mACProfileLevel = level;
	}

	private void setSpeed(int speed) {

		for (int i = 0; i < 7; ++i) {
			View v = mMainView.findViewById(R.id.point0 + i);
			if (v != null) {
				if (i < speed) {
					v.setSelected(true);
				} else {
					v.setSelected(false);
				}
			}
		}
		mWindStep = speed;
	}

	private void setLoop(int loop) {

		ImageView v = (ImageView) mMainView
				.findViewById(R.id.air_title_ce_inner_loop);

		if (v != null) {
			if (loop == 0) {
				v.getDrawable().setLevel(0);
			} else {
				v.getDrawable().setLevel(1);
			}
		}
		mInner = loop;
	}

	private void setSeatheat(int id, int level) {

		ImageButton v = (ImageButton) mMainView.findViewById(id);
		int drawable;
		if (v != null) {
			if (id == R.id.con_seathotleft) {
				drawable = R.drawable.img_air_seathotleft0;
				switch (level) {
				case 1:
					drawable = R.drawable.img_air_seathotleft1;
					break;
				case 2:
					drawable = R.drawable.img_air_seathotleft2;
					break;
				case 3:
					drawable = R.drawable.img_air_seathotleft3;
					break;
				}
			} else {
				drawable = R.drawable.img_air_seathotright0;
				switch (level) {
				case 1:
					drawable = R.drawable.img_air_seathotright1;
					break;
				case 2:
					drawable = R.drawable.img_air_seathotright2;
					break;
				case 3:
					drawable = R.drawable.img_air_seathotright3;
					break;
				}
			}

			v.setImageResource(drawable);
		}
	}

	private void setTemp(int id, int temperature, int unit) {
		TextView v = (TextView) mMainView.findViewById(id);
		String s;

		temperature = (byte) ((15.5f + (0.5f * temperature)) * 2);

		if (v != null) {
			if (temperature <= 31) {
				s = "LOW";
			} else if (temperature >= 62) {
				s = "HI";

			} else {
				if (unit == 0) {
					s = String.format(Locale.ENGLISH, "%.1f%s",
							((float) temperature) / 2, getResources()
									.getString(R.string.temp_unic));
				} else {
					s = String.format(Locale.ENGLISH, "%dF", (int) temperature);
				}
			}
			v.setText(s);
		}
	}

	private void updateView(byte[] buf) {
		switch (buf[0]) {
		case 0x21:
			// if (buf[2]&0x80)
			updateSelect(R.id.air_title_ce_ac_max, buf[6] & 0x08);
			updateSelect(R.id.air_title_ce_rear_lock, buf[2] & 0x01);
			updateSelect(R.id.air_title_ce_ac_1, buf[2] & 0x40);
			updateSelect(R.id.air_title_ce_auto_large, buf[2] & 0x08);
			updateSelect(R.id.air_title_ce_max, buf[2] & 0x02);
			updateSelect(R.id.air_title_ce_aqs, buf[6] & 0x20);
			updateSelect(R.id.wheel, buf[7] & 0x80);

			updateSelect(R.id.air_title_sync, buf[2] & 0x04);
			updateSelect(R.id.icon_power, buf[2] & 0x80);
			updateSelect(R.id.rear, buf[6] & 0x40);

			setSpeed((buf[3] & 0xf));
			setACProfile((buf[8] & 0x3));

			setSeatheat(R.id.con_seathotright, (buf[7] & 0x07));

			mSeatHeatRight = buf[7] & 0x7;
			setSeatheat(R.id.con_seathotleft, (buf[7] & 0x70) >> 4);

			mSeatHeatLeft = (buf[7] & 0x70) >> 4;

			setTemp(R.id.con_txt_left_temp, (buf[4] & 0xff), (buf[6] & 0x01));
			setTemp(R.id.con_txt_right_temp, (buf[5] & 0xff), (buf[6] & 0x01));

			updateSelect(R.id.canbus21_mode1, buf[3] & 0x40);
			updateSelect(R.id.canbus21_mode3, buf[3] & 0x20);
			updateSelect(R.id.canbus21_mode5, buf[3] & 0x80);

			setLoop(buf[2] & 0x20);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
			// updateSelect(R.id.air_title_ce_max, buf[6]&0x80);

			super.callBack(0);
			break;
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
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendCanboxInfo0x90(0x21);
			}
		}, 1000);
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

						byte[] buf = intent.getByteArrayExtra("buf");
						if (buf != null) {
							try {
								updateView(buf);
							} catch (Exception e) {
								Log.d("aa", "!!!!!!!!" + e);
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
