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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class AC160 extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{{R.id.power, 0x01},

            {R.id.con_left_temp_up, 0x3}, {R.id.con_left_temp_down, 0x2}, {R.id.con_right_temp_up, 0x5}, {R.id.con_right_temp_down, 0x4},

            {R.id.wind_horizontal1, 0x7}, {R.id.wind_down1, 0x8},


            {R.id.wind_minus, 0x9}, {R.id.wind_add, 0xa},


            {R.id.dual, 0x10},


            {R.id.max, 0x13}, {R.id.rear, 0x14},


            {R.id.ac_auto, 0x15}, {R.id.ac_max, 0x16},

            {R.id.ac, 0x17}, {R.id.inner_loop, 0x19},


    };
    private CommonUpdateView mCommonUpdateView;
    private View invalidButton;
    private View mMainView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_byd_ods, container, false);
        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        initButtonTouch();

        invalidButton(R.id.ac_auto_rear);
        invalidButton(R.id.con_left_temp_rear_up);
        invalidButton(R.id.con_left_temp_rear_down);
        mMainView.findViewById(R.id.wind_horizontal_down_rear).setVisibility(View.GONE);
        //		mMainView.findViewById(R.id.reartemp).setVisibility(View.INVISIBLE);
        return mMainView;
    }

    private void invalidButton(int id) {
        ImageView iv = (ImageView) mMainView.findViewById(id);
        if (iv != null) {
            iv.setImageDrawable(null);
            iv.setClickable(false);
        }
    }

    private void sendCanboxInfo(int cmd, int d0, int d1) {
        byte[] buf = new byte[]{(byte) cmd, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxKeyInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xe0, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendCmd(msg.arg1, 2);
            mHandler.removeMessages(0);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(0, msg.arg1, 0), 400);

        }
    };

    private void initButtonTouch() {
        for (int i = 0; i < CMD_ID.length; ++i) {
            View v = mMainView.findViewById(CMD_ID[i][0]);
            if (v != null) {
                v.setOnTouchListener(mTouchListener);
            }
        }
    }

    private void sendCmd(int id, int down) {
        int cmd = getCmd(id);
        if (cmd != -1) {
            sendCanboxKeyInfo(cmd & 0xff, down);
        }
    }    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    sendCmd(v.getId(), 1);
                    mHandler.removeMessages(0);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(0, v.getId(), 0), 2000);
                    break;
                case KeyEvent.ACTION_UP:
                    sendCmd(v.getId(), 0);
                    mHandler.removeMessages(0);
                    break;
            }
            return false;
        }
    };

    private int getCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                return (CMD_ID[i][1] & 0xffffff);
            }
        }
        return -1;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.air_rear) {
            showRear(true);
        } else if (id == R.id.air_front) {

            showRear(false);
        } else {
            //			int cmd = getCmd(id);
            //			if (cmd != -1) {
            //				if ((cmd & 0xff00) == 0) {
            //					sendCanboxKey0x82(cmd & 0xff);
            //				} else {
            //					sendCanboxInfo((cmd & 0xff00) >> 8, cmd & 0xff, 1);
            //					Util.doSleep(200);
            //					sendCanboxInfo((cmd & 0xff00) >> 8, cmd & 0xff, 0);
            //				}
            //			}

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
