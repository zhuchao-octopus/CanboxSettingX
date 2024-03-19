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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class CD251 extends MyFragment {
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
		mMainView = inflater.inflate(R.layout.rx330_cd_player,
				container, false);

		registerListener();
		mMainView.findViewById(R.id.bottom).setVisibility(View.INVISIBLE);
		mMainView.findViewById(R.id.cd_pic).setVisibility(View.INVISIBLE);
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
		case 0xc0: {
			byte[] str = new byte[buf[1]];
			Util.byteArrayCopy(str, buf, 0, 2, str.length);
			s = new String(str);

			((TextView) mMainView.findViewById(R.id.num)).setText(s);

		}

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
