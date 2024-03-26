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

package com.canboxsetting.set;

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
public class X30RaiseAirControlFragment extends MyFragment {
    private static final String TAG = "X30RaiseAirControlFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_x30_raise, container, false);

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);

        String[] ss = mCanboxType.split(",");
        for (int i = 1; i < ss.length; ++i) {
            if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE)) {
                try {
                    mCarType = Integer.valueOf(ss[i].substring(1));
                } catch (Exception e) {

                }
            }
        }

        updateAutoManualView();
        return mMainView;
    }

    boolean mPaused = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mPaused) {
                sendCanboxInfo0xA8((msg.arg1), msg.arg2);
            }
        }
    };

    private void sendCanboxInfo0xA8(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0xa8, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private final static int[][] CMD_ID = new int[][]{
            {R.id.icon_power, 0x0}, {R.id.air_title_ce_ac_1, 0x1}, {R.id.air_title_ce_auto_large, 0x02}, {R.id.air_title_ce_max, 0x06}, {R.id.canbus21_mode1, 0x07}, {R.id.canbus21_mode2, 0x08},
            {R.id.canbus21_mode3, 0x09}, {R.id.canbus21_mode4, 0x0a}, {R.id.canbus21_mode5, 0x11}, {R.id.wind_add, 0x0b}, {R.id.wind_minus, 0x0c}, {R.id.con_left_temp_up, 0x0d},
            {R.id.con_left_temp_down, 0x0e}, {R.id.air_title_ce_inner_loop, 0x15}, {R.id.air_title_ce_rear, 0x17},

    };

    private final static int[] AUTO_AIR_HIDE = new int[]{
            R.id.canbus21_mode1, R.id.canbus21_mode2, R.id.canbus21_mode3, R.id.canbus21_mode4
    };

    private final static int[] MANUAL_AIR_HIDE = new int[]{
            R.id.canbus21_mode5, R.id.icon_power, R.id.air_title_ce_auto_large
    };

    private void updateAutoManualView() {
        int[] hide;
        int[] show;
        if (mCarType == 2) {
            hide = AUTO_AIR_HIDE;
            show = MANUAL_AIR_HIDE;
        } else {
            hide = MANUAL_AIR_HIDE;
            show = AUTO_AIR_HIDE;
        }
        for (int i : hide) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }
        for (int i : show) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                sendCanboxInfo0xA8((CMD_ID[i][1] & 0xff), 1);
                mHandler.removeMessages(0);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(0, (CMD_ID[i][1] & 0xff), 0), 200);
            }
        }
    }

    public void onClick(View v) {
        sendCmd(v.getId());
    }

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    private void setSpeed(int speed) {

        for (int i = 0; i < 7; ++i) {
            View v = mMainView.findViewById(R.id.point0 + i);
            if (v != null) {
                if (i < speed) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
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
                }
            }

            v.setImageResource(drawable);
        }
    }

    private void setTemp(int id, int temperature, int unit) {
        TextView v = (TextView) mMainView.findViewById(id);
        String s;
        if (v != null) {
            if (temperature == 0) {
                s = "LOW";
            } else if (temperature == 0) {
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
                updateSelect(R.id.air_title_ce_rear, buf[6] & 0x40);
                updateSelect(R.id.air_title_ce_ac_1, buf[2] & 0x40);
                updateSelect(R.id.air_title_ce_auto_large, buf[2] & 0x08);
                updateSelect(R.id.air_title_ce_max, buf[6] & 0x80);
                updateSelect(R.id.wheel, buf[9] & 0x80);

                updateSelect(R.id.air_title_sync, buf[2] & 0x04);
                updateSelect(R.id.icon_power, buf[2] & 0x80);

                setSpeed((buf[3] & 0xf));

                setSeatheat(R.id.con_seathotright, (buf[7] & 0x0f));
                setSeatheat(R.id.con_seathotleft, (buf[7] & 0xf0) >> 4);

                setTemp(R.id.con_txt_left_temp, (buf[4] & 0xff), (buf[6] & 0x01));
                setTemp(R.id.con_txt_right_temp, (buf[5] & 0xff), (buf[6] & 0x01));

                int t = 0;
                updateSelect(R.id.canbus21_mode1, 0);
                updateSelect(R.id.canbus21_mode2, 0);
                updateSelect(R.id.canbus21_mode3, 0);
                updateSelect(R.id.canbus21_mode4, 0);
                if ((buf[3] & 0x80) != 0) {
                    if ((buf[3] & 0x20) != 0) {
                        t = R.id.canbus21_mode4;
                    }
                } else {
                    if ((buf[3] & 0x20) != 0 && (buf[3] & 0x40) != 0) {
                        t = R.id.canbus21_mode2;
                    } else if ((buf[3] & 0x20) != 0) {
                        t = R.id.canbus21_mode3;
                    } else if ((buf[3] & 0x40) != 0) {
                        t = R.id.canbus21_mode1;
                    }
                }

                if (t != 0) {
                    updateSelect(t, 1);
                }

                setLoop(buf[2] & 0x20);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);
                // updateSelect(R.id.air_title_ce_max, buf[6]&0x80);

                break;
        }
    }

    @Override
    public void onPause() {
        mPaused = true;
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        mPaused = false;
        registerListener();
        // sendCanboxInfo0x90(0x21);
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
                                //								updateView(buf);
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
