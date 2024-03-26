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

package com.canboxsetting.cd;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.common.adapter.MyListViewAdapterCD;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class JeepCarCDXinbasFragment extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // setContentView(R.layout.jeep_car_cd_player);

    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.jeep_car_cd_player, container, false);

        mListViewCD = (ListView) mMainView.findViewById(R.id.list_view);

        return mMainView;
    }

    private MyListViewAdapterCD mMyListViewAdapter;
    private ListView mListViewCD;
    private final static String CD_TITLE = "    CD   Track    ";
    private int mTrackNum = 0;

    private void updateCDList() {
        if (mMyListViewAdapter != null) {
            mTrackNum = mMyListViewAdapter.getCount();
        }
        if (mTrackNum > 0) {

            mListViewCD.setVisibility(View.VISIBLE);

            // updateCDPlayingList();
        } else {
            mListViewCD.setVisibility(View.GONE);
        }
    }

    private void updateCDList(int index, String name) {

        if (mMyListViewAdapter == null) {
            mMyListViewAdapter = new MyListViewAdapterCD(getActivity(), R.layout.tl_list);
            mListViewCD.setAdapter(mMyListViewAdapter);

            mListViewCD.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
                    int index = mMyListViewAdapter.getSelectIndex(postion);
                    if (index != -1) {

                        byte[] buf = new byte[]{
                                (byte) 0x86, 0x3, (byte) 0xf, (byte) ((index & 0xff00) >> 8), (byte) ((index & 0xff) >> 0)
                        };
                        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

                    }
                }
            });

        }

        if (mMyListViewAdapter != null) {
            mMyListViewAdapter.addList(index, name);
        }
        updateCDList();

    }

    private void sendCanboxInfo0xC8(int d0) {
        byte[] buf = new byte[]{(byte) 0x86, 0x3, (byte) d0, 0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x4, (byte) d0, 0, 0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    int mPlayStatus = 0;
    int mRepeatMode = 0;

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.repeat) {
            sendCanboxInfo0xC8(0x11);
        } else if (id == R.id.prev) {
            sendCanboxInfo0xC8(0x2);
        } else if (id == R.id.pp) {
            if (mPlayStatus != 1) {
                sendCanboxInfo0xC8(0x13);
            } else {
                sendCanboxInfo0xC8(0x14);
            }
        } else if (id == R.id.next) {
            sendCanboxInfo0xC8(0x1);
        } else if (id == R.id.ff) {
            sendCanboxInfo0xC8(0x4);
        } else if (id == R.id.fr) {
            sendCanboxInfo0xC8(0x3);
        } else if (id == R.id.shuffle) {
            sendCanboxInfo0xC8(0x8);
        }
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case 0xd: {
                mPlayStatus = buf[2] & 0xff;
                mRepeatMode = (buf[2] & 0xf0) >> 4;
                if (mPlayStatus == 3) {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(1);
                } else {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(0);
                }

                if ((mRepeatMode & 0x1) == 0) {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.VISIBLE);
                }
                if ((mRepeatMode & 0x2) == 0) {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
                }
                String s1 = "";
                String s2 = "";

                s2 += ((buf[3] & 0xff) << 8) | (buf[4] & 0xff);
                s1 += ((buf[5] & 0xff) << 8) | (buf[6] & 0xff);

                ((TextView) mMainView.findViewById(R.id.num)).setText(s2 + "/" + s1);

                int totalTime = ((buf[7] & 0xff) << 8) | (buf[8] & 0xff);
                int curTime = ((buf[9] & 0xff) << 8) | (buf[10] & 0xff);
                String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d", totalTime / 3600, (totalTime / 60) % 60, totalTime % 60, curTime / 3600, (curTime / 60) % 60, curTime % 60);

                ((TextView) mMainView.findViewById(R.id.time)).setText(s);
            }
            break;
            case 0xe:
                byte[] b = new byte[buf.length - 6];
                Util.byteArrayCopy(b, buf, 0, 5, b.length);
                String s = getId3String(0x11, b);
                if (buf[2] == 0x1) {
                    ((TextView) mMainView.findViewById(R.id.song)).setText(s);
                } else if (buf[2] == 0x2) {
                    ((TextView) mMainView.findViewById(R.id.albums)).setText(s);
                } else if (buf[2] == 0x3) {
                    ((TextView) mMainView.findViewById(R.id.singer)).setText(s);
                } else if ((buf[2] & 0xff) == 0x80) {
                    updateCDList(((buf[3] & 0xff) << 8) | (buf[4] & 0xff), s);
                }
                break;
        }
    }

    private String getId3String(int type, byte[] buf) {
        String s = "";
        try {
            if (type == 0x1) {
                s = new String(buf);
            } else if (type == 0x2) {
                s = new String(buf, "GB2312");
            } else if (type == 0x10) {
                for (int i = 0; i < buf.length; i += 2) {
                    byte b = buf[i];
                    buf[i] = buf[i + 1];
                    buf[i + 1] = b;
                }
                s = new String(buf, "UNICODE");
            } else {// if (type == 0x11) {
                s = new String(buf, "UNICODE");
            }
        } catch (Exception e) {

        }

        return s;
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        sendCanboxInfo0xC8(0x82);
        //		sendCanboxInfo0x90(0x42);
        //		Util.doSleep(30);
        //		sendCanboxInfo0x90(0x43);
        //		Util.doSleep(30);
        //		sendCanboxInfo0x90(0x44);
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
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

            getActivity().registerReceiver(mReceiver, iFilter);
        }
    }
}
