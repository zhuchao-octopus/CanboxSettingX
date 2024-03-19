/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.canboxsetting.eqset;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.utils.BroadcastUtil;

/**
 * This activity plays a video from a specified URI.
 */
public class EQActivity extends Activity {

	private static final String[] SHOW_VOLUME_PRO = { "11","2","9","280","274","255", "25", "66", "68",
			"72", "74", "3", "37", "76", "80", "89", "107", "108", "109",
			"110", "117", "119", "120", "122", "125", "149", "150", "154",
			"156", "157", "173", "184", "186", "200", "213", "52", "222",
			"223", "216", "54", "59", "227", "239", "253","284","285"

	};
	
	private static final String[] HIDE_MIDDLE_PRO =  {
		"72", "3", "37","122","173","284"
		
	};
	
	private boolean getShowVolumeBar(String pro) {
		
		for (String s : SHOW_VOLUME_PRO) {
			if (s.equals(pro)){
				return true;
			}
		}
		return false;
	}
	private boolean getHideMiddleBar(String pro) {
		
		for (String s : HIDE_MIDDLE_PRO) {
			if (s.equals(pro)){
				return true;
			}
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_eq);
		
		
		init();
	}	
	

	private void init() {

		String mCanboxType = MachineConfig
				.getPropertyOnce(MachineConfig.KEY_CAN_BOX);
		int mProVersion = 0;
		String mProIndex = null;
		if (mCanboxType != null) {
			String[] ss = mCanboxType.split(",");			
			try {
				for (int i = 1; i < ss.length; ++i) {
					if (ss[i]
							.startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
						mProVersion = Integer.valueOf(ss[i].substring(1));
					} else if (ss[i]
							.startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
						mProIndex = ss[i].substring(1);
					}
				}
			} catch (Exception e) {

			}
		}

		if (mProVersion >= 3 && mProIndex != null) {
			if (getShowVolumeBar(mProIndex)){
				findViewById(R.id.volume).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.volume).setVisibility(View.GONE);
			}
			
			if (!getHideMiddleBar(mProIndex)){
				findViewById(R.id.middle).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.middle).setVisibility(View.GONE);
			}
		} 
		
		mSeekBarLow = (SeekBar) findViewById(R.id.seekbar_bass);
		mSeekBarLow.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		mSeekBarMiddle = (SeekBar) findViewById(R.id.seekbar_middle);
		mSeekBarMiddle.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		mSeekBarHigh = (SeekBar) findViewById(R.id.seekbar_treble);
		mSeekBarHigh.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		mSeekVolume = (SeekBar) findViewById(R.id.seekbar_volume);
		mSeekVolume.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

	}

	private void sendEQCmd(int cmd, int data) {
		Intent i = new Intent(MyCmd.BROADCAST_SEND_TO_CAN);
		i.putExtra(MyCmd.EXTRA_COMMON_CMD, cmd | CMD_GROUP_EQ);
		i.putExtra(MyCmd.EXTRA_COMMON_DATA, data);

		BroadcastUtil.sendToCarService(this, i);
	}

	OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				int id = seekBar.getId();
				if (id == R.id.seekbar_treble) {
					sendEQCmd(EQ_CMD_SET_HIGH, progress);
				} else if (id == R.id.seekbar_middle) {
					sendEQCmd(EQ_CMD_SET_MIDDLE, progress);
				} else if (id == R.id.seekbar_bass) {
					sendEQCmd(EQ_CMD_SET_LOW, progress);
				} else if (id == R.id.seekbar_volume) {
					sendEQCmd(EQ_CMD_SET_VOLUME, progress);
				}
			}
		}
	};

	SeekBar mSeekBarLow;
	SeekBar mSeekBarMiddle;
	SeekBar mSeekBarHigh;
	SeekBar mSeekVolume;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerListener();
		requestMax();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterListener();
	}

	public final static int CMD_GROUP_EQ = 0x100;

	// sub cmd <= 0xff
	public final static int EQ_CMD_SET_HIGH = 1;
	public final static int EQ_CMD_SET_MIDDLE = 2;
	public final static int EQ_CMD_SET_LOW = 3;
	public final static int EQ_CMD_SET_ZONE_FR = 4;
	public final static int EQ_CMD_SET_ZONE_LR = 5;
	public final static int EQ_CMD_SET_VOLUME = 6;

	public final static int EQ_CMD_SET_ALL_DATA = 0xf0;
	public final static int EQ_REQUEST_ALL_MAX = 0xff;

	private void requestMax() {
		Intent i = new Intent(MyCmd.BROADCAST_SEND_TO_CAN);
		i.putExtra(MyCmd.EXTRA_COMMON_CMD, EQ_REQUEST_ALL_MAX | CMD_GROUP_EQ);
		BroadcastUtil.sendToCarService(this, i);
	}

	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_amp_rear) {
			setFR(true);
		} else if (id == R.id.btn_amp_front) {
			setFR(false);
		} else if (id == R.id.btn_amp_left) {
			setLR(false);
		} else if (id == R.id.btn_amp_right) {
			setLR(true);
		}
	}

	private void setFR(boolean f) {
		if (mZoneMax > 0) {
			if (f) {
				if (mFRValue < (mZoneMax-1)) {
					mFRValue = (mFRValue + 1);
				}
			} else {
				if (mFRValue > 0) {
					mFRValue = (mFRValue - 1);
				}
			}
			sendEQCmd(EQ_CMD_SET_ZONE_FR, mFRValue);
		}
	}

	private void setLR(boolean f) {
		if (mZoneMax > 0) {
			if (f) {
				if (mLRValue < (mZoneMax-1)) {
					mLRValue = (mLRValue + 1);
				}
			} else {
				if (mLRValue > 0) {
					mLRValue = (mLRValue - 1);
				}
			}
			sendEQCmd(EQ_CMD_SET_ZONE_LR, mLRValue);
		}
	}

	private int mZoneMax;
	private int mVolumeMax;
	private int mEQMax;
	private int mLRValue = 0;
	private int mFRValue = 0;

	private float mCrossStepX = 0;
	private float mCrossStepY = 0;

	private void updateCross(int fr, int lr) {
		mFRValue = fr;
		mLRValue = lr;

		View v;
		LayoutParams lp;
		v = findViewById(R.id.sound_buttun_line2);
		if (v != null) {
			lp = (LayoutParams) v.getLayoutParams();
			lp.x = (int) (lr * mCrossStepX);// - (SOUND_SET_RANGE/2);
			lp.y = 0;
			v.setLayoutParams(lp);
		}

		v = findViewById(R.id.sound_buttun_line1);
		if (v != null) {
			lp = (LayoutParams) v.getLayoutParams();
			lp.x = 0;
			lp.y = (int) (fr * mCrossStepY);
			v.setLayoutParams(lp);
		}
	}

	private void initCrossStep() {
		if ((mCrossStepX == 0 || mCrossStepY == 0) && mZoneMax > 0) {
			View v = findViewById(R.id.linearLayoutMid);
			Log.d("ccfk", v.getWidth() + ":");
			if (v.getWidth() > 0) {
				mCrossStepX = ((float) (v.getWidth() - 2)) / (mZoneMax - 1);
			}
			if (v.getHeight() > 0) {
				mCrossStepY = ((float) (v.getHeight() - 2)) / (mZoneMax - 1);
			}
		}
	}

	private void doCmd(int cmd, Intent intent) {

		if (cmd == EQ_REQUEST_ALL_MAX) {
			int data = intent.getIntExtra(MyCmd.EXTRA_COMMON_DATA, 0);
			mEQMax = (data & 0xff);
			mZoneMax = ((data & 0xff00) >> 8);
			mVolumeMax = ((data & 0xff0000) >> 16);

			mSeekBarLow.setMax(mEQMax - 1);
			mSeekBarMiddle.setMax(mEQMax - 1);
			mSeekBarHigh.setMax(mEQMax - 1);
			mSeekVolume.setMax(mVolumeMax - 1);
			initCrossStep();
		} else if (cmd == EQ_CMD_SET_ALL_DATA) {
			initCrossStep();
			byte[] buf = intent.getByteArrayExtra("buf");
			if (buf != null) {
				checkBuf(buf);
				mSeekBarHigh.setProgress(buf[0]);
				mSeekBarMiddle.setProgress(buf[1]);
				mSeekBarLow.setProgress(buf[2]);

				setEQText(R.id.val_treble, buf[0] & 0xff);
				setEQText(R.id.val_middle, buf[1] & 0xff);
				setEQText(R.id.val_bass, buf[2] & 0xff);

				updateCross(buf[3], buf[4]);
				
				if (buf.length>5){
					mSeekVolume.setProgress(buf[5]);
					((TextView) findViewById(R.id.val_volume)).setText((buf[5] & 0xff) + "");
				}
			}
		}

	}
	
	private void checkBuf(byte[] buf) {
		for (int i = 0; i < buf.length; ++i) {
			if (buf[i] < 0) {
				buf[i] = 0;
			}
		}
	}
	


	private void setEQText(int id, int v) {
		int h = (mEQMax / 2);
		v = v - h;
		((TextView) findViewById(id)).setText(v + "");
	}

	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
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

						int cmd = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
						if ((cmd & 0xff00) == CMD_GROUP_EQ) {

							doCmd(cmd & 0xff, intent);
						}
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

			registerReceiver(mReceiver, iFilter);
		}
	}

}
