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
public class MingJueRongWeiACRaiselFragment extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{


            {R.id.con_left_temp_up, 0x0101}, {R.id.con_left_temp_down, 0x0102}, {R.id.mode, 0x0201}, {R.id.inner_loop, 0x0301},

            {R.id.wind_minus, 0x0402}, {R.id.wind_add, 0x0401},


            {R.id.power, 0x0501}, {R.id.max, 0x0601}, {R.id.rear, 0x0701},

            {R.id.ac, 0x0801}, {R.id.auto, 0x0901},


            //		{ R.id.wind_up1, 0x10 },
            {R.id.wind_horizontal1, 0x1001}, {R.id.wind_horizontal_down, 0x1002},

            {R.id.wind_down1, 0x1003}, {R.id.wind_up_down, 0x1004},


            {R.id.left_seat_heat, 0x1101}, {R.id.right_seat_heat, 0x1201},


            {R.id.con_right_temp_up, 0x1501}, {R.id.con_right_temp_down, 0x1502}, {R.id.rear_lock, 0x1601}, {R.id.sync, 0x1701},


            {R.id.power_rear, 0x1a01}, {R.id.con_left_temp_rear_up, 0x1801}, {R.id.con_left_temp_rear_down, 0x1802},

            {R.id.wind_horizontal1_rear, 0x1901}, {R.id.wind_down1_rear, 0x1903}, {R.id.wind_horizontal_down_rear, 0x1902}, {R.id.ac_auto_rear, 0x1b01}, {R.id.wind_add_rear, 0x1401}, {R.id.wind_minus_rear, 0x1402},

    };
    private View mMainView;

    private CommonUpdateView mCommonUpdateView;
    private BroadcastReceiver mReceiver;
    private int power = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.ac_mingjuerongwei_raise, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        init();
        return mMainView;
    }

    private void init() {

        if (mCarType != 51) {
            mMainView.findViewById(R.id.air_rear).setVisibility(View.GONE);
        }
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x1, (byte) d0,};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x95(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xA8, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendKey(int key) {
        sendCanboxInfo0x95(key, 1);
        Util.doSleep(200);
        sendCanboxInfo0x95(key, 0);
    }

    private void sendCmd(int id) {

        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                if ((CMD_ID[i][1] & 0xff00) == 0) {
                    sendKey((CMD_ID[i][1] & 0xff));
                } else {
                    byte[] buf = new byte[]{(byte) 0x8a, 0x2, (byte) ((CMD_ID[i][1] & 0xff00) >> 8), (byte) (CMD_ID[i][1] & 0xff)};
                    BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                    Util.doSleep(200);
                    buf[3] = 0;
                    BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                }
            }
        }

    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.air_rear) {
            showRear(true);
        } else if (id == R.id.air_front) {

            showRear(false);
        } else {
            sendCmd(id);
        }
    }

    private void showRear(boolean show) {
        if (show) {
            mMainView.findViewById(R.id.ac_layout_rear).setVisibility(View.VISIBLE);
            mMainView.findViewById(R.id.ac_layout_front).setVisibility(View.GONE);
        } else {

            mMainView.findViewById(R.id.ac_layout_rear).setVisibility(View.GONE);
            mMainView.findViewById(R.id.ac_layout_front).setVisibility(View.VISIBLE);
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

        sendCanboxInfo0x90(0x21);
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
                                    // updateView(buf);
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
