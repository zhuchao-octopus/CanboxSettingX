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

package com.canboxsetting.radio;

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
import android.widget.ListView;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.canboxsetting.MyFragment;
import com.common.adapter.MyListViewAdapterRadio;
import com.common.util.AuxInUI;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class Radio155 extends MyFragment {
	private static final String TAG = "Radio153";

	private View mMainView;

	private ListView mListViewCD;

	private MyListViewAdapterRadio mMyListViewAdapter;

	private ListView mListViewPreset;

	private MyListViewAdapterRadio mMyListViewAdapterPreset;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.honda_radio, container, false);

//		mListViewCD = (ListView) mMainView.findViewById(R.id.liststations);
//		mListViewPreset = (ListView) mMainView.findViewById(R.id.listpreset);
		

		mMainView.findViewById(R.id.other).setVisibility(View.GONE);
		mMainView.findViewById(R.id.liststations).setVisibility(View.GONE);
		mMainView.findViewById(R.id.layout_radio_list).setVisibility(View.GONE);
		mMainView.findViewById(R.id.panel).setVisibility(View.GONE);
		mMainView.findViewById(R.id.radio).setVisibility(View.VISIBLE);

		mMainView.findViewById(R.id.stations).setVisibility(View.GONE);
		mMainView.findViewById(R.id.preset).setVisibility(View.GONE);
		mMainView.findViewById(R.id.update_stations_list).setVisibility(View.GONE);
		mMainView.findViewById(R.id.radio_step_up_button).setVisibility(View.GONE);
		mMainView.findViewById(R.id.radio_step_down_button).setVisibility(View.GONE);
//		mListViewPreset.setVisibility(View.VISIBLE);
//		initUI();
		return mMainView;
	}



	private void sendCanboxInfo0x83(int d0) {
		sendCanboxInfo0x83(d0, 1);
	}

	private void sendCanboxInfo0xC5(int d0) {
//		byte[] buf = new byte[] { (byte) 0xc5, 0x2, (byte) d0, 0 };
//		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendCanboxInfo0x83(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0xa1, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}


	


	private void updateView(byte[] buf) {
		String s = "";
		switch (buf[0]) {
		case 0x4: {

			int freq = ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);
			String s2;
			String s3;

			int index = (buf[2] & 0xff);
			if (index < 3) {
				s = "FM";
				if (index != 0) {
					s += index;
				}
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
		}
			break;
		}

	}

	private boolean mPaused = true;
	@Override
	public void onPause() {
		unregisterListener();mPaused = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();mPaused = false;
		BroadcastUtil
				.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
//		requestInitData();

		byte[] buf = new byte[] {(byte) 0xff, 0x1, 0x4 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		super.onResume();
	}
	
	private final static int[] INIT_CMDS = { 0x22, 0x21 };


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				byte[] buf = new byte[] {(byte) 0x1f, 0x1, (byte)(msg.what & 0xff) };
				BroadcastUtil.sendCanboxInfo(getActivity(), buf);
			}
		}
	};


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
								// sendCanboxInfo0xc7(0xE);
								// } else {
								// sendCanboxInfo0xc7(0x0);
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

	private AuxInUI mAuxInUI;

	private int mSource = MyCmd.SOURCE_NONE;

	public boolean isCurrentSource() {
		return (mSource == MyCmd.SOURCE_AUX);
	}

}
