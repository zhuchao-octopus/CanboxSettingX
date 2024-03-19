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
import com.common.adapter.MyListViewAdapterCD;
import com.common.adapter.MyListViewAdapterRadio;
import com.common.util.AuxInUI;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
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
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class NissanRaiseRaidoFragment extends MyFragment {

	private View mMainView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater.inflate(R.layout.nissan_radio, container, false);

		return mMainView;
	}

	private void setViewVisible(int id, int s) {

		View v = mMainView.findViewById(id);
		if (v != null) {
			if (s != 0) {
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void updateView(byte[] buf) {
		String s = "";
		switch (buf[0]) {
		case 0x5:
			byte[] text = new byte[0x10];
			Util.byteArrayCopy(text, buf, 0, 2, text.length);
			s = new String(text);
			((TextView)mMainView.findViewById(R.id.status)).setText(s);
			break;
		case 0x3: {
			setViewVisible(R.id.scane, buf[2] & 0x40);
			setViewVisible(R.id.autop, buf[2] & 0x10);
			setViewVisible(R.id.st, buf[2] & 0x20);
			setViewVisible(R.id.rds, buf[2] & 0x80);
			setViewVisible(R.id.status, buf[2] & 0x8);
		}
			break;
		case 0x4: {
			switch (buf[2] & 0xff) {
			case 1:
				s = "AM";
				break;
			case 2:
				s = "AMAP";
				break;
			case 3:
				s = "FM1";
				break;
			case 4:
				s = "FM2";
				break;
			case 5:
				s = "FMAP";
				break;
			}
			if (buf[3] > 0 && buf[3] <= 6) {
				s += " " + buf[3];
			}
			int freq = ((buf[4] & 0xff) << 8) | (buf[5] & 0xff);
			String s2;
			String s3;
			if ((buf[2] & 0xff) >= 3) {

				s3 = "MHz";
				freq = freq * 5 + 8750;
				s2 = String.format("%d.%d", freq / 100, freq % 100);
			} else {
				if ((freq & 0x8000) == 0) {
					freq &= 0xff;
					freq = (freq - 1) * 9 + 531;
				} else {
					freq &= 0xff;
					freq = (freq - 1) * 10 + 530;
				}
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

	@Override
	public void onPause() {
		unregisterListener();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerListener();

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
