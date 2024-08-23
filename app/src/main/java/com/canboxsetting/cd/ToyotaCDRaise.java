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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class ToyotaCDRaise extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";
    View.OnTouchListener mTouchFF = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                sendCanboxInfo(0x9, 1);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {

                sendCanboxInfo(0x9, 0);
            }
            return false;
        }
    };
    View.OnTouchListener mTouchFR = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                sendCanboxInfo(0xa, 1);
            } else if (event.getAction() == KeyEvent.ACTION_UP) {

                sendCanboxInfo(0xa, 0);
            }
            return false;
        }
    };
    private View mMainView;
    private int mPlayStatus = 0;
    private int mRepeatMode = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.crown12_car_cd_player, container, false);
        ((TextView) mMainView.findViewById(R.id.str_total_song_title)).setText(R.string.track);
        mMainView.findViewById(R.id.layout_song_singer).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.pp).setVisibility(View.VISIBLE);
        mMainView.findViewById(R.id.ff).setOnTouchListener(mTouchFF);
        mMainView.findViewById(R.id.fr).setOnTouchListener(mTouchFR);
        mMainView.findViewById(R.id.gac_cd_state_scan).setVisibility(View.GONE);
        mMainView.findViewById(R.id.disk_shuffle).setVisibility(View.GONE);
        mMainView.findViewById(R.id.disk_scan).setVisibility(View.GONE);
        mMainView.findViewById(R.id.gac_cd_state_scan).setVisibility(View.GONE);
        return mMainView;
    }

    private void sendCanboxInfo(int d) {
        byte[] buf = new byte[]{(byte) 0x85, 0x2, (byte) d, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d1, int d2) {
        byte[] buf = new byte[]{(byte) 0x85, 0x2, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x8f(int d0) {
        byte[] buf = new byte[]{(byte) 0x8f, 0x3, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.disk_icon1) {
            sendCanboxInfo(0x8, 1);
        } else if (id == R.id.disk_icon2) {
            sendCanboxInfo(0x8, 2);
        } else if (id == R.id.disk_icon3) {
            sendCanboxInfo(0x8, 3);
        } else if (id == R.id.disk_icon4) {
            sendCanboxInfo(0x8, 4);
        } else if (id == R.id.disk_icon5) {
            sendCanboxInfo(0x8, 5);
        } else if (id == R.id.disk_icon6) {
            sendCanboxInfo(0x8, 6);
        } else if (id == R.id.repeat) {
            sendCanboxInfo(2);
        } else if (id == R.id.shuffle) {
            sendCanboxInfo(1);
        } else if (id == R.id.prev) {
            sendCanboxInfo(5);
        } else if (id == R.id.pp) {
            sendCanboxInfo(3);
        } else if (id == R.id.next) {
            sendCanboxInfo(4);
        }
    }

    private void setViewVisible(int id, int b) {
        mMainView.findViewById(id).setVisibility((b != 0) ? View.VISIBLE : View.GONE);
    }

    private void setViewSelect(int id, int b) {
        mMainView.findViewById(id).setSelected((b != 0) ? true : false);
        //		mMainView.findViewById(id).setSelected( true );
    }

    private void setDiskType(int index, int b1, int b2) {
        int id = R.string.gac_cd_state_idle;
        if (b1 != 0) {
            if (b2 != 0) {
                id = R.string.dvd;
            } else {
                id = R.string.cd;
            }
        }

        ((TextView) mMainView.findViewById(index)).setText(id);
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case 0x61: {
                setDiskType(R.id.disk1, buf[2] & 0x1, buf[5] & 0x1);
                setDiskType(R.id.disk2, buf[2] & 0x2, buf[5] & 0x2);
                setDiskType(R.id.disk3, buf[2] & 0x4, buf[5] & 0x4);
                setDiskType(R.id.disk4, buf[2] & 0x8, buf[5] & 0x8);
                setDiskType(R.id.disk5, buf[2] & 0x10, buf[5] & 0x10);
                setDiskType(R.id.disk6, buf[2] & 0x20, buf[5] & 0x20);

                ((TextView) mMainView.findViewById(R.id.str_current_dish)).setText("" + (buf[4] & 0x0f));


                mPlayStatus = buf[3] & 0xff;

            }
            break;
            case 0x62:
                if (buf[2] == 2) {
                    String s = "";
                    if (buf[3] == 0) {
                        ((TextView) mMainView.findViewById(R.id.str_current_dish)).setText("" + (buf[4] & 0x0f));

                        setViewSelect(R.id.shuffle, buf[5] & 0x1);
                        setViewSelect(R.id.repeat, buf[5] & 0x10);
                        setViewSelect(R.id.disk_repeat, buf[5] & 0x20);
                        // setViewSelect(R.id.gac_cd_state_scan, buf[7] & 0x4);
                        // setViewSelect(R.id.disk_scan, buf[7] & 0x40);
                        // setViewSelect(R.id.disk_shuffle, buf[7] & 0x20);

                        int value;

                        value = (buf[8] & 0xff) | ((buf[9] & 0xff) << 8);
                        ((TextView) mMainView.findViewById(R.id.str_current_song)).setText("" + value);

                        s = String.format("%02d:%02d:%02d", (buf[13] & 0xff), (buf[14] & 0xff), (buf[15] & 0xff));

                        ((TextView) mMainView.findViewById(R.id.str_play_time)).setText(s);

                    } else if (buf[3] == 1) {
                        byte[] data = new byte[buf.length - 5];
                        Util.byteArrayCopy(data, buf, 0, 4, data.length);
                        try {
                            s = new String(data, "GBK");
                            ((TextView) mMainView.findViewById(R.id.str_total_song)).setText(s);
                        } catch (Exception e) {

                        }
                    } else if (buf[3] == 2) {
                        byte[] data = new byte[buf.length - 5];
                        Util.byteArrayCopy(data, buf, 0, 4, data.length);
                        try {
                            s = new String(data, "GBK");
                            ((TextView) mMainView.findViewById(R.id.artist)).setText(s);
                        } catch (Exception e) {

                        }
                    }
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

        sendCanboxInfo0x8f(0x4);
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();

        sendCanboxInfo(0x20, 4);
        Util.doSleep(30);
        sendCanboxInfo0x90(0x61);
        Util.doSleep(30);
        sendCanboxInfo0x90(0x62);

        super.onResume();
    }

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
