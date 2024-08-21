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

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.R.id;
import com.canboxsetting.R.layout;
import com.canboxsetting.R.string;
import com.car.ui.GlobalDef;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.Locale;
import java.util.Objects;

/**
 * This activity plays a video from a specified URI.
 */
public class SlimKeyAirControlFragment extends MyFragment {
    private static final String TAG = "SlimKeyAirControlFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(layout.ac_peugeot_slim, container, false);
        if ((GlobalDef.getProId() == 186)) {
            mMainView.findViewById(id.wheel).setVisibility(View.GONE);
        }

        ///byte[] buf = new byte[]{0x6, (byte) 0xA7, 0x50,0x03,0x00};//去掉原车空调面板
        //BroadcastUtil.sendCanboxInfo(getActivity(), buf);

        MMLog.d(TAG, "SlimKeyAirControlFragment.onCreateView!");
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

    private void sendCanboxInfo0x8A(int d0, int d1) {
        byte[] buf = new byte[]{0x5, (byte) 0x8A, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x8f(int d0) {
        byte[] buf = new byte[]{0x4, (byte) 0x8f, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private final static int[][] CMD_ID = new int[][]{{id.air_title_ce_max, 0x010c}, {id.air_title_ce_rear, 0x010e}, {id.air_title_ce_ac_1, 0x0101}, {id.air_title_ce_inner_loop, 0x0103}, {id.air_title_ce_auto_large, 0x0102}, {id.air_title_ce_ac_max, 0x010f}, {id.wheel, 0x0118}, {id.con_left_temp_up, 0x0104}, {id.con_left_temp_down, 0x0105}, {id.con_right_temp_up, 0x0114}, {id.con_right_temp_down, 0x0115}, {id.canbus21_mode1, 0x0108}, {id.canbus21_mode3, 0x0109}, {id.canbus21_mode2, 0x010a}, {id.canbus21_mode4, 0x010b}, {id.con_seathotleft, 0x0111}, {id.con_seathotright, 0x0112}, {id.air_title_sync, 0x010d}, {id.icon_power, 0x0110}, {id.wind_add, 0x0106}, {id.wind_minus, 0x0107},};

    private void updateSelect(int id, int s) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setSelected(s != 0);
        }
    }

    private void setSpeed(int speed) {
        for (int i = 0; i < 7; ++i) {
            View v = mMainView.findViewById(id.point0 + i);
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
        ImageView v = (ImageView) mMainView.findViewById(id.air_title_ce_inner_loop);
        if (v != null) {
            if (loop == 0) {
                v.getDrawable().setLevel(0);
            } else {
                v.getDrawable().setLevel(1);
                ///MMLog.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!");
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
                isCentigradeUnit = true;
            }
        } else if (GlobalDef.mTempUnit == 2) {
            if (isCentigradeUnit) {
                temperature = ((temperature / 2) * 1.8f + 32);
                isCentigradeUnit = false;
            }
        }

        if (isCentigradeUnit) {
            return String.format(Locale.ENGLISH, "%.1f%s", temperature / 2, view.getResources().getString(string.temp_unic_centigrade));
        } else {
            return String.format(Locale.ENGLISH, "%d%s", (int) temperature, view.getResources().getString(string.temp_unic_fahrenheit));
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
                    s = getString(string.air_manual_cryogen);
                    s += " " + ((temperature & 0x3f));
                    break;
                case 2:
                    s = getString(string.air_manual_heat);
                    s += " " + ((temperature & 0x3f));
                    break;
                default:
                    s = getString(string.air_manual_normal);
                    s += " " + ((temperature & 0x3f));
                    break;
            }

            v.setText(s);
        }
    }

    private final Handler mHandler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            sendCanboxInfo0x8f(0x21);
        }
    };

    @Override
    public void onPause() {
        unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume() {
        registerListener();
        //sendCanboxInfo0x90(0x21);
        mHandler.sendEmptyMessageDelayed(0, 500);
        mHandler.sendEmptyMessageDelayed(0, 1000);
        super.onResume();
    }

    private BroadcastReceiver mReceiver;

    private void unregisterListener() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
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
                                //                                updateView(buf);
                                updateSlimView(buf);
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

    private void updateSlimView(byte[] buf) {
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            if (buf[3] == 0x05 && buf[4] == 0x01) {
                MMLog.d(TAG, "updateSlimView: 打开AC");
                updateSelect(id.air_title_ce_ac_1, 1);
            } else if (buf[3] == 0x05 && buf[4] == 0x00) {
                MMLog.d(TAG, "updateSlimView: 关闭AC");
                updateSelect(id.air_title_ce_ac_1, 0);
            } else if (buf[3] == 0x06 && (buf[4] == 0x00 || buf[4] == 0x01)) {
                MMLog.d(TAG, "updateSlimView: AC MAX = " + buf[4]);
                updateSelect(id.air_title_ce_ac_max, buf[4]);
            } else if (buf[3] == 0x00 && buf[4] == 0x02) {
                MMLog.d(TAG, "updateSlimView: 打开外循环");
                setLoop(0);
            } else if (buf[3] == 0x00 && buf[4] == 0x03) {
                MMLog.d(TAG, "updateSlimView: 打开内循环");
                setLoop(1);
            } else if (buf[3] == 0x00 && buf[4] == 0x04) {
                MMLog.d(TAG, "updateSlimView: 后除霜器已关闭");
                updateSelect(id.air_title_ce_rear, 0);
            } else if (buf[3] == 0x00 && buf[4] == 0x05) {
                MMLog.d(TAG, "updateSlimView: 后除霜器已打开");
                updateSelect(id.air_title_ce_rear, 1);
            } else if (buf[3] == 0x01 && buf[4] >= 0x00 && buf[4] <= 4) {
                MMLog.d(TAG, "updateSlimView: 空气方向 = " + buf[4]);
                updateAirDirection(buf[4]);
            } else if (buf[3] == 0x02 && buf[4] > 0 && buf[4] <= 7) {
                MMLog.d(TAG, "updateSlimView: 风速调节 = " + buf[4]);
                setSpeed(buf[4]);
            } else if (buf[3] == 0x03 && buf[4] > 0 && buf[4] <= 16) {
                MMLog.d(TAG, "updateSlimView: 温度调节 = " + buf[4]);
                //TODO 暂时还未实现,不确定温度的显示方式
            } else if (buf[3] == 0x04 && (buf[4] == 0x00 || buf[4] == 0x01)) {
                MMLog.d(TAG, "updateSlimView: 空调开关 = " + buf[4]);
                updateSelect(id.icon_power, buf[4]);
            }
        }
        super.callBack(0);
    }

    private void updateView(byte[] buf) {
        MMLog.d(TAG, "SlimKeyAirControlFragment.updateView buf:" + ByteUtils.BuffToHexStr(buf));
        if (buf[1] == 0x21) {
            updateSelect(id.icon_power, buf[2] & 0x80);
            updateSelect(id.air_title_ce_ac_1, buf[2] & 0x40);
            setLoop(buf[2] & 0x20);
            updateSelect(id.air_title_ce_rear, buf[2] & 0x01);//后窗加热

            updateSelect(id.air_title_ce_auto_large, buf[2] & 0x08);
            updateSelect(id.air_title_sync, buf[2] & 0x04);

            updateSelect(id.canbus21_mode1, buf[3] & 0x40);
            updateSelect(id.canbus21_mode2, buf[3] & 0x20);
            updateSelect(id.canbus21_mode3, buf[3] & 0x80);

            //updateSelect(id.canbus21_mode4, 0);

            setSpeed((buf[3] & 0xf));

            setTemp(id.con_txt_left_temp, (buf[4] & 0xff), (buf[4] & 0x01));
            setTemp(id.con_txt_right_temp, (buf[5] & 0xff), (buf[5] & 0x01));
            ///setTempManul(id.con_txt_left_temp, (buf[3] & 0xff), (buf[3] & 0x01));
            ///setTempManul(id.con_txt_right_temp, (buf[4] & 0xff), (buf[4] & 0x01));

            updateSelect(id.air_title_ce_max, buf[6] & 0x80);//前窗除雾
            updateSelect(id.air_title_ce_ac_max, buf[6] & 0x08);

            ///updateSelect(id.wheel, buf[9] & 0x80);
            ///setSeatheat(id.con_seathotright, (buf[7] & 0x0f));
            ///setSeatheat(id.con_seathotleft, (buf[7] & 0xf0) >> 4);
            super.callBack(0);
        }
    }

    private void updateAirDirection(byte cmd) {
        updateSelect(id.canbus21_mode1, cmd == 0x00 ? 1 : 0);
        updateSelect(id.canbus21_mode2, cmd == 0x02 ? 1 : 0);
        updateSelect(id.canbus21_mode3, cmd == 0x01 ? 1 : 0);
        updateSelect(id.canbus21_mode4, cmd == 0x03 ? 1 : 0);
        updateSelect(id.canbus21_mode5, cmd == 0x04 ? 1 : 0);
    }

    public void onClick(View v) {
        ///sendCmd(v.getId());
        if (v.getId() == id.air_title_ce_auto_large) {
            switchStatus(v, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_ac_1) {
            switchStatus(v, (byte) 0x02);
        } else if (v.getId() == id.air_title_ce_ac_max) {
            switchStatus(v, (byte) 0x03);
        } else if (v.getId() == id.con_left_temp_up) {
            sendCanboxInfo0x8A(0x04, 0x01);
        } else if (v.getId() == id.con_left_temp_down) {
            sendCanboxInfo0x8A(0x04, 0x02);
        } else if (v.getId() == id.con_right_temp_up) {
            sendCanboxInfo0x8A(0x05, 0x01);
        } else if (v.getId() == id.con_right_temp_down) {
            sendCanboxInfo0x8A(0x05, 0x02);
        } else if (v.getId() == id.canbus21_mode1) {
            switchStatus(v, (byte) 0x06);
        } else if (v.getId() == id.canbus21_mode2) {
            switchStatus(v, (byte) 0x08);
        } else if (v.getId() == id.canbus21_mode3) {
            switchStatus(v, (byte) 0x07);
        } else if (v.getId() == id.wind_minus) {
            sendCanboxInfo0x8A(0x0A, 0x02);
        } else if (v.getId() == id.wind_add) {
            sendCanboxInfo0x8A(0x0A, 0x01);
        } else if (v.getId() == id.air_title_sync) {
            switchStatus(v, (byte) 0x0B);
        } else if (v.getId() == id.icon_power) {
            ///if (v.isSelected())
            sendCanboxInfo0x8A(0x0C, 0x01);
            ///else
            sendCanboxInfo0x8A(0x0C, 0x00);
        } else if (v.getId() == id.air_title_ce_max) {//前窗
            ///sendCanboxInfo0x8A(0x11, 0x01);
            ///sendCanboxInfo0x8A(0x11, 0x00);
            switchStatus(v, (byte) 0x11);
        } else if (v.getId() == id.air_title_ce_rear) {//后窗
            ///if (v.isSelected())
            sendCanboxInfo0x8A(0x12, 0x01);
            ///else
            sendCanboxInfo0x8A(0x12, 0x00);
        } else if (v.getId() == id.air_title_ce_inner_loop) {
            ImageView vv = (ImageView) mMainView.findViewById(id.air_title_ce_inner_loop);
            int level = vv.getDrawable().getLevel();
            if (level == 0) vv.getDrawable().setLevel(1);
            else vv.getDrawable().setLevel(0);
        }
    }

    private void switchStatus(View v, byte id) {
        if (v.isSelected()) sendCanboxInfo0x8A(id, 0x00);
        else sendCanboxInfo0x8A(id, 0x01);
    }

    private void sendCmd(int id) {
        ///for (int[] ints : CMD_ID) {
        ///    if (ints[0] == id) {
        ///        sendCanboxInfo0x8A((ints[1] & 0xff00) >> 8, (ints[1] & 0xff));
        ///    }
        ///}
    }
}
