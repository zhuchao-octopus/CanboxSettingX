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
import com.common.adapter.MyListViewAdapterCD;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class CD228 extends MyFragment {
	private static final String TAG = "JeepCarCDFragment";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// setContentView(R.layout.jeep_car_cd_player);

	}

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.subaru_player,
				container, false);

		registerListener();
		return mMainView;
	}

	private void sendCanboxInfo0xc7(int d0, int d1) {
		byte[] buf = new byte[] { 0x2, (byte) 0xf2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0xc7(int d0) {
		sendCanboxInfo0xc7(d0, 0);
	}

	private void sendCanboxInfo(int d0) {
		byte[] buf = new byte[] { 0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	byte mPlayStatus = 0;
	byte mRepeatMode = 0;

	private int totalSong = -1;
	private int curSong = 0;

	private void updateView(byte[] buf) {

		String s = "";
		String s2 = "";
		switch (buf[0] & 0xff) {
		case 0x62: {

			(mMainView.findViewById(R.id.radio)).setVisibility(View.GONE);
			(mMainView.findViewById(R.id.cd)).setVisibility(View.GONE);
			switch(buf[2]){
			case 0:
				s = "OFF";
				break;
			case 1:
				s = "RADIO";
				if (buf[3] == 0){
					int freq = ((buf[7] & 0xff) << 8) | (buf[6] & 0xff);
					String s3;
					if ((buf[4] & 0xff) != 0x10) {
						s = "FM";
						s3 = "MHz";
						s2 = String.format("%d.%d", freq / 100, freq % 100);
					} else {
						s = "AM";
						s3 = "KHz";
						s2 = freq + "";
					}
					((TextView) mMainView.findViewById(R.id.freq_baud)).setText(s);
					((TextView) mMainView.findViewById(R.id.freq_text)).setText(s2);
					((TextView) mMainView.findViewById(R.id.freq_unit)).setText(s3);
				

					(mMainView.findViewById(R.id.radio)).setVisibility(View.VISIBLE);
				}
				break;
			case 2:
				s = "CD";
				if ((buf[5] & 0xf0) == 0) {
					mMainView.findViewById(R.id.repeat_tag)
							.setVisibility(View.GONE);
				} else {
					mMainView.findViewById(R.id.repeat_tag).setVisibility(
							View.VISIBLE);
				}
				if ((buf[5] & 0x0f) == 0) {
					mMainView.findViewById(R.id.shuffle_tag).setVisibility(
							View.GONE);
				} else {
					mMainView.findViewById(R.id.shuffle_tag).setVisibility(
							View.VISIBLE);
				}
				
				curSong = ((buf[7] & 0xff) << 8) | (buf[6] & 0xff);

				s2 = "" + curSong;				
				((TextView) mMainView.findViewById(R.id.albums)).setText(s2);
				
				s2 = String.format("%02d:%02d",
						(buf[8] & 0xff), (buf[9] & 0xff));

				((TextView) mMainView.findViewById(R.id.time)).setText(s2);			
				

				((TextView) mMainView.findViewById(R.id.song)).setText(""+(buf[4]&0xf));	
				
				
				
				
				(mMainView.findViewById(R.id.cd)).setVisibility(View.VISIBLE);
				break;
			case 3:
				s = "AUX";
				break;
			case 4:
				s = "REAR";
				break;
			}
			
			((TextView) mMainView.findViewById(R.id.title)).setText(s);


			
		}
			break;
		}
	}

	private String getString(int type, byte[] buf) {
		String s = "";
		try {
			if (type == 0x0) {
				s = new String(buf, "GBK");
			} else if (type == 0x3) {
				s = new String(buf, "utf-8");
			} else if (type == 0x2) {
				for (int i = 0; i < buf.length; i += 2) {
					byte b = buf[i];
					buf[i] = buf[i + 1];
					buf[i + 1] = b;
				}
				s = new String(buf, "UNICODE");
			} else {// if (type == 0x11) {
				s = new String(buf, "UNICODE");
			}
		} catch (Exception e) {

		}

		return s;
	}

	@Override
	public void onPause() {
		// unregisterListener();
		mPaused = true;
		super.onPause();
	}

	private final static int[] INIT_CMDS = { 0x62 };

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo(msg.what & 0xff);
			}
		}
	};

	private boolean mPaused = true;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterListener();
		sendCanboxInfo0xc7(0xb, 0);
		BroadcastUtil.sendToCarServiceSetSource(getActivity(),
				MyCmd.SOURCE_MX51);
	}

	@Override
	public void onResume() {
		super.onResume();
		mPaused = false;

		BroadcastUtil
				.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
		requestInitData();
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
					} else if (action.equals(MyCmd.BROADCAST_CAR_SERVICE_SEND)) {

						int cmd = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
						switch (cmd) {
						case MyCmd.Cmd.SOURCE_CHANGE:
						case MyCmd.Cmd.RETURN_CURRENT_SOURCE:
							int source = intent.getIntExtra(
									MyCmd.EXTRA_COMMON_DATA, 0);
							if (mSource == MyCmd.SOURCE_AUX
									&& source != MyCmd.SOURCE_AUX) {
								sendCanboxInfo0xc7(0xb, 0);
							}
							mSource = source;
							break;
						}
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
			iFilter.addAction(MyCmd.BROADCAST_CAR_SERVICE_SEND);

			getActivity().registerReceiver(mReceiver, iFilter);
		}
	}

	private int mSource = MyCmd.SOURCE_NONE;

	public boolean isCurrentSource() {
		return (mSource == MyCmd.SOURCE_AUX);
	}
}
