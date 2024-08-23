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
public class PSARaiseACFragment extends MyFragment {

    private final static int[][] CMD_ID = new int[][]{{R.id.wind_minus, 0xa02}, {R.id.wind_add, 0xa01}, {R.id.con_left_temp_up, 0x401}, {R.id.con_left_temp_down, 0x402}, {R.id.con_right_temp_up, 0x501}, {R.id.con_right_temp_down, 0x502},

    };
    private final static int[][] CMD_ID1 = new int[][]{

            {R.id.ac_auto, 0x1}, {R.id.ac, 0x2}, {R.id.ac_max, 0x3},

            {R.id.wind_horizontal1, 6}, {R.id.wind_up1, 7}, {R.id.wind_down1, 8}, {R.id.max, 0x11}, {R.id.dual, 0xb},

    };
    private final static int[][] CMD_ID2 = new int[][]{{R.id.power, 0xC},

            {R.id.inner_loop, 0xe}, {R.id.rear, 0x12},

    };
    private View mMainView;
    private CommonUpdateView mCommonUpdateView;
    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_psa_raise, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        return mMainView;
    }

    private int getCmd(int id, int[][] cmd) {
        for (int i = 0; i < cmd.length; ++i) {
            if (cmd[i][0] == id) {
                return (cmd[i][1] & 0xffffff);
            }
        }
        return 0;
    }

    private void sendCanboxInfo0xE1(int d0, int d1) {
        byte[] buf = new byte[]{0x5, (byte) 0x8a, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{0x4, (byte) 0x8f, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int cmd = getCmd(v.getId(), CMD_ID);
        if (cmd != 0) {
            sendCanboxInfo0xE1((cmd & 0xff00) >> 8, (cmd & 0xff));
        } else {
            cmd = getCmd(v.getId(), CMD_ID1);
            if (cmd != 0) {
                int param = 0;
                if (v.isSelected()) {
                    param = 0;
                } else {
                    param = 1;
                }
                sendCanboxInfo0xE1(cmd, param);
            } else {
                cmd = getCmd(v.getId(), CMD_ID2);
                sendCanboxInfo0xE1(cmd, 1);
                Util.doSleep(200);
                sendCanboxInfo0xE1(cmd, 0);
            }
        }


    }

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    private void updateView(byte[] buf) {

        // if ((buf[0] & 0xff) == 0x21) {
        // if ((buf[6] & 0x40) != 0) {
        // mShow = true;
        // } else if (mShow) {
        // getActivity().finish();
        // }
        // }
        if ((buf[11] & 0x10) != 0) {
            ((TextView) mMainView.findViewById(R.id.rearstat)).setText(R.string.rear_seat_air_conditioning_control_open_auto);
        } else {
            ((TextView) mMainView.findViewById(R.id.rearstat)).setText(R.string.rear_seat_air_conditioning_control_close_auto);
        }

        updateSelect(R.id.wind_up, (buf[11] & 0x80));
        updateSelect(R.id.wind_horizontal, (buf[11] & 0x40));
        updateSelect(R.id.wind_down, (buf[11] & 0x20));

        int speed = buf[11] & 0xf;

        ((TextView) mMainView.findViewById(R.id.rearfanspeed)).setText(getString(R.string.rear_wind_speed) + " " + speed);
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo0x90(0x21);
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
                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {
                            if ("ac".equals(cmd)) {

                                mCommonUpdateView.postChanged(CommonUpdateView.MESSAGE_AIR_CONDITION, 0, 0, buf);

                                try {
                                    updateView(buf);
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
