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
public class AC270 extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{{R.id.power, 0x0},

            {R.id.con_left_temp_up, 0x03}, {R.id.con_left_temp_down, 0x04}, {R.id.con_right_temp_up, 0x3}, {R.id.con_right_temp_down, 0x4},


            {R.id.wind_add, 0x01}, {R.id.wind_minus, 0x02},


            {R.id.ac, 0x6}, {R.id.mode, 0x5}, {R.id.inner_loop, 0x7},


    };
    private CommonUpdateView mCommonUpdateView;
    private View mMainView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_futian_od, container, false);
        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

        return mMainView;
    }

    private void sendCanboxKey0x82(int d0) {

        sendCanboxInfo(d0, 1);
        Util.doSleep(200);
        sendCanboxInfo(d0, 0);

    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xe0, 2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
        int id = v.getId();

        int cmd = getCmd(id);
        //			if (cmd != 0) {
        sendCanboxKey0x82(cmd & 0xff);
        //			}


    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        byte[] buf = new byte[]{(byte) 0x90, 1, (byte) 0x24};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
