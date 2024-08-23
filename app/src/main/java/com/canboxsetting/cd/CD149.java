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
import android.view.LayoutInflater;
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
public class CD149 extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    private View mMainView;
    private int mPlayStatus = 0;
    private int mRepeatMode = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.crown12_car_cd_player, container, false);

        return mMainView;
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x2, (byte) d1, (byte) d2};
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
            sendCanboxInfo(0x8a, 0x6, 1);
        } else if (id == R.id.disk_icon2) {
            sendCanboxInfo(0x8a, 0x6, 2);
        } else if (id == R.id.disk_icon3) {
            sendCanboxInfo(0x8a, 0x6, 3);
        } else if (id == R.id.disk_icon4) {
            sendCanboxInfo(0x8a, 0x6, 4);
        } else if (id == R.id.disk_icon5) {
            sendCanboxInfo(0x8a, 0x6, 5);
        } else if (id == R.id.disk_icon6) {
            sendCanboxInfo(0x8a, 0x6, 6);
        } else if (id == R.id.repeat) {
            if (v.isSelected()) {
                sendCanboxInfo(0x8a, 0xb, 0);
            } else {
                sendCanboxInfo(0x8a, 0xb, 1);
            }
        } else if (id == R.id.prev) {
            sendCanboxInfo(0x74, 0x9, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x9, 0);
        } else if (id == R.id.pp) {// if (mPlayStatus == 3) {
            // sendCanboxInfo0xC8(0x13);
            // } else {
            // sendCanboxInfo0xC8(0x14);
            // }
        } else if (id == R.id.next) {
            sendCanboxInfo(0x74, 0x8, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x8, 0);
        } else if (id == R.id.ff) {
            if (v.isSelected()) {

                v.setSelected(false);
                sendCanboxInfo(0x8a, 0xe, 0);
            } else {
                v.setSelected(true);
                sendCanboxInfo(0x8a, 0xe, 1);
            }
        } else if (id == R.id.fr) {
            if (v.isSelected()) {
                v.setSelected(false);
                sendCanboxInfo(0x8a, 0xf, 0);
            } else {

                v.setSelected(true);
                sendCanboxInfo(0x8a, 0xf, 1);
            }
        } else if (id == R.id.shuffle) {
            if (v.isSelected()) {
                sendCanboxInfo(0x8a, 0xc, 0);
            } else {

                sendCanboxInfo(0x8a, 0xc, 1);
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

    private void setDiskType(int index, int type) {
        int id = R.string.gac_cd_state_idle;
        switch (type) {
            case 1:
                id = R.string.cd;
                break;
            case 2:
                id = R.string.dvd;
                break;
        }

        ((TextView) mMainView.findViewById(index)).setText(id);
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case 0x1d: {
                setDiskType(R.id.disk1, (buf[2] & 0xc0) >> 6);
                setDiskType(R.id.disk2, (buf[2] & 0x30) >> 4);
                setDiskType(R.id.disk3, (buf[2] & 0xc) >> 2);
                setDiskType(R.id.disk4, (buf[2] & 0x3) >> 0);
                setDiskType(R.id.disk5, (buf[3] & 0xc0) >> 6);
                setDiskType(R.id.disk6, (buf[3] & 0x30) >> 4);
            }
            break;
            case 0x1c:
                if (buf[2] == 2) {
                    ((TextView) mMainView.findViewById(R.id.str_current_dish)).setText("" + (buf[4] & 0xff));

                    String s = "";
                    if (buf[5] != 0) {
                        s += (buf[5] & 0xff);
                    }
                    ((TextView) mMainView.findViewById(R.id.str_current_song)).setText(s);

                    s = "";
                    if (buf[6] != 0) {
                        s += (buf[6] & 0xff);
                    }
                    ((TextView) mMainView.findViewById(R.id.str_total_song)).setText(s);

                    s = String.format("%02d:%02d", (buf[8] & 0xff), (buf[9] & 0xff));

                    ((TextView) mMainView.findViewById(R.id.str_play_time)).setText(s);

                    setViewSelect(R.id.repeat, buf[7] & 0x1);
                    setViewSelect(R.id.shuffle, buf[7] & 0x2);
                    setViewSelect(R.id.gac_cd_state_scan, buf[7] & 0x4);
                    setViewSelect(R.id.disk_scan, buf[7] & 0x40);
                    setViewSelect(R.id.disk_repeat, buf[7] & 0x10);
                    setViewSelect(R.id.disk_shuffle, buf[7] & 0x20);

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
        sendCanboxInfo0x90(0x1c);
        Util.doSleep(30);
        sendCanboxInfo0x90(0x1d);

        Util.doSleep(30);
        sendCanboxInfo0x8f(0x3);

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
