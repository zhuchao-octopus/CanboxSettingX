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
public class AC250 extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{

            {R.id.power, 0x1},

            {R.id.con_left_temp_up, 0x3}, {R.id.con_left_temp_down, 0x2}, {R.id.con_right_temp_up, 0x5}, {R.id.con_right_temp_down, 0x4},


            {R.id.wind_horizontal1, 0x7}, {R.id.wind_down1, 0x8},


            {R.id.wind_add, 0xa}, {R.id.wind_minus, 0x9},

            {R.id.left_seat_heat, 0xd}, {R.id.right_seat_heat, 0xb},

            {R.id.dual, 0x10}, {R.id.max, 0x13}, {R.id.rear, 0x14}, {R.id.ac_auto, 0x15}, {R.id.ac, 0x17}, {R.id.ac_max, 0x18}, {R.id.inner_loop, 0x19},


            {R.id.wind_horizontal_down, 0x21}, {R.id.wind_up_down, 0x20}, {R.id.mode, 0x24}, {R.id.eco, 0x25},


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

        mMainView = inflater.inflate(R.layout.ac_zongtai_bnr, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        return mMainView;
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x82, 0x02, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x95(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xe0, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendKey(int d0) {
        sendCanboxInfo0x95(d0, 1);
        Util.doSleep(200);
        sendCanboxInfo0x95(d0, 0);
    }

    private int getCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                return (CMD_ID[i][1] & 0xffffff);
            }
        }
        return 0;
    }

    public void onClick(View v) {
        int cmd = getCmd(v.getId());

        if (cmd != 0) {
            sendKey((cmd & 0xff));
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
                                    mCommonUpdateView.postChanged(CommonUpdateView.MESSAGE_AIR_CONDITION, 0, 0, buf);
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
