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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;

import java.util.Locale;

/**
 * This activity plays a video from a specified URI.
 */
public class VWMQBAirControlFragment extends MyFragment {
    private static final String TAG = "VWMQBAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{{R.id.air_title_ce_max, 0xbb03}, {R.id.ac_profile, 0xb100}, {R.id.air_title_ce_rear_lock, 0x1bc00}, {R.id.air_title_ce_aqs, 0x1b000}, {R.id.air_title_ce_ac_1, 0x1bd01}, {R.id.air_title_ce_inner_loop, 0xbe00}, {R.id.air_title_ce_auto_large, 0xbb01}, {R.id.air_title_ce_ac_max, 0xbb02}, {R.id.wheel, 0x1AC00}, {R.id.con_left_temp_up, 0xb801}, {R.id.con_left_temp_down, 0xb800}, {R.id.con_right_temp_up, 0xb901}, {R.id.con_right_temp_down, 0xb900}, {R.id.canbus21_mode1, 0x1b4ff}, {R.id.canbus21_mode2, 0x1b5ff}, {R.id.canbus21_mode5, 0x1b6ff}, {R.id.con_seathotleft, 0xad00}, {R.id.con_seathotright, 0xae00}, {R.id.air_title_sync, 0x1b3ff}, {R.id.icon_power, 0x1b2ff},

            {R.id.point0, 0xb701}, {R.id.point1, 0xb702}, {R.id.point2, 0xb703}, {R.id.point3, 0xb704}, {R.id.point4, 0xb705}, {R.id.point5, 0xb706}, {R.id.point6, 0xb707},

            {R.id.wind_minus, 0xb700}, {R.id.wind_add, 0xb700},

    };
    private View mMainView;
    private int mWindStep = 0;
    private int mInner = 0;
    private int mSeatHeatLeft = 0;
    private int mSeatHeatRight = 0;
    private int mACProfileLevel = 0;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_vw_mqb_simple, container, false);

        return mMainView;
    }

    private void sendCanboxInfo0xc6(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xc6, 0x2, (byte) d0, (byte) d1,};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private int getCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                return (CMD_ID[i][1] & 0xffffff);
            }
        }
        return 0;
    }

    public void onClick(View v) {
        int cmd = getCmd(v.getId());
        if (v.getId() == R.id.wind_minus) {
            if (mWindStep > 0) {
                mWindStep--;
            }
            cmd |= mWindStep;
        } else if (v.getId() == R.id.wind_add) {
            if (mWindStep < 7) {
                mWindStep++;
            }
            cmd |= mWindStep;
        } else if (v.getId() == R.id.ac_profile) {
            mACProfileLevel = (mACProfileLevel + 1) % 3;
            cmd |= mACProfileLevel;
        } else if (v.getId() == R.id.air_title_ce_inner_loop) {
            if (mInner == 0) {
                mInner = 1;
            } else {
                mInner = 0;
            }
            cmd |= mInner;
        } else if (v.getId() == R.id.con_seathotright) {
            mSeatHeatRight = (mSeatHeatRight + 1) % 4;
            cmd |= mSeatHeatRight;
        } else if (v.getId() == R.id.con_seathotleft) {
            mSeatHeatLeft = (mSeatHeatLeft + 1) % 4;
            cmd |= mSeatHeatLeft;
        } else {

            if ((cmd & 0x10000) != 0) {
                cmd &= ~0xff;
                if (!v.isSelected()) {
                    cmd |= 0x1;
                }
            } else if ((cmd & 0xff00) == 0xbb00) {
                if (v.isSelected()) {
                    cmd &= ~0xff;
                }
            } else {

            }
        }

        sendCanboxInfo0xc6((cmd & 0xff00) >> 8, (cmd & 0xff));
    }

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    private void setACProfile(int level) {
        TextView tv = (TextView) mMainView.findViewById(R.id.ac_profile);
        if (tv != null) {
            String[] array = getResources().getStringArray(R.array.air_con_profile_entries);

            if (level >= 0 && level < array.length) {
                String s = getString(R.string.ac_profile) + ": " + array[level];
                tv.setText(s);
            }

        }
        mACProfileLevel = level;
    }

    private void setSpeed(int speed) {

        for (int i = 0; i < 7; ++i) {
            View v = mMainView.findViewById(R.id.point0 + i);
            if (v != null) {
                if (i < speed) {
                    v.setSelected(true);
                } else {
                    v.setSelected(false);
                }
            }
        }
        mWindStep = speed;
    }

    private void setLoop(int loop) {

        ImageView v = (ImageView) mMainView.findViewById(R.id.air_title_ce_inner_loop);

        if (v != null) {
            if (loop == 0) {
                v.getDrawable().setLevel(0);
            } else {
                v.getDrawable().setLevel(1);
            }
        }
        mInner = loop;
    }

    private void setSeatheat(int id, int level) {

        ImageButton v = (ImageButton) mMainView.findViewById(id);
        int drawable;
        if (v != null) {
            if (id == R.id.con_seathotleft) {
                drawable = R.drawable.img_air_seathotleft0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.img_air_seathotleft1;
                        break;
                    case 2:
                        drawable = R.drawable.img_air_seathotleft2;
                        break;
                    case 3:
                        drawable = R.drawable.img_air_seathotleft3;
                        break;
                }
            } else {
                drawable = R.drawable.img_air_seathotright0;
                switch (level) {
                    case 1:
                        drawable = R.drawable.img_air_seathotright1;
                        break;
                    case 2:
                        drawable = R.drawable.img_air_seathotright2;
                        break;
                    case 3:
                        drawable = R.drawable.img_air_seathotright3;
                        break;
                }
            }

            v.setImageResource(drawable);
        }
    }

    private void setTemp(int id, int temperature, int unit) {
        TextView v = (TextView) mMainView.findViewById(id);
        String s;

        temperature = (byte) ((15.5f + (0.5f * temperature)) * 2);

        if (v != null) {
            if (temperature <= 31) {
                s = "LOW";
            } else if (temperature >= 62) {
                s = "HI";

            } else {
                if (unit == 0) {
                    s = String.format(Locale.ENGLISH, "%.1f%s", ((float) temperature) / 2, getResources().getString(R.string.temp_unic));
                } else {
                    s = String.format(Locale.ENGLISH, "%dF", (int) temperature);
                }
            }
            v.setText(s);
        }
    }

    private void updateView(byte[] buf) {
        switch (buf[0]) {
            case 0x21:
                // if (buf[2]&0x80)
                updateSelect(R.id.air_title_ce_ac_max, buf[6] & 0x08);
                updateSelect(R.id.air_title_ce_rear_lock, buf[2] & 0x01);
                updateSelect(R.id.air_title_ce_ac_1, buf[2] & 0x40);
                updateSelect(R.id.air_title_ce_auto_large, buf[2] & 0x08);
                updateSelect(R.id.air_title_ce_max, buf[2] & 0x02);
                updateSelect(R.id.air_title_ce_aqs, buf[6] & 0x20);
                updateSelect(R.id.wheel, buf[7] & 0x80);

                updateSelect(R.id.air_title_sync, buf[2] & 0x04);
                updateSelect(R.id.icon_power, buf[2] & 0x80);

                setSpeed((buf[3] & 0xf));
                setACProfile((buf[8] & 0x3));

                setSeatheat(R.id.con_seathotright, (buf[7] & 0x07));

                mSeatHeatRight = buf[7] & 0x7;
                setSeatheat(R.id.con_seathotleft, (buf[7] & 0x70) >> 4);

                mSeatHeatLeft = (buf[7] & 0x70) >> 4;

                setTemp(R.id.con_txt_left_temp, (buf[4] & 0xff), (buf[6] & 0x01));
                setTemp(R.id.con_txt_right_temp, (buf[5] & 0xff), (buf[6] & 0x01));

                updateSelect(R.id.canbus21_mode1, buf[3] & 0x40);
                updateSelect(R.id.canbus21_mode2, buf[3] & 0x20);
                updateSelect(R.id.canbus21_mode5, buf[3] & 0x80);

                setLoop(buf[2] & 0x20);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);

                super.callBack(0);
                break;
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
        sendCanboxInfo0x90(0x21);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                sendCanboxInfo0x90(0x21);
            }
        }, 1000);
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
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

            getActivity().registerReceiver(mReceiver, iFilter);
        }
    }
}
