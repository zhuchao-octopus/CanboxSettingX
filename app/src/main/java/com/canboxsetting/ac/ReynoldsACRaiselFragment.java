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
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class ReynoldsACRaiselFragment extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{

            {R.id.power, 0x00}, {R.id.ac, 0x1}, {R.id.ac_auto, 0x2}, {R.id.dual, 0x3}, {R.id.inner_loop, 0x15}, {R.id.inner_loop_auto, 0x16}, {R.id.max, 0x6}, {R.id.rear, 0x17},

            {R.id.wind_horizontal1, 0x7}, {R.id.wind_down1, 0x9}, {R.id.wind_up1, 0x18}, {R.id.wind_minus, 0xc}, {R.id.wind_add, 0xb}, {R.id.con_left_temp_up, 0xd}, {R.id.con_left_temp_down, 0xe}, {R.id.con_right_temp_up, 0xf}, {R.id.con_right_temp_down, 0x10}, {R.id.tab_wind_mode, 0xf},

            {R.id.left_seat_heat, 0x24}, {R.id.right_seat_heat, 0x25},

            {R.id.left_seat_refrigeration, 0x26}, {R.id.right_seat_refrigeration, 0x27},

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

        mMainView = inflater.inflate(R.layout.ac_reylnolds_raise, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);

        return mMainView;
    }

    private void initView() {
        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        int carConfig = 0;
        String mProId = null;
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_CONFIG)) {
                        carConfig = Integer.valueOf(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID)) {
                        mProId = ss[i].substring(1);
                    }
                }
            } catch (Exception e) {

            }
        }

        String mModelId = null;
        if (mProId != null && mProId.length() >= 4) {
            int start = 0;
            int end = 0;
            if (mProId.charAt(1) == '0' && mProId.charAt(2) != 0) {
                end = 1;
            } else if (mProId.charAt(2) == '0') {
                end = 2;
            }

            start = end + 1;

            if (mProId.contains("-")) {
                String[] ss = mProId.substring(start).split("-");
                mModelId = ss[1];
            } else {
                if ((mProId.length() - start) == 2) {
                    mModelId = mProId.substring(start + 1, start + 2);
                } else if ((mProId.length() - start) == 4) {
                    mModelId = mProId.substring(start + 2, start + 4);
                } else if ((mProId.length() - start) == 3) {
                    mModelId = mProId.substring(start + 2, start + 3);
                }
            }
        }

        if (carConfig != 0) {
            mMainView.findViewById(R.id.tab_wind_mode).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.right_temp_parent).setVisibility(
            //					View.VISIBLE);

        } else {
            //			if (mModelId != null) {
            //				if (mModelId.equals("3") || mModelId.equals("5")
            //						|| mModelId.equals("14")) {

            mMainView.findViewById(R.id.tab_wind_mode).setVisibility(View.VISIBLE);
            //					mMainView.findViewById(R.id.right_temp_parent)
            //							.setVisibility(View.GONE);
            //				}
            //			}
        }
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x1, (byte) d0,};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x95(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xa8, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendKey(int key) {
        sendCanboxInfo0x95(key, 1);
        Util.doSleep(200);
        sendCanboxInfo0x95(key, 0);
    }

    private void sendCmd(int id) {
        if (id == R.id.con_right_temp_up || id == R.id.con_right_temp_down) {
            if (mMainView.findViewById(R.id.tab_wind_mode).getVisibility() == View.VISIBLE) {
                return;
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
        super.onResume();
        initView();
        sendCanboxInfo0x90(0x23);
        Util.doSleep(200);
        sendCanboxInfo0x90(0x35);
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
