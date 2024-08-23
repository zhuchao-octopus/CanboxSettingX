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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

/**
 * This activity plays a video from a specified URI.
 */
public class CD155 extends MyFragment {

    private View mMainView;
    private BroadcastReceiver mReceiver;
    private int mSource = MyCmd.SOURCE_NONE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.cd_nissan_raise, container, false);

        return mMainView;
    }

    private void setViewVisible(int id, int s) {

        View v = mMainView.findViewById(id);
        if (v != null) {
            if (s != 0) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String bcd2Str0xF(int b) {
        String c = "";
        if (b >= 0 && b < 10) {
            c = "" + b;
        } else if (b >= 0xa && b <= 0xf) {
            switch (b) {
                case 0xa:
                    c = "A";
                    break;
                case 0xb:
                    c = "B";
                    break;
                case 0xc:
                    c = "C";
                    break;
                case 0xd:
                    c = "D";
                    break;
                case 0xe:
                    c = "E";
                    break;
                case 0xf:
                    c = "F";
                    break;
            }
        }
        return c;
    }

    public String bcd2Str(byte b) {

        return (bcd2Str0xF((b & 0xF0) >> 4) + bcd2Str0xF((b & 0xF)));
    }

    private void updateView(byte[] buf) {
        String s = "";
        switch (buf[0]) {
            case 0x5:

                s = "" + buf[2];
                ((TextView) mMainView.findViewById(R.id.song)).setText(s);

                s = bcd2Str(buf[6]) + bcd2Str(buf[7]);
                ((TextView) mMainView.findViewById(R.id.albums)).setText(s);

                s = bcd2Str(buf[8]) + ":" + bcd2Str(buf[9]);
                ((TextView) mMainView.findViewById(R.id.time)).setText(s);

                int index = 0;
                s = null;
                switch (buf[3] & 0xf) {
                    case 0:
                        index = R.string.am_normal;
                        break;
                    case 1:
                        index = R.string.str_no_disc_loaded;
                        break;
                    case 2:
                        index = R.string.str_cd_busy;
                        break;
                    case 3:
                        index = R.string.gac_cd_state_load;
                        break;
                    case 4:
                        index = R.string.gac_cd_state_read;
                        break;
                    case 5:
                        s = "eject";
                        break;
                    case 6:
                        index = R.string.fast_forward;
                        break;
                    case 7:
                        index = R.string.fast_rewind;
                        break;
                    case 8:
                        s = "error";
                        break;
                    case 9:
                        s = "no data";
                        break;
                    case 10:
                        s = "no song";
                        break;
                    case 11:
                        s = "usb load";
                        break;
                    case 12:
                        s = "unsupported";
                        break;
                }
                if (index != 0) {
                    ((TextView) mMainView.findViewById(R.id.playstatus)).setText(index);
                } else if (s != null) {
                    ((TextView) mMainView.findViewById(R.id.playstatus)).setText(s);
                }
                break;
            // case 0x6: {
            // if ((buf[2] & 0x80) != 0) {
            // s += " " + "FOLDER";
            // }
            // if ((buf[2] & 0x40) != 0) {
            // s += " " + "WMA";
            // }
            // if ((buf[2] & 0x20) != 0) {
            // s += " " + "MP3";
            // }
            // if ((buf[2] & 0x10) != 0) {
            // s += " " + "SCAN";
            // }
            // ((TextView) mMainView.findViewById(R.id.status)).setText(s);
            //
            // mMainView.findViewById(R.id.shuffle_tag).setVisibility(View.GONE);
            // mMainView.findViewById(R.id.repeat_tag).setVisibility(View.GONE);
            // if ((buf[2] & 0x7) == 3) {
            // mMainView.findViewById(R.id.shuffle_tag).setVisibility(
            // View.VISIBLE);
            // }
            // if ((buf[2] & 0x7) == 1) {
            //
            // mMainView.findViewById(R.id.repeat_tag).setVisibility(
            // View.VISIBLE);
            // }
            //
            // break;
            // }
        }
    }

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();


        byte[] buf = new byte[]{(byte) 0xff, 0x1, 0x5, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
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

    public boolean isCurrentSource() {
        return (mSource == MyCmd.SOURCE_AUX);
    }

}
