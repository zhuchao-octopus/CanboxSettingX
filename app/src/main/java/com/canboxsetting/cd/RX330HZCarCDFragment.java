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
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class RX330HZCarCDFragment extends MyFragment {
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
		mMainView = inflater.inflate(R.layout.rx330_player, container, false);

		mMainView.findViewById(R.id.freq_1).setOnLongClickListener(
				mOnLongClickListenerRadio);
		mMainView.findViewById(R.id.freq_2).setOnLongClickListener(
				mOnLongClickListenerRadio);
		mMainView.findViewById(R.id.freq_3).setOnLongClickListener(
				mOnLongClickListenerRadio);
		mMainView.findViewById(R.id.freq_4).setOnLongClickListener(
				mOnLongClickListenerRadio);
		mMainView.findViewById(R.id.freq_5).setOnLongClickListener(
				mOnLongClickListenerRadio);
		mMainView.findViewById(R.id.freq_6).setOnLongClickListener(
				mOnLongClickListenerRadio);
		showUI(0);
		mMainView.findViewById(R.id.aux_main).setOnClickListener(
				mOnClickListener);
		return mMainView;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toggleFullScreen();
		}
	};

	private OnLongClickListener mOnLongClickListenerRadio = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int index = v.getId() - R.id.freq_1;
			sendCanboxInfo0x84(0x21, index);
			return false;
		}
	};

	private int mUI;
	private boolean mCD = true;
	private boolean mIsUSB = false;

	private void showUI(int i) {
		if (i == 0) {
			hideCamera();
			mMainView.findViewById(R.id.off).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.usb).setVisibility(View.GONE);
			mUI = 0;
		} else {
			mMainView.findViewById(R.id.off).setVisibility(View.GONE);
			if (i == 4) {
				mUI = 4;
				mIsUSB = true;
				mMainView.findViewById(R.id.usb).setVisibility(View.VISIBLE);
			} else {
				mMainView.findViewById(R.id.usb).setVisibility(View.GONE);
				mIsUSB = false;
				showCarPlayerUI(i, null);
			}
		}
	}

	private void showCarPlayerUI(int i, byte[] buf) {
		if (mIsUSB) {
			return;
		}

		if (i != 2 || (mCD)) {
			if (i != 3) {
				hideDVD();
			}
		}
		mUI = i;

		switch (i) {
		case 0:
			hideCamera();
			mMainView.findViewById(R.id.off).setVisibility(View.VISIBLE);
			break;
		case 1:
			hideCamera();
			mMainView.findViewById(R.id.off).setVisibility(View.GONE);
			mMainView.findViewById(R.id.radio).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.cd).setVisibility(View.GONE);
			mMainView.findViewById(R.id.dvd).setVisibility(View.GONE);
			((TextView) mMainView.findViewById(R.id.car_status))
					.setText(R.string.fam);
			updateRaidoUI(buf);
			break;
		case 2:

			((TextView) mMainView.findViewById(R.id.car_status))
					.setText(R.string.button_text_disc);
			if (mCD) {
				mMainView.findViewById(R.id.off).setVisibility(View.GONE);
				mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
				mMainView.findViewById(R.id.cd).setVisibility(View.VISIBLE);
				mMainView.findViewById(R.id.dvd).setVisibility(View.GONE);
			} else {
				mMainView.findViewById(R.id.off).setVisibility(View.GONE);
				mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
				mMainView.findViewById(R.id.cd).setVisibility(View.GONE);
				mMainView.findViewById(R.id.dvd).setVisibility(View.VISIBLE);
				mMainView.findViewById(R.id.dvd_menu).setVisibility(
						View.VISIBLE);
			}
			updateCDUI(buf);
			break;
		case 3:
			mMainView.findViewById(R.id.off).setVisibility(View.GONE);
			mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
			mMainView.findViewById(R.id.cd).setVisibility(View.GONE);
			mMainView.findViewById(R.id.dvd).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.dvd_menu).setVisibility(View.GONE);
			showCamera();
			break;
		}
	}

	private void sendCanboxInfo0xC5(int d0) {
		byte[] buf = new byte[] { (byte) 0xC5, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x84(int d0, int d1) {
		byte[] buf = new byte[] { (byte) 0x84, 0x2, (byte) d0, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void sendCanboxInfo0x90(int d0) {
		byte[] buf = new byte[] { (byte) 0x90, 0x2, (byte) d0, 0 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	private void next() {
		switch (mUI) {
		case 0:
			break;
		case 1:
			sendCanboxInfo0x84(0x25, 0);
			break;
		case 2:
			if (mCD) {
				sendCanboxInfo0x84(0x13, 0);
			} else {

			}
			break;
		}
	}

	private void prev() {
		switch (mUI) {
		case 1:
			sendCanboxInfo0x84(0x26, 0);
			break;
		case 2:
			if (mCD) {
				sendCanboxInfo0x84(0x14, 0);
			} else {

			}
			break;
		}
	}

	private void pp() {
		switch (mUI) {
		case 2:
			if (mCD) {
				sendCanboxInfo0x84(0x12, 0);
			} else {
				if (mPlayStatus == 4) {
					sendCanboxInfo0x84(0x41, 0);
				} else {

					sendCanboxInfo0x84(0x40, 0);
				}

			}
			break;
		}
	}

	private byte mPlayStatus = 0;

	private void ff() {
		switch (mUI) {
		case 2:
			if (mCD) {
				sendCanboxInfo0x84(0x18, mPlayStatus == 4 ? 0 : 1);
			} else {
				sendCanboxInfo0x84(0x4b, mPlayStatus == 4 ? 0 : 1);
			}
			break;
		}
	}

	private void fr() {
		switch (mUI) {
		case 2:
			if (mCD) {
				sendCanboxInfo0x84(0x19, mPlayStatus == 4 ? 0 : 1);
			} else {

				sendCanboxInfo0x84(0x4c, mPlayStatus == 4 ? 0 : 1);
			}
			break;
		}
	}

	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.shuffle) {
            sendCanboxInfo0x84(0x10, 0);
        } else if (id == R.id.repeat) {
            sendCanboxInfo0x84(0x11, 0);
        } else if (id == R.id.radio_prev || id == R.id.prev) {
            prev();
        } else if (id == R.id.pp) {
            pp();
        } else if (id == R.id.radio_next || id == R.id.next) {
            next();
        } else if (id == R.id.radio_function_button_scan) {
            sendCanboxInfo0x84(0x24, 0);
        } else if (id == R.id.radio_step_up_button) {
            sendCanboxInfo0x84(0x23, 0);
        } else if (id == R.id.radio_step_down_button) {
            sendCanboxInfo0x84(0x22, 0);
        } else if (id == R.id.freq_1) {
            sendCanboxInfo0x84(0x20, 1);
        } else if (id == R.id.freq_2) {
            sendCanboxInfo0x84(0x20, 2);
        } else if (id == R.id.freq_3) {
            sendCanboxInfo0x84(0x20, 3);
        } else if (id == R.id.freq_4) {
            sendCanboxInfo0x84(0x20, 4);
        } else if (id == R.id.freq_5) {
            sendCanboxInfo0x84(0x20, 5);
        } else if (id == R.id.freq_6) {
            sendCanboxInfo0x84(0x20, 6);
        } else if (id == R.id.ff) {
            ff();
        } else if (id == R.id.fr) {
            fr();
        } else if (id == R.id.to_fm1) {
            showUI(1);
            sendCanboxInfo0x84(0x30, 1);
        } else if (id == R.id.to_fm2) {
            showUI(1);
            sendCanboxInfo0x84(0x30, 2);
        } else if (id == R.id.to_am) {
            showUI(1);
            sendCanboxInfo0x84(0x30, 3);
        } else if (id == R.id.to_disc) {
            showUI(2);
            sendCanboxInfo0x84(0x30, 4);
        } else if (id == R.id.to_aux) {
            showUI(3);
            sendCanboxInfo0x84(0x30, 5);
        } else if (id == R.id.to_usb) {
            showUI(4);
            sendCanboxInfo0xC5(0x1);
            // case R.id.list:
            // case R.id.radio_list:
            // mMainView.findViewById(R.id.off).setVisibility(View.VISIBLE);
            // break;
        } else if (id == R.id.aux_main) {
            toggleFullScreen();
        } else if (id == R.id.dvd_next) {
            sendCanboxInfo0x84(0x4a, 5);
        } else if (id == R.id.dvd_pre) {
            sendCanboxInfo0x84(0x49, 5);
        } else if (id == R.id.dvd_ff) {
            ff();
        } else if (id == R.id.dvd_fr) {
            fr();
        } else if (id == R.id.dvd_pp) {
            pp();
        } else if (id == R.id.dvd_stop) {
            sendCanboxInfo0x84(0x42, 0);
        } else if (id == R.id.up) {
            sendCanboxInfo0x84(0x43, 0);
        } else if (id == R.id.down) {
            sendCanboxInfo0x84(0x44, 0);
        } else if (id == R.id.left) {
            sendCanboxInfo0x84(0x45, 0);
        } else if (id == R.id.right) {
            sendCanboxInfo0x84(0x46, 0);
        } else if (id == R.id.ok) {
            sendCanboxInfo0x84(0x47, 0);
        } else if (id == R.id.dvd_title) {
            sendCanboxInfo0x84(0x48, 0);
        } else if (id == R.id.usb_play) {
            sendCanboxInfo0xC5(1);
        } else if (id == R.id.usb_stop) {
            sendCanboxInfo0xC5(2);
        } else if (id == R.id.usb_next) {
            sendCanboxInfo0xC5(4);
        } else if (id == R.id.usb_prev) {
            sendCanboxInfo0xC5(3);
        } else if (id == R.id.dvd_set) {
            if (mMainView.findViewById(R.id.dvd_lang_main).getVisibility() == View.VISIBLE) {

                mMainView.findViewById(R.id.dvd_lang_main).setVisibility(
                        View.GONE);
            } else {
                mMainView.findViewById(R.id.dvd_lang_main).setVisibility(
                        View.VISIBLE);
            }
        } else if (id == R.id.dvd_lang) {
            showLangDialog(0x4f);
        } else if (id == R.id.dvd_lang_subtitle) {
            showLangDialog(0x4d);
        } else if (id == R.id.dvd_lang_voice) {
            showLangDialog(0x4e);
        }
	}

	private int mDialogId;
	private void showLangDialog(int id){
		mDialogId  =id;
		AlertDialog ad = new AlertDialog.Builder(getActivity()).
				setItems(getActivity().getResources().getStringArray(R.array.rx330_languages), 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						sendCanboxInfo0x84(mDialogId, whichButton);						
					}
					
				}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int whichButton) {

					}
				}).create();
		
		ad.show();
		

	}
	private boolean mFull = false;

	private void setFullScreen() {
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (!mCD) {
			mMainView.findViewById(R.id.dvd_menu).setVisibility(View.GONE);
			mMainView.findViewById(R.id.disc_status).setVisibility(View.GONE);
		}
		mFull = true;
	}

	private void quitFullScreen() {
		final WindowManager.LayoutParams attrs = getActivity().getWindow()
				.getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActivity().getWindow().setAttributes(attrs);
		getActivity().getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (!mCD) {
			mMainView.findViewById(R.id.dvd_menu).setVisibility(View.VISIBLE);
			mMainView.findViewById(R.id.disc_status)
					.setVisibility(View.VISIBLE);
		}
		mFull = false;
	}

	private void toggleFullScreen() {
		if (!mFull) {
			setFullScreen();
		} else {
			quitFullScreen();
		}
	}

	private int m6DiskStatus = 0;

	private void hideCamera() {
		if (mAuxInUI != null) {
			if (!mAuxInUI.mPause) {
				mAuxInUI.onPause();
			}
		}
	}

	private void showCamera() {
		if (mAuxInUI == null) {
			mAuxInUI = AuxInUI.getInstanse(getActivity(),
					mMainView.findViewById(R.id.aux_main), 0);
			mAuxInUI.onCreate();
		}
		if (mAuxInUI.mPause) {
			mAuxInUI.onResume();
		}
	}

	private void showDVD() {
		showCamera();
		mMainView.findViewById(R.id.cd).setVisibility(View.GONE);
		mMainView.findViewById(R.id.dvd).setVisibility(View.VISIBLE);
		mMainView.findViewById(R.id.dvd_menu).setVisibility(View.VISIBLE);
		mMainView.findViewById(R.id.disc_status).setVisibility(View.VISIBLE);
		mMainView.findViewById(R.id.dvd_lang_main).setVisibility(View.GONE);
	}

	private void hideDVD() {
		hideCamera();
		quitFullScreen();
		mMainView.findViewById(R.id.dvd).setVisibility(View.GONE);
		mMainView.findViewById(R.id.disc_status).setVisibility(View.VISIBLE);
	}

	private void updateCDUI(byte[] buf) {
		if (buf != null) {
			boolean cd;
			if ((m6DiskStatus & (0x1 << (buf[4] - 1))) != 0) {
				cd = false;
			} else {
				cd = true;
			}

			if (mCD != cd) {
				mCD = cd;
				if (cd) {
					mMainView.findViewById(R.id.cd).setVisibility(View.VISIBLE);
					hideDVD();
				} else {

					showDVD();
				}
			}

			String s1 = "";
			String s2 = "";

			s1 += ((buf[7] & 0xff) << 8) | (buf[6] & 0xff);
			s2 += ((buf[9] & 0xff) << 8) | (buf[8] & 0xff);

			((TextView) mMainView.findViewById(R.id.num))
					.setText(s2 + "/" + s1);

			String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d",
					(buf[13] & 0xff), (buf[14] & 0xff), (buf[15] & 0xff),
					(buf[10] & 0xff), (buf[11] & 0xff), (buf[12] & 0xff));

			((TextView) mMainView.findViewById(R.id.time)).setText(s);

			if ((buf[5] & 0xf0) == 0x0) {
				mMainView.findViewById(R.id.repeat_tag)
						.setVisibility(View.GONE);
			} else if ((buf[5] & 0xf0) == 0x10) {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(
						View.VISIBLE);
				((ImageView) mMainView.findViewById(R.id.repeat_tag))
						.setImageResource(R.drawable.common_repeat_tag);
			} else {
				mMainView.findViewById(R.id.repeat_tag).setVisibility(
						View.VISIBLE);
				((ImageView) mMainView.findViewById(R.id.repeat_tag))
						.setImageResource(R.drawable.common_repeat1_tag);
			}

			if ((buf[5] & 0x0f) == 0) {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.GONE);
			} else {
				mMainView.findViewById(R.id.shuffle_tag).setVisibility(
						View.VISIBLE);
			}
		}
	}

	private void updateDVDUI(byte[] buf) {
		if (buf != null) {

		}
	}

	private void updateRaidoUI(byte[] buf) {
		if (buf != null) {
			if (buf[1] == 6) {
				String freq = "";
				switch (buf[4]) {
				case 0:
					freq = "FM ";
					break;
				case 1:
					freq = "FM1 ";
					break;
				case 2:
					freq = "FM2 ";
					break;
				case 0x10:
					freq = "AM ";
					break;
				case 0x11:
					freq = "AM1 ";
					break;
				case 0x12:
					freq = "FM2 ";
					break;
				}

				if ((buf[5] & 0xf) > 0 && (buf[5] & 0xf) < 6) {
					freq += "P" + (buf[5] & 0xf) + " ";
				}

				((TextView) mMainView.findViewById(R.id.freq_text1))
						.setText(freq);

				int f = ((buf[7] & 0xff) << 8) | ((buf[6] & 0xff));

				// ((TextView)mMainView.findViewById(R.id.freq_text)).setText(freq);
				setFreq(R.id.freq_text, f, buf[4] < 3);
				setVisible(R.id.ico_st, (buf[5] & 0x80));
				setVisible(R.id.ico_search, (buf[5] & 0x20));

			} else if (buf[1] == 15) {
				boolean fm = false;
				if (buf[4] < 3) {
					fm = true;
				}
				int f;
				f = ((buf[6] & 0xff) << 8) | ((buf[5] & 0xff));
				setFreq(R.id.freq_1, f, fm);
				f = ((buf[8] & 0xff) << 8) | ((buf[7] & 0xff));
				setFreq(R.id.freq_2, f, fm);
				f = ((buf[10] & 0xff) << 8) | ((buf[9] & 0xff));
				setFreq(R.id.freq_3, f, fm);
				f = ((buf[12] & 0xff) << 8) | ((buf[11] & 0xff));
				setFreq(R.id.freq_4, f, fm);
				f = ((buf[14] & 0xff) << 8) | ((buf[13] & 0xff));
				setFreq(R.id.freq_5, f, fm);
				f = ((buf[16] & 0xff) << 8) | ((buf[15] & 0xff));
				setFreq(R.id.freq_6, f, fm);
			}
		}
	}

	private void setFreq(int id, int f, boolean fm) {
		String freq;
		if (fm) {
			freq = String.format(Locale.ENGLISH, "%d.%02d MHz", f / 100,
					f % 100);
		} else {
			freq = f + " KHz";
		}

		((TextView) mMainView.findViewById(id)).setText(freq);
	}

	private void setVisible(int id, int value) {
		View v = mMainView.findViewById(id);
		if (v != null) {
			v.setVisibility(value != 0 ? View.VISIBLE : View.GONE);
		}
	}

	private void updateView(byte[] buf) {
		String s;
		switch (buf[0]) {
		case 0x62:

			showCarPlayerUI(buf[2], buf);

			break;
		case 0x61: {
			m6DiskStatus = buf[5];
			boolean cd = true;
			if ((buf[5] & (0x1 << (buf[4] - 1))) != 0) {
				cd = false;
			}
			// else {
			// mCD = true;
			// }

			mPlayStatus = (byte) ((buf[3] & 0xff) >> 4);
			if (mCD != cd) {
				mCD = cd;
				if (cd) {
					mMainView.findViewById(R.id.cd).setVisibility(View.VISIBLE);
					hideDVD();
				} else {
					showDVD();
				}
			}

			String ss[] = getActivity().getResources().getStringArray(
					R.array.disc_status);
			if (mPlayStatus != 0xf) {

				s = ss[mPlayStatus];
			} else {
				s = ss[6] + (buf[3] & 0xf);
			}
			((TextView) mMainView.findViewById(R.id.disc_status)).setText(s);
			
			ss = getActivity().getResources().getStringArray(
					R.array.rx330_languages);
			s = getActivity().getString(R.string.dvd) + " " + ss[buf[8]&0xff];
			((TextView) mMainView.findViewById(R.id.dvd_lang)).setText(s);
			s = getActivity().getString(R.string.subtitle) + " " + ss[buf[6]&0xff];
			((TextView) mMainView.findViewById(R.id.dvd_lang_subtitle)).setText(s);
			s = getActivity().getString(R.string.voice) + " " + ss[buf[7]&0xff];
			((TextView) mMainView.findViewById(R.id.dvd_lang_voice)).setText(s);
			break;
		}
		case 0x21: {
			s = "";
			String ss[] = getActivity().getResources().getStringArray(
					R.array.usb_status);
			if (((buf[2] & 0xf)) < ss.length && ((buf[2] & 0xf)) >= 0) {
				s = ss[((buf[2] & 0xf))];
			}
			((TextView) mMainView.findViewById(R.id.usb_status)).setText(s);

			String s1 = "";
			String s2 = "";

			s1 += ((buf[6] & 0xff) << 8) | (buf[7] & 0xff);
			s2 += ((buf[8] & 0xff) << 8) | (buf[9] & 0xff);

			s1 = s1 + "/" + s2;
			if (((buf[2] & 0x3) >> 4) == 2) {
				s1 += " FOLDER" + buf[10];
			}
			((TextView) mMainView.findViewById(R.id.num)).setText(s1);

			s = String.format("%02d:%02d", (buf[4] & 0xff), (buf[5] & 0xff));

			((TextView) mMainView.findViewById(R.id.time)).setText(s);

			((SeekBar) mMainView.findViewById(R.id.seekbar))
					.setProgress(buf[11] & 0xff);
			break;
		}
		}
	}

	private int mPauseUI = 0;
	@Override
	public void onPause() {
		unregisterListener();
		hideCamera();
		mPauseUI = mUI;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPauseUI = 0;
	}
	@Override
	public void onResume() {
		registerListener();
		if (mPauseUI != 0){
			mUI = 0;
			showUI(mPauseUI);
		}
		BroadcastUtil
				.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
		sendCanboxInfo0x90(0x62);
		Util.doSleep(30);
		sendCanboxInfo0x90(0x61);

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

	public boolean onBackKey() {
		if (mUI != 0) {
			showUI(0);
			return true;
		}
		return false;
	}
	
	
}
