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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.adapter.MyListViewAdapterCD;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class MazdaSimpleCarCDFragment extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";
    private final static int[] INIT_CMDS = {0xf0};
    private final static String CD_TITLE = "    CD   Track    ";
    byte mPlayStatus = 0;
    byte mRepeatMode = 0;
    private View mMainView;
    private int totalSong = -1;
    private int curSong = 0;
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                // sendCanboxInfo0x83((msg.what & 0xff00) >> 8, msg.what &
                // 0xff);
                sendCanboxInfo0xc7(msg.what & 0xff);
            }
        }
    };
    private BroadcastReceiver mReceiver;
    private int mSource = MyCmd.SOURCE_NONE;
    private MyListViewAdapterCD mMyListViewAdapter;
    private ListView mListViewCD;

    // private final static int[] INIT_CMDS = { 0x0400, 0x0500, 0x0600, 0x0601,
    // 0x0602, 0x0603 };
    private int mTrackNum = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // setContentView(R.layout.jeep_car_cd_player);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.mazda3_simple_car_cd_player, container, false);

        mListViewCD = (ListView) mMainView.findViewById(R.id.list_view);

        registerListener();
        return mMainView;
    }

    private void sendCanboxInfo0xc7(int d0) {
        byte[] buf = new byte[]{(byte) 0xc7, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0xc7(int d0, int d1, int d2, int d3, int d4) {
        byte[] buf = new byte[]{(byte) 0xc7, 0x5, (byte) d0, (byte) d1, (byte) d2, (byte) d3, (byte) d4};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.repeat) {
            if ((mRepeatMode & 0x01) == 0) {
                sendCanboxInfo0xc7(0x7);
            } else {
                sendCanboxInfo0xc7(0x8);
            }
        } else if (id == R.id.prev) {
            sendCanboxInfo0xc7(0x4);
        } else if (id == R.id.pp) {
            if (mPlayStatus == 5) {
                sendCanboxInfo0xc7(0x1);
            } else {
                sendCanboxInfo0xc7(0x2);
            }
        } else if (id == R.id.next) {
            sendCanboxInfo0xc7(0x3);
        } else if (id == R.id.ff) {
            sendCanboxInfo0xc7(0x5);
        } else if (id == R.id.fr) {
            sendCanboxInfo0xc7(0x6);
        } else if (id == R.id.shuffle) {
            if ((mRepeatMode & 0x02) == 0) {
                sendCanboxInfo0xc7(0x9);
            } else {
                sendCanboxInfo0xc7(0xa);
            }
        }
    }

    private void updateView(byte[] buf) {

        switch (buf[0]) {
            case 0x25: {

                mPlayStatus = buf[2];
                mRepeatMode = buf[3];

                if (mPlayStatus == 5) {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(1);
                } else {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(0);
                }

                if ((mRepeatMode & 0x01) == 0) {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.VISIBLE);
                }
                if ((mRepeatMode & 0x02) == 0) {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
                }
                break;
            }
            case 0x26: {

                curSong = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
                String s2 = "";
                if (curSong > 0 && curSong <= 999) {
                    s2 += curSong;
                }
                ((TextView) mMainView.findViewById(R.id.num)).setText(s2);

                String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d", buf[7], buf[8], buf[9], buf[4], buf[5], buf[6]);

                ((TextView) mMainView.findViewById(R.id.time)).setText(s);
            }
            break;
            case 0x27:
                byte[] b = new byte[buf.length - 5];
                Util.byteArrayCopy(b, buf, 0, 4, b.length);
                String s = "";
                if ((buf[3] & 0xf) == 1) {
                    s = getString((buf[3] & 0xf0) >> 4, b);
                }
                if (buf[2] == 0x1) {
                    ((TextView) mMainView.findViewById(R.id.song)).setText(s);
                } else if (buf[2] == 0x2) {
                    // ((TextView) mMainView.findViewById(R.id.albums)).setText(s);
                } else if (buf[2] == 0x3) {
                    ((TextView) mMainView.findViewById(R.id.singer)).setText(s);
                }
                break;
            case 0x28:
                int index = ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
                b = new byte[buf.length - 6];
                Util.byteArrayCopy(b, buf, 0, 5, b.length);
                s = "";
                if ((buf[4] & 0xf) == 1) {
                    s = getString((buf[4] & 0xf0) >> 4, b);
                }
                updateCDList(index, s);
                break;
        }
    }

    private String getString(int type, byte[] buf) {
        String s = "";
        try {
            if (type == 0x0) {
                s = new String(buf, "GBK");
            } else if (type == 0x3) {
                s = new String(buf, "utf-8");
            } else if (type == 0x2) {
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
        // unregisterListener();
        mPaused = true;
        super.onPause();
    }

    private void requestInitData() {
        // mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterListener();
        sendCanboxInfo0xc7(0x0);
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_MX51);
    }

    @Override
    public void onResume() {
        mPaused = false;
        // requestInitData();

        updateCDList();
        sendCanboxInfo0xc7(0xf0);

        sendCanboxInfo0xc7(0x10, 0, 0, 0, 16);
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
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
                                    sendCanboxInfo0xc7(0x0);
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
                        sendCanboxInfo0xc7(0x11, (byte) ((index & 0xff00) >> 8), (byte) ((index & 0xff) >> 0), 0, 0);
                    }
                }
            });

        }

        if (mMyListViewAdapter != null) {
            mMyListViewAdapter.addList(index, name);
        }
        updateCDList();

    }

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }
}
