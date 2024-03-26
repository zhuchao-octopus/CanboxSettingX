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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class ChengRaiselACFragment extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    private View mMainView;

    private CommonUpdateView mCommonUpdateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.ac_qicheng_raise, container, false);

        mCommonUpdateView = new CommonUpdateView(mMainView, mMsgInterface);
        return mMainView;
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

    private final static int[][] CMD_ID = new int[][]{

            {R.id.power, 0x0}, {R.id.ac, 0x1},

            {R.id.inner_loop_in, 0x4}, {R.id.inner_loop_out, 0x5},


            {R.id.rear, 0x15}, {R.id.mode, 0x11},


    };

    private void sendCanboxInf(int d0, int d1) {
        byte[] buf = new byte[]{
                (byte) 0xa8, 0x2, (byte) d0, (byte) d1
        };
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {

                byte[] buf = new byte[]{
                        (byte) 0xa8, 0x2, (byte) (CMD_ID[i][1] & 0xff), (byte) 1
                };
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
                Util.doSleep(200);
                buf[3] = 0;
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);

            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.wind_minus) {
            int wind = mCommonUpdateView.getWind();
            if (wind > 0) {
                wind--;
            }
            sendCanboxInf(0x12, wind);
        } else if (v.getId() == R.id.wind_add) {
            int wind = mCommonUpdateView.getWind();
            if (wind < 7) {
                wind++;
            }

            sendCanboxInf(0x12, wind);
        }
        if (v.getId() == R.id.con_left_temp_down) {
            int wind = mCommonUpdateView.getTempLeft();
            if (wind > 1 && wind <= 15) {
                wind--;
            } else {
                wind = 1;
            }
            sendCanboxInf(0x13, wind);
        } else if (v.getId() == R.id.con_left_temp_up) {
            int wind = mCommonUpdateView.getTempLeft();
            if (wind > 0 && wind < 15) {
                wind++;
            } else {
                wind = 1;
            }

            sendCanboxInf(0x13, wind);
        }
        if (v.getId() == R.id.con_right_temp_down) {
            int wind = mCommonUpdateView.getTempRight();
            if (wind > 1 && wind <= 15) {
                wind--;
            } else {
                wind = 1;
            }
            sendCanboxInf(0x14, wind);
        } else if (v.getId() == R.id.con_right_temp_up) {
            int wind = mCommonUpdateView.getTempRight();
            if (wind > 0 && wind < 15) {
                wind++;
            } else {
                wind = 1;
            }

            sendCanboxInf(0x14, wind);
        } else {
            sendCmd(v.getId());
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

    private BroadcastReceiver mReceiver;

    private void unregisterListener() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private int power = 0;

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
