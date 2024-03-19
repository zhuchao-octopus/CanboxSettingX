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

package com.canboxsetting.cd;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class CD80 extends MyFragment {
	private static final String TAG = "JeepCarCDFragment";


	

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.jeep_car_cd_player, container,
				false);

		return mMainView;
	}

	private void sendCanboxInfo0xC8(int d0,int d1,int d2) {
		byte[] buf = new byte[] { (byte) 0x88, 0x3, (byte) d0, (byte) d1,(byte) d2 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	byte mPlayStatus = 0;
	byte mRepeatMode = 0;

	public void onClick(View v) {
		setSource();
		Util.doSleep(100);
        int id = v.getId();
        if (id == R.id.repeat) {
            sendCanboxInfo0xC8(0x11, 0, 0);
        } else if (id == R.id.fr) {
            sendCanboxInfo0xC8(0x3, 0, 0);
        } else if (id == R.id.ff) {
            sendCanboxInfo0xC8(0x4, 0, 0);
        } else if (id == R.id.prev) {
            sendCanboxInfo0xC8(0x2, 0, 0);
        } else if (id == R.id.pp) {
            if (mPlayStatus == 5) {
                sendCanboxInfo0xC8(0x13, 0, 0);//play
            } else {
                sendCanboxInfo0xC8(0x14, 0, 0);
            }
        } else if (id == R.id.next) {
            sendCanboxInfo0xC8(0x1, 0, 0);
        } else if (id == R.id.shuffle) {
            sendCanboxInfo0xC8(0x8, 0, 0);
        }
	}

	private void updateView(byte[] buf) {

		switch (buf[0]) {
		case 0x38:{
			mPlayStatus = (byte) (buf[2] & 0x0f);
			mRepeatMode = (byte) ((buf[2] & 0x30) >> 4);
			
			if (mPlayStatus == 1) {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(1);
			} else {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(0);
			}

			if ((mRepeatMode ) != 2) {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(View.GONE);
			} else {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(View.VISIBLE);
			}
			if ((mRepeatMode ) != 1) {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
			} else {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
			}			
			
			
			String s1 = "";
			String s2 = "";

			s1 += ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);
			s2 += ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);

			((TextView) mMainView.findViewById(R.id.num)).setText(s2 + "/" + s1);

			int total_time =  ((buf[7] & 0xff) << 8) | (buf[8] & 0xff);
			int cur_time =  ((buf[9] & 0xff) << 8) | (buf[10] & 0xff);
			String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d",
					total_time/3600, (total_time%3600)/60, total_time%60,
					cur_time/3600, (cur_time%3600)/60, cur_time%60);

			((TextView) mMainView.findViewById(R.id.time)).setText(s);
		}
			break;
		case 0x39:
			byte[] b = new byte[buf.length - 6];
			
			Util.byteArrayCopy(b, buf, 0, 5, b.length);
			
			String s = "";

			try {
				s = new String(b, "utf-8");
			} catch (Exception e) {

			}
			if (buf[2] == 0x1) {
				((TextView) mMainView.findViewById(R.id.song)).setText(s);
			} else if (buf[2] == 0x2) {
				((TextView) mMainView.findViewById(R.id.albums)).setText(s);
			} else if (buf[2] == 0x3) {
				((TextView) mMainView.findViewById(R.id.singer)).setText(s);
			}
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
		sendCanboxInfo0x90(0x38);
		Util.doSleep(30);
		sendCanboxInfo0x90(0x39);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setSource();
			}
		}, 1000);
		super.onResume();
	}

	private void setSource(){

		byte[] buf = new byte[] { (byte) 0xc0, 0x2, 0x0c, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
