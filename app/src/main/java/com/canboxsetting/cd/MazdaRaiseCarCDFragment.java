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
public class MazdaRaiseCarCDFragment extends MyFragment {
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
		mMainView = inflater.inflate(R.layout.jeep_car_cd_player, container,
				false);

		registerListener();
		return mMainView;
	}

	private void sendCanboxInfo0x82(int d0) {
		byte[] buf = new byte[] { (byte) 0x82, 0x1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x1, (byte) d0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x83(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x83, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	byte mPlayStatus = 0;
	byte mRepeatMode = 0;

	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.repeat) {
			if ((mRepeatMode & 0xf0) == 0) {
				sendCanboxInfo0x82(0x7);
			} else {
				sendCanboxInfo0x82(0x8);
			}
		} else if (id == R.id.prev) {
			sendCanboxInfo0x82(0x5);
		} else if (id == R.id.pp) {
			if (mPlayStatus == 2) {
				sendCanboxInfo0x82(0x1);
			} else {
				sendCanboxInfo0x82(0x0);
			}
		} else if (id == R.id.next) {
			sendCanboxInfo0x82(0x4);
		} else if (id == R.id.ff) {
			sendCanboxInfo0x82(0xc);
		} else if (id == R.id.fr) {
			sendCanboxInfo0x82(0xd);
		} else if (id == R.id.shuffle) {
			if ((mRepeatMode & 0xf) == 0) {
				sendCanboxInfo0x82(0x9);
			} else {
				sendCanboxInfo0x82(0xb);
			}
		}
	}

	private int totalSong = -1;
	private int curSong = 0;

	private void updateView(byte[] buf) {

		switch (buf[0]) {
		case 0x61:{
			
			String s2 = "";
			totalSong = ((buf[4] & 0xff) << 8) | (buf[5] & 0xff);

			s2 += curSong;

			s2 += "/" + totalSong;

			((TextView) mMainView.findViewById(R.id.num)).setText(s2);
			

			mPlayStatus = buf[2];
			int repeat = ((buf[3] & 0x80) == 0) ? 0 : 0x40;
			int random = ((buf[3] & 0x40) == 0) ? 0 : 0x04;
			mRepeatMode = (byte) (repeat | random);

			if (mPlayStatus == 2) {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable()
						.setLevel(1);
			} else {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable()
						.setLevel(0);
			}

			if ((mRepeatMode & 0xf0) == 0) {
				mMainView.findViewById(R.id.repeat_tag)
						.setVisibility(View.GONE);
			} else {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(
						View.VISIBLE);
			}
			if ((mRepeatMode & 0xf) == 0) {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.GONE);
			} else {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.VISIBLE);
			}

			String s1 = "";
			s2 = "";

			// s1 += ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
			curSong = ((buf[6] & 0xff) << 8) | (buf[7] & 0xff);

			s2 += curSong;

			if (totalSong != -1) {
				s2 += "/" + totalSong;
			}

			((TextView) mMainView.findViewById(R.id.num)).setText(s2);

//			int t = ((buf[8] & 0xff) << 8) | (buf[9] & 0xff);
//			int c = ((buf[10] & 0xff) << 8) | (buf[11] & 0xff);

			String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d",
					(buf[11] & 0xff), (buf[12] & 0xff), (buf[13] & 0xff),
					(buf[8] & 0xff), (buf[9] & 0xff), (buf[10] & 0xff));

			((TextView) mMainView.findViewById(R.id.time)).setText(s);
			
		}
			break;
		case 0x04: {
			String s2 = "";
			totalSong = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);

			s2 += curSong;

			s2 += "/" + totalSong;

			((TextView) mMainView.findViewById(R.id.num)).setText(s2);

			break;
		}
		case 0x5: {

			mPlayStatus = buf[3];
			mRepeatMode = buf[4];

			if (mPlayStatus == 2) {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable()
						.setLevel(1);
			} else {
				((ImageView) mMainView.findViewById(R.id.pp)).getDrawable()
						.setLevel(0);
			}

			if ((mRepeatMode & 0xf0) == 0) {
				mMainView.findViewById(R.id.repeat_tag)
						.setVisibility(View.GONE);
			} else {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(
						View.VISIBLE);
			}
			if ((mRepeatMode & 0xf) == 0) {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.GONE);
			} else {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.VISIBLE);
			}

			String s1 = "";
			String s2 = "";

			// s1 += ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
			curSong = ((buf[7] & 0xff) << 8) | (buf[8] & 0xff);

			s2 += curSong;

			if (totalSong != -1) {
				s2 += "/" + totalSong;
			}

			((TextView) mMainView.findViewById(R.id.num)).setText(s2);

			int t = ((buf[9] & 0xff) << 8) | (buf[10] & 0xff);
			int c = ((buf[11] & 0xff) << 8) | (buf[12] & 0xff);

			String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d", c / 3600,
					(c / 60) % 60, c % 60, t / 3600, (t / 60) % 60, t % 60);

			((TextView) mMainView.findViewById(R.id.time)).setText(s);
		}
			break;
		case 0x62:{
			byte[] b = new byte[buf.length - 5];
			Util.byteArrayCopy(b, buf, 0, 4, b.length);
			String s = "";
			if ((buf[3] & 0xf) == 1) {
				s = getString((buf[3] & 0xf0) >> 4, b);
			}
			if (buf[2] == 0x3) {
				((TextView) mMainView.findViewById(R.id.song)).setText(s);
			} else if (buf[2] == 0x2) {
				((TextView) mMainView.findViewById(R.id.albums)).setText(s);
			} else if (buf[2] == 0x1) {
				((TextView) mMainView.findViewById(R.id.singer)).setText(s);
			}
		}
			break;
		case 0x06:{
			byte[] b = new byte[buf.length - 5];
			Util.byteArrayCopy(b, buf, 0, 4, b.length);
			String s = "";
			if ((buf[3] & 0xf) == 1) {
				s = getString((buf[3] & 0xf0) >> 4, b);
			}
			if (buf[2] == 0x0) {
				((TextView) mMainView.findViewById(R.id.song)).setText(s);
			} else if (buf[2] == 0x2) {
				((TextView) mMainView.findViewById(R.id.albums)).setText(s);
			} else if (buf[2] == 0x3) {
				((TextView) mMainView.findViewById(R.id.singer)).setText(s);
			}
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
//		unregisterListener();
		mPaused = true;
		super.onPause();
		byte[] buf = new byte[] { (byte) 0xa1, 0x2, (byte) 4, 3 };
	
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	// private final static int[] INIT_CMDS = { 0x0400, 0x0500, 0x0600, 0x0601,
	// 0x0602, 0x0603 };

	private final static int[] INIT_CMDS = { 0x60, 0x61, 0x62, 0x63 };

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
				// sendCanboxInfo0x83((msg.what & 0xff00) >> 8, msg.what &
				// 0xff);
				sendCanboxInfo0x90(msg.what & 0xff);
			}
		}
	};

	private boolean mPaused = true;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterListener();
		sendCanboxInfo0x82(0xF);
		BroadcastUtil.sendToCarServiceSetSource(getActivity(),
				MyCmd.SOURCE_MX51);
	}

	@Override
	public void onResume() {
		mPaused = false;
		byte[] buf = new byte[] { (byte) 0xa1, 0x2, (byte) 4, 4 };
//		if (GlobalDef.getModelId() == 30) //test
//		{
//			buf[3] = 5;
//		}
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		Util.doSleep(50);
		sendCanboxInfo0x82(0xE);
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		requestInitData();
		BroadcastUtil
				.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
		
		
		
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

//						int cmd = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
//						switch (cmd) {
//						case MyCmd.Cmd.SOURCE_CHANGE:
//						case MyCmd.Cmd.RETURN_CURRENT_SOURCE:
//							int source = intent.getIntExtra(
//									MyCmd.EXTRA_COMMON_DATA, 0);
//							if (mSource == MyCmd.SOURCE_AUX&&
//									source != MyCmd.SOURCE_AUX) {
////								sendCanboxInfo0x82(0xE);
////							} else {
//								sendCanboxInfo0x82(0xF);
//							}
//							mSource = source;
//							break;
//						}
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
