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
import android.widget.ImageView;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.Util;

/**
 * This activity plays a video from a specified URI.
 */
public class CD154 extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";
    private final static int[] INIT_CMDS = {0x23};
    byte mPlayStatus = 0;
    byte mRepeatMode = 0;
    private View mMainView;
    private int totalSong = -1;
    private int curSong = 0;

    // private void sendCanboxInfo0xc7(int d0, int d1, int d2, int d3, int d4) {
    // byte[] buf = new byte[] { (byte) 0xc7, 0x5, (byte) d0, (byte) d1,
    // (byte) d2, (byte) d3, (byte) d4 };
    // BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    // }
    private boolean mPaused = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {

                byte[] buf = new byte[]{(byte) 0x1f, 0x1, (byte) (msg.what & 0xff)};
                BroadcastUtil.sendCanboxInfo(getActivity(), buf);
            }
        }
    };
    private BroadcastReceiver mReceiver;
    private int mSource = MyCmd.SOURCE_NONE;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // setContentView(R.layout.jeep_car_cd_player);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.mazda3_simple_car_cd_player, container, false);
        mMainView.findViewById(R.id.list_view).setVisibility(View.GONE);
        mMainView.findViewById(R.id.layout_id3).setVisibility(View.INVISIBLE);
        mMainView.findViewById(R.id.albums_layout).setVisibility(View.VISIBLE);
        registerListener();
        return mMainView;
    }

    private void sendCanboxInfo0xc7(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xa3, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0xc7(int d0) {
        sendCanboxInfo0xc7(d0, 1);
    }

    // private final static int[] INIT_CMDS = { 0x0400, 0x0500, 0x0600, 0x0601,
    // 0x0602, 0x0603 };

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{0x3, (byte) 0x6a, 0x5, 0x1, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.repeat) {
            if ((mRepeatMode & 0x01) == 0) {
                sendCanboxInfo0xc7(0x6);
            } else {
                sendCanboxInfo0xc7(0x8);
            }
        } else if (id == R.id.prev) {
            sendCanboxInfo0xc7(0x1);
        } else if (id == R.id.pp) {
            if (mPlayStatus == 1) {
                sendCanboxInfo0xc7(0x4);
            } else {
                sendCanboxInfo0xc7(0x3);
            }
        } else if (id == R.id.next) {
            sendCanboxInfo0xc7(0x2);
        } else if (id == R.id.ff) {
            sendCanboxInfo0xc7(0x9);
        } else if (id == R.id.fr) {
            sendCanboxInfo0xc7(0xa);
        } else if (id == R.id.shuffle) {
            if ((mRepeatMode & 0x02) == 0) {
                sendCanboxInfo0xc7(0x5);
            } else {
                sendCanboxInfo0xc7(0x7);
            }
        }
    }

    private void updateView(byte[] buf) {

        switch (buf[0] & 0xff) {
            case 0x23: {

                mPlayStatus = buf[2];
                mRepeatMode = buf[3];

                if (mPlayStatus == 1) {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(1);
                } else {
                    ((ImageView) mMainView.findViewById(R.id.pp)).getDrawable().setLevel(0);
                }

                if ((mRepeatMode & 0x01) == 0) {
                    mMainView.findViewById(R.id.repeat1_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.repeat1_tag).setVisibility(View.VISIBLE);
                }
                if ((mRepeatMode & 0x02) == 0) {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
                } else {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
                }

                curSong = ((buf[8] & 0xff) << 8) | (buf[9] & 0xff);
                totalSong = ((buf[10] & 0xff) << 8) | (buf[11] & 0xff);
                String s2 = "";
                if (curSong > 0 && curSong <= 999) {
                    s2 = curSong + "/" + totalSong;
                }
                ((TextView) mMainView.findViewById(R.id.num)).setText(s2);

                curSong = ((buf[4] & 0xff) << 8) | (buf[5] & 0xff);
                totalSong = ((buf[6] & 0xff) << 8) | (buf[7] & 0xff);

                String s = String.format("%02d:%02d:%02d/%02d:%02d:%02d", curSong / 3600, curSong / 60, curSong % 60, totalSong / 3600, totalSong / 60, totalSong % 60);

                ((TextView) mMainView.findViewById(R.id.time)).setText(s);
            }
            break;
            case 0xa5:
                byte[] b = new byte[buf.length - 6];
                Util.byteArrayCopy(b, buf, 0, 5, b.length);
                String s = "";

                s = getString(0, b);

                if (buf[2] == 0x1) {
                    ((TextView) mMainView.findViewById(R.id.song)).setText(s);
                } else if (buf[2] == 0x2) {
                    ((TextView) mMainView.findViewById(R.id.singer)).setText(s);
                } else if (buf[2] == 0x3) {
                    ((TextView) mMainView.findViewById(R.id.albums)).setText(s);
                }
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

        byte[] buf = new byte[]{(byte) 0x90, 0x2, 0x4, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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
        sendCanboxInfo0xc7(0xb, 0);
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_MX51);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;

        byte[] buf = new byte[]{(byte) 0x90, 0x2, 0x3, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        requestInitData();
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
                                //							int source = intent.getIntExtra(
                                //									MyCmd.EXTRA_COMMON_DATA, 0);
                                //							if (mSource == MyCmd.SOURCE_AUX
                                //									&& source != MyCmd.SOURCE_AUX) {
                                //								sendCanboxInfo0xc7(0xb, 0);
                                //							}
                                //							mSource = source;
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

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }
}
