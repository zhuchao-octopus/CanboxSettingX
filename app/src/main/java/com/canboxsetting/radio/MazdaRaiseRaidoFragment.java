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

package com.canboxsetting.radio;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.car.ui.GlobalDef;
import com.common.adapter.MyListViewAdapterCD;
import com.common.adapter.MyListViewAdapterRadio;
import com.common.util.AuxInUI;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class MazdaRaiseRaidoFragment extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    private View mMainView;

    private ListView mListViewCD;

    private MyListViewAdapterRadio mMyListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.mazda_radio, container, false);

        mListViewCD = (ListView) mMainView.findViewById(R.id.liststations);

        return mMainView;
    }

    private void updateCDList(int index, String name, int freq) {

        if (mMyListViewAdapter == null) {
            mMyListViewAdapter = new MyListViewAdapterRadio(getActivity(), R.layout.tl_list);
            mListViewCD.setAdapter(mMyListViewAdapter);

            mListViewCD.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int freq = mMyListViewAdapter.getSelectFreq(postion);
                    if (freq != -1) {

                        sendCanboxInfo0xA2((freq & 0xff00) >> 8, freq & 0xff);

                    }
                }
            });

        }

        if (mMyListViewAdapter != null) {
            mMyListViewAdapter.addList(index, name, freq);
        }

    }

    private void sendCanboxInfo0xA2(int d0) {
        byte[] buf = new byte[]{(byte) 0xA2, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0xA2(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xA2, 0x3, 0xa, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void next() {
        if (mSaveStatus == 6) {
            mSaveStatus = 1;
            sendCanboxInfo0xA2(7);
        } else {
            mSaveStatus = 6;
            sendCanboxInfo0xA2(6);
        }

    }

    private void prev() {
        if (mSaveStatus == 5) {
            mSaveStatus = 1;
            sendCanboxInfo0xA2(5);
        } else {
            mSaveStatus = 5;
            sendCanboxInfo0xA2(4);
        }

    }

    private byte mPlayStatus = 0;
    private byte mSaveStatus = 0;

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.update_stations_list) {
            if (mSaveStatus == 2) {
                mSaveStatus = 1;
                sendCanboxInfo0xA2(0);
            } else {
                mSaveStatus = 2;
                sendCanboxInfo0xA2(1);
            }
        } else if (id == R.id.radio_prev) {
            prev();
        } else if (id == R.id.radio_next) {
            next();
        } else if (id == R.id.radio_function_button_scan) {
            if (mSaveStatus == 3) {
                mSaveStatus = 1;
                sendCanboxInfo0xA2(3);
            } else {
                mSaveStatus = 3;
                sendCanboxInfo0xA2(2);
            }
        } else if (id == R.id.radio_step_up_button) {
            sendCanboxInfo0xA2(8);
        } else if (id == R.id.radio_step_down_button) {
            sendCanboxInfo0xA2(9);
        }
    }

    private void updateStatus(int b) {
        mSaveStatus = mPlayStatus = (byte) b;
        switch (b) {
            case 0:
                b = R.string.stop;
                break;
            case 1:
                b = R.string.play;
                break;
            case 2:
                b = R.string.refreshing_radio_list;
                break;
            case 3:
                b = R.string.scaning;
                break;
            case 4:
                b = R.string.scan_radio_playing;
                break;
            case 5:
                b = R.string.upward_scanning;
                break;
            case 6:
                b = R.string.downward_scanning;
                break;
        }

        ((TextView) mMainView.findViewById(R.id.status)).setText(b);
    }

    private void updateView(byte[] buf) {
        String s;
        switch (buf[0]) {
            case 0x72: {

                updateStatus(buf[3]);

                int freq = ((buf[4] & 0xff) << 8) | (buf[5] & 0xff);
                String s2;
                String s3;
                if (buf[2] == 0) {
                    s = "FM";
                    s3 = "MHz";
                    freq = (freq - 1) * 5 + 8750;
                    s2 = String.format("%d.%d", freq / 100, freq % 100);

                    byte[] buf_source = new byte[]{(byte) 0xa1, 0x2, (byte) 4, 1};
                    BroadcastUtil.sendCanboxInfo(getActivity(), buf_source);

                } else {
                    s = "AM";
                    s3 = "KHz";
                    freq = (freq - 1) * 9 + 522;
                    s2 = freq + "";


                    byte[] buf_source = new byte[]{(byte) 0xa1, 0x2, (byte) 4, 2};
                    BroadcastUtil.sendCanboxInfo(getActivity(), buf_source);

                }
                ((TextView) mMainView.findViewById(R.id.freq_baud)).setText(s);
                ((TextView) mMainView.findViewById(R.id.freq_text)).setText(s2);
                ((TextView) mMainView.findViewById(R.id.freq_unit)).setText(s3);
            }
            break;
            case 0x73: {
                int freq = ((buf[4] & 0xff) << 8) | (buf[5] & 0xff);
                int freq2 = freq;
                if (buf[2] == 0) {

                    freq = (freq - 1) * 5 + 8750;
                    s = String.format("%d.%d", freq / 100, freq % 100);
                } else {

                    freq = (freq - 1) * 9 + 522;
                    s = freq + "";
                }

                updateCDList(((buf[3] & 0xff)), s, freq2);
                break;
            }
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();


        byte[] buf_source = new byte[]{(byte) 0xa1, 0x2, (byte) 4, 3};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf_source);
    }

    @Override
    public void onResume() {
        registerListener();
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        sendCanboxInfo0x90(0x72);
        Util.doSleep(200);
        sendCanboxInfo0x90(0x61);


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

                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {
                            try {
                                updateView(buf);
                            } catch (Exception e) {
                                Log.d("aa", "!!!!!!!!" + e);
                            }
                        }
                    } else if (action.equals(MyCmd.BROADCAST_CAR_SERVICE_SEND)) {

                        int cmd = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
                        switch (cmd) {
                            case MyCmd.Cmd.SOURCE_CHANGE:
                            case MyCmd.Cmd.RETURN_CURRENT_SOURCE:
                                int source = intent.getIntExtra(MyCmd.EXTRA_COMMON_DATA, 0);
                                if (mSource == MyCmd.SOURCE_AUX && source != MyCmd.SOURCE_AUX) {
                                    // sendCanboxInfo0xc7(0xE);
                                    // } else {
                                    // sendCanboxInfo0xc7(0x0);
                                }
                                mSource = source;
                                break;
                        }
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);
            iFilter.addAction(MyCmd.BROADCAST_CAR_SERVICE_SEND);

            getActivity().registerReceiver(mReceiver, iFilter);
        }
    }

    private AuxInUI mAuxInUI;

    private int mSource = MyCmd.SOURCE_NONE;

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }

}
