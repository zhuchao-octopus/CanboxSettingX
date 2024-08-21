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

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This activity plays a video from a specified URI.
 */
public class AC261 extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    private CommonUpdateView mCommonUpdateView;
    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_tata_hiworld, container, false);
        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);


        return mMainView;
    }


    private void sendCanboxInfo0xc7(int d0, int d1) {
        byte[] buf;

        buf = new byte[]{0x2, (byte) d0, (byte) d1, 1};

        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        Util.doSleep(200);
        buf[3] = 0;
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {

        byte[] buf;
        buf = new byte[]{0x3, (byte) 0x6a, 5, (byte) d0, 1};


        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

    }

    private final static int[][] CMD_ID = new int[][]{

            {R.id.ac, 0x2}, {R.id.ac_auto, 0x4},

            {R.id.max, 0x5}, {R.id.rear, 0x6}, {R.id.inner_loop, 0x7}, {R.id.wind_add, 0xb}, {R.id.wind_minus, 0xc},


            {R.id.con_left_temp_up, 0xd}, {R.id.con_left_temp_down, 0xe},


            {R.id.mode, 0x15},


    };


    private int getCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                return (CMD_ID[i][1] & 0xffffff);
            }
        }
        return 0;
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

    public void onClick(View v) {

        int cmd = getCmd(v.getId());
        int idCmd = 0x3d;
        if (idCmd == 0) {
            return;
        }
        sendCanboxInfo0xc7(idCmd, (cmd & 0xff));

    }


    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo0x90(0x31);
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
                        String cmd = intent.getStringExtra(MyCmd.EXTRA_COMMON_CMD);
                        if ("ac".equals(cmd)) {
                            byte[] buf = intent.getByteArrayExtra("buf");
                            if (buf != null) {

                                mCommonUpdateView.postChanged(CommonUpdateView.MESSAGE_AIR_CONDITION, 0, 0, buf);

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
