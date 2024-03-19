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
public class Radio153 extends MyFragment {
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

		mListViewCD = (ListView) mMainView.findViewById(R.id.liststations);
		mListViewPreset = (ListView) mMainView.findViewById(R.id.listpreset);
		initUI();
		return mMainView;
	}

	private void initUI() {
		if (mCarType == 0) {
			showUI(0);
		} else {
			showUI(1);
		}
	}

	private void showUI(int type) {
		if (mCarType == 0) {
			type = 0;
		}

		if (type == 0) {
			mMainView.findViewById(R.id.other).setVisibility(View.GONE);
			mMainView.findViewById(R.id.radio).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.fmam).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.line2).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.radio_function_button_scan)
					.setVisibility(View.VISIBLE);
		} else {
			mMainView.findViewById(R.id.other).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
			mMainView.findViewById(R.id.fmam).setVisibility(View.GONE);
			mMainView.findViewById(R.id.line1).setVisibility(View.GONE);
			mMainView.findViewById(R.id.line2).setVisibility(View.GONE);
			mMainView.findViewById(R.id.radio_function_button_scan)
					.setVisibility(View.GONE);
		}
	}

	private void updateSaveList(int index, String name, int freq) {

		if (mMyListViewAdapter == null) {
			mMyListViewAdapter = new MyListViewAdapterRadio(getActivity(),
					R.layout.tl_list);
			mListViewCD.setAdapter(mMyListViewAdapter);

			mListViewCD.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View view,
						int postion, long id) {
					int index = mMyListViewAdapter.getSelectIndex(postion);
					if (index != -1) {

						sendCanboxInfo0x83(0xb, index);

					}
				}
			});

		}

		if (mMyListViewAdapter != null) {
			mMyListViewAdapter.addList(index, name, freq);
		}

	}

	private void updatePresetList(int index, String name, int freq) {

		if (mMyListViewAdapterPreset == null) {
			mMyListViewAdapterPreset = new MyListViewAdapterRadio(
					getActivity(), R.layout.tl_list);
			mListViewPreset.setAdapter(mMyListViewAdapterPreset);

			mListViewPreset.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View view,
						int postion, long id) {
					int index = mMyListViewAdapterPreset
							.getSelectIndex(postion);
					if (index != -1) {

						sendCanboxInfo0x83(0x6, index);

					}
				}
			});

		}

		if (mMyListViewAdapterPreset != null) {
			mMyListViewAdapterPreset.addList(index, name, freq);
		}

	}

	private void sendCanboxInfo0x83(int d0) {
		sendCanboxInfo0x83(d0, 0);
	}

	private void sendCanboxInfo0xC5(int d0) {
//		byte[] buf = new byte[] { (byte) 0xc5, 0x2, (byte) d0, 0 };
//		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}
	
	private void sendCanboxInfo0x83(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x83, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x8f, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fmam) {
            sendCanboxInfo0x83(1);
        } else if (id == R.id.radio_prev) {
            sendCanboxInfo0x83(3);
            Util.doSleep(200);
            sendCanboxInfo0xC5(3);
        } else if (id == R.id.radio_next) {
            sendCanboxInfo0x83(2);
            Util.doSleep(200);
            sendCanboxInfo0xC5(4);
        } else if (id == R.id.radio_ff) {
            sendCanboxInfo0x83(4);
        } else if (id == R.id.radio_fr) {
            sendCanboxInfo0x83(5);
        } else if (id == R.id.radio_function_button_scan) {
            if ((mStatus & 0x20) != 0) {
                sendCanboxInfo0x83(0xa, 1);
            } else {
                sendCanboxInfo0x83(0xa, 0);
            }
        } else if (id == R.id.radio_step_up_button) {
            sendCanboxInfo0x83(8);
        } else if (id == R.id.radio_step_down_button) {
            sendCanboxInfo0x83(9);
        } else if (id == R.id.update_stations_list) {
            if (mFlashList == 1) {
                mFlashList = 0;
            } else {
                mFlashList = 1;
            }
            sendCanboxInfo0x83(0xc, mFlashList);
        } else if (id == R.id.stations) {
            mMainView.findViewById(R.id.liststations).setVisibility(
                    View.VISIBLE);
            mMainView.findViewById(R.id.listpreset).setVisibility(View.GONE);
        } else if (id == R.id.preset) {
            mMainView.findViewById(R.id.liststations).setVisibility(View.GONE);
            mMainView.findViewById(R.id.listpreset).setVisibility(View.VISIBLE);
        }
	}
	
	private int mFlashList = 0;

	private int mListIndex;
	private int mStatus;

	private void updateStatus(byte b) {
		mStatus = (b & 0xf0);
		mListIndex = (b & 0x0f);
		String s = "";
		if ((b & 0x20) != 0) {
			s = getActivity().getString(R.string.scaning);
		} else {
			if ((b & 0x10) != 0) {

				s = getActivity().getString(R.string.refreshing);
			} else {
				s = "";
			}
		}

		((TextView) mMainView.findViewById(R.id.status)).setText(s);

		if ((b & 0x40) != 0) {
			((TextView) mMainView.findViewById(R.id.st))
					.setVisibility(View.VISIBLE);
		} else {

			((TextView) mMainView.findViewById(R.id.st))
					.setVisibility(View.GONE);
		}
	}

	private void updateView(byte[] buf) {
		String s = "";
		switch (buf[0]) {
		case 0x3: {

			updateStatus(buf[2]);

			int freq = ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);
			String s2;
			String s3;
			if ((buf[2] & 0x80) == 0) {
				s = "FM";
				s3 = "MHz";
				s2 = String.format("%d.%d", freq / 10, freq % 10);
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
		case 0x7: {
			int freq = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
			if ((buf[2] & 0x80) == 0) {
				s = String.format("%d.%d", freq / 10, freq % 10);
			} else {
				s = freq + "";
			}
			updateSaveList(((buf[4] & 0xff)), s, freq);
			break;
		}
		case 0x4: {
			int num;
			if ((buf[2] & 0x80) == 0) {
				num = 12;
			} else {
				num = 6;
			}
			for (int i = 0; i < num; ++i) {
				int freq = ((buf[3 + i*2] & 0xff) << 8) | (buf[4 + i*2] & 0xff);
				if ((buf[2] & 0x80) == 0) {
					s = String.format("%d.%d", freq / 10, freq % 10);
				} else {
					s = freq + "";
				}


				Log.d("ccfk", i+":"+freq);
				
				updatePresetList(i, s, freq);
			}

			break;
		}
		case 0x7b: {
			switch (buf[2]) {
			case 1:
			case 2:
			case 3:
				showUI(0);
				break;
			case 4:
				s = "CD";
				showUI(1);

				String s1 = "";
				String s2 = "";

				s2 += (buf[3] & 0xff);
				s1 += (buf[4] & 0xff);

				mMainView.findViewById(R.id.common_repeat1_tag).setVisibility(
						View.GONE);
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.GONE);
				if (buf[5] == 1) {

					mMainView.findViewById(R.id.common_repeat1_tag)
							.setVisibility(View.VISIBLE);
				} else if (buf[5] == 2) {
					mMainView.findViewById(R.id.shuffle_tag).setVisibility(
							View.VISIBLE);
				} else if (buf[5] == 3) {
					s1 += " " + getString(R.string.scaning);
				}

				((TextView) mMainView.findViewById(R.id.num)).setText(s2 + "/"
						+ s1);

				String s3 = String.format("%02d:%02d", (buf[7] & 0xff),
						(buf[8] & 0xff));

				((TextView) mMainView.findViewById(R.id.time)).setText(s3);

				break;
			case 5:
				s = "AUX";
				showUI(1);
				((TextView) mMainView.findViewById(R.id.num)).setText("");
				((TextView) mMainView.findViewById(R.id.time)).setText("");
				((TextView) mMainView.findViewById(R.id.status)).setText("");
				break;
			}
			((TextView) mMainView.findViewById(R.id.title)).setText(s);
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
		requestInitData();

		super.onResume();
	}
	
	private final static int[] INIT_CMDS = { 0x8d0300, 0x8d0400,0x8d0700 };

	private void requestInitData() {
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 500));
		}
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo((msg.what & 0xff0000) >> 16, (msg.what & 0xff00) >> 8, msg.what & 0xff);
			}
		}
	};

	private void sendCanboxInfo(int cmd, int d0, int d1) {
		byte[] buf = new byte[] { (byte) cmd, 0x2, (byte) d0, (byte) d1 };
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
								Log.d("ffck", ""+Util.byte2HexStr(buf));
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
