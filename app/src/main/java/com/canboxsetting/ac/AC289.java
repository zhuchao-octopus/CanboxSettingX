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
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class AC289 extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{

            {R.id.power, 0x01},


            {R.id.con_left_temp_up, 0x03}, {R.id.con_left_temp_down, 0x02}, {R.id.con_right_temp_up, 0x5}, {R.id.con_right_temp_down, 0x4},


            {R.id.mode, 0x8},

            {R.id.wind_minus, 0x09}, {R.id.wind_add, 0x0a},

            {R.id.dual, 0x10},

            {R.id.max, 0x13}, {R.id.rear, 0x14}, {R.id.ac_auto, 0x15}, {R.id.ac_max, 0x16}, {R.id.ac, 0x17},

            {R.id.inner_loop, 0x19},


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
        mMainView = inflater.inflate(R.layout.ac_skyworth_od, container, false);
        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

        return mMainView;
    }

    private void sendCanboxKey0x82(int d0) {
        sendCanboxInfo(d0, 1);
        Util.doSleep(300);
        sendCanboxInfo(d0, 0);
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xe0, 0x2, (byte) d0, (byte) d1};
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

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo0x90(0x28);
        super.onResume();
    }

    private void setTemp(int id, int str_id, int temp) {

        TextView tv = ((TextView) mMainView.findViewById(id));
        if (tv != null) {
            String s = "--";
            if (temp >= 0 && temp <= 0xb3) {
                temp = -400 + (temp * 5);

                s = String.format("%d.%d", temp / 10, (temp > 0) ? temp % 10 : -temp % 10);

            }

            tv.setText(getString(str_id) + s + getString(R.string.temp_unic));
        }
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
                                try {
                                    setTemp(R.id.air_indoor_temp, R.string.air_indoor_temp, buf[10] & 0xff);
                                    setTemp(R.id.air_exterior_temp, R.string.air_exterior_temp, buf[11] & 0xff);
                                } catch (Exception e) {

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
