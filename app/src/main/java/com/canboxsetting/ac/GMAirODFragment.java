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
public class GMAirODFragment extends MyFragment {
	private static final String TAG = "JeepAirControlXinbasFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	}

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.gm_od, container, false);

		return mMainView;
	}

	private void sendCanboxInfo0x82(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x71, 0x2, (byte) d0, (byte) d1, };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0xff, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private final static int[][] CMD_ID = new int[][] {
			{ R.id.air_title_ce_max, 4 }, 
			{ R.id.air_title_ce_rear, 5 },
			{ R.id.air_title_ce_ac_1, 8 }, 
			{ R.id.air_title_ce_inner_loop, 3 },
			{ R.id.air_title_ce_auto_large, 1 }, 
			{ R.id.air_title_sync, 2 },
			{ R.id.wind_add, 6 },
			{ R.id.wind_minus, 7 },
			

			{ R.id.icon_power, 0xa }, 
			{ R.id.con_left_temp_up, 0xc },
			{ R.id.con_left_temp_down, 0xd },
			{ R.id.con_right_temp_up, 0xe },
			{ R.id.con_right_temp_down, 0xf }, 
			{ R.id.con_mode_up, 0x10 },
			{ R.id.con_mode_down, 0x11 },
			

			{ R.id.con_seathotleft, 0x24 },
			{ R.id.con_seathotright, 0x25 },

	};

	private void sendCmd(int id) {
		for (int i = 0; i < CMD_ID.length; ++i) {
			if (CMD_ID[i][0] == id) {
				sendCanboxInfo0x82((CMD_ID[i][1] & 0xff), 0);
				Util.doSleep(200);
				sendCanboxInfo0x82((CMD_ID[i][1] & 0xff), 1);
			}
		}
	}

	public void onClick(View v) {
		sendCmd(v.getId());
	}

	private void updateSelect(int id, int s) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setSelected(s != 0);
		}
	}

	private void setSpeed(int speed) {

		for (int i = 0; i < 7; ++i) {
			View v = mMainView.findViewById(R.id.point0 + i);
			if (v != null) {
				if (i < speed) {
					v.setVisibility(View.VISIBLE);
				} else {
					v.setVisibility(View.INVISIBLE);
				}
			}
		}
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
				}
			}

			v.setImageResource(drawable);
		}
	}

	String getAirTemperature(View view,float temperature,byte[]mAirData){
    	if((mAirData[7]&0x40)!=0){
    		return "";
    	}
    	
    	
    	boolean isCentigradeUnit = ((mAirData[5] & 0x1) == 0)?true:false;	
    	
//		if (CarUtil.mTempUnit == 1) {
//			if (!isCentigradeUnit) {
//				temperature = ((temperature)-32)/1.8f;
//				isCentigradeUnit = !isCentigradeUnit;
//			}
//		} else if (CarUtil.mTempUnit == 2) {
//			if (isCentigradeUnit) {
//				temperature = ((temperature/2)*1.8f+32);
//				isCentigradeUnit = !isCentigradeUnit;
//			}
//		}
    	
    	
		if (isCentigradeUnit == true) {
			return String.format(Locale.ENGLISH, "%.1f%s", temperature / 2,
					view.getResources()
							.getString(R.string.temp_unic));
		} else {
			return String.format(Locale.ENGLISH, "%d%s", (int) temperature,
					"F");
		}
    	
    }
	
	private void setTemp(int id, int temperature, int unit) {
		TextView v = (TextView) mMainView.findViewById(id);
		String s;
		if (v != null) {
			if (temperature == 0) {
				s = "LOW";
			} else if (temperature == 0xff) {
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
		switch ((int) (buf[0] & 0xff)) {
		case 0xff:
			if (((int) (buf[1] & 0xff)) == 0xff) {

				/* 参考欣朴大众协议v2.61.002,考虑兼容性，不完全一致
				 * data[0]
				 * 
				 *  
			Bit7: 空调开关指示
			Bit6: A/C指示
			Bit5: 内外循环指示
			Bit4 AUTO 大风灯指示
			Bit3:AUTO 小风灯指示
			Bit2: DUAL 指示
			Bit 1:  前窗除雾  MAX FRONT灯指示
			Bit 0: REAR灯指示 后窗

			data[1] 0:OFF 1:ON

			Bit7	向上送风指示	
			Bit6	水平送风指示	
			Bit5	向下送风指示	
			Bit4	空调显示请求
			Bit3~Bit0
			风速	0x0~07	风速等级 指示 0-7级

			data[2] 左边设定温度
			          
			0x00: LO
			0xff: HI
			0xfa: hide
			0xfb: no update
			0xf0~0xf8: show step
			0x01~0xef: 温度, 0.5 步进

			data[3] 右边设定温度

			0x00: LO
			0xff: HI
			0xfa: hide
			0x01~0xff: 温度

			data[4] 座椅加热
			Bit7 1==AQS内循环 0==非
			Bit5~4 左座椅 00:不显示 01~11:1~3级温度
			Bit3 rear lock 1==LOCK 0==非
			Bit2 1==AC MAX 0==非
			Bit1~0 右座椅 00:不显示 01~11:1~3级温度




			data[5] bit0: 0-> C 1-> F 温度单位
					bit1: 1: hide left temp
					bit2: 1: hide right temp



			data[6]

			同 data[2]，右边座椅吹风。  有些车分左右吹风。

			data[7]

			Bit 0: eco
			Bit 1~2:  0:off 1:soft 2:fast                         ( 0:soft 1:off 2:fast (bagoo GM) )
			Bit3 AUTO REAR SWITCH
			Bit4 AUTO 超大风灯指示
			Bit5 前窗除雾 （有些车有前窗除雾 MAX FRONT灯指示，又另外有一个前窗除雾）
			Bit6 0：手动空调, 1:自动空调 
			Bit7 sync 指示

			data[8] 
			Bit7 rest 指示
			Bit6 temp show level 指示

				 */
				
				updateSelect(R.id.air_title_ce_ac_max, buf[6] & 0x04);
				updateSelect(R.id.air_title_ce_rear, buf[2] & 0x01);
				updateSelect(R.id.air_title_ce_ac_1, buf[2] & 0x40);
				updateSelect(R.id.air_title_ce_auto_large, buf[2] & 0x08);
				updateSelect(R.id.air_title_ce_max, buf[2] & 0x02);

				updateSelect(R.id.air_title_sync, buf[2] & 0x04);
				updateSelect(R.id.icon_power, buf[2] & 0x80);

				setSpeed((buf[3] & 0xf));

				setSeatheat(R.id.con_seathotright, (buf[6] & 0x03));
				setSeatheat(R.id.con_seathotleft, (buf[6] & 0x30) >> 4);

				setTemp(R.id.con_txt_left_temp, (buf[4] & 0xff),
						(buf[7] & 0x01));
				setTemp(R.id.con_txt_right_temp, (buf[5] & 0xff),
						(buf[7] & 0x01));

				int t = 0;
				updateSelect(R.id.canbus21_mode1, 0);
				updateSelect(R.id.canbus21_mode2, 0);
				updateSelect(R.id.canbus21_mode3, 0);
				updateSelect(R.id.canbus21_mode4, 0);
				if ((buf[3] & 0x80) != 0) {
					if ((buf[3] & 0x20) != 0) {
						t = R.id.canbus21_mode4;
					}
				} else {
					if ((buf[3] & 0x20) != 0 && (buf[3] & 0x40) != 0) {
						t = R.id.canbus21_mode2;
					} else if ((buf[3] & 0x20) != 0) {
						t = R.id.canbus21_mode3;
					} else if ((buf[3] & 0x40) != 0) {
						t = R.id.canbus21_mode1;
					}
				}

				if (t != 0) {
					updateSelect(t, 1);
				}

				setLoop(buf[2] & 0x20);
				super.callBack(0);
				break;
			}
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
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendCanboxInfo0x90(0x07);
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
