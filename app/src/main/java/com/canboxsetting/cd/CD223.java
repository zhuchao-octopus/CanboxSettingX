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
import com.car.ui.GlobalDef;
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
public class CD223 extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.crown13_car_cd_player_hiworld, container, false);

        return mMainView;
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x2, (byte) d0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private boolean isHighCrown13() {//for future to do
        if (GlobalDef.getCarConfig() == 2) {
            return true;
        }
        return false;
    }


    private void sendCanboxInfo0x90(int d0) {

        byte[] buf;
        if (isHighCrown13()) {
            buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0};
        } else {
            buf = new byte[]{0x2, (byte) 0x6a, (byte) d0, 0};
        }

        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }


    //	private void sendCanboxInfo0x8f(int d0) {
    //		byte[] buf = new byte[] { (byte) 0x8f, 0x3, 0x2, (byte) d0, 0 };
    //		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    //	}

    private int mPlayStatus = 0;
    private int mRepeatMode = 0;

    public void onClick(View v) {
        int id = v.getId();//		case R.id.disk_icon1:
        //			sendCanboxInfo(0x8a, 0x6, 1);
        //			break;
        //		case R.id.disk_icon2:
        //			sendCanboxInfo(0x8a, 0x6, 2);
        //			break;
        //		case R.id.disk_icon3:
        //			sendCanboxInfo(0x8a, 0x6, 3);
        //			break;
        //		case R.id.disk_icon4:
        //			sendCanboxInfo(0x8a, 0x6, 4);
        //			break;
        //		case R.id.disk_icon5:
        //			sendCanboxInfo(0x8a, 0x6, 5);
        //			break;
        //		case R.id.disk_icon6:
        //			sendCanboxInfo(0x8a, 0x6, 6);
        //			break;
        if (id == R.id.repeat) {
            if (v.isSelected()) {
                sendCanboxInfo(0xf2, 0x3, 0);
            } else {
                sendCanboxInfo(0xf2, 0x3, 1);
            }
        } else if (id == R.id.prev) {
            sendCanboxInfo(0xf2, 0x7, 0);
        } else if (id == R.id.pp) {
            if (mPlayStatus == 1) {
                sendCanboxInfo(0xf2, 0x2, 0);
                mPlayStatus = 2;
            } else {
                sendCanboxInfo(0xf2, 0x1, 0);
                mPlayStatus = 1;
            }
        } else if (id == R.id.next) {
            sendCanboxInfo(0xf2, 0x7, 1);
        } else if (id == R.id.ff) {
            sendCanboxInfo(0xf2, 0x8, 1);
        } else if (id == R.id.fr) {
            sendCanboxInfo(0xf2, 0x8, 0);
        } else if (id == R.id.shuffle) {
            if (v.isSelected()) {
                sendCanboxInfo(0xf2, 0x5, 0);
            } else {

                sendCanboxInfo(0xf2, 0x5, 1);
            }
        } else if (id == R.id.disk_shuffle) {
            if (v.isSelected()) {
                sendCanboxInfo(0x8a, 0x9, 0);
            } else {

                sendCanboxInfo(0x8a, 0x9, 1);
            }
        } else if (id == R.id.disk_repeat) {
            if (v.isSelected()) {
                sendCanboxInfo(0x8a, 0x8, 0);
            } else {

                sendCanboxInfo(0x8a, 0x8, 1);
            }
        } else if (id == R.id.disk_scan) {
            if (v.isSelected()) {
                sendCanboxInfo(0x8a, 0xa, 0);
            } else {

                sendCanboxInfo(0x8a, 0xa, 1);
            }
        } else if (id == R.id.gac_cd_state_scan) {
            sendCanboxInfo(0x74, 0x12, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x12, 0);
        }
    }

    private void setViewVisible(int id, int b) {
        mMainView.findViewById(id).setVisibility((b != 0) ? View.VISIBLE : View.GONE);
    }

    private void setViewSelect(int id, int b) {
        mMainView.findViewById(id).setSelected((b != 0) ? true : false);
        //		mMainView.findViewById(id).setSelected( true );
    }

    private void setDiskType(int index, int type, int type2) {
        int id = R.string.gac_cd_state_idle;

        if (type != 0) {
            if (type2 == 0) {
                id = R.string.cd;
            } else {
                id = R.string.dvd;
            }
        }

        ((TextView) mMainView.findViewById(index)).setText(id);
    }

    public String bcd2Str0xF(int b) {
        String c = "";
        if (b >= 0 && b < 10) {
            c = "" + b;
        } else if (b >= 0xa && b <= 0xf) {
            switch (b) {
                case 0xa:
                    c = "A";
                    break;
                case 0xb:
                    c = "B";
                    break;
                case 0xc:
                    c = "C";
                    break;
                case 0xd:
                    c = "D";
                    break;
                case 0xe:
                    c = "E";
                    break;
                case 0xf:
                    c = "F";
                    break;
            }
        }
        return c;
    }

    public String bcd2Str(byte b) {

        return (bcd2Str0xF((b & 0xF0) >> 4) + bcd2Str0xF((b & 0xF)));
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case (byte) 0x86: {

                setDiskType(R.id.disk1, (buf[3] & 0x1), (buf[4] & 0x1));
                setDiskType(R.id.disk2, (buf[3] & 0x2), (buf[4] & 0x2));
                setDiskType(R.id.disk3, (buf[3] & 0x4), (buf[4] & 0x4));
                setDiskType(R.id.disk4, (buf[3] & 0x8), (buf[4] & 0x8));
                setDiskType(R.id.disk5, (buf[3] & 0x10), (buf[4] & 0x10));
                setDiskType(R.id.disk6, (buf[3] & 0x20), (buf[4] & 0x20));

                ((TextView) mMainView.findViewById(R.id.str_current_dish)).setText("" + ((buf[2] & 0xf0) >> 4));

                String s = "";
                //				if (buf[5] != 0) {
                //					s += (buf[5] & 0xff);
                //				}

                s = bcd2Str(buf[5]);
                ((TextView) mMainView.findViewById(R.id.str_current_song)).setText(s);

                s = "";
                //				if (buf[6] != 0) {
                //					s += (buf[6] & 0xff);
                //				}
                //				((TextView) mMainView.findViewById(R.id.str_total_song))
                //						.setText(s);

                s = String.format("%02d:%02d", (buf[6] & 0xff), (buf[7] & 0xff));

                ((TextView) mMainView.findViewById(R.id.str_play_time)).setText(s);

                setViewSelect(R.id.repeat, buf[8] & 0x20);
                setViewSelect(R.id.shuffle, buf[8] & 0x08);
                setViewSelect(R.id.gac_cd_state_scan, buf[8] & 0x80);
                setViewSelect(R.id.disk_scan, buf[8] & 0x40);
                setViewSelect(R.id.disk_repeat, buf[8] & 0x10);
                setViewSelect(R.id.disk_shuffle, buf[8] & 0x04);

            }
            break;
        }
    }

    private String getCanBoxString(int type, byte[] buf) {
        String s = "";
        try {
            if (type == 0x1) {
                s = new String(buf);
            } else if (type == 0x2) {
                s = new String(buf, "GB2312");
            } else if (type == 0x10) {
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
        unregisterListener();

        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo0x90(0x86);
        Util.doSleep(30);
        sendCanboxInfo(0xf3, 0x1, 4);

        Util.doSleep(30);
        sendCanboxInfo(0xf2, 0x1, 0);

        mPlayStatus = 1;
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
