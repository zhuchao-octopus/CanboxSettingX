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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class Radio149 extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    private View mMainView;

    private ListView mListViewCD;

    private MyListViewAdapterRadio mMyListViewAdapter;

    private ListView mListViewPreset;

    private MyListViewAdapterRadio mMyListViewAdapterPreset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.crown12_radio, container, false);

        mListViewCD = (ListView) mMainView.findViewById(R.id.liststations);
        mListViewPreset = (ListView) mMainView.findViewById(R.id.listpreset);
        initUI();
        return mMainView;
    }

    private void initUI() {
        if (mCarType == 0) {
            showUI(0);
        } else {
            showUI(1);
        }
    }

    private void showUI(int type) {
        if (mCarType == 0) {
            type = 0;
        }

        if (type == 0) {
            //			mMainView.findViewById(R.id.other).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.radio).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.fmam).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.line2).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.radio_function_button_scan)
            //					.setVisibility(View.VISIBLE);
        } else {
            //			mMainView.findViewById(R.id.other).setVisibility(View.VISIBLE);
            //			mMainView.findViewById(R.id.radio).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.fmam).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.line1).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.line2).setVisibility(View.GONE);
            //			mMainView.findViewById(R.id.radio_function_button_scan)
            //					.setVisibility(View.GONE);
        }
    }

    private void updateSaveList(int index, String name, int freq) {

        if (mMyListViewAdapter == null) {
            mMyListViewAdapter = new MyListViewAdapterRadio(getActivity(), R.layout.tl_list);
            mListViewCD.setAdapter(mMyListViewAdapter);

            mListViewCD.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int index = mMyListViewAdapter.getSelectIndex(postion);
                    if (index != -1) {

                        sendCanboxInfo0x83(0xb, index);

                    }
                }
            });

        }

        if (mMyListViewAdapter != null) {
            mMyListViewAdapter.addList(index, name, freq);
        }

    }

    private void updatePresetList(int index, String name, int freq) {

        if (mMyListViewAdapterPreset == null) {
            mMyListViewAdapterPreset = new MyListViewAdapterRadio(getActivity(), R.layout.tl_list);
            mListViewPreset.setAdapter(mMyListViewAdapterPreset);

            mListViewPreset.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int index = mMyListViewAdapterPreset.getSelectIndex(postion);
                    if (index != -1) {
                        index++;
                        if (mBaud == 0x11) {
                            sendCanboxInfo(0x8a, 0x4, index);
                        } else {

                            sendCanboxInfo(0x8a, 0x5, index);
                        }

                    }
                }
            });


            mListViewPreset.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    position++;
                    sendCanboxInfo(0x8a, 0x3, position);
                    return true;
                }

            });

        }

        if (mMyListViewAdapterPreset != null) {
            mMyListViewAdapterPreset.addList(index, name, freq);
        }

    }

    private void sendCanboxInfo0x83(int d0) {
        sendCanboxInfo0x83(d0, 0);
    }

    private void sendCanboxInfo0xC5(int d0) {
        byte[] buf = new byte[]{(byte) 0xc5, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x83(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x83, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x2, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.radio_prev) {
            sendCanboxInfo(0x74, 0x9, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x9, 0);
        } else if (id == R.id.radio_next) {
            sendCanboxInfo(0x74, 0x8, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x8, 0);
        } else if (id == R.id.radio_function_button_scan) {
            sendCanboxInfo(0x74, 0x12, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x12, 0);
        } else if (id == R.id.am) {
            sendCanboxInfo(0x74, 0x4, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x4, 0);
        } else if (id == R.id.fm) {
            sendCanboxInfo(0x74, 0x5, 1);
            Util.doSleep(200);
            sendCanboxInfo(0x74, 0x5, 0);
        }
    }

    private int mListIndex;
    private int mStatus;

    private void updateStatus(byte b) {
        mStatus = (b & 0xf0);
        mListIndex = (b & 0x0f);
        String s = "";
        if ((b & 0x20) != 0) {
            s = getActivity().getString(R.string.scaning);
        } else {
            if ((b & 0x10) != 0) {

                s = getActivity().getString(R.string.refreshing);
            } else {
                s = "";
            }
        }

        ((TextView) mMainView.findViewById(R.id.status)).setText(s);

        if ((b & 0x40) != 0) {
            ((TextView) mMainView.findViewById(R.id.st)).setVisibility(View.VISIBLE);
        } else {

            ((TextView) mMainView.findViewById(R.id.st)).setVisibility(View.GONE);
        }
    }

    private byte mBaud;

    private void updateView(byte[] buf) {
        String s = "";
        switch (buf[0]) {

            case 0x1c: {
                if (buf[2] == 1) {

                    sendCanboxInfo0x90(0x10);
                    int freq = ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);
                    String s2;
                    String s3;
                    mBaud = buf[4];
                    if ((buf[4] & 0xff) == 1 || (buf[4] & 0xff) == 2) {
                        s = "FM";
                        s3 = "MHz";
                        if ((buf[4] & 0xff) == 2) {
                            s += 2;
                        }
                        s2 = String.format("%d.%d", freq / 10, freq % 10);
                        sendCanboxInfo0x8f(0x1);
                    } else {
                        s = "AM";
                        s3 = "KHz";
                        s2 = freq + "";
                        sendCanboxInfo0x8f(0x2);
                    }
                    ((TextView) mMainView.findViewById(R.id.freq_baud)).setText(s);
                    ((TextView) mMainView.findViewById(R.id.freq_text)).setText(s2);
                    ((TextView) mMainView.findViewById(R.id.freq_unit)).setText(s3);

                    s = "";
                    if (buf[7] > 0 && buf[7] <= 6) {
                        s = getString(R.string.preset) + buf[7];
                    }

                    if (buf[8] == 1) {
                        s += " " + getString(R.string.scaning);
                    } else if (buf[8] == 2) {
                        s += " " + getString(R.string.refreshing);
                    }

                    ((TextView) mMainView.findViewById(R.id.status)).setText(s);
                }
            }
            break;

            case 0x10: {
                int num;

                num = 6;

                for (int i = 0; i < num; ++i) {
                    int freq = ((buf[3 + i * 2] & 0xff) << 8) | (buf[4 + i * 2] & 0xff);
                    if ((buf[2] & 0x80) == 0) {
                        s = String.format("%d.%d", freq / 10, freq % 10);
                    } else {
                        s = freq + "";
                    }

                    updatePresetList(i, s, freq);
                }

                break;

            }
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
        sendCanboxInfo0x8f(0x4);
    }

    private void sendCanboxInfo0x8f(int d0) {
        byte[] buf = new byte[]{(byte) 0x8f, 0x3, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    @Override
    public void onResume() {
        registerListener();
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        sendCanboxInfo0x90(0x1c);
        Util.doSleep(200);
        sendCanboxInfo0x90(0x10);
        Util.doSleep(30);
        sendCanboxInfo0x8f(0x1);

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
