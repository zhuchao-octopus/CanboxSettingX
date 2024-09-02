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
import com.common.utils.BroadcastUtil;
import com.common.utils.GlobalDef;
import com.common.utils.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.Locale;
import java.util.Objects;

/**
 * This activity plays a video from a specified URI.
 */
public class HiWorldDF08AirControlFragment extends MyFragment {
    private static final String TAG = "SlimKeyAirControlFragment";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(layout.ac_hiworld_df08, container, false);
        if ((GlobalDef.getProId() == 186)) {
            mMainView.findViewById(id.wheel).setVisibility(View.GONE);
        }

        ///byte[] buf = new byte[]{0x6, (byte) 0xA7, 0x50,0x03,0x00};//去掉原车空调面板
        //BroadcastUtil.sendCanboxInfo(getActivity(), buf);

        MMLog.d(TAG, "SlimKeyAirControlFragment.onCreateView!");
        initSlimKeyACData();
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

    private void updateInfo() {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0x31, (byte) 0xA3
        };
        MMLog.d(TAG, "updateInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxHiworld(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x02, 0x3D, d0, d1,0x00
        };
        buf[buf.length -1] = (byte) (0x3E + d0 + d1);
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
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
        currAirWind = (byte) speed;
        for (int i = 0; i <= 8; ++i) {
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

    private byte currTemp = 1;
    private void setSlimTemp(byte temp) {
        Log.d(TAG, "setSlimTemp: temp " + temp);
        if (temp > 0 && temp <= 16) {
            sendCanboxHiworld((byte) 0x83, temp);
            updateTempView(temp);

        } else if (temp < 1) {
            setSlimTemp((byte) 1);
        } else if (temp > 16) {
            setSlimTemp((byte) 16);
        }
    }

    private void updateTempView(byte temp) {
        currTemp = temp;
        TextView leftT = mMainView.findViewById(id.con_txt_left_temp);
        TextView rightT = mMainView.findViewById(id.con_txt_right_temp);
        if (leftT != null) {
            Log.d(TAG, "updateTempView: leftT");
            if (temp == (byte)0xFE) {
                leftT.setText("Low_Temp");
            } else if (temp == (byte)0xFF) {
                leftT.setText("High_Temp");
            } else {
                leftT.setText(String.valueOf(temp * 0.5));
            }
        }
        if (rightT != null) {
            Log.d(TAG, "updateTempView: rightT");
            rightT.setText(String.valueOf(temp));
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
        updateInfo();
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
        Log.d(TAG, "updateSlimView: buf = " + ByteUtils.BuffToHexStr(buf) + "  length = " + buf.length + "   buf[1] = " + (buf[1] + 4) + "   buf[0] = " + buf[0]);
        if (buf != null && buf.length == 16 && buf[0] == (byte)0xA5 && buf[1] + 4 == buf.length && checkSum(buf) == buf[buf.length - 1]) {
            Log.d(TAG, "updateSlimView: buf正确 = " + ByteUtils.BuffToHexStr(buf));
            updateSelect(id.air_title_ce_ac_1, buf[3] & 0B00000001);//AC
            updateSelect(id.air_title_sync, buf[3] & 0B00000010);//替换为PTC
            updateSelect(id.icon_power, buf[3] & 0B01000000);//空调开关
            updateSelect(id.air_title_ce_auto_large, buf[4] & 0B00001000);//AUTO
            setLoop(buf[4] & 0B00010000);//内外循环
            updateSelect(id.air_title_ce_max, buf[5] & 0B00010000);//前窗除雾
            updateSelect(id.air_title_ce_rear, buf[5] & 0B00100000);//后窗除雾
            updateSelect(id.air_title_ce_ac_max, buf[5] & 0B01000000);//后视镜加热
            updateAirDirection(buf[7]);
            setSpeed(buf[8]);
            updateTempView(buf[9]);
        }
        super.callBack(0);
    }

    private byte checkSum(byte[] buf){
        byte sum = 0;
        if (buf != null) {
            for (int i = 1;i<buf.length - 2;i++) {
                sum += buf[i];
            }
        }
        MMLog.d(TAG, "checkSum: " + (sum - 1));
        return (byte) (sum - 1);
    }

    private void controlAirDirection(byte cmd) {
        sendCanboxHiworld((byte) 0x81, cmd);
        updateAirDirection(cmd);
    }

    private void updateAirDirection(byte cmd) {
        updateSelect(id.canbus21_mode1, cmd == 0x01 ? 1 : 0);
        updateSelect(id.canbus21_mode2, cmd == 0x02 ? 1 : 0);
        updateSelect(id.canbus21_mode3, cmd == 0x03 ? 1 : 0);
        updateSelect(id.canbus21_mode4, cmd == 0x04 ? 1 : 0);
    }

    public void onClick(View v) {
        ///sendCmd(v.getId());
        if (v.getId() == id.air_title_ce_auto_large) {
            sendCanboxHiworld((byte) 0x04, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_ac_1) {
            sendCanboxHiworld((byte) 0x02, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_ac_max) {
            sendCanboxHiworld((byte) 0x2A, (byte) 0x01);
        } else if (v.getId() == id.con_left_temp_up) {
            sendCanboxHiworld((byte) 0x0D, (byte) 0x01);
        } else if (v.getId() == id.con_left_temp_down) {
            sendCanboxHiworld((byte) 0x0E, (byte) 0x01);
        }/* else if (v.getId() == id.con_right_temp_up) {
            sendCanboxSlim((byte) 0x04, (byte) 0x00);
        } else if (v.getId() == id.con_right_temp_down) {
            sendCanboxSlim((byte) 0x04, (byte) 0x00);
        }*/ else if (v.getId() == id.canbus21_mode1) {
            sendCanboxHiworld((byte) 0x1A, (byte) 0x01);
        } else if (v.getId() == id.canbus21_mode2) {
            sendCanboxHiworld((byte) 0x1B, (byte) 0x01);
        } else if (v.getId() == id.canbus21_mode3) {
            sendCanboxHiworld((byte) 0x1D, (byte) 0x01);
        } else if (v.getId() == id.canbus21_mode4) {
            sendCanboxHiworld((byte) 0x1C, (byte) 0x01);
        } else if (v.getId() == id.wind_minus) {
            sendCanboxHiworld((byte) 0x0C, (byte) 0x01);
        } else if (v.getId() == id.wind_add) {
            sendCanboxHiworld((byte) 0x0B, (byte) 0x01);
        } else if (v.getId() == id.air_title_sync) {
            sendCanboxHiworld((byte) 0x50, (byte) 0x01);
        } else if (v.getId() == id.icon_power) {
            sendCanboxHiworld((byte) 0x01, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_max) {//前窗
            sendCanboxHiworld((byte) 0x05, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_rear) {//后窗
            sendCanboxHiworld((byte) 0x06, (byte) 0x01);
        } else if (v.getId() == id.air_title_ce_inner_loop) {
            sendCanboxHiworld((byte) 0x07, (byte) 0x01);
        }
    }

    private byte currAirWind = 1;

    private void airWindControl(byte windValue) {
        MMLog.d(TAG, "airWindControl: windValue = " + windValue);
        if (windValue > 0 && windValue <= 7) {
            sendCanboxHiworld((byte) 0x82, windValue);
            setSpeed(windValue);

        } else if (windValue < 1){
            airWindControl((byte) 1);
        } else if (windValue > 7) {
            airWindControl((byte) 7);
        }
    }


    private void switchStatus(View v, byte id) {
        if (v.isSelected()) {
            sendCanboxHiworld(id, (byte) 0x00);
            v.setSelected(false);
        } else {
            sendCanboxHiworld(id, (byte) 0x01);
            v.setSelected(true);
        }
    }

    private void sendCmd(int id) {
        ///for (int[] ints : CMD_ID) {
        ///    if (ints[0] == id) {
        ///        sendCanboxInfo0x8A((ints[1] & 0xff00) >> 8, (ints[1] & 0xff));
        ///    }
        ///}
    }

    private void initSlimKeyACData() {
//        String acData = MachineConfig.getProperty("AC_UPDATE_DATA");
//        Log.d(TAG, "initSlimKeyACData: acData = " + acData);
//        byte[] slimKeyACData = HexStr2Bytes(acData.replace(" ",""));
//        updateACAllView(slimKeyACData);
    }

    private void updateACAllView(byte[] slimKeyACData) {
        ImageView vv = (ImageView) mMainView.findViewById(id.air_title_ce_inner_loop);
        vv.getDrawable().setLevel(slimKeyACData[2] & 0x02);
        updateSelect(id.air_title_ce_rear, slimKeyACData[2] & 0x04);//后窗加热
        updateAirDirection(slimKeyACData[3]);
        setSpeed(slimKeyACData[4]);
        updateTempView(slimKeyACData[5]);
        updateSelect(id.icon_power, slimKeyACData[6]);
        updateSelect(id.air_title_ce_ac_1, slimKeyACData[7]);
        updateSelect(id.air_title_ce_ac_max, slimKeyACData[8]);
    }

    public static byte[] HexStr2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}
