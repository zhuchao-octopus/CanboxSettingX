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

package com.canboxsetting.ac;

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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class JeepAirRaiseControlFragment extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{

            {R.id.ac, 0x11}, {R.id.ac_max, 0x12}, {R.id.inner_loop, 0x13}, {R.id.ac_auto, 0x14}, {R.id.max, 0x15}, {R.id.rear, 0x16}, {R.id.sync, 0x17}, {R.id.wind_horizontal1, 0x18}, {R.id.wind_horizontal_down, 0x19}, {R.id.wind_down1, 0x1a}, {R.id.wind_up_down, 0x1b}, {R.id.wind_minus, 0x1c}, {R.id.wind_add, 0x1d}, {R.id.con_left_temp_up, 0x1f}, {R.id.con_left_temp_down, 0x1e}, {R.id.con_right_temp_up, 0x21}, {R.id.con_right_temp_down, 0x20},

            {R.id.left_seat_heat, 0x30}, {R.id.right_seat_heat, 0x32},


            {R.id.left_seat_refrigeration, 0x31}, {R.id.right_seat_refrigeration, 0x33}, {R.id.wheel, 0x34},

    };
    private View mMainView;

    private CommonUpdateView mCommonUpdateView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendCanboxInfo0x90(0x5);
        }
    };
    private BroadcastReceiver mReceiver;
    private int power = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_jeep_raise, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        return mMainView;
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0xf1, 0x1, (byte) d0,};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x95(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x95, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendKey(int key) {
        sendCanboxInfo0x95(key, 1);
        Util.doSleep(200);
        sendCanboxInfo0x95(key, 0);
    }

    private void sendCmd(int id) {
        if (id == R.id.power) {
            if (power == 0) {
                sendKey(0x9);
            } else {
                sendKey(0x10);
            }
        }
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                sendKey((CMD_ID[i][1] & 0xff));
            }
        }
    }

    public void onClick(View v) {
        sendCmd(v.getId());
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        //		sendCanboxInfo0x90(0x5);

        mHandler.sendEmptyMessageDelayed(0, 500);
        mHandler.sendEmptyMessageDelayed(0, 1000);
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
                        String cmd = intent.getStringExtra(MyCmd.EXTRA_COMMON_CMD);
                        if ("ac".equals(cmd)) {
                            byte[] buf = intent.getByteArrayExtra("buf");
                            if (buf != null) {
                                try {
                                    //								updateView(buf);
                                    mCommonUpdateView.postChanged(CommonUpdateView.MESSAGE_AIR_CONDITION, 0, 0, buf);
                                    power = buf[0] & 0x80;
                                } catch (Exception e) {
                                    Log.d("aa", "!!!!!!!!" + e);
                                }
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
