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
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class AC134 extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{{R.id.power, 0x01}, {R.id.ac, 0x02}, {R.id.sync, 0x3}, {R.id.ac_auto, 0x04}, {R.id.max, 0x05}, {R.id.rear, 0x06}, {R.id.inner_loop, 0x07},

            {R.id.wind_up1, 0x8}, {R.id.wind_horizontal1, 0x9}, {R.id.wind_down1, 0xa},

            {R.id.wind_add, 0x0b}, {R.id.wind_minus, 0x0c},

            {R.id.con_left_temp_up, 0x0d}, {R.id.con_left_temp_down, 0x0e}, {R.id.con_right_temp_up, 0xf}, {R.id.con_right_temp_down, 0x10},

            {R.id.wind_up_down, 0x17}, {R.id.wind_horizontal_down, 0x18},


            {R.id.left_seat_heat, 0x11}, {R.id.right_seat_heat, 0x12},


            {R.id.dual, 0x29},

            {R.id.ac_max, 0x1e},


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
        mMainView = inflater.inflate(R.layout.ac_chuaiqi_hiworld, container, false);
        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        switch (GlobalDef.getModelId()) {
            case 1:
            case 2:
            case 3:
            case 6:
                break;
            default:
                mMainView.findViewById(R.id.left_seat_heat).setVisibility(View.GONE);
                mMainView.findViewById(R.id.right_seat_heat).setVisibility(View.GONE);
                break;
        }
        return mMainView;
    }

    private void sendCanboxKey0x82(int d0) {
        if (d0 == 0x11 || d0 == 0x12) {
            byte[] buf = new byte[]{0x2, (byte) 0x3b, 0, 0};
            int h;
            if (d0 == 0x11) {
                buf[2] = 0xf;
                h = mCommonUpdateView.getHeatLeft();
                h = (h + 1) % 4;
            } else {

                buf[2] = 0x10;
                h = mCommonUpdateView.getHeatRight();
                h = (h + 1) % 4;
            }
            buf[3] = (byte) h;
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            Util.doSleep(200);
        } else {
            sendCanboxInfo(d0, 1);
            Util.doSleep(200);
            sendCanboxInfo(d0, 0);
        }
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{0x2, (byte) 0x3d, (byte) d0, (byte) d1};
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
        if (cmd != 0) {
            sendCanboxKey0x82(cmd & 0xff);
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
        // sendCanboxInfo0x90(0x3);
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
