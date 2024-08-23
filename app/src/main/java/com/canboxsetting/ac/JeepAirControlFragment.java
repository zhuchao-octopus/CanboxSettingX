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
import android.os.Looper;
import android.os.Message;
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
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.Locale;

/**
 * This activity plays a video from a specified URI.
 */
public class JeepAirControlFragment extends MyFragment {
    private static final String TAG = "JeepAirControlFragment";
    private final static int[][] CMD_ID = new int[][]{{R.id.air_title_ce_max, 0x010c}, {R.id.air_title_ce_rear, 0x010e}, {R.id.air_title_ce_ac_1, 0x0101}, {R.id.air_title_ce_inner_loop, 0x0103}, {R.id.air_title_ce_auto_large, 0x0102}, {R.id.air_title_ce_ac_max, 0x010f}, {R.id.wheel, 0x0118}, {R.id.con_left_temp_up, 0x0104}, {R.id.con_left_temp_down, 0x0105}, {R.id.con_right_temp_up, 0x0114}, {R.id.con_right_temp_down, 0x0115}, {R.id.canbus21_mode1, 0x0108}, {R.id.canbus21_mode3, 0x0109}, {R.id.canbus21_mode2, 0x010a}, {R.id.canbus21_mode4, 0x010b}, {R.id.con_seathotleft, 0x0111}, {R.id.con_seathotright, 0x0112}, {R.id.air_title_sync, 0x010d}, {R.id.icon_power, 0x0110}, {R.id.wind_add, 0x0106}, {R.id.wind_minus, 0x0107},

    };
    private View mMainView;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            sendCanboxInfo0x90(0x21);
        }
    };
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_jeep_simple, container, false);

        if ((GlobalDef.getProId() == 186)) {
            mMainView.findViewById(R.id.wheel).setVisibility(View.GONE);
        }
        return mMainView;
    }

    private void sendCanboxInfo0x82(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x82, 0x2, (byte) d0, (byte) d1,};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x4, (byte) d0, 0, 0, 0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCmd(int id) {
        for (int i = 0; i < CMD_ID.length; ++i) {
            if (CMD_ID[i][0] == id) {
                sendCanboxInfo0x82((CMD_ID[i][1] & 0xff00) >> 8, (CMD_ID[i][1] & 0xff));
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

    String getAirTemperature(View view, float temperature, boolean isCentigradeUnit) {

        if (GlobalDef.mTempUnit == 1) {
            if (!isCentigradeUnit) {
                temperature = ((temperature) - 32) / 1.8f;
                isCentigradeUnit = !isCentigradeUnit;
            }
        } else if (GlobalDef.mTempUnit == 2) {
            if (isCentigradeUnit) {
                temperature = ((temperature / 2) * 1.8f + 32);
                isCentigradeUnit = !isCentigradeUnit;
            }
        }

        if (isCentigradeUnit == true) {
            return String.format(Locale.ENGLISH, "%.1f%s", temperature / 2, view.getResources().getString(R.string.temp_unic_centigrade));
        } else {
            return String.format(Locale.ENGLISH, "%d%s", (int) temperature, view.getResources().getString(R.string.temp_unic_fahrenheit));
        }

    }

    private void setTemp(int id, int temperature, int unit) {
        TextView v = (TextView) mMainView.findViewById(id);
        String s;
        if (v != null) {
            if (temperature == 0) {
                s = "LOW";
            } else if (temperature == 0xff) {
                s = "HI";

            } else {
                s = getAirTemperature(mMainView, temperature, unit == 0);
                //				if (unit == 0) {
                //					s = String.format(Locale.ENGLISH, "%.1f%s",
                //							((float) temperature) / 2, getResources()
                //									.getString(R.string.temp_unic));
                //				} else {
                //					s = String.format(Locale.ENGLISH, "%dF", (int) temperature);
                //				}
            }
            v.setText(s);
        }
    }

    private void setTempManul(int id, int temperature, int unit) {
        TextView v = (TextView) mMainView.findViewById(id);
        String s;
        if (v != null) {
            switch ((temperature & 0xc0) >> 6) {
                case 1:
                    s = getString(R.string.air_manual_cryogen);
                    s += " " + ((temperature & 0x3f));
                    break;
                case 2:
                    s = getString(R.string.air_manual_heat);
                    s += " " + ((temperature & 0x3f));
                    break;
                default:
                    s = getString(R.string.air_manual_normal);
                    s += " " + ((temperature & 0x3f));
                    break;
            }

            v.setText(s);
        }
    }

    private void updateView(byte[] buf) {
        MMLog.d(TAG, "updateView buf:" + ByteUtils.BytesToHexStr(buf));
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

                if ((buf[6] & 0x02) == 0) {
                    setTemp(R.id.con_txt_left_temp, (buf[4] & 0xff), (buf[6] & 0x01));
                    setTemp(R.id.con_txt_right_temp, (buf[5] & 0xff), (buf[6] & 0x01));
                } else {
                    setTempManul(R.id.con_txt_left_temp, (buf[4] & 0xff), (buf[6] & 0x01));
                    setTempManul(R.id.con_txt_right_temp, (buf[5] & 0xff), (buf[6] & 0x01));
                }

                int t = 0;
                updateSelect(R.id.canbus21_mode1, 0);
                updateSelect(R.id.canbus21_mode3, 0);
                updateSelect(R.id.canbus21_mode2, 0);
                updateSelect(R.id.canbus21_mode4, 0);
                if ((buf[3] & 0x80) != 0) {
                    if ((buf[3] & 0x20) != 0) {
                        t = R.id.canbus21_mode4;
                    }
                } else {
                    if ((buf[3] & 0x20) != 0 && (buf[3] & 0x40) != 0) {
                        t = R.id.canbus21_mode3;
                    } else if ((buf[3] & 0x20) != 0) {
                        t = R.id.canbus21_mode2;
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
        //		sendCanboxInfo0x90(0x21);
        mHandler.sendEmptyMessageDelayed(0, 500);
        mHandler.sendEmptyMessageDelayed(0, 1000);
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
                    if (action == null) return;
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
