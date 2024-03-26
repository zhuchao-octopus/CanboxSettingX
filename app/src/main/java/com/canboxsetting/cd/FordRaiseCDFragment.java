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
import java.util.Locale;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.array;
import com.canboxsetting.R.drawable;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity plays a video from a specified URI.
 */
public class FordRaiseCDFragment extends MyFragment {
    private static final String TAG = "JeepCarCDFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.rx330_cd_player, container, false);
        ((ImageView) mMainView.findViewById(R.id.pp)).setImageResource(R.drawable.play);
        return mMainView;
    }

    private void sendCanboxInfo0xc6(int d0) {
        byte[] buf = new byte[]{(byte) 0xc6, 0x2, (byte) 0xa9, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfoKey(int d0) {
        sendCanboxInfo0xc6(d0);
        Util.doSleep(10);
        sendCanboxInfo0xc6(0x0);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void next() {
        sendCanboxInfoKey(0x22);

    }

    private void prev() {

        sendCanboxInfoKey(0x21);
    }

    private void pp() {
        if (mPlayStatus == 0) {
            sendCanboxInfoKey(0x24);
        } else {
            sendCanboxInfoKey(0x23);
        }

    }

    private byte mPlayStatus = 0;
    private byte mRepeatStatus = 0;

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.shuffle) {
            if ((mRepeatStatus & 0x40) != 0) {
                sendCanboxInfoKey(0x26);
            } else {
                sendCanboxInfoKey(0x25);
            }
        } else if (id == R.id.repeat) {
            if ((mRepeatStatus & 0x80) != 0) {
                sendCanboxInfoKey(0x28);
            } else {
                sendCanboxInfoKey(0x27);
            }
        } else if (id == R.id.prev) {
            prev();
        } else if (id == R.id.pp) {
            pp();
        } else if (id == R.id.next) {
            next();
        } else if (id == R.id.ff) {
            sendCanboxInfoKey(0x2b);
        } else if (id == R.id.fr) {
            sendCanboxInfoKey(0x2a);
        }
    }

    private Toast mToast;

    private void updateView(byte[] buf) {
        String s;
        switch (buf[0]) {
            case 0x67: {
                mPlayStatus = buf[3];
                mRepeatStatus = buf[4];
                if (mPlayStatus == 0) {
                    ((ImageView) mMainView.findViewById(R.id.pp)).setImageResource(R.drawable.play);
                } else {
                    ((ImageView) mMainView.findViewById(R.id.pp)).setImageResource(R.drawable.pause);
                }
                if ((mRepeatStatus & 0x80) != 0) {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.VISIBLE);
                } else {
                    mMainView.findViewById(R.id.repeat_tag).setVisibility(View.GONE);
                }
                if ((mRepeatStatus & 0x40) != 0) {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.VISIBLE);
                } else {
                    mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
                }
                String[] ss = getResources().getStringArray(R.array.raise_ford_cd_states);
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                if ((buf[2] & 0xff) == 0xff) {
                    s = ss[ss.length - 1];
                } else {
                    s = ss[buf[2]];
                }
                mToast = Toast.makeText(getActivity(), s, Toast.LENGTH_LONG);
                mToast.show();
                break;
            }

            case 0x65: {

                if (buf[2] == 2) {
                    s = String.format("%d/%d", ((buf[3] & 0xff) << 8) | (buf[4] & 0xff), ((buf[5] & 0xff) << 8) | (buf[6] & 0xff));
                    ((TextView) mMainView.findViewById(R.id.num)).setText(s);
                }
                break;
            }
            case 0x66: {
                s = String.format("%02d:%02d:%02d/%02d:%02d:%02d", (buf[5] & 0xff), (buf[6] & 0xff), (buf[7] & 0xff), (buf[2] & 0xff), (buf[3] & 0xff), (buf[4] & 0xff));

                ((TextView) mMainView.findViewById(R.id.time)).setText(s);

                break;
            }
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResume() {
        registerListener();

        byte[] buf = new byte[]{(byte) 0x83, 0x1, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);

        BroadcastUtil.sendToCarServiceSetSource(getActivity(), MyCmd.SOURCE_AUX);
        requestInitData();

        super.onResume();
    }

    private final static int[] INIT_CMDS = {0x65, 0x66, 0x67, 0x68};

    private void requestInitData() {
        for (int i = 0; i < INIT_CMDS.length; ++i) {
            mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], ((i + 1) * 200));
        }

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendCanboxInfo0x90(msg.what & 0xff);
        }
    };
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
                                Log.d(TAG, "canbox updateView err" + e);
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

    private int mSource = MyCmd.SOURCE_NONE;

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }

}
